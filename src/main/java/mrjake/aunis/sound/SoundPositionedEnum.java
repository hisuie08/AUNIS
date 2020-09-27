package mrjake.aunis.sound;

import java.util.HashMap;
import java.util.Map;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public enum SoundPositionedEnum {
	MILKYWAY_RING_ROLL(0, "gate_milkyway_ring_roll", 	AunisSoundHelper.AUNIS_SOUND_CATEGORY, false, true),
	WORMHOLE_LOOP(1, "wormhole_loop", 					AunisSoundHelper.AUNIS_SOUND_CATEGORY, true),
	UNIVERSE_RING_ROLL(2, "gate_universe_roll", 		AunisSoundHelper.AUNIS_SOUND_CATEGORY, false, true),
	BEAMER_LOOP(3, "beamer_loop", 						AunisSoundHelper.AUNIS_SOUND_CATEGORY, true);
	
	public int id;
	public ResourceLocation resourceLocation;
	public SoundCategory soundCategory;
	public boolean repeat;
	public boolean attenuationMultiplied = false;
	
	SoundPositionedEnum(int id, String name, SoundCategory soundCategory, boolean repeat) {
		this.id = id;
		this.resourceLocation = new ResourceLocation(Aunis.ModID, name);
		this.soundCategory = soundCategory;
		this.repeat = repeat;
	}
	
	SoundPositionedEnum(int id, String name, SoundCategory soundCategory, boolean repeat, boolean attenuationMultiplied) {
		this(id, name, soundCategory, repeat);
		this.attenuationMultiplied = attenuationMultiplied;
	}
	
	public float getVolumeMultiplied() {		
		if (attenuationMultiplied)
			return 1.5f * (float)AunisConfig.avConfig.audioRangeMultiplier;
		
		return 1.5f;
	}
	
	private static Map<Integer, SoundPositionedEnum> ID_MAP = new HashMap<Integer, SoundPositionedEnum>(values().length);
	
	static {
		for (SoundPositionedEnum positionedSound : values()) {
			ID_MAP.put(positionedSound.id, positionedSound);
		}
	}
	
	public static SoundPositionedEnum valueOf(int id) {
		return ID_MAP.get(id);
	}
}
