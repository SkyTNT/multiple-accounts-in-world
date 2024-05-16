package moe.fab.mc.maiw.mixin;

import moe.fab.mc.maiw.extension.ClientAccessExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin implements ClientAccessExtension {
    @Shadow @Mutable @Final private MinecraftClient client;

    @Override
    public void multiple_accounts_in_world$setClient(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public MinecraftClient multiple_accounts_in_world$getClient() {
        return this.client;
    }
}
