package com.app.buna.dontdelay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.buna.dontdelay.AdapterDataRecycler.ToDoData;
import com.app.buna.dontdelay.Calendar.CalendarAdapter;
import com.app.buna.dontdelay.Calendar.EventDecorator;
import com.app.buna.dontdelay.Calendar.OnDayDecorator;
import com.app.buna.dontdelay.Calendar.SaturdayDecorator;
import com.app.buna.dontdelay.Calendar.SundayDecorator;
import com.app.buna.dontdelay.CustomView.BackPressEditText;
import com.app.buna.dontdelay.DB.DBHelper;
import com.app.buna.dontdelay.CalcCalendar.CalendarDOW;
import com.app.buna.dontdelay.CalcCalendar.DayManager;
import com.app.buna.dontdelay.Etc.MyTextWatcher;
import com.app.buna.dontdelay.Push.AlarmHATT;
import com.app.buna.dontdelay.R;
import com.app.buna.dontdelay.Widget.MAppWidget;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

import static com.app.buna.dontdelay.AdapterDataRecycler.ViewTypeVO.VIEW_TYPE_ITEM;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    private PopupMenu popupMenu;

    String[] weekDay = { "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일" };

    private MaterialCalendarView materialCalendarView;
    String time,kcal,menu;
    private OnDayDecorator onDayDecorator;
    Cursor cursor;
    private ArrayList<ToDoData> toDoList = new ArrayList<>();
    DBHelper dbHelper;
    SQLiteDatabase db;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CalendarAdapter recyclerViewAdapter;
    CalendarDOW calendarDOW;
    String[] result;

    private ImageView backButton;
    private BackPressEditText enterToDoEditText;
    private Snackbar snackbar;

    private boolean isKeyBoardView = false;
    private AppBarLayout appBarLayout;

    private EditText howRepeatEditText;

    private String todayDayOfWeek, tomorrowDayOfWeek, nextWeekDayOfWeek, calendarDayOfWeek;     // 일정 deadline
    private String selectedYear = "", selectedMonth = "", selectedDay = "";                                    // 일정 일자
    private String howRepeat = "NOREPEAT", howRepeatDayString = "";
    private String alarmHour = "", alarmMin = "";                                                         // 알림 시간
    private String alarmYear ="", alarmMonth="", alarmDate="";
    private String repeatMonth = "", repeatDay = "", repeatDow = "", repeatText = "";                                      // 반복 월, 일, 요일
    private String memo = "";
    private String forSeparatingCreated = "";

    private CheckBox monCB, tueCB, wedCB, thurCB, friCB, satCB, sunCB;

    private boolean isFavorite = false;

    StringBuilder dowStringBuilder = new StringBuilder();

    private Spinner howRepeatSpinner;

    private ImageView favoriteImageView, writeOkImageView, backgroundImageView;
    private View howRepeatView;

    private boolean isDaySet = false;
    private boolean isRepeatSet = false;
    private boolean isAlarmSet = false;

    AlertDialog.Builder builder;
    AlertDialog howRepeatDialog;

    private int editPosition, howRepeatUnit = 1, howRepeatDayInt = 0;

    private TextView firstMenuTextView, secondMenuTextView, thirdMenuTextView;

    private String todayYear, todayMonth, todayDay, toDoContent = "";

    private LinearLayout todoLinearLayout, addLinearLayout, writeOkLayout
            ,firstMenuLayout, secondMenuLayout, thirdMenuLayout, topLayout, notDoneView;

    private FloatingActionButton fab;
    private InputMethodManager imm;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private ArrayList<String> toDoDayList = new ArrayList<>();
    private ArrayList<String> dateList;
    DayManager dayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        setting();

        dbHelper = new DBHelper(getApplicationContext());
        onDayDecorator = new OnDayDecorator(getResources());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = sdf.format(new Date());

        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(Integer.parseInt(dateFormat.split("-")[0]) - 2, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(new SundayDecorator(),
                new SaturdayDecorator(),
                onDayDecorator);

        dateList = new ArrayList<>();

        /*db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT dayToDo FROM todo_table", null);
        while(cursor.moveToNext()){
            dateList.add(cursor.getString(0));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                new ApiSimulator(dateList).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
        }).start();*/


// 달력 클릭했을 때 이벤트
// 해당 날짜의 일정 보여주기
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {


                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Year, Month-1, Day);

                String DOW = new CalendarDOW(calendar.get(Calendar.DAY_OF_WEEK)).getDow();


                db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM todo_table WHERE dayToDo=?", new String[]{Year+"-"+new DayManager().getAddNumMonth(Month)+Month+"-"+new DayManager().getAddNumDay(Day)+Day});

                toDoList.clear();
                while(cursor.moveToNext()){
                    ToDoData toDoData = new ToDoData();
                    toDoData.setToDoContent(cursor.getString(1));
                    toDoData.setToDoDay((cursor.getString(2))); //format : mYear-mMonth-mDay
                    toDoData.setRepeatDow(cursor.getString(3)); // format : x요일y요일z요일 or x요일y요일
                    toDoData.setCreated(cursor.getString(4));
                    toDoData.setHowRepeat(cursor.getString(5));
                    toDoData.setRepeatMonth(cursor.getString(6));
                    toDoData.setRepeatDate(cursor.getString(7));
                    toDoData.setIsFavroite(Integer.parseInt(cursor.getString(8)));
                    toDoData.setIsClear(Integer.parseInt(cursor.getString(9)));
                    toDoData.setAlarmYear(cursor.getString(10));
                    toDoData.setAlarmMonth(cursor.getString(11));
                    toDoData.setAlarmDate(cursor.getString(12));
                    toDoData.setAlarmHour(cursor.getString(13));
                    toDoData.setAlarmMin(cursor.getString(14));
                    toDoData.setRepeatText(cursor.getString(15));
                    toDoData.setHowRepeatUnit(Integer.parseInt(cursor.getString(16)));
                    toDoData.setMemo((cursor.getString(17)));
                    toDoData.setForSeparatingCreated((cursor.getString(18)));
                    toDoData.setViewType(VIEW_TYPE_ITEM);
                    toDoData.setdDay(calculateDDay(Integer.parseInt(cursor.getString(2).split("-")[0]), Integer.parseInt(cursor.getString(2).split("-")[1]), Integer.parseInt(cursor.getString(2).split("-")[2])));
                    toDoData.setActionBarNoti(Integer.parseInt(cursor.getString(20)));
                    toDoList.add(toDoData);
                    Log.i("Content test", toDoData.getToDoContent());
                }

                View dateLayout = getLayoutInflater().inflate(R.layout.calendar_date, null);

                TextView dateTextView = dateLayout.findViewById(R.id.calendar_date_date);
                TextView dateInfoTextView = dateLayout.findViewById(R.id.calendar_date_day);

                dateTextView.setText(Day + "일");
                dateInfoTextView.setText(DOW + " <" + Year+"년 " + Month + "월 " + Day+"일" + ">");


                DialogPlus dialogPlus = DialogPlus.newDialog(CalendarActivity.this)
                        .setContentHolder(new ViewHolder(dateLayout))
                        .setGravity(Gravity.CENTER)
                        .setCancelable(true)
                        .create();


                recyclerView = dateLayout.findViewById(R.id.calendar_recyclerview);
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerViewAdapter = new CalendarAdapter(toDoList, CalendarActivity.this, CalendarActivity.this, dialogPlus);
                recyclerView.setAdapter(recyclerViewAdapter);

                dialogPlus.show();

                materialCalendarView.clearSelection();

            }
        });
    }



    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        ArrayList<String> Time_Result;

        ApiSimulator(ArrayList<String> Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            Iterator<String> iterator = Time_Result.iterator();
            while(iterator.hasNext()){

                String[] time = iterator.next().split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);

            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.writeButtonColor), calendarDays,CalendarActivity.this));
        }
    }

    private void setting(){
        dayManager = new DayManager();

        loadAndSetListenerMenuLayout();
        backButton = findViewById(R.id.back_image_view);
        fab = findViewById(R.id.calendar_fab);

        addLinearLayout = findViewById(R.id.add_linear_layout);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        backButton.setOnClickListener(this);
        fab.setOnClickListener(this);

        todayYear = sdf.format(new Date()).split("-")[0];
        todayMonth = sdf.format(new Date()).split("-")[1];
        todayDay = sdf.format(new Date()).split("-")[2];

        firstMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);
        secondMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);
        thirdMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);

        favoriteImageView = findViewById(R.id.favorite_image_view);
        favoriteImageView.setOnTouchListener(this);
        writeOkImageView = findViewById(R.id.write_ok_image_view);
        writeOkLayout = findViewById(R.id.write_ok_layout);
        writeOkImageView.setOnClickListener(this);


        enterToDoEditText = findViewById(R.id.add_edit_text);
        enterToDoEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        enterToDoEditText.setOnBackPressListener(onBackPressListener);
        enterToDoEditText.setOnEditorActionListener(editorActionListener);
        MyTextWatcher editTextWatcher = new MyTextWatcher(enterToDoEditText);
        enterToDoEditText.addTextChangedListener(editTextWatcher);
        editTextWatcher.setResource(getResources(), writeOkImageView, writeOkLayout);



        firstMenuTextView = findViewById(R.id.first_menu_text_view);
        secondMenuTextView = findViewById(R.id.second_menu_text_view);
        thirdMenuTextView = findViewById(R.id.third_menu_text_view);

        howRepeatView = getLayoutInflater().inflate(R.layout.custom_how_repeat_dialog, null);

        howRepeatEditText = howRepeatView.findViewById(R.id.how_repeat_edit_text);
        howRepeatSpinner = howRepeatView.findViewById(R.id.how_repeat_spinner);
        howRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // 여기
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                LinearLayout layout = howRepeatView.findViewById(R.id.day_select_layout);
                switch (position) {
                    case 0:
                        howRepeatUnit = 1;
                        initCheckBox();
                        toDoDayList.clear();
                        layout.setVisibility(View.GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                    case 1:
                        howRepeatUnit = 7;
                        initCheckBox();
                        toDoDayList.clear();
                        layout.setVisibility(View.VISIBLE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_visible));
                        break;
                    case 2:
                        howRepeatUnit = 30;
                        initCheckBox();
                        toDoDayList.clear();
                        layout.setVisibility(View.GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                    case 3:
                        howRepeatUnit = 365;
                        initCheckBox();
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
        loadCheckBox();

        enterToDoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        if(enterToDoEditText.getText().length() == 0) {
                            return true;
                        }else if(enterToDoEditText.getText().length() > 0) {
                            hideAddLayout(); // 이 곳에 추가하는 메소드 삽입 ***
                        }
                }
                return true;
            }
        });

        builder = new AlertDialog.Builder(CalendarActivity.this);


        builder.setView(howRepeatView)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        initCheckBox();
                    }
                });

        howRepeatDialog = builder.create();
        howRepeatDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button posBtn = howRepeatDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negBtn = howRepeatDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negBtn.setTextColor(Color.GRAY);
                posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        howRepeatDayString = Integer.toString(howRepeatDayInt = (Integer.parseInt(howRepeatEditText.getText().toString()) * howRepeatUnit));
                        howRepeat = "CALENDAR";
                        thirdMenuTextView.setTextSize(12);
                        addToDoDOWToArrayList();
                        Iterator<String> iterator = toDoDayList.iterator();
                        while(iterator.hasNext()){
                            dowStringBuilder.append(iterator.next());      // 나중에 split("요일")로 끊어주기
                        }
                        repeatDow = dowStringBuilder.toString();
                        if(howRepeatEditText.getText().toString().equals("0")) {
                            Toast.makeText(CalendarActivity.this, "0보다 크게 입력해주세요.", Toast.LENGTH_SHORT).show();
                            toDoDayList.clear();
                        }else if(howRepeatUnit == 7 && toDoDayList.isEmpty()) {
                            Toast.makeText(CalendarActivity.this, "요일을 선택해주세요.", Toast.LENGTH_SHORT).show();
                            toDoDayList.clear();
                        }else {
                            setThirdTextView();
                            initCheckBox();
                            repeatText = howRepeatEditText.getText().toString();
                            thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                            howRepeatDialog.dismiss();
                            isRepeatSet = true;
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

    private void addToDoDOWToArrayList() {
        if(monCB.isChecked()) {
            toDoDayList.add("월요일");
        }if(tueCB.isChecked()) {
            toDoDayList.add("화요일");
        }if(wedCB.isChecked()) {
            toDoDayList.add("수요일");
        }if(thurCB.isChecked()) {
            toDoDayList.add("목요일");
        }if(friCB.isChecked()) {
            toDoDayList.add("금요일");
        }if(satCB.isChecked()) {
            toDoDayList.add("토요일");
        }if(sunCB.isChecked()) {
            toDoDayList.add("일요일");
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

    private void loadAndSetListenerMenuLayout() {
        firstMenuLayout = findViewById(R.id.first_menu_layout);
        secondMenuLayout = findViewById(R.id.second_menu_layout);
        thirdMenuLayout = findViewById(R.id.third_menu_layout);
        firstMenuLayout.setOnClickListener(this);
        secondMenuLayout.setOnClickListener(this);
        thirdMenuLayout.setOnClickListener(this);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {

        popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);

        switch (view.getId()){
            case R.id.back_image_view:
                onBackPressed();
                break;
            case R.id.calendar_fab:
                initAddView();
                enterToDoEditText.requestFocus();
                isKeyBoardView = true;
                initSettings();

                fab.setVisibility(View.GONE);
                fab.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_invisible));

                if(addLinearLayout.getVisibility() == View.GONE) {
                    addLinearLayout.setVisibility(View.VISIBLE);
                }

                Thread visibleThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        imm.showSoftInput(enterToDoEditText, 0);
                    }
                });
                visibleThread.start();
