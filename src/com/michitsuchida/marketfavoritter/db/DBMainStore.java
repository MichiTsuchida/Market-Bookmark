
package com.michitsuchida.marketfavoritter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.michitsuchida.marketfavoritter.main.AppElement;

/**
 * データベースを開いたりレコードの取得、更新等を行うためのクラス。
 * 
 * @author MichiTsuchida
 */
public class DBMainStore {

    /** LOG TAG */
    static final String LOG_TAG = "DBMainStore";

    /** DBOpenHelperオブジェクト */
    private DBOpenHelper mHelper;

    /** SQLiteDatabaseオブジェクト */
    private SQLiteDatabase mDb;

    /** データベースのテーブル名 */
    static final String TBL_NAME = "app";

    /** テーブルのカラム:ID */
    public static final String COLUMN_ID = "_id";

    /** テーブルのカラム:アプリ名 */
    public static final String COLUMN_APP_NAME = "name";

    /** テーブルのカラム:パッケージ名 */
    public static final String COLUMN_APP_PACKAGE = "pkg";

    /** テーブルのカラム:URL */
    public static final String COLUMN_APP_URL = "url";

    /** テーブルのソートオーダー */
    public static final String ASC = "ASC";

    /** テーブルのソートオーダー */
    public static final String DESC = "DESC";

    /** データの件数のためのカウンタ */
    private int mCount = 0;

    /**
     * コンストラクタ。データベースのオブジェクトを取得する。
     * 
     * @param context コンテキスト
     * @param isUpdate レコードを更新するかどうか
     */
    public DBMainStore(Context context, boolean isUpdate) {
        mHelper = DBOpenHelper.getInstance(context);
        if (mHelper != null) {
            if (isUpdate) {
                mDb = mHelper.getWritableDatabase();
            } else {
                mDb = mHelper.getReadableDatabase();
            }
        } else {
            mDb = null;
        }
    }

    /**
     * データベースを閉じる。
     */
    public void close() {
        mDb.close();
    }

    /**
     * データベースにレコードを追加する。
     * 
     * @param name アプリ名
     * @param pkg パッケージ名
     * @param url URL
     */
    public void add(String name, String pkg, String url) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_NAME, name);
        values.put(COLUMN_APP_PACKAGE, pkg);
        values.put(COLUMN_APP_URL, url);
        mDb.insert(TBL_NAME, null, values);
    }

    /**
     * データベースのレコードを更新する。
     * 
     * @param _id レコードのID
     * @param name アプリ名
     * @param pkg パッケージ名
     * @param url URL
     */
    public void update(int _id, String name, String pkg, String url) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_NAME, name);
        values.put(COLUMN_APP_PACKAGE, pkg);
        values.put(COLUMN_APP_URL, url);
        mDb.update(TBL_NAME, values, COLUMN_ID + "=?", new String[] {
            Integer.toString(_id)
        });
    }

    /**
     * データベースからレコードを削除する。
     * 
     * @param レコードのIDを指定した配列
     */
    public void delete(String[] _ids) {
        for (int i = 0; i < _ids.length; i++) {
            mDb.delete(TBL_NAME, "_id=?", new String[] {
                _ids[i]
            });
        }
    }

    /**
     * すべてのレコードをデータベースから取得する。
     * 
     * @param order 並び順を指定しない場合は、nullをセットすること。
     *            並び順を指定する場合は、"pkg DESC"のように、"COLUMN_NAME ASC|DESC"と指定すること。
     * @return すべてのレコードが格納されたArrayList
     */
    public List<AppElement> fetchAllData(String order) {
        List<AppElement> data = new ArrayList<AppElement>();
        Cursor c = null;
        try {
            c = mDb.query(TBL_NAME, new String[] {
                    COLUMN_ID, COLUMN_APP_NAME, COLUMN_APP_PACKAGE, COLUMN_APP_URL
            }, null, null, null, null, order);
            c.moveToFirst();
            mCount = c.getCount();
            for (int i = 0; i < mCount; i++) {
                AppElement elem = new AppElement(c.getString(1), c.getString(2), c.getString(3),
                        c.getInt(0));
                data.add(elem);
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "SQLException occurred..");
        } finally {
            if (c != null)
                c.close();
        }
        return data;
    }

    /**
     * データベースに格納されているレコードの件数を取得する。
     * 
     * @return レコードの件数
     */
    public int getCount() {
        return mCount;
    }
}
