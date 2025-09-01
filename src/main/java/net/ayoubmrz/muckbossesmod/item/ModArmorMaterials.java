package net.ayoubmrz.muckbossesmod.item;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {

    public static final RegistryEntry<ArmorMaterial> CHUNKIUM_ARMOR_MATERIAL = registerArmorMaterial("chunkium",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 10);
                map.put(ArmorItem.Type.LEGGINGS, 15);
                map.put(ArmorItem.Type.CHESTPLATE, 18);
                map.put(ArmorItem.Type.HELMET, 10);
                map.put(ArmorItem.Type.BODY, 20);
            }), 20, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> Ingredient.ofItems(ModItems.CHUNKIUM_INGOT),
                    List.of(new ArmorMaterial.Layer(Identifier.of(MuckBossesMod.MOD_ID, "chunkium"))), 0,0));

    public static RegistryEntry<ArmorMaterial> registerArmorMaterial(String name, Supplier<ArmorMaterial> material) {
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(MuckBossesMod.MOD_ID, name), material.get());
    }
}