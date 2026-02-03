# --- DEBUG SETTINGS ---

# the log shows the correct line number in code.

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- TENSORFLOW LITE ---
# Ignores warnings about GPU classes that may not be in the classpath
-dontwarn org.tensorflow.lite.gpu.**
-keep class org.tensorflow.lite.gpu.** { *; }

# Image processing support (MNIST)
-dontwarn org.tensorflow.lite.support.**
-keep class org.tensorflow.lite.support.** { *; }

# Keeps the base TF Lite classes to avoid compilation errors
-keep class org.tensorflow.lite.** { *; }