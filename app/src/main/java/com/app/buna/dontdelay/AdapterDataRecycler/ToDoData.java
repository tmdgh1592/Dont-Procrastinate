package com.app.buna.dontdelay.AdapterDataRecycler;

public class ToDoData { // "DTO"

    private String id;
    private String toDoContent; // 해야할 일 내용
    private String toDoDay; // format : year-month-date
    private int dDay; // d - day
    private String created;
    private String howRepeat;
    private String repeatMonth;
    private String repeatDate;
    private String repeatDow;   //화요일수요일목요일 split("요일")로 끊어주기
    private String repeatText;
    private String alarmYear;
    private String alarmMonth;
    private String alarmDate;
    private String alarmHour;
    private String alarmMin;
    private String memo;
    private int howRepeatUnit; // 1 7 30 365
    private int isFavroite;
    private int isClear;
    private int viewType;
    private int actionBarNoti;
    private String forSeparatingCreated;

    public int getActionBarNoti() {
        return actionBarNoti;
    }

    public void setActionBarNoti(int actionBarNoti) {
        this.actionBarNoti = actionBarNoti;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getForSeparatingCreated() {
        return forSeparatingCreated;
    }

    public void setForSeparatingCreated(String forSeparatingCreated) {
        this.forSeparatingCreated = forSeparatingCreated;
    }

    public int getHowRepeatUnit() {
        return howRepeatUnit;
    }

    public void setHowRepeatUnit(int howRepeatUnit) {
        this.howRepeatUnit = howRepeatUnit;
    }

    public String getMemo(){
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAlarmHour() {
        return alarmHour;
   }

    public void setAlarmHour(String alarmHour) {
      this.alarmHour = alarmHour;
    }

    public String getAlarmMin() {
        return alarmMin;
    }

    public void setAlarmMin(String alarmMin) {
        this.alarmMin = alarmMin;
    }

    public String getAlarmYear() {
        return alarmYear;
    }

    public void setAlarmYear(String alarmYear) {
        this.alarmYear = alarmYear;
    }

    public String getAlarmMonth() {
        return alarmMonth;
    }

    public void setAlarmMonth(String alarmMonth) {
        this.alarmMonth = alarmMonth;
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }

    public String getRepeatDow() {
        return repeatDow;
    }

    public void setRepeatDow(String repeatDow) {
        this.repeatDow = repeatDow;
    }

    public String getRepeatDate() {
        return repeatDate;
    }

    public void setRepeatDate(String repeatDate) {
        this.repeatDate = repeatDate;
    }

    public String getRepeatMonth() {
        return repeatMonth;
    }

    public void setRepeatMonth(String repeatMonth) {
        this.repeatMonth = repeatMonth;
    }

    public String getToDoContent() {
        return toDoContent;
    }

    public void setToDoContent(String toDoContent) {
        this.toDoContent = toDoContent;
    }

    public String getToDoDay() {
        return toDoDay;
    }

    public void setToDoDay(String toDoDay) {
        this.toDoDay = toDoDay;
    }

    public int getdDay() {
        return dDay;
    }

    public void setdDay(int dDay) {
        this.dDay = dDay;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getHowRepeat() {
        return howRepeat;
    }

    public void setHowRepeat(String howRepeat) {
        this.howRepeat = howRepeat;
    }

    public int getIsFavroite() {
        return isFavroite;
    }

    public void setIsFavroite(int isFavroite) {
        this.isFavroite = isFavroite;
    }

    public int getIsClear() {
        return isClear;
    }

    public void setIsClear(int isClear) {
        this.isClear = isClear;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getRepeatText() {
        return repeatText;
    }

    public void setRepeatText(String repeatText) {
        this.repeatText = repeatText;
    }
}
