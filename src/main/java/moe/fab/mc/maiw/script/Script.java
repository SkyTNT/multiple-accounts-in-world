package moe.fab.mc.maiw.script;

import com.google.gson.Gson;
import moe.fab.mc.maiw.MultipleAccountsInWorld;
import moe.fab.mc.maiw.script.action.Action;
import moe.fab.mc.maiw.storage.ScriptStore;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Vector;

public class Script{
    public final List<Action> actions;
    private final transient ScriptContext context;
    private transient Runnable onStop;
    private transient boolean stopped = false;
    public Script() {
        this.actions = new Vector<>();
        this.context = new ScriptContext();
    }

    public Script clone(){
        Gson gson = ScriptStore.getInstance().getGson();
        String json = gson.toJson(this);
        return gson.fromJson(json, Script.class);
    }

    public void addAction(Action action) {
        this.actions.add(action);
    }
    public void removeAction(Action action) {
        this.actions.remove(action);
    }

    public void insertAction(int index, Action action) {
        this.actions.add(index, action);
    }

    public void setClient(MinecraftClient client){
        context.client = client;
    }

    public void setOnStop(Runnable onStop){
        this.onStop = onStop;
    }
    public void stop(){
        stopped = true;
        if(onStop != null) onStop.run();
    }

    private transient boolean lastFinished = true;
    private transient int loopTimes = 0;
    public void tick() {
        if(stopped) return;
        loopTimes = 0;
        do {
            if(context.actionIndex >= actions.size()){
                stop();
                return;
            }
            if(loopTimes > 1000){
                MultipleAccountsInWorld.LOGGER.warn("script loop too long");
                stop();
                return;
            }
            Action action = actions.get(context.actionIndex);
            if(lastFinished)
                action.init();
            lastFinished = action.tick(context);
            if(lastFinished)
                context.actionIndex++;
            loopTimes++;
        } while (lastFinished);

    }
}
