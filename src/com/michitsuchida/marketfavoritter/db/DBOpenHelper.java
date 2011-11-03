package com.michitsuchida.marketfavoritter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    static final String LOG_TAG = "DBOpenHelper";

    private static final String DB_NAME = "app_lists.db";
    private static final int DB_VERSION = 1;
    private int m_writableDatabaseCount = 0;

    private static DBOpenHelper m_instance = null;

    synchronized static public DBOpenHelper getInstance(Context context) {
        if (m_instance == null) {
            m_instance = new DBOpenHelper(context.getApplicationContext());
        }
        return m_instance;
    }

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    synchronized public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        return db;
    }

    @Override
    synchronized public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        if (db != null) {
            ++m_writableDatabaseCount;
        }
        return db;
    }

    synchronized public void closeWritableDatabase(SQLiteDatabase database) {
        if (m_writableDatabaseCount > 0 && database != null) {
            --m_writableDatabaseCount;
            if (m_writableDatabaseCount == 0) {
                database.close();
            }
        }
    }

    /**
     * <pre>
     * {@link SQLiteOpenHelper}クラスは、オブジェクトが生成されると
     * データベースが存在するか確認され、存在しない場合は、
     * このメソッドが呼び出されます。このタイミングで、テーブルを
     * 作成するクエリーを実行する。
     * </pre>
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Create db, start transaction");

        // トランザクションを使用したDBデータの作成
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DBMainStore.TBL_NAME
                    + "( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "name TEXT, " + "pkg TEXT, " + "url TEXT );");

            // こんな感じでDB作成の段階でinsertすることもできる
//             SQLiteStatement stmt;
//             stmt = db.compileStatement("INSERT INTO " + DBMainStore.TBL_NAME
//             +
//             " VALUES (?, ?, ?);");
//             for (String[] title : TITLE_LIST) {
//             stmt.bindString(2, title[0]);
//             stmt.bindString(3, title[1]);
//             stmt.bindString(4, title[2]);
//             stmt.executeInsert();
//             }
            db.setTransactionSuccessful();
            Log.d(LOG_TAG, "Transaction success.");
        } finally {
            db.endTransaction();
            Log.d(LOG_TAG, "Transaction finish.");
        }
    }

    /**
     * <pre>
     * {@link SQLiteOpenHelper}クラスには、データベースをアップグレードする
     * 仕組みがある。
     * {@link SQLiteOpenHelper}クラスのオブジェクトが生成されると、スキーマ
     * のバージョンのチェックが行われ、バージョンが変更されていると、
     * このメソッドが呼び出される。
     * これはデータベース自体のアップグレードであって、レコードの更新
     * メソッドではない。
     * </pre>
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
}
