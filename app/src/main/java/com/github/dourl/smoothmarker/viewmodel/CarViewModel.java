package com.github.dourl.smoothmarker.viewmodel;

import android.content.Context;
import android.util.Log;

import com.github.dourl.app_lib_soomthmarker.model.CarLatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author: douruanliang
 * @date: 2019-12-03
 */
public class CarViewModel extends ViewModel {

    private static final String TAG = "CarViewModel";

    private MockManager mMockManager; //测试数据
    private static final int mCarLatLngSize = 10;
    private MutableLiveData<List<CarLatLng>> vehicleLocations = new MutableLiveData<>();
    private Map<String, List<CarLatLng>> mCarLatLngListMap = new ConcurrentHashMap<>();
    private Map<String, Long> mCarTimeMap = new ConcurrentHashMap<>();

    public CarViewModel() {
        mMockManager = new MockManager();
        mMockManager.start();
    }

    public LiveData<List<CarLatLng>> getVehicleLocation(Context context) {

        mMockManager.setListener(new MockManager.MockEventListener() {
            @Override
            public void onRemoteVehicleInfoEvent(CarLatLng latLng) {

                String carId = latLng.carId;
                if (mCarLatLngListMap.containsKey(carId)) {

                    Log.d(TAG, "更新" + carId);
                    List<CarLatLng> tempList = mCarLatLngListMap.get(carId);
                    tempList.add(latLng);
                    //方案 一  每1秒移动10个点
                   /* if (tempList.size() == mCarLatLngSize) {
                        Log.d(TAG, "update{}" + tempList.size() + "carId:{}" + carId);
                        vehicleLocations.postValue(new LinkedList<>(tempList));
                        mCarLatLngListMap.get(carId).clear();
                    }*/
                    //方案 二  每1秒一点不确定点
                    if (mCarTimeMap.containsKey(carId)) {
                        if (((System.currentTimeMillis() - mCarTimeMap.get(carId)) > 3000)) {
                            Log.d(TAG, "update{}" + tempList.size() + "当前车carId:{}" + carId);
                            vehicleLocations.postValue(new LinkedList<>(tempList));
                            mCarLatLngListMap.get(carId).clear();
                            mCarTimeMap.put(carId, System.currentTimeMillis());
                        }

                    }


                } else {
                    Log.d(TAG, "新增add" + carId);
                    List<CarLatLng> tempList = new ArrayList<>();
                    tempList.add(latLng);
                    mCarLatLngListMap.put(carId, tempList);
                    mCarTimeMap.put(carId, System.currentTimeMillis());
                }
            }
        });

        return vehicleLocations;
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        if (mCarLatLngListMap != null) {
            mCarLatLngListMap.clear();
        }

        if (mMockManager != null) {

            mMockManager.stop();
        }

    }
}
