package fr.frinn.custommachinery.client.integration.jei;

import com.mojang.datafixers.util.Pair;
import fr.frinn.custommachinery.CustomMachinery;
import fr.frinn.custommachinery.api.crafting.IMachineRecipe;
import fr.frinn.custommachinery.api.integration.jei.IDisplayInfo;
import fr.frinn.custommachinery.common.machine.CustomMachine;
import fr.frinn.custommachinery.impl.util.TextureSizeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RequirementDisplayInfo implements IDisplayInfo {

    private static final IDisplayInfoRenderer DEFAULT_RENDERER = new Texture(CustomMachinery.rl("textures/gui/create_icon.png"), 10, 10, 0, 0);

    private IDisplayInfoRenderer renderer = DEFAULT_RENDERER;
    private final List<Pair<Component, TooltipPredicate>> tooltips = new ArrayList<>();
    private ClickAction clickAction;

    @Override
    public RequirementDisplayInfo setTextureIcon(ResourceLocation icon, int width, int height, int u, int v) {
        this.renderer = new Texture(icon, width, height, u, v);

        return this;
    }

    @Override
    public IDisplayInfo setSpriteIcon(ResourceLocation atlas, ResourceLocation sprite) {
        this.renderer = new Sprite(atlas, sprite);
        return this;
    }

    @Override
    public RequirementDisplayInfo setItemIcon(ItemStack stack) {
        this.renderer = new Item(stack);
        return this;
    }

    @Override
    public RequirementDisplayInfo addTooltip(Component tooltip, TooltipPredicate predicate) {
        this.tooltips.add(Pair.of(tooltip, predicate));
        return this;
    }

    @Override
    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public void renderIcon(GuiGraphics graphics, int size) {
        this.renderer.render(graphics, size);
    }

    public List<Pair<Component, TooltipPredicate>> getTooltips() {
        return this.tooltips;
    }

    public boolean hasClickAction() {
        return this.clickAction != null;
    }

    public boolean handleClick(CustomMachine machine, IMachineRecipe recipe, int button) {
        if(hasClickAction()) {
            this.clickAction.handleClick(machine, recipe, button);
            return true;
        }
        return false;
    }

    public boolean shouldRender() {
        return this.renderer != DEFAULT_RENDERER || !this.tooltips.isEmpty() || this.hasClickAction();
    }

    public interface IDisplayInfoRenderer {
        void render(GuiGraphics graphics, int size);
    }

    private record Item(ItemStack stack) implements IDisplayInfoRenderer {
        @Override
        public void render(GuiGraphics graphics, int size) {
            graphics.pose().scale(size / 16.0F, size / 16.0F, 1.0F);
            graphics.renderItem(this.stack, 0, 0);
        }
    }

    private record Texture(ResourceLocation icon, int width, int height, int u, int v) implements IDisplayInfoRenderer {
        @Override
        public void render(GuiGraphics graphics, int size) {
            int textureWidth = TextureSizeHelper.getTextureWidth(this.icon);
            int textureHeight = TextureSizeHelper.getTextureHeight(this.icon);
            graphics.blit(this.icon, 0, 0, size, size, this.u, this.v, textureWidth, textureHeight, textureWidth, textureHeight);
        }
    }

    private record Sprite(ResourceLocation atlas, ResourceLocation sprite) implements IDisplayInfoRenderer {
        @Override
        public void render(GuiGraphics graphics, int size) {
            graphics.blit(0, 0, 0, size, size, Minecraft.getInstance().getTextureAtlas(this.atlas).apply(this.sprite));
        }
    }
}
