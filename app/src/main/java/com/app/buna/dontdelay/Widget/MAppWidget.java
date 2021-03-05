package com.app.buna.dontdelay.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.app.buna.dontdelay.Activity.MainActivity;
import com.app.buna.dontdelay.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class MAppWidget extends AppWidgetProvider {

    static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
    static SimpleDateFormat sdfE = new SimpleDateFormat("EEEE");

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        /* widget에서 link 버튼 눌렀을 때 */

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pMainIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_link, pMainIntent);
        views.setTextViewText(R.id.widget_date, sdf.format(new Date()).split("-")[0] + "/" + sdf.format(new Date()).split("-")[1]
                + " (" + MainActivity.getTranslatedKoreanDow(sdfE.format(new Date()), 2) + ")");


        /* widget에서 Refresh 버튼 눌렀을 때 */
        Intent updateIntent = new Intent(context, MAppWidget.class);
        ComponentName componentName = new ComponentName(context.getPackageName(), MAppWidget.class.getName());

        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetManager.getAppWidgetIds(componentName));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_refresh, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName testWidget = new ComponentName(context.getPackageName(), MAppWidget.class.getName());
        String action = intent.getAction();
        int[] widgetIds = appWidgetManager.getAppWidgetIds(testWidget);

        if(action != null & action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) && widgetIds.length > 0){
            this.onUpdate(context, AppWidgetManager.getInstance(context), widgetIds);
            this.onUpdate(context, AppWidgetManager.getInstance(context), widgetIds);
            Log.d("MAppWidget", "onReceive: update");
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            //RemoteViewsService 실행 등록시키는 함수
            Intent serviceIntent = new Intent(context, MyRemoteViewsService.class);
            serviceIntent.setData(Uri.fromParts("content", String.valueOf(appWidgetId+new Random().nextInt()), null));
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            widget.setRemoteAdapter(R.id.widget_list_view, serviceIntent);

            appWidgetManager.updateAppWidget(appWidgetIds, widget);
            Log.d("MAppWidget", "onUpdate: onUpdate");

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }


        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}


