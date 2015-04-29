vlc-android-sdk
===============

VLC Android SDK pushed to Maven Central. Primarily used in project tomahawk-android.

https://github.com/tomahawk-player/tomahawk-android

Get it via Maven Central
------------------------
Just add this dependency to your project and you're good to go.

<pre>dependencies {
    compile "de.mrmaffen:vlc-android-sdk:1.0.5"</br>
}</pre>

Building libvlcjni.so yourself
------------------------------

To build a fresh version of libvlcjni.so, please follow these steps:

* Set `$ANDROID_SDK` to point to your Android SDK directory

  ```export ANDROID_SDK=/path/to/android-sdk```

* Set `$ANDROID_NDK` to point to your Android NDK directory

  ```export ANDROID_NDK=/path/to/android-ndk```

* Add some useful binaries to your `$PATH`

  ```export PATH=$PATH:$ANDROID_SDK/platform-tools:$ANDROID_SDK/tools```

* Export the ABI for your device (one of armeabi-v7a, armeabi, x86 or mips):

  ```export ANDROID_ABI=armeabi-v7a```

* Run gradle:

  ```./gradlew updateVlc```

For more information on compiling VLC for use on Android see: https://wiki.videolan.org/AndroidCompile
