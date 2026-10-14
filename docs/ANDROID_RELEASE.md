# Replay Calculator — Android release signing (Windows)

The Android app is native Java, offline, and uses application ID `com.replaycalculator.app`.

## 1. Create your private upload keystore (once only)
Open PowerShell on your own Windows computer. Install JDK 17 if `keytool` is unavailable.

```powershell
New-Item -ItemType Directory -Force "$HOME\ReplayCalculatorPrivate" | Out-Null
keytool -genkeypair -v -keystore "$HOME\ReplayCalculatorPrivate\replay-upload.jks" -alias replay-upload -keyalg RSA -keysize 3072 -validity 10000
```

Enter and remember your passwords. Never commit the `.jks` file, passwords, or base64 text to GitHub or chat. Back up the keystore in a secure second location. This is the **upload key**; use Google Play App Signing for the app signing key.

## 2. Convert keystore to base64 locally
```powershell
$bytes = [System.IO.File]::ReadAllBytes("$HOME\ReplayCalculatorPrivate\replay-upload.jks")
[System.IO.File]::WriteAllText("$HOME\ReplayCalculatorPrivate\replay-upload-base64.txt", [Convert]::ToBase64String($bytes))
```
The base64 file is just as sensitive as the keystore. Delete it after configuring GitHub Secrets.

## 3. Add four repository Actions secrets
Open https://github.com/airtronunitedkingdom-byte/replaycalculatorgpt/settings/secrets/actions and choose **New repository secret** for each:
- `ANDROID_KEYSTORE_BASE64`: complete content of `replay-upload-base64.txt`
- `ANDROID_KEYSTORE_PASSWORD`: keystore password
- `ANDROID_KEY_ALIAS`: `replay-upload`
- `ANDROID_KEY_PASSWORD`: key password (often the same as keystore password)

Do not paste secrets into chat.

## 4. Trigger signed release
Open https://github.com/airtronunitedkingdom-byte/replaycalculatorgpt/actions/workflows/android-build.yml, choose **Run workflow**, select `main`, then **Run workflow**.

Download artifact `ReplayCalculator-signed-release-aab` after a successful run. Ordinary pushes only run tests and produce a debug APK. A manual release without configured secrets fails intentionally.

## 5. Google Play Console
Create the app in Play Console, opt into Google Play App Signing, and upload the signed `.aab` to an appropriate testing track. Complete store listing, privacy/data-safety declarations, app-content questions and testing requirements applicable to the account. Do not submit before testing the signed build. Increase `versionCode` for subsequent updates.
