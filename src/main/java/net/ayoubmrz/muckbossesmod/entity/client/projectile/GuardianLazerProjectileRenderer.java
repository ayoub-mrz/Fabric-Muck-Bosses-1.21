package net.ayoubmrz.muckbossesmod.entity.client.projectile;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GuardianLazerProjectileEntity;
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

public class GuardianLazerProjectileRenderer extends EntityRenderer<GuardianLazerProjectileEntity> {
    protected GuardianLazerProjectileModel model;

    public GuardianLazerProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new GuardianLazerProjectileModel(ctx.getPart(GuardianLazerProjectileModel.GUARDIANLAZER));
    }

    @Override
    public void render(GuardianLazerProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        if(entity.groundedOffset != null) {
            if(!entity.isGrounded()) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw())));
                matrices.translate(0, -1.0f, 0);
            } else {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.groundedOffset.getY()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.groundedOffset.getX()));
                matrices.translate(0, -0.5f, 0);
            }
        }

        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                this.model.getLayer(Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/guardian_lazer.png")), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(GuardianLazerProjectileEntity entity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/guardian_lazer.png");
    }
}
