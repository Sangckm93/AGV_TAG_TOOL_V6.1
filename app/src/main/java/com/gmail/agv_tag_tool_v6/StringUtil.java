package com.gmail.agv_tag_tool_v6;

class StringUtil {
    public static String getHexString(byte[] b) {
        String result = "";
        for (int i=1; i < b.length; i++) {  // remove first char of string by start counter i from 1
            result += String.format("%c", b[i] & 0xff);
        }
        return result;
    }
}