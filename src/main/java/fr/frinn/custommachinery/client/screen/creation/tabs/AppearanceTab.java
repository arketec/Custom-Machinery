package fr.frinn.custommachinery.client.screen.creation.tabs;

import fr.frinn.custommachinery.CustomMachinery;
import fr.frinn.custommachinery.client.screen.creation.AppearanceListWidget;
import fr.frinn.custommachinery.client.screen.creation.MachineEditScreen;
import fr.frinn.custommachinery.client.screen.popup.ConfirmPopup;
import fr.frinn.custommachinery.common.machine.builder.MachineAppearanceBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public class AppearanceTab extends MachineEditTab {

    private final CycleButton<MachineAppearanceBuilder> builderButton;
    private final AppearanceListWidget appearanceList;

    public AppearanceTab(MachineEditScreen parent) {
        super(Component.translatable("custommachinery.gui.creation.tab.appearance"), parent);
        GridLayout.RowHelper row = this.layout.createRowHelper(2);
        this.layout.defaultCellSetting().paddingTop(5);
        this.builderButton = row.addChild(new CycleButton.Builder<MachineAppearanceBuilder>(builder -> builder.getStatus() == null ? Component.literal("Default") : builder.getStatus().getTranslatedName())
                .withValues(parent.getBuilder().getAppearanceBuilders())
                .displayOnlyValue()
                .withTooltip(builder -> Tooltip.create(Component.translatable("custommachinery.gui.creation.appearance.status." + (builder.getStatus() == null ? "default" : builder.getStatus().getSerializedName()))))
                .create(0, 0, 100, 20, Component.literal("Machine status"), (button, value) -> this.initList()), row.newCellSettings().alignHorizontallyCenter().alignVerticallyTop());
        WidgetSprites resetSprites = new WidgetSprites(CustomMachinery.rl("creation/reset_button"), CustomMachinery.rl("creation/reset_button_hovered"));
        ImageButton resetButton = row.addChild(new ImageButton(0, 0, 20, 20, resetSprites, button -> this.resetAppearance()));
        resetButton.setTooltip(Tooltip.create(Component.translatable("custommachinery.gui.creation.appearance.reset.tooltip")));
        this.appearanceList = row.addChild(new AppearanceListWidget(parent.x, parent.y + 50, parent.xSize - 20, parent.ySize - 40, 30, this.builderButton::getValue, this.parent), 2);
    }

    public void initList() {
        this.appearanceList.init();
    }

    public void resetAppearance() {
        Component status = Component.translatable("custommachinery.craftingstatus." + (this.builderButton.getValue().getStatus() == null ? "default" : this.builderButton.getValue().getStatus().getSerializedName())).withStyle(ChatFormatting.GOLD);
        this.parent.openPopup(new ConfirmPopup(this.parent, 116, 96, () -> {
            this.builderButton.getValue().reset();
            this.appearanceList.init();
        })
        .title(Component.translatable("custommachinery.gui.creation.appearance.reset.title"))
        .text(Component.translatable("custommachinery.gui.creation.appearance.reset.text", status)));
    }
}
