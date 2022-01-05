package wilyan_kramer.portable_beacons.common.item.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BenchUpgradeRecipe implements IRecipe<IInventory> {
	
	public final static String GROUP = "bench_upgrade";
	public final static IRecipeType<BenchUpgradeRecipe> TYPE = ModRecipeSerializers.registerRecipeType(GROUP);
	
	protected final ResourceLocation id;
	protected final Ingredient input;
	protected final String profession;
	protected final int xp;
	protected final IRecipeType<?> type;
	protected final String group;

	
	public BenchUpgradeRecipe(ResourceLocation id, String group, Ingredient input, String profession, int xp) {
		this.id = id;
		this.group = group;
		this.input = input;
		this.xp = xp;
		this.profession = profession;
		this.type = TYPE;
	}
	
	@Override
	public boolean matches(IInventory inv, World world) {
		return input.test(inv.getItem(0));
	}
	public String getProfession() {
		return this.profession;
	}

	public int getExperience() {
		return this.xp;
	}
	
	@Override
	public ItemStack assemble(IInventory inv) {
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int x, int y) {
		return true;
	}

	@Override
	public ItemStack getResultItem() {
		return null;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		// TODO Auto-generated method stub
		return ModRecipeSerializers.BENCH_UPGRADE.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return this.type;
	}

	@Override
	public String getGroup() {
		// TODO Auto-generated method stub
		return this.group;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(Items.TOTEM_OF_UNDYING);
	}


}
