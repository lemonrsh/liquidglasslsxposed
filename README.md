# Liquid Glass (LSPosed module)

Gives dialogs and system alerts a glass look: blurred screen behind, translucent
panel with rim light and top highlight. Settings in `GlassConfig.java`.

## Build
1. Open this folder in Android Studio (it creates the Gradle wrapper), let it sync.
2. Build > Build APK(s). Install the APK.
   - If `api.xposed.info` is unreachable, download `api-82.jar` into `app/libs/`
     and switch the dependency in `app/build.gradle.kts` to the `files(...)` line.
   - If Studio asks to upgrade AGP/Gradle, accept.

## Enable
1. LSPosed > Modules > Liquid Glass > enable.
2. Scope: `System Framework`, `System UI`, plus any apps you want.
3. Force-stop the ticked apps (reboot for System UI / framework).

## Requirements / troubleshooting
- Android 12+ and window blur enabled. Check:
  `adb shell wm` ... or Developer options > "Disable window-level blurs" (off),
  battery saver off, and `adb shell settings get global disable_window_blurs` -> null or 0.
- Logs: `adb logcat | grep -i LiquidGlass` (Xposed log: LSPosed > Logs).
- Heavy on weak GPUs: lower `BLUR_BEHIND_PX` (e.g. 16-24).

## Limits
- Affects `Dialog`-based UI (alerts, bottom sheets get blur-behind only). Not popup
  menus, not the notification shade/QS, not full app windows.
- No true backdrop refraction; that isn't possible from a userspace hook.
