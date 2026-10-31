@echo off
echo Launching Appium server 
set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk
set ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk
call appium --address 127.0.0.1 --port 4723 --relaxed-security
pause
