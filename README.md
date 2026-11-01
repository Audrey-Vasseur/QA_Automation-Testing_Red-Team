# Appium Android - Automation Testing - Red Team

## I. How to configure the project
<details>
<summary>More informations below</summary>
<br>
  
→ You must have clone the repository before doing all of this. <br>
→ You must create an account for WordPress if you don't already own one

### 1) WordPress apk download
Download the WordPress apk in the GitHub releases:
```
https://github.com/wordpress-mobile/WordPress-Android/releases
```
Use this version:
```
wpandroid-27.7.apk
```

-------------------------------------------------------------------

### 2) Appium Installation
Open a new terminal in IntelliJ and type the command below to install Appium:
```
npm install -g appium
```
------------------------------------------------------------------
### 3) Android Studio Installation
Install Android studio on your computer via this link below:
```
https://developer.android.com/studio?hl=fr
```

Open it, click on *More Actions* and then on *SDK Manager*. You need to check some things:
- go to the *SDK Platforms tab*, you must have ticked the **Android 17.0("CinnamonBun")** version
- go to the *SDK Tools tab* and check that Android SDK Platforms-Tools is ticked
Apply the changes if there are some.

Now, look at the top of the *SDK Manager* window, you should see a field named **Android SDK Location**: copy this path and keep it. You will need it a bit later.

Now, open the Windows Menu and search environment variables. Then, click on environment variables and in the next window, look for user variables in the top and click on new. 
```
- name of the variable: ANDROID_HOME
- value of the variable: [the path you previously copied]
```
Confirm the input
Still on user variables, look for the Path variable. Click on it and then on modify. Click on new and write:
```
%ANDROID_HOME%\platform-tools
```
Confirm 

Now, go back to Android Studio, click on *More Actions* then on *Virtual Device Manager*. Here, we will create a fictious phone to do our testing.
- Click on + (or Create Virtual Device)
- Select Pixel 9
- Select the version (Android 17.0 we chose before)
- Let the name set by default
- Finish

A virtual phone should appear on the list. Click on the triangle to turn on the phone. Let it open and go back to IntelliJ's terminal to execute this command : 
```
adb devices
```
You should see a line with **emulator-5554 device written**. If so, everything is working as it should.

---------------------------------------------------------------
### 4) UiAutomator2 Driver Installation
You will install the UiAutomator2 Driver but first, be assure that the Appium server is closed (Ctrl+C), then execute the command:
```
appium driver install uiautomator2
```
To check if it worked:
```
appium driver doctor uiautomator2
```
→ You should see: 
```
Diagnostic completed, 0 required fixes needed
```
-----------------------------------------------------------
### 5) To launch Appium
Execute the command below in the terminal to launch the server Appium:
```
appium
```
→ You should see in the available drivers: uiautomator2

You should have both the virtual phone and appium launched.

--------------------------------------------------------------

### 6) Appium Inspector Installation
Now download Appium inspector from the releases in GitHub with this link below:
```
https://github.com/appium/appium-inspector/releases
```
And precisely this version: 
```
Appium-Inspector-2026.9.2-win-x64.exe
```

While you still have the android emulator and the appium server open, open appium inspector

You now configure the remote host, remote port and remote path as such:
- Remote Host: 127.0.0.1
- Remote Port: 4723
- Remote Path: /
  
Then in the capacity Builder tab, add:

| 1st row | 2nd row | 3rd row
| :----------------------: | :-------: | :----------:
| platformName           |  text   | Android  
| appium:automationName  |  text   | uiautomator2
| appium:appWaitActivity |  text   | *
| appium:appWaitDuration | number  | 30000
| appium:app             |  text   | *[insert the path where you downloaded the apk of the wordpress app]*

Then click on the Start Session Button. WordPress is now launched on your emulated phone

-------------------------------------------------------------------
</details>

## II. How to launch the tests

<details>
<summary>More informations below</summary>
<br>
1: Open Android Studio, go to Virtual Device Manager and start your phone <br>
2: Launch the appium server in IntelliJ's terminal by typing appium <br>
3: Run the tests by clicking on the green arrow <br>
<br>
If you want to work on the tests: <br>
4: Open Appium Inspector too

</details>

## III. UI automation tests (Appium Android – WordPress app)

