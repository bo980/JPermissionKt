package com.liang.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

import com.liang.permission.utils.hasSelfPermission

import java.util.ArrayList

class PermissionRequestImp : PermissionRequest() {

    private var request: Request? = null

    override fun checkPermissions(context: Context, permissions: Array<String>, request: Request) {
        Log.e("requestPermissions","permissions:${permissions.size}")
        this.request = request
        val denied = ArrayList<String>()
        permissions.forEach {
            if (!context.hasSelfPermission(it)) {
                denied.add(it)
            }
        }

        if (denied.isNotEmpty()) {
            Log.e("requestPermissions","PERMISSION_UNTREATED........")
            callbackRequestResult(PERMISSION_UNTREATED, *denied.toTypedArray())
            return
        }

        callbackRequestResult(PERMISSION_GRANTED, *permissions)
        Log.e("requestPermissions","PERMISSION_GRANTED........")
    }

    override fun requestPermissions(`object`: Any, permissions: Array<String>) {
        Log.e("requestPermissions","........")
        if (`object` is PermissionActivity) {
            ActivityCompat.requestPermissions(`object`, permissions, REQUEST_CODE)
        }

        if (`object` is PermissionFragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                `object`.requestPermissions(permissions, REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQUEST_CODE) {
            return
        }
        if (grantResults.isNotEmpty()) {
            val denied = ArrayList<String>()
            val banned = ArrayList<String>()
            for (j in grantResults.indices) {
                if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            permissions[j]
                        )
                    ) {
                        banned.add(permissions[j])
                    } else {
                        denied.add(permissions[j])
                    }
                }
            }

            if (banned.isNotEmpty()) {
                callbackRequestResult(PERMISSION_BANNED, *banned.toTypedArray())
                return
            }

            if (denied.isNotEmpty()) {
                callbackRequestResult(PERMISSION_DENIED, *denied.toTypedArray())
                return
            }
            callbackRequestResult(PERMISSION_GRANTED, *permissions)
        } else {
            callbackRequestResult(PERMISSION_BANNED, *permissions)
        }
    }

    private fun callbackRequestResult(permissionType: Int, vararg permissions: String) {
        request?.let {
            when (permissionType) {
                PERMISSION_UNTREATED -> it.onPermissionUntreated(permissions as Array<String>)
                PERMISSION_GRANTED -> it.onPermissionGranted(permissions as Array<String>)
                PERMISSION_DENIED -> it.onPermissionDenied(permissions as Array<String>)
                PERMISSION_BANNED -> it.onPermissionBanned(permissions as Array<String>)
            }
        }

    }

    companion object {

        private const val REQUEST_CODE = 0x200
        private const val PERMISSION_UNTREATED = 1
        private const val PERMISSION_GRANTED = 2
        private const val PERMISSION_DENIED = 3
        private const val PERMISSION_BANNED = 4
    }
}
