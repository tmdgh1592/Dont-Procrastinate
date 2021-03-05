package com.app.buna.dontdelay.Push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.buna.dontdelay.Activity.MainActivity;
import com.app.buna.dontdelay.R;
import com.app.buna.dontdelay.DB.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private ArrayList<String> contentList;
    String summaryText = "";
    String alarmTime = null;
    int alarmId;
    SimpleDateFormat mysdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", java.util.Locale.getDefault());
    SharedPreferences setting, mainSetting;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive: getData");
        contentList = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        alarmId = intent.getIntExtra("alarmId", 0);
        Cursor cursor = null;

        if(alarmId == AlarmVO.Id.MORNINGID) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            setting = PreferenceManager.getDefaultSharedPreferences(context);
            mainSetting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
            editor = mainSetting.edit();

            /* 예정 알람시간보다 현재 시간의 minute이 더 작을 경우 푸시알림을 취소함
                -> editactivty에서 뒤로가기했을 때 발생하는 무분별한 푸시알림을 방지하기 위함
                ** 어느정도 텀을 주기 위해 5초 추가
             */

            //Calendar alarmCalendar = Calendar.getInstance();
            /*try {
                alarmCalendar.setTime(mysdf.parse(alarmTime));
                alarmCalendar.add(alarmCalendar.SECOND, 5);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/




           /* if(mainSetting.getInt("firstStart", 0) == 0){
                editor.putInt("firstStart", 1);
                editor.commit();
                Log.d("AlarmReceiver", "onReceive: "+ alarmId +" 모닝 종료");
                return;
            }*/

            cursor = db.rawQuery("SELECT content FROM todo_table WHERE (howRepeat=? OR dayToDo=?) AND isclear=?", new String[]{"EVERYDAY", sdf.format(new Date()), "0"});
            Log.d("AlarmReceiver", "onReceive: REPEAT");

        }else if(alarmId != AlarmVO.Id.MORNINGID) {
            alarmTime = intent.getStringExtra("alarmTime");

            /* 예정 알람시간보다 현재 시간의 minute이 더 작을 경우 푸시알림을 취소함
                -> editactivty에서 뒤로가기했을 때 발생하는 무분별한 푸시알림을 방지하기 위함
                ** 어느정도 텀을 주기 위해 5초 추가
             */

            Calendar alarmCalendar = Calendar.getInstance();
            try {
                alarmCalendar.setTime(mysdf.parse(alarmTime));
                alarmCalendar.add(alarmCalendar.SECOND, 5);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(alarmCalendar.getTimeInMillis() < System.currentTimeMillis()){
                new AlarmHATT(context).alarmManagerCancel(alarmId);
                Log.d("AlarmReceiver", "onReceive: "+ alarmId +" 종료");
                Log.d("AlarmReceiver", alarmCalendar.getTimeInMillis() + "<" + System.currentTimeMillis());
                return;
            }

            cursor = db.rawQuery("SELECT content FROM todo_table WHERE (alarmYear=? AND alarmMonth=? AND alarmDate=? AND alarmHour=? AND alarmMin=?) AND isclear=?",
                    new String[]{alarmTime.split("-")[0], alarmTime.split("-")[1], alarmTime.split("-")[2], alarmTime.split("-")[3], alarmTime.split("-")[4], "0"});

            Log.d("AlarmReceiver", "onReceive: NOREPEAT");
            Log.d("AlarmReceiver", "onReceive: " + alarmTime.split("-")[0]+ alarmTime.split("-")[1]+ alarmTime.split("-")[2]+ alarmTime.split("-")[3]+ alarmTime.split("-")[4]);
        }

        while(cursor.moveToNext()){
            contentList.add(cursor.getString(0));
        }

        /* 할 일 내용 string builder에 append */
        StringBuilder contentStringBuilder = new StringBuilder();
        if(contentList.size() == 0 && alarmId == AlarmVO.Id.MORNINGID){   // 아침 반복 일정에 아무것도 없을 경우
            contentStringBuilder.append("일정이 없습니다.");
        }else if(contentList.size() == 0 && alarmId != AlarmVO.Id.MORNINGID){ // 일정도 없고 반복이 아닌경우 : 알람울릴 데이터가 삭제되거나 시간이 바뀐경우
            Log.d("AlarmReceiver", "onReceive: exit");
            return; // 아무것도 하지 않고 종료 : 푸시알림 보내지 않음
        }else {
            for (int i = 0; i < contentList.size(); i++) {
                contentStringBuilder.append(contentList.get(i) + "\n");
            }
            contentStringBuilder.delete(contentStringBuilder.length() - 1, contentStringBuilder.length());
        }
        if (contentList.size() > 0) {
            summaryText = "+ " + (contentList.size()) + "more";
        }
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context, "default");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, AlarmVO.Id.MORNINGID, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mbuilder.setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL)
                .setContentIntent(pendingIntent)
                .setTicker("미루지마 - 푸시 알림이 도착했습니다.")
                .setContentTitle("미루지 마")
                .setContentText("알림을 펼쳐서 내용을 확인하세요!")
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("일정")
                        .bigText(contentStringBuilder.toString())
                        .setSummaryText(summaryText));
        if(alarmId == AlarmVO.Id.MORNINGID){
            mbuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .setBigContentTitle((setting.getString("morning_ment", "오늘의 일정")).equals("") ? "오늘의 일정" : setting.getString("morning_ment", "오늘의 일정"))
                    .bigText(contentStringBuilder.toString())
                    .setSummaryText(summaryText));

            /*다음날 반복을 위해 또 푸시 알림 설정*/
            int[] time = new AlarmHATT(context).getTime(setting.getString("morning_push_list", ""));
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), time[0], time[1], 00);
            calendar.add(Calendar.DATE, 1);

            (new AlarmHATT(context, AlarmVO.Id.MORNINGID)).alarm(calendar);
            Log.d("AlarmReceiver", "onReceive: 다음날 아침 알림 전송");
            Log.d("AlarmReceiver", "onReceive: " + calendar.get(Calendar.DATE) + " " + time[0] + " " + time[1]);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_HIGH));
        }

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK  |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "My:Tag");
        wakeLock.acquire(5000);

        notificationManager.notify(alarmId, mbuilder.build());

        if(alarmId != AlarmVO.Id.MORNINGID){
            new AlarmHATT(context).alarmManagerCancel(alarmId);
        }


    }
}
