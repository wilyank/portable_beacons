package wilyan_kramer.portable_beacons.common.item.recipe;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class BenchUpgradeRecipeSerializer<T extends BenchUpgradeRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

	private final BenchUpgradeRecipeSerializer.IFactory<T> factory;

	public BenchUpgradeRecipeSerializer(BenchUpgradeRecipeSerializer.IFactory<T> factory) 
	{
		this.factory = factory;
	}
	
	@Override
	public T fromJson(ResourceLocation id, JsonObject object) {
		String group = JSONUtils.getAsString(object, "group", "");
		Ingredient input = Ingredient.fromJson(object.get("input"));
		String profession = JSONUtils.getAsString(object, "profession");
		int xp = JSONUtils.getAsInt(object, "xp", 100);
		return this.factory.create(id, group, input, profession, xp);
	}

	@Override
	public T fromNetwork(ResourceLocation id, PacketBuffer buffer) {
		String group = buffer.readUtf(32767);
		Ingredient input = Ingredient.fromNetwork(buffer);
		String profession = buffer.readUtf();
		int xp = buffer.readVarInt();
		
		return this.factory.create(id, group, input, profession, xp);
	}

	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		buffer.writeUtf(recipe.group);
		recipe.input.toNetwork(buffer);
		buffer.writeUtf(recipe.profession);
		buffer.writeVarInt(recipe.xp);		
	}
	public interface IFactory<T extends BenchUpgradeRecipe> 
	{
		T create(ResourceLocation id, String group, Ingredient input, String profession, int xp);
	}

}
