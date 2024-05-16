package moe.fab.mc.maiw.fakeplayer;

import moe.fab.mc.maiw.script.Script;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerState {
    public boolean online;
    public boolean joining;
    public String scriptName;
    public Script script;
    public ConcurrentHashMap<KeyBinding, Boolean> keysPressed = new ConcurrentHashMap<>();
    public ConcurrentHashMap<KeyBinding, Integer> keysTimesPressed = new ConcurrentHashMap<>();
    public List<InputUtil.Key> pressingKeys = new Vector<>();
    public List<InputUtil.Key> releasingKeys = new Vector<>();
    public MinecraftClient client;
}
