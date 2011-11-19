
package com.michitsuchida.marketfavoritter.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.michitsuchida.marketfavoritter.db.DBMainStore;

/**
 * このアプリのメインのActivity
 * 
 * @author MichiTsuchida
 */
public class MarketFavoritterActivity extends Activity {

    /** LOG TAG */
    static final String LOG_TAG = "MarketBookmark";

    /** SharedPreferenceに保存するためのキー */
    private static final String SHARED_PREF_KEY_SORT_ODER = "SortOrder";

    /** メニューID */
    private final int MENU_ID1 = Menu.FIRST;

    private final int MENU_ID2 = Menu.FIRST + 1;

    /** アプリのリスト */
    private List<AppElement> mAppList = new ArrayList<AppElement>();

    /** DBを操作する */
    private DBMainStore mMainStore;

    /** SharedPreferenceオブジェクト */
    private SharedPreferences mSharedPref;

    /**
     * onCreate
     * 
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SharedPreferenceオブジェクトの初期化
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.main);
        mMainStore = new DBMainStore(this, true);
        buildListView();
    }

    /**
     * onRestart
     */
    @Override
    public void onRestart() {
        super.onRestart();
        mMainStore = new DBMainStore(this, true);
        buildListView();
    }

    /**
     * メニューボタンが押された場合の処理
     * 
     * @param menu メニュー
     * @return メニューが表示される場合はtrue、そうでなければfalse
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean ret = super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ID1, Menu.NONE, R.string.menu_sort_item);
        menu.add(0, MENU_ID2, Menu.NONE, R.string.menu_remove_item);
        return ret;
    }

    /**
     * メニューから項目を選択された場合の処理<br>
     * 
     * @param item メニューアイテム
     * @return メニューアイテムが表示される場合はtrue、そうでなければfalse
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            // リストをソート
            case MENU_ID1:
                String[] orders = this.getResources().getStringArray(R.array.sort_order);
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.sort_title);
                dialog.setItems(orders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // デフォルト(_id昇順)
                                sort(DBMainStore.COLUMN_ID, DBMainStore.ASC);
                                break;

                            case 1:
                                // アプリ名昇順
                                sort(DBMainStore.COLUMN_APP_NAME, DBMainStore.ASC);
                                break;

                            case 2:
                                // アプリ名降順
                                sort(DBMainStore.COLUMN_APP_NAME, DBMainStore.DESC);
                                break;

                            case 3:
                                // パッケージ名昇順
                                sort(DBMainStore.COLUMN_APP_PACKAGE, DBMainStore.ASC);
                                break;

                            case 4:
                                // パッケージ名降順
                                sort(DBMainStore.COLUMN_APP_PACKAGE, DBMainStore.DESC);
                                break;

                            default:
                                // do nothing.
                                break;
                        }
                    }
                });
                dialog.show();
                break;

            // リストから削除
            case MENU_ID2:
                List<String> ids = new ArrayList<String>();
                for (int i = 0; i < mAppList.size(); i++) {
                    if (mAppList.get(i).getIsChecked()) {
                        ids.add(String.valueOf(mAppList.get(i).get_id()));
                    }
                }
                if (ids.size() > 0) {
                    mMainStore.delete(ids.toArray(new String[] {}));
                    Log.i(LOG_TAG, "Selected item was deleted.");
                    buildListView();
                } else {
                    Log.i(LOG_TAG, "There are no selected item(s).");
                    Toast.makeText(this, R.string.toast_no_item_is_checked, Toast.LENGTH_LONG)
                            .show();
                }
                break;

            default:

        }

        return ret;
    }

    /**
     * アプリのリストを作成する
     */
    private void buildListView() {
        // SharedPreferenceに保存された並び替えのオーダーを取得して、その順でリストを作成する
        mAppList = mMainStore.fetchAllData(getSortOrder());
        if (mMainStore.getCount() == 0) {
            Log.i(LOG_TAG, "AppList is empty!!");
            finish();
            Toast.makeText(this, R.string.toast_no_item_in_list, Toast.LENGTH_LONG).show();
        }
        // add add add...
        // mAppList.add(new AppElement("SPモードメール",
        // "jp.co.nttdocomo.carriermail",
        // "https://market.android.com/details?id=jp.co.nttdocomo.carriermail"));
        // mAppList.add(new AppElement("モバイルGoogleマップ",
        // "com.google.android.apps.maps",
        // "https://market.android.com/details?id=com.google.android.apps.maps"));

        AppElementAdapter adapter = new AppElementAdapter(this, R.id.inflaterLayout, mAppList);
        ListView listView = (ListView) findViewById(R.id.mainAppListView);
        listView.setAdapter(adapter);
    }

