vlc-android-sdk
===============

VLC Android SDK pushed to Maven Central. Primarily used in project tomahawk-android.

https://github.com/tomahawk-player/tomahawk-android

Get it via Maven Central
------------------------
Just add this dependency to your project and you're good to go.

<pre>dependencies {
    compile "de.mrmaffen:vlc-android-sdk:3.0.0"
}</pre>

Building the LibVLC Android SDK yourself
----------------------------------------

To build a fresh version of the LibVLC Android SDK please make sure that you have setup your machine correctly
(You can ignore the steps after and including 'Building'): https://wiki.videolan.org/AndroidCompile

Afterwards simply run this Gradle command:
```./gradlew buildVlc```
  
The VLC-Android and the VLC repo will now get pulled if they haven't been previously.
After that's done the compilation process for the ABIs armeabi-v7a, x86 and armeabi gets started.
Finally the resulting .java files will be copied over into the src/main/java folder of this project.
The resulting .so files will be copied into src/main/jniLibs.

Building a specific version of the LibVLC Android SDK       
-----------------------------------------------------

If you want to build a specific version (maybe you want a major stable release) you have to 
checkout the vlc-android git repository at the corresponding commit:
```
cd vlc-android          // if this folder doesn't exist yet, simply run ./gradlew cloneVlcAndroid
git tag                 // to list all release versions
git checkout {tag-name} // to checkout the git repo at the given tag
cd ..
./gradlew buildVlc     // build it        
```