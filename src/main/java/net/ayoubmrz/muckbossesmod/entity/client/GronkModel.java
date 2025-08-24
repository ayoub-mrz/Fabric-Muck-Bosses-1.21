package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.GronkEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GronkModel<T extends GronkEntity> extends GeoModel<GronkEntity> {

    @Override
    public Identifier getModelResource(GronkEntity gronkEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "geo/" + gronkEntity.getGronkStats() + ".geo.json");
    }

    @Override
    public Identifier getTextureResource(GronkEntity gronkEntity) {

        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/gronk.png");
    }

    @Override
    public Identifier getAnimationResource(GronkEntity gronkEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "animations/gronk.animation.json");
    }

    @Override
    public void setCustomAnimations(GronkEntity animatable, long instanceId, AnimationState<GronkEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            // head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}