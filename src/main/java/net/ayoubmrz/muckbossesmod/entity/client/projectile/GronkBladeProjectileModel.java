package net.ayoubmrz.muckbossesmod.entity.client.projectile;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkBladeProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GronkBladeProjectileModel extends EntityModel<GronkBladeProjectileEntity> {
    public static final EntityModelLayer GRONK_BLADE = new EntityModelLayer(Identifier.of(MuckBossesMod.MOD_ID, "gronk_blade"), "main");
    private final ModelPart gronk_blade;

    public GronkBladeProjectileModel(ModelPart root) {
        this.gronk_blade = root.getChild("gronk_blade");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData gronk_sword = modelPartData.addChild("gronk_blade", ModelPartBuilder.create(), ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData blade = gronk_sword.addChild("blade", ModelPartBuilder.create().uv(0, 2).cuboid(-9.75F, 0.25F, -0.25F, 22.0F, 1.0F, 0.25F, new Dilation(0.0F))
                .uv(0, 5).cuboid(2.25F, 1.25F, -0.25F, 9.0F, 1.0F, 0.25F, new Dilation(0.0F))
                .uv(0, 6).cuboid(-9.75F, 1.25F, -0.25F, 6.0F, 1.0F, 0.25F, new Dilation(0.0F))
                .uv(0, 7).cuboid(-2.75F, 1.25F, -0.25F, 4.0F, 1.0F, 0.25F, new Dilation(0.0F))
                .uv(0, 1).cuboid(-9.75F, -0.75F, -0.25F, 23.0F, 1.0F, 0.25F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-9.75F, -1.75F, -0.25F, 24.0F, 1.0F, 0.25F, new Dilation(0.0F))
                .uv(0, 3).cuboid(-9.75F, 2.25F, -0.25F, 20.0F, 1.0F, 0.25F, new Dilation(0.0F))
                .uv(0, 4).cuboid(-9.75F, 3.25F, -0.25F, 19.0F, 1.0F, 0.25F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));
        return TexturedModelData.of(modelData, 48, 48);
    }

    @Override
    public void setAngles(GronkBladeProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        gronk_blade.render(matrices, vertexConsumer, light, overlay, color);
    }
}
