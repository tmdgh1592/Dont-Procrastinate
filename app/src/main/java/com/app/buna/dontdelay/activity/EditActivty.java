package com.app.buna.dontdelay.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.buna.dontdelay.noti.actionbar.ActionBarNotification;
import com.app.buna.dontdelay.adapter.recycler.data.NotDoneRecyclerAdapter;
import com.app.buna.dontdelay.push.AlarmHATT;
import com.app.buna.dontdelay.R;
import com.app.buna.dontdelay.calendar.calc.CalendarDOW;
import com.app.buna.dontdelay.db.DBHelper;
import com.app.buna.dontdelay.calendar.calc.DayManager;
import com.app.buna.dontdelay.calendar.calc.HourType;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class EditActivty extends AppCompatActivity {

    private MainActivity mainActivity;
    private Calendar calendar;
    private SimpleDateFormat sdf;

    private static final int memoRequestCode = 1000;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Spinner howRepeatSpinner;
    private View howRepeatView;
    AlertDialog howRepeatDialog;
    AlertDialog.Builder builder;

    int textLength = 0;
    boolean todOrTmrCheck = false;
    SpannableStringBuilder ssb;

    private EditText howRepeatEditText;
    DayManager dayManager;

    private PopupMenu popupMenu;
    private String todayDayOfWeek, tomorrowDayOfWeek, nextWeekDayOfWeek, calendarDayOfWeek;
    private SpannableStringBuilder dowStringBuilder = new SpannableStringBuilder();
    private ArrayList<String> toDoDayList = new ArrayList<>();
    private CheckBox monCB, tueCB, wedCB, thurCB, friCB, satCB, sunCB;


    /* MainActivity????????? ?????? ?????? ????????? ???????????? ?????? ?????? */

    private Intent getIntent;
    private String toDoContent; // ????????? ??? ??????
    private String toDoDay; // format : year-month-date
    private String created;
    private String howRepeat, repeatMonth, repeatDate, repeatDay, repeatDow, repeatText; //repeatDow format : ??????????????????????????? || repeatText : addview.???????????????.text
    private String alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin;
    private String memo = "";
    private int isFavorite, isClear, howRepeatUnit, isFixed;
    private LinearLayout dateLayout, repeatLayout, alarmLayout;
    private RelativeLayout memoLayout;
    private TextView memoTextView;
    private String forSeparatingCreated;

    private int isFixChange;
    private boolean isContentChange = false;

    /* ???????????? */
    String toDoYear, toDoMonth, toDoDate, toDoDow;
    String dow = null;

    private EditText contentEditText;
    private ImageView favoriteImageView, calendarImageView, repeatImageView, alarmImageView, deleteImageView, backImageView, shareImageView, pinImageView;
    private TextView toDoDayTextView, repeatTextView, alarmTextView, createTextView;

    final Calendar createCal = Calendar.getInstance();

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setAds();
        initialize();

        deleteImageView.setOnClickListener(clickListener);
        favoriteImageView.setOnClickListener(clickListener);
        favoriteImageView.setOnTouchListener(touchListener);
        dayManager = new DayManager();

    }

    private void setAds(){
        MobileAds.initialize(this, initializationStatus -> {
        });
        mAdView = findViewById(R.id.editAct_adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initialize() {
        intentInit();
        setView();
        createHRDialog();
        loadCheckBox();
    }

    private void intentInit() {

        mainActivity = new MainActivity();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();

        getIntent = getIntent();
        toDoContent = getIntent.getStringExtra("content");
        isFavorite = getIntent.getIntExtra("favorite", 0);
        memo = getIntent.getStringExtra("memo");
        toDoDay = getIntent.getStringExtra("toDoDay");
        created = getIntent.getStringExtra("createTime");
        repeatMonth = getIntent.getStringExtra("repeatMonth");
        repeatDate = getIntent.getStringExtra("repeatDate");
        repeatDow = getIntent.getStringExtra("repeatDow");  // format : ??????????????????????????? ...
        howRepeat = getIntent.getStringExtra("howRepeat");
        repeatText = getIntent.getStringExtra("repeatText");
        alarmYear = getIntent.getStringExtra("alarmYear");
        alarmMonth = getIntent.getStringExtra("alarmMonth");
        alarmDate = getIntent.getStringExtra("alarmDate");
        alarmHour = getIntent.getStringExtra("alarmHour");
        alarmMin = getIntent.getStringExtra("alarmMin");
        isClear = getIntent.getIntExtra("isClear", 0);
        howRepeatUnit = getIntent.getIntExtra("howRepeatUnit", 1);
        forSeparatingCreated = getIntent.getStringExtra("forSeparatingCreated");
        isFixed = getIntent.getIntExtra("actionBarNoti", 0);
        isFixChange = isFixed;
    }

    private void setView() {
        pinImageView = findViewById(R.id.edit_text_pin);
        shareImageView = findViewById(R.id.edit_text_share);
        deleteImageView = findViewById(R.id.edit_delete_image_view);
        contentEditText = findViewById(R.id.edit_to_do_content_edit_text);
        favoriteImageView = findViewById(R.id.edit_favorite_image_view);
        calendarImageView = findViewById(R.id.edit_calendar_image_view);
        alarmImageView = findViewById(R.id.edit_alarm_image_view);
        repeatImageView = findViewById(R.id.edit_repeat_image_view);
        toDoDayTextView = findViewById(R.id.edit_to_do_day_text_view);
        repeatTextView = findViewById(R.id.edit_repeat_text_view);
        alarmTextView = findViewById(R.id.edit_alarm_text_view);
        dateLayout = findViewById(R.id.edit_date_layout);
        repeatLayout = findViewById(R.id.edit_repeat_layout);
        alarmLayout = findViewById(R.id.edit_alarm_layout);
        createTextView = findViewById(R.id.edit_create_text_view);
        backImageView = findViewById(R.id.back_image_view);
        pinImageView.setOnClickListener(clickListener);
        shareImageView.setOnClickListener(clickListener);
        backImageView.setOnClickListener(clickListener);
        dateLayout.setOnClickListener(clickListener);
        repeatLayout.setOnClickListener(clickListener);
        alarmLayout.setOnClickListener(clickListener);
        memoLayout = findViewById(R.id.memo_layout);
        memoTextView = findViewById(R.id.memo_text_view);
        howRepeatView = getLayoutInflater().inflate(R.layout.custom_how_repeat_dialog, null);
        howRepeatEditText = howRepeatView.findViewById(R.id.how_repeat_edit_text);
        howRepeatSpinner = howRepeatView.findViewById(R.id.how_repeat_spinner);
        howRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // ??????
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                LinearLayout layout = howRepeatView.findViewById(R.id.day_select_layout);
                switch (position) {
                    case 0:
                        howRepeatUnit = 1;
                        toDoDayList.clear();
                        layout.setVisibility(View.GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                    case 1:
                        howRepeatUnit = 7;
                        toDoDayList.clear();
                        layout.setVisibility(View.VISIBLE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_visible));
                        break;
                    case 2:
                        howRepeatUnit = 30;
                        toDoDayList.clear();
                        layout.setVisibility(View.GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                    case 3:
                        howRepeatUnit = 365;
                        toDoDayList.clear();
                        layout.setVisibility(View.GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("LogD", "onNothingSelected: nothing selected");
            }
        });

        updatePinImage();

        if (isClear == 1) {
            contentEditText.setPaintFlags(contentEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); }
        if ((isFavorite == 1)) {
            favoriteImageView.setBackgroundResource(R.drawable.favorite_icon);
        } else {
            favoriteImageView.setBackgroundResource(R.drawable.empty_favorite_icon);
        }

        calendarImageView.setBackgroundResource(R.drawable.enable_edit_calendar_icon);

        try{
            if (!howRepeat.equals("NOREPEAT")) {
                repeatImageView.setBackgroundResource(R.drawable.enable_edit_repeat_icon);
            } else{
                repeatImageView.setBackgroundResource(R.drawable.disabled_edit_repeat_icon);
            }
            if (!alarmYear.equals("")) {
                alarmImageView.setBackgroundResource(R.drawable.enable_edit_alarm_icon);
            } else {
                alarmImageView.setBackgroundResource(R.drawable.disabled_edit_alarm_icon);
            }
        }catch (NullPointerException e){
        }



        contentEditText.setText(toDoContent);

        try {
            toDoYear = toDoDay.split("-")[0];
            toDoMonth = toDoDay.split("-")[1];
            toDoDate = toDoDay.split("-")[2];
            toDoDow = null;
        }catch (NullPointerException e){

        }

        try {
            dow = mainActivity.getDOW((toDoYear), (toDoMonth), (toDoDate), 2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* ?????? ?????? ?????? */
        try {
            if (!toDoYear.equals("")) {
                toDoDayTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                if (!toDoYear.equals(Integer.toString(calendar.get(Calendar.YEAR)))) {        // ????????? ????????? ?????? ??????
                    toDoDayTextView.setText(toDoYear + "??? " + toDoMonth + "??? "
                            + toDoDate + "??? (" + dow + ")");
                } else if (toDoDay.equals(sdf.format(new Date()))) {           // ????????? ????????? ??????
                    toDoDayTextView.setText("??????");
                } else if(toDoYear.equals("")){
                    toDoDayTextView.setText("??????");
                }
                else {     // ????????? ????????? ????????? ??????
                    toDoDayTextView.setText(toDoMonth + "??? " + toDoDate + "??? (" + dow + ")"); // ?????? ????????? ????????? ??????
                }
            }
        }catch (NullPointerException e){}




        /*?????? ??????*/
        try {
            switch (howRepeat) {
                case "EVERYDAY":
                    repeatTextView.setTextSize(16);
                    repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                    repeatTextView.setText("?????? ??????");
                    break;
                case "EVERYWEEK":
                    repeatTextView.setTextSize(16);
                    repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                    repeatTextView.setText("?????? ??????");
                    break;
                case "EVERYMONTH":
                    repeatTextView.setTextSize(16);
                    repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                    repeatTextView.setText("?????? ??????");
                    break;
                case "CALENDAR":
                    switch (howRepeatUnit) {
                        case 1:
                            repeatTextView.setTextSize(16);
                            repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                            repeatTextView.setText(repeatText + "????????? ??????");
                            break;
                        case 7:
                            repeatTextView.setTextSize(14);
                            repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                            repeatTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);

                            ssb = new SpannableStringBuilder(repeatText + "?????????\n" + repeatDow.replaceAll("??????", "?????? "));
                            textLength = repeatText.length() + "?????????\n".length();
                            // textLength ~ ?????? ????????? ?????? ???????????? ?????? ??????
                            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.defaultDisableColor)),
                                    textLength, (repeatText + "?????????\n" + repeatDow.replaceAll("??????", "?????? ")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ssb.setSpan(new AbsoluteSizeSpan(16, true), textLength, (repeatText + "?????????\n" + repeatDow.replaceAll("??????", "?????? ")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            repeatTextView.setText(ssb);
                            break;
                        case 30:
                            repeatTextView.setTextSize(16);
                            repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                            repeatTextView.setText(repeatText + "???????????? ??????");
                            break;
                        case 365:
                            repeatTextView.setTextSize(16);
                            repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                            repeatTextView.setText(repeatText + "????????? ??????");
                            break;
                    }
                    break;
                case "NOREPEAT":
                    repeatTextView.setText("??????");
                    repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.defaultDisableColor));
                    break;
            }
        }catch (NullPointerException e){}

//        alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin;
        try{
        if (!alarmMin.equals("")) {
            // Toast.makeText(this, alarmYear +"-" + alarmMonth + "-" + alarmDate +"\n" + alarmHour + "???" + alarmMin+"???", Toast.LENGTH_LONG).show();
            alarmTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
            String todayYear = sdf.format(new Date()).split("-")[0];
            String alarmTimeText = null;
            String alarmDayText = null;
            String hourType = "??????"; //?????? or ??????
            String expressingHour = null;

            if(!alarmHour.equals("") || !alarmMin.equals("")) {
                HourType ht = new HourType(Integer.parseInt(alarmHour), Integer.parseInt(alarmMin));
                hourType = ht.getHourType();
                expressingHour = ht.getTranslatedHour();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date date = null;

            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(alarmYear + "-" + alarmMonth + "-" + alarmDate + " " + alarmHour + ":" + alarmMin + ":" + "00");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (hourType.equals("??????")) {
                alarmTimeText = "'" + hourType + "'??? ??????\n";
            } else {
                alarmTimeText = hourType + " " + expressingHour + "??? " + alarmMin + "?????? ??????\n";
            }

            // ?????? ????????? '??????', '??????'??? ??????
            Calendar todCal = Calendar.getInstance();
            Calendar tmrCal = Calendar.getInstance();
            tmrCal.add(Calendar.DATE, 1);
            if ((new SimpleDateFormat("yyyyMMdd").format(todCal.getTime())).equals((alarmYear + alarmMonth + alarmDate))) {
                alarmDayText = "??????";
                todOrTmrCheck = true;

            } else if ((new SimpleDateFormat("yyyyMMdd").format(tmrCal.getTime())).equals((alarmYear + alarmMonth + alarmDate))) {
                alarmDayText = "??????";
                todOrTmrCheck = true;
            }

            if (todOrTmrCheck == false) {     // ?????? ????????? '??????', '??????'??? ?????? ??????
                if (alarmYear.equals(todayYear)) {     // ?????? ????????? "??? ???" ??? ??????
                    alarmDayText = alarmMonth + "??? " + alarmDate + "??? (" + mainActivity.getTranslatedKoreanDow(sdf.format(date), 2) + ")";
                } else {      // ????????? ?????? ??????
                    alarmDayText = alarmYear + "??? " + alarmMonth + "??? " + alarmDate + "??? (" + mainActivity.getTranslatedKoreanDow(sdf.format(date), 2) + ")";
                }
            }
            alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
            textLength = alarmTimeText.length();
            ssb = new SpannableStringBuilder(alarmTimeText + alarmDayText);
            ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.nightBackgroundColor)), textLength, (alarmTimeText + alarmDayText).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new AbsoluteSizeSpan(13, true), textLength, (alarmTimeText + alarmDayText).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            alarmTextView.setText(ssb);
        } else {  // ?????? ?????? ?????? ?????? ??????
            alarmTextView.setText("?????? ??????");
            alarmTextView.setTextColor(ContextCompat.getColor(this, R.color.defaultDisableColor));
            alarmImageView.setImageResource(R.drawable.disabled_edit_alarm_icon);
        }}catch (NullPointerException e){}


        /* ?????? ?????? */

        try{
        memoLayout.setOnClickListener(clickListener);
        if(!memo.equals("")){
            memoTextView.setText(memo);
        }}catch (NullPointerException e){}

        /* ?????? ?????? ?????? */
        String createdString = created;
        StringBuilder stringBuilder = new StringBuilder("");

        try {
            createCal.setTime(sdf.parse(createdString));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e){

        }

        try {
            if (!createdString.split("-")[0].equals(sdf.format(new Date()).split("-")[0])) {      // ?????? ????????? ????????? ?????? ?????? '??????'?????? ??????
                stringBuilder.append(createdString.split("-")[0] + "??? ")
                        .append(createdString.split("-")[1] + "??? ")
                        .append(createdString.split("-")[2] + "??? ")
                        .append("(" + new CalendarDOW(createCal.get(Calendar.DAY_OF_WEEK)).getDow().replace("??????", "") + ")")
                        .append("??? ?????????");


            } else {                                                                                          // ?????? ????????? ????????? ?????? '??????' ??????
                stringBuilder.append(createdString.split("-")[1] + "??? ")
                        .append(createdString.split("-")[2] + "??? ")
                        .append("(" + new CalendarDOW(createCal.get(Calendar.DAY_OF_WEEK)).getDow().replace("??????", "") + ")")
                        .append("??? ?????????");
            }

            createTextView.setText(stringBuilder.toString());
            stringBuilder.setLength(0);
        }catch (NullPointerException e){}

    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            popupMenu = new PopupMenu(getApplicationContext(), view);
            popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);

            switch (view.getId()) {
                case R.id.edit_text_share:
                    shareContent(contentEditText.getText().toString(), memo);
                    break;
                case R.id.edit_delete_image_view:
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivty.this)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    ActionBarNotification actionBarNotification = new ActionBarNotification(EditActivty.this);

                                    if(actionBarNotification.isIncluded(forSeparatingCreated)){
                                        actionBarNotification.cancel();
                                    }

                                    db.execSQL("DELETE FROM todo_table WHERE forSeparatingCreated=? AND content=?", new String[]{
                                            forSeparatingCreated, toDoContent
                                    });
                                    db.close();
                                    finish();
                                    NotDoneRecyclerAdapter.lastPosition--;

                                }
                            }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setMessage(toDoContent + "???(???) ?????????????????????????");
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                        }
                    });
                    alertDialog.show();
                    break;
                case R.id.edit_favorite_image_view:
                    if (isFavorite == 1) {
                        favoriteImageView.setBackgroundResource(R.drawable.empty_favorite_icon);
                        isFavorite = 0;
                    } else {
                        favoriteImageView.setBackgroundResource(R.drawable.favorite_icon);
                        isFavorite = 1;
                    }
                    break;
                case R.id.memo_layout:
                    Intent intent = new Intent(EditActivty.this, MemoActivity.class);
                    intent.putExtra("memo", memo);
                    startActivityForResult(intent, memoRequestCode);
                    break;
                case R.id.edit_date_layout:
                    getMenuInflater().inflate(R.menu.add_first_menu, popupMenu.getMenu());
                    todayDayOfWeek = mainActivity.setDeadlineDOW("??????");
                    tomorrowDayOfWeek = mainActivity.setDeadlineDOW("??????");
                    nextWeekDayOfWeek = mainActivity.setDeadlineDOW("?????????");
                    popupMenu.getMenu().add(Menu.NONE, 1, 0, "?????? (" + todayDayOfWeek + ")");  //groupId, itemId, order, title
                    popupMenu.getMenu().add(Menu.NONE, 2, 1, "?????? (" + tomorrowDayOfWeek + ")");
                    popupMenu.getMenu().add(Menu.NONE, 3, 2, "?????? ??? (" + nextWeekDayOfWeek + ")");
                    popupMenu.getMenu().add(Menu.NONE, 4, 3, "?????? ??????");
                    popupMenu.show();
                    break;
                case R.id.edit_alarm_layout:
                    getMenuInflater().inflate(R.menu.add_second_menu, popupMenu.getMenu());
                    popupMenu.show();
                    break;
                case R.id.edit_repeat_layout:
                    getMenuInflater().inflate(R.menu.add_third_menu, popupMenu.getMenu());
                    toDoDayList.clear();
                    popupMenu.show();
                    break;
                case R.id.back_image_view:
                    onBackPressed();
                    break;
                case R.id.edit_text_pin:
                    updateFix(false);
                    break;
            }
        }
    };

    private void updateFix(boolean isBack) {

        ActionBarNotification actionBarNotification = new ActionBarNotification(EditActivty.this);

        if(isBack){
            actionBarNotification.showCustomLayoutNotification();
            return;
        }


        DBHelper dbHelper = new DBHelper(EditActivty.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        if(isFixed == 0) {
            /* ?????? ??? ???????????? ????????????*/

            if(actionBarNotification.isExist()){
                db.execSQL("UPDATE todo_table SET actionBarNoti=? WHERE actionBarNoti=?",
                        new String[]{"0", "1"});
            }

            /* ?????? ?????? ?????? ?????? */
            isFixed = 1;
            db.execSQL("UPDATE todo_table SET actionBarNoti=?, content=? WHERE forSeparatingCreated=? AND content=?",
                    new String[]{"1", contentEditText.getText().toString(), forSeparatingCreated, toDoContent});
            updatePinImage();

            //actionBarNotification.cancel();
            actionBarNotification.showCustomLayoutNotification();

        }else if(isFixed == 1){
            isFixed = 0;
            db.execSQL("UPDATE todo_table SET actionBarNoti=?, content=? WHERE forSeparatingCreated=? AND content=?",
                    new String[]{"0", contentEditText.getText().toString(), forSeparatingCreated, toDoContent});
            updatePinImage();

            actionBarNotification.cancel();
        }
        db.close();
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (view.getId()) {
                case R.id.edit_favorite_image_view:
                    if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                        if (isFavorite == 1) {
                            favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_uncheck));
                        } else {
                            favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_uncheck));
                        }
                    } else if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                        if (isFavorite == 1) {
                            favoriteImageView.setBackgroundResource(R.drawable.empty_favorite_icon);
                            favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_check));
                            isFavorite = 0;
                        } else {
                            favoriteImageView.setBackgroundResource(R.drawable.favorite_icon);
                            favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_check));
                            isFavorite = 1;
                        }
                    }
            }
            return true;
        }
    };

    @Override
    protected void onStop() {
        saveData();
        super.onStop();
    }

    public void createHRDialog() {
        builder = new AlertDialog.Builder(EditActivty.this);


        builder.setView(howRepeatView)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    }
                });

        howRepeatDialog = builder.create();
        howRepeatDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button posBtn = howRepeatDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button negBtn = howRepeatDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                negBtn.setTextColor(Color.GRAY);
                posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dowStringBuilder = new SpannableStringBuilder();
                        howRepeat = "CALENDAR";
                        addToDoDOWToArrayList();
                        Iterator<String> iterator = toDoDayList.iterator();
                        while (iterator.hasNext()) {
                            dowStringBuilder.append(iterator.next());           // ???????????????????????????
                        }
                        repeatDow = dowStringBuilder.toString();
                        if (howRepeatEditText.getText().toString().equals("0")) {
                            Toast.makeText(getApplicationContext(), "0?????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            toDoDayList.clear();
                        } else if (howRepeatUnit == 7 && toDoDayList.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            toDoDayList.clear();
                        } else {
                            repeatText = howRepeatEditText.getText().toString();
                            if (howRepeatUnit == 1) {
                                ssb = new SpannableStringBuilder(repeatText + "?????????");
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), 0, (repeatText + "????????????").length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                repeatTextView.setText(ssb);
                                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                            } else if (howRepeatUnit == 7) {
                                repeatTextView.setTextSize(14);
                                ssb = new SpannableStringBuilder(repeatText + "?????????\n" + repeatDow.replaceAll("??????", "?????? "));

                                // textLength ~ ?????? ????????? ?????? ???????????? ?????? ??????
                                textLength = (repeatText + "?????????\n").length();
                                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor)),
                                        textLength, (repeatText + "?????????\n" + repeatDow.replaceAll("??????", "?????? ")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), textLength, (repeatText + "?????????\n" + repeatDow.replaceAll("??????", "?????? ")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                repeatTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
                                repeatTextView.setText(ssb);
                            } else if (howRepeatUnit == 30) {
                                ssb = new SpannableStringBuilder(repeatText + "????????????");
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), 0, (repeatText + "????????????").length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                repeatTextView.setText(ssb);
                                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                            } else if (howRepeatUnit == 365) {
                                ssb = new SpannableStringBuilder(repeatText + "?????????");
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), 0, (repeatText + "????????????").length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                repeatTextView.setText(ssb);
                                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                            }

                            repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                            repeatImageView.setImageResource(R.drawable.enable_edit_repeat_icon);

                            howRepeatDialog.dismiss();
                        }
                    }
                });
                negBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toDoDayList.clear();
                        howRepeatDialog.cancel();
                    }
                });
            }
        });
        howRepeatDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case memoRequestCode:
                    Bundle bundle = data.getExtras();
                    memo = bundle.getString("memo");
                    memoTextView.setText(memo);
                    if (memo.equals("")) {
                        memoTextView.setText("?????????");
                    }
            }
        } else {
            Toast.makeText(this, getString(R.string.memoError), Toast.LENGTH_SHORT).show();
        }
    }

    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {


            if (menuItem.getItemId() == 1) {     // ?????? ??????
                toDoYear = dateFormat.format(new Date()).split("-")[0];
                toDoMonth = dateFormat.format(new Date()).split("-")[1];
                toDoDate = dateFormat.format(new Date()).split("-")[2];
                toDoDayTextView.setText("??????");
                return true;
            } else if (menuItem.getItemId() == 2) {       // ?????? ??????
                Calendar cal = Calendar.getInstance();
                cal.add(cal.DATE, 1);
                String dateStr = dateFormat.format(cal.getTime());
                toDoYear = dateStr.split("-")[0];
                toDoMonth = dateStr.split("-")[1];
                toDoDate = dateStr.split("-")[2];
                toDoDayTextView.setText("??????");
                return true;
            } else if (menuItem.getItemId() == 3) {       // ????????? ??? ??????
                Calendar cal = Calendar.getInstance();
                cal.add(cal.DATE, 7);
                String dateStr = dateFormat.format(cal.getTime());

                toDoYear = dateStr.split("-")[0];
                toDoMonth = dateStr.split("-")[1];
                toDoDate = dateStr.split("-")[2];
                toDoDayTextView.setText(toDoMonth + "??? " + toDoDate + "??? (" + nextWeekDayOfWeek.replace("??????", "") + ")");
                return true;
            } else if (menuItem.getItemId() == 4) {       // ???????????? ?????? ??????
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditActivty.this, dateSetListener,
                        Integer.parseInt(dateFormat.format(new Date()).split("-")[0]),
                        Integer.parseInt(dateFormat.format(new Date()).split("-")[1]) - 1,
                        Integer.parseInt(dateFormat.format(new Date()).split("-")[2]));
                datePickerDialog.show();

                return true;
            } else if (menuItem.getItemId() == R.id.alarm_midnight) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                String when = dateFormat.format(cal.getTime());
                alarmYear = when.split("-")[0];
                alarmMonth = when.split("-")[1];
                alarmDate = dayManager.getAddNumDay(Integer.parseInt(when.split("-")[2])) + Integer.parseInt(when.split("-")[2]);
                alarmHour = "00";
                alarmMin = "00";

                String alarmTimeText = "'??????'??? ??????\n";

                alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
                textLength = alarmTimeText.length();
                ssb = new SpannableStringBuilder(alarmTimeText + "??????");
                ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor)), textLength, (alarmTimeText + "??????").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new AbsoluteSizeSpan(13, true), textLength, (alarmTimeText + "??????").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alarmTextView.setText(ssb);
                alarmTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                alarmImageView.setImageResource(R.drawable.enable_edit_alarm_icon);

            } else if (menuItem.getItemId() == R.id.alarm_morning) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                String when = dateFormat.format(cal.getTime());
                alarmYear = when.split("-")[0];
                alarmMonth = when.split("-")[1];
                alarmDate = dayManager.getAddNumDay(Integer.parseInt(when.split("-")[2])) + Integer.parseInt(when.split("-")[2]);
                alarmHour = "08";
                alarmMin = "00";

                String alarmTimeText = "?????? 8??? 00?????? ??????\n";

                alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
                textLength = alarmTimeText.length();
                ssb = new SpannableStringBuilder(alarmTimeText + "??????");
                ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor)), textLength, (alarmTimeText + "??????").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new AbsoluteSizeSpan(13, true), textLength, (alarmTimeText + "??????").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alarmTextView.setText(ssb);
                alarmTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                alarmImageView.setImageResource(R.drawable.enable_edit_alarm_icon);

            } else if (menuItem.getItemId() == R.id.alarm_no) {
                alarmYear = "";
                alarmMonth = "";
                alarmDate = "";
                alarmHour = "";
                alarmMin = "";
                alarmTextView.setText("?????? ??????");
                alarmTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor));
                alarmImageView.setImageResource(R.drawable.disabled_edit_alarm_icon);

            } else if (menuItem.getItemId() == R.id.alarm_user_control) {         // ????????? ??????
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(slideDateTimeListener)
                        .setMinDate(new Date())
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .setInitialDate(new Date())
                        .build()
                        .show();

            } else if (menuItem.getItemId() == R.id.repeat_every_day) {          // howRepeat?????? ??????
                howRepeat = "EVERYDAY";
                repeatTextView.setText("?????? ??????");
                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                repeatImageView.setImageResource(R.drawable.enable_edit_repeat_icon);
            } else if (menuItem.getItemId() == R.id.repeat_every_week) {          // ?????? ????????? '?????? DOW' ?????? -> ???????????? ?????? : ?????? ?????? == ??????array??? ?????????
                howRepeat = "EVERYWEEK";
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String DOW = sdf.format(new Date());
                DOW = mainActivity.getTranslatedKoreanDow(DOW, 1);
                repeatDow = DOW;
                repeatTextView.setText("?????? ??????");
                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                repeatImageView.setImageResource(R.drawable.enable_edit_repeat_icon);
            } else if (menuItem.getItemId() == R.id.repeat_every_month) {     // ????????? ????????? '???' ??????
                howRepeat = "EVERYMONTH";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                repeatMonth = "0";
                repeatDay = simpleDateFormat.format(new Date());
                repeatTextView.setText("?????? ??????");
                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                repeatImageView.setImageResource(R.drawable.enable_edit_repeat_icon);
            } else if (menuItem.getItemId() == R.id.repeat_user_control) {
                howRepeat = "CALENDAR";
                howRepeatDialog.show();
            } else if (menuItem.getItemId() == R.id.no_repeat) {
                howRepeat = "NOREPEAT";
                repeatTextView.setText("??????");
                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor));
                repeatImageView.setImageResource(R.drawable.disabled_edit_repeat_icon);
            }
            return false;
        }
    };


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            toDoYear = Integer.toString(year);

            if (month + 1 >= 10) {
                toDoMonth = Integer.toString(month + 1);
            } else if (month + 1 < 10) {
                toDoMonth = "0" + Integer.toString(month + 1);
            }

            if (day >= 10) {
                toDoDate = Integer.toString(day);
            } else if (day < 10) {
                toDoDate = "0" + Integer.toString(day);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
            calendarDayOfWeek = simpleDateFormat.format(new Date(year, month, day - 1));
            toDoDayTextView.setText(toDoMonth + "??? " + toDoDate + "??? (" + mainActivity.getTranslatedKoreanDow(calendarDayOfWeek, 2) + ")");
        }
    };


    private SlideDateTimeListener slideDateTimeListener = new SlideDateTimeListener() {     // ????????????  alarmDayText <-null

        @Override
        public void onDateTimeSet(Date date) {
            SimpleDateFormat tempFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

            String thisDay = tempFormat.format(date);

            alarmYear = thisDay.split("-")[0];
            alarmMonth = thisDay.split("-")[1];
            alarmDate = thisDay.split("-")[2];
            alarmHour = thisDay.split("-")[3];
            alarmMin = thisDay.split("-")[4];
            todOrTmrCheck = false;


            alarmTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
            alarmImageView.setImageResource(R.drawable.enable_edit_alarm_icon);
            String todayYear = tempFormat.format(new Date()).split("-")[0];
            String alarmTimeText = null;
            String alarmDayText = null;
            String hourType = "??????"; //?????? or ??????
            String expressingHour = null;

            HourType ht = new HourType(Integer.parseInt(alarmHour), Integer.parseInt(alarmMin));
            hourType = ht.getHourType();
            expressingHour = ht.getTranslatedHour();

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date date2 = null;

            try {
                date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(alarmYear + "-" + alarmMonth + "-" + alarmDate + " " + alarmHour + ":" + alarmMin + ":" + "00");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (hourType.equals("??????")) {
                alarmTimeText = "'" + hourType + "'??? ??????\n";
            } else {
                alarmTimeText = hourType + " " + expressingHour + "??? " + alarmMin + "?????? ??????\n";
            }

            // ?????? ????????? '??????', '??????'??? ??????
            Calendar todCal = Calendar.getInstance();
            Calendar tmrCal = Calendar.getInstance();
            tmrCal.add(Calendar.DATE, 1);
            if ((new SimpleDateFormat("yyyyMMdd").format(todCal.getTime())).equals((alarmYear + alarmMonth + alarmDate))) {
                alarmDayText = "??????";
                todOrTmrCheck = true;

            } else if ((new SimpleDateFormat("yyyyMMdd").format(tmrCal.getTime())).equals((alarmYear + alarmMonth + alarmDate))) {
                alarmDayText = "??????";
                todOrTmrCheck = true;
            }

            if (todOrTmrCheck == false) {     // ?????? ????????? '??????', '??????'??? ?????? ??????
                if (alarmYear.equals(todayYear)) {     // ?????? ????????? "??? ???" ??? ??????
                    alarmDayText = alarmMonth + "??? " + alarmDate + "??? (" + mainActivity.getTranslatedKoreanDow(sdf.format(date2), 2) + ")";
                } else {      // ????????? ?????? ??????
                    alarmDayText = alarmYear + "??? " + alarmMonth + "??? " + alarmDate + "??? (" + mainActivity.getTranslatedKoreanDow(sdf.format(date2), 2) + ")";
                }
            }
            alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
            textLength = alarmTimeText.length();
            ssb = new SpannableStringBuilder(alarmTimeText + alarmDayText);
            ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.nightBackgroundColor)), textLength, (alarmTimeText + alarmDayText).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new AbsoluteSizeSpan(13, true), textLength, (alarmTimeText + alarmDayText).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            alarmTextView.setText(ssb);

            alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);

        }

        @Override
        public void onDateTimeCancel() {
            // on canceled
        }
    };

    private void shareContent(String toDoMessage, String toDoMemo){
        StringBuilder message = new StringBuilder();
        message.append("??? ?????? : " + toDoMessage +"\n");

        if(!toDoMemo.equals("") || toDoMemo != null){ /* ????????? ???????????? ?????? ?????? */
            message.append("??? ?????? : " + toDoMemo);
        }

        try{
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message.toString());
        intent.setPackage("com.kakao.talk");
        startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uriMarket = Uri.parse("market://details?id=com.kakao.talk");
            Intent intent = new Intent(Intent.ACTION_VIEW, uriMarket);
            startActivity(intent);
        }
    }

    private void addToDoDOWToArrayList() {
        toDoDayList.clear();
        if (monCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (tueCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (wedCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (thurCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (friCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (satCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (sunCB.isChecked()) {
            toDoDayList.add("?????????");
        }
    }

    private void loadCheckBox() {
        monCB = howRepeatView.findViewById(R.id.monday_check_box);
        tueCB = howRepeatView.findViewById(R.id.tuesday_check_box);
        wedCB = howRepeatView.findViewById(R.id.wednesday_check_box);
        thurCB = howRepeatView.findViewById(R.id.thursday_check_box);
        friCB = howRepeatView.findViewById(R.id.friday_check_box);
        satCB = howRepeatView.findViewById(R.id.saturday_check_box);
        sunCB = howRepeatView.findViewById(R.id.sunday_check_box);
    }

    @Override
    public void onBackPressed() {
        if(contentEditText.getText().toString().length() == 0){
            Toast.makeText(EditActivty.this, "????????? ???????????????.", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
        saveData();

        if(isContentChange && isFixChange == 1){
            updateFix(true);
        }
    }

    private void updatePinImage(){
        if(isFixed == 0){
            pinImageView.setColorFilter(getResources().getColor(R.color.unfixed_pin_color));
        }else if(isFixed == 1){
            pinImageView.setColorFilter(getResources().getColor(R.color.fixed_pin_color));
        }
    }

    private void saveData(){
        String content = contentEditText.getText().toString();
        if(!content.equals(toDoContent)){
            isContentChange = true;
        }
        toDoDay = toDoYear+"-"+toDoMonth+"-"+toDoDate;

        updateDB(content, isFavorite, toDoDay, repeatDow, howRepeat, isClear, alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, howRepeatUnit, memo, isFixed);
        if(!alarmYear.equals("")){
            Calendar alarmCalendar = Calendar.getInstance();
            int alarmId = Integer.parseInt(forSeparatingCreated.substring(7, forSeparatingCreated.length()-1));
            // ??? ??? ????????? ????????? ?????? ????????? ???
            // calendar??? 0 ~ 11
            alarmCalendar.set(Integer.parseInt(alarmYear),
                    Integer.parseInt(alarmMonth) - 1, Integer.parseInt(alarmDate),
                    Integer.parseInt(alarmHour), Integer.parseInt(alarmMin), 00);
            AlarmHATT alarmHATT = new AlarmHATT(getApplicationContext(), alarmId);
            alarmHATT.alarmManagerCancel(alarmId);
            alarmHATT.alarm(alarmCalendar);
        }
    }



    private void updateDB(String content, int isFavorite, String dayToDo, String repeatDOW,
                          String howRepeat, int isClear, String alarmYear, String alarmMonth, String alarmDate
            , String alarmHour, String alarmMin, String repeatText, int howRepeatUnit, String memo, int actionBarNoti) {

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("UPDATE todo_table SET content=?, dayToDo=?, repeatDOW=?, howRepeat=?, repeatMonth=?, repeatDate=?, isFavorite=?, isclear=?," +
                "alarmYear=?, alarmMonth=?, alarmDate=?, alarmHour=?, alarmMin=?, repeatText=?, howRepeatUnit=?, memo=?, actionBarNoti=?" +
                        "WHERE forSeparatingCreated=? AND content=?",
                new String[] {content, dayToDo, repeatDOW, howRepeat, repeatMonth, repeatDate, Integer.toString(isFavorite), Integer.toString(isClear),
        alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, Integer.toString(howRepeatUnit), memo, String.valueOf(actionBarNoti), forSeparatingCreated, toDoContent});

        db.close();

    }
}


