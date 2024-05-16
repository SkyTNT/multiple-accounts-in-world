package moe.fab.mc.maiw.screen;

import moe.fab.mc.maiw.MultipleAccountsInWorld;
import moe.fab.mc.maiw.extension.IntegratedServerExtension;
import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import moe.fab.mc.maiw.fakeplayer.PlayerState;
import moe.fab.mc.maiw.storage.PlayersStore;
import moe.fab.mc.maiw.storage.SettingStore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(value= EnvType.CLIENT)
public class MainScreen extends Screen {
    private final SettingStore.Settings settings;
    private PlayerList playerList;

    public MainScreen() {
        super(Text.translatable("maiw.gui.title", MultipleAccountsInWorld.MOD_VERSION));
        settings = SettingStore.getInstance().get();
    }

    @Override
    protected void init() {
        super.init();
        ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
        GridWidget grid = new GridWidget();
        layout.addHeader(Text.translatable("maiw.gui.title", MultipleAccountsInWorld.MOD_VERSION),textRenderer);
        playerList = new PlayerList(layout.getContentHeight(), layout.getHeaderHeight());
        playerList.addPlayer(FakePlayer.realPlayer);
        PlayersStore.getInstance().get().forEach(playerList::addPlayer);

        layout.addBody(playerList);
        grid.getMainPositioner().margin(4, 4, 4, 0);
        grid.setColumnSpacing(10).setRowSpacing(8);
        GridWidget.Adder adder = grid.createAdder(2);
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.add_player"),btn ->{
            if (client != null) {
                client.setScreen(new AddPlayerScreen(this));
            }
        }).build());
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.server_entry"),btn ->{
            if (client != null) {
                client.setScreen(new ServerEntryScreen(this, settings.server));
            }
        }).build());
        layout.addFooter(grid);
        layout.forEachChild(this::addDrawableChild);
        layout.refreshPositions();
    }

    public void addPlayer(String playerName){
        FakePlayer.addPlayer(playerName);
        playerList.addPlayer(playerName);
    }

    public void removePlayer(String playerName){
        playerList.removePlayer(playerName);
        FakePlayer.removePlayer(playerName);
    }

    public void joinWorld(String playerName){
        if (client != null) {
            if (settings.server.isAuto){
                IntegratedServer server = client.getServer();
                if (server != null) {
                    int port = ((IntegratedServerExtension)server).multiple_accounts_in_world$lanPort();
                    if(port != -1 || settings.server.allowAutoLan){
                        if(port == -1){
                            port = NetworkUtils.findLocalPort();
                            server.openToLan(server.getDefaultGameMode(),server.getSaveProperties().areCommandsAllowed(),port);
                        }
                        ServerInfo info = new ServerInfo(server.getServerMotd(),String.format("localhost:%d",port), ServerInfo.ServerType.LAN);
                        FakePlayer.playerJoinWorld(playerName,info);
                    }
                }else {
                    ServerInfo info = client.getCurrentServerEntry();
                    FakePlayer.playerJoinWorld(playerName,info);
                }
            }else {
                ServerInfo info = new ServerInfo("server", settings.server.address, settings.server.isDedicatedServer? ServerInfo.ServerType.OTHER: ServerInfo.ServerType.LAN);
                FakePlayer.playerJoinWorld(playerName,info);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (MultipleAccountsInWorld.guiKey.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public class PlayerList extends ElementListWidget<PlayerWeight>{
        Map<String, PlayerWeight> players = new HashMap<>();

        public PlayerList(int contentHeight, int headerHeight) {
            super(MainScreen.this.client, MainScreen.this.width, contentHeight, headerHeight, 24);
        }

        @Override
        public int getRowWidth() {
            return MainScreen.this.width - 50;
        }

        public void removePlayer(String name){
            removeEntry(players.get(name));
            players.remove(name);
        }

        public void addPlayer(String name){
            if(players.containsKey(name)){
                return;
            }
            PlayerWeight weight = new PlayerWeight(name);
            addEntry(weight);
            players.put(name, weight);
        }

        @Override
        protected void drawMenuListBackground(DrawContext context) {

        }
    }

    public class PlayerWeight extends ElementListWidget.Entry<PlayerWeight> {
        protected final List<ClickableWidget> children;
        protected String username;
        private PlayerState playerState;
        private final ButtonWidget scriptButton;
        private final ButtonWidget controlButton;
        private final ButtonWidget joinOrLeftButton;
        private final ButtonWidget removeButton;
        public PlayerWeight(String username){
            children = new ArrayList<>();
            this.username = username;
            playerState = FakePlayer.getPlayer(username);
            scriptButton = ButtonWidget.builder(Text.translatable("maiw.gui.player_script"),btn->{
                if (client != null) {
                    client.setScreen(new ScriptListScreen(MainScreen.this, username));
                }
            }).width(44).build();
            controlButton = ButtonWidget.builder(Text.translatable("maiw.gui.player_ctrl"),btn->{
                FakePlayer.controlPlayer(username);
                MainScreen.this.close();
            }).width(44).build();
            joinOrLeftButton = ButtonWidget.builder(Text.translatable(playerState.online?"maiw.gui.player_left":"maiw.gui.player_join"),btn->{
                if(playerState.online)
                    FakePlayer.playerLeftWorld(username);
                else
                    joinWorld(username);
            }).width(44).build();
            removeButton = ButtonWidget.builder(Text.translatable("maiw.gui.remove_player"),btn->{removePlayer(username);}).width(44).build();
            children.add(scriptButton);
            children.add(controlButton);
            children.add(joinOrLeftButton);
            children.add(removeButton);
        }
        @Override
        public List<? extends Selectable> selectableChildren() {
            return children;
        }

        @Override
        public List<? extends Element> children() {
            return children;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.fill(x,y-2, x +entryWidth,y + entryHeight+2, (index & 1) == 0? 0x55333333: 0x55aaaaaa);
            MutableText state = Text.translatable("maiw.gui.player_state",
                    playerState.online? Text.translatable("maiw.gui.player_state.online"):Text.translatable("maiw.gui.player_state.offline"),
                    playerState.script == null? Text.literal(""): Text.translatable("maiw.gui.player_state.script",playerState.scriptName));
            state.formatted(playerState.online? Formatting.YELLOW : Formatting.GRAY);
            context.drawText(textRenderer, Text.literal(username).append(state),x+4, y+4, Colors.WHITE,false);
            scriptButton.setX(x + entryWidth - 180);
            scriptButton.setY(y);
            controlButton.setX(x + entryWidth - 135);
            controlButton.setY(y);
            joinOrLeftButton.setX(x + entryWidth - 90);
            joinOrLeftButton.setY(y);
            removeButton.setX(x + entryWidth - 45);
            removeButton.setY(y);
            if (playerState.joining){
                joinOrLeftButton.setMessage(Text.translatable("maiw.gui.player_joining"));
                joinOrLeftButton.active = false;
            }else {
                if(playerState.online){
                    joinOrLeftButton.setMessage(Text.translatable("maiw.gui.player_left"));
                }else {
                    joinOrLeftButton.setMessage(Text.translatable("maiw.gui.player_join"));
                }
                joinOrLeftButton.active = (!username.equals(FakePlayer.realPlayer)) && (!username.equals(FakePlayer.controllingPlayerName));
            }
            scriptButton.active = playerState.online;
            controlButton.active = playerState.online && (!username.equals(FakePlayer.controllingPlayerName));
            removeButton.active = (!username.equals(FakePlayer.realPlayer)) && (!username.equals(FakePlayer.controllingPlayerName));
            children.forEach(child -> {child.render(context, mouseX, mouseY, tickDelta);});
        }
    }

}


