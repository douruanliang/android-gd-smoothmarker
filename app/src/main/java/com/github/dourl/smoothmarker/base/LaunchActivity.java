package com.github.dourl.smoothmarker.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.github.dourl.smoothmarker.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * @author: douruanliang
 * @date: 2019-11-29
 */

@RuntimePermissions
public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LaunchActivityPermissionsDispatcher.callPermissionWithPermissionCheck(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LaunchActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @TargetApi(23)
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void callPermission() {

        // TODO  做你想做的 just do it
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * 第一次拒绝后,再次请求权限时，对需要的权限的解释
     *
     * @param request
     */
    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showWhy(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setTitle("权限请求提示")
                .setMessage("提示：本应用需要获取定位、存储权限，否则无法正常使用。")
                .setPositiveButton("授权", (dialog, which) -> {
                    request.proceed();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    request.cancel();
                    dialog.dismiss();
                })
                .show();
    }

    // 4 当用户拒绝获取权限的提示
    @SuppressLint("WrongConstant")
    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDenied() {
        Toast.makeText(this, "应用无法获取相应权限", Toast.LENGTH_SHORT).show();
    }

    // 5 当用户勾选不再提示并且拒绝的时候调用的方法
    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void never() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限设置提示");
        builder.setMessage("提示：应用权限被拒绝,为了不影响您的正常使用，请在 '设置-权限'中开启对应权限。");
        builder.setNegativeButton("取消", ((dialog, which) -> {
            dialog.dismiss();
            finish();
        }));
        builder.setPositiveButton("设置", (dialog, which) -> {
            startAppSettings();
        });
        builder.show();
    }


    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
