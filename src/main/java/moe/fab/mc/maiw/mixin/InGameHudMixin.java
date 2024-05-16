package moe.fab.mc.maiw.mixin;

import moe.fab.mc.maiw.extension.ClientAccessExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin implements ClientAccessExtension {
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
