package moe.fab.mc.maiw.mixin;

import moe.fab.mc.maiw.extension.ClientAccessExtension;
import moe.fab.mc.maiw.extension.MinecraftClientExtension;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin implements ClientAccessExtension {

    @Shadow @Mutable @Final private MinecraftClient client;

    @Override
    public void multiple_accounts_in_world$setClient(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public MinecraftClient multiple_accounts_in_world$getClient() {
        return this.client;
    }

    // cancel all render stuff
    @Unique
    void cancelIfFake(CallbackInfo callbackInfo){
        if(((MinecraftClientExtension)client).multiple_accounts_in_world$isFake())
            callbackInfo.cancel();
    }

    @Redirect(method = "resetChunkColor", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;method_52815(Lnet/minecraft/util/math/ChunkPos;)V"))
    public void resetChunkColor(WorldRenderer instance, ChunkPos chunkPos){
    }

    @Inject(method = "updateListeners", at= @At("HEAD"), cancellable = true)
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "scheduleBlockRerenderIfNeeded", at= @At("HEAD"), cancellable = true)
    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "scheduleBlockRenders", at= @At("HEAD"), cancellable = true)
    public void scheduleBlockRenders(int x, int y, int z, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "setBlockBreakingInfo", at= @At("HEAD"), cancellable = true)
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "syncGlobalEvent", at= @At("HEAD"), cancellable = true)
    public void syncGlobalEvent(int eventId, BlockPos pos, int data, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "syncWorldEvent", at= @At("HEAD"), cancellable = true)
    public void syncWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V", at= @At("HEAD"), cancellable = true)
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;ZDDDDDD)V", at= @At("HEAD"), cancellable = true)
    public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "addImportantParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V", at= @At("HEAD"), cancellable = true)
    public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci){
        cancelIfFake(ci);
    }

    @Inject(method = "addImportantParticle(Lnet/minecraft/particle/ParticleEffect;ZDDDDDD)V", at= @At("HEAD"), cancellable = true)
    public void addImportantParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci){
        cancelIfFake(ci);
    }
}
