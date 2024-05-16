package moe.fab.mc.maiw.storage;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;

public class PlayersStore extends Store<List<String>>{
    private static PlayersStore instance;
    private final List<String> players;

    public PlayersStore() {
        super("players");
        players = read();
    }

    @Override
    public List<String> providedDefault() {
        return new Vector<>();
    }

    @Override
    public List<String> get() {
        return players;
    }

    @Override
    Type getType() {
        return new TypeToken<List<String>>() {}.getType();
    }

    public static PlayersStore getInstance() {
        if (instance == null) {
            instance = new PlayersStore();
        }
        return instance;
    }
}
