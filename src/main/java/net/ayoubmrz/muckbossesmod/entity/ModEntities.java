package net.ayoubmrz.muckbossesmod.entity;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.*;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian.*;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<GronkEntity> GRONK = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "gronk"),
            EntityType.Builder.create(GronkEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.8f, 4f).build());

    public static final EntityType<WhiteGuardianEntity> WHITE_GUARDIAN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "white_guardian"),
            EntityType.Builder.create(WhiteGuardianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.8f, 4f).build());
    public static final EntityType<BlueGuardianEntity> BLUE_GUARDIAN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "blue_guardian"),
            EntityType.Builder.create(BlueGuardianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.8f, 4f).build());
    public static final EntityType<GreenGuardianEntity> GREEN_GUARDIAN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "green_guardian"),
            EntityType.Builder.create(GreenGuardianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.8f, 4f).build());
    public static final EntityType<YellowGuardianEntity> YELLOW_GUARDIAN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "yellow_guardian"),
            EntityType.Builder.create(YellowGuardianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.8f, 4f).build());
    public static final EntityType<RedGuardianEntity> RED_GUARDIAN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "red_guardian"),
            EntityType.Builder.create(RedGuardianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.8f, 4f).build());
    public static final EntityType<PurpleGuardianEntity> PURPLE_GUARDIAN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "purple_guardian"),
            EntityType.Builder.create(PurpleGuardianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.8f, 4f).build());

    public static final EntityType<ChiefEntity> CHIEF = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "chief"),
            EntityType.Builder.create(ChiefEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.8f, 4f).build());

    public static final EntityType<BigChunkEntity> BIG_CHUNK = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "big_chunk"),
            EntityType.Builder.create(BigChunkEntity::new, SpawnGroup.MONSTER)
                    .dimensions(4.4f, 12f).build());

    public static final EntityType<BobEntity> BOB = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "bob"),
            EntityType.Builder.create(BobEntity::new, SpawnGroup.MONSTER)
                    .dimensions(4.0f, 6f).build());

    public static final EntityType<ChiefSpearProjectileEntity> CHIEF_SPEAR = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "chief_spear"),
            EntityType.Builder.<ChiefSpearProjectileEntity>create(ChiefSpearProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.6f, 0.6f).build());

    public static final EntityType<GronkBladeProjectileEntity> GRONK_BLADE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "gronk_blade"),
            EntityType.Builder.<GronkBladeProjectileEntity>create(GronkBladeProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.8f, 1.5f).build());

    public static final EntityType<GronkSwordProjectileEntity> GRONK_SWORD = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "gronk_sword"),
            EntityType.Builder.<GronkSwordProjectileEntity>create(GronkSwordProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.4f, 2.4f).build());

    public static final EntityType<ChunkyRockProjectileEntity> CHUNKY_ROCK = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MuckBossesMod.MOD_ID, "chunky_rock"),
            EntityType.Builder.<ChunkyRockProjectileEntity>create(ChunkyRockProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(3f, 2.8f).build());

    public static void registerModEntities() {
        MuckBossesMod.LOGGER.info("Registering Mod Bosses for " + MuckBossesMod.MOD_ID);
    }
}