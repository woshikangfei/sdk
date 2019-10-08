
-keepattributes Signature,InnerClasses,*Annotation*
-ignorewarnings
-dontshrink
-dontoptimize
#解决 Can't find common super class of [com/analytics/sdk/e/a/a] (with 1 known super classes) and [com/analytics/sdk/e/a/j] (with 1 known super classes
-dontpreverify

# 将.class信息中的类名重新定义为"sdk"字符串
-renamesourcefileattribute adsdk
# 并保留源文件名为"sdk"字符串，而非原始的类名 并保留行号
# 将出错的源文件的类名定义为xcm,然后保留行号,如果不加renamesourcefileattribute，则会显示全类名和错误行号
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(int,android.content.Context,android.os.Handler);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}


-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers enum  *,* {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

#广点通
-keep class com.qq.e.** {
    public protected *;
}
-keep class android.support.v4.**{
    public *;
}
-keep class android.support.v7.**{
    public *;
}

-keep class com.androidquery.** {*;}
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep class com.androidquery.callback.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}

-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}

-keep class com.baidu.mobads.*.** { *; }

-keepattributes Exceptions,InnerClasses,Signature,*Annotation*
-keepnames class * implements java.io.Serializable


-keep class com.google.gson.** {*;}
-keep class com.alibaba.fastjson.** {*;}

-keep class com.analytics.sdk.*.** {*;}

-dontwarn com.alibaba.fastjson.**
-dontwarn com.baidu.**
-dontwarn com.qq.e.**
-dontwarn com.google.gson.**
-dontwarn com.base.**
-dontwarn com.sdk.**
-dontwarn javassist.**
-dontwarn com.androidquery.**