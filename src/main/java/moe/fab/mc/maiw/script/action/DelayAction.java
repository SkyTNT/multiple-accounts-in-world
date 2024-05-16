package moe.fab.mc.maiw.script.action;

import moe.fab.mc.maiw.script.ScriptContext;
import moe.fab.mc.maiw.script.action.annotation.Range;
import moe.fab.mc.maiw.script.action.annotation.TextDesc;

@TextDesc("maiw.action.delay")
public class DelayAction extends Action {
    @TextDesc("maiw.action.delay.ticks")
    @Range(min = 1)
    public int ticks = 1;
    private transient int tickCnt;

    @Override
    public void init() {
        tickCnt = 0;
    }

    @Override
    public boolean tick(ScriptContext context) {
        tickCnt ++;
        return tickCnt > ticks;
    }

    @Override
    public ActionTypes.ActionType getActionType() {
        return ActionTypes.delay;
    }
}
