package net.ayoubmrz.muckbossesmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkBladeProjectileEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.entity.LivingEntity;

public class ChunkyRockCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher, registryAccess);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(CommandManager.literal("ThrowChunkyRock")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ChunkyRockCommand::executeChunkyRock));
    }

    private static int executeChunkyRock(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            if (source.getEntity() instanceof LivingEntity livingEntity) {

                UsefulMethods.spawnProjectileSpread(livingEntity.getWorld(), livingEntity, "rock");

                source.sendFeedback(() -> Text.literal("Chunky Rocks spread spawned!"), false);
                return 1;
            } else {
                source.sendError(Text.literal("This command must be executed by a living entity!"));
                return 0;
            }
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to spawn Chunky Rock spread: " + e.getMessage()));
            return 0;
        }
    }
}

