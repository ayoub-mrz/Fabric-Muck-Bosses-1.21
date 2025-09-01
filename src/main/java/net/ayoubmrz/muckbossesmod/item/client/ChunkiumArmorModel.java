package net.ayoubmrz.muckbossesmod.item.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.item.custom.ChunkiumArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ChunkiumArmorModel extends GeoModel<ChunkiumArmorItem> {
    @Override
    public Identifier getModelResource(ChunkiumArmorItem chunkiumArmorItem) {
        return Identifier.of(MuckBossesMod.MOD_ID, "geo/chunkium_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChunkiumArmorItem chunkiumArmorItem) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/armor/chunkium_armor.png");
    }

    @Override
    public Identifier getAnimationResource(ChunkiumArmorItem chunkiumArmorItem) {
        return Identifier.of(MuckBossesMod.MOD_ID, "animations/armor.animation.json");
    }
}
