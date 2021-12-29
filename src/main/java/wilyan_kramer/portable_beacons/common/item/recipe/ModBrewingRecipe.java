package wilyan_kramer.portable_beacons.common.item.recipe;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

public class ModBrewingRecipe extends BrewingRecipe {
	
	@Nonnull private final Ingredient input;
    @Nonnull private final Ingredient ingredient;
    @Nonnull private final ItemStack output;

    public ModBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        super(input, ingredient, output);
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack) {
        if (stack == null) {
            return false;
        } else {
            ItemStack[] matchingStacks = input.getItems();
            if (matchingStacks.length == 0) {
                return stack.isEmpty();
            } else {
                for (ItemStack itemstack : matchingStacks) {
                    if (itemstack.sameItem(stack) && ItemStack.tagMatches(itemstack, stack)) {
                        return true;
                    }
                }

                return false;
            }
        }
    }
}
