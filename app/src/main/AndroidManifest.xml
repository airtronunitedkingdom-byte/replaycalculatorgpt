# Replay Calculator — Native Android

A standalone **Java Android app**. No Replit, Expo, React Native, ads, login, analytics, or internet permission.

## Features
- Arithmetic, parentheses, and percent postfix (e.g. `50%` means `0.5`)
- Named saved calculations; replay by editing each numeric operand
- On-device history (up to 100 entries)
- All app data stored in Android SharedPreferences; app backup disabled

## Build with Android Studio
1. Install the latest stable Android Studio and Android SDK 35.
2. **File > Open** and select this extracted project folder.
3. Allow Gradle sync. Android Studio may offer to install missing SDK components.
4. **Build > Build Bundle(s) / APK(s) > Build APK(s)** for testing.
5. Install APK on an Android phone and test carefully.
6. For Google Play, **Build > Generate Signed Bundle / APK > Android App Bundle**, create/store a signing keystore securely and build `release`.

The ZIP does not include a Gradle wrapper binary or a compiled APK/AAB. Android Studio can use its installed Gradle support; if it requests a wrapper, configure Gradle 8.9 for Android Gradle Plugin 8.7.3. Java 17 required.

## Publishing checklist
- Keep application ID `com.replaycalculator.app` consistent with the Play Console app.
- Keep the **same signing key** for future updates and increment `versionCode` each release.
- Verify Data safety and privacy policy against the **actual release build**, including any SDKs added later.
- Confirm Google Play target SDK requirements at time of submission; update Gradle/targetSdk as needed.
- Complete Android phone testing and Google Play testing requirements before production.

## Important
This is a **new native implementation**, not a byte-for-byte port of the old Replit app. UI and some behaviors may differ. Code has not been compiled or device-tested in this environment. Saved formulas/history from the old app are not automatically migrated.

## Maintenance
Store this source in a private GitHub repository, commit every change, and tag releases (`v1.0.0`, `v1.0.1`, etc.). Share the repository URL for future changes.
