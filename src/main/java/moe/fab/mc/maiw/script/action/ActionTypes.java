package moe.fab.mc.maiw.script.action;

import moe.fab.mc.maiw.script.action.annotation.Range;
import moe.fab.mc.maiw.script.action.annotation.TextDesc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class ActionTypes {
    public static Map<String, ActionType> types = new HashMap<>();
    public static ActionType delay = register("delay", DelayAction.class);
    public static ActionType key = register("key", KeyAction.class);
    public static ActionType goto_ = register("goto", GotoAction.class);

    public static ActionType register(String name, final Class<? extends Action> action) {
        ActionType actionType = new ActionType();
        actionType.name = name;
        actionType.cls = action;
        actionType.description = action.getAnnotation(TextDesc.class).value();
        List<ParameterType> parameterTypes = new ArrayList<>();
        for (Field field : action.getFields()) {
            if(Modifier.isTransient(field.getModifiers()))
                continue;
            ParameterType parameterType = new ParameterType();
            parameterType.cls = field.getType();
            parameterType.name = field.getName();
            parameterType.description = field.getAnnotation(TextDesc.class).value();
            Range range = field.getAnnotation(Range.class);
            if(range != null) {
                parameterType.hasRange = true;
                parameterType.rangeMin = range.min();
                parameterType.rangeMax = range.max();
            }
            if (parameterType.cls.isEnum()) {
                Map<Enum<?>, EnumOption> enumOptions = new HashMap<>();
                for (Enum<?> enumVal: (Enum<?>[])parameterType.cls.getEnumConstants()){
                    EnumOption enumOption = new EnumOption();
                    enumOption.name = enumVal.name();
                    try {
                        enumOption.description = parameterType.cls.getField(enumOption.name).getAnnotation(TextDesc.class).value();
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                    enumOptions.put(enumVal, enumOption);
                }
                parameterType.enumOptions = enumOptions;
            }
            parameterTypes.add(parameterType);
        }
        actionType.parameters = parameterTypes;
        types.put(name, actionType);
        return actionType;
    }

    public static class ActionType{
        public Class<? extends Action> cls;
        public String name;
        public String description;
        public List<ParameterType> parameters;

        public Action newInstance() {
            try {
                return (Action) cls.getDeclaredConstructors()[0].newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ParameterType{
        public Class<?> cls;
        public String name;
        public String description;
        public boolean hasRange = false;
        public int rangeMin;
        public int rangeMax;
        public Map<Enum<?>, EnumOption> enumOptions;
    }

    public static class EnumOption{
        public String name;
        public String description;
    }

}
