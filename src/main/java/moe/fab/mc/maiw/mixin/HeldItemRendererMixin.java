package moe.fab.mc.maiw.mixin;

import moe.fab.mc.maiw.extension.MinecraftClientExtension;
import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Inject(method = "resetEquipProgress", at = @At("HEAD"), cancellable = true)
    private void resetEquipProgress(CallbackInfo ci) {
        if(((MinecraftClientExtension)FakePlayer.tickingClient).multiple_accounts_in_world$isFake())
            ci.cancel();
    }
}
