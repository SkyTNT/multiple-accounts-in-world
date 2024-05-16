package moe.fab.mc.maiw.script.action;

import moe.fab.mc.maiw.script.ScriptContext;

public abstract class Action {
    public void init(){}
    public abstract boolean tick(ScriptContext context);//return true if action finished

    public Object getParameter(String parameterName) {
        try {
            return  this.getClass().getField(parameterName).get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setParameter(String parameterName, Object parameterValue) {
        try {
            this.hashCode();
            this.getClass().getField(parameterName).set(this, parameterValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract ActionTypes.ActionType getActionType();


}
