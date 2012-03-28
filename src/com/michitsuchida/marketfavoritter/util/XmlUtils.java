
package com.michitsuchida.marketfavoritter.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.michitsuchida.marketfavoritter.main.AppElement;

/**
 * XMLのUtilityを定義しておくクラス。
 * 
 * @author MichiTsuchida
 */
public class XmlUtils {

    /** LOG TAG */
    static final String LOG_TAG = "MarketBookmark";

    /**
     * XMLをParseして、AppElement型のオブジェクトに格納する。
     * 
     * @param xml XMLテキスト
     * @return アプリ情報を格納したリスト、例外発生時はnull
     */
    public static List<AppElement> analyzeXml(String xml) {
        // AppElementの情報を格納するList
        List<AppElement> elements = new ArrayList<AppElement>();

        try {
            // XMLPullParserのインスタンスにXMLを格納する
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(new StringReader(xml));

            // XmlPullParserのイベントタイプ
            int evtType;

            // アプリ1つ分の情報
            AppElement app = new AppElement();
            String appName = "", pkgName = "", url = "", label = "";
            // フラグ
            boolean hasApp = false, hasLabel = false;

            // XMLの最後までタグ単位でループ
            while ((evtType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
                String xmlName = xmlPullParser.getName();
                if (evtType == XmlPullParser.START_TAG && xmlName.equals("app")) {
                    // <app>タグの中身を取得する
                    appName = xmlPullParser.getAttributeValue(null, "name");
                    pkgName = xmlPullParser.getAttributeValue(null, "pkg");
                    url = xmlPullParser.getAttributeValue(null, "url");
                    // フラグをセット
                    hasApp = true;
                } else if (evtType == XmlPullParser.START_TAG && xmlName.equals("label")) {
                    // <label>タグの中身を取得する
                    label = xmlPullParser.getAttributeValue(null, "name");
                    // フラグをセット
                    hasLabel = true;
                }
                if (hasApp && hasLabel) {
                    // <app>タグ、<label>タグのどちらも取得したら、ArrayListに追加
                    app.setAppName(appName);
                    app.setPkgName(pkgName);
                    app.setMarketUrl(url);
                    app.setLabel(label);
                    elements.add(app);
                    // Log.d(LOG_TAG, "App element: " + app.toString());

                    // フラグと変数をリセット
                    hasApp = false;
                    hasLabel = false;
                    app = new AppElement();
                    appName = "";
                    pkgName = "";
                    label = "";
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            elements = null;
        } catch (IOException e) {
            e.printStackTrace();
            elements = null;
        }
        return elements;
    }
}
