package net.ayoubmrz.muckbossesmod;

import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.entity.client.*;
import net.ayoubmrz.muckbossesmod.entity.client.projectile.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MuckBossesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModEntities.GRONK, GronkRenderer::new);
        EntityRendererRegistry.register(ModEntities.WHITE_GUARDIAN, WhiteGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLUE_GUARDIAN, BlueGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.GREEN_GUARDIAN, GreenGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.YELLOW_GUARDIAN, YellowGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.RED_GUARDIAN, RedGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.PURPLE_GUARDIAN, PurpleGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.BIG_CHUNK, BigChunkRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHIEF, ChiefRenderer::new);
        EntityRendererRegistry.register(ModEntities.BOB, BobRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ChiefSpearProjectileModel.CHIEF_SPEAR, ChiefSpearProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.CHIEF_SPEAR, ChiefSpearProjectileRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(GronkBladeProjectileModel.GRONK_BLADE, GronkBladeProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.GRONK_BLADE, GronkBladeProjectileRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(GuardianLazerProjectileModel.GUARDIANLAZER, GuardianLazerProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.GUARDIAN_LAZER, GuardianLazerProjectileRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(GronkSwordProjectileModel.GRONK_SWORD, GronkSwordProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.GRONK_SWORD, GronkSwordProjectileRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ChunkyRockProjectileModel.CHUNKY_ROCK, ChunkyRockProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.CHUNKY_ROCK, ChunkyRockProjectileRenderer::new);
    }
}
