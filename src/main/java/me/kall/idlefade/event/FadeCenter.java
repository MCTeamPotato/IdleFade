package me.kall.idlefade.event;

import me.kall.idlefade.config.FadeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.NotNull;

public class FadeCenter {
    private static final FadeCenter INSTANCE = new FadeCenter();

    private static final float HIDDEN = 0F;
    private static final float FULL = 1F;

    private final Mouse last = new Mouse();

    private int stopTicks = 0;
    private int fadeAlpha = 100;

    public static FadeCenter getInstance() {
        return INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(INSTANCE::tickClient);
        MinecraftForge.EVENT_BUS.addListener(INSTANCE::anyInput);
    }

    public boolean hidden() {
        return this.fadeAlpha == 0;
    }

    public boolean full() {
        return this.fadeAlpha == 100;
    }

    public float fadeAlpha() {
        if (this.full()) return FULL;
        if (this.hidden()) return HIDDEN;
        return (float) this.fadeAlpha / 100F;
    }

    public int modifyAlpha(int color) {
        if (!FadeConfig.getInstance().fadable() || this.full()) return color;
        if (this.hidden()) return (color & 0x00FFFFFF);
        int alpha = (int)(this.fadeAlpha() * 255.0F) & 0xFF;
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    public void tickClient(TickEvent.@NotNull ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getInstance();
        MouseHandler mouseHandler = minecraft.mouseHandler;
        double x = mouseHandler.xpos();
        double y = mouseHandler.ypos();

        if (FadeConfig.getInstance().fadable() && minecraft.level == null && minecraft.player == null && minecraft.screen != null && !this.last.init(x, y)) {
            this.stopTicks = this.last.same(x, y) ? this.stopTicks + 1 : 0;
            this.last.set(x, y);
        } else {
            this.stopTicks = 0;
        }

        int fadeAlpha = this.stopTicks >= FadeConfig.getInstance().tickCountBeforeFade() ? Math.max(0, this.fadeAlpha - FadeConfig.getInstance().fadeSpeed()) : Math.min(100, this.fadeAlpha + FadeConfig.getInstance().fadeSpeed());
        if (this.fadeAlpha != fadeAlpha) {
            System.out.println("FadeAlpha updated from " + this.fadeAlpha + " to " + fadeAlpha);
            this.fadeAlpha = fadeAlpha;
        }
    }

    public void anyInput(InputEvent event) {
        Minecraft.getInstance().execute(() -> this.stopTicks = 0);
    }

    static class Mouse {
        double x = Double.NaN;
        double y = Double.NaN;

        boolean same(double x, double y) {
            return this.x == x && this.y == y;
        }

        boolean init(double x, double y) {
            if (Double.isNaN(this.x) || Double.isNaN(this.y)) {
                this.x = x;
                this.y = y;
                return true;
            }

            return false;
        }

        void set(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
