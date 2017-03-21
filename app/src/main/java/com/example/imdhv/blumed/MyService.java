package com.example.imdhv.blumed;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.IntDef;

import java.util.HashMap;
import java.util.Map;

public class MyService extends Service {
    private final static String TAG = "BroadcastService";
    int f=0;
    public static final String COUNTDOWN_BR = "com.example.imdhv.blumed.countdown_br";
    Intent i = new Intent(COUNTDOWN_BR);


    Map<Integer,CountDownTimer> kMap = new HashMap<Integer,CountDownTimer>();


   /* @Override
    public int onStartCommand(Intent intent, @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true) int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
        CountDownTimer countDownTimer = kMap.get(f);
        f++;
    }*/

    CountDownTimer cdt = null;





    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
