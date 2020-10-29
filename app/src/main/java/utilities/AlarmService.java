package utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class AlarmService extends Service {
    final static String MY_TAG = "IntentAlarmservice";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startNotification();
        Functions.agnekLog("Srvice called");
    }


    private void startNotification() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notification = sp.getBoolean("notifications", false);
        if (!notification) {
            return;
        }
        String strTime = sp.getString("time_not", "05:00");
        String[] timeSplit = strTime.split(":");
        String strHour = timeSplit[0];
        String strMinute = timeSplit[1];
        int hour = Integer.parseInt(strHour.replaceFirst("^0+(?!$)", ""));
        int minute = Integer.parseInt(strMinute.replaceFirst("^0+(?!$)", ""));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }*/


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Functions.agnekLog("Service tué !!!!!!!!!!!!!!!!!!!!!");
    }
}
