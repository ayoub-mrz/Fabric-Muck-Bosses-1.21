package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.ChiefEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ChiefModel<T extends ChiefEntity> extends GeoModel<ChiefEntity> {

    @Override
    public Identifier getModelResource(ChiefEntity chiefEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "geo/" + chiefEntity.isChiefStats() + ".geo.json");
    }

    @Override
    public Identifier getTextureResource(ChiefEntity chiefEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/chief.png");
    }

    @Override
    public Identifier getAnimationResource(ChiefEntity chiefEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "animations/chief.animation.json");
    }

    @Override
    public void setCustomAnimations(ChiefEntity animatable, long instanceId, AnimationState<ChiefEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }



}
