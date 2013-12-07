
package com.michitsuchida.marketfavoritter.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * マーケットアプリからのIntentを受けた場合に呼ばれるActivity。
 * 
 * @author MichiTsuchida
 */
public class ReceiveMarketIntentActivity extends Activity {

    /** LOG TAG */
    static final String LOG_TAG = "MarketBookmark";

    /** Handlerでアプリ名を取得する際に使用するKey */
    public static final String EXTRA_APP_NAME = "APP_NAME";

    /** <title>タグの内容をぶっこ抜く正規表現パターン */
    public static final String PATTERN_FOR_TITLE_TAG = ".*<title>(.+)\\s-\\s.+</title>.*";

    /** Intent.EXTRA_SUBJECTからアプリ名をぶっこ抜く正規表現パターン(日本語) */
    public static final String PATTERN_APP_NAME_FROM_SUBJECT_JA = "「(.+)」";

    /** Intent.EXTRA_SUBJECTからアプリ名をぶっこ抜く正規表現パターン(日本語) */
    public static final String PATTERN_APP_NAME_FROM_SUBJECT_EN = "\"(.+)\"";

    /** マーケットアプリからのIntent URL */
    public static final String MARKET_URL = "market.android.com/details";

    /** Google playストアからのIntent URL */
    public static final String GOOGLE_PLAY_URL = "play.google.com/store/apps";

    /** 取得されたアプリ名 */
    private String mAppName = "";

    /** Package名 */
    private String mPkg = "";

    /** マーケットのURL */
    private String mUrl = "";

    /** ラベル */
    private String mLabel;

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
        setContentView(R.layout.market_link_intent);
        final Context context = this;

        Intent marketIntent = getIntent();
        String action = marketIntent.getAction();

