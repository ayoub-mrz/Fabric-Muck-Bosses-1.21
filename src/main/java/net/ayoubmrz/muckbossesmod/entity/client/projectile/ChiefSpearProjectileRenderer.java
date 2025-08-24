package net.ayoubmrz.muckbossesmod.entity.client.projectile;

import net.ayoubmrz.muckbossesmod.MuckBossesMod;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.ChiefSpearProjectileEntity;
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

public class ChiefSpearProjectileRenderer extends EntityRenderer<ChiefSpearProjectileEntity> {
    protected ChiefSpearProjectileModel model;

    public ChiefSpearProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new ChiefSpearProjectileModel(ctx.getPart(ChiefSpearProjectileModel.CHIEF_SPEAR));
    }

    @Override
    public void render(ChiefSpearProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        if(!entity.isGrounded()) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw())));
        }

        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                this.model.getLayer(Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/chief_spear.png")), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(ChiefSpearProjectileEntity entity) {
        return Identifier.of(MuckBossesMod.MOD_ID, "textures/entity/chief_spear.png");
    }
}
