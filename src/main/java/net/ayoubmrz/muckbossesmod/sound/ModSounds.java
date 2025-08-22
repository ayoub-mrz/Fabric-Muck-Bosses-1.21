package net.ayoubmrz.muckbossesmod.sound;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent GRONK_CHARGE = registerSoundEvent("gronk_charge");
    public static final SoundEvent BLADE_MOVE = registerSoundEvent("blade_move");
    public static final SoundEvent SWORD_SPINNING = registerSoundEvent("sword_spinning");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(MuckBossesMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        MuckBossesMod.LOGGER.info("Registering Mod Sounds for " + MuckBossesMod.MOD_ID);
    }
}