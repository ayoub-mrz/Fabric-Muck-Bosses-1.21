package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.BigChunkEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BigChunkModel<T extends BigChunkEntity> extends GeoModel<BigChunkEntity> {

    @Override
    public Identifier getModelResource(BigChunkEntity big_chunkEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "geo/big_chunk.geo.json");
    }

    @Override
    public Identifier getTextureResource(BigChunkEntity big_chunkEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/big_chunk.png");
    }

    @Override
    public Identifier getAnimationResource(BigChunkEntity big_chunkEntity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "animations/gronk.animation.json");
    }

    @Override
    public void setCustomAnimations(BigChunkEntity animatable, long instanceId, AnimationState<BigChunkEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }



}