/*                    addViewAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.add_layout_visible);
                    addLinearLayout.startAnimation(addViewAnimation);*/

                selectedYear = todayYear;
                selectedMonth = todayMonth;
                selectedDay = todayDay;

                firstMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);
                secondMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);

                break;
            case R.id.first_menu_layout:
                getMenuInflater().inflate(R.menu.add_first_menu, popupMenu.getMenu());
                todayDayOfWeek = setDeadlineDOW("오늘");
                tomorrowDayOfWeek = setDeadlineDOW("내일");
                nextWeekDayOfWeek = setDeadlineDOW("다음주");
                popupMenu.getMenu().add(Menu.NONE, 1,0,"오늘 (" + todayDayOfWeek + ")");  //groupId, itemId, order, title
                popupMenu.getMenu().add(Menu.NONE, 2,1,"내일 (" + tomorrowDayOfWeek + ")");
                popupMenu.getMenu().add(Menu.NONE, 3,2,"다음 주 (" + nextWeekDayOfWeek + ")");
                popupMenu.getMenu().add(Menu.NONE, 4,3,"날짜 선택");
                popupMenu.show();
                break;
            case R.id.second_menu_layout:
                getMenuInflater().inflate(R.menu.add_second_menu, popupMenu.getMenu());
                popupMenu.show();
                break;
            case R.id.third_menu_layout:
                getMenuInflater().inflate(R.menu.add_third_menu, popupMenu.getMenu());
                toDoDayList.clear();
                popupMenu.show();
                break;
            case R.id.write_ok_image_view:
                if(enterToDoEditText.getText().length() != 0) { // 할 일 입력칸이 비어있지 않은 경우
                        /*if(isAlarmSet == false || isDaySet == false || isRepeatSet == false) {  // 알람, 일자, 반복 모두 설정했을 때
                            Toast.makeText(mContext, getString(R.string.alertAboutUnSet), Toast.LENGTH_SHORT).show();
                            return;
                        }else{*/  // 알람, 일정날짜, 반복설정이 모두 돼 있는 경우
                    toDoContent = enterToDoEditText.getText().toString();
                    forSeparatingCreated = Long.toString(System.currentTimeMillis());

                    dbInsert(toDoContent, isFavorite, selectedYear + "-" + selectedMonth + "-" + selectedDay,
                            dowStringBuilder.toString(),
                            sdf.format(new Date()), howRepeat, false,
                            alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, Integer.toString(howRepeatUnit), memo, forSeparatingCreated,
                            calculateDDay(Integer.parseInt(selectedYear), Integer.parseInt(selectedMonth), Integer.parseInt(selectedDay)), 0);

                    if(!alarmYear.equals("")){
                        Calendar alarmCalendar = Calendar.getInstance();
                        int alarmId = Integer.parseInt(forSeparatingCreated.substring(7, forSeparatingCreated.length()-1));
                        // 한 달 빼줘야 정확한 알람 시간이 됌
                        // calendar는 0 ~ 11

                        alarmCalendar.set(Integer.parseInt(alarmYear),
                                Integer.parseInt(alarmMonth) - 1, Integer.parseInt(alarmDate),
                                Integer.parseInt(alarmHour), Integer.parseInt(alarmMin), 00);
                        new AlarmHATT(getApplicationContext(), alarmId).alarm(alarmCalendar);
                    }

                    /* 초기화 */
                    updateWidget();
                    initSettings();
                    hideAddLayout();
                    onResume();
                    //}
                }
                break;
        }

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.favorite_image_view:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isFavorite) {       // 즐겨찾기 설정 안돼있는 경우
                        setClickedStar();
                    } else if (isFavorite) {     // 즐겨찾기 설정 돼있는 경우
                        setUnclikedStar();
                    }
                }
                break;
            case R.id.first_menu_layout:
                firstMenuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menus_layout_click));
                break;
            case R.id.second_menu_layout:
                secondMenuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menus_layout_click));
                break;
            case R.id.third_menu_layout:
                thirdMenuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menus_layout_click));
                break;
        }
        return false;
    };

    private void initAddView() {
        textViewInit();
        favoriteImageView.setImageResource(R.drawable.empty_favorite_icon);
        isFavorite = false;

        firstMenuTextView.setText("기한 설정");
        secondMenuTextView.setText("알림 설정");
        thirdMenuTextView.setText("반복 설정");

        firstMenuTextView.setTextSize(13);
        secondMenuTextView.setTextSize(13);
        thirdMenuTextView.setTextSize(13);

        howRepeatSpinner.setSelection(0);
        howRepeatUnit = 1;
        howRepeatDayInt = 0;
        selectedYear = ""; selectedMonth = ""; selectedDay = "";
        alarmHour = ""; alarmMin = "";
        howRepeat = ""; howRepeatDayString = "";
    }


    private void initSettings() {
        textViewInit();
        initStrings();
        initCheckBox();
        setUnclikedStar();
        initBoolean();
        initAlarmTime();
    }

    private void initAlarmTime() {
        alarmYear = "";
        alarmMonth = "";
        alarmDate = "";
        alarmHour = "";
        alarmMin = "";
        secondMenuTextView.setTextSize(13);
    }

    private void initCheckBox() {
        monCB.setChecked(false);
        tueCB.setChecked(false);
        wedCB.setChecked(false);
        thurCB.setChecked(false);
        friCB.setChecked(false);
        satCB.setChecked(false);
        sunCB.setChecked(false);
        repeatDow = "";
        toDoDayList.clear();
    }


    private void initStrings() {
        selectedYear = ""; selectedMonth = ""; selectedDay = "";                                    // 일정 일자
        howRepeat = "NOREPEAT"; howRepeatDayString = "";
        alarmHour = ""; alarmMin = "";
        repeatMonth = ""; repeatDay = ""; repeatDow = "";
        repeatText = "";
        isAlarmSet = false;
        isDaySet = false;
        isRepeatSet = false;
        dowStringBuilder.setLength(0);
        forSeparatingCreated = "";
        toDoContent = "";
    }

    private void textViewInit() {
        enterToDoEditText.setText("");
    }

    private void initBoolean() {
        isDaySet = false;
        isAlarmSet = false;
        isRepeatSet = false;
    }

    private void setClickedStar() {
        favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_check));
        favoriteImageView.setImageResource(R.drawable.favorite_icon);
        isFavorite = true;
    }

    private void setUnclikedStar() {
        favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_uncheck));
        favoriteImageView.setImageResource(R.drawable.empty_favorite_icon);
        isFavorite = false;
    }

    @SuppressLint("RestrictedApi")
    public void hideAddLayout() {

        imm.hideSoftInputFromWindow(enterToDoEditText.getWindowToken(), 0);

        Thread hideThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //addLinearLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.add_layout_invisible));
                fab.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_visible));
                addLinearLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.add_layout_invisible));
            }
        });
        hideThread.start();
        addLinearLayout.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private BackPressEditText.OnBackPressListener onBackPressListener = new BackPressEditText.OnBackPressListener() {

        @Override
        public void onBackPress() {
            initSettings();
            hideAddLayout();
            initCheckBox();
        }
    };

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            switch (actionId){
                case EditorInfo.IME_ACTION_DONE:
                    if(enterToDoEditText.getText().length() == 0) {
                        return true;
                    } else if(enterToDoEditText.getText().length() > 0) { // 할 일 입력칸이 비어있지 않은 경우
                        /*if(isAlarmSet == false || isDaySet == false || isRepeatSet == false) {  // 알람, 일자, 반복 모두 설정했을 때
                            Toast.makeText(mContext, getString(R.string.alertAboutUnSet), Toast.LENGTH_SHORT).show();
                            return;
                        }else{*/  // 알람, 일정날짜, 반복설정이 모두 돼 있는 경우
                        toDoContent = enterToDoEditText.getText().toString();
                        forSeparatingCreated = Long.toString(System.currentTimeMillis());

                        dbInsert(toDoContent, isFavorite, selectedYear + "-" + selectedMonth + "-" + selectedDay,
                                dowStringBuilder.toString(),
                                sdf.format(new Date()), howRepeat, false,
                                alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, Integer.toString(howRepeatUnit), memo, forSeparatingCreated,
                                calculateDDay(Integer.parseInt(selectedYear), Integer.parseInt(selectedMonth), Integer.parseInt(selectedDay)), 0);

                        if(!alarmYear.equals("")){
                            Calendar alarmCalendar = Calendar.getInstance();
                            int alarmId = Integer.parseInt(forSeparatingCreated.substring(7, forSeparatingCreated.length()-1));
                            // 한 달 빼줘야 정확한 알람 시간이 됌
                            // calendar는 0 ~ 11

                            alarmCalendar.set(Integer.parseInt(alarmYear),
                                    Integer.parseInt(alarmMonth) - 1, Integer.parseInt(alarmDate),
                                    Integer.parseInt(alarmHour), Integer.parseInt(alarmMin), 00);
                            new AlarmHATT(getApplicationContext(), alarmId).alarm(alarmCalendar);
                        }

                        /* 초기화 */
                        updateWidget();
                        initSettings();
                        hideAddLayout();
                        onResume();
                        //}
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    @Override
    protected void onResume() {
        dateList.clear();
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT dayToDo FROM todo_table", null);
        while(cursor.moveToNext()){
            dateList.add(cursor.getString(0));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                new ApiSimulator(dateList).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
        }).start();
        db.close();

        updateWidget();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateWidget();
        Log.d("MainActivity", "onPause: updateWidget()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateWidget();
        Log.d("MainActivity", "onStop: updateWidget()");
    }

    protected void updateWidget() {
        Intent updateIntent = new Intent(getApplicationContext(), MAppWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        CalendarActivity.this.sendBroadcast(updateIntent);
    }


    private void dbInsert(String toDoContent, boolean isFavorite, String dayToDo, String repeatDOW,
                          String created, String howRepeat, boolean isClear, String alarmYear, String alarmMonth, String alarmDate
            , String alarmHour, String alarmMin, String repeatText, String howRepeatUnit, String memo, String forSeparatingCreated, int dDay, int actionBarNoti) {

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String favoriteChk;
        String clearChk;


        if(isFavorite == true) {
            favoriteChk = "1";
        }else{
            favoriteChk = "0";
        }

        if(isClear == true) {
            clearChk = "1";
        }else {
            clearChk = "0";
        }


        db.execSQL("INSERT INTO todo_table (content, dayToDo, repeatDOW, created, howRepeat, repeatMonth, repeatDate, isFavorite, isclear," +
                        " alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, howRepeatUnit, memo, forSeparatingCreated, dDay, actionBarNoti) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new String[] {toDoContent, dayToDo, repeatDOW, created, howRepeat, repeatMonth, repeatDay, favoriteChk, clearChk,
                        alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, howRepeatUnit, memo, forSeparatingCreated, Integer.toString(dDay), Integer.toString(actionBarNoti)});

        db.close();

    }

    public int calculateDDay(int _year, int _month, int _day) {

        Calendar today = Calendar.getInstance(); // 오늘 날짜
        Calendar dDay = Calendar.getInstance(); // d - Day 날짜

        dDay.set(_year, _month - 1, _day); // d-day 날짜 셋팅

        long _dDay = dDay.getTimeInMillis() / 86400000; // 8640000 = 24 * 60 * 60 * 1000 milliseconds
        long _today = today.getTimeInMillis() / 86400000;
        int count = (int) (_dDay - _today); // d - Day

        return count;
    }


    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if(menuItem.getItemId() == 1) {     // 오늘 선택
                selectedYear = todayYear;
                selectedMonth = todayMonth;
                selectedDay = todayDay;
                firstMenuTextView.setText("오늘까지");
                firstMenuTextView.setTextSize(12);
                firstMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isDaySet = true;
                return true;
            }else if(menuItem.getItemId() == 2) {       // 내일 선택
                Calendar cal = Calendar.getInstance();
                cal.add(cal.DATE, 1);
                String dateStr = dateFormat.format(cal.getTime());
                selectedYear = dateStr.split("-")[0];
                selectedMonth = dateStr.split("-")[1];
                selectedDay = dateStr.split("-")[2];
                firstMenuTextView.setText("내일까지");
                firstMenuTextView.setTextSize(12);
                firstMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isDaySet = true;
                return true;
            }else if(menuItem.getItemId() == 3) {       // 일주일 후 선택
                Calendar cal = Calendar.getInstance();
                cal.add(cal.DATE, 7);
                String dateStr = dateFormat.format(cal.getTime());

                selectedYear = dateStr.split("-")[0];
                selectedMonth = dateStr.split("-")[1];
                selectedDay = dateStr.split("-")[2];

                firstMenuTextView.setText(selectedMonth + "월 " + selectedDay + "일 " + nextWeekDayOfWeek + "까지");
                firstMenuTextView.setTextSize(12);
                firstMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);

                isDaySet = true;
                return true;
            }else if(menuItem.getItemId() == 4) {       // 캘린더로 지정 선택
                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this, dateSetListener,
                        Integer.parseInt(todayYear), Integer.parseInt(todayMonth) - 1, Integer.parseInt(todayDay));
                datePickerDialog.show();
                firstMenuTextView.setTextSize(12);

                return true;
            }else if(menuItem.getItemId() == R.id.alarm_midnight) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                String when = dateFormat.format(cal.getTime());
                alarmYear = when.split("-")[0];
                alarmMonth = when.split("-")[1];
                alarmDate = dayManager.getAddNumDay(Integer.parseInt(when.split("-")[2])) + Integer.parseInt(when.split("-")[2]);
                alarmHour = "00";
                alarmMin = "00";
                alarmTimeSet("24", alarmMin);
                secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                secondMenuTextView.setTextSize(12);
                isAlarmSet = true;
                Toast.makeText(CalendarActivity.this, "다음 날 \'자정\' 푸시알림", Toast.LENGTH_SHORT).show();
            }else if(menuItem.getItemId() == R.id.alarm_morning) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                String when = dateFormat.format(cal.getTime());
                alarmYear = when.split("-")[0];
                alarmMonth = when.split("-")[1];
                alarmDate = dayManager.getAddNumDay(Integer.parseInt(when.split("-")[2])) + Integer.parseInt(when.split("-")[2]);
                alarmHour = "08";
                alarmMin = "00";
                alarmTimeSet(alarmHour, alarmMin);
                secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                secondMenuTextView.setTextSize(12);
                isAlarmSet = true;
                Toast.makeText(CalendarActivity.this, "다음 날 " + alarmHour + "시 " + alarmMin + "분 푸시알림", Toast.LENGTH_SHORT).show();
            }else if(menuItem.getItemId() == R.id.alarm_no){
                alarmHour = "";
                alarmMin = "";
                secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                secondMenuTextView.setText("알림 안함");
                secondMenuTextView.setTextSize(12);
                isAlarmSet = true;
            }else if(menuItem.getItemId() == R.id.alarm_user_control) {
                /*TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, timeSetListener, 12, 00, false);
                timePickerDialog.show();*/
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(slideDateTimeListener)
                        .setInitialDate(new Date())
                        .setMinDate(new Date())
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .build()
                        .show();
            } else if(menuItem.getItemId() == R.id.repeat_every_day) {          // howRepeat으로 구분
                howRepeat = "EVERYDAY";
                thirdMenuTextView.setText("매일 반복");
                thirdMenuTextView.setTextSize(12);
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isRepeatSet = true;
            }else if(menuItem.getItemId() == R.id.repeat_every_week) {          // 매주 반복은 '요일 DOW' 저장 -> 불러올때 로직 : 오늘 요일 == 요일array에 있는지
                howRepeat = "EVERYWEEK";
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String DOW = sdf.format(new Date());
                DOW = MainActivity.getTranslatedKoreanDow(DOW, 1);
                repeatDow = DOW;
                thirdMenuTextView.setText(repeatDow + " 매주 반복");
                thirdMenuTextView.setTextSize(12);
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isRepeatSet = true;
            }else if(menuItem.getItemId() == R.id.repeat_every_month) {     // 한달에 한번은 '일' 저장
                howRepeat = "EVERYMONTH";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                repeatMonth = "0"; repeatDay = simpleDateFormat.format(new Date());
                thirdMenuTextView.setText("매달 반복");
                thirdMenuTextView.setTextSize(12);
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isRepeatSet = true;
            }else if(menuItem.getItemId() == R.id.repeat_user_control) {
                howRepeat = "CALENDAR";
                howRepeatDialog.show();
            }else if(menuItem.getItemId() == R.id.no_repeat) {
                howRepeat = "NOREPEAT";
                thirdMenuTextView.setText("반복 안함");
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isRepeatSet = true;
            }
            return false;
        }
    };


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            selectedYear = Integer.toString(year);

            if(month+1 >= 10){
                selectedMonth = Integer.toString(month+1);
            }else if(month+1 < 10){
                selectedMonth = "0" + Integer.toString(month+1);
            }

            if(day >= 10){
                selectedDay = Integer.toString(day);
            }else if(day < 10){
                selectedDay = "0" + Integer.toString(day);
            }



            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
            calendarDayOfWeek = simpleDateFormat.format(new Date(year, month, day - 1));
            firstMenuTextView.setText(selectedMonth + "월 " + selectedDay + "일 " + MainActivity.getTranslatedKoreanDow(calendarDayOfWeek, 1) + "까지");
            firstMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
            isDaySet = true;
        }
    };


    private SlideDateTimeListener slideDateTimeListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            SimpleDateFormat tempFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            SimpleDateFormat tempFormat2 = new SimpleDateFormat("EEEE");

            Calendar curCalendar = Calendar.getInstance();

            String thisDay = tempFormat.format(date);

            alarmYear = thisDay.split("-")[0];
            alarmMonth = thisDay.split("-")[1];
            alarmDate = thisDay.split("-")[2];
            alarmHour = thisDay.split("-")[3];
            alarmMin = thisDay.split("-")[4];
            Log.d("MainActivity", "onDateTimeSet: " + alarmYear+alarmMonth+alarmDate+alarmHour+alarmMin);

            secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
            if(Integer.parseInt(alarmYear) == curCalendar.get(Calendar.YEAR)){
                secondMenuTextView.setTextSize(11);
                secondMenuTextView.setText(alarmMonth+"월 " + alarmDate +"일 (" + MainActivity.getTranslatedKoreanDow(tempFormat2.format(date), 2) + ")" + "\n" + alarmHour+"시 " + alarmMin + "분 푸시알림");
            }else{
                secondMenuTextView.setTextSize(11);
                secondMenuTextView.setText(alarmYear +"년 " + alarmMonth+"월 " + alarmDate +"일" +"\n" + alarmHour+"시 " + alarmMin + "분 푸시알림");
            }
            secondMenuTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.0f, getResources().getDisplayMetrics()), 1.0f);
            isAlarmSet = true;
        }

        @Override
        public void onDateTimeCancel()
        {
            // on canceled
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

            alarmHour = Integer.toString(hourOfDay);
            alarmMin = Integer.toString(minute);

            if(hourOfDay < 10) {
                alarmHour = "0" + alarmHour;
            }
            if(minute < 10) {
                alarmMin = "0" + alarmMin;
            }
            secondMenuTextView.setText(alarmHour + "시 " + alarmMin + "분에 푸시알림");
            secondMenuTextView.setTextSize(12);
            secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
            isAlarmSet = true;
        }
    };

    private void alarmTimeSet(String hour, String minute) {
        if(hour.equals("24") && minute.equals("00")){
            secondMenuTextView.setText("금일 자정 알림");
        }
        secondMenuTextView.setText(hour + "시 " + minute + "분 푸시알림");
        secondMenuTextView.setTextSize(12);
    }

    private void setThirdTextView() {
        if(howRepeatUnit == 1) {
            thirdMenuTextView.setText(Integer.parseInt(howRepeatEditText.getText().toString()) + "일마다");
        }else if(howRepeatUnit == 7) {
            if(toDoDayList.isEmpty() || howRepeatEditText.getText().toString().equals("0")) {
                toDoDayList.clear();
                thirdMenuTextView.setText("반복 안함");
            }else{
                StringBuilder textContent = new StringBuilder(Integer.parseInt(howRepeatEditText.getText().toString()) +"주마다 ");
                Iterator<String> iterator = toDoDayList.iterator();

                while(iterator.hasNext()) {
                    textContent.append(iterator.next() + ", ");
                }
                textContent.delete(textContent.length()-2, textContent.length()-1);
                thirdMenuTextView.setText(textContent);
            }
        }else if(howRepeatUnit == 30) {
            thirdMenuTextView.setText(Integer.parseInt(howRepeatEditText.getText().toString()) + "개월마다");
        }else if(howRepeatUnit == 365) {
            thirdMenuTextView.setText(Integer.parseInt(howRepeatEditText.getText().toString()) + "년마다");
        }

    }

    public String setDeadlineDOW(String when) {
        String dow = "";
        Calendar cal = Calendar.getInstance();
        int dayIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;       // cal.get = 일요일 : 1  ~   토요일 : 7

        // dayIndex = 일요일 : 0 ~   토요일 : 6
        // weekDay = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"}
        /*               0         1        2        3         4        5        6         */

        if(when.equals("오늘")) {
            dow = weekDay[dayIndex];
        } else if(when.equals("내일")) {
            dow = weekDay[(dayIndex+1) % 7];
        } else if(when.equals("다음주")) {
            dow = weekDay[dayIndex];
        }
        return dow;
    }


}
