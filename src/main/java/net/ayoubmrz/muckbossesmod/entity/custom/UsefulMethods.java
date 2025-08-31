package net.ayoubmrz.muckbossesmod.entity.custom;

import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.ChunkyRockProjectileEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkBladeProjectileEntity;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsefulMethods {

    public static void applyKnockback(Entity entity, Entity source, float upKnockback, float knockbackStrenght) {

        // Calculate knockback direction
        Vec3d knockbackDirection = source.getPos().subtract(entity.getPos()).normalize();

        double knockbackX = -knockbackDirection.x * knockbackStrenght;
        double knockbackZ = -knockbackDirection.z * knockbackStrenght;
        double knockbackY = upKnockback;

        Vec3d currentVelocity = entity.getVelocity();
        entity.setVelocity(
                currentVelocity.x + knockbackX,
                currentVelocity.y + knockbackY,
                currentVelocity.z + knockbackZ
        );

        // Sync velocity to client if it's a player
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer));
        }

        if (!source.getWorld().isClient) {
            ((ServerWorld) source.getWorld()).spawnParticles(
                    ParticleTypes.CRIT,
                    entity.getX(), entity.getY() + entity.getHeight() / 2, entity.getZ(),
                    5, 0.1, 0.1, 0.1, 0.1
            );
        }
    }

    public static void spawnDamagingParticles(
            int particleCount, LivingEntity source, double impactDis, float ParticlesDamage, double radius, boolean swing) {
        if (source.getWorld().isClient) {
            return;
        }

        World world = source.getWorld();
        Vec3d mobPos = source.getPos();
        Vec3d lookDirection = source.getRotationVector().normalize();

        double impactDistance = impactDis;
        Vec3d impactPoint = mobPos.add(lookDirection.multiply(impactDistance));

        BlockPos groundPos = findGroundLevel(world, impactPoint, source);
        Vec3d groundImpactPos = new Vec3d(groundPos.getX() + 0.5, groundPos.getY() + 1.0, groundPos.getZ() + 0.5);

        double maxRange = radius;
        float damage = ParticlesDamage;

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.EXPLOSION,
                    groundImpactPos.x, groundImpactPos.y, groundImpactPos.z,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );
        }

        if (swing) {
            // Swing mode: spawn particles with delay from right to left
            spawnSwingParticlesWithDelay(world, source, groundImpactPos, particleCount, maxRange, damage, impactDistance);
        } else {
            // Normal mode: spawn all particles at once
            for (int i = 0; i < particleCount; i++) {
                double angle = (2 * Math.PI * i) / particleCount;
                Vec3d particleDirection = new Vec3d(Math.cos(angle), 0, Math.sin(angle));

                traceParticlePath(world, groundImpactPos, particleDirection, maxRange, damage, source);

                if (i % 2 == 0) {
                    source.getWorld().playSound(
                            null,
                            source.getX(),
                            source.getY(),
                            source.getZ(),
                            SoundEvents.ENTITY_GENERIC_EXPLODE,
                            SoundCategory.HOSTILE,
                            0.6f,
                            0.1f
                    );
                }
            }
        }
    }

    private static void spawnSwingParticlesWithDelay(World world, LivingEntity source, Vec3d groundImpactPos, int particleCount, double maxRange, float damage, double impactDistance) {
        double entityYaw = Math.toRadians(source.getYaw());

        double forwardX = source.getX() + (-Math.sin(entityYaw)) * impactDistance;
        double forwardZ = source.getZ() + Math.cos(entityYaw) * impactDistance;

        double rightX = forwardX - Math.cos(entityYaw) * maxRange;
        double rightZ = forwardZ - Math.sin(entityYaw) * maxRange;
        double leftX = forwardX + Math.cos(entityYaw) * maxRange;
        double leftZ = forwardZ + Math.sin(entityYaw) * maxRange;

        Vec3d particleDirection = new Vec3d(-Math.sin(entityYaw), 0, Math.cos(entityYaw));

        for (int i = 0; i < particleCount; i++) {
            final int particleIndex = i;

            int delayTicks = i * 3;

            if (world instanceof ServerWorld serverWorld) {
                serverWorld.getServer().execute(() -> {
                    scheduleDelayedParticle(serverWorld, source, rightX, rightZ, leftX, leftZ,
                            particleIndex, particleCount, particleDirection, maxRange, damage, delayTicks);
                });
            }
        }
    }

    private static void scheduleDelayedParticle(ServerWorld world, LivingEntity source, double rightX, double rightZ,
                                                double leftX, double leftZ, int particleIndex, int particleCount,
                                                Vec3d particleDirection, double maxRange, float damage, int delayTicks) {

        // Create a delayed task
        world.getServer().execute(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(delayTicks * 15L);

                    double progress = (double) particleIndex / (particleCount - 1);
                    double currentX = rightX + (leftX - rightX) * progress;
                    double currentZ = rightZ + (leftZ - rightZ) * progress;

                    BlockPos currentGroundPos = findGroundLevel(world, new Vec3d(currentX, source.getY(), currentZ), source);
                    Vec3d startPos = new Vec3d(currentGroundPos.getX() + 0.5, currentGroundPos.getY() + 1.0, currentGroundPos.getZ() + 0.5);

                    world.getServer().execute(() -> {
                        traceParticlePath(world, startPos, particleDirection, maxRange, damage, source);

                        if (particleIndex % 2 == 0) {
                            world.playSound(null, source.getX(), source.getY(), source.getZ(),
                                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 0.6f, 0.1f);
                        }
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    public static void traceParticlePath(World world, Vec3d startPos, Vec3d direction, double maxRange, float damage, LivingEntity source) {
        double stepSize = 0.3;
        int steps = (int)(maxRange / stepSize);

        Set<Entity> hitEntities = new HashSet<>();

        for (int step = 1; step <= steps; step++) {
            Vec3d currentPos = startPos.add(direction.multiply(step * stepSize));

            // Spawn visual particles along the ground
            if (world instanceof ServerWorld serverWorld) {
                // Spawn particles slightly above ground level
                Vec3d particlePos = new Vec3d(currentPos.x, startPos.y + 0.1, currentPos.z);

                serverWorld.spawnParticles(
                        ParticleTypes.DUST_PLUME,
                        particlePos.x, particlePos.y, particlePos.z,
                        2,
                        0.2, 0.1, 0.2,
                        0.05
                );

                // Add some flame particles for extra effect
                serverWorld.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        particlePos.x, particlePos.y + 0.2, particlePos.z,
                        1,
                        0.1, 0.1, 0.1,
                        0.02
                );
            }

            Box checkBox = new Box(
                    currentPos.subtract(0.6, 0.0, 0.6),
                    currentPos.add(0.6, 2.0, 0.6)
            );
            List<Entity> nearbyEntities = world.getOtherEntities(source, checkBox);

            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity livingEntity && !hitEntities.contains(entity)) {
                    if (entity != source && canDamageEntity(livingEntity, source)) {
                        hitEntities.add(entity);

                        // Deal damage
                        DamageSource damageSource = source.getDamageSources().mobAttack(source);
                        livingEntity.damage(damageSource, damage);

                        // Visual effect on hit
                        if (world instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(
                                    ParticleTypes.DAMAGE_INDICATOR,
                                    entity.getX(), entity.getY() + entity.getHeight() * 0.5, entity.getZ(),
                                    10,
                                    0.3, 0.3, 0.3,
                                    0.2
                            );
                        }
                    }
                }
            }

            double distanceFromOrigin = step * stepSize;
            if (distanceFromOrigin > maxRange * 0.8) {
                if (world.getRandom().nextFloat() > 0.5f) {
                    continue;
                }
            }
        }
    }

    private static BlockPos findGroundLevel(World world, Vec3d position, LivingEntity source) {
        BlockPos startPos = BlockPos.ofFloored(position);

        // Raycast down to find solid ground
        for (int y = startPos.getY(); y >= world.getBottomY(); y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            if (!world.getBlockState(checkPos).isAir()) {
                return checkPos; // Found solid ground
            }
        }

        // If no ground found, use the original Y level
        return startPos;
    }

    private static boolean canDamageEntity(LivingEntity target, LivingEntity source) {
        if (target == source) {
            return false;
        }
        if (target.getClass() == source.getClass()) {
            return false;
        }
        if (target.isDead()) {
            return false;
        }

        return true;
    }

    public static void playStepSound(LivingEntity source) {
        source.getWorld().playSound(null, source.getX(), source.getY(), source.getZ(),
                ModSounds.HUGE_STEP, SoundCategory.HOSTILE, 2.0f, 0.6f);
    }

    public static void spawnProjectileSpread(World world, LivingEntity thrower, String projectile) {
        spawnProjectileSpread(world, thrower, projectile, null); }

    public static void spawnProjectileSpread(World world, LivingEntity thrower, String projectile, Vec3d targetPos) {
        if (world.isClient) return;

        float baseYaw;
        float basePitch;

        if (targetPos != null) {
            Vec3d throwerPos = new Vec3d(thrower.getX(), thrower.getEyeY(), thrower.getZ());
            Vec3d direction = targetPos.subtract(throwerPos).normalize();

            double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
            baseYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
            basePitch = (float) Math.toDegrees(Math.atan2(-direction.y, horizontalDistance));
        } else {
            baseYaw = thrower.getYaw();
            basePitch = thrower.getPitch();
        }

        double yawRadians = Math.toRadians(baseYaw);
        double pitchRadians = Math.toRadians(basePitch);

        double sideX = -Math.sin(yawRadians + Math.PI / 2);
        double sideZ = Math.cos(yawRadians + Math.PI / 2);

        if (projectile.equals("blade")) {

            double offsetY;
            if (thrower instanceof PlayerEntity player) {
                offsetY = 1.5;
            } else {
                offsetY = 3;
            }

            for (int i = 0; i < 5; i++) {
                GronkBladeProjectileEntity blade = new GronkBladeProjectileEntity(world, thrower);

                double sideOffset = (i - 2) * 2.0;

                double finalX = thrower.getX() + (sideX * sideOffset);
                double finalY = thrower.getEyeY() - offsetY;
                double finalZ = thrower.getZ() + (sideZ * sideOffset);

                blade.setPosition(finalX, finalY, finalZ);

                float angleOffset = (i - 2) * 15.0f;

                float bladeYaw = baseYaw + angleOffset;
                double bladeYawRadians = Math.toRadians(bladeYaw);

                double bladeForwardX = -Math.sin(bladeYawRadians) * Math.cos(pitchRadians);
                double bladeForwardZ = Math.cos(bladeYawRadians) * Math.cos(pitchRadians);

                double speed = 0.8;
                Vec3d velocity = new Vec3d(bladeForwardX * speed, 0, bladeForwardZ * speed);
                blade.setStableVelocity(velocity);

                world.spawnEntity(blade);
            }
        } else if (projectile.equals("rock")) {

            double offsetY;
            if (thrower instanceof PlayerEntity player) {
                offsetY = 1.0;
            } else {
                offsetY = 10;
            }

            for (int i = 0; i < 5; i++) {
                ChunkyRockProjectileEntity rock = new ChunkyRockProjectileEntity(world, thrower);

                double sideOffset = (i - 2) * 2.0;

                double finalX = thrower.getX() + (sideX * sideOffset);
                double finalY = thrower.getEyeY() - offsetY;
                double finalZ = thrower.getZ() + (sideZ * sideOffset);

                rock.setPosition(finalX, finalY, finalZ);

                float angleOffset = (i - 2) * 15.0f;

                float bladeYaw = baseYaw + angleOffset;
                double bladeYawRadians = Math.toRadians(bladeYaw);

                double bladeForwardX = -Math.sin(bladeYawRadians) * Math.cos(pitchRadians);
                double bladeForwardZ = Math.cos(bladeYawRadians) * Math.cos(pitchRadians);

                double speed = 0.8;
                Vec3d velocity = new Vec3d(bladeForwardX * speed, 0, bladeForwardZ * speed);
                rock.setStableVelocity(velocity);

                world.spawnEntity(rock);
            }
        }
    }

}
