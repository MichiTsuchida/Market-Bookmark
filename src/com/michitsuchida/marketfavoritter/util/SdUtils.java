
package com.michitsuchida.marketfavoritter.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.os.Environment;
import android.util.Log;

/**
 * SDカードの入出力を行うクラス。<br>
 * 保存先は"/sdcard/MarketBookmark/bookmarks.xml"。
 * 
 * @author MichiTsuchida
 */
public class SdUtils {

    /** LOG TAG */
    static final String LOG_TAG = "MarketBookmark";

    /** SDカードのパス */
    public static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();

    /** SDカードに作成するフォルダ名 */
    static final String FOLDER_PATH = SD_PATH + File.separator + "MarketBookmark";

    /** XMLのファイル名 */
    static final String FILE_PATH = FOLDER_PATH + File.separator + "bookmarks.xml";

    /**
     * SDカードがマウントされているかチェックする。
     * 
     * @return マウントされていればtrue、そうでなければfalse
     */
    public static boolean isSdCardMounted() {
        // SDカードの状態
        String status = Environment.getExternalStorageState();
        Log.d(LOG_TAG, "Sdcard status: " + status);
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * SDカードから読み出すメソッド。<br>
     * 読み出したXMLを改行等しないでそのまま返却する。
     * 
     * @return SDカードのXMLファイルから読み出した文字列、読み出せなかった場合はnull
     */
    public static String readFromSdCard() {
        File file = new File(FILE_PATH);
        StringBuilder fileString = new StringBuilder();
        try {
            if (file.exists()) {
                Log.d(LOG_TAG, "Read from sdcard");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    fileString.append(line);
                }
                br.close();
            } else {
                Log.e(LOG_TAG, "Could not find backup file!!");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return fileString.toString();
    }

    /**
     * SDカードにXMLとしてアプリの一覧を書き出すメソッド。
     * 
     * @param xml SDカードに書き出す文字列
     */
    public static boolean writeToSdCard(String xml) {
        boolean isWritten = false;
        File folder = new File(FOLDER_PATH);
        try {
            if (!folder.exists()) {
                // フォルダが存在しない場合新規作成
                folder.mkdirs();
                Log.d(LOG_TAG, "Backup folder doesn't exist, create it");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        File file = new File(FILE_PATH);
        BufferedWriter bw = null;
        try {
            // ファイルがある場合は上書きする
            boolean notOverwrite = true;
            if (file.exists()) {
                notOverwrite = false;
                Log.d(LOG_TAG, "Backup file already exists, overwtite it");
            }
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, notOverwrite), "UTF-8"));
            // Log.d(LOG_TAG, "Export to sdcard");
            bw.write(xml);
            bw.close();
            bw = null;
            isWritten = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // これだと"/mnt/sdcard/MarketBookmark/bookmarks.xml (Permission denied)"みたいなのが出る
            // Log.e(LOG_TAG, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isWritten;
    }

}
