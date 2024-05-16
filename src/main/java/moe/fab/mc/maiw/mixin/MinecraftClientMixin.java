package moe.fab.mc.maiw.mixin;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import moe.fab.mc.maiw.MultipleAccountsInWorld;
import moe.fab.mc.maiw.extension.*;
import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import moe.fab.mc.maiw.storage.PlayersStore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnreachableCode")
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin  implements MinecraftClientExtension {

    @Shadow @Mutable @Final private Session session;

    @Shadow @Mutable @Final private ProfileKeys profileKeys;
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Mutable @Final private UserApiService userApiService;
    @Shadow @Final public File runDirectory;

    @Shadow  @Mutable  @Nullable public ClientWorld world;


    @Shadow @Nullable private IntegratedServer server;
    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;
    @Shadow @Mutable @Final public InGameHud inGameHud;
    @Shadow @Mutable @Final private MessageHandler messageHandler;
    @Shadow @Mutable @Final private ToastManager toastManager;
    @Shadow @Mutable @Final private TutorialManager tutorialManager;

    @Shadow protected abstract void setWorld(@Nullable ClientWorld world);

    @Shadow @Nullable public Entity cameraEntity;

    @Shadow @Nullable public Screen currentScreen;

    @Shadow @Mutable @Final public WorldRenderer worldRenderer;
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;
    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Shadow protected abstract void render(boolean tick);

    @Shadow @Final private YggdrasilAuthenticationService authenticationService;
    @Shadow @Mutable @Final private CompletableFuture<ProfileResult> gameProfileFuture;
    @Shadow @Final private MinecraftSessionService sessionService;
    @Shadow @Mutable @Final private CompletableFuture<UserApiService.UserProperties> userPropertiesFuture;
    @Shadow @Mutable @Final private TelemetryManager telemetryManager;
    @Shadow private AbuseReportContext abuseReportContext;
    @Shadow @Final public GameOptions options;

    @Shadow protected abstract void handleInputEvents();

    @Shadow private @Nullable Overlay overlay;
    @Shadow private int itemUseCooldown;

    @Shadow public abstract Session getSession();

    @Shadow @Final public GameRenderer gameRenderer;
    @Shadow @Final public Mouse mouse;
    @Unique
    boolean isFake = false;

    @Override
    public boolean multiple_accounts_in_world$isFake() {
        return this.isFake;
    }

    @Override
    public void multiple_accounts_in_world$setFake(boolean fake) {
        this.isFake = fake;
    }

    @Override
    public CompletableFuture<ProfileResult> multiple_accounts_in_world$getGameProfileFuture() {
        return this.gameProfileFuture;
    }

    @Override
    public UserApiService multiple_accounts_in_world$getUserApiService() {
        return this.userApiService;
    }

    @Override
    public CompletableFuture<UserApiService.UserProperties> multiple_accounts_in_world$getUserPropertiesFuture() {
        return this.userPropertiesFuture;
    }

    @Override
    public void multiple_accounts_in_world$setClient(MinecraftClient fakeClient) {

        this.session = fakeClient.getSession();
        this.gameProfileFuture = ((MinecraftClientExtension)fakeClient).multiple_accounts_in_world$getGameProfileFuture();
        this.userApiService = ((MinecraftClientExtension)fakeClient).multiple_accounts_in_world$getUserApiService();

        this.userPropertiesFuture = ((MinecraftClientExtension)fakeClient).multiple_accounts_in_world$getUserPropertiesFuture();
        this.telemetryManager = fakeClient.getTelemetryManager();
        this.profileKeys = fakeClient.getProfileKeys();
        this.abuseReportContext = fakeClient.getAbuseReportContext();

        this.inGameHud = fakeClient.inGameHud;
        this.toastManager = fakeClient.getToastManager();
        this.tutorialManager = fakeClient.getTutorialManager();
        this.messageHandler = fakeClient.getMessageHandler();
        this.world = fakeClient.world;
        this.setWorld(null);
        this.setWorld(fakeClient.world);
        this.player = fakeClient.player;
        this.interactionManager = fakeClient.interactionManager;
        this.cameraEntity = fakeClient.cameraEntity;
        this.currentScreen = fakeClient.currentScreen;
        this.overlay = fakeClient.getOverlay();

        MinecraftClient client = (MinecraftClient)(Object) this;
        ((ClientAccessExtension)this.inGameHud).multiple_accounts_in_world$setClient(client);
        ((ClientAccessExtension)this.toastManager).multiple_accounts_in_world$setClient(client);
        ((ClientAccessExtension)this.tutorialManager).multiple_accounts_in_world$setClient(client);
        ((ClientAccessExtension)this.messageHandler).multiple_accounts_in_world$setClient(client);

        if (this.player != null) {
            ((ClientAccessExtension)this.player).multiple_accounts_in_world$setClient(client);
            ((ClientAccessExtension)this.player.networkHandler).multiple_accounts_in_world$setClient(client);
        }
        if (this.world != null) {
            ((ClientAccessExtension)this.world).multiple_accounts_in_world$setClient(client);
        }
        if (this.interactionManager != null) {
            ((ClientAccessExtension)this.interactionManager).multiple_accounts_in_world$setClient(client);
        }
    }

    @Override
    public void multiple_accounts_in_world$setWorld(ClientWorld world) {
        this.setWorld(world);
    }

    @Override
    public void multiple_accounts_in_world$setCurrentWorld(ClientWorld world) {
        this.world = world;
    }

    @Override
    public void multiple_accounts_in_world$setCurrentScreen(Screen screen) {
        this.currentScreen = screen;
    }

    @Override
    public void multiple_accounts_in_world$fakeInit(String username, UUID uuid) {

        Path path = this.runDirectory.toPath();
        this.isFake = true;
        this.session = new Session(username, uuid, this.session.getAccessToken(), this.session.getXuid(), this.session.getClientId(), Session.AccountType.LEGACY);
        this.gameProfileFuture = CompletableFuture.supplyAsync(() -> this.sessionService.fetchProfile(this.session.getUuidOrNull(), true), Util.getDownloadWorkerExecutor());
        this.userApiService = this.session.getAccountType() == Session.AccountType.MSA? this.authenticationService.createUserApiService(this.session.getAccessToken()): UserApiService.OFFLINE;
        this.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return this.userApiService.fetchProperties();
            } catch (AuthenticationException authenticationException) {
                MultipleAccountsInWorld.LOGGER.error("Failed to fetch user properties", authenticationException);
                return UserApiService.OFFLINE_PROPERTIES;
            }
        }, Util.getDownloadWorkerExecutor());
        this.telemetryManager = new TelemetryManager((MinecraftClient) (Object)this, this.userApiService, this.session);
        this.profileKeys = ProfileKeys.create(this.userApiService, this.session, path);
        this.abuseReportContext = AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), this.userApiService);

        this.server = null;
        this.interactionManager = null;
        this.world = null;
        this.player = null;
        this.currentScreen = null;
        this.overlay = null;
        MinecraftClient client = (MinecraftClient) (Object)this;
        this.inGameHud = new InGameHud(client);
        this.toastManager = new ToastManager(client);
        this.tutorialManager = new TutorialManager(client, this.options);
        this.messageHandler = new MessageHandler(client);
    }

    @Override
    public MinecraftClient multiple_accounts_in_world$clone() {
        try {
            return (MinecraftClient) super.clone();
        } catch (CloneNotSupportedException e) {
            MultipleAccountsInWorld.LOGGER.error("fake client init failed", e);
        }
        return null;
    }

    @Override
    public void multiple_accounts_in_world$render(boolean tick) {
        render(tick);
    }

    @Override
    public void multiple_accounts_in_world$handleInputEvents() {
        handleInputEvents();
    }

    @Override
    public int multiple_accounts_in_world$getItemUseCooldown() {
        return this.itemUseCooldown;
    }

    @Override
    public void multiple_accounts_in_world$setItemUseCooldown(int val) {
        this.itemUseCooldown = val;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ClientWorld ori_world = this.world;
        ClientPlayerEntity ori_player = this.player;
        ClientPlayerInteractionManager ori_interactionManager = this.interactionManager;
        Entity ori_cameraEntity = this.cameraEntity;
        FakePlayer.controllingPlayer.online = this.player != null && this.world != null;
        if(FakePlayer.controllingPlayer.online)
            FakePlayer.controllingPlayer.joining = false;
        FakePlayer.fakePlayers.forEach((name, playerState) ->{
            try {
                MinecraftClient client = playerState.client;
                if(client == null)
                    return;
                MinecraftClientExtension extension = (MinecraftClientExtension)client;
                FakePlayer.tickingClient = client;
                FakePlayer.tickingPlayer = playerState;
                this.world = client.world;
                this.player = client.player;
                this.interactionManager = client.interactionManager;
                this.cameraEntity = client.cameraEntity;
                ((ClientAccessExtension)client.gameRenderer).multiple_accounts_in_world$setClient(client);

                playerState.online = client.player != null && client.world != null;
                if(playerState.online)
                    playerState.joining = false;
                if (client.world != null) {
                    client.world.getTickManager().step();
                }
                client.getMessageHandler().processDelayedMessages();
                client.gameRenderer.updateCrosshairTarget(1.0f);
                int itemUseCooldown = extension.multiple_accounts_in_world$getItemUseCooldown();
                if (itemUseCooldown > 0) {
                    extension.multiple_accounts_in_world$setItemUseCooldown(itemUseCooldown - 1);
                }
                if (client.currentScreen != null) {
                    client.attackCooldown = 10000;
                }
                if(client.getOverlay() == null && client.currentScreen == null) {
                    extension.multiple_accounts_in_world$handleInputEvents();
                    if (client.attackCooldown > 0) {
                        --client.attackCooldown;
                    }
                }
                if (client.interactionManager != null) {
                    client.interactionManager.tick();
                }
                if (client.world != null) {
                    client.world.tickEntities();
                }
                if (client.world != null) {
                    client.world.tick(() -> true);
                }
                if(client.player!=null && client.player.isDead()){
                    client.player.requestRespawn();
                }
            }catch (Throwable throwable){
                MultipleAccountsInWorld.LOGGER.error("Fake client tick failed ", throwable);
            }
        });
        FakePlayer.tickingClient = (MinecraftClient) (Object)this;
        FakePlayer.tickingPlayer = FakePlayer.controllingPlayer;
        this.world = ori_world;
        this.player = ori_player;
        this.interactionManager = ori_interactionManager;
        this.cameraEntity = ori_cameraEntity;
        ((ClientAccessExtension)this.gameRenderer).multiple_accounts_in_world$setClient((MinecraftClient) (Object)this);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(boolean tick, CallbackInfo ci) {
        if (this.isFake) {
            ci.cancel();
        }
    }

    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void onJoinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {

    }

    @Inject(method = "setWorld", at = @At("HEAD"), cancellable = true)
    private void onSetWorld(ClientWorld world, CallbackInfo ci) {
        if (this.isFake) {
            this.world = world;
            ci.cancel();
        }
    }

    @Redirect(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;unlockCursor()V"))
    private void onSetScreenUnlockCursor(Mouse instance) {
        if (!this.isFake) {
            this.mouse.unlockCursor();
        }
    }

    @Redirect(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;lockCursor()V"))
    private void onSetScreenLockCursor(Mouse instance) {
        if (!this.isFake) {
            this.mouse.lockCursor();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V", at = @At("HEAD"), cancellable = true)
    private void onDisconnect(Screen disconnectionScreen, boolean transferring, CallbackInfo ci) {
        FakePlayer.getPlayer(this.session.getUsername()).joining = false;
        MultipleAccountsInWorld.LOGGER.info("onDisconnect {} {} {}", this.isFake, this.session.getUsername(), FakePlayer.controllingPlayerName);
        if (!this.isFake && FakePlayer.realPlayer.equals(this.session.getUsername()))
            FakePlayer.fakePlayers.forEach((name, player) ->{FakePlayer.playerLeftWorld(name);});
        if((!FakePlayer.controllingPlayerName.equals(FakePlayer.realPlayer)) && FakePlayer.controllingPlayerName.equals(this.session.getUsername())){
            MultipleAccountsInWorld.LOGGER.info("back to realPlayer");
            String fakePlayerName = FakePlayer.controllingPlayerName;
            FakePlayer.controlPlayer(FakePlayer.realPlayer);
            FakePlayer.getPlayer(fakePlayerName).client.disconnect(disconnectionScreen, transferring);
            ci.cancel();
        }

    }

}
