package net.ayoubmrz.muckbossesmod.sound;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent GRONK_CHARGE = registerSoundEvent("gronk_charge");
    public static final SoundEvent BLADE_MOVE = registerSoundEvent("blade_move");
    public static final SoundEvent SWORD_SPINNING = registerSoundEvent("sword_spinning");
    public static final SoundEvent SPEAR_SWING = registerSoundEvent("spear_swing");
    public static final SoundEvent HUGE_STEP = registerSoundEvent("huge_step");
    public static final SoundEvent GUARDIAN_AMBIENT = registerSoundEvent("guardian_ambient");
    public static final SoundEvent GUARDIAN_AMBIENT_1 = registerSoundEvent("guardian_ambient_1");
    public static final SoundEvent GUARDIAN_AMBIENT_2 = registerSoundEvent("guardian_ambient_2");
    public static final SoundEvent GUARDIAN_AMBIENT_3 = registerSoundEvent("guardian_ambient_3");
    public static final SoundEvent GUARDIAN_AMBIENT_4 = registerSoundEvent("guardian_ambient_4");
    public static final SoundEvent LASER_CHARGE_UP = registerSoundEvent("laser_charge_up");
    public static final SoundEvent BIG_CHUNK_LOOP_AMBIENT = registerSoundEvent("big_chunk_loop_ambient");
    public static final SoundEvent ROCK_ROLLING_LOOP = registerSoundEvent("rock_rolling_loop");
    public static final SoundEvent PARTICLE_EXPLOSION = registerSoundEvent("particle_explosion");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(MuckBossesMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        MuckBossesMod.LOGGER.info("Registering Mod Sounds for " + MuckBossesMod.MOD_ID);
    }
}