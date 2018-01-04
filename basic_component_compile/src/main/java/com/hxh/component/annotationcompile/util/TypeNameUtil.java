package com.hxh.component.annotationcompile.util;

import com.squareup.javapoet.ClassName;

/**
 * Created by hxh on 2017/7/5.
 */
public class TypeNameUtil {
    public static ClassName ANDROID_VIEW = ClassName.get("android.view","View");
    public static ClassName ANDROID_ACTIVITY = ClassName.get("android.app","Activity");
    public static ClassName ANDROID_ACTIVITYCOMPAT = ClassName.get("android.app","Activity");
    public static ClassName ANDROID_INTENT = ClassName.get("android.content","Intent");


    public static ClassName ANDROID_CONTEXT = ClassName.get("android.content","Context");
    public static ClassName ANDROID_PARCEABLE = ClassName.get("android.os","Parcelable");
    public static ClassName ANDROID_SERIALIZABLE = ClassName.get("java.io","Serializable");


    public static ClassName rx_observable = ClassName.get("rx","Observable");
    public static ClassName baseapi = ClassName.get("com.hxh.component.basicore.net","BaseAPI");
    public static ClassName rxutils = ClassName.get("com.hxh.component.basicore.rx","RxUtils");

    public static ClassName convertFactory = ClassName.get("com.hxh.component.basicore.net","ConverterFactory");

    public static ClassName my_App = ClassName.get("workai.empassist.com.xiaoaiwidget.app","App");

    public static ClassName my_CoreLib = ClassName.get("com.hxh.component.basicore","CoreLib");
}
