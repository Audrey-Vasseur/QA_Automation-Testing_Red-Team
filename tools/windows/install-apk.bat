@echo off
rem Installs the WordPress APK on the running emulator
set ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
"%ADB%" install -r -g "%USERPROFILE%\wordpress-apk\wpandroid-27.7.apk"
pause
