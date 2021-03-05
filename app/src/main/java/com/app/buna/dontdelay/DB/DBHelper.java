package com.app.buna.dontdelay.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 24;
    public static final String DBName = "AppDB";
    public static final String toDoSql = "create table if not exists todo_table (" +
            "_id integer PRIMARY KEY AUTOINCREMENT," +                    // 0 Column
            "content text not null," +                                    // 1
            "dayToDo text not null," +                                    // 2  format year-month-date
            "repeatDOW text not null," +                                  // 3
            "created text not null," +                                    // 4
            "howRepeat text not null," +                                  // 5
            "repeatMonth text not null," +                                // 6
            "repeatDate text not null," +                                 // 7
            "isFavorite text not null," +                                 // 8
            "isclear text not null, " +                                   // 9
            "alarmYear text not null," +                                  // 10
            "alarmMonth text not null," +                                 // 11
            "alarmDate text not null," +                                  // 12
            "alarmHour text not null," +                                  // 13
            "alarmMin text not null, " +                                  // 14
            "repeatText text not null," +                                 // 15
            "howRepeatUnit text not null," +                              // 16
            "memo text not null," +                                       // 17
            "forSeparatingCreated text not null," +                       // 18
            "dDay integer not null," +                                    // 19
            "actionBarNoti integer)";                                          // 20




    public static final String backupSql = "create table if not exists backup_table (" +
            "_id integer PRIMARY KEY AUTOINCREMENT," +                    // 0 Column
            "content text not null," +                                    // 1
            "dayToDo text not null," +                                    // 2  format year-month-date
            "repeatDOW text not null," +                                  // 3
            "created text not null," +                                    // 4
            "howRepeat text not null," +                                  // 5
            "repeatMonth text not null," +                                // 6
            "repeatDate text not null," +                                 // 7
            "isFavorite text not null," +                                 // 8
            "isclear text not null, " +                                   // 9
            "alarmYear text not null," +                                  // 10
            "alarmMonth text not null," +                                 // 11
            "alarmDate text not null," +                                  // 12
            "alarmHour text not null," +                                  // 13
            "alarmMin text not null, " +                                  // 14
            "repeatText text not null," +                                 // 15
            "howRepeatUnit text not null," +                              // 16
            "memo text not null," +                                       // 17
            "forSeparatingCreated text not null," +                       // 18
            "dDay integer not null)";                                     // 19



    public DBHelper(Context context){
        super(context, DBName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { // 앱 설치시 한번만 실행, 변경 시 DATABASE_VERSION up.

        db.execSQL(toDoSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 버전이 변경될 때마다 실행, db table upgrade해줄 때
        if(newVersion == DATABASE_VERSION) {

            /*

            이 부분에 복구할 로직 추가해야함
            (로직) : 복구용 테이블을 하나 더 만들고 거기서 값 가져오기

             */

            db.execSQL("DROP TABLE todo_table");
            onCreate(db);
        }

    }
}
