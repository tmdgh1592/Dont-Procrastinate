package com.app.buna.dontdelay.calendar.calc;

public class DayManager {

    public String getAddNumMonth(int month){
        String addNumMonth = "";
        addNumMonth = (month < 10) ? "0" : "";

        return addNumMonth;
    }

    public String getAddNumDay(int day){
        String addNumDay = ""; // default is zero
        addNumDay = (day < 10)? "0" : "";

        return addNumDay;
    }
}
