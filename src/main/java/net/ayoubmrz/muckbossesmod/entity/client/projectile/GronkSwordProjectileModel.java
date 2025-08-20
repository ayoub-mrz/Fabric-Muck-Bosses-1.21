package net.ayoubmrz.muckbossesmod.entity.client.projectile;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.ChiefSpearProjectileEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkSwordProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GronkSwordProjectileModel extends EntityModel<GronkSwordProjectileEntity> {
    public static final EntityModelLayer GRONK_SWORD = new EntityModelLayer(Identifier.of(MuckBossesMod.MOD_ID, "gronk_sword"), "main");
    private final ModelPart gronk_sword;

    public GronkSwordProjectileModel(ModelPart root) {
        this.gronk_sword = root.getChild("gronk_sword");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData gronk_sword = modelPartData.addChild("gronk_sword", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        ModelPartData blade = gronk_sword.addChild("blade", ModelPartBuilder.create().uv(4, 12).cuboid(-0.25F, -22.0F, 0.0F, 0.25F, 22.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 12).cuboid(-0.25F, -21.0F, -1.0F, 0.25F, 9.0F, 1.0F, new Dilation(0.0F))
                .uv(20, 12).cuboid(-0.25F, -6.0F, -1.0F, 0.25F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(20, 19).cuboid(-0.25F, -11.0F, -1.0F, 0.25F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(2, 12).cuboid(-0.25F, -23.0F, 1.0F, 0.25F, 23.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 12).cuboid(-0.25F, -24.0F, 2.0F, 0.25F, 24.0F, 1.0F, new Dilation(0.0F))
                .uv(6, 12).cuboid(-0.25F, -20.0F, -2.0F, 0.25F, 20.0F, 1.0F, new Dilation(0.0F))
                .uv(31, 44).cuboid(-0.25F, -19.0F, -3.0F, 0.25F, 19.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData hilt = gronk_sword.addChild("hilt", ModelPartBuilder.create().uv(12, 51).cuboid(-1.0F, 2.0F, -1.0F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.25F, 0.0F, -5.0F, 2.5F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(GronkSwordProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        gronk_sword.render(matrices, vertexConsumer, light, overlay, color);
    }
}
