package moe.fab.mc.maiw.storage;

import com.google.gson.*;
import moe.fab.mc.maiw.MultipleAccountsInWorld;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.lang.reflect.Type;

public abstract class Store<T> {
    private final String configPath;
    private final String name;
    private final String file;

    public boolean justCreated = false;

    Store(String name) {
        this.configPath = String.format("%s/config/%s",MinecraftClient.getInstance().runDirectory, MultipleAccountsInWorld.MOD_ID);
        this.name = name;
        this.file = String.format("%s/%s.json",this.configPath, this.name);
    }

    public Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public T read() {
        Gson gson = this.getGson();

        try {
            try {
                return gson.fromJson(new FileReader(this.file), this.getType());
            } catch (JsonParseException e) {
                MultipleAccountsInWorld.LOGGER.error("error with json loading on {}.json", this.name, e);
            }
        } catch (FileNotFoundException ignored) {
            this.justCreated = true;

            // Write a blank version of the file
            if (new File(configPath).mkdirs()) {
                this.write(true);
            }
        }

        return this.providedDefault();
    }

    public void write() {
        this.write(false);
    }

    private void write(Boolean firstWrite) {
        Gson gson = this.getGson();

        try {
            try (FileWriter writer = new FileWriter(this.file)) {
                gson.toJson(firstWrite ? this.providedDefault() : this.get(), writer);
                writer.flush();
            }
        } catch (IOException | JsonIOException e) {
            MultipleAccountsInWorld.LOGGER.error("could not write config to file", e);
        }
    }

    public abstract T providedDefault();

    public abstract T get();

    abstract Type getType();
}
