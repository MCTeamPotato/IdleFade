package me.kall.idlefade.integration;

import me.kall.duplicationless.util.SodiumOptions;
import me.kall.idlefade.IdleFade;
import me.kall.idlefade.config.FadeConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.embeddedt.embeddium.api.OptionGUIConstructionEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IdleFade.MOD_ID)
public class SodiumOptionRegistry {
    @SubscribeEvent
    public static void optionRegistry(@NotNull OptionGUIConstructionEvent event) {
        var enable = SodiumOptions.boolOption(
                "config.idlefade.enable_fade",
                true,
                () -> FadeConfig.getInstance().fadable(),
                (value) -> FadeConfig.getInstance().jsonConfig.put(FadeConfig.KEY_ENABLE_FADE, value).saveToFile(), null
        );
        var tick = SodiumOptions.intOption(
                "config.idlefade.tick_count_before_fade",
                true,
                () -> FadeConfig.getInstance().tickCountBeforeFade(),
                (intValue) -> FadeConfig.getInstance().jsonConfig.put(FadeConfig.KEY_TICK_COUNT_BEFORE_FADE, intValue).saveToFile(),
                20, 240, 20, null
        );
        var speed = SodiumOptions.intOption(
                "config.idlefade.fade_speed",
                true,
                () -> (int) (FadeConfig.getInstance().fadeSpeed() * 100),
                (intValue) -> FadeConfig.getInstance().jsonConfig.put(FadeConfig.KEY_FADE_SPEED, (double) intValue / 100D).saveToFile(),
                1, 100, 3, null
        );
        event.addPage(SodiumOptions.newPage("config.idlefade.page", SodiumOptions.newGroup(IdleFade.MOD_ID, enable, tick, speed)));
    }
}
