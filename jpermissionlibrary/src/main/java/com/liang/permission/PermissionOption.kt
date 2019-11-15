package com.liang.permission

import org.aspectj.lang.ProceedingJoinPoint

import java.util.Arrays

class PermissionOption {
    var proceedingJoinPoint: ProceedingJoinPoint? = null
    var permissionListener: OnPermissionListener? = null
    var permissions: Array<String>? = null


    override fun toString(): String {
        return "PermissionOption{ permissions = ${Arrays.toString(permissions)}}"
    }
}

fun PermissionOption.reset() {
    this.apply {
        proceedingJoinPoint = null
        permissionListener = null
        permissions = null
    }
}



