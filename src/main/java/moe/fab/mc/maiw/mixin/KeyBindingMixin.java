package moe.fab.mc.maiw.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin{

    @Inject(method = "setPressed", at = @At("HEAD"))
    private void onSetPressed(boolean pressed, CallbackInfo ci) {
        FakePlayer.tickingPlayer.keysPressed.put((KeyBinding)(Object)this, pressed);
    }

    @Inject(method = "onKeyPressed", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/KeyBinding;timesPressed:I",opcode = Opcodes.PUTFIELD))
    private static void onOnKeyPressed(InputUtil.Key key, CallbackInfo ci, @Local KeyBinding keyBinding) {
        ConcurrentHashMap<KeyBinding, Integer> keysTimesPressed = FakePlayer.tickingPlayer.keysTimesPressed;
        Integer val = keysTimesPressed.get(keyBinding);
        if (val == null) {
            val = 0;
        }
        keysTimesPressed.put(keyBinding, val + 1);
    }

    @Inject(method = "wasPressed", at = @At("HEAD"), cancellable = true)
    private void onWasPressed(CallbackInfoReturnable<Boolean> cir){
        ConcurrentHashMap<KeyBinding, Integer> keysTimesPressed = FakePlayer.tickingPlayer.keysTimesPressed;
        KeyBinding keyBinding = (KeyBinding)(Object)this;
        Integer val = keysTimesPressed.get(keyBinding);
        if (val == null || val == 0) {
            cir.setReturnValue(false);
        }else {
            --val;
            keysTimesPressed.put(keyBinding, val);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void onIsPressed(CallbackInfoReturnable<Boolean> cir){
        Boolean val = FakePlayer.tickingPlayer.keysPressed.get((KeyBinding)(Object)this);
        cir.setReturnValue(Objects.requireNonNullElse(val, false));
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void onReset(CallbackInfo ci){
        FakePlayer.tickingPlayer.keysTimesPressed.put((KeyBinding)(Object)this, 0);
    }

}
