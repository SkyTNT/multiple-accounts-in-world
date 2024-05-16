package moe.fab.mc.maiw.mixin;

import moe.fab.mc.maiw.extension.IntegratedServerExtension;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin implements IntegratedServerExtension {
    @Shadow private int lanPort;

    @Override
    public int multiple_accounts_in_world$lanPort() {
        return this.lanPort;
    }
}
