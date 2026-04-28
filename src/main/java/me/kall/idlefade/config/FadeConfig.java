package me.kall.idlefade.config;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.kall.duplicationless.config.JsonConfig;
import me.kall.idlefade.IdleFade;
import me.kall.idlefade.api.FadeApi;
import net.minecraft.resources.ResourceLocation;

public class FadeConfig implements FadeApi {
    private static final FadeConfig INSTANCE = new FadeConfig();
    private static final String CONFIG_VERSION = "1.0.0";

    public final JsonConfig jsonConfig;

    private final ObjectOpenHashSet<ResourceLocation> unfadableResourcesCache = new ObjectOpenHashSet<>();

    public static final String KEY_ENABLE_FADE = "enableFade";
    public static final String KEY_TICK_COUNT_BEFORE_FADE = "tickCountBeforeFade";
    public static final String KEY_FADE_SPEED = "fadeSpeed";

    public static final String KEY_UNFADABLE_RESOURCES = "unfadableResources";

    private FadeConfig() {
        this.jsonConfig = JsonConfig.create(IdleFade.MOD_ID, CONFIG_VERSION).put(KEY_ENABLE_FADE, true)
                .put(KEY_TICK_COUNT_BEFORE_FADE, 100)
                .put(KEY_FADE_SPEED, 0.05)
                .put(KEY_UNFADABLE_RESOURCES, new ObjectOpenHashSet<String>())
                .initialize();
        this.validate();
    }

    public static FadeConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public void setUnfadable(ResourceLocation id, boolean unfadable) {
        synchronized (this.unfadableResourcesCache) {
            if (unfadable) {
                this.unfadableResourcesCache.add(id);
            } else {
                this.unfadableResourcesCache.remove(id);
            }

            this.jsonConfig.put(KEY_UNFADABLE_RESOURCES, this.unfadableResourcesCache.stream().map(ResourceLocation::toString).toList()).saveToFile();
            this.validate();
        }
    }

    public boolean fadable() {
        return this.jsonConfig.getBoolean(KEY_ENABLE_FADE);
    }

    public int tickCountBeforeFade() {
        return this.jsonConfig.getInt(KEY_TICK_COUNT_BEFORE_FADE);
    }

    public double fadeSpeed() {
        return this.jsonConfig.getDouble(KEY_FADE_SPEED);
    }

    public boolean isUnfadable(ResourceLocation id) {
        synchronized (this.unfadableResourcesCache) {
            return this.unfadableResourcesCache.contains(id);
        }
    }

    private void validate() {
        synchronized (this.unfadableResourcesCache) {
            this.unfadableResourcesCache.clear();
            this.jsonConfig.getStream(KEY_UNFADABLE_RESOURCES, String.class).forEach(id -> this.unfadableResourcesCache.add(ResourceLocation.parse(id)));
        }

        double speed = this.jsonConfig.getDouble(KEY_FADE_SPEED);
        double inverse = 1.0 / speed;
        double rounded = Math.round(inverse);
        if (Math.abs(inverse - rounded) > 1e-6) {
            throw new IllegalArgumentException("1.0 / fadeSpeed must be an integer. Current fadeSpeed = " + speed + ", 1.0 / speed = " + inverse + ", expected integer near " + rounded);
        }
    }
}