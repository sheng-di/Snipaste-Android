package com.to3g.snipasteandroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    public static final String ACTION_SCREENSHOT = "ACTION_SCREENSHOT";
    MyReceiverHandler myReceiverHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String intentAction = intent.getAction();
        if (intentAction.equals(ACTION_SCREENSHOT)) {
            myReceiverHandler.doScreenshot();
        }
    }

    public void setMyReceiverHandler (MyReceiverHandler m) {
        this.myReceiverHandler = m;
    }
}
