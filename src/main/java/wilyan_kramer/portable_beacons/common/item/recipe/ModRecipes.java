package wilyan_kramer.portable_beacons.common.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import wilyan_kramer.portable_beacons.common.effect.ModPotions;
import wilyan_kramer.portable_beacons.common.item.ItemList;

public class ModRecipes {
	
	private static ItemStack createPotion(Potion potion) {
		return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
	}
	public static void registerRecipes() {
		BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ItemList.starberries), createPotion(ModPotions.glowing)));
		BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(Ingredient.of(createPotion(ModPotions.glowing)), Ingredient.of(Items.REDSTONE), createPotion(ModPotions.long_glowing)));
	}
}
