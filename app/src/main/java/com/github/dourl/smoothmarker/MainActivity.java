package com.github.dourl.smoothmarker;

import android.os.Bundle;
import android.util.Log;

import com.github.dourl.app_lib_soomthmarker.AmapFragment;
import com.github.dourl.app_lib_soomthmarker.manager.SMManager;
import com.github.dourl.app_lib_soomthmarker.model.CarLatLng;
import com.github.dourl.smoothmarker.base.BaseActivity;
import com.github.dourl.smoothmarker.viewmodel.CarViewModel;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private SMManager mSMManager;
    private CarViewModel mCarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addFragment(R.id.map_view_layout, new AmapFragment());
        mCarViewModel = ViewModelProviders.of(this).get(CarViewModel.class);

    }

    @Override
    protected void onResume() {
        super.onResume();


        // TODO  为啥要发到 onResume 回调例
        if (mSMManager == null) {
            mSMManager = new SMManager();
        }

        mCarViewModel.getVehicleLocation(this).observe(this, new Observer<List<CarLatLng>>() {
            @Override
            public void onChanged(List<CarLatLng> carLatLngs) {
                Log.d(TAG, carLatLngs.size() + "");
                mSMManager.addLatLngByCarID(carLatLngs);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSMManager.onDestroySmoothMove();
    }
}
