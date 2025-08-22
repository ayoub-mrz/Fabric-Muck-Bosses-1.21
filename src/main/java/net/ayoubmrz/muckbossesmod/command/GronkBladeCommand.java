package net.ayoubmrz.muckbossesmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkBladeProjectileEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.entity.LivingEntity;

public class GronkBladeCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher, registryAccess);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(CommandManager.literal("ThrowGronkBlade")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(GronkBladeCommand::executeGronkBlade));
    }

    private static int executeGronkBlade(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            if (source.getEntity() instanceof LivingEntity livingEntity) {
                GronkBladeProjectileEntity.spawnBladeSpread(livingEntity.getWorld(), livingEntity);

                source.sendFeedback(() -> Text.literal("Gronk Blade spread spawned!"), false);
                return 1;
            } else {
                source.sendError(Text.literal("This command must be executed by a living entity!"));
                return 0;
            }
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to spawn Gronk Blade spread: " + e.getMessage()));
            return 0;
        }
    }
}

