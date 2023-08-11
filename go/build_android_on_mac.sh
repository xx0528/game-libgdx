export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/21.0.6113669
###
 # @Author: xx
 # @Date: 2023-08-11 18:38:46
 # @LastEditTime: 2023-08-11 19:37:27
 # @Description: 
### 

export GOARCH=arm
export GOOS=android
export CGO_ENABLED=1
export CC=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/windows-x86_64/bin/armv7a-linux-androideabi21-clang
go build -buildmode=c-shared -o output/android/armeabi-v7a/libadd.so

echo "Build armeabi-v7a success"

export GOARCH=arm64
export GOOS=android
export CGO_ENABLED=1
export CC=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/windows-x86_64/bin/aarch64-linux-android21-clang
go build -buildmode=c-shared -o output/android/arm64-v8a/libadd.so

echo "Build arm64-v8a success"


export GOARCH=386
export GOOS=android
export CGO_ENABLED=1
export CC=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/windows-x86_64/bin/i686-linux-android21-clang
go build -buildmode=c-shared -o output/android/x86/libadd.so

echo "Build x86 success"


export GOARCH=amd64
export GOOS=android
export CGO_ENABLED=1
export CC=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/windows-x86_64/bin/x86_64-linux-android21-clang
go build -buildmode=c-shared -o output/android/x86_64/libadd.so

echo "Build x86_64 success"
