package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.BigChunkEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.BobEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BigChunkRenderer extends GeoEntityRenderer<BigChunkEntity> {

    public BigChunkRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BigChunkModel<>());
    }

    @Override
    public Identifier getTextureLocation(BigChunkEntity animatable) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/big_chunk.png");
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack,
                                    BigChunkEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick,
                                    int packedLight, int packedOverlay) {
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick,
                packedLight, packedOverlay);
        this.withScale(6.0f);
    }

}
