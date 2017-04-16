package com.itant.zhuling.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.Method;

/**
 * 权限工具类
 * http://blog.csdn.net/zhangquanit/article/details/52735791
 */
/*
用例
PermissionUtil.needPermission(this,PER_CAMERA, Manifest.permission.CAMERA);

@PermissionSuccess(requestCode = PER_CAMERA)
private void grantPermissionSuccess(){
    Toast.makeText(this,"已获照相机得权限",Toast.LENGTH_SHORT).show();
}

@PermissionFail(requestCode = PER_CAMERA)
private void grantPersmissionFail(){
    Toast.makeText(this,"照相机权限被拒绝",Toast.LENGTH_SHORT).show();
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    PermissionUtil.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
}
*/
public class PermissionUtil {
    public static final int REQUEST_EXTERNAL_STORAGE = 1;//存储权限
    public static final int REQUEST_CALL_PHONE = 2;//播打电话
    public static final int REQUEST_LOCATION = 3;//定位

    public static void needPermission(Fragment context, int reqCode, String... permissions) {
        checkPermission(context, reqCode, permissions);
    }


    public static void needPermission(Activity context, int reqCode, String... permissions) {
        checkPermission(context, reqCode, permissions);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private static void checkPermission(Object context, int reqCode, String... permissions) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //6.0以下
            executeSuccessResult(context, reqCode);
            return;
        }
        boolean granted = hasPermission(context, permissions);//检查权限
        if (granted) {
            executeSuccessResult(context, reqCode);
        } else {
            if (context instanceof Fragment) {
                ((Fragment) context).requestPermissions(permissions, reqCode);
            } else {
                ((Activity) context).requestPermissions(permissions, reqCode);
            }
        }
    }

    private static void executeSuccessResult(Object context, int reqCode) {
        Method successMethod = getTargetMethod(context, reqCode, PermissionSuccess.class);
        try {
            successMethod.invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeFailResult(Object context, int reqCode) {
        Method successMethod = getTargetMethod(context, reqCode, PermissionFail.class);
        try {
            successMethod.invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Method getTargetMethod(Object context, int reqCode, Class annotation) {
        Method[] declaredMethods = context.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!method.isAccessible()) {
                method.setAccessible(true); //私有的方法必须强制
            }
            //判断方法上是否使用了目标注解
            boolean annotationPresent = method.isAnnotationPresent(annotation);
            if (annotationPresent) {
                if (isTargetMethod(method, reqCode, annotation)) { //比较requestCode是否相等
                    return method;
                }
            }
        }
        return null;
    }

    private static boolean isTargetMethod(Method method, int reqCode, Class cls) {
        if (cls.equals(PermissionSuccess.class)) {
            return reqCode == method.getAnnotation(PermissionSuccess.class).requestCode();
        } else if (cls.equals(PermissionFail.class)) {
            return reqCode == method.getAnnotation(PermissionFail.class).requestCode();
        }
        return false;
    }


    private static boolean hasPermission(Object context, String... permissions) {
        Activity activity = null;
        if (context instanceof Fragment) {
            activity = ((Fragment) context).getActivity();
        } else {
            activity = (Activity) context;
        }
        for (String permission : permissions) {
            int granted = ContextCompat.checkSelfPermission(activity, permission);
            if (granted == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public static void onRequestPermissionsResult(Fragment context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handlePermissionsResult(context, requestCode, permissions, grantResults);
    }


    public static void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handlePermissionsResult(context, requestCode, permissions, grantResults);
    }

    private static void handlePermissionsResult(Object context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = true;
        for (int grant : grantResults) {
            if (grant == PackageManager.PERMISSION_DENIED) {
                permissionGranted = false;
                break;
            }
        }
        if (permissionGranted) {
            //获得权限
            executeSuccessResult(context, requestCode);
        } else {
            //权限被用户拒绝
            executeFailResult(context, requestCode);
        }
    }
}
