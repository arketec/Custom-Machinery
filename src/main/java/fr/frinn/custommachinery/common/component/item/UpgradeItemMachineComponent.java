package fr.frinn.custommachinery.common.component.item;

import fr.frinn.custommachinery.CustomMachinery;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.component.ComponentIOMode;
import fr.frinn.custommachinery.api.component.IMachineComponentManager;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.common.init.Registration;
import fr.frinn.custommachinery.common.util.ingredient.IIngredient;
import fr.frinn.custommachinery.impl.component.config.SideConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class UpgradeItemMachineComponent extends ItemMachineComponent {

    public UpgradeItemMachineComponent(IMachineComponentManager manager, ComponentIOMode mode, String id, int capacity, int maxInput, int maxOutput, List<IIngredient<Item>> filter, boolean whitelist, SideConfig.Template configTemplate, boolean locked) {
        super(manager, mode, id, capacity, maxInput, maxOutput, filter, whitelist, configTemplate, locked);
    }

    @Override
    public MachineComponentType<ItemMachineComponent> getType() {
        return Registration.ITEM_UPGRADE_MACHINE_COMPONENT.get();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return super.isItemValid(slot, stack) && !CustomMachinery.UPGRADES.getUpgradesForItemAndMachine(stack.getItem(), this.getManager().getTile().getMachine().getId()).isEmpty();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        ItemStack remaining = super.insertItem(slot, stack, simulate);
        if(remaining != stack)
            this.getManager().getTile().getUpgradeManager().markDirty();
        return remaining;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extracted = super.extractItem(slot, amount, simulate);
        if(!extracted.isEmpty())
            this.getManager().getTile().getUpgradeManager().markDirty();
        return extracted;
    }

    public static class Template extends ItemMachineComponent.Template {

        public static final NamedCodec<Template> CODEC = defaultCodec(Template::new, "Upgrade item machine component");

        public Template(String id, ComponentIOMode mode, int capacity, Optional<Integer> maxInput, Optional<Integer> maxOutput, List<IIngredient<Item>> filter, boolean whitelist, Optional<SideConfig.Template> config, boolean locked) {
            super(id, mode, capacity, maxInput, maxOutput, filter, whitelist, config, locked);
        }

        @Override
        public MachineComponentType<ItemMachineComponent> getType() {
            return Registration.ITEM_UPGRADE_MACHINE_COMPONENT.get();
        }

        @Override
        public boolean isItemValid(IMachineComponentManager manager, ItemStack stack) {
            return !CustomMachinery.UPGRADES.getUpgradesForItemAndMachine(stack.getItem(), manager.getTile().getMachine().getId()).isEmpty();
        }

        @Override
        public ItemMachineComponent build(IMachineComponentManager manager) {
            return new UpgradeItemMachineComponent(manager, this.mode, this.id, this.capacity, this.maxInput, this.maxOutput, this.filter, this.whitelist, this.config, this.locked);
        }
    }
}
