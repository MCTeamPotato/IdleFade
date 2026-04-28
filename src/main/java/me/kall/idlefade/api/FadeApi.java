package me.kall.idlefade.api;

import me.kall.idlefade.config.FadeConfig;
import net.minecraft.resources.ResourceLocation;

public interface FadeApi {
    static FadeApi getInstance() {
        return FadeConfig.getInstance();
    }

    void setUnfadable(ResourceLocation id, boolean unfadable);
}
