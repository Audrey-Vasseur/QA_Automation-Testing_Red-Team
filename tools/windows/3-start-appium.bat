@echo off
echo Starting Appium server on port 4723 (keep this window open)...
set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk
set ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk
call appium --address 127.0.0.1 --port 4723 --relaxed-security
pause
