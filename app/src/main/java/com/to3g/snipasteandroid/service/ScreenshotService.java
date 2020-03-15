package com.to3g.snipasteandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.to3g.snipasteandroid.receiver.MyReceiver;

public class ScreenshotService extends Service {
    public ScreenshotService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "截屏服务", Toast.LENGTH_SHORT).show();
        Intent it = new Intent();
        it.setAction(MyReceiver.ACTION_SCREENSHOT);
        sendBroadcast(it);
        return super.onStartCommand(intent, flags, startId);
    }
}
