package org.projetmanager.projetmanager.utils;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StrUtils {

    public static List<String> splitString(String cmd) {
        List<String> res=new ArrayList<>();
        var str=cmd;
        var end=false;
        while(!end&& StringUtils.hasLength(str)) {
            int pos = str.indexOf('\'');
            int pos2 = str.indexOf('"');
            if (pos != -1 || pos2 != -1) {
                int pos3;
                if (pos != -1 && pos2 == -1) {
                    pos3=pos;
                } else if (pos == -1 && pos2 != -1) {
                    pos3=pos2;
                } else {
                    pos3=Math.min(pos,pos2);
                }
                str= strWithQuote(str,pos3,res);
            } else {
                res.addAll(split(str));
                end=true;
            }
        }
        return res;
    }

    private static String strWithQuote(String str, int pos, List<String> res){
        var c=str.charAt(pos);
        Assert.isTrue(c=='\''||c=='"',()->"invalide char:"+c);
        var pos3 = str.indexOf(c, pos + 1);
        Assert.isTrue(pos3 != -1, "invalide string");
        String debut,suite,fin;
        debut = str.substring(0, pos);
        suite = str.substring(pos, pos3 + 1);
        if(str.length()>pos3+2) {
            fin = str.substring(pos3 + 2);
        } else {
            fin="";
        }
        if(StringUtils.hasText(debut)) {
            res.addAll(split(debut));
        }
        res.add(suite.substring(1, suite.length() - 1));
        return fin;
    }

    private static List<String> split(String s){
        if(StringUtils.hasText(s)) {
            return List.of(s.split("[ ]+"));
        } else {
            return List.of();
        }
    }
}
