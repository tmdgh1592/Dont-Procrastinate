package com.app.buna.dontdelay.noti.actionbar;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.app.buna.dontdelay.activity.MainActivity;
import com.app.buna.dontdelay.calendar.calc.CalendarDOW;
import com.app.buna.dontdelay.db.DBHelper;
import com.app.buna.dontdelay.R;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ActionBarNotification {

    public int fixedNotiId = 1593;

    private Context context;
    private ArrayList<String> contentList;
    //private ArrayList<String> dayList;
    private String channedId = "1592";
    private String channelName = "상단 고정 알림";
    private String channelDiscription = "상단 고정 알림을 위한 푸시알림 채널입니다.";
    //private Uri alertSound;

    /*private String lightAction = "TURN_ON_LIGHT";
    private String webAction = "SHOW_ME_WEB";*/


    public ActionBarNotification(Context context) {
        this.context = context;
        //alertSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.silence);
    }

    public void showCustomLayoutNotification(){
        createChannel();
        contentList = getContents();
        //dayList = getDays();

        /*Intent lightIntent = new Intent(context, ActionBarNotiBroadcastReceiver.class);
        lightIntent.setAction(lightAction);

        Intent webIntent = new Intent(context, ActionBarNotiBroadcastReceiver.class);
        webIntent.setAction(webAction);

        PendingIntent lightPendingIntent = PendingIntent.getBroadcast(context, 0, lightIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent webPendingIntent = PendingIntent.getBroadcast(context, 0, webIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/

        //커스텀 화면 만들기
        NotificationCompat.Builder mBuilder = createNotification();
        RemoteViews notificationLayoutExpanded  = new RemoteViews(context.getPackageName(), R.layout.custom_mini_notification);

        // 액션바에 '일정' 고정
        if(contentList.size() == 1){
            notificationLayoutExpanded.setTextViewText(R.id.noti_content1, "" + contentList.get(0));
            //notificationLayoutExpanded.setTextViewText(R.id.noti_day1, dayList.get(0));
        }else {
            notificationLayoutExpanded.setTextViewText(R.id.noti_content1, "고정된 일정이 없습니다.");
            notificationLayoutExpanded.setTextViewText(R.id.noti_content1, "");
        }

        //액션바 clickListener
        /*notificationLayoutExpanded.setOnClickPendingIntent(R.id.noti_light_icon, lightPendingIntent);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.noti_web_icon, webPendingIntent);*/


        //노티피케이션에 커스텀 뷰 장착
        mBuilder.setCustomContentView(notificationLayoutExpanded);
        mBuilder.setContentIntent(createPendingIntent());

        Notification notification = mBuilder.build();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(fixedNotiId, notification);

    }

    /**
     * 노티피케이션을 누르면 실행되는 기능을 가져오는 노티피케이션
     *
     * 실제 기능을 추가하는 것
     * @return
     */
    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @SuppressLint("WrongConstant")
    private NotificationCompat.Builder createNotification(){
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channedId)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(icon)
                .setContentTitle("미루지 마")
                .setContentText("상단바에서 고정 일정을 확인하세요!")
                .setSmallIcon(R.drawable.app_noti_icon) /*스와이프 전 아이콘*/
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.FLAG_FOREGROUND_SERVICE)
                .setOngoing(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }

    public void cancel(){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(fixedNotiId);
    }

    public boolean isIncluded(String forSeparatingCreated){
        int count;
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT actionBarNoti FROM todo_table WHERE forSeparatingCreated=? AND actionBarNoti=?",
                new String[]{forSeparatingCreated, "1"});

        Log.d("test", "isIncluded: " + cursor.getCount());
        count = cursor.getCount();

        if(count == 0) {
            return false;
        }else if(count > 0){
            return true;
        }
        return false;
    }

    public boolean isExist() {
        int count = 0;
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT actionBarNoti FROM todo_table WHERE actionBarNoti=1", null);

        while(cursor.moveToNext()){
            count++;
        }

        if(count == 0) {
            return false;
        }else if(count > 0){
            return true;
        }

        return false;
    }

    private ArrayList<String> getContents(){
        ArrayList<String> contentList = new ArrayList<>();

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT content FROM todo_table WHERE actionBarNoti=1", null);

        while(cursor.moveToNext()){
            contentList.add(cursor.getString(0));
        }
        db.close();

        return contentList;
    }

    private ArrayList<String> getDays() {
        ArrayList<String> dayList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        CalendarDOW calendarDOW;
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT dayToDo FROM todo_table WHERE actionBarNoti=1", null);

        while(cursor.moveToNext()){
            String date = cursor.getString(0);
            String[] dateArr = date.split("-");

            calendar.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2])); //요일 구하기
            calendarDOW = new CalendarDOW(calendar.get(Calendar.DAY_OF_WEEK));

            dayList.add(date + " ("+calendarDOW.getDow().split("요일")[0]+")");
        }
        db.close();

        return dayList;
    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channedId, channelName, NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(channelDiscription);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }



}

