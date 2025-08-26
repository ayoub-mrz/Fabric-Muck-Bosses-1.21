package net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals;

import net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian.BlueGuardianEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.UsefulMethods;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.*;

public class GuardianMeleeAttackGoal extends Goal {
    protected final BlueGuardianEntity mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private int cooldown;
    private long lastUpdateTime;
    private int shootCooldown = 0;
    private int waitForAnimation = 0;
    public boolean animationTriggered = false;
    Random random = new Random();
    private int startShooting = random.nextInt(10, 21);
    private boolean hasAttacked = false;
    private int timeWithoutAttack = 0;
    private int particleAttackTimer = 0;
    private boolean isPerformingParticleAttack = false;
    private int particleDelayTimer = 0;
    private boolean hasShooted = false;
    private int timeAfterHits = 0;
    private String lastShoot;
    private int particlesNumber = 0;
    private boolean playerIsNear = false;
    private boolean isJumpAttacking = false;
    private Vec3d jumpTargetPos = null;
    private int jumpAttackPhase = 0;
    private int attackTicks = 0;
    private boolean shootLazer = false;

    private final LinkedList<Vec3d> targetPositionHistory = new LinkedList<>();
    private final int POSITION_HISTORY_SIZE = 5;

    public GuardianMeleeAttackGoal(BlueGuardianEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        long l = this.mob.getWorld().getTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        } else {
            this.lastUpdateTime = l;
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.mob.isInAttackRange(livingEntity);
                }
            }
        }
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isIdle();
        } else if (!this.mob.isInWalkTargetRange(livingEntity.getBlockPos())) {
            return false;
        } else {
            return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative();
        }
    }

    public void start() {
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget((LivingEntity)null);
        }

        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();

        // Reset all attack states when stopping
        resetAllAttackStates();
    }

    private void resetAllAttackStates() {
        // Reset laser attack states
        this.shootLazer = false;
        this.waitForAnimation = 0;
        this.animationTriggered = false;

        // Reset general attack states
        this.hasAttacked = false;
        this.hasShooted = false;
        this.timeAfterHits = 0;
        this.timeWithoutAttack = 0;
        this.lastShoot = null;

        // Reset particle attack states
        this.isPerformingParticleAttack = false;
        this.particleDelayTimer = 0;
        this.particleAttackTimer = 0;
        this.particlesNumber = 0;

        // Reset jump attack states
        this.isJumpAttacking = false;
        this.jumpTargetPos = null;
        this.jumpAttackPhase = 0;
        this.attackTicks = 0;

        // Clear position history
        this.targetPositionHistory.clear();

        // Restore normal gravity in case entity was flying
        this.mob.setNoGravity(false);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        ++this.shootCooldown;
        ++this.timeWithoutAttack;

        // Track target position history every tick
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            updateTargetPositionHistory(target);
        }

