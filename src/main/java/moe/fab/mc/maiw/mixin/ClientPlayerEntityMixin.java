package moe.fab.mc.maiw.mixin;

import moe.fab.mc.maiw.extension.ClientAccessExtension;
import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import moe.fab.mc.maiw.fakeplayer.PlayerState;
import moe.fab.mc.maiw.script.Script;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements ClientAccessExtension {

    @Shadow @Mutable @Final protected MinecraftClient client;

    @Override
    public void multiple_accounts_in_world$setClient(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public MinecraftClient multiple_accounts_in_world$getClient() {
        return this.client;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci){
        PlayerState playerState =  FakePlayer.getPlayer(client.getSession().getUsername());
        if(playerState.script != null){
            playerState.script.tick();
        }
        playerState.pressingKeys.forEach(key -> {KeyBinding.setKeyPressed(key,true); KeyBinding.onKeyPressed(key);});
        playerState.releasingKeys.forEach(key -> {KeyBinding.setKeyPressed(key,false);});
        playerState.releasingKeys.clear();
    }
}
