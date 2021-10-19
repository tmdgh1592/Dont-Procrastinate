package com.app.buna.dontdelay.calendar.calc;

public class HourType {

    int hour, min;
    String hourType, translatedHour;

    public HourType(int hour, int min){
        this.hour = hour;
        this.min = min;
        translate();
    }

    public void translate(){
        if(hour == 24 && min == 0){
            hourType = "자정";
            translatedHour = "12";
        }else if (hour == 0){
            hourType = "오전";
            translatedHour = "12";
        }
        else if (hour < 12) {
            hourType = "오전";
            translatedHour = Integer.toString(hour);
        }else {
            hourType = "오후";
            if (hour == 12) {
                translatedHour = Integer.toString(hour);
            } else {
                translatedHour = Integer.toString(hour - 12);
            }
        }
    }

    public String getHourType(){
        return hourType;
    }

    public String getTranslatedHour(){
        return translatedHour;
    }
}
