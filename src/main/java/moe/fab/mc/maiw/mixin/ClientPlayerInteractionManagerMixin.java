package moe.fab.mc.maiw.mixin;

import moe.fab.mc.maiw.MultipleAccountsInWorld;
import moe.fab.mc.maiw.extension.ClientAccessExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin implements ClientAccessExtension {
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
