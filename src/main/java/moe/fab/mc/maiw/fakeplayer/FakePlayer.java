package moe.fab.mc.maiw.fakeplayer;

import moe.fab.mc.maiw.MultipleAccountsInWorld;
import moe.fab.mc.maiw.extension.*;
import moe.fab.mc.maiw.script.Script;
import moe.fab.mc.maiw.storage.PlayersStore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.*;
import net.minecraft.client.util.InputUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class FakePlayer
{
    public static String realPlayer = MinecraftClient.getInstance().getSession().getUsername();
    public static final Map<String, PlayerState> fakePlayers = new ConcurrentHashMap<>();
    public static PlayerState controllingPlayer = new PlayerState();
    public static String controllingPlayerName = realPlayer;
    public static volatile MinecraftClient tickingClient = MinecraftClient.getInstance();
    public static volatile PlayerState tickingPlayer = controllingPlayer;

    static {
        PlayersStore.getInstance().get().forEach( name -> fakePlayers.put(name, new PlayerState()));
    }


    public static void addPlayer(String name){
        List<String> players = PlayersStore.getInstance().get();
        if (players.contains(name)){
            return;
        }
        fakePlayers.put(name, new PlayerState());
        players.add(name);
    }

    public static void removePlayer(String name){
        playerLeftWorld(name);
        PlayersStore.getInstance().get().remove(name);
        fakePlayers.remove(name);
    }

    public static String tickingPlayerName(){
        return FakePlayer.tickingClient.getSession().getUsername();
    }

    public static void controlPlayer(String name){
        MinecraftClient client = MinecraftClient.getInstance();

        PlayerState fakePlayer = fakePlayers.get(name);
        if(fakePlayer == null){
            return;
        }
        if (controllingPlayer.client == null){ //set to a clone of real client for the first time
            controllingPlayer.client = ((MinecraftClientExtension)client).multiple_accounts_in_world$clone();
            ((MinecraftClientExtension)controllingPlayer.client).multiple_accounts_in_world$setFake(true);
            ((MinecraftClientExtension)controllingPlayer.client).multiple_accounts_in_world$setCurrentScreen(null);
        }
        if (fakePlayer.client != null) {
            ((MinecraftClientExtension)controllingPlayer.client).multiple_accounts_in_world$setClient(client);
            controllingPlayer.client.setScreen(null);
            fakePlayers.remove(name);
            fakePlayers.put(controllingPlayerName, controllingPlayer); //put last controlling client to fake clients
            controllingPlayerName = name;
            controllingPlayer = fakePlayer;
            ((MinecraftClientExtension)client).multiple_accounts_in_world$setClient(fakePlayer.client);
        }

    }

    public static void playerJoinWorld(String name, ServerInfo info){
        PlayerState fakePlayer = fakePlayers.get(name);
        fakePlayer.joining = true;
        fakePlayer.online = false;
        if (fakePlayer.client == null){
            UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
            fakePlayer.client = ((MinecraftClientExtension)MinecraftClient.getInstance()).multiple_accounts_in_world$clone();
            ((MinecraftClientExtension)fakePlayer.client).multiple_accounts_in_world$fakeInit(name, uuid);
        }
        ConnectScreen.connect(fakePlayer.client.currentScreen,fakePlayer.client,ServerAddress.parse(info.address),info,false, null);

    }

    public static void playerLeftWorld(String name){
        PlayerState fakePlayer = fakePlayers.get(name);
        fakePlayer.joining = false;
        fakePlayer.online = false;
        if (fakePlayer.client == null) return;
        if (fakePlayer.client.world != null) {
            fakePlayer.client.world.disconnect();
        }
        fakePlayer.client.disconnect();
    }

    public static void runScript(String playerName,String scriptName, Script script){
        Script script_clone = script.clone();
        script_clone.setClient(MinecraftClient.getInstance());
        PlayerState playerState = getPlayer(playerName);
        script_clone.setOnStop(()->{
            playerState.script = null;
            playerState.scriptName = "";
            playerState.releasingKeys.addAll(playerState.pressingKeys);
            playerState.pressingKeys.clear();
        });
        if(playerState.script != null){
            playerState.script.stop();
        }
        playerState.script = script_clone;
        playerState.scriptName = scriptName;
    }

    public static void stopScript(String playerName){
        PlayerState playerState = getPlayer(playerName);
        if(playerState.script != null)
            playerState.script.stop();
    }

    public static void pressKey(String playerName, InputUtil.Key key){
        PlayerState playerState = getPlayer(playerName);
        if(!playerState.pressingKeys.contains(key)){
            playerState.pressingKeys.add(key);
        }
    }

    public static void releaseKey(String playerName, InputUtil.Key key){
        PlayerState playerState = getPlayer(playerName);
        playerState.pressingKeys.remove(key);
        playerState.releasingKeys.add(key);
    }

    public static PlayerState getPlayer(String name){
        if(name.equals(controllingPlayerName)){
            return controllingPlayer;
        }else {
            return fakePlayers.get(name);
        }
    }

}
