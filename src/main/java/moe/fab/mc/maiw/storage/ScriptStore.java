package moe.fab.mc.maiw.storage;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import moe.fab.mc.maiw.script.Script;
import moe.fab.mc.maiw.script.action.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ScriptStore extends Store<Map<String, Script>>{
    private static ScriptStore instance;
    final Map<String, Script> scripts;
    ScriptStore() {
        super("scripts");
        scripts = read();
    }

    @Override
    public Map<String, Script> providedDefault() {
        Map<String, Script> scripts = new HashMap<>();
        Script example = new Script();
        String leftKey = InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_LEFT).getTranslationKey();
        String rightKey = InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_RIGHT).getTranslationKey();
        KeyAction keyAction =new KeyAction();
        keyAction.key.name = leftKey;
        keyAction.event = KeyAction.KeyEvent.PRESS;
        example.addAction(keyAction);
        example.addAction(new DelayAction());
        keyAction =new KeyAction();
        keyAction.key.name = leftKey;
        keyAction.event = KeyAction.KeyEvent.RELEASE;
        example.addAction(keyAction);
        keyAction =new KeyAction();
        keyAction.key.name = rightKey;
        keyAction.event = KeyAction.KeyEvent.PRESS;
        example.addAction(keyAction);
        example.addAction(new DelayAction());
        keyAction =new KeyAction();
        keyAction.key.name = rightKey;
        keyAction.event = KeyAction.KeyEvent.RELEASE;
        example.addAction(keyAction);
        example.addAction(new GotoAction());
        scripts.put("example", example);
        return scripts;
    }

    @Override
    public Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(Action.class, new ActionAdapter()).setPrettyPrinting().create();
    }

    @Override
    public Map<String, Script> get() {
        return scripts;
    }

    @Override
    Type getType() {
        return new TypeToken<Map<String, Script>>() {}.getType();
    }

    public static ScriptStore getInstance() {
        if (instance == null) {
            instance = new ScriptStore();
        }
        return instance;
    }

    static class ActionAdapter implements JsonSerializer<Action>, JsonDeserializer<Action> {

        @Override
        public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement actionTypeElement = jsonObject.get("actionType");
            if (actionTypeElement == null) {
                throw new JsonParseException("can not get action type");
            }
            String actionTypeName = actionTypeElement.getAsString();
            ActionTypes.ActionType actionType = ActionTypes.types.get(actionTypeName);
            if (actionType == null) {
                throw new JsonParseException("Unknown action type: " + actionTypeName);
            }
            return context.deserialize(jsonObject, actionType.cls);
        }

        @Override
        public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = context.serialize(src).getAsJsonObject();
            jsonObject.addProperty("actionType", src.getActionType().name);
            return jsonObject;
        }
    }
}
