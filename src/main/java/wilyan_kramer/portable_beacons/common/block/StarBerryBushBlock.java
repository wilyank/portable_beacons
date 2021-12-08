package wilyan_kramer.portable_beacons.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import wilyan_kramer.portable_beacons.common.effect.ModDamageSource;
import wilyan_kramer.portable_beacons.common.item.ItemList;

public class StarBerryBushBlock extends SweetBerryBushBlock {

	public StarBerryBushBlock(Properties prop) {
		super(prop);
	}
	public ItemStack getCloneItemStack(IBlockReader blockReader, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(ItemList.starberries);
	}

	@Override
	public void entityInside(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).addEffect(new EffectInstance(Effects.GLOWING, 200));
			entity.makeStuckInBlock(blockState, new Vector3d((double)0.8F, 0.75D, (double)0.8F));

			if (!world.isClientSide && blockState.getValue(AGE) > 0 && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
				double d0 = Math.abs(entity.getX() - entity.xOld);
				double d1 = Math.abs(entity.getZ() - entity.zOld);
				if (d0 >= (double)0.003F || d1 >= (double)0.003F) {
					entity.hurt(ModDamageSource.starberry_bush, 1.0F);
				}
			}
        }
		//super.entityInside(blockState, world, blockPos, entity);
	}

	@Override
	protected boolean mayPlaceOn(BlockState blockstate, IBlockReader blockReader, BlockPos blockPos) {
		return blockstate.is(BlockList.nether_star_block);
	}

	public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult posOnFace) {
		int i = blockState.getValue(AGE);
		boolean flag = i == 3;
		if (!flag && player.getItemInHand(hand).getItem() == Items.BONE_MEAL) {
			return ActionResultType.PASS;
		} else if (i > 1) {
			int j = 1 + world.random.nextInt(2);
			popResource(world, blockPos, new ItemStack(ItemList.starberries, j + (flag ? 1 : 0)));
			world.playSound((PlayerEntity)null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
			world.setBlock(blockPos, blockState.setValue(AGE, Integer.valueOf(1)), 2);
			return ActionResultType.sidedSuccess(world.isClientSide);
		} else {
			return super.use(blockState, world, blockPos, player, hand, posOnFace);
		}
	}
}
