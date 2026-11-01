@echo off
echo Starting virtual device wp_pixel (keep this window open)...
"%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe" -avd wp_pixel -no-snapshot-save
pause
