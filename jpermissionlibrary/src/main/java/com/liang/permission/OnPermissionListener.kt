package com.liang.permission

interface OnPermissionListener {
    fun onPermissionGranted()

    fun onPermissionDenied(permissions: Array<String>)

    fun onPermissionBanned(permissions: Array<String>)
}
