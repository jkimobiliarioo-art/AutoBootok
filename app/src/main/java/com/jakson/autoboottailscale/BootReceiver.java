package com.jakson.autoboottailscale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || 
            "android.intent.action.QUICKBOOT_POWERON".equals(action) ||
            Intent.ACTION_SCREEN_ON.equals(action)) {

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "AutoBootTailscale::WakeLock"
            );
            wakeLock.acquire(40000);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                conectarTailscale(context);
            }, 20000);
        }
    }

    private void conectarTailscale(Context context) {
        try {
            Intent vpnIntent = new Intent("com.tailscale.ipn.CONNECT_VPN");
            vpnIntent.setPackage("com.tailscale.ipn");
            context.sendBroadcast(vpnIntent);

            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.tailscale.ipn");
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}