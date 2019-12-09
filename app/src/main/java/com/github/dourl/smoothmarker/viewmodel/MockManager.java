package com.github.dourl.smoothmarker.viewmodel;

import com.github.dourl.app_lib_soomthmarker.model.CarLatLng;
import com.github.dourl.smoothmarker.AppExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: douruanliang
 * @date: 2019-12-09
 */
public class MockManager {
    private Executor executor = AppExecutor.getInstance().networkIO();
    private static int DELAY = 100; // ms
    private List<CarLatLng> carLatLngs = null;
    private int messageIndex = 0;

    private MockEventListener listener;
    private MockRunnable task;


    public MockManager() {
        if (carLatLngs == null) {
            carLatLngs = new ArrayList<>();
        }

        initData();
    }

    private void initData() {
        readLatLngs();
    }


    public void start() {
        task = new MockRunnable();
        executor.execute(task);
    }


    /**
     * 自己优化 方案
     */
    public void stop() {
        if (task != null) {
            task.stop();
        }
    }

    /**
     * 读取坐标点
     *
     * @return
     */
    private List<CarLatLng> readLatLngs() {
        for (int i = 0; i < coords2.length; i += 2) {

            carLatLngs.add(new CarLatLng("120000", coords2[i + 1], coords2[i]));
        }
        return carLatLngs;
    }


    public interface MockEventListener {
        void onRemoteVehicleInfoEvent(CarLatLng latLng);
    }

    class MockRunnable implements Runnable {
        AtomicBoolean isRunning = new AtomicBoolean(true);

        @Override
        public void run() {
            while (isRunning.get() && messageIndex < carLatLngs.size()) {

                System.out.println(carLatLngs.size()+"carLatLngs.size()");
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (listener != null) {
                    CarLatLng event = carLatLngs.get(messageIndex);
                    listener.onRemoteVehicleInfoEvent(event);
                    messageIndex++;
                }


            }
        }

        public void stop() {
            isRunning.set(false);
        }
    }


    public void setListener(MockEventListener listener) {
        this.listener = listener;
    }

    /**
     * 模拟坐标点  官网上的坐标 截取一节
     */
    private double[] coords2 = {116.34821304891533, 39.977652209132174, 116.34820923399242,
            39.977764016531076, 116.3482045955917, 39.97786190186833,
            116.34822159449203, 39.977958856930286, 116.3482256370537,
            39.97807288885813, 116.3482098441266, 39.978170063673524,
            116.34819564465377, 39.978266951404066, 116.34820541974412,
            39.978380693859116, 116.34819672351216, 39.97848741209275,
            116.34816588867105, 39.978593409607825, 116.34818489339459,
            39.97870216883567, 116.34818473446943, 39.978797222300166,
            116.34817728972234, 39.978893492422685, 116.34816491505472,
            39.978997133775266, 116.34815408537773, 39.97911413849568,
            116.34812908154862, 39.97920553614499, 116.34809495907906,
            39.979308267469264, 116.34805113358091, 39.97939658036473,
            116.3480310509613, 39.979491697188685, 116.3480082124968,
            39.979588529006875, 116.34799530586834, 39.979685789111635,
            116.34798818413954, 39.979801430587926, 116.3479996420353,
            39.97990758587515, 116.34798697544538, 39.980000796262615,
            116.3479912988137, 39.980116318796085, 116.34799204219203,
            39.98021407403913, 116.34798535084123, 39.980325006125696,
            116.34797702460183, 39.98042511477518, 116.34796288754136,
            39.98054129336908, 116.34797509821901, 39.980656820423505,
            116.34793922017285, 39.98074576792626, 116.34792586413015,
            39.98085620772756, 116.3478962642899, 39.98098214824056,
            116.34782449883967, 39.98108306010269, 116.34774758827285,
            39.98115277119176, 116.34761476652932, 39.98115430642997,
            116.34749135408349, 39.98114590845294, 116.34734772765582,
            39.98114337322547, 116.34722082902628, 39.98115066909245,
            116.34708205250223, 39.98114532232906, 116.346963237696,
            39.98112245161927, 116.34681500222743, 39.981136637759604,
            116.34669622104072, 39.981146248090866, 116.34658043260109,
            39.98112495260716, 116.34643721418927, 39.9811107163792,
            116.34631638374302, 39.981085081075676, 116.34614782996252,
            39.98108046779486, 116.3460256053666, 39.981049089345206,
            116.34588814050122, 39.98104839362087, 116.34575119741586,
            39.9810544889668, 116.34562885420186, 39.981040940565734,
            116.34549232235582, 39.98105271658809, 116.34537348820508,
            39.981052294975264, 116.3453513775533, 39.980956549928244
    };

}
