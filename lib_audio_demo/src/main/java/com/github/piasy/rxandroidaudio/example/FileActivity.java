/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.rxandroidaudio.example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xxf.media.audio.AudioRecorder;
import com.xxf.media.audio.PlayConfig;
import com.xxf.media.audio.RxAmplitude;
import com.xxf.media.audio.RxAudioPlayer;
import com.xxf.permission.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressWarnings("unused")
@SuppressLint("ClickableViewAccessibility")
public class FileActivity extends AppCompatActivity implements AudioRecorder.OnErrorListener {

    public static final int MIN_AUDIO_LENGTH_SECONDS = 2;

    private static final String TAG = "FileActivity";

    FrameLayout mFlIndicator;
    TextView mTvPressToSay;
    TextView mTvLog;
    TextView mTvRecordingHint;

    private List<ImageView> mIvVoiceIndicators;

    private AudioRecorder mAudioRecorder;
    private RxAudioPlayer mRxAudioPlayer;
    private File mAudioFile;
    private Disposable mRecordDisposable;
    private RxPermissions mPermissions;
    private Queue<File> mAudioFiles = new LinkedList<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        mFlIndicator = findViewById(R.id.mFlIndicator);
        mTvPressToSay = findViewById(R.id.mTvPressToSay);
        mTvLog = findViewById(R.id.mTvLog);
        mTvPressToSay = findViewById(R.id.mTvPressToSay);
        mTvRecordingHint = findViewById(R.id.mTvRecordingHint);
        findViewById(R.id.mBtnPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay();
            }
        });

        mPermissions = new RxPermissions(this);

        mIvVoiceIndicators = new ArrayList<>();
        mIvVoiceIndicators.add((ImageView) findViewById(R.id.mIvVoiceIndicator1));
        mIvVoiceIndicators.add((ImageView) findViewById(R.id.mIvVoiceIndicator2));
        mIvVoiceIndicators.add((ImageView) findViewById(R.id.mIvVoiceIndicator3));
        mIvVoiceIndicators.add((ImageView) findViewById(R.id.mIvVoiceIndicator4));
        mIvVoiceIndicators.add((ImageView) findViewById(R.id.mIvVoiceIndicator5));
        mIvVoiceIndicators.add((ImageView) findViewById(R.id.mIvVoiceIndicator6));
        mIvVoiceIndicators.add((ImageView) findViewById(R.id.mIvVoiceIndicator7));

        mAudioRecorder = AudioRecorder.getInstance();
        mRxAudioPlayer = RxAudioPlayer.getInstance();
        mAudioRecorder.setOnErrorListener(this);

        mTvPressToSay.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    press2Record();
                    break;
                case MotionEvent.ACTION_UP:
                    release2Send();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    release2Send();
                    break;
                default:
                    break;
            }

            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRxAudioPlayer != null) {
            mRxAudioPlayer.stopPlay();
        }
        compositeDisposable.dispose();
    }

    private void press2Record() {
        mTvPressToSay.setBackgroundResource(R.drawable.button_press_to_say_pressed_bg);
        mTvRecordingHint.setText(R.string.voice_msg_input_hint_speaking);

        boolean isPermissionsGranted
                = mPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && mPermissions.isGranted(Manifest.permission.RECORD_AUDIO);
        if (!isPermissionsGranted) {
            compositeDisposable.add(mPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO)
                    .subscribe(granted -> {
                        // not record first time to request permission
                        if (granted) {
                            Toast.makeText(getApplicationContext(), "Permission granted",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Permission not granted",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, Throwable::printStackTrace));
        } else {
            recordAfterPermissionGranted();
        }
    }

    private void recordAfterPermissionGranted() {
        mRecordDisposable = Observable
                .fromCallable(() -> {
                    mAudioFile = new File(
                            Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + File.separator + System.nanoTime() + ".file.m4a");
                    Log.d(TAG, "to prepare record");
                    return mAudioRecorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                            MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                            192000, 192000, mAudioFile);
                })
                .flatMap(b -> {
                    Log.d(TAG, "prepareRecord success");
                    Log.d(TAG, "to play audio_record_ready: " + R.raw.audio_record_ready);
                    return mRxAudioPlayer.play(
                            PlayConfig.res(getApplicationContext(), R.raw.audio_record_ready)
                                    .build());
                })
                .doOnComplete(() -> {
                    Log.d(TAG, "audio_record_ready play finished");
                    mFlIndicator.post(() -> mFlIndicator.setVisibility(View.VISIBLE));
                    mAudioRecorder.startRecord();
                })
                .doOnNext(b -> Log.d(TAG, "startRecord success"))
                .flatMap(o -> RxAmplitude.from(mAudioRecorder))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(level -> {
                    int progress = mAudioRecorder.progress();
                    Log.d(TAG, "amplitude: " + level + ", progress: " + progress);

                    refreshAudioAmplitudeView(level);

                    if (progress >= 12) {
                        mTvRecordingHint.setText(String.format(
                                getString(R.string.voice_msg_input_hint_time_limited_formatter),
                                15 - progress));
                        if (progress == 15) {
                            release2Send();
                        }
                    }
                }, Throwable::printStackTrace);
    }

    private void release2Send() {
        mTvPressToSay.setBackgroundResource(R.drawable.button_press_to_say_bg);
        mFlIndicator.setVisibility(View.GONE);

        if (mRecordDisposable != null && !mRecordDisposable.isDisposed()) {
            mRecordDisposable.dispose();
            mRecordDisposable = null;
        }

        compositeDisposable.add(Observable
                .fromCallable(() -> {
                    int seconds = mAudioRecorder.stopRecord();
                    Log.d(TAG, "stopRecord: " + seconds);
                    if (seconds >= MIN_AUDIO_LENGTH_SECONDS) {
                        mAudioFiles.offer(mAudioFile);
                        return true;
                    }
                    return false;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(added -> {
                    if (added) {
                        mTvLog.setText(mTvLog.getText() + "\n"
                                + "audio file " + mAudioFile.getName() + " added");
                    }
                }, Throwable::printStackTrace));
    }

    private void refreshAudioAmplitudeView(int level) {
        int end = level < mIvVoiceIndicators.size() ? level : mIvVoiceIndicators.size();

        List<ImageView> imageViews = mIvVoiceIndicators.subList(0, end);
        for (ImageView item : imageViews) {
            item.setVisibility(View.VISIBLE);
        }

        List<ImageView> imageViews1 = mIvVoiceIndicators.subList(end, mIvVoiceIndicators.size());
        for (ImageView item : imageViews1) {
            item.setVisibility(View.INVISIBLE);
        }
    }

    public void startPlay() {
        mTvLog.setText("");
        if (!mAudioFiles.isEmpty()) {
            File audioFile = mAudioFiles.poll();
            compositeDisposable.add(mRxAudioPlayer.play(
                    PlayConfig.file(audioFile)
                            .streamType(AudioManager.STREAM_VOICE_CALL)
                            .build())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Functions.emptyConsumer(), Throwable::printStackTrace,
                            this::startPlay));
        }
    }

    @WorkerThread
    @Override
    public void onError(int error) {
        runOnUiThread(
                () -> Toast.makeText(FileActivity.this, "Error code: " + error, Toast.LENGTH_SHORT)
                        .show());
    }
}
