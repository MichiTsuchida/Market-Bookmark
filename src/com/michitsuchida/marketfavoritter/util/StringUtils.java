
package com.michitsuchida.marketfavoritter.util;

import java.util.ArrayList;
import java.util.List;

/**
 * StringクラスのUtilityを定義しておくクラス。
 * 
 * @author MichiTsuchida
 */
public class StringUtils {

    /**
     * パラメータで受け取った文字列の全角/半角スペースをカンマに置換する。
     * 
     * @param text
     * @return カンマ区切りの文字列
     */
    public static String splitWithCommaAndSpace(String text) {
        // 全角/半角スペースをカンマに置換して区切る
        String[] str = text.replace(" ", ",").replace("　", ",").trim().split(",");
        List<String> list = new ArrayList<String>();
        for (String string : str) {
            if (string.length() != 0) {
                list.add(string);
            }
        }

        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                buff.append(list.get(i));
                buff.append(",");
            } else {
                // 最後だけはカンマ付けない
                buff.append(list.get(i));
            }
        }
        return buff.toString();
    }
}
