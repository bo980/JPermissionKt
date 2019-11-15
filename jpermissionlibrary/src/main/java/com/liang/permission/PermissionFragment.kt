package com.liang.permission

import android.app.Activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.liang.permission.utils.permissionOption
import com.liang.permission.utils.release

import java.util.Arrays

class PermissionFragment : Fragment(), Request {

    private val permissionRequest: PermissionRequest by lazy {
        PermissionRequestImp()
    }
    private val permissionResult: PermissionResult by lazy {
        PermissionResultImp()
    }

    private val permissionOption: PermissionOption by lazy {
        permissionOption().apply {
            activity?.let {
                if (permissions.isNullOrEmpty()) {
                    it.supportFragmentManager.beginTransaction().remove(this@PermissionFragment)
                        .commit()
                } else {
                    permissionRequest.checkPermissions(
                        it,
                        permissions!!,
                        this@PermissionFragment
                    )
                }
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        activity?.let {
            permissionRequest.onRequestPermissionsResult(
                it,
                requestCode,
                permissions,
                grantResults
            )
            it.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
        }
    }

    override fun onPermissionUntreated(permissions: Array<String>) {
        permissionRequest.requestPermissions(this, permissions)
    }

    override fun onPermissionGranted(permissions: Array<String>) {
        permissionResult.onPermissionGranted(permissions)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }

    override fun onPermissionDenied(permissions: Array<String>) {
        permissionResult.onPermissionDenied(permissions)
    }

    override fun onPermissionBanned(permissions: Array<String>) {
        permissionResult.onPermissionBanned(permissions)
    }

}

fun FragmentActivity.injectIfNeededIn() {
    val manager = supportFragmentManager
    manager.beginTransaction().add(PermissionFragment(), "j_permission_fragment_tag").commit()
    manager.executePendingTransactions()
}