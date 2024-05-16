package moe.fab.mc.maiw.screen;

import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import moe.fab.mc.maiw.storage.PlayersStore;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AddPlayerScreen extends Screen {
    private final MainScreen parent;
    public AddPlayerScreen(MainScreen parent) {
        super(Text.translatable("maiw.gui.add_player"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        GridWidget grid = new GridWidget();
        grid.getMainPositioner().margin(4, 4, 4, 0);
        grid.setColumnSpacing(10).setRowSpacing(8);
        GridWidget.Adder adder = grid.createAdder(2);

        TextFieldWidget usernameField = adder.add(new TextFieldWidget(textRenderer,320,20,Text.literal("")),2);
        usernameField.setPlaceholder(Text.translatable("maiw.gui.username").formatted(Formatting.DARK_GRAY));
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.confirm"), btn ->{
            String name = usernameField.getText();
            if ((!name.isEmpty()) && (!PlayersStore.getInstance().get().contains(name))) {
                parent.addPlayer(name);
                close();
            }
        }).build());
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.cancel"), btn ->{
            close();
        }).build());
        grid.forEachChild(this::addDrawableChild);
        grid.refreshPositions();
        SimplePositioningWidget.setPos(grid, 0, 0, this.width, this.height, 0.5f, 0.25f);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}
