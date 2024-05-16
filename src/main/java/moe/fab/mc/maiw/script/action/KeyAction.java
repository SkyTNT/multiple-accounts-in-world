package moe.fab.mc.maiw.script.action;

import moe.fab.mc.maiw.MultipleAccountsInWorld;
import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import moe.fab.mc.maiw.script.ScriptContext;
import moe.fab.mc.maiw.script.action.annotation.TextDesc;
import net.minecraft.client.util.InputUtil;

@TextDesc("maiw.action.key")
public class KeyAction extends Action{

    @TextDesc("maiw.action.key.key")
    public Key key = new Key();
    @TextDesc("maiw.action.key.event")
    public KeyEvent event = KeyEvent.PRESS;

    private transient InputUtil.Key ikey;

    @Override
    public void init() {
        ikey = key.getKey();
    }

    @Override
    public boolean tick(ScriptContext context) {
        if(ikey == null){
            return true;
        }
        if (event == KeyEvent.PRESS){
            FakePlayer.pressKey(FakePlayer.tickingPlayerName(), ikey);
        }else{
            FakePlayer.releaseKey(FakePlayer.tickingPlayerName(), ikey);
        }
        return true;
    }

    @Override
    public ActionTypes.ActionType getActionType() {
        return ActionTypes.key;
    }

    public static class Key{
        public String name = InputUtil.UNKNOWN_KEY.getTranslationKey();

        public InputUtil.Key getKey() {
            try {
                return InputUtil.fromTranslationKey(name);
            }catch(Exception e) {
                MultipleAccountsInWorld.LOGGER.warn("key error {}" , name, e);
            }
            return null;
        }
    }

    public enum KeyEvent {
        @TextDesc("maiw.action.key.event.press")
        PRESS,
        @TextDesc("maiw.action.key.event.release")
        RELEASE
    }
}
