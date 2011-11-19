
package com.michitsuchida.marketfavoritter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteでDBをオープンするためのヘルパークラス。
 * 
 * @author MichiTsuchida
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    /** LOG TAG */
    static final String LOG_TAG = "DBOpenHelper";

    /** データベースの名前 */
    private static final String DB_NAME = "app_lists.db";

    /** データベースのバージョン */
    private static final int DB_VERSION = 1;

    /** トランザクション管理のためのカウンタ??ｗ */
    private int mWritableDatabaseCount = 0;

    private static DBOpenHelper mInstance = null;

    synchronized static public DBOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBOpenHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * コンストラクタ
     * 
     * @param context
     */
    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * データベースをRead onlyで取得する。
     * 
     * @return Read onlyのデータベース
     */
    synchronized public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        return db;
    }

    /**
     * データベースをRead/Writeで取得する。
     * 
     * @return Read/Writeのデータベース
     */
    @Override
    synchronized public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        if (db != null) {
            ++mWritableDatabaseCount;
        }
        return db;
    }

    /**
     * Writableなデータベースを閉じる。
     * 
     * @param database Writableなデータベース
     */
    synchronized public void closeWritableDatabase(SQLiteDatabase database) {
        if (mWritableDatabaseCount > 0 && database != null) {
            --mWritableDatabaseCount;
            if (mWritableDatabaseCount == 0) {
                database.close();
            }
        }
    }

    /**
     * SQLiteOpenHelperクラスは、オブジェクトが生成されると、データベースが存在するか確認し、<br>
     * 存在しない場合は、このメソッドが呼び出される。<br>
     * このタイミングで、テーブルを作成するクエリーを実行する。
     * 
     * @param db データベース
     * @see SQLiteOpenHelper
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Create db, start transaction");

        // トランザクションを使用したDBデータの作成
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DBMainStore.TBL_NAME
                    + "( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + "name TEXT, "
                    + "pkg TEXT, " + "url TEXT );");

            // こんな感じでDB作成の段階でinsertすることもできる
            // SQLiteStatement stmt;
            // stmt = db.compileStatement("INSERT INTO " + DBMainStore.TBL_NAME
            // +
            // " VALUES (?, ?, ?);");
            // for (String[] title : TITLE_LIST) {
            // stmt.bindString(2, title[0]);
            // stmt.bindString(3, title[1]);
            // stmt.bindString(4, title[2]);
            // stmt.executeInsert();
            // }
            db.setTransactionSuccessful();
            Log.d(LOG_TAG, "Transaction success.");
        } finally {
            db.endTransaction();
            Log.d(LOG_TAG, "Transaction finish.");
        }
    }

    /**
     * SQLiteOpenHelperクラスには、データベースをアップグレードする仕組みがある。<br>
     * SQLiteOpenHelperクラスのオブジェクトが生成されると、スキーマのバージョンのチェックが行われ、<br>
     * バージョンが変更されていると、このメソッドが呼び出される。<br>
     * これはデータベース自体のアップグレードであって、レコードの更新メソッドではない。
     * 
     * @param db データベース
     * @param oldVersion 旧バージョン番号
     * @param newVersion 新バージョン番号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
}
