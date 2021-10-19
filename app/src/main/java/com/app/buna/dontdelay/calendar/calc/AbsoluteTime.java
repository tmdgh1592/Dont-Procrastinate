package com.app.buna.dontdelay.calendar.calc;

public class AbsoluteTime {

    private int hour;
    private int am_pm;

    private static int ERROR = -1;

    public AbsoluteTime(int hour, int am_pm){
        this.hour = hour;
        this.am_pm = am_pm;
    }

    public int getAbsTime(){
        if(am_pm == 0){
            return this.hour;
        }else if(am_pm == 1 && hour != 12){
            return this.hour + 12;
        }else if(am_pm == 1 && hour == 12){
            return 12;
        }
        return AbsoluteTime.ERROR;
    }


}
