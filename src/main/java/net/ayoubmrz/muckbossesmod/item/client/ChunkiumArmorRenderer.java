package net.ayoubmrz.muckbossesmod.item.client;

import net.ayoubmrz.muckbossesmod.item.custom.ChunkiumArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ChunkiumArmorRenderer extends GeoArmorRenderer<ChunkiumArmorItem> {

    public ChunkiumArmorRenderer() {
        super(new ChunkiumArmorModel());
    }
}
