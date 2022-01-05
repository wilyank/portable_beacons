package wilyan_kramer.portable_beacons.common.effect;

import net.minecraft.util.IStringSerializable;

public enum Profession implements IStringSerializable {
	POTIONEER("potioneer"),
	ARTIFICER("artificer"),
	SUMMONER("summoner");

	private final String name;

	private Profession(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	public String getSerializedName() {
		return this.name;
	}
}
