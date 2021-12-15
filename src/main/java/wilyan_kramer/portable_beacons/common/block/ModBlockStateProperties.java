package wilyan_kramer.portable_beacons.common.block;

import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;

public class ModBlockStateProperties {
	   public static final IntegerProperty POTIONEER_LEVEL = IntegerProperty.create("potioneer_level", 0, 5);
	   public static final IntegerProperty ARTIFICER_LEVEL = IntegerProperty.create("artificer_level", 0, 5);
	   public static final IntegerProperty SUMMONER_LEVEL = IntegerProperty.create("summoner_level", 0, 5);
	   public static final EnumProperty<BenchPart> BENCH_PART = EnumProperty.create("bench_part", BenchPart.class);
}
