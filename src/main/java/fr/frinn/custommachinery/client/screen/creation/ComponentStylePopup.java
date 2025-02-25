package fr.frinn.custommachinery.client.screen.creation;

import fr.frinn.custommachinery.CustomMachinery;
import fr.frinn.custommachinery.client.screen.BaseScreen;
import fr.frinn.custommachinery.client.screen.popup.PopupScreen;
import fr.frinn.custommachinery.client.screen.widget.ComponentEditBox;
import fr.frinn.custommachinery.client.screen.widget.ToggleImageButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ComponentStylePopup extends PopupScreen {

    private static final WidgetSprites EXIT_SPRITES = new WidgetSprites(CustomMachinery.rl("config/exit_button"), CustomMachinery.rl("config/exit_button_hovered"));

    private final ComponentEditBox editBox;

    public ComponentStylePopup(BaseScreen parent, ComponentEditBox editBox) {
        super(parent, 64, 82);
        this.editBox = editBox;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new ImageButton(this.x + 5, this.y + 5, 9, 9, EXIT_SPRITES, button -> this.parent.closePopup(this)));
        //Colors
        GridLayout colorsLayout = new GridLayout(this.x, this.y + 5);
        colorsLayout.defaultCellSetting().alignHorizontallyCenter();
        RowHelper row = colorsLayout.rowSpacing(3).columnSpacing(3).createRowHelper(4);
        row.addChild(new StringWidget(Component.translatable("custommachinery.gui.popup.style"), this.mc.font), 4);
        for(int i = 0; i < 16; i++) {
            ChatFormatting format = ChatFormatting.getById(i);
            if(format == null)
                continue;
            String name = format.getName();
            WidgetSprites sprites = new WidgetSprites(CustomMachinery.rl("creation/style/" + name), CustomMachinery.rl("creation/style/" + name + "_selected"));
            ImageButton button = new ImageButton(0, 0, 10, 10, sprites, b -> this.editBox.setStyle(this.editBox.getStyle().applyFormat(format)));
            row.addChild(button);
            button.setTooltip(Tooltip.create(Component.translatable(format.getName())));
        }
        colorsLayout.arrangeElements();
        colorsLayout.visitWidgets(this::addRenderableWidget);
        colorsLayout.setX(this.x + this.xSize / 2 - colorsLayout.getWidth() / 2);

        //Style
        GridLayout styleLayout = new GridLayout(this.x, this.y + this.ySize - 15);
        styleLayout.defaultCellSetting().alignHorizontallyCenter();
        row = styleLayout.rowSpacing(3).columnSpacing(2).createRowHelper(5);
        AtomicInteger index = new AtomicInteger(16);
        for(ChatFormatting format : List.of(ChatFormatting.BOLD, ChatFormatting.ITALIC, ChatFormatting.UNDERLINE, ChatFormatting.STRIKETHROUGH, ChatFormatting.OBFUSCATED)) {
            String name = format.getName();
            WidgetSprites sprites = new WidgetSprites(CustomMachinery.rl("creation/style/" + name), CustomMachinery.rl("creation/style/" + name + "_selected"));
            ToggleImageButton button = new ToggleImageButton(0, 0, 10, 10, sprites, b -> this.editBox.invert(format));
            row.addChild(button);
            button.setTooltip(Tooltip.create(Component.translatable(format.getName())));
            if(format == ChatFormatting.BOLD)
                button.setToggle(this.editBox.getStyle().isBold());
            else if(format == ChatFormatting.ITALIC)
                button.setToggle(this.editBox.getStyle().isItalic());
            else if(format == ChatFormatting.UNDERLINE)
                button.setToggle(this.editBox.getStyle().isUnderlined());
            else if(format == ChatFormatting.STRIKETHROUGH)
                button.setToggle(this.editBox.getStyle().isStrikethrough());
            else if(format == ChatFormatting.OBFUSCATED)
                button.setToggle(this.editBox.getStyle().isObfuscated());
        }
        styleLayout.arrangeElements();
        styleLayout.visitWidgets(this::addRenderableWidget);
        styleLayout.setX(this.x + this.xSize / 2 - styleLayout.getWidth() / 2);
    }
}
