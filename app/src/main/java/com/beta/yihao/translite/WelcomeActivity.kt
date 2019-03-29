package com.beta.yihao.translite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * 欢迎界面，主要处理权限申请逻辑
 */
class WelcomeActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 0x10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_about_layout)
        //申请必要的权限
        initPermission()
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    private fun initPermission() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )

        val toApplyList = ArrayList<String>()

        for (perm in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                // 进入到这里代表没有权限.
                toApplyList.add(perm)
            }
        }
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toTypedArray(), 123)
        } else {
            gotoMainActivityDelayed(2000)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // 动态授权的回调，对授权结果进行处理

        //组合权限名称和申请结果，并筛选出用户未授权的权限
        val list = grantResults.zip(permissions).filter {
            it.first == -1
        }
        if (list.isNotEmpty()) {//存在用户未授权权限
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.description_permission))
                .setPositiveButton("ok") { dialog, _ ->
                    //用户选择了勾选后不在提示选项
                    val b = shouldShowRequestPermissionRationale(list.first().second)
                    if (!b) {
                        //打开设置界面，让用户授权权限
                        goToAppSetting()
                    } else {
                        //弹窗授权
                        initPermission()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    //未同意，退出应用
                    dialog.dismiss()
                    finish()
                }
                .setCancelable(false)
                .create()
                .show()
        } else {
            gotoMainActivityDelayed(2000)
        }

    }

    // 跳转到当前应用的设置界面
    private fun goToAppSetting() {
        val intent = Intent()

        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CODE)
    }

    //延时跳转到主界面
    private fun gotoMainActivityDelayed(millis: Long) {
        CoroutineScope(Main).launch {
            delay(millis)
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }
    }

    //从设置界面的回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            initPermission()
        }
    }
}