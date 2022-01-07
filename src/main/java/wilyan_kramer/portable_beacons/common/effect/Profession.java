package wilyan_kramer.portable_beacons.common.effect;

import net.minecraft.inventory.EquipmentSlotType;
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

	public static int asInt(String profession) {
		if (profession.equals("potioneer")) {
			return 0;
		}
		if (profession.equals("artificer")) {
			return 1;
		}
		if (profession.equals("summoner")) {
			return 2;
		}
		return -1;
	}
}
