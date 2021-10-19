package com.app.buna.dontdelay.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmHATT {
    private Context context;
    private int alarmId = 0;
    private ArrayList<String> arrayList;

    private SharedPreferences setting, mainSetting;
    private SharedPreferences.Editor editor;

    public static AlarmManager mAlarmMgr = null;
    public static PendingIntent mAlarmIntent = null;

    public AlarmHATT(Context context) {
        this.context = context;
    }

    public AlarmHATT(Context context, int _id) {
        this.context = context;
        this.alarmId = _id;
    }

    public AlarmHATT(Context context, int _id, ArrayList<String> arrayList) {
        this.context = context;
        this.alarmId = _id;
        this.arrayList = arrayList;
    }

    public void alarm(Calendar calendar) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        String alarmTime = sdf.format(calendar.getTime());

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmId", alarmId);
        intent.putStringArrayListExtra("contentList", arrayList);
        intent.putExtra("alarmTime", alarmTime);

        PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        /* M 이상부터는 setExactAndAllowWhileIdle
        *  KITKAT 이상부터는 setExact
        *  그 미만 버전은 set
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            Log.d("AlarmHATT", "Type : setExactAndAllowWhileIdle");
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            Log.d("AlarmHATT", "Type : setExact");
        }else{
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
        Log.d("AlarmHATT", "Send alarm To AlarmReceiver : " + alarmTime);

    }

    public void morningAlarm() {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        setting = PreferenceManager.getDefaultSharedPreferences(context);
        mainSetting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = mainSetting.edit();


        int[] time = getTime(setting.getString("morning_push_list", ""));

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmId", alarmId);
        intent.putStringArrayListExtra("contentList", arrayList);
        intent.putExtra("REPEAT", 1);

        PendingIntent sender = PendingIntent.getBroadcast(context, AlarmVO.Id.MORNINGID, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), time[0], time[1], 00);
        Log.d("ALARMTIME", "morningAlarm: "+time[0]+"시 "+ time[1]+"분");


        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,sender);
        //am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        Log.d("AlarmHATT", "Send morning alarm to AlarmReceiver");
    }

    public void alarmManagerCancel(int id) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context, id, intent, 0);
        am.cancel(p);
        p.cancel();
        Log.d("AlarmHATT", id + " is canceled");
    }

    public int[] getTime(String time){
        //setting.getString("morning_push_list", "");

        int initHour=7, initMin = 0;

        int hour = initHour;
        int min = initMin;

        if(!time.equals("")){
            switch (Integer.parseInt(time)){
                case 0:
                    hour= 6;
                    min = 0;
                    break;
                case 1:
                    hour= 6;
                    min = 30;
                    break;
                case 2:
                    hour= 7;
                    min = 0;
                    break;
                case 3:
                    hour= 7;
                    min = 30;
                    break;
                case 4:
                    hour= 8;
                    min = 0;
                    break;
                default:
                    hour = 7;
                    min = 0;
            }
        }

        return new int[]{hour, min};
    }

}
