package net.ayoubmrz.muckbossesmod;

import net.ayoubmrz.muckbossesmod.command.ChunkyRockCommand;
import net.ayoubmrz.muckbossesmod.command.GronkBladeCommand;
import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.*;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian.*;
import net.ayoubmrz.muckbossesmod.item.ModItemGroups;
import net.ayoubmrz.muckbossesmod.item.ModItems;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuckBossesMod implements ModInitializer {
	public static final String MOD_ID = "muckbossesmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();

		ModEntities.registerModEntities();

		ModItemGroups.registerItemGroups();

		GronkBladeCommand.register();
		ChunkyRockCommand.register();

		ModSounds.registerSounds();

		FabricDefaultAttributeRegistry.register(ModEntities.GRONK, GronkEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.WHITE_GUARDIAN, WhiteGuardianEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BLUE_GUARDIAN, BlueGuardianEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.GREEN_GUARDIAN, GreenGuardianEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.YELLOW_GUARDIAN, YellowGuardianEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.RED_GUARDIAN, RedGuardianEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PURPLE_GUARDIAN, PurpleGuardianEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BIG_CHUNK, BigChunkEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CHIEF, ChiefEntity.setAttributes());

	}
}