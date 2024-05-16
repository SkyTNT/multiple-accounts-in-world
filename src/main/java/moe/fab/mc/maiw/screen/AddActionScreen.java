package moe.fab.mc.maiw.screen;

import moe.fab.mc.maiw.MultipleAccountsInWorld;
import moe.fab.mc.maiw.script.Script;
import moe.fab.mc.maiw.script.action.Action;
import moe.fab.mc.maiw.script.action.ActionTypes;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;


public class AddActionScreen extends Screen {
    private final Screen parent;
    private final Script script;
    private ActionTypeList actionTypeList;
    private CyclingButtonWidget<Boolean> positionToggle;
    private TextFieldWidget positionField;
    protected AddActionScreen(Screen parent, Script script) {
        super(Text.translatable("maiw.gui.add_action"));
        this.parent = parent;
        this.script = script;
    }

    @Override
    protected void init() {
        super.init();
        ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
        layout.setFooterHeight(66);
        GridWidget grid = new GridWidget();
        layout.addHeader(Text.translatable("maiw.gui.add_action"),textRenderer);
        actionTypeList = new ActionTypeList(layout.getContentHeight(), layout.getHeaderHeight());
        ActionTypes.types.forEach((name, actionType) -> {actionTypeList.addType(actionType);});
        layout.addBody(actionTypeList);

        grid.getMainPositioner().margin(4, 4, 4, 0);
        grid.setColumnSpacing(10).setRowSpacing(8);
        GridWidget.Adder adder = grid.createAdder(2);
        positionToggle = CyclingButtonWidget.onOffBuilder(
                Text.translatable("maiw.gui.add_action.position_toggle.end"),
                Text.translatable("maiw.gui.add_action.position_toggle.specified")
        ).build(
                0,0,150,20,
                Text.translatable("maiw.gui.add_action.position_toggle"),(button, value) -> {
                    togglePosition(value);
                }
        );
        positionField = new TextFieldWidget(textRenderer,150,20,Text.literal(""));
        positionField.setPlaceholder(Text.translatable("maiw.gui.add_action.position").formatted(Formatting.DARK_GRAY));
        positionField.setText("1");
        adder.add(positionToggle);
        adder.add(positionField);
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.add_action.add"),btn ->{addAction();}).build());
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.back"),btn ->{close();}).build());

        layout.addFooter(grid);
        layout.forEachChild(this::addDrawableChild);
        layout.refreshPositions();
        togglePosition(true);
    }

    void addAction(){
        ActionTypeWeight weight = actionTypeList.getSelectedOrNull();
        if (weight == null) {
            return;
        }
        ActionTypes.ActionType actionType = weight.actionType;
        Action action = actionType.newInstance();
        if(positionToggle.getValue()) {
            script.addAction(action);
            close();
        }else {
            try {
                int index = Integer.parseInt(positionField.getText()) - 1;
                if(index <0 || index > script.actions.size()) {
                    return;
                }
                script.insertAction(index, action);
                close();
            }catch (NumberFormatException e){
                MultipleAccountsInWorld.LOGGER.warn("Failed to add action to position: {}", positionField.getText());
            }
        }
    }

    void togglePosition(boolean end) {
        if (end){
            remove(positionField);
            positionToggle.setWidth(320);
        }else {
            addDrawableChild(positionField);
            positionToggle.setWidth(150);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    public class ActionTypeList extends AlwaysSelectedEntryListWidget<ActionTypeWeight> {

        public ActionTypeList(int contentHeight, int headerHeight) {
            super(AddActionScreen.this.client, AddActionScreen.this.width, contentHeight, headerHeight, 24);
        }

        @Override
        public int getRowWidth() {
            return AddActionScreen.this.width - 50;
        }

        public void addType(ActionTypes.ActionType actionType){
            ActionTypeWeight weight = new ActionTypeWeight(actionType);
            addEntry(weight);
        }
        @Override
        protected void drawMenuListBackground(DrawContext context) {

        }
    }

    public class ActionTypeWeight extends AlwaysSelectedEntryListWidget.Entry<ActionTypeWeight> {
        private final ActionTypes.ActionType actionType;
        private final Text name;
        public ActionTypeWeight(ActionTypes.ActionType actionType){
            this.actionType = actionType;
            name = Text.translatable(actionType.description);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(textRenderer,name,x+4, y + 4, Colors.WHITE, false);
        }

        @Override
        public Text getNarration() {
            return name;
        }
    }

}
