package wilyan_kramer.portable_beacons.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class NetherStarBlock extends Block {

	public NetherStarBlock(Properties prop) {
		super(prop);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos,
			PlayerEntity player, Hand hand, BlockRayTraceResult face) {
		if (player.getItemInHand(hand).getItem() == Items.SWEET_BERRIES) {
			if (level.getBlockState(pos.above()) == Blocks.AIR.defaultBlockState()) {
				level.setBlock(pos.above(), BlockList.starberry_bush.defaultBlockState(), 0);
			}
		}
		return ActionResultType.PASS;
	}

}
