package com.example.lab3_20202396;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

public class TimerService extends Service {
    private final IBinder binder = new LocalBinder();
    private CountDownTimer timer;
    private long timeLeftInMillis;
    private boolean isRunning = false;

    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startTimer(long durationInMillis) {
        if (timer != null) {
            timer.cancel();
        }

        timeLeftInMillis = durationInMillis;
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                isRunning = true;
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timeLeftInMillis = 0;
            }
        }.start();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            isRunning = false;
        }
    }

    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
} 