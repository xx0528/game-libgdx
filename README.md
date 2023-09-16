# game-libgdx

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff).

This project was generated with a Kotlin project template that includes Kotlin application launchers and [KTX](https://libktx.github.io/) utilities.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.
- `android`: Android mobile platform. Needs Android SDK.
- `ios`: iOS mobile platform using RoboVM.

## Gradle

This project uses [Gradle](http://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.


此项目 在go里判断逻辑跳转
编译go lib 库
使用powershell进入 game-libgdx/go 目录
执行 build_android_on_mac.sh
生成各个版本的libadd.so 和 libadd.h 文件
将libadd.so 放到 game-libgdx/android/lib目录下
将libadd.h文件复制到 game-libgdx/android/src/main/jni下
jni下的jni.c 是java调到c的接口，libadd.h 又是go给c准备的接口

JniLibrary.java 文件是声明的jni函数，还有加载lib

game-libgdx/server 是server，客户端访问的接口，接口可返回加密数据
每次编译server都会产生配置文件 config.json 有源和加密完的数据
每次启动加密数据会不一样 但用go解密后是一样的
所有配置字符串都是加密，然后go解密，就是 android 里也是 go

有时 生成的 libadd.h 会有报错 32位的问题 改成64位
typedef char _check_for_32_bit_pointer_matching_GoInt[sizeof(void*)==32/8 ? 1:-1];
typedef char _check_for_64_bit_pointer_matching_GoInt[sizeof(void*)==64/8 ? 1:-1];
