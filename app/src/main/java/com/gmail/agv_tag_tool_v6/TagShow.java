package com.gmail.agv_tag_tool_v6;

public class TagShow {
    private String tag_value;
    private String tag_type;
    private int type_symbol;
    private String type_symbol_text;
    private String date_time;

    //    public TagShow(String tag_value, String tag_type, int type_symbol) {
//        this.tag_value = tag_value;
//        this.tag_type = tag_type;
//        this.type_symbol = type_symbol;
//    }
    public TagShow(String tag_value, String tag_type, String type_symbol_text, String date_time) {
        this.tag_value = tag_value;
        this.tag_type = tag_type;
        this.type_symbol_text = type_symbol_text;
        this.date_time = date_time;
    }
    public String getTag_value() {
        return tag_value;
    }

    public void setTag_value(String tag_value) {
        this.tag_value = tag_value;
    }

    public String getTag_type() {
        return tag_type;
    }

    public void setTag_type(String tag_type) {
        this.tag_type = tag_type;
    }

    public int getType_symbol() {
        if (type_symbol_text.equals("history"))
            return R.mipmap.ic_launcher_foreground_history;
        else return R.mipmap.ic_launcher_foreground_save;
//        return type_symbol;
    }

    //    public void setType_symbol(int type_symbol) {
//        this.type_symbol = type_symbol;
//    }
    public void setType_symbol(String type_symbol_text) {
        this.type_symbol_text = type_symbol_text;
    }
    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}