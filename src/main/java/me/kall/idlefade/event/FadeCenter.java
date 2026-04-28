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

    private final Mouse last = new Mouse();

    private int stopTicks = 0;
    private double fadeAlpha = 1.0;

    public static FadeCenter getInstance() {
        return INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(INSTANCE::tickClient);
        MinecraftForge.EVENT_BUS.addListener(INSTANCE::anyInput);
    }

    public boolean hidden() {
        return this.fadeAlpha == 0.0F;
    }

    public double fadeAlpha() {
        return this.fadeAlpha;
    }

    public int modifyAlpha(int color) {
        float fadeAlpha = (float) this.fadeAlpha;
        if (!FadeConfig.getInstance().fadable()) return color;
        if (fadeAlpha == 1.0) return color;
        if (fadeAlpha == 0.0) return (color & 0x00FFFFFF);
        int alpha = (int)(fadeAlpha * 255.0F) & 0xFF;
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

        this.fadeAlpha = this.stopTicks >= FadeConfig.getInstance().tickCountBeforeFade() ? Math.max(0.0, this.fadeAlpha - FadeConfig.getInstance().fadeSpeed()) : Math.min(1.0, this.fadeAlpha + FadeConfig.getInstance().fadeSpeed());
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
