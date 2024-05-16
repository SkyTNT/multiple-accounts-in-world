package moe.fab.mc.maiw.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;

public class ConfirmNoPauseScreen extends ConfirmScreen {
    public ConfirmNoPauseScreen(BooleanConsumer callback, Text title, Text message, Text yesText, Text noText) {
        super(callback, title, message, yesText, noText);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
