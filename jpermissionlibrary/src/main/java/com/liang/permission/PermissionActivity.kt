package com.liang.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity

import com.liang.permission.utils.permissionOption
import com.liang.permission.utils.release

import java.util.Arrays

class PermissionActivity : AppCompatActivity(), Request {

    private val permissionRequest: PermissionRequest by lazy {
        PermissionRequestImp()
    }
    private val resultHelper: PermissionResult by lazy {
        PermissionResultImp()
    }

    private val permissionOption: PermissionOption by lazy {
        permissionOption().apply {

            if (permissions.isNullOrEmpty()) {
                finish()
            } else {
                permissionRequest.checkPermissions(
                    this@PermissionActivity,
                    permissions!!,
                    this@PermissionActivity
                )
            }

            Log.d(
                javaClass.simpleName, "permissions: ${if (permissions.isNullOrEmpty()) {
                    "permissions is null or empty"
                } else {
                    Arrays.toString(permissions)
                }}"
            )

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionOption.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionRequest.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onPermissionUntreated(permissions: Array<String>) {
        permissionRequest.requestPermissions(this, permissions)
    }

    override fun onPermissionGranted(permissions: Array<String>) {
        resultHelper.onPermissionGranted(permissions)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onPermissionDenied(permissions: Array<String>) {
        resultHelper.onPermissionDenied(permissions)
    }

    override fun onPermissionBanned(permissions: Array<String>) {
        resultHelper.onPermissionBanned(permissions)
    }

}

fun Context.startPermissionActivity() {
    val intent = Intent(this, PermissionActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
    if (this is Activity) overridePendingTransition(0, 0)
}