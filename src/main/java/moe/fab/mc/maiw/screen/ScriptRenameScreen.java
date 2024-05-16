package moe.fab.mc.maiw.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class ScriptRenameScreen extends Screen {
    private final Screen parent;
    private final String name;
    private final Consumer<String> callback;
    private TextFieldWidget nameField;
    protected ScriptRenameScreen(Screen parent, String name, Consumer<String> callback) {
        super(Text.translatable("maiw.gui.script_rename", name));
        this.parent = parent;
        this.name = name;
        this.callback = callback;
    }

    @Override
    protected void init() {
        super.init();
        ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
        layout.setFooterHeight(0);
        GridWidget grid = new GridWidget();
        layout.addHeader(Text.translatable("maiw.gui.script_rename", name),textRenderer);
        grid.getMainPositioner().margin(4, 4, 4, 0);
        grid.setColumnSpacing(10).setRowSpacing(8);
        GridWidget.Adder adder = grid.createAdder(2);
        nameField = new TextFieldWidget(textRenderer,320,20,Text.literal(""));
        nameField.setPlaceholder(Text.translatable("maiw.gui.script_rename.new_name").formatted(Formatting.DARK_GRAY));
        nameField.setText(name);
        adder.add(nameField,2);
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.confirm"), btn ->{
            callback.accept(nameField.getText());
            close();
        }).build());
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.cancel"),btn ->{close();}).build());
        layout.addBody(grid);
        layout.forEachChild(this::addDrawableChild);
        layout.refreshPositions();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

}
