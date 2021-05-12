package fr.frinn.custommachinery.common.integration.jei.wrapper;

import fr.frinn.custommachinery.common.crafting.requirements.IRequirement;
import fr.frinn.custommachinery.common.integration.jei.CustomIngredientTypes;
import fr.frinn.custommachinery.common.integration.jei.energy.Energy;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredients;

public class EnergyIngredientWrapper implements IJEIIngredientWrapper<Energy> {

    private IRequirement.MODE mode;
    private int amount;
    private boolean isPerTick;

    public EnergyIngredientWrapper(IRequirement.MODE mode, int amount, boolean isPerTick) {
        this.mode = mode;
        this.amount = amount;
        this.isPerTick = isPerTick;
    }

    @Override
    public IIngredientType<Energy> getJEIIngredientType() {
        return CustomIngredientTypes.ENERGY;
    }

    @Override
    public Energy asJEIIngredient() {
        return new Energy(this.amount, this.isPerTick);
    }

    @Override
    public void addJeiIngredients(IIngredients ingredients) {
        if(this.mode == IRequirement.MODE.INPUT)
            ingredients.setInput(CustomIngredientTypes.ENERGY, new Energy(this.amount));
        else
            ingredients.setOutput(CustomIngredientTypes.ENERGY, new Energy(this.amount));
    }
}
