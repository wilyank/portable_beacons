package wilyan_kramer.portable_beacons.common.block;

import net.minecraft.util.IStringSerializable;

public enum BenchPart implements IStringSerializable {
	LEFT("left"),
	RIGHT("right");

	private final String name;

	private BenchPart(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	public String getSerializedName() {
		return this.name;
	}
}
