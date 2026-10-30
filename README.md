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
If you want to work on the test: <br>
4: Open Appium Inspector

</details>

## III. Individual contributions

<details>
<summary>More informations below</summary>
<br>  
  
| Name | Role | Tasks
| :-----------------------: | :-------: | :----------:
| Logan Comble              |       /   |      /  
| Alekseï Mollet-Zielinski |       /   |      /  
| Théo Neveu                |       /   |      /
| Audrey Vasseur            |       /   |      /  
| William Willemart         |       /   |      /  

</details>
