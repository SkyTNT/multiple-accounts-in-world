package moe.fab.mc.maiw.mixin;

import moe.fab.mc.maiw.extension.ClientAccessExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements ClientAccessExtension {
    @Mutable @Shadow @Final MinecraftClient client;

    @Override
    public void multiple_accounts_in_world$setClient(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public MinecraftClient multiple_accounts_in_world$getClient() {
        return this.client;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openGameMenu(Z)V"))
    private void onRenderLostFocus(MinecraftClient instance, boolean pauseOnly) {//do not open GameMenu when lost focus

    }
}
