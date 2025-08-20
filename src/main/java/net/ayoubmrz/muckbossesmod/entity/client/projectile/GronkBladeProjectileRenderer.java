package net.ayoubmrz.muckbossesmod.entity.client.projectile;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkBladeProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class GronkBladeProjectileRenderer extends EntityRenderer<GronkBladeProjectileEntity> {
    protected GronkBladeProjectileModel model;

    public GronkBladeProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new GronkBladeProjectileModel(ctx.getPart(GronkBladeProjectileModel.GRONK_BLADE));
    }

    @Override
    public void render(GronkBladeProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.scale(1.4f, 1.4f, 1.4f);

        if(!entity.isGrounded()) {
            float entityYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
            float entityPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());

             matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entityYaw));
             matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entityPitch));

        }

        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                this.model.getLayer(Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/blue_blade.png")), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(GronkBladeProjectileEntity entity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/blue_blade.png");
    }
}
