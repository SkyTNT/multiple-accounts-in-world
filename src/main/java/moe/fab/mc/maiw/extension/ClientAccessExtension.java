package moe.fab.mc.maiw.extension;

import net.minecraft.client.MinecraftClient;

public interface ClientAccessExtension {
    void multiple_accounts_in_world$setClient(MinecraftClient client);
    MinecraftClient multiple_accounts_in_world$getClient();
}
