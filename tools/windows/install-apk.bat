@echo off
rem Installe l'APK WordPress sur l'emulateur demarre
set ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
"%ADB%" install -r -g "%USERPROFILE%\wordpress-apk\wpandroid-27.7.apk"
pause
