package net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals;

import net.ayoubmrz.muckbossesmod.entity.custom.bosses.GronkEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkSwordProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class GronkMeleeAttackGoal extends Goal {
    protected final GronkEntity mob;
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
    private int attackDuration = 0;
    private int particleAttackTimer = 0;
    private boolean isPerformingParticleAttack = false;
    private int particleDelayTimer = 0;

    public GronkMeleeAttackGoal(GronkEntity mob, double speed, boolean pauseWhenMobIdle) {
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
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        ++this.shootCooldown;

        if (this.isPerformingParticleAttack) {
            // Handle delay phase
            if (this.particleDelayTimer > 0) {
                this.particleDelayTimer--;
                return; // Don't spawn particles yet, just wait
            }

            // Delay is over, start spawning particles
            this.particleAttackTimer--;

            // Spawn particles every few ticks during the attack duration
            if (this.particleAttackTimer > 0 && this.particleAttackTimer % 2 == 0) {
                spawnDamagingParticles();
            }

            // End the particle attack when timer reaches 0
            if (this.particleAttackTimer <= 0) {
                this.isPerformingParticleAttack = false;
            }

            return;
        }

        if (!this.hasAttacked) {
            LivingEntity livingEntity = this.mob.getTarget();

            if (livingEntity != null) {

                this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
                this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);

                if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == (double) 0.0F && this.targetY == (double) 0.0F && this.targetZ == (double) 0.0F || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= (double) 1.0F || this.mob.getRandom().nextFloat() < 0.05F)) {
                    this.targetX = livingEntity.getX();
                    this.targetY = livingEntity.getY();
                    this.targetZ = livingEntity.getZ();
                    this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                    double d = this.mob.squaredDistanceTo(livingEntity);

                    if (d > (double) 1024.0F) {
                        this.updateCountdownTicks += 10;

                    } else if (d > (double) 256.0F) {
                        this.updateCountdownTicks += 5;
                    }
                    if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {

                        this.updateCountdownTicks += 15;
                    }
                    this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
                }
                this.cooldown = Math.max(this.cooldown - 1, 0);
                this.attack(this.mob.getTarget());
            }
        }
//        else {
////             trigger animation
//            if (!animationTriggered) {
//                this.mob.setShooting(true);
//                this.waitForAnimation = 0;
//                this.animationTriggered = true;
//            }
//
//            this.waitForAnimation++;
//
//            if (this.waitForAnimation == 27) {
//                this.shootSword();
//                this.shootCooldown = 0;
//                this.waitForAnimation = 0;
//                this.animationTriggered = false;
//            }
//        }
    }

    public void spawnDamagingParticles() {
        if (this.mob.getWorld().isClient) {
            return;
        }

        World world = this.mob.getWorld();
        Vec3d mobPos = this.mob.getPos();
        Vec3d lookDirection = this.mob.getRotationVector().normalize();

        double impactDistance = 1.0;
        Vec3d impactPoint = mobPos.add(lookDirection.multiply(impactDistance));

        BlockPos groundPos = findGroundLevel(world, impactPoint);
        Vec3d groundImpactPos = new Vec3d(groundPos.getX() + 0.5, groundPos.getY() + 1.0, groundPos.getZ() + 0.5);

        int particleCount = 12;
        double maxRange = 4.0;
        float damage = 20.0f;

        // Create impact effect at ground zero
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.EXPLOSION,
                    groundImpactPos.x, groundImpactPos.y, groundImpactPos.z,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );
        }

        // Spawn particles in all horizontal directions from the impact point
        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount; // Evenly distribute around 360 degrees

            // Create horizontal direction vector
            Vec3d particleDirection = new Vec3d(Math.cos(angle), 0, Math.sin(angle));

            // Trace the particle path and check for entities
            traceParticlePath(world, groundImpactPos, particleDirection, maxRange, damage);
        }
    }

    private BlockPos findGroundLevel(World world, Vec3d position) {
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

    private void traceParticlePath(World world, Vec3d startPos, Vec3d direction, double maxRange, float damage) {
        double stepSize = 0.3;
        int steps = (int)(maxRange / stepSize);

        Set<Entity> hitEntities = new HashSet<>();

        for (int step = 1; step <= steps; step++) {
            Vec3d currentPos = startPos.add(direction.multiply(step * stepSize));

            // Spawn visual particles along the ground (you can change the particle type as needed)
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
            List<Entity> nearbyEntities = world.getOtherEntities(this.mob, checkBox);

            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity livingEntity && !hitEntities.contains(entity)) {
                    if (entity != this.mob && canDamageEntity(livingEntity)) {
                        hitEntities.add(entity);

                        // Deal damage
                        DamageSource damageSource = this.mob.getDamageSources().mobAttack(this.mob);
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

    private boolean canDamageEntity(LivingEntity target) {
        if (target == this.mob) {
            return false;
        }
        if (target.getClass() == this.mob.getClass()) {
            return false;
        }
        if (target.isDead()) {
            return false;
        }

        return true;
    }

    protected void shootSword() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {

            double offsetX = -Math.sin(Math.toRadians(this.mob.getYaw())) * 0.5;
            double offsetZ = Math.cos(Math.toRadians(this.mob.getYaw())) * 0.5;

            GronkSwordProjectileEntity sword = new GronkSwordProjectileEntity(this.mob.getWorld(), this.mob);
            sword.setPosition(
                    this.mob.getX() + offsetX,
                    this.mob.getY() + this.mob.getHeight() * 0.5,
                    this.mob.getZ() + offsetZ
            );

            double dX = target.getX() - sword.getX();
            double dY = target.getBodyY(0.5) - sword.getY();
            double dZ = target.getZ() - sword.getZ();

            double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);

            float upwardForce = 0.2f + (float)horizontalDistance * 0.1f;
            dY += upwardForce;

            double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
            dX /= distance;
            dY /= distance;
            dZ /= distance;

            double speed = 1.2;
            sword.setVelocity(
                    dX * speed,
                    dY * speed,
                    dZ * speed,
                    2.0f,
                    1.0f
            );

            // Making sword face the same face as the Goblin
            sword.setYaw(this.mob.getYaw());
            sword.setPitch(this.mob.getPitch());

            this.mob.getWorld().spawnEntity(sword);
        }
    }

    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);

            // Start the timed particle attack with delay
            this.isPerformingParticleAttack = true;
            this.particleDelayTimer = 15; // delay before particles start
            this.particleAttackTimer = 20; // 1 second of particles after delay

            this.hasAttacked = true;
        }
    }

    protected void resetCooldown() {
        this.cooldown = this.getTickCount(20);
    }

    protected boolean isCooledDown() {
        return this.cooldown <= 0;
    }

    protected boolean canAttack(LivingEntity target) {
        return this.isCooledDown() && this.mob.isInAttackRange(target) && this.mob.getVisibilityCache().canSee(target);
    }

}