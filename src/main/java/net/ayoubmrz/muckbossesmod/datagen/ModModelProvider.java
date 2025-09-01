package net.ayoubmrz.muckbossesmod.datagen;

import net.ayoubmrz.muckbossesmod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(ModItems.GRONK_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.CHIEF_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.WHITE_GUARDIAN_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.BLUE_GUARDIAN_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.GREEN_GUARDIAN_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.YELLOW_GUARDIAN_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.RED_GUARDIAN_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.PURPLE_GUARDIAN_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.BIG_CHUNK_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

       itemModelGenerator.register(ModItems.RED_GEM, Models.GENERATED);
       itemModelGenerator.register(ModItems.YELLOW_GEM, Models.GENERATED);
       itemModelGenerator.register(ModItems.WHITE_GEM, Models.GENERATED);
       itemModelGenerator.register(ModItems.BLUE_GEM, Models.GENERATED);
       itemModelGenerator.register(ModItems.GREEN_GEM, Models.GENERATED);
       itemModelGenerator.register(ModItems.PURPle_GEM, Models.GENERATED);

       itemModelGenerator.register(ModItems.BLACK_SHARD, Models.GENERATED);

        itemModelGenerator.register(ModItems.RAW_CHUNKIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHUNKIUM_INGOT, Models.GENERATED);

        itemModelGenerator.register(ModItems.CHUNKIUM_HELMET, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHUNKIUM_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHUNKIUM_LEGGINGS, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHUNKIUM_BOOTS, Models.GENERATED);

    }
}
