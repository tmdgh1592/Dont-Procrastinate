package com.app.buna.dontdelay.Widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.app.buna.dontdelay.R;
import com.app.buna.dontdelay.DB.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public Context context = null;
    public ArrayList<WidgetItem> arrayList;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String todayDate = sdf.format(new Date());

    public MyRemoteViewsFactory(Context context){
        this.context = context;
    }

    protected void setData() {
        arrayList = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, content, isFavorite FROM todo_table WHERE (dayToDo=? or howRepeat=?) AND isclear=?", new String[]{todayDate, "EVERYDAY", "0"});

        while(cursor.moveToNext()){
            WidgetItem widgetItem = new WidgetItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
            arrayList.add(widgetItem);
        }

    }

    @Override
    public void onCreate() {
        setData();
    }

    @Override
    public void onDataSetChanged() {
        setData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews listViewWidget = new RemoteViews(context.getPackageName(), R.layout.widget_listview_item);
        listViewWidget.setTextViewText(R.id.list_item_content, arrayList.get(position).getContent());
        if(arrayList.get(position).getIsFavorite() == 1){
            listViewWidget.setImageViewResource(R.id.widget_favorite, R.drawable.item_favorite_icon);
        }else{
            listViewWidget.setImageViewResource(R.id.widget_favorite, R.drawable.item_empty_favorite_icon);
        }

        Intent dataIntent = new Intent();
        dataIntent.putExtra("item_id", arrayList.get(position).get_id());
        dataIntent.putExtra("item_content", arrayList.get(position).getContent());
        listViewWidget.setOnClickFillInIntent(R.id.list_item_content, dataIntent);

        return listViewWidget;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
