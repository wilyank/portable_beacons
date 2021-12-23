package wilyan_kramer.portable_beacons.common.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.item.ItemList;


public class Recipes extends RecipeProvider implements IConditionBuilder {

	public Recipes(DataGenerator generatorIn) {
		super(generatorIn);
	}
	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, Items.SUGAR, Potions.SWIFTNESS);
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, Items.BLAZE_POWDER, Potions.STRENGTH);
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, Items.RABBIT_FOOT, Potions.LEAPING);
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
		
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, ItemList.starberries, Effects.GLOWING, 400);
		this.addInfusionRecipe(consumer, ItemList.potion_necklace, Items.SHULKER_SHELL, Effects.LEVITATION, 400);

		
		this.addInfusionRecipe(consumer, ItemList.infused_dagger, Items.BLAZE_POWDER, Items.FERMENTED_SPIDER_EYE, Effects.WEAKNESS, 400);
		this.addInfusionRecipe(consumer, ItemList.infused_dagger, Items.SUGAR, Items.FERMENTED_SPIDER_EYE, Effects.MOVEMENT_SLOWDOWN, 50);
		this.addInfusionRecipe(consumer, ItemList.infused_dagger, Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE, Effects.BLINDNESS, 50);
		this.addInfusionRecipe(consumer, ItemList.infused_dagger, Items.PHANTOM_MEMBRANE, Effects.SLOW_FALLING, 400);
		this.addInfusionRecipe(consumer, ItemList.infused_dagger, Items.SPONGE, Effects.DIG_SLOWDOWN, 100);
		this.addInfusionRecipe(consumer, ItemList.infused_dagger, Items.SPIDER_EYE, Effects.POISON, 60);
		this.addInfusionRecipe(consumer, ItemList.infused_dagger, Items.GHAST_TEAR, Effects.REGENERATION, 100);
		this.addInfusionRecipe(consumer,  ItemList.infused_dagger, Items.SHULKER_SHELL, Effects.LEVITATION, 200);
		this.addInfusionRecipe(consumer, ItemList.infused_dagger, ItemList.starberries, Effects.GLOWING, 400);
		
		ShapelessRecipeBuilder.shapeless(Items.NETHER_STAR, 9)
		.requires(ItemList.nether_star_block)
		.unlockedBy("has_item", has(Items.NETHER_STAR))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, "nether_star_block_to_nether_star"));
		
		ShapedRecipeBuilder.shaped(ItemList.nether_star_block)
		.pattern("xxx")
		.pattern("xxx")
		.pattern("xxx")
		.define('x', Items.NETHER_STAR)
		.group("")
		.unlockedBy("has_item", has(Items.NETHER_STAR))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, "nether_star_to_nether_star_block"));
		
		ShapedRecipeBuilder.shaped(ItemList.potion_necklace)
		.pattern("cCc")
		.pattern("RBR")
		.pattern("InI")
		.define('c', Items.CHAIN)
		.define('C', Items.CONDUIT)
		.define('R', Items.BLAZE_ROD)
		.define('B', Items.BEACON)
		.define('I', Items.IRON_BLOCK)
		.define('n', Items.NETHERITE_INGOT)
		.group("")
		.unlockedBy("has_item", has(Items.BEACON))
		.save(consumer, new ResourceLocation("portable_beacons", "potion_necklace"));
		
		ShapedRecipeBuilder.shaped(ItemList.beacon_backpack_0)
		.pattern("LggL")
		.pattern("LBCL")
		.pattern("InnI")
		.define('g', Items.GLASS)
		//.define('s', Items.STRING)
		.define('C', Items.CONDUIT)
		.define('L', Items.LEATHER)
		.define('I', Items.IRON_BLOCK)
		.define('n', Items.NETHERITE_INGOT)
		.define('B', Items.BEACON)
		.group("")
		.unlockedBy("has_item", has(Items.BEACON))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.beacon_backpack_0.toString()));;
		
		this.addUpgradingRecipe(consumer, ItemList.beacon_backpack_0, ItemList.beacon_backpack_1, Items.GOLD_BLOCK);
		this.addUpgradingRecipe(consumer, ItemList.beacon_backpack_1, ItemList.beacon_backpack_2, Items.DIAMOND_BLOCK);
		this.addUpgradingRecipe(consumer, ItemList.beacon_backpack_2, ItemList.beacon_backpack_3, Items.EMERALD_BLOCK);
		this.addUpgradingRecipe(consumer, ItemList.beacon_backpack_3, ItemList.beacon_backpack_4, Items.NETHERITE_BLOCK);
		
		ShapedRecipeBuilder.shaped(ItemList.diffuser)
		.pattern("GCG")
		.pattern("GNG")
		.pattern("BOB")
		.define('C', Items.CHISELED_NETHER_BRICKS)
		.define('G', Items.GOLD_BLOCK)
		.define('N', Items.NETHERITE_BLOCK)
		.define('B', Items.NETHER_BRICKS)
		.define('O', Items.OBSIDIAN)
		.group("")
		.unlockedBy("has_item", has(Items.NETHERITE_BLOCK))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.diffuser.toString()));
		
		ShapedRecipeBuilder.shaped(ItemList.bonk_stick)
		.pattern("S")
		.pattern("L")
		.pattern("L")
		.pattern("L")
		.define('S', Items.SPONGE)
		.define('L', ItemTags.LOGS)
		.group("")
		.unlockedBy("has_item", has(Items.SPONGE))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.bonk_stick.toString()));
		
		ShapedRecipeBuilder.shaped(ItemList.infused_dagger)
		.pattern("wnw")
		.pattern("wnw")
		.pattern(" R ")
		.define('w', Items.NETHER_WART)
		.define('n', Items.NETHERITE_INGOT)
		.define('R', Items.BLAZE_ROD)
		.group("")
		.unlockedBy("has_netherite", has(Items.NETHERITE_INGOT))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.infused_dagger.toString()));
		
		
