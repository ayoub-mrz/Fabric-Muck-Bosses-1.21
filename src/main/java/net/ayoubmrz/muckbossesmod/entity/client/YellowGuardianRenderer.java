package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian.YellowGuardianEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class YellowGuardianRenderer extends GeoEntityRenderer<YellowGuardianEntity> {

    public YellowGuardianRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new YellowGuardianModel<>());
    }

    @Override
    public Identifier getTextureLocation(YellowGuardianEntity animatable) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/yellow_guardian.png");
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack,
                                    YellowGuardianEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick,
                                    int packedLight, int packedOverlay) {
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick,
                packedLight, packedOverlay);
        this.withScale(1.4f);
    }

}
