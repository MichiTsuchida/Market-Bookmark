
package com.michitsuchida.marketfavoritter.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
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

            Button labelSuggestButton = (Button) findViewById(R.id.editElementSuggestLabelButton);
            labelSuggestButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ダイアログに表示するアイテムと、アイテムがチェックされたかのフラグの配列
                    final String[] labelArray = buildSuggestionLabelList();
                    final boolean[] labelFlags = new boolean[labelArray.length];

                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(R.string.dialog_suggest_label_title);

                    // チェックボックスのダイアログ
                    dialog.setMultiChoiceItems(labelArray, labelFlags,
                            new OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                                    labelFlags[which] = isChecked;
                                }
                            });
                    // OKボタン
                    dialog.setPositiveButton(R.string.button_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StringBuffer buff = new StringBuffer();
                                    // チェックが付けられたラベルをカンマで結合
                                    for (int i = 0; i < labelArray.length; i++) {
                                        if (labelFlags[i]) {
                                            buff.append(labelArray[i]);
                                            buff.append(",");
                                        }
                                    }

                                    // 何も選択せずにOKを押した場合は何もしない
                                    if (buff.length() != 0) {
                                        // ラベルのテキストボックスに反映
                                        mEtLabel.setText(buff);
                                    }
                                }
                            });
                    // キャンセルボタン
                    dialog.setNegativeButton(R.string.button_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 何もしない
                                }
                            });
                    dialog.show();
                }
            });

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
        }
    }

    /**
     * ラベルの一覧を作成する。
     * 
     * @return ラベル一覧の配列
     */
    private String[] buildSuggestionLabelList() {
        // すべてのデータのラベル部分を取得する
        DBMainStore mainStore = new DBMainStore(this, true);
        List<AppElement> apps = mainStore.fetchAllAppData(null);
        List<String> labelList = new ArrayList<String>();
        for (AppElement elem : apps) {
            labelList.add(elem.getLabel());
        }
        // Log.d(LOG_TAG, labelList.toString());
        // ラベルの重複をなくす
        List<String> splittedLabelList = new ArrayList<String>();
        for (String string1 : labelList) {
            // ラベルが1個もないとぬるぽになる
            if (string1 != null) {
                String[] str = string1.split(",");
                for (String string2 : str) {
                    splittedLabelList.add(string2);
                }
            }
        }
        // Log.d(LOG_TAG, splittedLabelList.toString());
        List<String> duplicatedLabelList = new ArrayList<String>();
        for (int i = 0; i < splittedLabelList.size(); i++) {
            if (!duplicatedLabelList.contains(splittedLabelList.get(i))
                    && !splittedLabelList.get(i).equals("")) {
                duplicatedLabelList.add(splittedLabelList.get(i));
            }
        }
        // 完成したラベルリストを並び替える
        Collections.sort(duplicatedLabelList);

        return duplicatedLabelList.toArray(new String[duplicatedLabelList.size()]);
    }
}
