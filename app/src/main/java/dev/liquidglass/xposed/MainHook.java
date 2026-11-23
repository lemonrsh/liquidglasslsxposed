package dev.liquidglass.xposed;

import android.app.Dialog;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    private static final String SELF = "dev.liquidglass.xposed";
    private static boolean hooked;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (SELF.equals(lpparam.packageName)) return;
        synchronized (MainHook.class) {
            if (hooked) return;                         // one hook per process
            hooked = true;
        }
        try {
            // Every framework/AndroidX/Material dialog goes through Dialog.show().
            XposedHelpers.findAndHookMethod(Dialog.class, "show", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        GlassStyler.apply((Dialog) param.thisObject);
                    } catch (Throwable t) {
                        XposedBridge.log("LiquidGlass: " + t);
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("LiquidGlass hook failed in " + lpparam.packageName + ": " + t);
        }
    }
}
