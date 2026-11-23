package dev.liquidglass.xposed;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

final class GlassStyler {
    private GlassStyler() {}

    private static final Set<Window> DONE =
            Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<Window, Boolean>()));

    static void apply(Dialog dialog) {
        Window w = dialog.getWindow();
        if (w == null) return;
        View decor = w.peekDecorView();
        if (decor == null) return;
        if (!DONE.add(w)) return;                       // already styled

        Context c = dialog.getContext();

        // Respect system state: blur disabled (battery saver, dev option, no GPU support).
        WindowManager wm = c.getSystemService(WindowManager.class);
        if (wm == null || !wm.isCrossWindowBlurEnabled()) return;
        ActivityManager am = c.getSystemService(ActivityManager.class);
        if (am != null && am.isLowRamDevice()) return;

        // 1) Blur the screen behind the dialog (iOS-style).
        WindowManager.LayoutParams a = w.getAttributes();
        if (GlassConfig.BLUR_BEHIND_PX > 0) {
            a.flags |= WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            a.setBlurBehindRadius(GlassConfig.BLUR_BEHIND_PX);
        }
        a.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        a.dimAmount = Math.min(a.dimAmount > 0 ? a.dimAmount : GlassConfig.DIM_AMOUNT,
                GlassConfig.DIM_AMOUNT);
        w.setAttributes(a);

        // 2) Swap the dialog's panel background for the glass surface.
        //    Skip full-screen windows and windows with a transparent background
        //    (bottom sheets etc.), where a panel would cover everything.
        boolean fullScreen = a.width == WindowManager.LayoutParams.MATCH_PARENT
                && a.height == WindowManager.LayoutParams.MATCH_PARENT;
        Drawable orig = decor.getBackground();
        boolean transparent = orig == null
                || (orig instanceof ColorDrawable && ((ColorDrawable) orig).getAlpha() == 0);
        if (fullScreen || transparent) return;

        Rect pad = new Rect();
        orig.getPadding(pad);                           // keeps original margins + content padding
        boolean night = (c.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        Drawable glass = new GlassDrawable(c, night);
        w.setBackgroundDrawable(pad.isEmpty()
                ? glass
                : new InsetDrawable(glass, pad.left, pad.top, pad.right, pad.bottom));
    }
}
