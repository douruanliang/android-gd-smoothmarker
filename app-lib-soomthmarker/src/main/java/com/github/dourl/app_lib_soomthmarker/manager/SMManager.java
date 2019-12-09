package com.github.dourl.app_lib_soomthmarker.manager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.utils.overlay.MovingPointOverlay;
import com.github.dourl.app_lib_soomthmarker.R;
import com.github.dourl.app_lib_soomthmarker.model.CarLatLng;
import com.github.dourl.app_lib_soomthmarker.model.MarkerObject;
import com.github.dourl.app_lib_soomthmarker.util.PositionUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;


/**
 * @author: douruanliang 核心类
 * @date: 2019-08-27
 */
public class SMManager {
    public AMap mAMap;
    private static String TAG = "SMManager";
    private Map<String, MovingPointOverlay> mMovingPointOverlayMap = new ConcurrentHashMap<>();
    private Map<String, Queue<List<LatLng>>> mCarLatLngMap = new ConcurrentHashMap<>();
    private Map<String, LatLng> mCarLastLatLng = new ConcurrentHashMap<>();
    private onMarkerClickForAction mOnMarkerClickForAction;
    private static final int MESSAGE_ID_PATH_ADD = 0;
    private static final int MESSAGE_ID_PATH_CONSUMED = 1;

    public SMManager() {
        this.mAMap = NaviManager.getAMap();
    }

    private Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String carId = (String) msg.obj;
            switch (msg.what) {
                case MESSAGE_ID_PATH_ADD:
                    handlePathAdd(carId);
                    break;
                case MESSAGE_ID_PATH_CONSUMED:
                    handlePathUpdate(carId);
                    break;
                default:
                    break;
            }

        }
    };

    private void handlePathAdd(String id) {
        LinkedList<LatLng> path = (LinkedList<LatLng>) mCarLatLngMap.get(id).poll();
        if (path != null && path.size() > 0) {
            addSmoothMarkerByCarId(id, path);
        }
    }


    private void handlePathUpdate(String id) {
        LinkedList<LatLng> path = (LinkedList<LatLng>) mCarLatLngMap.get(id).poll();
        LatLng myLastPosition = mCarLastLatLng.get(id);
        if (myLastPosition != null && path != null) {
            path.addFirst(myLastPosition);
        }
        LatLng lastPosition = null;
        if (path != null) {
            lastPosition = path.getLast();
            mCarLastLatLng.put(id, lastPosition);
            addSmoothMarkerByCarId(id, path);
        } else {
            Log.e(TAG, "我是空的");
        }
    }


    /**
     * 更新小车坐标
     *
     * @param carLatLngs
     */
    public void addLatLngByCarID(List<CarLatLng> carLatLngs) {
        if (carLatLngs == null || carLatLngs.size() == 0) return;
        LinkedList<LatLng> tempList = new LinkedList<>();
        //小车ID
        String carId = carLatLngs.get(0).carId;
        for (CarLatLng carLatLng : carLatLngs) {
            LatLng tempLatLng = addLatLngByCarID(carLatLng.lat, carLatLng.lng);
            tempList.add(tempLatLng);
        }
        Message message = sHandler.obtainMessage();
        message.obj = carId;
        if (mCarLatLngMap.containsKey(carId)) {
            Log.e(TAG, "更新");
            mCarLatLngMap.get(carId).add(tempList);
            message.what = MESSAGE_ID_PATH_CONSUMED;
        } else {
            Log.e(TAG, "新增");
            Queue<List<LatLng>> carLatLngQueue = new LinkedList<>();
            carLatLngQueue.add(tempList);
            mCarLatLngMap.put(carId, carLatLngQueue);
            message.what = MESSAGE_ID_PATH_ADD;
        }
        sHandler.sendMessage(message);
    }

    /**
     * @param lat
     * @param lng
     * @return
     */
    private LatLng addLatLngByCarID(double lat, double lng) {
        double[] mapLatLng = PositionUtil.gps84_To_Gcj02(lat, lng);
        if (mapLatLng.length < 2) {
            return null;
        }
        return new LatLng(mapLatLng[0], mapLatLng[1]);
    }

    private void addSmoothMarkerByCarId(final String carID, List<LatLng> points) {
        MovingPointOverlay smoothMarker = null;
        if (!mMovingPointOverlayMap.containsKey(carID)) {
            if (mAMap != null) {
                mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker != null && marker.getObject() != null) {
                            MarkerObject markerObject = (MarkerObject) marker.getObject();
                            if (markerObject != null && !TextUtils.isEmpty(markerObject.carId)) {
                                if (mOnMarkerClickForAction != null) {
                                    mOnMarkerClickForAction.action(markerObject.carId);
                                }

                            }
                        }
                        return false;
                    }
                });

                if (smoothMarker == null) {
                    Marker marker = null;
                    marker = mAMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car))
                            .anchor(0.5f, 0.5f));
                    marker.setObject(new MarkerObject(carID));
                    smoothMarker = new MovingPointOverlay(mAMap, marker);
                }
                mMovingPointOverlayMap.put(carID, smoothMarker);
            } else {
                Log.e(TAG, "amap 获取不到");
            }

        } else {
            //复用
            smoothMarker = mMovingPointOverlayMap.get(carID);

        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(points.get(0));
        builder.include(points.get(points.size() - 2));

        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        // 设置轨迹点
        smoothMarker.setPoints(points);
        // 设置平滑移动的总时间  单位  秒
        //smoothMarker.setTotalDuration((int) (points.size() * 0.1));

        // TODO 根据自己的项目
        smoothMarker.setTotalDuration(1);

        smoothMarker.setMoveListener(new MovingPointOverlay.MoveListener() {
            @Override
            public void move(double v) {
                if (v == 0.0) {
                   /* Message message = sHandler.obtainMessage();
                    message.obj = carID;
                    message.what = MESSAGE_ID_PATH_CONSUMED;
                    sHandler.sendMessage(message);*/
                }
            }
        });
        // 开始移动
        smoothMarker.startSmoothMove();


    }

    public void onDestroySmoothMove() {
        if (!mMovingPointOverlayMap.isEmpty()) {
            for (String carId : mMovingPointOverlayMap.keySet()) {
                MovingPointOverlay smoothMarker = (MovingPointOverlay) mMovingPointOverlayMap.get(carId);
                if (smoothMarker != null) {
                    Log.d("smoothMarker", "onDestroy");
                    smoothMarker.removeMarker();
                    smoothMarker.setMoveListener(null);
                    smoothMarker.destroy();
                }
            }
        } else {
            Log.d("smoothMarker", "no car on map");
        }
    }

    public interface onMarkerClickForAction {
        void action(String CarID);
    }

    public void setOnMarkerClickForAction(onMarkerClickForAction onMarkerClickForAction) {
        mOnMarkerClickForAction = onMarkerClickForAction;
    }

}
