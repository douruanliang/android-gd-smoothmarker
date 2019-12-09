package com.github.dourl.app_lib_soomthmarker.manager;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;


/**
 * @author: douruanliang
 * @date: 2019-11-21
 */
public class NaviManager {
    private Context mContext;
    private static NaviManager mInStance;
    private MapView mAMapNaviView;
    private static AMap mAMap;

    public static NaviManager getInstance(Context context) {
        if (mInStance == null) {
            synchronized (NaviManager.class) {
                if (mInStance == null) {
                    mInStance = new NaviManager(context);
                }
            }
        }
        return mInStance;
    }


    /**
     * 设置导航属性
     */
    public void setNaviViewOptions(MapView aMapNaviView) {
        this.mAMapNaviView = aMapNaviView;
        this.mAMap = aMapNaviView.getMap();
    }

    private NaviManager(Context context) {
        this.mContext = context.getApplicationContext();

    }

    public static AMap getAMap() {
        return mAMap;
    }
}
