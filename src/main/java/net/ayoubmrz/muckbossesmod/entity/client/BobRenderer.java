package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.BobEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.ChiefEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BobRenderer extends GeoEntityRenderer<BobEntity> {

    public BobRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BobModel<>());
    }

    @Override
    public Identifier getTextureLocation(BobEntity animatable) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/bob.png");
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack,
                                    BobEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick,
                                    int packedLight, int packedOverlay) {
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick,
                packedLight, packedOverlay);
        this.withScale(1.0f);
    }

}
