package dev.liquidglass.xposed;

/** Tweak these, rebuild. Lower blur = smoother on weak GPUs (e.g. Helio G99). */
final class GlassConfig {
    private GlassConfig() {}

    /** Blur of the whole screen behind a dialog, in pixels. 0 disables. */
    static final int BLUR_BEHIND_PX = 32;
    /** Dim behind the dialog (stock is ~0.6, glass looks better with less). */
    static final float DIM_AMOUNT = 0.15f;
    /** Glass panel corner radius. */
    static final float CORNER_DP = 28f;
    /** Rim highlight width. */
    static final float RIM_DP = 1.2f;
}
