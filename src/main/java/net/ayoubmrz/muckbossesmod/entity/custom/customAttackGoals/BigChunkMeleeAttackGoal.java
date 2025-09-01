package net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals;

import net.ayoubmrz.muckbossesmod.entity.custom.bosses.BaseValues;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.BigChunkEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class BigChunkMeleeAttackGoal extends Goal {
    protected final BigChunkEntity mob;
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
    private String lastAttack;
    private int attackCooledown = 0;
    private boolean firstAttack = false;
    private int clubSwingTimer = 0;
    private boolean isAttacking = false;
    private int attackAge = 0;

    public BigChunkMeleeAttackGoal(BigChunkEntity mob, double speed, boolean pauseWhenMobIdle) {
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

        resetAllAttackStates();
    }

    private void resetAllAttackStates() {
        waitForAnimation = 0;
        animationTriggered = false;
        hasAttacked = false;
        timeWithoutAttack = 0;
        particleAttackTimer = 0;
        isPerformingParticleAttack = false;
        particleDelayTimer = 0;
        hasShooted = false;
        timeAfterHits = 0;
        lastAttack = null;
        attackCooledown = 0;
        firstAttack = false;
        clubSwingTimer = 0;
        isAttacking = false;
        attackAge = 0;
    }


    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        ++this.shootCooldown;
        ++this.timeWithoutAttack;
        ++this.attackCooledown;

        if (this.isAttacking) {
            this.mob.getNavigation().stop();
            this.attackAge--;
            if (this.attackAge == 0) {
                this.isAttacking = false;
            }
        }

        if (this.clubSwingTimer > 0) {
            this.clubSwingTimer--;
            if (this.clubSwingTimer == 0) {
                this.mob.setClubSwing(false);
            }
        }

        if (hasAttacked) {
            ++this.timeAfterHits;
        }

        // Start Shooting if Entity didn't attack after certain time
        if (++this.timeWithoutAttack >= 400) {
            this.hasShooted = false;
            this.hasAttacked = true;
            if (this.timeAfterHits < 55) {
                this.timeAfterHits = 55;
            }
        }

        if (this.isPerformingParticleAttack) {
            if (this.particleDelayTimer == 3) {

            }
            if (this.particleDelayTimer > 0) {
                this.particleDelayTimer--;
                return;
            }

            this.particleAttackTimer--;

            if (this.particleAttackTimer > 0 && this.particleAttackTimer % 2 == 0) {
                if (this.lastAttack == null || this.lastAttack.equals("attack")) {
                    UsefulMethods.spawnDamagingParticles(12, this.mob, 7.0,
                            this.mob.clubHit, 6.0, false);
                } else {
                    UsefulMethods.spawnDamagingParticles(12, this.mob, 3.0,
                            this.mob.clubSwing, 10.0, true);
                }
            }

            if (this.particleAttackTimer <= 0) {
                this.isPerformingParticleAttack = false;
            }

            return;
        }

        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity != null) {
            this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);

            if (!this.hasAttacked) {

                if (!isAttacking) {
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
                }
                this.cooldown = Math.max(this.cooldown - 1, 0);

                if (!this.firstAttack || this.attackCooledown > 140) {
                    if (this.lastAttack == null || this.lastAttack.equals("swing")) {
                        this.attack(this.mob.getTarget());
                    } else if (this.lastAttack.equals("attack")) {
                        this.swingAttack(this.mob.getTarget());
                    }
                }

                this.hasShooted = false;
                this.timeAfterHits = 0;

            } else if (!hasShooted && this.timeAfterHits >= 160) {
                this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
                rocksAttack();
            }

        }
    }

    private void swingAttack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.mob.setClubSwing(true);
            this.clubSwingTimer = 3;
            this.isAttacking = true;
            this.attackAge = 80;

//          Start the timed particle attack with delay
            this.isPerformingParticleAttack = true;
            this.particleDelayTimer = 45;
            this.particleAttackTimer = 5;
            this.timeWithoutAttack = 0;
            this.timeAfterHits = 0;

            this.lastAttack = "swing";

            this.attackCooledown = 0;
        }
    }

    private void rocksAttack() {
        if (!animationTriggered) {
            this.mob.setShootingRocks(true);
            this.isAttacking = true;
            this.attackAge = 100;
            this.waitForAnimation = 0;
            this.animationTriggered = true;
            this.mob.getWorld().playSound(
                    null,
                    this.mob.getX(),
                    this.mob.getY(),
                    this.mob.getZ(),
                    SoundEvents.BLOCK_NETHER_BRICKS_PLACE,
                    SoundCategory.HOSTILE,
                    2.0f,
                    1.2f
            );
        }
        this.waitForAnimation++;
        if (this.waitForAnimation <= 40) {
            Vec3d currentVelocity = this.mob.getVelocity();
            this.mob.setVelocity(currentVelocity.x, 0.35, currentVelocity.z);
        } else if (this.waitForAnimation <= 75) {
            Vec3d currentVelocity = this.mob.getVelocity();
            this.mob.setVelocity(currentVelocity.x, -0.45, currentVelocity.z);
            if (this.waitForAnimation == 75) {
                LivingEntity livingEntity = this.mob.getTarget();
                this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
                UsefulMethods.spawnProjectileSpread(this.mob.getWorld(), this.mob, "rock", this.mob.getTarget().getPos());
                UsefulMethods.spawnDamagingParticles(1, this.mob, 3.0, 20.0f, 4.0, false);
            }
        }
        if (this.waitForAnimation == 80) {
            this.waitForAnimation = 0;
            this.animationTriggered = false;
            this.shootCooldown = 0;
            this.hasShooted = true;
            this.hasAttacked = false;
            this.timeWithoutAttack = 0;
        }
    }

    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.firstAttack = true;
            this.isAttacking = true;
            this.attackAge = 80;

//          Start the timed particle attack with delay
            this.isPerformingParticleAttack = true;
            this.particleDelayTimer = 55;
            this.particleAttackTimer = 5;
            this.timeWithoutAttack = 0;
            this.lastAttack = "attack";

            this.attackCooledown = 0;
        }
    }

    protected void resetCooldown() { this.cooldown = this.getTickCount(20); }

    protected boolean isCooledDown() { return this.cooldown <= 0; }

    protected boolean canAttack(LivingEntity target) {
        return isCooledDown() && this.mob.distanceTo(target) < 8.0f && this.mob.getVisibilityCache().canSee(target);
    }

}