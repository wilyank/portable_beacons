package wilyan_kramer.portable_beacons.common.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
		
		List<Item> itemList = new ArrayList<Item>(Arrays.asList(
				Items.GOLDEN_CARROT, 
				Items.SUGAR, 
				Items.BLAZE_POWDER, 
				Items.RABBIT_FOOT, 
				Items.PHANTOM_MEMBRANE, 
				Items.SHULKER_SHELL,
				Items.MAGMA_CREAM,
				Items.TURTLE_HELMET,
				ItemList.glowberries));
		
		for (Item item : itemList) {
			ShapelessRecipeBuilder.shapeless(ItemList.potion_necklace)
			.requires(ItemList.potion_necklace)
			.requires(item)
			.group("")
			.unlockedBy("has_necklace", has(ItemList.potion_necklace))
			.save(consumer, new ResourceLocation("portable_beacons", ItemList.potion_necklace.toString().concat("_").concat(item.toString())));
			
			ShapelessRecipeBuilder.shapeless(ItemList.infused_dagger)
			.requires(ItemList.infused_dagger)
			.requires(item)
			.requires(Items.FERMENTED_SPIDER_EYE)
			.group("")
			.unlockedBy("has_dagger", has(ItemList.infused_dagger))
			.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.infused_dagger.toString().concat("_").concat(item.toString())));
		}
		
		ShapelessRecipeBuilder.shapeless(ItemList.glowberries)
		.requires(Items.SWEET_BERRIES)
		.requires(Items.GLOWSTONE_DUST)
		.requires(Items.GLOWSTONE_DUST)
		.group("")
		.unlockedBy("has_item", has(Items.SWEET_BERRIES))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.glowberries.toString()));
		
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
		.pattern("sCs")
		.pattern("LBL")
		.pattern("InI")
		.define('s', Items.STRING)
		.define('C', Items.CONDUIT)
		.define('L', Items.LEATHER)
		.define('I', Items.IRON_BLOCK)
		.define('n', Items.NETHERITE_INGOT)
		.define('B', Items.BEACON)
		.group("")
		.unlockedBy("has_item", has(Items.BEACON))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.beacon_backpack_0.toString()));;
		
		ShapedRecipeBuilder.shaped(ItemList.beacon_backpack_1)
		.pattern("ooo")
		.pattern("oxo")
		.pattern("ooo")
		.define('o', Items.GOLD_BLOCK)
		.define('x', ItemList.beacon_backpack_0)
		.group("")
		.unlockedBy("has_item", has(ItemList.beacon_backpack_0))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.beacon_backpack_1.toString()));
		
		ShapedRecipeBuilder.shaped(ItemList.beacon_backpack_2)
		.pattern("ooo")
		.pattern("oxo")
		.pattern("ooo")
		.define('o', Items.DIAMOND_BLOCK)
		.define('x', ItemList.beacon_backpack_1)
		.group("")
		.unlockedBy("has_item", has(ItemList.beacon_backpack_1))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.beacon_backpack_2.toString()));
		
		ShapedRecipeBuilder.shaped(ItemList.beacon_backpack_3)
		.pattern("ooo")
		.pattern("oxo")
		.pattern("ooo")
		.define('o', Items.EMERALD_BLOCK)
		.define('x', ItemList.beacon_backpack_2)
		.group("")
		.unlockedBy("has_item", has(ItemList.beacon_backpack_2))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.beacon_backpack_3.toString()));
		
		ShapedRecipeBuilder.shaped(ItemList.beacon_backpack_4)
		.pattern("ooo")
		.pattern("oxo")
		.pattern("ooo")
		.define('o', Items.NETHERITE_BLOCK)
		.define('x', ItemList.beacon_backpack_3)
		.group("")
		.unlockedBy("has_item", has(ItemList.beacon_backpack_3))
		.save(consumer, new ResourceLocation(PortableBeaconsMod.MODID, ItemList.beacon_backpack_4.toString()));
		
		ShapedRecipeBuilder.shaped(ItemList.bonk_stick)
		.pattern("S")
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
	}
}	