        // Actionの判定
        if (Intent.ACTION_SEND.equals(action)) {
            // Intentの情報を取得する
            Bundle extras = marketIntent.getExtras();
            if (extras != null) {
                // アプリのURLを取得する
                mUrl = extras.getCharSequence(Intent.EXTRA_TEXT).toString();
                Log.d(LOG_TAG, "Intent.EXTRA_TEXT: " + mUrl);

                // 呼び出し元がマーケットかどうか判定
                if (mUrl.contains(MARKET_URL) || mUrl.contains(GOOGLE_PLAY_URL)) {
                    // URLをパッケージ名に変換
                    mPkg = mUrl.substring(mUrl.indexOf("id=") + 3);

                    // DBの重複チェック
                    final DBMainStore mainStore = new DBMainStore(this, true);
                    AppElement elem = mainStore.fetchAppDataByColumnAndValue(
                            DBMainStore.COLUMN_APP_PACKAGE, mPkg);
                    if (elem != null) {

                        // DBに既にアプリ名が登録されてる場合

                        Log.d(LOG_TAG, "Already exist on database. App name: " + mAppName);
                        // なぜかToast表示されないので、重複してるよダイアログ
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle(R.string.dialog_add_item_title);
                        dialog.setMessage(R.string.dialog_add_item_text);

                        // OKボタン
                        dialog.setPositiveButton(R.string.button_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        // Activityを終了する
                                        finish();
                                    }
                                }).show();
                    } else {

                        // DBにアプリ名が登録されてない場合

                        // IntentのSubjectにアプリ名が含まれているのでそれを引っ張ってくる
                        String extraSubject = extras.getCharSequence(Intent.EXTRA_SUBJECT)
                                .toString();
                        // Log.d(LOG_TAG, "Intent.EXTRA_SUBJECT: " +
                        // extraSubject);

                        // 日本語の場合
                        Pattern pattern = Pattern.compile(PATTERN_APP_NAME_FROM_SUBJECT_JA);
                        Matcher matcher = pattern.matcher(extraSubject);
                        if (matcher.find()) {
                            mAppName = matcher.group(1);
                            Log.d(LOG_TAG, "App name: " + mAppName);
                        } else {
                            // 英語の場合
                            pattern = Pattern.compile(PATTERN_APP_NAME_FROM_SUBJECT_EN);
                            matcher = pattern.matcher(extraSubject);
                            if (matcher.find()) {
                                mAppName = matcher.group(1);
                                Log.d(LOG_TAG, "App name: " + mAppName);
                            } else {
                                Log.e(LOG_TAG, "Could not find App name!");
                            }

                        }

                        // Viewに情報をセットする
                        mEtAppName = (EditText) findViewById(R.id.marketLinkEditTextAppName);
                        mEtAppName.setText(mAppName);
                        TextView tvPkgName = (TextView) findViewById(R.id.marketLinkTextPkgName);
                        tvPkgName.setText(mPkg);
                        TextView tvUrl = (TextView) findViewById(R.id.marketLinkTextUrlBody);
                        tvUrl.setText(mUrl);
                        mEtLabel = (EditText) findViewById(R.id.marketLinkEditTextLabel);

                        Button copyButton = (Button) findViewById(R.id.marketLinkUrlCopyButton);
                        copyButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ClipboardManager clpbrd = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                clpbrd.setText(mUrl);
                                Toast.makeText(context,
                                        mUrl + getString(R.string.toast_copied_to_clipboard),
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                        Button labelSuggestButton = (Button) findViewById(R.id.marketLinkSuggestLabelButton);
                        labelSuggestButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // ダイアログに表示するアイテムと、アイテムがチェックされたかのフラグの配列
                                final String[] labelArray = buildSuggestionLabelList();
                                final boolean[] labelFlags = new boolean[labelArray.length];

                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                                // ラベルリストが空だった場合
                                if (labelArray.length == 0) {
                                    dialog.setMessage(R.string.dialog_suggest_label_empty_text);
                                    dialog.setPositiveButton(R.string.button_ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                        int which) {
                                                    // 何もしない
                                                }
                                            });
                                } else {
                                    dialog.setTitle(R.string.dialog_suggest_label_title);

                                    // チェックボックスのダイアログ
                                    dialog.setMultiChoiceItems(labelArray, labelFlags,
                                            new OnMultiChoiceClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                        int which, boolean isChecked) {
                                                    labelFlags[which] = isChecked;
                                                }
                                            });
                                    // OKボタン
                                    dialog.setPositiveButton(R.string.button_ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                        int which) {
                                                    StringBuffer buff = new StringBuffer();
                                                    // チェックが付けられたラベルをカンマで結合
                                                    for (int i = 0; i < labelArray.length; i++) {
                                                        if (labelFlags[i]) {
                                                            buff.append(labelArray[i]);
                                                            buff.append(",");
                                                        }
                                                    }
                                                    // ラベルのテキストボックスに反映
                                                    mEtLabel.setText(buff);
                                                }
                                            });
                                    // キャンセルボタン
                                    dialog.setNegativeButton(R.string.button_cancel,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                        int which) {
                                                    // 何もしない
                                                }
                                            });
                                }

                                // ダイアログを表示
                                dialog.show();
                            }
                        });

                        // DBに追加するボタン
                        Button addButton = (Button) findViewById(R.id.marketLinkAddButton);
                        addButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAppName = mEtAppName.getText().toString();
                                mLabel = mEtLabel.getText().toString();

                                // DBに追加する
                                mainStore.add(mAppName, mPkg, mUrl, mLabel);

                                // Activityを終了する
                                finish();
                                Toast.makeText(context,
                                        mAppName + getString(R.string.toast_added_item),
                                        Toast.LENGTH_LONG).show();
                                Log.d(LOG_TAG, "Add to Market Bookmark. App name: " + mAppName);
                            }
                        });
                    } /* if (elem != null) */
                } else {
                    // マーケットから呼びだしてねダイアログ
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle(R.string.dialog_share_from_market_title);
                    dialog.setMessage(R.string.dialog_share_from_market_text);

                    // OKボタン
                    dialog.setPositiveButton(R.string.button_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    // Activityを終了する
                                    finish();
                                }
                            }).show();
                }
            }
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
