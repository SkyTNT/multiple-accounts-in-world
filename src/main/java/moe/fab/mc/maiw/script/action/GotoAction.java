package moe.fab.mc.maiw.script.action;

import moe.fab.mc.maiw.script.ScriptContext;
import moe.fab.mc.maiw.script.action.annotation.Range;
import moe.fab.mc.maiw.script.action.annotation.TextDesc;

@TextDesc("maiw.action.goto")
public class GotoAction extends Action {
    @TextDesc("maiw.action.goto.position")
    @Range(min = 1)
    public int position=1;
    @TextDesc("maiw.action.goto.maxTimes")
    @Range(min = -1)
    public int maxTimes=-1;
    private transient int times=0;

    @Override
    public boolean tick(ScriptContext context) {
        if(maxTimes != -1 && times >= maxTimes){
            times = 0; //reset
            return true;
        }
        if(position - 1 == context.actionIndex) //avoid empty loop
            return true;
        context.actionIndex = position - 2;
        times++;
        return true;
    }

    @Override
    public ActionTypes.ActionType getActionType() {
        return ActionTypes.goto_;
    }
}
