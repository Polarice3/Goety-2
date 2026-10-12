package com.Polarice3.Goety.client.gui.overlay;

import net.minecraft.util.Mth;

public class SoulChangePopup {
    public static final int DURATION = 40;
    public static final int MERGE_WINDOW = 30;

    private static int AMOUNT;
    private static int TICKS_LEFT;
    private static int PREV_TICKS_LEFT;

    public static void push(int change) {
        if (change == 0) {
            return;
        }
        boolean sameSign = TICKS_LEFT > DURATION - MERGE_WINDOW && Integer.signum(change) == Integer.signum(AMOUNT);
        AMOUNT = sameSign ? AMOUNT + change : change;
        TICKS_LEFT = DURATION;
        PREV_TICKS_LEFT = DURATION;
    }

    public static void tick() {
        PREV_TICKS_LEFT = TICKS_LEFT;
        if (TICKS_LEFT > 0) {
            --TICKS_LEFT;
        }
    }

    public static void clear() {
        AMOUNT = 0;
        TICKS_LEFT = 0;
        PREV_TICKS_LEFT = 0;
    }

    public static boolean isActive() {
        return TICKS_LEFT > 0 && AMOUNT != 0;
    }

    public static int getAmount() {
        return AMOUNT;
    }

    public static float progress(float partialTicks) {
        float t = Mth.lerp(partialTicks, PREV_TICKS_LEFT, TICKS_LEFT);
        return Mth.clamp(1.0F - (t / DURATION), 0.0F, 1.0F);
    }
}
