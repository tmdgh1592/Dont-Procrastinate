package com.app.buna.dontdelay.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.app.buna.dontdelay.activity.EditActivty;
import com.app.buna.dontdelay.adapter.recycler.data.ToDoData;

import java.util.ArrayList;

public class GoEditActivity {
    int position, CODE;
    Context context;
    Activity mainActivity;
    ArrayList<ToDoData> mDataset;

    public GoEditActivity(Activity mainActivity, ArrayList<ToDoData> mDataset, int position, int CODE, Context context){
        this.position = position;
        this.CODE = CODE;
        this.context = context;
        this.mainActivity = mainActivity;
        this.mDataset = mDataset;
    }

    public void goToEditActivity(){

        // 일정 추가도중에 다른 부분 클릭 방지
/*
            if(mainActivity.isSoftKeyboardShown() || position == 0){
                return;
            }
*/
            ToDoData data = mDataset.get(position);
            Intent intent = new Intent(context, EditActivty.class);

            //old Data
            intent.putExtra("content", data.getToDoContent()); // [String] format : content
            intent.putExtra("favorite", data.getIsFavroite());
            intent.putExtra("memo", data.getMemo());
            intent.putExtra("toDoDay", data.getToDoDay());
            intent.putExtra("createTime", data.getCreated());
            intent.putExtra("repeatMonth", data.getRepeatMonth());
            intent.putExtra("repeatDate", data.getRepeatDate());
            intent.putExtra("repeatDow", data.getRepeatDow());
            intent.putExtra("howRepeat", data.getHowRepeat());
            intent.putExtra("repeatText", data.getRepeatText());
            intent.putExtra("alarmYear", data.getAlarmYear());
            intent.putExtra("alarmMonth", data.getAlarmMonth());
            intent.putExtra("alarmDate", data.getAlarmDate());
            intent.putExtra("alarmHour", data.getAlarmHour());
            intent.putExtra("alarmMin", data.getAlarmMin());
            intent.putExtra("isClear", data.getIsClear());
            intent.putExtra("howRepeatUnit", data.getHowRepeatUnit());
            intent.putExtra("forSeparatingCreated", data.getForSeparatingCreated());
            intent.putExtra("actionBarNoti", data.getActionBarNoti());
            // DB 특정 조건 값 전송 => editactivity에서 update, where문으로 쿼리값 수정
            mainActivity.startActivityForResult(intent, CODE);
        }


    }
