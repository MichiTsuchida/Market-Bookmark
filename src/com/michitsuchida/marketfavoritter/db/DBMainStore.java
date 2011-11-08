
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

public class DBMainStore {

    static final String LOG_TAG = "DBMainStore";

    private DBOpenHelper m_helper;

    private SQLiteDatabase m_db;

    static final String TBL_NAME = "app";

    private static final String COLUMN_ID = "_id";

    private static final String COLUMN_APP_NAME = "name";

    private static final String COLUMN_APP_PACKAGE = "pkg";

    private static final String COLUMN_APP_URL = "url";

    // Counter for DB data rows.
    private int count = 0;

    /**
     * Constructor. Get the DB object.
     * 
     * @param context
     * @param isUpdate
     */
    public DBMainStore(Context context, boolean isUpdate) {
        m_helper = DBOpenHelper.getInstance(context);
        if (m_helper != null) {
            if (isUpdate) {
                m_db = m_helper.getWritableDatabase();
            } else {
                m_db = m_helper.getReadableDatabase();
            }
        } else {
            m_db = null;
        }
    }

    /**
     * Close DB.
     */
    public void close() {
        m_db.close();
    }

    /**
     * Add the record to DB.
     */
    public void add(String name, String pkg, String url) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_NAME, name);
        values.put(COLUMN_APP_PACKAGE, pkg);
        values.put(COLUMN_APP_URL, url);
        m_db.insert(TBL_NAME, null, values);
    }

    /**
     * Update the record of DB.
     */
    public void update(int _id, String name, String pkg, String url) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_NAME, name);
        values.put(COLUMN_APP_PACKAGE, pkg);
        values.put(COLUMN_APP_URL, url);
        m_db.update(TBL_NAME, values, COLUMN_ID + "=?", new String[] {
            Integer.toString(_id)
        });
    }

    /**
     * Delete record from DB.
     */
    public void delete(String[] _ids) {
        for (int i = 0; i < _ids.length; i++) {
            m_db.delete(TBL_NAME, "_id=?", new String[] {
                _ids[i]
            });
        }
    }

    /**
     * Get all record(s) from DB.
     */
    public List<AppElement> fetchAllData() {
        List<AppElement> data = new ArrayList<AppElement>();
        Cursor c = null;
        try {
            c = m_db.query(TBL_NAME, new String[] {
                    COLUMN_ID, COLUMN_APP_NAME, COLUMN_APP_PACKAGE, COLUMN_APP_URL
            }, null, null, null, null, null);
            c.moveToFirst();
            count = c.getCount();
            for (int i = 0; i < count; i++) {
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

    public int getCount() {
        return count;
    }
}
