# Building Puff

Use JDK 17, Android SDK platform 35, NDK 27.0.12077973 and CMake 3.22.1. Use a checkout path without spaces. Gradle wrapper 8.11.1 is included.

```sh
sdkmanager 'platforms;android-35' 'build-tools;35.0.0' 'ndk;27.0.12077973' 'cmake;3.22.1'
printf 'sdk.dir=%s\n' "$ANDROID_SDK_ROOT" > local.properties
./gradlew testReleaseUnitTest assembleRelease
```

This builds ARM32 (older TVs), ARM64 and x86_64 in one APK. Output: `app/build/outputs/apk/release/app-release-unsigned.apk`. Dependencies are downloaded on the first build. Android components are resolved from Google/Maven Central. OpenSSL 3.5.5 prebuilts (SHA-256 pinned per architecture) are downloaded from ViaDuck over HTTPS by its vendored CMake integration.

To produce an installable release, create your own keystore with `keytool`, then sign the aligned APK with Android SDK `apksigner`. Never commit the keystore, passwords or `local.properties`. The public Puff release signing key is not included. An APK signed with a different key cannot update the official release in place.

Gradle also supports `storeFile`, `storePassword`, `keyAlias`, and `keyPassword` in your ignored `local.properties` if you choose to use its local release signing configuration.

Native code and JNI keep the upstream `io.github.jqssun.airplay` namespace. The installed application ID is `dev.local.tvbootcleaner` to retain settings and the local ADB authorization from earlier Puff builds.

OpenSSL prebuilt sources/build recipe: https://github.com/viaduck/openssl-cmake and https://github.com/openssl/openssl/tree/openssl-3.5.5. Vendored UxPlay, libplist and ALAC sources are under `app/src/main/cpp/third_party`; their licenses remain there.

A GitHub Actions build/test template is provided at `ci/github-actions-build.yml`. It is not active in this repository; an account with workflow publishing permission can place it under `.github/workflows/`.
