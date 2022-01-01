package wilyan_kramer.portable_beacons.common.item.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
	private static ModBrewingRecipe createNecklaceRecipe(ItemStack ingredient, Potion potion) {
		return new ModBrewingRecipe(Ingredient.of(ItemList.potion_necklace), Ingredient.of(ingredient), PotionUtils.setPotion(new ItemStack(ItemList.potion_necklace), potion));
	}
	private static ModBrewingRecipe createDaggerRecipe(ItemStack ingredient, List<EffectInstance> effects) {
		return new ModBrewingRecipe(Ingredient.of(ItemList.infused_dagger), Ingredient.of(ingredient), PotionUtils.setCustomEffects(new ItemStack(ItemList.infused_dagger), effects));
	}
	private static ModBrewingRecipe createDaggerRecipe(ItemStack ingredient, Potion potion) {
		return new ModBrewingRecipe(Ingredient.of(createPotion(potion)), Ingredient.of(ItemList.infused_dagger), PotionUtils.setPotion(new ItemStack(ItemList.infused_dagger), potion));
	}
	public static void registerRecipes() {
		BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ItemList.starberries), createPotion(ModPotions.glowing)));
		BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(Ingredient.of(createPotion(ModPotions.glowing)), Ingredient.of(Items.REDSTONE), createPotion(ModPotions.long_glowing)));
		BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(Items.SHULKER_SHELL), createPotion(ModPotions.levitation)));
		BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(Ingredient.of(createPotion(ModPotions.levitation)), Ingredient.of(Items.REDSTONE), createPotion(ModPotions.long_levitation)));
		BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(Ingredient.of(createPotion(ModPotions.levitation)), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(ModPotions.strong_levitation)));

		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(Items.BLAZE_POWDER), Potions.STRENGTH));
		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(Items.GOLDEN_CARROT), Potions.NIGHT_VISION));
		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(Items.MAGMA_CREAM), Potions.FIRE_RESISTANCE));
		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(Items.SHULKER_SHELL), ModPotions.levitation));
		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(Items.RABBIT_FOOT), Potions.LEAPING));
		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(ItemList.starberries), ModPotions.glowing));
		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(Items.SUGAR), Potions.SWIFTNESS));
		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(Items.TURTLE_HELMET), Potions.TURTLE_MASTER));
		BrewingRecipeRegistry.addRecipe(createNecklaceRecipe(new ItemStack(Items.PHANTOM_MEMBRANE), Potions.SLOW_FALLING));

		
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.BLAZE_POWDER), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.WEAKNESS, 400)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.GHAST_TEAR), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.REGENERATION, 100)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.GOLDEN_CARROT), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.BLINDNESS, 50)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.SHULKER_SHELL), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.LEVITATION, 200)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.SHULKER_SHELL), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.LEVITATION, 200)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.PHANTOM_MEMBRANE), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.SLOW_FALLING, 400)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.SPIDER_EYE), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.POISON, 60)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.SPONGE), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.DIG_SLOWDOWN, 100)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(ItemList.starberries), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.GLOWING, 400)))));
		BrewingRecipeRegistry.addRecipe(createDaggerRecipe(new ItemStack(Items.SUGAR), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 50)))));
		
	}
}
