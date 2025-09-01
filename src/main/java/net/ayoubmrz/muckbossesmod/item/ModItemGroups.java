package net.ayoubmrz.muckbossesmod.item;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup MUCK_BOSSES_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(MuckBossesMod.MOD_ID, "muck_bosses"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.SPEAR_TIP))
                    .displayName(Text.translatable("itemgroup.muckbossesmod.muck_bosses"))
                    .entries((displayContext, entries) -> {

                        entries.add(ModItems.RED_GEM);
                        entries.add(ModItems.YELLOW_GEM);
                        entries.add(ModItems.BLUE_GEM);
                        entries.add(ModItems.PURPle_GEM);
                        entries.add(ModItems.WHITE_GEM);
                        entries.add(ModItems.GREEN_GEM);
                        entries.add(ModItems.ANCIENT_CORE);
                        entries.add(ModItems.BLACK_SHARD);
                        entries.add(ModItems.SPEAR_TIP);
                        entries.add(ModItems.RUBY);
                        entries.add(ModItems.HAMMER_SHAFT);

                        entries.add(ModItems.NIGHT_BLADE);
                        entries.add(ModItems.WYVERN_DAGGER);
                        entries.add(ModItems.CHIEF_SPEAR);
                        entries.add(ModItems.CHUNKY_HAMMER);
                        entries.add(ModItems.GRONK_SWORD);

                        entries.add(ModItems.GRONK_SPAWN_EGG);
                        entries.add(ModItems.CHIEF_SPAWN_EGG);
                        entries.add(ModItems.WHITE_GUARDIAN_SPAWN_EGG);
                        entries.add(ModItems.BLUE_GUARDIAN_SPAWN_EGG);
                        entries.add(ModItems.GREEN_GUARDIAN_SPAWN_EGG);
                        entries.add(ModItems.YELLOW_GUARDIAN_SPAWN_EGG);
                        entries.add(ModItems.RED_GUARDIAN_SPAWN_EGG);
                        entries.add(ModItems.PURPLE_GUARDIAN_SPAWN_EGG);
                        entries.add(ModItems.BIG_CHUNK_SPAWN_EGG);

                        entries.add(ModItems.RAW_CHUNKIUM);
                        entries.add(ModItems.CHUNKIUM_INGOT);

                        entries.add(ModItems.CHUNKIUM_HELMET);
                        entries.add(ModItems.CHUNKIUM_CHESTPLATE);
                        entries.add(ModItems.CHUNKIUM_LEGGINGS);
                        entries.add(ModItems.CHUNKIUM_BOOTS);

                    }).build());

    public static void registerItemGroups() {
        MuckBossesMod.LOGGER.info("Registering Item Groups for " + MuckBossesMod.MOD_ID);
    }
}

