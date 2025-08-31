package net.ayoubmrz.muckbossesmod.entity.client.projectile;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.ChiefSpearProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ChunkyRockProjectileModel extends EntityModel<ChiefSpearProjectileEntity> {
    public static final EntityModelLayer CHUNKY_ROCK = new EntityModelLayer(Identifier.of(MuckBossesMod.MOD_ID, "chunky_rock"), "main");
    private final ModelPart chunky_rock;

    public ChunkyRockProjectileModel(ModelPart root) {
        this.chunky_rock = root.getChild("chunky_rock");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData chunky_rock = modelPartData.addChild("chunky_rock", ModelPartBuilder.create().uv(0, 0).cuboid(-28.0F, -18.0F, -28.0F, 40.0F, 40.0F, 40.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, 4.0F, -4.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void setAngles(ChiefSpearProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        chunky_rock.render(matrices, vertexConsumer, light, overlay, color);
    }
}
