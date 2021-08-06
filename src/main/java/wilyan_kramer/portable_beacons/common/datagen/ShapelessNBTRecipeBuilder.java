package wilyan_kramer.portable_beacons.common.datagen;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class ShapelessNBTRecipeBuilder {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ItemStack result;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	private String group;

	public ShapelessNBTRecipeBuilder(ItemStack resultItemStack) {
		this.result = resultItemStack;
	}

	public static ShapelessNBTRecipeBuilder shapeless(ItemStack itemStack) {
		return new ShapelessNBTRecipeBuilder(itemStack);
	}


	public ShapelessNBTRecipeBuilder requires(ITag<Item> p_203221_1_) {
		return this.requires(Ingredient.of(p_203221_1_));
	}

	public ShapelessNBTRecipeBuilder requires(IItemProvider p_200487_1_) {
		return this.requires(p_200487_1_, 1);
	}

	public ShapelessNBTRecipeBuilder requires(IItemProvider p_200491_1_, int p_200491_2_) {
		for(int i = 0; i < p_200491_2_; ++i) {
			this.requires(Ingredient.of(p_200491_1_));
		}

		return this;
	}

	public ShapelessNBTRecipeBuilder requires(Ingredient p_200489_1_) {
		return this.requires(p_200489_1_, 1);
	}

	public ShapelessNBTRecipeBuilder requires(Ingredient p_200492_1_, int p_200492_2_) {
		for(int i = 0; i < p_200492_2_; ++i) {
			this.ingredients.add(p_200492_1_);
		}

		return this;
	}

	public ShapelessNBTRecipeBuilder unlockedBy(String p_200483_1_, ICriterionInstance p_200483_2_) {
		this.advancement.addCriterion(p_200483_1_, p_200483_2_);
		return this;
	}

	public ShapelessNBTRecipeBuilder group(String p_200490_1_) {
		this.group = p_200490_1_;
		return this;
	}

	public void save(Consumer<IFinishedRecipe> p_200482_1_) {
		this.save(p_200482_1_, new ResourceLocation(PortableBeaconsMod.MODID, this.result.toString()));
	}

	public void save(Consumer<IFinishedRecipe> p_200484_1_, String p_200484_2_) {
		ResourceLocation resourcelocation = new ResourceLocation(PortableBeaconsMod.MODID, this.result.toString());
		if ((new ResourceLocation(p_200484_2_)).equals(resourcelocation)) {
			throw new IllegalStateException("Shapeless Recipe " + p_200484_2_ + " should remove its 'save' argument");
		} else {
			this.save(p_200484_1_, new ResourceLocation(p_200484_2_));
		}
	}

	public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeName) {
		this.ensureValid(recipeName);
		this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeName)).rewards(AdvancementRewards.Builder.recipe(recipeName)).requirements(IRequirementsStrategy.OR);
		consumer.accept(new ShapelessNBTRecipeBuilder.Result(
				recipeName, 
				this.result, 
				this.group == null ? "" : this.group, 
				this.ingredients, this.advancement, 
				new ResourceLocation(
						recipeName.getNamespace(), 
						"recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + recipeName.getPath())
				));
	}

	private void ensureValid(ResourceLocation p_200481_1_) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + p_200481_1_);
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final ItemStack result;
		private final String group;
		private final List<Ingredient> ingredients;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation recipeId, ItemStack resultItemStack, String recipeGroup, List<Ingredient> ingredients, Advancement.Builder advancement, ResourceLocation advancementId) {
			this.id = recipeId;
			this.result = resultItemStack;
			this.group = recipeGroup;
			this.ingredients = ingredients;
			this.advancement = advancement;
			this.advancementId = advancementId;
		}

		public void serializeRecipeData(JsonObject object) {
			if (!this.group.isEmpty()) {
				object.addProperty("group", this.group);
			}

			JsonArray jsonarray = new JsonArray();

			for(Ingredient ingredient : this.ingredients) {
				jsonarray.add(ingredient.toJson());
			}

			object.add("ingredients", jsonarray);
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", this.result.getItem().getRegistryName().toString());
			if (this.result.getCount() > 1) {
				jsonobject.addProperty("count", this.result.getCount());
			}
			if (this.result.hasTag()) {
				jsonobject.add("nbt", tagToJson(this.result.getTag()));
			}

			object.add("result", jsonobject);
		}
		
		private JsonObject tagToJson(CompoundNBT tag) {
			JsonObject object = new JsonObject();
			for (String key : tag.getAllKeys()) 
			{	
				byte tagType = tag.getTagType(key);
				switch(tagType) {
				case 0:
					return object;
				case 1:
					object.addProperty(key, tag.getByte(key));
					break;
				case 2:
					object.addProperty(key, tag.getShort(key));
					break;
				case 3:
					object.addProperty(key, tag.getInt(key));
					break;
				case 4:
					object.addProperty(key, tag.getLong(key));
					break;
				case 5:
					object.addProperty(key, tag.getFloat(key));
					break;
				case 6:
					object.addProperty(key, tag.getDouble(key));
					break;
				case 7:
					JsonArray byteList = new JsonArray();
					for (byte b : tag.getByteArray(key)) {
						byteList.add(b);
					}
					object.add(key, byteList);
					break;
				case 8:
					object.addProperty(key, tag.getString(key));
					break;
				case 9:
					JsonArray list = new JsonArray();
					for (INBT element : (ListNBT)tag.get(key)) {
						list.add(tagToJson((CompoundNBT) element));
					}
					object.add(key, list);
					break;
				case 10:
					object.add(key, tagToJson(tag.getCompound(key)));
					break;
				case 11:
					JsonArray intList = new JsonArray();
					for (int b : tag.getIntArray(key)) {
						intList.add(b);
					}
					object.add(key, intList);
					break;
				case 12:
					JsonArray longList = new JsonArray();
					for (long b : tag.getLongArray(key)) {
						longList.add(b);
					}
					object.add(key, longList);
					break;
				default:
					break;
				}
			}
			return object;
			
		}

		public IRecipeSerializer<?> getType() {
			return IRecipeSerializer.SHAPELESS_RECIPE;
		}

		public ResourceLocation getId() {
			return this.id;
		}

		@Nullable
		public JsonObject serializeAdvancement() {
			return this.advancement.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return this.advancementId;
		}
	}
}