    /**
     * 並び替えを実行する。<br>
     * 並び替えた後、アプリのリスト作成まで行う。
     * 
     * @param orderColumn 並び替えの列(データベースのカラム名で指定)
     * @param sortOrder 昇順(ASC)または降順(DESC)
     */
    private void sort(String orderColumn, String sortOrder) {
        String order = orderColumn + " " + sortOrder;
        Log.d(LOG_TAG, "Sort order: " + order);
        mAppList = mMainStore.fetchAllData(order);

        AppElementAdapter adapter = new AppElementAdapter(this, R.id.inflaterLayout, mAppList);
        ListView listView = (ListView) findViewById(R.id.mainAppListView);
        listView.setAdapter(adapter);

        // 並び替えのオーダーを保存しておく
        putSortOrder(order);
    }

    /**
     * 並び替えのオーダーを取得する。<br>
     * 初回起動時は値が無いので、第2引数の値をdefault値として取得する。<br>
     * また、もし値が取得出来なかった時もこの値が取得される。
     * 
     * @return 取得した並び替えのオーダー、値がない場合はnull
     */
    private String getSortOrder() {
        return this.mSharedPref.getString(SHARED_PREF_KEY_SORT_ODER, null);
    }

    /**
     * 並び替えのオーダーをSharedPreferenceに書き込む。<br>
     * 最後のcommit()を行った時点で書き込まれる。<br>
     * ＃それまではメモリに保持される??
     * 
     * @param order 並び替えのオーダー
     */
    private void putSortOrder(String order) {
        this.mSharedPref.edit().putString(SHARED_PREF_KEY_SORT_ODER, order).commit();
    }

    // ============================================================================================
    /**
     * ArrayAdapterを拡張したインナークラス
     */
    class AppElementAdapter extends ArrayAdapter<AppElement> {

        /** AppElement型のリスト */
        private List<AppElement> mList;

        /** カスタムListViewを作成するためのInflator */
        private LayoutInflater mInflater;

        /**
         * コンストラクタ
         * 
         * @param context このアプリケーションのコンテキスト
         * @param resourceId リソースのID
         * @param mList 描画するデータの格納されたリスト
         */
        public AppElementAdapter(Context context, int resourceId, List<AppElement> list) {
            super(context, resourceId, list);
            this.mList = list;
            this.mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * AppElement分のViewをInflateして作成する
         * 
         * @param position AppElementリストのインデックス
         * @param convertView View
         * @param parent このViewの親View
         * @return 作成されたカスタムListView
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Viewが使いまわされていない場合、nullが格納されている
            if (convertView == null) {
                // 1行分layoutからViewの塊を生成
                convertView = mInflater.inflate(R.layout.inflater, null);
                Log.d(LOG_TAG, "New convertView create.");
            }

            // listからAppのデータ、viewから画面にくっついているViewを取り出して値を格納する
            final AppElement app = mList.get(position);
            TextView appNameText = (TextView) convertView.findViewById(R.id.inflaterAppName);
            appNameText.setText(app.getAppName());
            TextView appPkgText = (TextView) convertView.findViewById(R.id.inflaterAppPkgName);
            appPkgText.setText(app.getPkgName());

            // Buttonを実装
            Button button = (Button) convertView.findViewById(R.id.inflaterButton);
            button.setText(R.string.button_market);
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // MarketアプリへのIntentを作成して投げる
                    // AndroidMarketのアプリ詳細画面を開く
                    // market://details?id=<pkg name>
                    // AndroidMarketをアプリ開発者名で検索
                    // market://search?q=pub:<publisher name>
                    // AndroidMarketをフリーワード検索
                    // market://search?q=<words>
                    Uri uri = Uri.parse("market://details?id=" + app.getPkgName());
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    Log.i(LOG_TAG, "Throw intent for AndroidMarket. Uri: " + uri.toString());
                }
            });
            // ButtonのかわりにViewをクリックしたらマーケットに飛ぶ(上手く動いてないｗ)
            // convertView.setOnClickListener(new OnClickListener() {
            // @Override
            // public void onClick(View v) {
            // Uri uri = Uri.parse("market://details?id=" + app.getPkgName());
            // startActivity(new Intent(Intent.ACTION_VIEW, uri));
            // Log.i(LOG_TAG, "Throw intent for AndroidMarket. Uri: " +
            // uri.toString());
            // }
            // });

            // CheckBoxを実装
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.inflaterCheckBox);
            final int pos = position;
            // setChecked()をやる前にリスナ登録しないと、
            // 使いまわしてる他のViewのチェックも道連れにチェックされるｗ
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked) {
                    Log.d(LOG_TAG,
                            "pos: " + String.valueOf(pos) + ", mIsChecked: "
                                    + String.valueOf(isChecked));
                    mList.get(pos).setIsChecked(isChecked);
                }
            });
            checkBox.setChecked(mList.get(position).getIsChecked());

            // これでロングタップした時のポップアップメニューが作れるよ!!
            // v.setOnCreateContextMenuListener(new
            // OnCreateContextMenuListener() {
            // public void onCreateContextMenu(ContextMenu contextmenu, View
            // view, ContextMenuInfo contextmenuinfo) {
            // ;
            // }
            // });
            return convertView;
        }
    }
}
