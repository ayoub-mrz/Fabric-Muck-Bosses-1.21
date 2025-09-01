package net.ayoubmrz.muckbossesmod.item;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.item.custom.ChunkiumArmorItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item GRONK_SPAWN_EGG = registerItem("gronk_spawn_egg",
            new SpawnEggItem(ModEntities.GRONK, 0x6a9648, 0x522914, new Item.Settings()));
    public static final Item CHIEF_SPAWN_EGG = registerItem("chief_spawn_egg",
            new SpawnEggItem(ModEntities.CHIEF, 0x786d56, 0xe4d970, new Item.Settings()));
    public static final Item WHITE_GUARDIAN_SPAWN_EGG = registerItem("white_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.WHITE_GUARDIAN, 0x272929, 0xf5f5f5, new Item.Settings()));
    public static final Item BLUE_GUARDIAN_SPAWN_EGG = registerItem("blue_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.BLUE_GUARDIAN, 0x272929, 0x3afff1, new Item.Settings()));
    public static final Item GREEN_GUARDIAN_SPAWN_EGG = registerItem("green_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.GREEN_GUARDIAN, 0x272929, 0x3bff2f, new Item.Settings()));
    public static final Item YELLOW_GUARDIAN_SPAWN_EGG = registerItem("yellow_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.YELLOW_GUARDIAN, 0x272929, 0xe3ff2f, new Item.Settings()));
    public static final Item RED_GUARDIAN_SPAWN_EGG = registerItem("red_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.RED_GUARDIAN, 0x272929, 0xff3e3e, new Item.Settings()));
    public static final Item PURPLE_GUARDIAN_SPAWN_EGG = registerItem("purple_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.PURPLE_GUARDIAN, 0x272929, 0xda30ff, new Item.Settings()));
    public static final Item BIG_CHUNK_SPAWN_EGG = registerItem("big_chunk_spawn_egg",
            new SpawnEggItem(ModEntities.BIG_CHUNK, 0x7a613a, 0xf3d19b, new Item.Settings()));

    public static final Item RED_GEM = registerItem("red_gem", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item YELLOW_GEM = registerItem("yellow_gem", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item BLUE_GEM = registerItem("blue_gem", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item PURPle_GEM = registerItem("purple_gem", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item WHITE_GEM = registerItem("white_gem", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item GREEN_GEM = registerItem("green_gem", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));

    public static final Item ANCIENT_CORE = registerItem("ancient_core", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item BLACK_SHARD = registerItem("black_shard", new Item( new Item.Settings().rarity(Rarity.EPIC)));
    public static final Item SPEAR_TIP = registerItem("spear_tip", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item HAMMER_SHAFT = registerItem("hammer_shaft", new Item( new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item RUBY = registerItem("ruby", new Item( new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));

    public static final Item WYVERN_DAGGER = registerItem("wyvern_dagger",
            new MuckItem((new Item.Settings()).rarity(Rarity.EPIC).maxDamage(250)
                    .attributeModifiers(createAttributeModifiers(15.0F, -1.8F))
                    .component(DataComponentTypes.TOOL, MuckItem.createToolComponent()), 15.0F, -1.8F));
    public static final Item GRONK_SWORD = registerItem("gronk_sword",
            new MuckItem((new Item.Settings()).rarity(Rarity.EPIC).maxDamage(250)
                    .attributeModifiers(createAttributeModifiers(23.0F, -2.4F))
                    .component(DataComponentTypes.TOOL, MuckItem.createToolComponent()), 23.0F, -2.4F));
    public static final Item CHUNKY_HAMMER = registerItem("chunky_hammer",
            new MuckItem((new Item.Settings()).rarity(Rarity.EPIC).maxDamage(250)
                    .attributeModifiers(createAttributeModifiers(25.0F, -3.2F))
                    .component(DataComponentTypes.TOOL, MuckItem.createToolComponent()), 25.0F, -3.2F));
    public static final Item NIGHT_BLADE = registerItem("night_blade",
            new MuckItem((new Item.Settings()).rarity(Rarity.EPIC).maxDamage(250)
                    .attributeModifiers(createAttributeModifiers(27.0F, -2.4F))
                    .component(DataComponentTypes.TOOL, MuckItem.createToolComponent()), 27.0F, -2.4F));
    public static final Item CHIEF_SPEAR = registerItem("chief_spear",
            new MuckItem((new Item.Settings()).rarity(Rarity.EPIC).maxDamage(250)
                    .attributeModifiers(createAttributeModifiers(31.0F, -2.4F))
                    .component(DataComponentTypes.TOOL, MuckItem.createToolComponent()), 31.0F, -2.4F));

    public static final Item RAW_CHUNKIUM = registerItem("raw_chunkium", new Item( new Item.Settings().rarity(Rarity.RARE)));
    public static final Item CHUNKIUM_INGOT = registerItem("chunkium_ingot", new Item( new Item.Settings().rarity(Rarity.RARE)));

    public static final Item CHUNKIUM_HELMET = registerItem("chunkium_helmet",
            new ChunkiumArmorItem(ModArmorMaterials.CHUNKIUM_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings().rarity(Rarity.RARE)
                    .maxDamage(ArmorItem.Type.HELMET.getMaxDamage(30))));
    public static final Item CHUNKIUM_CHESTPLATE = registerItem("chunkium_chestplate",
            new ChunkiumArmorItem(ModArmorMaterials.CHUNKIUM_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings().rarity(Rarity.RARE)
                    .maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(30))));
    public static final Item CHUNKIUM_LEGGINGS = registerItem("chunkium_leggings",
            new ChunkiumArmorItem(ModArmorMaterials.CHUNKIUM_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings().rarity(Rarity.RARE)
                    .maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(30))));
    public static final Item CHUNKIUM_BOOTS = registerItem("chunkium_boots",
            new ChunkiumArmorItem(ModArmorMaterials.CHUNKIUM_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings().rarity(Rarity.RARE)
                    .maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(30))));

    private static AttributeModifiersComponent createAttributeModifiers(float attackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, (double)attackDamage, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, (double)attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .build();
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MuckBossesMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        MuckBossesMod.LOGGER.info("Registering Mod Items for " + MuckBossesMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(RED_GEM);
            entries.add(YELLOW_GEM);
            entries.add(BLUE_GEM);
            entries.add(PURPle_GEM);
            entries.add(WHITE_GEM);
            entries.add(GREEN_GEM);
            entries.add(ANCIENT_CORE);
            entries.add(BLACK_SHARD);
            entries.add(SPEAR_TIP);
            entries.add(HAMMER_SHAFT);
            entries.add(RUBY);
            entries.add(RAW_CHUNKIUM);
            entries.add(CHUNKIUM_INGOT);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(CHIEF_SPEAR);
            entries.add(CHUNKY_HAMMER);
            entries.add(GRONK_SWORD);
            entries.add(NIGHT_BLADE);
            entries.add(WYVERN_DAGGER);
            entries.add(CHUNKIUM_HELMET);
            entries.add(CHUNKIUM_CHESTPLATE);
            entries.add(CHUNKIUM_LEGGINGS);
            entries.add(CHUNKIUM_BOOTS);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(GRONK_SPAWN_EGG);
            entries.add(CHIEF_SPAWN_EGG);
            entries.add(WHITE_GUARDIAN_SPAWN_EGG);
            entries.add(BLUE_GUARDIAN_SPAWN_EGG);
            entries.add(GREEN_GUARDIAN_SPAWN_EGG);
            entries.add(YELLOW_GUARDIAN_SPAWN_EGG);
            entries.add(RED_GUARDIAN_SPAWN_EGG);
            entries.add(PURPLE_GUARDIAN_SPAWN_EGG);
            entries.add(BIG_CHUNK_SPAWN_EGG);
        });

    }
}