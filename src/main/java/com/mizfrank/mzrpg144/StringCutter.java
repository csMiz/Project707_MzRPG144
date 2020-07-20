package com.mizfrank.mzrpg144;

import java.util.ArrayList;
import java.util.List;

public class StringCutter {

    public static List<String> cut(String input, int maxWidth){
        int uniformMaxWidth = maxWidth;
        if (getCharCount(input) >= input.length()*1.25){
            uniformMaxWidth = (int)(maxWidth / 1.5);
        }
        List<String> result = new ArrayList<>();
        String[] crlf = input.split("##");
        for (String seg : crlf){
            String current = seg;
            while (current.length() > uniformMaxWidth){
                String tmpStr = current.substring(0,uniformMaxWidth);
                if (tmpStr.contains(" ")){
                    int lastSpace = tmpStr.lastIndexOf(" ");
                    result.add(current.substring(0, lastSpace));
                    current = current.substring(lastSpace + 1);
                }
                else{
                    result.add(current.substring(0, uniformMaxWidth));
                    current = current.substring(uniformMaxWidth);
                }
            }
            result.add(current);
        }
        return result;
    }

    public static int getCharCount(String s)
    {
        String tmpStr = s;
        tmpStr = tmpStr.replaceAll("[^\\x00-\\xff]", "**");
        int length = tmpStr.length();
        return length;
    }

}
