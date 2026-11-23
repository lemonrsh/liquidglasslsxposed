package dev.liquidglass.xposed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Translucent "glass" panel: vertical tint gradient, diagonal bright rim,
 * and a soft specular highlight along the top edge. The blur itself comes
 * from the window's blur-behind, so this only draws the surface.
 */
final class GlassDrawable extends Drawable {
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rim = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gloss = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final Path clip = new Path();
    private final float radius;
    private final boolean dark;

    GlassDrawable(Context c, boolean dark) {
        float d = c.getResources().getDisplayMetrics().density;
        this.radius = GlassConfig.CORNER_DP * d;
        this.dark = dark;
        rim.setStyle(Paint.Style.STROKE);
        rim.setStrokeWidth(GlassConfig.RIM_DP * d);
    }

    @Override
    protected void onBoundsChange(Rect b) {
        float half = rim.getStrokeWidth() / 2f;
        rect.set(b);
        rect.inset(half, half);
        clip.reset();
        clip.addRoundRect(rect, radius, radius, Path.Direction.CW);

        fill.setShader(new LinearGradient(0, b.top, 0, b.bottom,
                dark ? 0x70181C22 : 0x66FFFFFF,
                dark ? 0x55181C22 : 0x4DFFFFFF,
                Shader.TileMode.CLAMP));

        int hi = dark ? 0x80FFFFFF : 0xE6FFFFFF;
        rim.setShader(new LinearGradient(b.left, b.top, b.right, b.bottom,
                new int[]{hi, 0x14FFFFFF, 0x59FFFFFF},
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP));

        gloss.setShader(new RadialGradient(b.exactCenterX(), b.top,
                Math.max(1f, b.width() * 0.75f),
                dark ? 0x30FFFFFF : 0x55FFFFFF, 0x00FFFFFF,
                Shader.TileMode.CLAMP));
    }

    @Override
    public void draw(Canvas c) {
        if (rect.isEmpty()) return;
        c.drawRoundRect(rect, radius, radius, fill);
        int save = c.save();
        c.clipPath(clip);
        Rect b = getBounds();
        c.drawRect(b.left, b.top, b.right, b.top + b.height() * 0.45f, gloss);
        c.restoreToCount(save);
        c.drawRoundRect(rect, radius, radius, rim);
    }

    @Override public void setAlpha(int alpha) { /* fixed look */ }
    @Override public void setColorFilter(ColorFilter cf) { /* fixed look */ }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