<details>
<summary>More informations below</summary>
<br>

### 1) What is tested
12 UI test cases on the WordPress Android app 27.7 (posts, post settings, media library, site settings,
self-hosted login form). Full inventory with test data, expected and actual results:
[docs/ui-test-cases.md](docs/ui-test-cases.md). Failure investigation (section 4.3 of the assignment):
[docs/failure-investigation.md](docs/failure-investigation.md).

| Test type | Test |
|---|---|
| Positive | UI-01 create a draft |
| Negative | UI-02 non-WordPress site address |
| Field validation | UI-03 site address format (4 data sets) |
| Boundary | UI-04 "Posts per page" 0 / 1 / 100 / 101 |
| Dropdown | UI-05 post status |
| Date validation | UI-06 future date (scheduled) vs past date (backdated) |
| Dialog | UI-07 delete permanently: cancel / confirm |
| Dynamic element | UI-08 "Post sent to trash" banner + UNDO |
| File validation (accepted) | UI-09 PNG upload |
| State verification + regression | UI-10 edit a draft, content is kept |
| Data-driven | UI-11 5 titles from `src/test/resources/testdata/post_titles.csv` |
| Failure-oriented | UI-12 fake image is rejected ("Upload Failed") |

### 2) Project structure (UI part)
```
src/test/java/
├── core/      AppConfig (configuration), DriverFactory (one Appium driver per thread),
│              DevicePool (one emulator per running test), BaseUiTest (setup / teardown)
├── pages/     Page Object Model: one class per screen (MySitePage, PostsListPage, EditorPage,
│              PostSettingsPage, PublishDatePage, MediaLibraryPage, SiteSettingsPage, SelfHostedLoginPage)
│   └── components/  reusable widgets: ConfirmDialog, Snackbar
├── ui/        test classes (UI-01 ... UI-12)
└── utils/     TestData (unique names), PostCleanup, DeviceFiles (push test files), Evidence (screenshots)
src/test/resources/
├── config.example.properties   configuration template (no secret)
├── junit-platform.properties   parallel execution settings
└── testdata/                   CSV for the data-driven test + image files for upload tests
tools/windows/                  setup scripts (SDK, emulator, Appium, APK)
```

### 3) Design choices
- **Page Object Model**: tests never use locators; they call business methods (`postsList.trashPost(title)`).
- **Locators**: Android `resource-id` first, then `accessibilityId`, visible text only on screens without ids
  (Jetpack Compose login screen); no absolute XPath, no coordinates.
- **Synchronization**: explicit waits only (`WebDriverWait` in `BasePage`), no `Thread.sleep` in tests.
  A random "Jetpack" promotion pop-up is closed automatically (`AppPopups`).
- **Independence**: each test opens its own Appium session, creates its own data with a unique name and
  deletes it in `@AfterEach` / `finally`. No test depends on another one.
- **No secrets in code**: configuration comes from `config.properties` (git-ignored) or environment variables.
- **Evidence**: on failure, a screenshot and the screen hierarchy (XML) are saved in `evidence/`.

### 4) Configure
1. Follow part I (Android SDK, emulator, Appium + uiautomator2). On Windows, `tools/windows/1-setup.bat` does it automatically.
2. Install the APK on the running emulator: `adb install -r -g wpandroid-27.7.apk` (or `tools/windows/install-apk.bat`).
3. Open the WordPress app on the emulator and log in with a **test** WordPress.com account that owns one free site.
4. Optional: copy `src/test/resources/config.example.properties` to `src/test/resources/config.properties`
   (ignored by git) to change the defaults, e.g. `devices` (see `adb devices`). No password is needed.

### 5) Run
Emulator and Appium (`appium --relaxed-security`, needed to push test files) must be running.
```
mvn test -Dtest="ui.*Test"                 # all UI tests
mvn test -Dtest=PostCreationTest           # one class
mvn test -Dtest=PostTitleDataDrivenTest    # data-driven test (one report line per CSV row)
```
Reports: `target/surefire-reports/` (one XML/TXT file per class). Evidence of failures: `evidence/`.

