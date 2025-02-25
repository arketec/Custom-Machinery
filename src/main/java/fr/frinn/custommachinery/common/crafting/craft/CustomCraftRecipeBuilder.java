package fr.frinn.custommachinery.common.crafting.craft;

import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.requirement.IRequirement;
import fr.frinn.custommachinery.impl.codec.DefaultCodecs;
import fr.frinn.custommachinery.impl.codec.NamedMapCodec;
import fr.frinn.custommachinery.impl.crafting.AbstractRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;

public class CustomCraftRecipeBuilder extends AbstractRecipeBuilder<CustomCraftRecipe> {

    public static final NamedMapCodec<CustomCraftRecipeBuilder> CODEC = NamedCodec.record(builderInstance ->
            builderInstance.group(
                    DefaultCodecs.RESOURCE_LOCATION.fieldOf("machine").forGetter(AbstractRecipeBuilder::getMachine),
                    DefaultCodecs.ITEM_OR_STACK.fieldOf("output").forGetter(builder -> builder.output),
                    IRequirement.CODEC.listOf().optionalFieldOf("requirements", Collections.emptyList()).forGetter(AbstractRecipeBuilder::getRequirements),
                    IRequirement.CODEC.listOf().optionalFieldOf("jei", Collections.emptyList()).forGetter(AbstractRecipeBuilder::getJeiRequirements),
                    NamedCodec.INT.optionalFieldOf("priority", 0).forGetter(AbstractRecipeBuilder::getPriority),
                    NamedCodec.INT.optionalFieldOf("jeiPriority", 0).forGetter(AbstractRecipeBuilder::getJeiPriority),
                    NamedCodec.BOOL.optionalFieldOf("hidden", false).forGetter(AbstractRecipeBuilder::isHidden)
            ).apply(builderInstance, (machine, output, requirements, jeiRequirements, priority, jeiPriority, hidden) -> {
                    CustomCraftRecipeBuilder builder = new CustomCraftRecipeBuilder(machine, output);
                    requirements.forEach(builder::withRequirement);
                    jeiRequirements.forEach(builder::withJeiRequirement);
                    builder.withPriority(priority);
                    builder.withJeiPriority(jeiPriority);
                    if(hidden)
                        builder.hide();
                    return builder;
            }), "Craft recipe builder"
    );

    private final ItemStack output;

    public CustomCraftRecipeBuilder(ResourceLocation machine, ItemStack output) {
        super(machine);
        this.output = output;
    }

    public CustomCraftRecipeBuilder(CustomCraftRecipe recipe) {
        super(recipe);
        this.output = recipe.getOutput();
    }

    @Override
    public CustomCraftRecipe build() {
        return new CustomCraftRecipe(this.getMachine(), this.output, this.getRequirements(), this.getJeiRequirements(), this.getPriority(), this.getJeiPriority(), this.isHidden());
    }
}
