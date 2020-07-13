# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/sumirrowu/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5

#混淆时不会产生形形色色的类名
-dontusemixedcaseclassnames

#指定不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

#不预校验
#-dontpreverify

#不优化输入的类文件
-dontoptimize

-ignorewarnings

-verbose

#优化
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护内部类
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class com.bytedance.sdk.openadsdk.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}