### 6) Parallel execution
JUnit 5 runs test **classes** in parallel (`junit-platform.properties`, 2 threads). Each thread gets its own
emulator from `DevicePool` (own `udid` and `systemPort`) and its own driver (`ThreadLocal`).
To run 2 tests at the same time, start a second emulator and list both in `config.properties`:
```
devices=emulator-5554,emulator-5556
```
With a single emulator, tests simply wait for the free device (no conflict, but no real concurrency).

**Limitation (documented as allowed by section 7 of the assignment):** an Android device can run only one
Appium/UiAutomator2 session at a time, and each emulator needs its own WordPress login. Our demonstration
machine runs one emulator, so the demo shows the parallel configuration (2 JUnit threads, `ThreadLocal`
driver, `DevicePool`, unique test data) and the tests waiting for the device; adding a second emulator to
`devices=` is the only change needed to run two UI tests at the same time.

</details>

## IV. Individual contributions

<details>
<summary>More informations below</summary>
<br>  
  
| Name | Role | Tasks
| :-----------------------: | :-------: | :----------:
| Logan Comble              |       /   |      /  
| Alekseï Mollet-Zielinski  | UI automation | UI framework, UI tests UI-01 to UI-12, UI test data, parallel configuration, UI documentation (details below) 
| Théo Neveu                |       /   |      /
| Audrey Vasseur            | QA Automation Lead |      /  
| William Willemart         | UI automation | UI framework, UI tests UI-01 to UI-12, UI test data, parallel configuration, UI documentation (details below)

### William Willemart & Alekseï Zielinski – UI automation

**Area:** UI automation of the WordPress Android app (framework + tests + documentation).

**Files / modules I contributed to**

| Module | Files | Work done |
|---|---|---|
| Framework core | `src/test/java/core/` – `AppConfig`, `DriverFactory`, `DevicePool`, `BaseUiTest` | configuration without secrets in code, one Appium driver per thread (`ThreadLocal`), one emulator per running test, common setup / teardown |
| Page objects | `src/test/java/pages/` – `BasePage`, `MySitePage`, `PostsListPage`, `EditorPage`, `PostSettingsPage`, `PublishDatePage`, `MediaLibraryPage`, `SiteSettingsPage`, `SelfHostedLoginPage`, `AppPopups`, `components/ConfirmDialog`, `components/Snackbar` | Page Object Model, locator strategy (resource-id / accessibilityId), explicit waits, keyboard typing for the Gutenberg editor |
| UI tests | `src/test/java/ui/` – `PostCreationTest`, `PostSettingsTest`, `PostDeletionTest`, `MediaUploadTest`, `SiteSettingsBoundaryTest`, `SelfHostedLoginTest`, `PostTitleDataDrivenTest` | 12 test cases: positive, negative, field validation, boundary, dropdown, date, dialog, dynamic element, file upload accepted / rejected, state verification, data-driven |
| Utilities | `src/test/java/utils/` – `TestData`, `PostCleanup`, `DeviceFiles`, `Evidence`, `EvidenceOnFailure` | unique test data, cleanup after each test, pushing test images to the phone, screenshots + XML on failure |
| Test data & config | `src/test/resources/` – `testdata/post_titles.csv`, `testdata/files/`, `config.example.properties`, `junit-platform.properties` | CSV for the data-driven test, valid / invalid image files, configuration template, parallel execution settings |
| Tooling & docs | `tools/windows/`, `docs/ui-test-cases.md`, `docs/failure-investigation.md`, README part III, `pom.xml` (selenium-support, surefire) | Windows setup scripts, test case inventory, failure investigation, how to run the UI tests |

**What I can explain during the demonstration**
- How a test runs: `BaseUiTest` opens a session, the test uses page objects, `@AfterEach` deletes the test's data and closes the session.
- The locator strategy and why there is no absolute XPath and no `Thread.sleep` (explicit `WebDriverWait`).
- Every UI test (UI-01 to UI-12): its data, its steps and why each assertion is needed.
- The data-driven test (`@ParameterizedTest` + `@CsvFileSource`): one report line per CSV row.
- Parallel execution: `junit-platform.properties`, `ThreadLocal` driver, `DevicePool`, unique data – and the one-emulator limitation.
- The failure investigations (stale element, typing synchronization, data-driven rows): detection, root cause, fix, verification.

</details>
