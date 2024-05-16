package moe.fab.mc.maiw.screen;

import moe.fab.mc.maiw.MultipleAccountsInWorld;
import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import moe.fab.mc.maiw.script.Script;
import moe.fab.mc.maiw.script.action.Action;
import moe.fab.mc.maiw.script.action.ActionTypes;
import moe.fab.mc.maiw.script.action.KeyAction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ScriptEditScreen extends Screen {
    private final Screen parent;
    private final String scriptName;
    private final Script script;
    private ActionList actionList;
    private Consumer<InputUtil.Key> setKey;
    protected ScriptEditScreen(Screen parent, String name, Script script) {
        super(Text.translatable("maiw.gui.script_edit", name));
        this.parent = parent;
        this.scriptName = name;
        this.script = script;
    }

    @Override
    protected void init() {
        super.init();
        ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
        GridWidget grid = new GridWidget();
        layout.addHeader(Text.translatable("maiw.gui.script_edit", scriptName),textRenderer);
        actionList = new ActionList(layout.getContentHeight(), layout.getHeaderHeight());
        script.actions.forEach(actionList::addAction);
        layout.addBody(actionList);

        grid.getMainPositioner().margin(4, 4, 4, 0);
        grid.setColumnSpacing(10).setRowSpacing(8);
        GridWidget.Adder adder = grid.createAdder(2);
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.script_edit.add"),btn ->{showAddActionScreen();}).build());
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.back"),btn ->{close();}).build());
        layout.addFooter(grid);
        layout.forEachChild(this::addDrawableChild);
        layout.refreshPositions();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (setKey != null) {
            setKey.accept(InputUtil.Type.MOUSE.createFromCode(button));
            setKey = null;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (setKey != null) {
            setKey.accept(InputUtil.fromKeyCode(keyCode, scanCode));
            setKey = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void showAddActionScreen(){
        client.setScreen(new AddActionScreen(this, script));
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    public void addAction(Action action){
        actionList.addAction(action);
        script.addAction(action);
    }

    public void removeAction(Action action){
        actionList.removeAction(action);
        script.removeAction(action);
    }

    public class ActionList extends ElementListWidget<ActionWeight> {
        Map<Action, ActionWeight>  actionWeights= new HashMap<>();

        public ActionList(int contentHeight, int headerHeight) {
            super(ScriptEditScreen.this.client, ScriptEditScreen.this.width, contentHeight, headerHeight, 24);
        }

        @Override
        public int getRowWidth() {
            return ScriptEditScreen.this.width - 50;
        }

        public void removeAction(Action action){
            removeEntry(actionWeights.get(action));
            actionWeights.remove(action);
        }

        public void addAction(Action action){
            ActionWeight weight = new ActionWeight(action);
            addEntry(weight);
            actionWeights.put(action, weight);
        }

        @Override
        protected void drawMenuListBackground(DrawContext context) {

        }
    }

    public class ActionWeight extends ElementListWidget.Entry<ActionWeight> {
        protected final List<ClickableWidget> children;
        private final Action action;
        private final ActionTypes.ActionType actionType;
        private final TextWidget nameWidget;
        private final List<ClickableWidget> parameterWeights;
        private final ButtonWidget delButton;
        public ActionWeight(Action action){
            children = new ArrayList<>();
            this.action = action;
            this.actionType = action.getActionType();
            nameWidget = new TextWidget(Text.translatable(actionType.description), textRenderer);
            parameterWeights = new ArrayList<>();
            delButton = ButtonWidget.builder(Text.translatable("maiw.gui.script_edit.del"),btn->{removeAction(action);}).width(44).build();
            children.add(nameWidget);
            children.add(delButton);
            addParameterWeights();
            children.addAll(parameterWeights);
        }

        private void addParameterWeights(){
            actionType.parameters.forEach(parameterType -> {
                Class<?> cls = parameterType.cls;
                if(cls == String.class){
                    TextFieldWidget widget = new TextFieldWidget(textRenderer,44,20,Text.literal(""));
                    widget.setPlaceholder(Text.translatable(parameterType.description).formatted(Formatting.DARK_GRAY));
                    widget.setTooltip(Tooltip.of(Text.translatable(parameterType.description)));
                    widget.setText((String) action.getParameter(parameterType.name));
                    widget.setChangedListener(val ->{
                        action.setParameter(parameterType.name, val);
                    });
                    parameterWeights.add(widget);
                }else if(cls == int.class){
                    TextFieldWidget widget = new TextFieldWidget(textRenderer,44,20,Text.literal(""));
                    widget.setPlaceholder(Text.translatable(parameterType.description).formatted(Formatting.DARK_GRAY));
                    widget.setTooltip(Tooltip.of(Text.translatable(parameterType.description)));
                    widget.setText(String.valueOf(action.getParameter(parameterType.name)));
                    widget.setChangedListener(val ->{
                        try {
                            if(val.equals("-"))
                                return;
                            int value = Integer.parseInt(val);
                            if (parameterType.hasRange){
                                if(value< parameterType.rangeMin){
                                    value = parameterType.rangeMin;
                                    widget.setText(String.valueOf(value));
                                }
                                else if(value>parameterType.rangeMax){
                                    value = parameterType.rangeMax;
                                    widget.setText(String.valueOf(value));
                                }
                            }
                            action.setParameter(parameterType.name, value);
                        }catch (NumberFormatException e){
                            widget.eraseCharacters(-1);
                        }
                    });
                    parameterWeights.add(widget);
                }else if(cls.isEnum()){

                    CyclingButtonWidget<Object> widget = CyclingButtonWidget.builder(
                            val -> Text.translatable(parameterType.enumOptions.get(val).description)
                    ).values(parameterType.enumOptions.keySet().toArray()
                    ).initially(action.getParameter(parameterType.name)
                    ).build(0,0,54,20,Text.translatable(parameterType.description),(button, value) -> {
                        action.setParameter(parameterType.name, value);
                    });
                    parameterWeights.add(widget);
                }else if(cls == KeyAction.Key.class){
                    KeyAction.Key key= (KeyAction.Key)action.getParameter(parameterType.name);
                    InputUtil.Key ikey = key.getKey();
                    ButtonWidget widget = ButtonWidget.builder(ikey.getLocalizedText(), btn ->{
                        setKey = (new_key)->{
                            btn.setMessage(new_key.getLocalizedText());
                            btn.setFocused(false);
                            key.name = new_key.getTranslationKey();
                        };
                    }).width(64).build();
                    widget.setTooltip(Tooltip.of(Text.translatable(parameterType.description)));
                    parameterWeights.add(widget);
                }
            });
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return children;
        }

        @Override
        public List<? extends Element> children() {
            return children;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.fill(x,y-2, x +entryWidth,y + entryHeight+2, (index & 1) == 0? 0x55333333: 0x55aaaaaa);
            context.drawText(textRenderer, String.valueOf(index + 1), x+4,y+4, Colors.GRAY,false);
            nameWidget.setX(x+14);
            nameWidget.setY(y+4);
            delButton.setX(x + entryWidth - 45);
            delButton.setY(y);
            int x1 = x + 80;
            for (ClickableWidget parameterWeight: parameterWeights)
            {
                parameterWeight.setX(x1);
                parameterWeight.setY(y);
                x1 += parameterWeight.getWidth() + 1;
            }
            children.forEach(child -> {child.render(context, mouseX, mouseY, tickDelta);});
        }
    }
}
