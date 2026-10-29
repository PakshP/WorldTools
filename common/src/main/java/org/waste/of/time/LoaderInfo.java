package org.waste.of.time;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class LoaderInfo {
    @Contract(pure = true)
    public static @NotNull String getVersion() {
        return FabricLoader.getInstance().getModContainer("worldtools").orElseThrow().getMetadata().getVersion().getFriendlyString();
    }
}
