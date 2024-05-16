package moe.fab.mc.maiw.screen;

import moe.fab.mc.maiw.storage.SettingStore;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ServerEntryScreen  extends Screen {
    private final MainScreen parent;
    private final SettingStore.ServerSetting setting;
    private CyclingButtonWidget<Boolean> isAuto;
    private CyclingButtonWidget<Boolean> isServer;
    private CyclingButtonWidget<Boolean> allowAutoLan;
    private TextFieldWidget addressField;
    private ButtonWidget backButton;

    public ServerEntryScreen(MainScreen parent, SettingStore.ServerSetting setting) {
        super(Text.translatable("maiw.gui.server_entry"));
        this.parent = parent;
        this.setting = setting;
    }

    @Override
    protected void init() {
        super.init();

        isAuto = CyclingButtonWidget.onOffBuilder(
                Text.translatable("maiw.gui.server_entry.auto"),
                Text.translatable("maiw.gui.server_entry.specified")
        ).build(
                0,0,320,20,
                Text.translatable("maiw.gui.server_entry.select"),(button, value) -> {
                    refreshPositions(value);
                    setting.isAuto = value;
                }
        );
        isAuto.setValue(setting.isAuto);

        isServer = CyclingButtonWidget.onOffBuilder(
                Text.translatable("maiw.gui.server_entry.type.server"),
                Text.translatable("maiw.gui.server_entry.type.lan")
        ).build(
                0,0,150,20,
                Text.translatable("maiw.gui.server_entry.type"),(button, value) -> {
                    setting.isDedicatedServer = value;
                }
        );
        isServer.setValue(setting.isDedicatedServer);

        addressField = new TextFieldWidget(textRenderer,150,20,Text.literal(""));
        addressField.setPlaceholder(Text.translatable("maiw.gui.server_entry.address").formatted(Formatting.DARK_GRAY));
        addressField.setText(setting.address);
        addressField.setChangedListener(address -> {setting.address = address;});
        allowAutoLan = CyclingButtonWidget.onOffBuilder(
                Text.translatable("maiw.gui.server_entry.allow_auto_lan.on"),
                Text.translatable("maiw.gui.server_entry.allow_auto_lan.off")
        ).build(
                0,0,320,20,
                Text.translatable("maiw.gui.server_entry.allow_auto_lan"),(button, value) -> {
                    setting.allowAutoLan = value;
                }
        );
        allowAutoLan.setValue(setting.allowAutoLan);

        backButton = ButtonWidget.builder(Text.translatable("maiw.gui.back"), button -> {
            close();
        }).size(320,20).build();
        refreshPositions(setting.isAuto);
    }

    private void refreshPositions(boolean auto){
        clearChildren();
        GridWidget grid = new GridWidget();
        grid.getMainPositioner().margin(4, 4, 4, 0);
        grid.setColumnSpacing(10).setRowSpacing(8);
        GridWidget.Adder adder = grid.createAdder(2);
        adder.add(isAuto,2);
        if(auto){
            adder.add(allowAutoLan,2);
        }else {
            adder.add(isServer);
            adder.add(addressField);
        }
        adder.add(backButton,2);
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
