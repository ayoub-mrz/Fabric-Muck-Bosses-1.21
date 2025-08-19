package net.ayoubmrz.muckbossesmod.entity.client.projectile;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.ChiefSpearProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ChiefSpearProjectileModel extends EntityModel<ChiefSpearProjectileEntity> {
    public static final EntityModelLayer CHIEF_SPEAR = new EntityModelLayer(Identifier.of(MuckBossesMod.MOD_ID, "chief_spear"), "main");
    private final ModelPart chief_spear;

    public ChiefSpearProjectileModel(ModelPart root) {
        this.chief_spear = root.getChild("chief_spear");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData chief_spear = modelPartData.addChild("chief_spear", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        ModelPartData spear_tip = chief_spear.addChild("spear_tip", ModelPartBuilder.create().uv(20, 0).cuboid(-1.5F, -10.0F, 0.5F, 3.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-2.5F, -21.0F, 0.5F, 5.0F, 11.0F, 1.0F, new Dilation(0.003F))
                .uv(12, 9).cuboid(-0.5F, -22.0F, 0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = spear_tip.addChild("cube_r1", ModelPartBuilder.create().uv(16, 9).cuboid(-3.0568F, -1.6016F, -0.5005F, 3.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 19).cuboid(-4.2663F, -6.7686F, -0.4995F, 2.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -15.8887F, 0.9995F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r2 = spear_tip.addChild("cube_r2", ModelPartBuilder.create().uv(12, 0).cuboid(0.0568F, -1.6016F, -0.5005F, 3.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 23).cuboid(2.2663F, -6.7686F, -0.4995F, 2.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -15.8887F, 0.9995F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r3 = spear_tip.addChild("cube_r3", ModelPartBuilder.create().uv(0, 27).cuboid(4.6238F, -6.5324F, -0.4995F, 1.95F, 1.95F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -15.8887F, 0.9995F, 0.0F, 0.0F, -0.7854F));

        ModelPartData spear_rod = chief_spear.addChild("spear_rod", ModelPartBuilder.create().uv(24, 5).cuboid(-1.0F, 20.0F, 0.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(17, 18).cuboid(-0.75F, 13.0F, 0.25F, 1.5F, 7.0F, 1.5F, new Dilation(0.0F))
                .uv(1, 17).cuboid(-0.75F, 5.0F, 0.25F, 1.5F, 8.0F, 1.5F, new Dilation(0.0F))
                .uv(9, 17).cuboid(-0.75F, -3.0F, 0.25F, 1.5F, 8.0F, 1.5F, new Dilation(0.0F))
                .uv(24, 10).cuboid(-1.0F, -5.0F, 0.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r4 = spear_rod.addChild("cube_r4", ModelPartBuilder.create().uv(0, 12).cuboid(-2.0F, -0.5F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.5F, 1.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r5 = spear_rod.addChild("cube_r5", ModelPartBuilder.create().uv(24, 15).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 23.0F, 1.0F, 0.0F, 0.0F, 0.7854F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ChiefSpearProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        chief_spear.render(matrices, vertexConsumer, light, overlay, color);
    }
}
