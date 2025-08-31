package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian.YellowGuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class YellowGuardianModel<T extends YellowGuardianEntity> extends GeoModel<YellowGuardianEntity> {

    @Override
    public Identifier getModelResource(YellowGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "geo/guardian.geo.json");
    }

    @Override
    public Identifier getTextureResource(YellowGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/yellow_guardian.png");
    }

    @Override
    public Identifier getAnimationResource(YellowGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "animations/guardian.animation.json");
    }

    @Override
    public void setCustomAnimations(YellowGuardianEntity animatable, long instanceId, AnimationState<YellowGuardianEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }



}
