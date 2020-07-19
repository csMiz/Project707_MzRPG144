package com.mizfrank.mzrpg144;

import java.util.ArrayList;
import java.util.List;

public class StringCutter {

    public static List<String> cut(String input, int maxWidth){
        List<String> result = new ArrayList<>();
        String[] crlf = input.split("##");
        for (String seg : crlf){
            String current = seg;
            while (current.length() > maxWidth){
                String tmpStr = current.substring(0,maxWidth);
                if (tmpStr.contains(" ")){
                    int lastSpace = tmpStr.lastIndexOf(" ");
                    result.add(current.substring(0, lastSpace));
                    current = current.substring(lastSpace + 1);
                }
                else{
                    result.add(current.substring(0, maxWidth));
                    current = current.substring(maxWidth);
                }
            }
            result.add(current);
        }
        return result;
    }

}
