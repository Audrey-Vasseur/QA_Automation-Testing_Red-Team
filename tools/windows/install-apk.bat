@echo off
rem Install WordPress APK on emulated phone
set ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
"%ADB%" install -r -g "%USERPROFILE%\wordpress-apk\wpandroid-27.7.apk"
pause
