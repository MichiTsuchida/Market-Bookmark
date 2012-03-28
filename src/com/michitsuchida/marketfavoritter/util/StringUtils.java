
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
     * @param text 文字列
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

    /** XMLで書き出すときのRoot要素の開始タグ */
    public static final String XML_ROOT_ELEMENT_START = "<marketbookmark>";

    /** XMLで書き出すときのRoot要素の終了タグ */
    public static final String XML_ROOT_ELEMENT_END = "</marketbookmark>";

    /**
     * パラメータで受け取ったAppElementの情報を、XML形式に変換する。
     * ルート要素の&lt;marketbookmark&gt;&lt;/marketbookmark&gt;は追加されないので気を付けること。
     * 
     * @param appName アプリケーション名
     * @param pkg パッケージ名
     * @param label ラベル
     * @return XMLフォーマットの文字列
     */
    public static String convertAppElementToXmlFormat(String appName, String pkg, String url,
            String label) {
        String element = "<app name=\"" + appName + "\" pkg=\"" + pkg + "\" url=\"" + url
                + "\"><label name=\"" + label + "\" /></app>";
        return element;
    }
}
