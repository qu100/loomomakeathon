package com.segway.loomo.vlssample;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.segway.robot.algo.Pose2D;

import com.segway.robot.algo.minicontroller.CheckPoint;
import com.segway.robot.algo.minicontroller.CheckPointStateListener;
import com.segway.robot.sdk.base.bind.ServiceBinder;
import com.segway.robot.sdk.locomotion.sbv.Base;
import com.segway.robot.sdk.voice.Languages;
import com.segway.robot.sdk.voice.Recognizer;
import com.segway.robot.sdk.voice.Speaker;
import com.segway.robot.sdk.voice.VoiceException;
import com.segway.robot.sdk.voice.audiodata.RawDataListener;
import com.segway.robot.sdk.voice.grammar.GrammarConstraint;
import com.segway.robot.sdk.voice.recognition.RecognitionListener;
import com.segway.robot.sdk.voice.recognition.WakeupListener;
import com.segway.robot.sdk.voice.tts.TtsListener;
import java.util.Timer;
import java.util.TimerTask;



import java.io.File;
import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Loomo";
    private Button mStart;
    private Button mStop;
    private Base mBase;
    private boolean isBeamForming = false;
    private boolean bindSpeakerService;
    private boolean bindRecognitionService;
    private int mSpeakerLanguage;
    private Speaker mSpeaker;
    private TtsListener mTtsListener;
    private VoiceHandler mHandler = new VoiceHandler(this);
    private static final int SHOW_MSG = 0x0001;
    private static final int APPEND = 0x000f;
    private static final int CLEAR = 0x00f0;
    private Timer  mTimer = new Timer(true);
    private TimerTask mTimerTask1;
    private TimerTask mTimerTask2;
    private TimerTask mTimerTask3;


    public static class VoiceHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        private VoiceHandler(MainActivity instance) {
            mActivity = new WeakReference<>(instance);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mActivity.get();
            if (mainActivity != null) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHOW_MSG:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStart = (Button) findViewById(R.id.start);
        mStop = (Button) findViewById(R.id.stop);
        mStart.setOnClickListener(this);
        mStop.setOnClickListener(this);

        mBase = Base.getInstance();
        mBase.bindService(this, mBaseBindStateListener);
        mSpeaker = Speaker.getInstance();
        mSpeaker.bindService(MainActivity.this, mSpeakerBindStateListener);
        mStart.setEnabled(true);
        mStop.setEnabled(true);
        mTtsListener = new TtsListener() {
            @Override
            public void onSpeechStarted(String s) {
                //s is speech content, callback this method when speech is starting.
                Log.d(TAG, "onSpeechStarted() called with: s = [" + s + "]");
                Message statusMsg = mHandler.obtainMessage(SHOW_MSG, CLEAR, 0);
                mHandler.sendMessage(statusMsg);
            }

            @Override
            public void onSpeechFinished(String s) {
                //s is speech content, callback this method when speech is finish.
                Log.d(TAG, "onSpeechFinished() called with: s = [" + s + "]");
                Message statusMsg = mHandler.obtainMessage(SHOW_MSG, CLEAR, 0);
                mHandler.sendMessage(statusMsg);
            }

            @Override
            public void onSpeechError(String s, String s1) {
                //s is speech content, callback this method when speech occurs error.
                Log.d(TAG, "onSpeechError() called with: s = [" + s + "], s1 = [" + s1 + "]");
                Message statusMsg = mHandler.obtainMessage(SHOW_MSG, CLEAR, 0);
                mHandler.sendMessage(statusMsg);
            }
        };
        mTimerTask1 = new TimerTask()
        {
            public void run()
            {
                Log.v("time","time:"+System.currentTimeMillis()/1000);
                try {
                    Log.d(TAG, "3d");
                    mSpeaker.speak("This is three D printer area", mTtsListener);
                    //block for 3 seconds, return true if speech time is smaller than 3 seconds, else return false.
                    /*boolean timeout = mSpeaker.waitForSpeakFinish(3000);*/
                } catch (VoiceException e) {
                    Log.w(TAG, "Exception: ", e);
                }

            }
        };
        mTimerTask2 = new TimerTask()
        {
            public void run()
            {
                Log.v("time","time:"+System.currentTimeMillis()/1000);
                try {
                    Log.d(TAG, "3d");
                    mSpeaker.speak("This is the working space", mTtsListener);
                    //block for 3 seconds, return true if speech time is smaller than 3 seconds, else return false.
                    /*boolean timeout = mSpeaker.waitForSpeakFinish(3000);*/
                } catch (VoiceException e) {
                    Log.w(TAG, "Exception: ", e);
                }

            }
        };
        mTimerTask3 = new TimerTask()
        {
            public void run()
            {
                Log.v("time","time:"+System.currentTimeMillis()/1000);
                try {
                    Log.d(TAG, "3d");
                    mSpeaker.speak("This is the last station， the laser is in your front, bye", mTtsListener);
                    //block for 3 seconds, return true if speech time is smaller than 3 seconds, else return false.
                    /*boolean timeout = mSpeaker.waitForSpeakFinish(3000);*/
                } catch (VoiceException e) {
                    Log.w(TAG, "Exception: ", e);
                }

            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBase.unbindService();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                startNavigation();
                break;
            case R.id.stop:
                mBase.clearCheckPointsAndStop();
                break;
        }
    }

    private void startNavigation() {
        mBase.cleanOriginalPoint();
        Pose2D pose2D = mBase.getOdometryPose(-1);
        mBase.setOriginalPoint(pose2D);
        try {
            Log.d(TAG, "start speak");
            mSpeaker.speak("hello world, I am a segway robot. Let's go" +
                    "                                                                  "+
                    "This is loomo, we are touring the maker space", mTtsListener);
            //block for 3 seconds, return true if speech time is smaller than 3 seconds, else return false.
                    /*boolean timeout = mSpeaker.waitForSpeakFinish(3000);*/
        } catch (VoiceException e) {
            Log.w(TAG, "Exception: ", e);
        }
        mTimer.schedule(mTimerTask1,28000);
        mTimer.schedule(mTimerTask2,45000);
//        mTimer.schedule(mTimerTask2,80000);
        mBase.addCheckPoint(5f,-1f);
        mBase.addCheckPoint(8f,-1.5f);
        mBase.addCheckPoint(8.1f,-3f);
        //s3d arrived

        mBase.addCheckPoint(8.3f,-6.5f);
        //s working space arrived

        mBase.addCheckPoint(5f,-6.5f);
        mBase.addCheckPoint(4f,-6f);
        mBase.addCheckPoint(3f,-6.5f);


    }


    private ServiceBinder.BindStateListener mBaseBindStateListener = new ServiceBinder.BindStateListener() {
        @Override
        public void onBind() {
            Log.d(TAG, "onBind() called");
            // set base control mode
            mBase.setControlMode(Base.CONTROL_MODE_NAVIGATION);
            // start VLS
            mBase.setOnCheckPointArrivedListener(new CheckPointStateListener() {
                @Override
                public void onCheckPointArrived(CheckPoint checkPoint, final Pose2D realPose, boolean isLast) {

                }

                @Override
                public void onCheckPointMiss(CheckPoint checkPoint, Pose2D realPose, boolean isLast, int reason) {

                }
            });

        }

        @Override
        public void onUnbind(String reason) {
            Log.d(TAG, "onUnbind() called with: reason = [" + reason + "]");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "failed when binding service!", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private ServiceBinder.BindStateListener mSpeakerBindStateListener = new ServiceBinder.BindStateListener() {
        @Override
        public void onBind() {
            Log.d(TAG, "speaker service onBind");
            try {
                //get speaker service language.
                mSpeakerLanguage = mSpeaker.getLanguage();
            } catch (VoiceException e) {
                Log.e(TAG, "Exception: ", e);
            }
            bindSpeakerService = true;

            // set the volume of TTS
            try {
                mSpeaker.setVolume(100);
            } catch (VoiceException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnbind(String s) {
            Log.d(TAG, "speaker service onUnbind");
        }
    };
}