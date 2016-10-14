    #指定代码的压缩级别
    -optimizationpasses 5

    #包名不混合大小写
    -dontusemixedcaseclassnames

    #不去忽略非公共的库类
    -dontskipnonpubliclibraryclasses

    #优化/不优化输入的类文件
    -dontoptimize

    #预校验
    -dontpreverify

    #混淆时是否记录日志
    -verbose

    #混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

    #保护注解
    -keepattributes *Annotation*

    #保持哪些类不被混淆
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService
    #如果有引用v4包可以添加下面这行
    #-keep public class * extends android.support.v4.app.Fragment

    #忽略警告
    -ignorewarning

    -keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(...);
    }

    #保持native方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }

    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }

    #保持自定义控件类不被混淆
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
    }

    #保持Parcelable不被混淆
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }

    #保持Serializable不被混淆
    -keepnames class * implements java.io.Serializable

    #保持Serializable不被混淆并且enum类也不被混淆
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

    -keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }

    #不混淆资源类
    -keepclassmembers class **.R$* {
        public static <fields>;
    }

    #避免混淆泛型(如果混淆报错建议关掉)
    #–keepattributes Signature

    #如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
    #gson
    #-libraryjars libs/gson-2.2.2.jar
    -keepattributes Signature
    # Gson specific classes
    -keep class sun.misc.Unsafe { *; }
    # Application classes that will be serialized/deserialized over Gson
    -keep class com.google.gson.examples.android.model.** { *; }


    ######记录生成的日志数据,gradle build时在本项目根目录输出######

    #apk包内所有class的内部结构
    -dump class_files.txt
    #未混淆的类和成员
    -printseeds seeds.txt
    #列出从apk中删除的代码
    -printusage unused.txt
    #混淆前后的映射
    -printmapping mapping.txt

    ######记录生成的日志数据,gradle build时在本项目根目录输出######


    ######项目特殊处理代码######
    #银联
    #保护第三方jar包(Android Studio设置了会报错)
    #-libraryjars libs/UPPayAssistEx.jar
    #忽略警告
    -dontwarn com.unionpay.**
    #不想混淆keep掉
    -keep class com.unionpay.** { *; }

    #友盟
    -keep class com.umeng.**{ *; }

    #腾讯
    -keep class com.tencent.**{ *; }

    #支付宝
    -keep class com.alipay.** { *; }

    -dontwarn android.support.**
    -dontwarn com.flurry.**
    -dontwarn com.paypal.**
    -dontwarn org.lucasr.**
    -dontwarn org.android.agoo.ut.impl.**
    #结束
    ######项目特殊处理代码######