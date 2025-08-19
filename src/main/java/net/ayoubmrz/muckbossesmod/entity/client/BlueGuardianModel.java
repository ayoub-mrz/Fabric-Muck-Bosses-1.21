package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian.BlueGuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BlueGuardianModel<T extends BlueGuardianEntity> extends GeoModel<BlueGuardianEntity> {

    @Override
    public Identifier getModelResource(BlueGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "geo/guardian.geo.json");
    }

    @Override
    public Identifier getTextureResource(BlueGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/guardian2.png");
    }

    @Override
    public Identifier getAnimationResource(BlueGuardianEntity guardianEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "animations/gronk.animation.json");
    }

    @Override
    public void setCustomAnimations(BlueGuardianEntity animatable, long instanceId, AnimationState<BlueGuardianEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }



}
