
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

    /** Memo for this Application such as TAGs */
    String mTagMemo;

    /** DataBase ID */
    int _id;

    /** Is this element checkbox tapped? */
    boolean mIsChecked;

    /**
     * @param mAppName
     * @param mPkgName
     * @param mMarketUrl
     */
    public AppElement(String appName, String pkgName, String marketUrl) {
        super();
        this.mAppName = appName;
        this.mPkgName = pkgName;
        this.mMarketUrl = marketUrl;
    }

    /**
     * @param mAppName
     * @param mPkgName
     * @param mMarketUrl
     * @param _id
     */
    public AppElement(String appName, String pkgName, String marketUrl, int _id) {
        super();
        this.mAppName = appName;
        this.mPkgName = pkgName;
        this.mMarketUrl = marketUrl;
        this._id = _id;
    }

    /**
     * @param mAppName
     * @param mPkgName
     * @param mMarketUrl
     * @param _id
     * @param mIsChecked
     */
    public AppElement(String appName, String pkgName, String marketUrl, int _id, boolean isChecked) {
        super();
        this.mAppName = appName;
        this.mPkgName = pkgName;
        this.mMarketUrl = marketUrl;
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
}
