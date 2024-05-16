package moe.fab.mc.maiw.screen;

import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import moe.fab.mc.maiw.fakeplayer.PlayerState;
import moe.fab.mc.maiw.script.Script;
import moe.fab.mc.maiw.storage.ScriptStore;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptListScreen extends Screen {
    private final Screen parent;
    private final String playerName;
    private final PlayerState playerState;
    private final Map<String, Script> scripts;
    private ScriptList scriptList;

    protected ScriptListScreen(Screen parent, String playerName) {
        super(Text.translatable("maiw.gui.script_list"));
        this.parent = parent;
        this.playerName = playerName;
        this.playerState = FakePlayer.getPlayer(playerName);
        this.scripts = ScriptStore.getInstance().get();
    }

    @Override
    protected void init() {
        super.init();
        ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
        GridWidget grid = new GridWidget();
        layout.addHeader(Text.translatable("maiw.gui.script_list"),textRenderer);
        scriptList = new ScriptList(layout.getContentHeight(), layout.getHeaderHeight());
        scripts.forEach(scriptList::addScript);
        layout.addBody(scriptList);

        grid.getMainPositioner().margin(4, 4, 4, 0);
        grid.setColumnSpacing(10).setRowSpacing(8);
        GridWidget.Adder adder = grid.createAdder(2);
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.script_list.new"),btn ->{newScript();}).build());
        adder.add(ButtonWidget.builder(Text.translatable("maiw.gui.back"),btn ->{close();}).build());
        layout.addFooter(grid);
        layout.forEachChild(this::addDrawableChild);
        layout.refreshPositions();
    }

    public void newScript() {
        String name;
        int i = 1;
        do{
            name = "script" + i;
            i++;
        }while (scripts.containsKey(name));
        Script script = new Script();
        scripts.put(name, script);
        scriptList.addScript(name, script);
    }

    public void delScript(String name) {
        scripts.remove(name);
        scriptList.removeScript(name);
    }

    public void duplicateScript(String name, Script script) {
        Script copy = script.clone();
        do{
            name = incrementString(name);
        }while(scripts.containsKey(name));
        scripts.put(name, copy);
        scriptList.addScript(name, script);
    }

    public void renameScript(String name, String new_name, Script script) {
        scripts.remove(name);
        scripts.put(new_name, script);
    }

    public static String incrementString(String str) {
        // 定义匹配字符串末尾数字的正则表达式
        Pattern pattern = Pattern.compile("(\\d+)$");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            // 如果找到了数字部分，提取出来并转换为整数
            String numberStr = matcher.group(1);
            int number = Integer.parseInt(numberStr);
            // 将整数加一并转换回字符串
            String incrementedNumberStr = String.valueOf(number + 1);
            // 替换掉原来的数字部分
            return str.substring(0, matcher.start(1)) + incrementedNumberStr;
        } else {
            // 如果没有找到数字部分，直接在末尾添加数字1
            return str + "1";
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

    public class ScriptList extends ElementListWidget<ScriptWeight>{
        Map<String, ScriptWeight> scriptWeights = new HashMap<>();

        public ScriptList(int contentHeight, int headerHeight) {
            super(ScriptListScreen.this.client, ScriptListScreen.this.width, contentHeight, headerHeight, 24);
        }

        @Override
        public int getRowWidth() {
            return ScriptListScreen.this.width - 50;
        }

        public void removeScript(String name){
            removeEntry(scriptWeights.get(name));
            scriptWeights.remove(name);
        }

        public void addScript(String name, Script script){
            ScriptWeight weight = new ScriptWeight(name, script);
            addEntry(weight);
            scriptWeights.put(name, weight);
        }

        @Override
        protected void drawMenuListBackground(DrawContext context) {

        }
    }

    public class ScriptWeight extends ElementListWidget.Entry<ScriptWeight> {
        protected final List<ClickableWidget> children;
        protected String scriptName;
        private final Script script;
        private final TextWidget nameWidget;
        private final ButtonWidget runOrStopButton;
        private final ButtonWidget editButton;
        private final ButtonWidget renameButton;
        private final ButtonWidget duplicateButton;
        private final ButtonWidget delButton;
        public ScriptWeight(String scriptName, Script script){
            children = new ArrayList<>();
            this.scriptName = scriptName;
            this.script = script;
            nameWidget = new TextWidget(Text.literal(scriptName), textRenderer);
            runOrStopButton = ButtonWidget.builder(Text.translatable("maiw.gui.script_list.run"),btn->{
                if(scriptName.equals(playerState.scriptName)){
                    FakePlayer.stopScript(playerName);
                }else {
                    FakePlayer.runScript(playerName, scriptName, script);
                    client.setScreen(null);
                }
            }).width(44).build();
            editButton = ButtonWidget.builder(Text.translatable("maiw.gui.script_list.edit"),btn->{
                client.setScreen(new ScriptEditScreen(ScriptListScreen.this, scriptName, script));
            }).width(44).build();
            renameButton = ButtonWidget.builder(Text.translatable("maiw.gui.script_list.rename"),btn->{
                client.setScreen(new ScriptRenameScreen(ScriptListScreen.this, scriptName, new_name ->{
                    renameScript(scriptName, new_name, script);
                }));
            }).width(44).build();
            duplicateButton = ButtonWidget.builder(Text.translatable("maiw.gui.script_list.duplicate"),btn->{duplicateScript(scriptName, script);}).width(44).build();
            delButton = ButtonWidget.builder(Text.translatable("maiw.gui.script_list.del"),btn->{deleteIfConfirmed();}).width(44).build();
            children.add(nameWidget);
            children.add(runOrStopButton);
            children.add(editButton);
            children.add(renameButton);
            children.add(duplicateButton);
            children.add(delButton);
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
            nameWidget.setX(x+4);
            nameWidget.setY(y+4);
            runOrStopButton.setX(x + entryWidth - 225);
            runOrStopButton.setY(y);
            editButton.setX(x + entryWidth - 180);
            editButton.setY(y);
            renameButton.setX(x + entryWidth - 135);
            renameButton.setY(y);
            duplicateButton.setX(x + entryWidth - 90);
            duplicateButton.setY(y);
            delButton.setX(x + entryWidth - 45);
            delButton.setY(y);
            if(scriptName.equals(playerState.scriptName)){
                runOrStopButton.setMessage(Text.translatable("maiw.gui.script_list.stop"));
            }else {
                runOrStopButton.setMessage(Text.translatable("maiw.gui.script_list.run"));
            }
            children.forEach(child -> {child.render(context, mouseX, mouseY, tickDelta);});
        }

        public void deleteIfConfirmed() {
            client.setScreen(new ConfirmNoPauseScreen(confirmed -> {
                if (confirmed) {
                    delScript(scriptName);
                }
                client.setScreen(ScriptListScreen.this);
            }, Text.translatable("maiw.gui.script_list.del.title"), Text.translatable("maiw.gui.script_list.del.confirm", scriptName), Text.translatable("maiw.gui.confirm"), Text.translatable("maiw.gui.cancel")));
        }
    }
}
