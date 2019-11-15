package com.liang.permission.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log


import androidx.collection.SimpleArrayMap
import androidx.core.app.ActivityCompat
import androidx.core.util.Pools
import com.liang.permission.*

import com.liang.permission.annotation.JPermissionBanned
import com.liang.permission.annotation.JPermissionDenied
import java.lang.reflect.InvocationTargetException

/**
 * 判断单个权限是否同意
 *
 * @param context    context
 * @param permission permission
 * @return return true if permission granted
 */
fun Context.hasSelfPermission(permission: String): Boolean {
    Log.e(
        "hasSelfPermission",
        "permission........${ActivityCompat.checkSelfPermission(this, permission)}"
    )
    return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.goPermissionRequest(
    permissions: Array<String>,
    permissionListener: OnPermissionListener
) {
    permissionOption().apply {
        this.proceedingJoinPoint = null
        this.permissions = permissions
        this.permissionListener = permissionListener
    }.release()
    startPermissionActivity()
}

fun requestPermissionsResult() {
    Log.e("PermissionsResult", "....")
    permissionOption().apply {
        Log.e("permissionOption", "permissionOption....${permissions}")
        proceedingJoinPoint?.let {
            Log.e("proceedingJoinPoint", "....${proceedingJoinPoint.toString()}")
            try {
                it.proceed()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
        permissionListener?.let {
            it.onPermissionGranted()
        }
        reset()
    }.release()
}

fun <T : Annotation> requestPermissionsResult(
    permissions: Array<String>,
    clazz: Class<T>
) {

    permissionOption().apply {
        proceedingJoinPoint?.let {
            val `object` = it.getThis()
            val cls = `object`.javaClass
            val methods = cls.declaredMethods

            if (methods.isEmpty()) return

            for (method in methods) {
                //过滤不含自定义注解的方法
                val isHasAnnotation = method.isAnnotationPresent(clazz)
                if (isHasAnnotation) {
                    method.isAccessible = true
                    //获取方法类型
                    val types = method.parameterTypes
                    if (types.size != 1) return
                    //获取方法上的注解
                    method.getAnnotation(clazz) ?: return
                    //解析注解上对应的信息
                    try {
                        method.invoke(`object`, permissions)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    }

                }
            }
        }

        permissionListener?.let {
            if (clazz == JPermissionDenied::class.java) {
                it.onPermissionDenied(permissions)
            }
            if (clazz == JPermissionBanned::class.java) {
                it.onPermissionBanned(permissions)
            }
        }

        reset()
    }.release()

}

val optionPool: Pools.SimplePool<PermissionOption> = Pools.SimplePool(1)

fun permissionOption(): PermissionOption {
    return optionPool.acquire() ?: PermissionOption()
}

fun PermissionOption.release() {
    optionPool.release(this)
}
