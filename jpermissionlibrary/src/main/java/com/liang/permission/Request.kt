package com.liang.permission

interface Request {
    fun onPermissionUntreated(permissions: Array<String>)

    fun onPermissionGranted(permissions: Array<String>)

    fun onPermissionDenied(permissions: Array<String>)

    fun onPermissionBanned(permissions: Array<String>)
}
