# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#阿里云播放器
#-keep class com.alivc.player.**{*;}
#-keep class com.aliyun.clientinforeport.**{*;}

-keep class anetwork.**{*;}
-keep class com.taobao.**{*;}
-keep class cn.**{*;}
-keep class com.alibaba.**{*;}
-keep class com.baidu.**{*;}
-keep class com.mob.**{*;}
-keep class io.**{*;}
-keep class org.**{*;}
-keep class anet.**{*;}
-keep class com.bumptech.**{*;}
-keep class com.google.**{*;}
-keep class com.tencent.**{*;}
-keep class com.zhihu.**{*;}
-dontwarn anetwork.**
-dontwarn com.taobao.**
-dontwarn cn.**
-dontwarn com.alibaba.**
-dontwarn com.baidu.**
-dontwarn com.mob.**
-dontwarn io.**
-dontwarn org.**
-dontwarn anet.**
-dontwarn com.bumptech.**
-dontwarn com.google.**
-dontwarn com.tencent.**
-dontwarn com.zhihu.**
-dontwarn com.qmuiteam.**


-keep class com.aliyun.vodplayer.**{*;}
-keep class com.alivc.**{*;}
-keep class com.aliyun.**{*;}
-keep class com.cicada.**{*;}
-dontwarn com.alivc.**
-dontwarn com.aliyun.**
-dontwarn com.cicada.**

#shareSDK
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-keep class m.framework.**{*;}
-keep class com.bytedance.**{*;}
#-dontwarn cn.sharesdk.**
#-dontwarn com.sina.**
#-dontwarn com.mob.**
#-dontwarn **.R$*

#高德地图
#3D 地图 V5.0.0之后：
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.amap.api.trace.**{*;}
#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
#搜索
#-keep   class com.amap.api.services.**{*;}
#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

# X5
#-dontwarn dalvik.**
#-dontwarn com.tencent.smtt.**
-keep class com.tencent.smtt.** { *;}
-keep class com.tencent.tbs.** { *;}


-keep class com.shuangling.software.entity.** { *;}


#GreenDao
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties { *; }
# If you DO use SQLCipher:
#-keep class org.greenrobot.greendao.database.SqlCipherEncryptedHelper { *; }
-keep class org.greenrobot.greendao.** { *; }
-keep class net.sqlcipher.database.**{ *; }
-keep class rx.**{ *; }
#-dontwarn rx.**
#-dontwarn org.greenrobot.greendao.database.**
# If you do NOT use SQLCipher:
#-dontwarn net.sqlcipher.database.**
# If you do NOT use RxJava:


#############################################
#
# 第三方混淆规则
#
#############################################

# 百度地图混淆配置
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}

# ButterKnife混淆配置
-keep class butterknife.** { *; }
#-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# OkHttp3混淆配置
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
#-dontwarn okio.**

# Retrofit2混淆配置
#-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# RxJava、RxAndroid混淆配置
#-dontwarn sun.misc.**
#-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
#   long producerIndex;
#   long consumerIndex;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode producerNode;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode consumerNode;
#}

# Glide混淆配置
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Picasso混淆配置
-keep class com.parse.*{ *; }
#-dontwarn com.parse.**
#-dontwarn com.squareup.picasso.**
-keepclasseswithmembernames class * {
    native <methods>;
}

# Fastjson混淆配置
#-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

# Gson混淆配置
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }


# 高徳地图混淆配置
#-dontwarn com.amap.api.**
#-dontwarn com.a.a.**
#-dontwarn com.autonavi.**
-keep class com.amap.api.**  {*;}
-keep class com.autonavi.**  {*;}
-keep class com.a.a.**  {*;}

# tinker
#-dontwarn com.tencent.tinker.**
#-keep class com.tencent.tinker.** { *; }
#-keep class androidx.**{*;}

#EventBus 3.0.x
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# androidx的混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
#-dontwarn com.google.android.material.**
#-dontnote com.google.android.material.**
#-dontwarn androidx.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
#-ignorewarnings
#-dontwarn com.squareup.picasso.**
#-dontwarn com.bumptech.glide.**
#-dontwarn com.baidu.**
#-dontwarn com.tencent.**
#-dontwarn org.webrtc.**
#百度移动统计
-keep class com.baidu.mobstat.** { *; }
-keep class com.baidu.bottom.** { *; }
#Sentry
-keepattributes LineNumberTable,SourceFile
#-dontwarn org.slf4j.**
#-dontwarn javax.**

-keep class com.taobao.** #{*;}
-keep class com.alibaba.** #{*;}
-keep class com.ta.**{*;}
-keep class com.ut.**{*;}
#-dontwarn com.taobao.**
#-dontwarn com.alibaba.**
#-dontwarn com.ta.**
#-dontwarn com.ut.**


#QMUI
#-keep class **_FragmentFinder { *; }
#-keep class androidx.fragment.app.* { *; }
#-keep class com.qmuiteam.qmui.arch.record.RecordIdClassMap { *; }
#-keep class com.qmuiteam.qmui.arch.record.RecordIdClassMapImpl { *; }
#-keep class com.qmuiteam.qmui.arch.scheme.SchemeMap {*;}
#-keep class com.qmuiteam.qmui.arch.scheme.SchemeMapImpl {*;}
-keep class com.qmuiteam.qmui.** {*;}

#android-database-sqlcipher
#-keep class net.sqlcipher.database.SQLiteDatabase
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }

-keepclassmembers class * implements java.io.Serializable {*;}

#zxing
-keep class com.google.zxing.** { *; }

