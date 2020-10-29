package utilities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import com.agnekdev.planlecturebible.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIF_ID = 123;
    NotificationHelper notificationHelper;
    @Override
    public void onReceive(Context context, Intent intent) {
        Functions.agnekLog("Notification recu");
        // TODO: This method is called when the BroadcastReceiver is receiving

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationHelper = new NotificationHelper(context);
            notificationHelper.notify(1,false,"Lecture du jour","2 Timothée 2");

        } else {
            notificationOldSdk(context,"Lecture du jour","test");
        }
    }

    void notificationOldSdk(Context context,String title,String message){
        Resources res = context.getResources();

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)     // drawable for API 26
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText( message)
                .setVibrate(new long[] { 0, 500, 110, 500, 110, 450, 110, 200, 110,
                        170, 40, 450, 110, 200, 110, 170, 40, 500 } )
                .setLights(Color.RED, 3000, 3000)
                .build();             // à partir de l'API 16

        NotificationManager notifManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify( NOTIF_ID, notification );
    }
}
