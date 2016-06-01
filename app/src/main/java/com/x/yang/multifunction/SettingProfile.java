package com.x.yang.multifunction;

/**
 * Created by yang on 2016/4/28.
 */
public class SettingProfile {
    private int maxNumberRecords = 1;
    private int duration = 60000;
    private String unit = "cm";
    private int G_level = 25;

    private static SettingProfile sp;

    public static SettingProfile getSp(){
        if(sp == null){
            sp = new SettingProfile();
            return sp;
        }
        return sp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getG_level() {
        return G_level;
    }

    public void setG_level(int g_level) {
        switch (g_level){
            case 0:
                G_level = 200;
                break;
            case 1:
                G_level = 30;
                break;
            case 2:
                G_level = 25;
                break;
            case 3:
                G_level = 20;
                break;
        }

    }

    public int getMaxNumberRecords() {
        return maxNumberRecords;
    }

    public void setMaxNumberRecords(int maxNumberRecords) {
        this.maxNumberRecords = maxNumberRecords;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
