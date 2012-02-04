
package com.michitsuchida.marketfavoritter.main;

/**
 * アプリ情報を保持するデータクラス。
 * 
 * @author MichiTsuchida
 */
public class AppElement {

    /** Application name */
    String mAppName;

    /** Application Package name */
    String mPkgName;

    /** Application URL of Market */
    String mMarketUrl;

    /** Label for Application */
    String mLabel;

    /** DataBase ID */
    int _id;

    /** Is checkbox of this element tapped? */
    boolean mIsChecked;

    /**
     * デフォルトコンストラクタ。
     */
    public AppElement() {
    }

    /**
     * @param mAppName
     * @param mPkgName
     * @param mMarketUrl
     * @param mLabel
     * @param _id
     */
    public AppElement(String appName, String pkgName, String marketUrl, String label, int _id) {
        super();
        this.mAppName = appName;
        this.mPkgName = pkgName;
        this.mMarketUrl = marketUrl;
        this.mLabel = label;
        this._id = _id;
    }

    /**
     * @param appName
     * @param pkgName
     * @param marketUrl
     * @param label
     * @param _id
     * @param isChecked
     */
    public AppElement(String appName, String pkgName, String marketUrl, String label, int _id,
            boolean isChecked) {
        super();
        this.mAppName = appName;
        this.mPkgName = pkgName;
        this.mMarketUrl = marketUrl;
        this.mLabel = label;
        this._id = _id;
        this.mIsChecked = isChecked;
    }

    public String getAppName() {
        return mAppName;
    }

    public String getPkgName() {
        return mPkgName;
    }

    public String getMarketUrl() {
        return mMarketUrl;
    }

    public int get_id() {
        return _id;
    }

    public boolean getIsChecked() {
        return mIsChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.mIsChecked = isChecked;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }
}