//		ConditionalRecipe.builder()
//		.addCondition(modLoaded("alexsmobs"))
//		.addRecipe(ShapelessNBTRecipeBuilder
//				.shapeless(
//						PotionUtils.setCustomEffects(
//								new ItemStack(ItemList.infused_dagger), 
//								EffectHelper.setProperties(
//										new ArrayList<EffectInstance>(Arrays.asList( new EffectInstance(
//												ForgeRegistries.POTIONS.getValue(
//														new ResourceLocation(
//																"alexsmobs", "sunbird_curse")
//														), 100, 0, true, true, true)
//												)
//											)
//									)
//						)
//				).requires(ItemList.infused_dagger)
//				.requires(Items.FIREWORK_ROCKET)
//				.requires(Items.FERMENTED_SPIDER_EYE)
//				.requires(Items.SUNFLOWER)
//				.unlockedBy("has_dirt", has(Blocks.DIRT))::save
//				).build(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.infused_dagger.toString() + "_sunflower"));;
		
//		ConditionalRecipe.builder()
//		.addCondition(modLoaded("alexsmobs"))
//		.addRecipe(
//				ShapelessRecipeBuilder
//				.shapeless(Items.DIAMOND)
//				.requires(Items.DIAMOND)
//				.group("")
//				.unlockedBy("has_dirt", has(Blocks.DIRT))::save)
//		.build(consumer, new ResourceLocation(PortableBeaconsMod.MODID, Items.DIAMOND.toString()));	
	}
	
	private void addUpgradingRecipe(Consumer<IFinishedRecipe> consumer, Item input, Item output, Item upgrader) {
		ShapedRecipeBuilder.shaped(output)
		.pattern("ooo")
		.pattern("oxo")
		.pattern("ooo")
		.define('o', upgrader)
		.define('x', input)
		.group("")
		.unlockedBy("has_item", has(input))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, output.toString()));
	}
	
	@SuppressWarnings("unused")
	private void addInfusionRecipe(Consumer<IFinishedRecipe> consumer, Item infused, Item primaryInput, Item secondaryInput, Potion potion) {
		ShapelessNBTRecipeBuilder.shapeless(PotionUtils.setPotion(new ItemStack(infused), potion))
		.requires(primaryInput)
		.requires(secondaryInput)
		.requires(infused)
		.group("")
		.unlockedBy("has_dagger", has(infused))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, infused.toString().concat("_").concat(primaryInput.toString())));
	}
	private void addInfusionRecipe(Consumer<IFinishedRecipe> consumer, Item infused, Item input, Potion potion) {
		ShapelessNBTRecipeBuilder.shapeless(PotionUtils.setPotion(new ItemStack(infused), potion))
		.requires(input)
		.requires(infused)
		.group("")
		.unlockedBy("has_dagger", has(infused))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, infused.toString().concat("_").concat(input.toString())));
	}
	
	private void addInfusionRecipe(Consumer<IFinishedRecipe> consumer, Item infused, Item input, Effect effect, int effectDuration) {
		EffectInstance effInst = new EffectInstance(effect, effectDuration, 0, true, true, true);
		effInst.setCurativeItems(new ArrayList<ItemStack>(Arrays.asList(new ItemStack(Items.MILK_BUCKET))));

		ShapelessNBTRecipeBuilder.shapeless(PotionUtils.setCustomEffects(new ItemStack(infused), new ArrayList<EffectInstance>(Arrays.asList(effInst))))
		.requires(input)
		.requires(infused)
		.group("")
		.unlockedBy("has_dagger", has(infused))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, infused.toString().concat("_").concat(input.toString())));
	}
	
	private void addInfusionRecipe(Consumer<IFinishedRecipe> consumer, Item infused, Item primaryInput, Item secondaryInput, Effect effect, int effectDuration) {
		addInfusionRecipe(consumer, infused, primaryInput, secondaryInput, effect, effectDuration, 0);
	}
	private void addInfusionRecipe(Consumer<IFinishedRecipe> consumer, Item infused, Item primaryInput, Item secondaryInput, Effect effect, int effectDuration, int amplifier) {
		EffectInstance effInst = new EffectInstance(effect, effectDuration, amplifier, true, true, true);
		effInst.setCurativeItems(new ArrayList<ItemStack>(Arrays.asList(new ItemStack(Items.MILK_BUCKET))));
		
		ShapelessNBTRecipeBuilder.shapeless(PotionUtils.setCustomEffects(new ItemStack(infused), new ArrayList<EffectInstance>(Arrays.asList(effInst))))
		.requires(primaryInput)
		.requires(secondaryInput)
		.requires(infused)
		.group("")
		.unlockedBy("has_dagger", has(infused))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, infused.toString().concat("_").concat(primaryInput.toString())));
	}
}	
