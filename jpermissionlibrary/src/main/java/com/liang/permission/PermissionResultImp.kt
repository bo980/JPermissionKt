package com.liang.permission

import android.content.Context
import android.util.Log

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import com.liang.permission.annotation.JPermission
import com.liang.permission.annotation.JPermissionBanned
import com.liang.permission.annotation.JPermissionDenied
import com.liang.permission.utils.permissionOption
import com.liang.permission.utils.release
import com.liang.permission.utils.requestPermissionsResult

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class PermissionResultImp : PermissionResult() {

    @Pointcut("execution(@com.liang.permission.annotation.JPermission * *(..))")//方法切入点
    fun requestPermissionMethod() {
    }

    @Around("requestPermissionMethod()")
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint) {

        var context: Context? = null

        when (val `object` = joinPoint.getThis()) {
            is Context -> context = `object`
            is Fragment -> context = `object`.activity
            is android.app.Fragment -> context = `object`.activity
        }

        val signature = joinPoint.signature as MethodSignature
        val permission = signature.method.getAnnotation(JPermission::class.java)

        if (context == null || permission == null) {
            return
        }

       permissionOption().apply {
            proceedingJoinPoint = joinPoint
            permissions = permission.value as Array<String>
            permissionListener = null
        }.release()

        permissionOption().apply {
            Log.e(
                "permissionOption",
                "permissionOption:${proceedingJoinPoint}"
            )
        }.release()

        if (context is FragmentActivity) {
            context.injectIfNeededIn()
            return
        }

        context.startPermissionActivity()
    }

    override fun onPermissionGranted(permissions: Array<String>) {
        requestPermissionsResult()
    }

    override fun onPermissionDenied(permissions: Array<String>) {
        requestPermissionsResult(permissions, JPermissionDenied::class.java)
    }

    override fun onPermissionBanned(permissions: Array<String>) {
        requestPermissionsResult(permissions, JPermissionBanned::class.java)
    }
}
