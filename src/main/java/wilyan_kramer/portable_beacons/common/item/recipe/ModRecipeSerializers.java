package wilyan_kramer.portable_beacons.common.item.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class ModRecipeSerializers {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = 
			DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PortableBeaconsMod.MODID);
	
	public static final RegistryObject<BenchUpgradeRecipeSerializer<BenchUpgradeRecipe>> BENCH_UPGRADE = RECIPE_SERIALIZERS.register("bench_upgrade", 
			() -> new BenchUpgradeRecipeSerializer<>(BenchUpgradeRecipe::new));
	
	
	public static <T extends IRecipe<?>> IRecipeType<T> registerRecipeType(String type) 
	{
		ResourceLocation recipeTypeId = new ResourceLocation(PortableBeaconsMod.MODID, type);
		return Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new IRecipeType<T>() 
		{
			public String toString() 
			{
				return type;
			}
	    });
	}
}
