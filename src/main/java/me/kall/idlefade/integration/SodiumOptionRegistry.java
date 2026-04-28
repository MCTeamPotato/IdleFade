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
        var enable = SodiumOptions.boolOption("config.idlefade.enable_fade", true, () -> FadeConfig.getInstance().fadable(), (value) -> FadeConfig.getInstance().setFadable(value), null);
        var tick = SodiumOptions.intOption("config.idlefade.fade_wait", true, () -> FadeConfig.getInstance().ticksBeforeFade() / 20, (intValue) -> FadeConfig.getInstance().setTicksBeforeFade(intValue * 20), 1, 100, 1, null);
        event.addPage(SodiumOptions.newPage("config.idlefade.page", SodiumOptions.newGroup(IdleFade.MOD_ID, enable, tick)));
    }
}
