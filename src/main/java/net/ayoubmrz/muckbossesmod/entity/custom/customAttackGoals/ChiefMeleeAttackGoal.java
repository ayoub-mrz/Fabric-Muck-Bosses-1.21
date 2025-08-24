package net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals;

import net.ayoubmrz.muckbossesmod.entity.custom.bosses.ChiefEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.ChiefSpearProjectileEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkBladeProjectileEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkSwordProjectileEntity;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class ChiefMeleeAttackGoal extends Goal {
    protected final ChiefEntity mob;
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

    public ChiefMeleeAttackGoal(ChiefEntity mob, double speed, boolean pauseWhenMobIdle) {
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
        ++this.timeWithoutAttack;

//      Start Shooting if Entity didn't attack after certain time
        if (++this.timeWithoutAttack >= 150) {
            this.hasShooted = false;
            this.hasAttacked = true;
            if (this.timeAfterHits < 55) {
                this.timeAfterHits = 55;
            }
        }

        if (isJumpAttacking) {
            handleJumpAttack();
        }

        if (hasAttacked) {
            timeAfterHits++;
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
            this.mob.getLookControl().lookAt(livingEntity, 10.0F, 20.0F);
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
                this.mob.getLookControl().lookAt(livingEntity, 10.0F, 20.0F);
                this.attack(this.mob.getTarget());
                this.hasShooted = false;
                this.timeAfterHits = 0;
            } else if (!hasShooted && this.timeAfterHits >= 60) {
                this.mob.getLookControl().lookAt(livingEntity, 10.0F, 20.0F);
                if (this.lastShoot == null || this.lastShoot.equals("jump")) {
                    jumpAttack();
                } else if (this.lastShoot.equals("spear_throw")) {
                    spearSpinAttack();
                } else if (this.lastShoot.equals("spin")) {
                    jumpAttack();
                }
            }

        }
    }

    public void jumpAttack() {
        LivingEntity target = this.mob.getTarget();

        if (target == null || isJumpAttacking) {
            return;
        }

        // Start jump attack
        isJumpAttacking = true;
        jumpAttackPhase = 1; // ascending phase

        // Store target position for landing
        jumpTargetPos = new Vec3d(target.getX(), target.getY(), target.getZ());

        // Calculate jump velocity
        double deltaX = jumpTargetPos.x - this.mob.getX();
        double deltaZ = jumpTargetPos.z - this.mob.getZ();
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // Horizontal velocity to reach target
        double jumpTime = 1.5; // Time in seconds to reach target
        double velocityX = deltaX / jumpTime / 20; // Convert to ticks (20 ticks per second)
        double velocityZ = deltaZ / jumpTime / 20;

        // Vertical velocity for 4 blocks high jump
        double velocityY = 1.2; // Adjust this value to control jump height

        // Apply the jump velocity
        this.mob.setVelocity(velocityX, velocityY, velocityZ);
        this.mob.velocityModified = true;

        // Play jump sound
        this.mob.getWorld().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                ModSounds.SPEAR_SWING, SoundCategory.HOSTILE, 1.0f, 0.5f);
    }

    // Add this to your existing tick() method in ChiefEntity (add it where your other tick logic is)
    private void handleJumpAttack() {
        if (!isJumpAttacking) return;

        if (jumpAttackPhase == 1) { // Ascending
            if (this.mob.getVelocity().y <= 0) {
                jumpAttackPhase = 2; // Start descending
            }
        } else if (jumpAttackPhase == 2) { // Descending
            if (this.mob.isOnGround()) {
                jumpAttackPhase = 3; // Landing
                performLandingAttack();
            }
        } else if (jumpAttackPhase == 3) { // Landing complete
            isJumpAttacking = false;
            jumpAttackPhase = 0;
            jumpTargetPos = null;
        }
    }

    private void performLandingAttack() {
        if (jumpTargetPos == null) return;

        // Create landing impact area (3x3 blocks around landing point)
        double impactRadius = 2.0;

        // Find entities in impact area
        Box impactArea = new Box(
                this.mob.getX() - impactRadius, this.mob.getY() - 1, this.mob.getZ() - impactRadius,
                this.mob.getX() + impactRadius, this.mob.getY() + 2, this.mob.getZ() + impactRadius
        );

        this.mob.getWorld().getEntitiesByClass(LivingEntity.class, impactArea, entity ->
                entity != this.mob && entity instanceof PlayerEntity
        ).forEach(entity -> {
            // Damage the player
            entity.damage(this.mob.getDamageSources().mobAttack(this.mob), 8.0f);

            // Apply strong knockback
            UsefulMethods.applyKnockback(entity, this.mob, 0.5f, 2.0f);
        });

        // Landing effects
        if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
            // Spawn particles at landing point
            for (int i = 0; i < 30; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 4;
                double offsetZ = (this.random.nextDouble() - 0.5) * 4;
                serverWorld.spawnParticles(ParticleTypes.EXPLOSION,
                        this.mob.getX() + offsetX, this.mob.getY() + 0.1, this.mob.getZ() + offsetZ,
                        1, 0, 0, 0, 0.1);
            }
        }

        // Play landing sound
        this.mob.getWorld().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 2.0f, 0.8f);
    }

    private void spearSpinAttack() {
        Entity target = this.mob.getTarget();

        if (target == null) {
            this.hasAttacked = false;
            this.hasShooted = true;
            this.timeWithoutAttack = 0;
            this.timeAfterHits = 0;
            return;
        }

        if (!animationTriggered) {
            if (this.mob.distanceTo(target) > 3.0f) {
                this.mob.getNavigation().startMovingTo(target, this.speed);
                return;
            }

            this.mob.setSpinAttack(true);
            this.waitForAnimation = 0;
            this.animationTriggered = true;
        }

        if (this.waitForAnimation == 3) {
            this.mob.setSpinAttack(false);
        }

        this.waitForAnimation++;
        if (this.waitForAnimation == 20) {
            this.isPerformingParticleAttack = true;
            this.particlesNumber = 20;
            this.particleDelayTimer = 0;
            this.particleAttackTimer = 30;
        }
        if (this.waitForAnimation == 40) {
            this.animationTriggered = false;
            this.waitForAnimation = 0;
            this.hasAttacked = false;
            this.lastShoot = "spin";
            this.hasShooted = true;
            this.timeWithoutAttack = 0;
            this.timeAfterHits = 0;
        }
    }

    private void throwSpearAttack() {
        if (!animationTriggered) {
            this.mob.setShootingSpear(true);
            this.waitForAnimation = 0;
            this.animationTriggered = true;
        }
        this.waitForAnimation++;
        if (this.waitForAnimation == 27) {
            this.shootSpear();
            this.mob.setChiefStats("chief_without_spear");
        }
        if (this.waitForAnimation == 35) {
            this.mob.setChiefStats("chief_with_spear");
            this.waitForAnimation = 0;
            this.animationTriggered = false;
            this.hasAttacked = false;
            this.lastShoot = "spear_throw";
            this.hasShooted = true;
            this.timeWithoutAttack = 0;
            this.timeAfterHits = 0;
        }
    }

    private void shootSpear() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {

            // Calculate forward direction
            double forwardX = -Math.sin(Math.toRadians(this.mob.getYaw()));
            double forwardZ = Math.cos(Math.toRadians(this.mob.getYaw()));

            // Calculate left direction (perpendicular to forward)
            double leftX = forwardZ; // Left is opposite of right
            double leftZ = -forwardX;

            // Position offsets for left shoulder
            double forwardOffset = 0.5; // Slightly forward from center
            double leftOffset = 1.2;    // To the left side
            double upOffset = 0.6;       // Higher up for shoulder height

            // Calculate spawn position at left shoulder
            double spawnX = this.mob.getX() + (forwardX * forwardOffset) + (leftX * leftOffset);
            double spawnY = this.mob.getY() + this.mob.getHeight() * 0.8 + upOffset;
            double spawnZ = this.mob.getZ() + (forwardZ * forwardOffset) + (leftZ * leftOffset);

            ChiefSpearProjectileEntity spear = new ChiefSpearProjectileEntity(this.mob.getWorld(), this.mob);
            spear.setPosition(spawnX, spawnY, spawnZ);

            double dX = target.getX() - spear.getX();
            double dY = target.getBodyY(0.5) - spear.getY();
            double dZ = target.getZ() - spear.getZ();

            double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);

            float upwardForce = 0.2f + (float)horizontalDistance * 0.1f;
            dY += upwardForce;

            double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
            dX /= distance;
            dY /= distance;
            dZ /= distance;

            double speed = 5;
            spear.setVelocity(
                    dX * speed,
                    dY * speed,
                    dZ * speed,
                    2.5f,
                    2.0f
            );

            this.mob.getWorld().spawnEntity(spear);
        }
    }

    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(target);
            this.hasAttacked = true;
            // Start the timed particle attack with delay
            this.isPerformingParticleAttack = true;
            this.particleDelayTimer = 15; // delay before particles start
            this.particlesNumber = 1;
            this.particleAttackTimer = 5; // 1 second of particles after delay
            this.mob.getWorld().playSound(
                    null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                    ModSounds.SPEAR_SWING, SoundCategory.HOSTILE, 4.0f, 0.6f
            );
        }
//        if (this.canAttack(target)) {
//            this.resetCooldown();
//            this.mob.swingHand(Hand.MAIN_HAND);
//
//            this.timeWithoutAttack = 0;
//
////            this.hasAttacked = true;
//        }
    }

    protected void resetCooldown() { this.cooldown = this.getTickCount(20); }

    protected boolean isCooledDown() { return this.cooldown <= 0; }

    protected boolean canAttack(LivingEntity target) {
        return this.isCooledDown() && this.mob.isInAttackRange(target) && this.mob.getVisibilityCache().canSee(target);
    }

}