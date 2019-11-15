package com.liang.permission

import android.app.Activity
import android.content.Context

abstract class PermissionRequest {

    abstract fun checkPermissions(activity: Context, permissions: Array<String>, listener: Request)

    abstract fun requestPermissions(`object`: Any, permissions: Array<String>)

    abstract fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    )

    companion object {
        val TAG = PermissionRequest::class.java.simpleName
    }
}