//      Start Shooting if Entity didn't attack after certain time
        if (this.timeWithoutAttack == 200) {
            this.hasShooted = false;
            this.hasAttacked = true;
            int randomNum = random.nextInt(0, 10);
            this.lastShoot = randomNum < 5 ? "lightning" : "lazer";
            this.timeWithoutAttack = 0;
            if (this.timeAfterHits < 55) {
                this.timeAfterHits = 55;
            }
        }

        if (hasAttacked) {
            timeAfterHits++;
        }

        if (shootLazer) {
            shootLazerProjectile();
        }

        if (this.isPerformingParticleAttack) {
            if (this.particleDelayTimer > 0) {
                this.particleDelayTimer--;
                return;
            }

            this.particleAttackTimer--;

            if (this.particleAttackTimer > 0 && this.particleAttackTimer % 2 == 0) {
                UsefulMethods.spawnDamagingParticles(this.particlesNumber, this.mob);
            }

            if (this.particleAttackTimer <= 0) {
                this.isPerformingParticleAttack = false;
            }

            return;
        }

        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity != null) {
            if (!isJumpAttacking) { this.mob.getLookControl().lookAt(livingEntity, 10.0F, 20.0F); }
            if (!this.hasAttacked) {

                this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);

                if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == (double) 0.0F && this.targetY == (double) 0.0F && this.targetZ == (double) 0.0F || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= (double) 1.0F || this.mob.getRandom().nextFloat() < 0.05F)) {
                    this.targetX = livingEntity.getX();
                    this.targetY = livingEntity.getY();
                    this.targetZ = livingEntity.getZ();
                    this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                    double d = this.mob.squaredDistanceTo(livingEntity);
                    if (d > (double) 1024.0F) { this.updateCountdownTicks += 10; }
                    else if (d > (double) 256.0F) { this.updateCountdownTicks += 5; }
                    if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) { this.updateCountdownTicks += 15; }
                    this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
                }
                this.cooldown = Math.max(this.cooldown - 1, 0);
                if (!isJumpAttacking) { this.mob.getLookControl().lookAt(livingEntity, 10.0F, 20.0F); }
                this.attack(this.mob.getTarget());
                this.hasShooted = false;
                this.timeAfterHits = 0;
            } else if (!hasShooted && this.timeAfterHits >= 60) {
                if (!isJumpAttacking) { this.mob.getLookControl().lookAt(livingEntity, 10.0F, 20.0F); }
                if (this.lastShoot == null || this.lastShoot.equals("lightning")) {
                    this.lazerAttack();
                } else if (this.lastShoot.equals("lazer")) {
                    this.spawnLightning();
                }
            }
        }
    }

    private void updateTargetPositionHistory(LivingEntity target) {
        Vec3d currentPos = new Vec3d(target.getX(), target.getY(), target.getZ());

        // Add current position to the front of the list
        targetPositionHistory.addFirst(currentPos);

        // Keep only the last POSITION_HISTORY_SIZE positions
        if (targetPositionHistory.size() > POSITION_HISTORY_SIZE) {
            targetPositionHistory.removeLast();
        }
    }

    private Vec3d getTargetPositionFromTicksAgo(int ticksAgo) {
        if (targetPositionHistory.size() > ticksAgo) {
            return targetPositionHistory.get(ticksAgo);
        }
        // If we don't have enough history, return the oldest position we have
        if (!targetPositionHistory.isEmpty()) {
            return targetPositionHistory.getLast();
        }
        // Fallback to current target position if no history exists
        LivingEntity target = this.mob.getTarget();
        return target != null ? new Vec3d(target.getX(), target.getY(), target.getZ()) : null;
    }

    private void spawnLightning() {
        LivingEntity target = this.mob.getTarget();

        if (!animationTriggered) {
            this.mob.setShootingLightning(true);
            this.waitForAnimation = 0;
            this.animationTriggered = true;

            this.mob.setNoGravity(true);
        }

        if (waitForAnimation == 3) {
            this.mob.setShootingLightning(false);
        }

        this.waitForAnimation++;

        if (this.waitForAnimation <= 30) {
            Vec3d currentVelocity = this.mob.getVelocity();
            this.mob.setVelocity(currentVelocity.x, 0.35, currentVelocity.z);
        }
        else if (this.waitForAnimation <= 50) {
            Vec3d currentVelocity = this.mob.getVelocity();
            this.mob.setVelocity(currentVelocity.x, -1.6, currentVelocity.z);
            if (this.waitForAnimation == 40) {
                if (target != null) {
                    for (int i=0; i < 10; i++) {
                        spawnLightningAtTarget(target);
                    }
                }
            }
        }
        else if (this.waitForAnimation >= 70) {
            this.shootLazer = false;
            this.waitForAnimation = 0;
            this.animationTriggered = false;
            this.hasAttacked = false;
            this.lastShoot = "lightning";
            this.hasShooted = true;
            this.timeWithoutAttack = 0;
            this.timeAfterHits = 0;

            this.mob.setNoGravity(false);
        }
    }

    private void spawnLightningAtTarget(LivingEntity target) {
        if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d pastPosition = getTargetPositionFromTicksAgo(10);

            double targetX, targetY, targetZ;
            if (pastPosition != null) {
                targetX = pastPosition.x;
                targetY = pastPosition.y;
                targetZ = pastPosition.z;
            } else {
                targetX = target.getX();
                targetY = target.getY();
                targetZ = target.getZ();
            }

            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(serverWorld);
            if (lightning != null) {
                lightning.refreshPositionAfterTeleport(targetX, targetY, targetZ);
                serverWorld.spawnEntity(lightning);
            }

            serverWorld.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    targetX, targetY + 1, targetZ,
                    20,
                    1.0, 2.0, 1.0,
                    0.1
            );

            serverWorld.spawnParticles(
                    ParticleTypes.SMOKE,
                    targetX, targetY, targetZ,
                    10,
                    0.5, 0.5, 0.5,
                    0.05
            );

            damageAreaAroundLightning(targetX, targetY, targetZ, serverWorld);

            this.mob.getWorld().playSound(
                    null, targetX, targetY, targetZ,
                    SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                    SoundCategory.HOSTILE,
                    1.0f,
                    0.8f
            );

            this.mob.getWorld().playSound(
                    null, targetX, targetY, targetZ,
                    SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                    SoundCategory.HOSTILE,
                    2.0f,
                    1.2f
            );
        }
    }

    private void damageAreaAroundLightning(double centerX, double centerY, double centerZ, ServerWorld world) {
        double radius = 3.0; // 3 block radius
        Box damageBox = new Box(
                centerX - radius, centerY - 1, centerZ - radius,
                centerX + radius, centerY + 3, centerZ + radius
        );

        List<LivingEntity> entitiesInArea = world.getEntitiesByClass(
                LivingEntity.class,
                damageBox,
                entity -> entity != this.mob && entity.isAlive()
        );

        for (LivingEntity entity : entitiesInArea) {
            double distance = entity.squaredDistanceTo(centerX, centerY, centerZ);

            if (distance <= radius * radius) {
                float damage = 8.0f;
                entity.damage(world.getDamageSources().lightningBolt(), damage);

                UsefulMethods.applyKnockback(entity, this.mob, 0.02f, 0.2f);

                world.spawnParticles(
                        ParticleTypes.FLAME,
                        entity.getX(), entity.getY() + entity.getHeight() / 2, entity.getZ(),
                        12,
                        0.4, 0.6, 0.4,
                        0.08
                );

                world.spawnParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        entity.getX(), entity.getY() + entity.getHeight() / 2, entity.getZ(),
                        8,
                        0.3, 0.5, 0.3,
                        0.1
                );
            }
        }
    }

    private void lazerAttack() {
        if (!animationTriggered) {
            this.mob.setShootingLazer(true);
            this.mob.setLazerSoundStart(true);
            this.waitForAnimation = 0;
            this.animationTriggered = true;
        }
        this.waitForAnimation++;
        if (this.waitForAnimation == 30) {
            LivingEntity livingEntity = this.mob.getTarget();
            this.mob.getLookControl().lookAt(livingEntity, 10.0F, 20.0F);
            this.shootLazer = true;
        }
        if (this.waitForAnimation == 70) {
            this.shootLazer = false;
            this.waitForAnimation = 0;
            this.animationTriggered = false;
            this.hasAttacked = false;
            this.lastShoot = "lazer";
            this.hasShooted = true;
            this.timeWithoutAttack = 0;
            this.timeAfterHits = 0;
        }
    }

    private void shootLazerProjectile() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && this.mob.getWorld() instanceof ServerWorld serverWorld) {

            double forwardX = -Math.sin(Math.toRadians(this.mob.getYaw()));
            double forwardZ = Math.cos(Math.toRadians(this.mob.getYaw()));

            double forwardOffset = -0.5;
            double upOffset = 0.6;

            double laserStartX = this.mob.getX() + forwardX * forwardOffset;
            double laserStartY = this.mob.getY() + this.mob.getHeight() * 0.8 + upOffset;
            double laserStartZ = this.mob.getZ() + forwardZ * forwardOffset;

            double targetX = target.getX();
            double targetY = target.getBodyY(0.5);
            double targetZ = target.getZ();

            double dX = targetX - laserStartX;
            double dY = targetY - laserStartY;
            double dZ = targetZ - laserStartZ;
            double totalDistance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

            dX /= totalDistance;
            dY /= totalDistance;
            dZ /= totalDistance;

            // Check for block collisions along the laser path
            Vec3d hitPoint = checkLaserCollision(laserStartX, laserStartY, laserStartZ,
                    targetX, targetY, targetZ, serverWorld);

            // If we hit a block, adjust the laser endpoint
            double laserEndX, laserEndY, laserEndZ;
            double actualDistance;

            if (hitPoint != null) {
                laserEndX = hitPoint.x;
                laserEndY = hitPoint.y;
                laserEndZ = hitPoint.z;
                actualDistance = Math.sqrt(
                        Math.pow(laserEndX - laserStartX, 2) +
                                Math.pow(laserEndY - laserStartY, 2) +
                                Math.pow(laserEndZ - laserStartZ, 2)
                );
            } else {
                laserEndX = targetX;
                laserEndY = targetY;
                laserEndZ = targetZ;
                actualDistance = totalDistance;
            }

            int particleCount = (int) Math.min(actualDistance * 2, 100);
            double stepSize = actualDistance / particleCount;

            // Recalculate direction for the actual laser path
            double actualDX = laserEndX - laserStartX;
            double actualDY = laserEndY - laserStartY;
            double actualDZ = laserEndZ - laserStartZ;
            actualDX /= actualDistance;
            actualDY /= actualDistance;
            actualDZ /= actualDistance;

            for (int i = 0; i < particleCount; i++) {
                double currentX = laserStartX + (actualDX * stepSize * i);
                double currentY = laserStartY + (actualDY * stepSize * i);
                double currentZ = laserStartZ + (actualDZ * stepSize * i);

                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        currentX, currentY, currentZ,
                        3,
                        0.2, 0.2, 0.2,
                        0.0
                );

                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        currentX, currentY, currentZ,
                        2,
                        0.25, 0.25, 0.25,
                        0.0
                );

                if (i % 2 == 0) {
                    serverWorld.spawnParticles(
                            ParticleTypes.ELECTRIC_SPARK,
                            currentX, currentY, currentZ,
                            4,
                            0.25, 0.25, 0.25,
                            0.02
                    );
                }
            }

            serverWorld.spawnParticles(
                    ParticleTypes.EXPLOSION,
                    laserEndX, laserEndY, laserEndZ,
                    1,
                    0.2, 0.2, 0.2,
                    0.0
            );

            serverWorld.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    laserEndX, laserEndY, laserEndZ,
                    15,
                    0.5, 0.5, 0.5,
                    0.1
            );

            // Only damage along the actual laser path (stops at collision point)
            damageLaserPath(laserStartX, laserStartY, laserStartZ, laserEndX, laserEndY, laserEndZ, serverWorld);

            this.mob.getWorld().playSound(
                    null, laserStartX, laserStartY, laserStartZ,
                    SoundEvents.BLOCK_BEACON_ACTIVATE,
                    SoundCategory.HOSTILE,
                    2.0f,
                    1.5f
            );

            this.mob.getWorld().playSound(
                    null, laserEndX, laserEndY, laserEndZ,
                    SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                    SoundCategory.HOSTILE,
                    1.5f,
                    2.0f
            );
        }
    }

    private Vec3d checkLaserCollision(double startX, double startY, double startZ,
                                      double endX, double endY, double endZ,
                                      ServerWorld world) {

        Vec3d start = new Vec3d(startX, startY, startZ);
        Vec3d end = new Vec3d(endX, endY, endZ);

        // Use Minecraft's built-in raycast to check for block collisions
        BlockHitResult hitResult = world.raycast(
                new RaycastContext(
                        start,
                        end,
                        RaycastContext.ShapeType.COLLIDER, // Check collision boxes
                        RaycastContext.FluidHandling.NONE, // Ignore fluids
                        this.mob
                )
        );

        // If we hit a block, return the hit position
        if (hitResult.getType() != HitResult.Type.MISS) {
            // Get the block state at the hit position
            BlockPos hitPos = hitResult.getBlockPos();
            BlockState blockState = world.getBlockState(hitPos);

            // Check if the block should stop the laser (solid blocks)
            if (!blockState.isAir() && blockState.isSolidBlock(world, hitPos)) {
                return hitResult.getPos();
            }
        }

        // No collision found, laser can reach the target
        return null;
    }

    private void damageLaserPath(double startX, double startY, double startZ,
                                 double endX, double endY, double endZ,
                                 ServerWorld world) {

        Box laserBox = new Box(
                Math.min(startX, endX) - 1, Math.min(startY, endY) - 1, Math.min(startZ, endZ) - 1,
                Math.max(startX, endX) + 1, Math.max(startY, endY) + 1, Math.max(startZ, endZ) + 1
        );

        List<LivingEntity> entitiesInPath = world.getEntitiesByClass(
                LivingEntity.class,
                laserBox,
                entity -> entity != this.mob && entity.isAlive()
        );

        double pathLength = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2) + Math.pow(endZ - startZ, 2));

        for (LivingEntity entity : entitiesInPath) {
            double entityX = entity.getX();
            double entityY = entity.getBodyY(0.5);
            double entityZ = entity.getZ();

            double distanceToLine = calculatePointToLineDistance(
                    startX, startY, startZ,
                    endX, endY, endZ,
                    entityX, entityY, entityZ
            );

            if (distanceToLine <= 2.0) {
                float damage = 6.0f;
                entity.damage(world.getDamageSources().mobAttack(this.mob), damage);

                UsefulMethods.applyKnockback(entity, this.mob, 0.02f, 0.2f);

                world.spawnParticles(
                        ParticleTypes.FLAME,
                        entityX, entityY, entityZ,
                        8,
                        0.3, 0.5, 0.3,
                        0.05
                );
            }
        }
    }

    private double calculatePointToLineDistance(double lineStartX, double lineStartY, double lineStartZ,
                                                double lineEndX, double lineEndY, double lineEndZ,
                                                double pointX, double pointY, double pointZ) {

        double lineVecX = lineEndX - lineStartX;
        double lineVecY = lineEndY - lineStartY;
        double lineVecZ = lineEndZ - lineStartZ;

        double pointVecX = pointX - lineStartX;
        double pointVecY = pointY - lineStartY;
        double pointVecZ = pointZ - lineStartZ;

        double lineLengthSquared = lineVecX * lineVecX + lineVecY * lineVecY + lineVecZ * lineVecZ;
        if (lineLengthSquared == 0) return Math.sqrt(pointVecX * pointVecX + pointVecY * pointVecY + pointVecZ * pointVecZ);

        double projection = (pointVecX * lineVecX + pointVecY * lineVecY + pointVecZ * lineVecZ) / lineLengthSquared;

        projection = Math.max(0, Math.min(1, projection));

        double closestX = lineStartX + projection * lineVecX;
        double closestY = lineStartY + projection * lineVecY;
        double closestZ = lineStartZ + projection * lineVecZ;

        double distX = pointX - closestX;
        double distY = pointY - closestY;
        double distZ = pointZ - closestZ;

        return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
    }

    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(target);
            this.hasAttacked = true;
        }
    }

    protected void resetCooldown() { this.cooldown = this.getTickCount(20); }

    protected boolean isCooledDown() { return this.cooldown <= 0; }

    protected boolean canAttack(LivingEntity target) {
        return this.isCooledDown() && this.mob.isInAttackRange(target) && this.mob.getVisibilityCache().canSee(target);
    }

}