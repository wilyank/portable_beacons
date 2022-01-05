package wilyan_kramer.portable_beacons.common.datagen;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class BenchUpgradeRecipeBuilder {
	private final Ingredient input;
	private int xp = 100;
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	private String group;
	private String profession;
	
	public BenchUpgradeRecipeBuilder(Ingredient input) {
		this.input = input;
	}
	public BenchUpgradeRecipeBuilder addRecipe(ITag<Item> input) {
		return new BenchUpgradeRecipeBuilder(Ingredient.of(input));
	}

	public static BenchUpgradeRecipeBuilder addRecipe(IItemProvider input) {
		return new BenchUpgradeRecipeBuilder(Ingredient.of(input));
	}
	public static BenchUpgradeRecipeBuilder addRecipe(Ingredient input) {
		return new BenchUpgradeRecipeBuilder(input);
	}
	
	public BenchUpgradeRecipeBuilder setProfession(String profession) {
		if (profession == "potioneer" || profession == "artificer" || profession == "summoner") {
			this.profession = profession;
			return this;
		}
		else throw new IllegalStateException("Recipe has an unknown profession " + profession);
	}
	
	public BenchUpgradeRecipeBuilder setExperience(int amount) {
		this.xp = amount;
		return this;
	}
	
	public BenchUpgradeRecipeBuilder unlockedBy(String p_200483_1_, ICriterionInstance p_200483_2_) {
		this.advancement.addCriterion(p_200483_1_, p_200483_2_);
		return this;
	}

	public BenchUpgradeRecipeBuilder group(String p_200490_1_) {
		this.group = p_200490_1_;
		return this;
	}

	public void save(Consumer<IFinishedRecipe> p_200482_1_) {
		this.save(p_200482_1_, new ResourceLocation(PortableBeaconsMod.MODID, this.input.toString()));
	}

	public void save(Consumer<IFinishedRecipe> p_200484_1_, String p_200484_2_) {
		ResourceLocation resourcelocation = new ResourceLocation(PortableBeaconsMod.MODID, this.input.toString());
		if ((new ResourceLocation(p_200484_2_)).equals(resourcelocation)) {
			throw new IllegalStateException("Shapeless Recipe " + p_200484_2_ + " should remove its 'save' argument");
		} else {
			this.save(p_200484_1_, new ResourceLocation(p_200484_2_));
		}
	}

	public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeName) {
		this.ensureValid(recipeName);
		this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeName)).rewards(AdvancementRewards.Builder.recipe(recipeName)).requirements(IRequirementsStrategy.OR);
		consumer.accept(new BenchUpgradeRecipeBuilder.Result(
				recipeName, 
				this.input, 
				this.group == null ? "" : this.group, 
				this.profession,
				this.xp, 
				this.advancement, 
				new ResourceLocation(
						recipeName.getNamespace(), 
						"recipes/" + recipeName.getPath())
				));
	}

	private void ensureValid(ResourceLocation p_200481_1_) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + p_200481_1_);
		}
	}
	public static class Result implements IFinishedRecipe {

		private final ResourceLocation id;
		private final Ingredient input;
		private final String profession;
		private final int xp;
		private final String group;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;
		
		public Result(ResourceLocation recipeName, Ingredient input, String recipeGroup, String profession, int xp, Advancement.Builder advancement,
				ResourceLocation advancementId) {
			this.id = recipeName;
			this.input = input;
			this.profession = profession;
			this.xp = xp;
			this.group = recipeGroup;
			this.advancement = advancement;
			this.advancementId = advancementId;
		}

		@Override
		public void serializeRecipeData(JsonObject object) {
			if (!this.group.isEmpty()) {
				object.addProperty("group", this.group);
			}
			object.add("input", input.toJson());
			object.addProperty("profession", this.profession);
			object.addProperty("xp", this.xp);

		}

		public IRecipeSerializer<?> getType() {
			return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(PortableBeaconsMod.MODID, "bench_upgrade"));
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
