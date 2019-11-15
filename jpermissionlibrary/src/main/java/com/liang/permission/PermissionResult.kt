package com.liang.permission


abstract class PermissionResult {

    abstract fun onPermissionGranted(permissions: Array<String>)

    abstract fun onPermissionDenied(permissions: Array<String>)

    abstract fun onPermissionBanned(permissions: Array<String>)
}
