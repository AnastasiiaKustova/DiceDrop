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
# Строки падений с номерами: расшифровываются через mapping.txt этой сборки
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Сохранения лежат в JSON, Gson читает их по именам полей и значений enum.
# Конструкторы тоже нужны: через них подставляются значения по умолчанию для старых сохранений
-keep class com.example.dicedrop.domain.entity.** { *; }

# Gson определяет тип списка через дженерик наследника TypeToken
-keepattributes Signature,InnerClasses,EnclosingMethod
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
