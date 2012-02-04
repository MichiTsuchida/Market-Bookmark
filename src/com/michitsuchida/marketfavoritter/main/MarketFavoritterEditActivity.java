
package com.michitsuchida.marketfavoritter.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.michitsuchida.marketfavoritter.db.DBMainStore;

/**
 * アプリの情報を編集するActivity。ReceiveMarketIntentActivity.javaをベースにしている。
 * 
 * @author MichiTsuchida
 */
public class MarketFavoritterEditActivity extends Activity {

    /** LOG TAG */
    static final String LOG_TAG = "MarketBookmark";

    /** データベースのID */
    private String mId = "";

    /** アプリ名 */
    private String mAppName = "";

    /** Package名 */
    private String mPkg = "";

    /** マーケットのURL */
    private String mUrl = "";

    /** ラベル */
    private String mLabel = "";

    /** アプリ名のEditText */
    private EditText mEtAppName;

    /** ラベルのEditText */
    private EditText mEtLabel;

    /**
     * onCreate.
     * 
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_app_element);
        final Context context = this;

        // Intentの情報を取得する
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            // intentでもらうのはDBの_idだけ、それでDBから取得する
            mId = extras.getString("ID");
            DBMainStore mainStore = new DBMainStore(context, true);
            AppElement elem = mainStore.fetchAppDataByColumnAndValue(DBMainStore.COLUMN_ID, mId);
            if (elem != null) {
                mAppName = elem.getAppName();
                mPkg = elem.getPkgName();
                mUrl = elem.getMarketUrl();
                mLabel = elem.getLabel();
            } else {
                Log.e(LOG_TAG, "ID: " + mId + " did not found!!");
            }

            // Viewに情報をセットする
            mEtAppName = (EditText) findViewById(R.id.editElementEditTextAppName);
            mEtAppName.setText(mAppName);
            TextView tvPkgName = (TextView) findViewById(R.id.editElementTextPkgName);
            tvPkgName.setText(mPkg);
            TextView tvUrl = (TextView) findViewById(R.id.editElementTextUrlBody);
            tvUrl.setText(mUrl);
            mEtLabel = (EditText) findViewById(R.id.editElementEditTextLabel);
            mEtLabel.setText(mLabel);

            Button addButton = (Button) findViewById(R.id.editElementButton);
            addButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBMainStore mainStore = new DBMainStore(context, true);
                    mAppName = mEtAppName.getText().toString();
                    mLabel = mEtLabel.getText().toString();
                    // DBを更新する
                    mainStore.update(Integer.parseInt(mId), mAppName, mPkg, mUrl, mLabel);

                    // Activityを終了する。
                    finish();
                    Toast.makeText(context, mAppName + getString(R.string.toast_edited_item),
                            Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "Edited " + mAppName + ":" + mLabel);
                }
            });

            Button copyButton = (Button) findViewById(R.id.editElementUrlCopyButton);
            copyButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clpbrd = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clpbrd.setText(mUrl);
                    Toast.makeText(context, mUrl + getString(R.string.toast_copied_to_clipboard),
                            Toast.LENGTH_LONG).show();

                }
            });
        } /* if (extras != null) */
    } /* onCreate() */
}
