package net.ayoubmrz.muckbossesmod.entity.client;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.GronkBladeProjectileEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.GuardianLazerProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GuardianLazerProjectileModel extends EntityModel<GuardianLazerProjectileEntity> {
    public static final EntityModelLayer GUARDIANLAZER = new EntityModelLayer(Identifier.of(MuckBossesMod.MOD_ID, "guardian_lazer"), "main");
    private final ModelPart guardian_lazer;

    public GuardianLazerProjectileModel(ModelPart root) {
        this.guardian_lazer = root.getChild("guardian_lazer");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData guardian_lazer = modelPartData.addChild("guardian_lazer", ModelPartBuilder.create().uv(0, 0).cuboid(-5.5F, -3.5909F, -0.5909F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 2).cuboid(-5.5F, -1.5909F, -2.5909F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 4).cuboid(-5.5F, -0.5909F, -0.5909F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 6).cuboid(-5.5F, -0.5909F, 1.4091F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 8).cuboid(-5.5F, 1.4091F, 1.4091F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 10).cuboid(-5.5F, 1.4091F, -0.5909F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 12).cuboid(-5.5F, 0.4091F, -2.5909F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 14).cuboid(-5.5F, 2.4091F, -1.5909F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 16).cuboid(-5.5F, -2.5909F, -1.5909F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 18).cuboid(-5.5F, 0.4091F, 0.4091F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 20).cuboid(-5.5F, -2.5909F, 1.4091F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 6.0F, 0.5909F, 0.0F, 0.0F, -1.5708F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(GuardianLazerProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        guardian_lazer.render(matrices, vertexConsumer, light, overlay, color);
    }
}
