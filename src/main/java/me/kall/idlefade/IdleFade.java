package me.kall.idlefade;

import me.kall.idlefade.event.FadeCenter;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(IdleFade.MOD_ID)
public final class IdleFade {
    public static final String MOD_ID = "idlefade";

    public IdleFade(FMLJavaModLoadingContext context) {
        if (!FMLLoader.getDist().isClient()) return;
        FadeCenter.register();
    }
}
