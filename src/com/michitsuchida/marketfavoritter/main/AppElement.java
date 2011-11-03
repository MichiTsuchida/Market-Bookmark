package com.michitsuchida.marketfavoritter.main;

public class AppElement {
    String appName;
    String pkgName;
    String marketUrl;
    int _id;
    boolean isChecked;

    /**
     * @param appName
     * @param pkgName
     * @param marketUrl
     */
    public AppElement(String appName, String pkgName, String marketUrl) {
        super();
        this.appName = appName;
        this.pkgName = pkgName;
        this.marketUrl = marketUrl;
    }

    /**
     * @param appName
     * @param pkgName
     * @param marketUrl
     * @param _id
     */
    public AppElement(String appName, String pkgName, String marketUrl, int _id) {
        super();
        this.appName = appName;
        this.pkgName = pkgName;
        this.marketUrl = marketUrl;
        this._id = _id;
    }

    /**
     * @param appName
     * @param pkgName
     * @param marketUrl
     * @param _id
     * @param isChecked
     */
    public AppElement(String appName, String pkgName, String marketUrl,
            int _id, boolean isChecked) {
        super();
        this.appName = appName;
        this.pkgName = pkgName;
        this.marketUrl = marketUrl;
        this._id = _id;
        this.isChecked = isChecked;
    }

    public String getAppName() {
        return appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public String getMarketUrl() {
        return marketUrl;
    }

    public int get_id() {
        return _id;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
