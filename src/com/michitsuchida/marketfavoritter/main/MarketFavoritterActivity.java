
package com.michitsuchida.marketfavoritter.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
 */
public class MarketFavoritterActivity extends Activity {

    /**
     * ArrayAdapterを拡張したインナークラス
     */
    public class AppElementAdapter extends ArrayAdapter<AppElement> {
        private List<AppElement> list;

        private LayoutInflater inflater;

        /**
         * コンストラクタ
         * 
         * @param context
         * @param resourceId
         * @param list
         */
        public AppElementAdapter(Context context, int resourceId, List<AppElement> list) {
            super(context, resourceId, list);
            this.list = list;
            this.inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * リスト1つ分のViewをInflateして作成する
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Viewが使いまわされていない場合、nullが格納されている
            if (convertView == null) {
                // 1行分layoutからViewの塊を生成
                convertView = inflater.inflate(R.layout.inflater, null);
                Log.d(LOG_TAG, "New convertView create.");
            }

            // listからAppのデータ、viewから画面にくっついているViewを取り出して値を格納する
            final AppElement app = list.get(position);
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
            // ButtonのかわりにViewをクリックしたらマーケットに飛ぶ
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
                            "pos: " + String.valueOf(pos) + ", isChecked: "
                                    + String.valueOf(isChecked));
                    list.get(pos).setIsChecked(isChecked);
                }
            });
            checkBox.setChecked(list.get(position).getIsChecked());

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

    /*
     * ==========================================================================
     * ==
     */
    /* MarketFavoritterActivityクラス本体 */
    /*
     * ==========================================================================
     * ==
     */
    // LOG TAG
    static final String LOG_TAG = "MarketBookmark";

    // アプリのリスト
    private List<AppElement> appList = new ArrayList<AppElement>();

    // DBを操作する
    private DBMainStore mainStore;

    /**
     * onCreate
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mainStore = new DBMainStore(this, true);
        buildListView();
    }

    /**
     * onRestart
     */
    @Override
    public void onRestart() {
        super.onRestart();
        mainStore = new DBMainStore(this, true);
        buildListView();
    }

    /**
     * メニューボタンが押された場合の処理
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean ret = super.onCreateOptionsMenu(menu);
        menu.add(0, Menu.FIRST, Menu.NONE, R.string.menu_remove_item);
        return ret;
    }

    /**
     * メニューから項目を選択された場合の処理 今回はリストのうち、チェックが付いたアプリをDBから削除する
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = super.onOptionsItemSelected(item);
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < appList.size(); i++) {
            if (appList.get(i).getIsChecked()) {
                ids.add(String.valueOf(appList.get(i).get_id()));
            }
        }
        if (ids.size() > 0) {
            mainStore.delete(ids.toArray(new String[] {}));
            Log.i(LOG_TAG, "Selected item was deleted.");
            buildListView();
        } else {
            Log.i(LOG_TAG, "There are no selected item(s).");
            Toast.makeText(this, R.string.toast_no_item_is_checked, Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    /**
     * アプリのリストを作成する
     */
    private void buildListView() {
        appList = mainStore.fetchAllData();
        if (mainStore.getCount() == 0) {
            Log.i(LOG_TAG, "AppList is empty!!");
            finish();
            Toast.makeText(this, R.string.toast_no_item_in_list, Toast.LENGTH_LONG).show();
        }
        // add add add...
        // appList.add(new AppElement("SPモードメール",
        // "jp.co.nttdocomo.carriermail",
        // "https://market.android.com/details?id=jp.co.nttdocomo.carriermail"));
        // appList.add(new AppElement("モバイルGoogleマップ",
        // "com.google.android.apps.maps",
        // "https://market.android.com/details?id=com.google.android.apps.maps"));

        AppElementAdapter adapter = new AppElementAdapter(this, R.id.inflaterLayout, appList);
        ListView listView = (ListView) findViewById(R.id.mainAppListView);
        listView.setAdapter(adapter);
    }
}
