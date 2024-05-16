package moe.fab.mc.maiw.storage;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SettingStore extends Store<SettingStore.Settings>{
    public static SettingStore instance;
    public Settings settings;

    public SettingStore() {
        super("setting");
        settings = read();
    }

    @Override
    public Settings providedDefault() {
        return new Settings();
    }

    @Override
    public Settings get() {
        return settings;
    }

    @Override
    Type getType() {
        return new TypeToken<Settings>(){}.getType();
    }

    public static SettingStore getInstance() {
        if (instance == null) {
            instance = new SettingStore();
        }
        return instance;
    }

    public static class Settings{
        public ServerSetting server;

        public Settings(){
            server = new ServerSetting();
        }
    }

    public static class ServerSetting{
        public boolean isAuto = true;
        public String address = "localhost";
        public boolean isDedicatedServer = true;
        public boolean allowAutoLan = true;
    }
}
