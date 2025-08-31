package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian.RedGuardianEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RedGuardianRenderer extends GeoEntityRenderer<RedGuardianEntity> {

    public RedGuardianRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new RedGuardianModel<>());
    }

    @Override
    public Identifier getTextureLocation(RedGuardianEntity animatable) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/red_guardian.png");
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack,
                                    RedGuardianEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick,
                                    int packedLight, int packedOverlay) {
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick,
                packedLight, packedOverlay);
        this.withScale(1.4f);
    }

}
