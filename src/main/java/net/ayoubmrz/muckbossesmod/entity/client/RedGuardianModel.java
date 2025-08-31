package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian.RedGuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RedGuardianModel<T extends RedGuardianEntity> extends GeoModel<RedGuardianEntity> {

    @Override
    public Identifier getModelResource(RedGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "geo/guardian.geo.json");
    }

    @Override
    public Identifier getTextureResource(RedGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/red_guardian.png");
    }

    @Override
    public Identifier getAnimationResource(RedGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "animations/guardian.animation.json");
    }

    @Override
    public void setCustomAnimations(RedGuardianEntity animatable, long instanceId, AnimationState<RedGuardianEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }



}
