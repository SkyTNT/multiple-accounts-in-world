package moe.fab.mc.maiw.extension;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MinecraftClientExtension extends Cloneable{
    MinecraftClient multiple_accounts_in_world$clone();
    boolean multiple_accounts_in_world$isFake();
    void multiple_accounts_in_world$setFake(boolean fake);
    void multiple_accounts_in_world$setClient(MinecraftClient client);
    void multiple_accounts_in_world$setWorld(ClientWorld world);
    void multiple_accounts_in_world$setCurrentWorld(ClientWorld world);
    void multiple_accounts_in_world$setCurrentScreen(Screen screen);
    void multiple_accounts_in_world$fakeInit(String username, UUID uuid);
    void multiple_accounts_in_world$render(boolean tick);
    CompletableFuture<ProfileResult> multiple_accounts_in_world$getGameProfileFuture();
    UserApiService multiple_accounts_in_world$getUserApiService();
    CompletableFuture<UserApiService.UserProperties> multiple_accounts_in_world$getUserPropertiesFuture();
    void multiple_accounts_in_world$handleInputEvents();
    int multiple_accounts_in_world$getItemUseCooldown();
    void multiple_accounts_in_world$setItemUseCooldown(int val);
}
