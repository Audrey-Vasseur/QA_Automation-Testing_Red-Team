# ---------------------------------------------------------------------------
#  Installation de l'environnement Appium Android (Windows) - projet Red Team
#  Installe : SDK Android (ligne de commande), emulateur, image Android,
#             Appium + driver UiAutomator2, APK WordPress 27.7
#  Le script peut etre relance sans risque : il saute ce qui est deja installe.
# ---------------------------------------------------------------------------
$ErrorActionPreference = "Continue"   # les outils natifs ecrivent sur stderr sans etre en erreur
$ProgressPreference = "SilentlyContinue"   # accelere fortement Invoke-WebRequest

$ToolsDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$SdkRoot    = Join-Path $env:LOCALAPPDATA "Android\Sdk"
$ApiLevel   = "36"
$SystemImg  = "system-images;android-$ApiLevel;google_apis;x86_64"
$AvdName    = "wp_pixel"
$ApkDir     = Join-Path $env:USERPROFILE "wordpress-apk"
$ApkPath    = Join-Path $ApkDir "wpandroid-27.7.apk"
$ApkUrl     = "https://github.com/wordpress-mobile/WordPress-Android/releases/download/27.7/wpandroid-27.7.apk"
$CmdToolsUrl = "https://dl.google.com/android/repository/commandlinetools-win-13114758_latest.zip"

function Step($msg) { Write-Host ""; Write-Host "==> $msg" -ForegroundColor Cyan }
function Ok($msg)   { Write-Host "    OK : $msg" -ForegroundColor Green }

function Add-UserPath($dir) {
    $current = [Environment]::GetEnvironmentVariable("Path", "User")
    if (-not $current) { $current = "" }
    if (($current -split ";") -notcontains $dir) {
        [Environment]::SetEnvironmentVariable("Path", ($current.TrimEnd(";") + ";" + $dir), "User")
    }
    if (($env:Path -split ";") -notcontains $dir) { $env:Path = $env:Path + ";" + $dir }
}

# 1) Java -------------------------------------------------------------------
Step "Java (JAVA_HOME)"
if (-not $env:JAVA_HOME -or -not (Test-Path $env:JAVA_HOME)) {
    $jdk = Get-ChildItem (Join-Path $env:USERPROFILE ".jdks") -Directory -ErrorAction SilentlyContinue |
           Sort-Object Name -Descending | Select-Object -First 1
    if (-not $jdk) { throw "Aucun JDK trouve dans $env:USERPROFILE\.jdks. Installe un JDK depuis IntelliJ (File > Project Structure > SDKs)." }
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $jdk.FullName, "User")
    $env:JAVA_HOME = $jdk.FullName
}
Add-UserPath (Join-Path $env:JAVA_HOME "bin")
Ok "JAVA_HOME = $env:JAVA_HOME"

# 2) Node.js ----------------------------------------------------------------
Step "Node.js"
if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    Write-Host "    Node.js absent, installation via winget..."
    winget install -e --id OpenJS.NodeJS.LTS --accept-source-agreements
    $env:Path = [Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [Environment]::GetEnvironmentVariable("Path", "User")
}
Ok ("node " + (node -v))

# 3) SDK Android en ligne de commande ---------------------------------------
Step "SDK Android ($SdkRoot)"
$sdkManager = Join-Path $SdkRoot "cmdline-tools\latest\bin\sdkmanager.bat"
if (-not (Test-Path $sdkManager)) {
    New-Item -ItemType Directory -Force -Path $SdkRoot | Out-Null
    $zip = Join-Path $env:TEMP "android-cmdline-tools.zip"
    Write-Host "    Telechargement des command-line tools..."
    Invoke-WebRequest $CmdToolsUrl -OutFile $zip
    $extract = Join-Path $env:TEMP "android-cmdline-tools"
    if (Test-Path $extract) { Remove-Item $extract -Recurse -Force }
    Expand-Archive $zip -DestinationPath $extract
    New-Item -ItemType Directory -Force -Path (Join-Path $SdkRoot "cmdline-tools") | Out-Null
    Move-Item (Join-Path $extract "cmdline-tools") (Join-Path $SdkRoot "cmdline-tools\latest")
}
[Environment]::SetEnvironmentVariable("ANDROID_HOME", $SdkRoot, "User")
[Environment]::SetEnvironmentVariable("ANDROID_SDK_ROOT", $SdkRoot, "User")
$env:ANDROID_HOME = $SdkRoot; $env:ANDROID_SDK_ROOT = $SdkRoot
Add-UserPath (Join-Path $SdkRoot "platform-tools")
Add-UserPath (Join-Path $SdkRoot "emulator")
Add-UserPath (Join-Path $SdkRoot "cmdline-tools\latest\bin")
Ok "ANDROID_HOME = $SdkRoot"

Step "Licences du SDK Android : reponds 'y' a chaque question"
& $sdkManager --sdk_root=$SdkRoot --licenses

Step "Installation des paquets Android (plusieurs Go, patience)"
& $sdkManager --sdk_root=$SdkRoot "platform-tools" "emulator" "platforms;android-$ApiLevel" $SystemImg
Ok "Paquets installes"

# 4) Telephone virtuel -------------------------------------------------------
Step "Telephone virtuel '$AvdName'"
$avdManager = Join-Path $SdkRoot "cmdline-tools\latest\bin\avdmanager.bat"
$existing = & $avdManager list avd -c
if ($existing -notcontains $AvdName) {
    "no" | & $avdManager create avd -n $AvdName -k $SystemImg -d "pixel_7"
}
# Active le clavier du PC dans le telephone virtuel
$avdConfig = Join-Path $env:USERPROFILE ".android\avd\$AvdName.avd\config.ini"
if (Test-Path $avdConfig) { (Get-Content $avdConfig) -replace '^hw.keyboard = no', 'hw.keyboard = yes' | Set-Content $avdConfig }
Ok "AVD $AvdName pret"

# 5) Appium -----------------------------------------------------------------
Step "Appium + driver UiAutomator2"
if (-not (Get-Command appium -ErrorAction SilentlyContinue)) {
    npm install -g appium
}
$drivers = (appium driver list --installed 2>&1) | Out-String
if ($drivers -notmatch "uiautomator2") { appium driver install uiautomator2 }
Ok ("appium " + (appium -v))

# 6) APK WordPress ------------------------------------------------------------
Step "APK WordPress 27.7"
if (-not (Test-Path $ApkPath)) {
    New-Item -ItemType Directory -Force -Path $ApkDir | Out-Null
    Write-Host "    Telechargement (~300 Mo)..."
    Invoke-WebRequest $ApkUrl -OutFile $ApkPath
}
Ok "APK : $ApkPath"

Step "Verification finale"
appium driver doctor uiautomator2
& (Join-Path $SdkRoot "emulator\emulator.exe") -accel-check

Write-Host ""
Write-Host "Installation terminee. Ferme cette fenetre puis lance 2-start-emulator.bat et 3-start-appium.bat." -ForegroundColor Green
