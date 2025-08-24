package net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals;

import net.ayoubmrz.muckbossesmod.entity.custom.bosses.GronkEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkBladeProjectileEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.projectiles.GronkSwordProjectileEntity;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
    private int timeWithoutAttack = 0;
    private int particleAttackTimer = 0;
    private boolean isPerformingParticleAttack = false;
    private int particleDelayTimer = 0;
    private boolean hasShooted = false;
    private int timeAfterHits = 0;
    private String lastShoot;

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
        ++this.timeWithoutAttack;

        // Start Shooting if Entity didn't attack after certain time
        if (++this.timeWithoutAttack >= 200) {
            this.hasShooted = false;
            this.hasAttacked = true;
            if (this.timeAfterHits < 55) {
                this.timeAfterHits = 55;
            }
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
                UsefulMethods.spawnDamagingParticles(12, this.mob);
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
                this.attack(this.mob.getTarget());
                this.hasShooted = false;
                this.timeAfterHits = 0;

            } else if (!hasShooted && this.timeAfterHits >= 60) {
                this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
                if (this.lastShoot == null || this.lastShoot.equals("blades")) {
                    swordThrowAttack();
                } else {
                    bladesAttack();
                }
            }

        }
    }

    public void swordThrowAttack() {
        if (!animationTriggered) {
            this.mob.setShootingSword(true);
            this.waitForAnimation = 0;
            this.animationTriggered = true;
        }
        this.waitForAnimation++;
        if (this.waitForAnimation == 25) {
            this.mob.setGronkStats("gronk_with_one_sword");
            this.shootSword();
            this.shootCooldown = 0;
        }

        if (this.waitForAnimation == 35) {
            this.mob.setGronkStats("gronk_with_no_sword");
            this.shootSword();
        }
        if (this.waitForAnimation == 45) {
            this.mob.setGronkStats("gronk");
            this.waitForAnimation = 0;
            this.animationTriggered = false;
            this.hasAttacked = false;
            this.lastShoot = "swords";
            this.hasShooted = true;
            this.timeWithoutAttack = 0;
        }
    }

    public void bladesAttack() {
        if (!animationTriggered) {
            this.mob.setShootingBlades(true);
            this.waitForAnimation = 0;
            this.animationTriggered = true;
        }
        this.waitForAnimation++;
        if (this.waitForAnimation == 10) {
            this.mob.getWorld().playSound(
                    null,
                    this.mob.getX(),
                    this.mob.getY(),
                    this.mob.getZ(),
                    ModSounds.GRONK_CHARGE,
                    SoundCategory.HOSTILE,
                    1.0f,
                    0.8f
            );
        }
        LivingEntity livingEntity = this.mob.getTarget();
        this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
        if (this.waitForAnimation == 40) {
            GronkBladeProjectileEntity.spawnBladeSpread(this.mob.getWorld(), this.mob, this.mob.getTarget().getPos());
            UsefulMethods.spawnDamagingParticles(1, this.mob);
            this.waitForAnimation = 0;
            this.animationTriggered = false;
            this.shootCooldown = 0;
            this.hasShooted = true;
            this.hasAttacked = false;
            this.timeWithoutAttack = 0;
            this.lastShoot = "blades";
        }
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

            double speed = 7;
            sword.setVelocity(
                    dX * speed,
                    dY * speed,
                    dZ * speed,
                    3.0f,
                    2.0f
            );

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
            this.timeWithoutAttack = 0;

            this.hasAttacked = true;
        }
    }

    protected void resetCooldown() { this.cooldown = this.getTickCount(20); }

    protected boolean isCooledDown() { return this.cooldown <= 0; }

    protected boolean canAttack(LivingEntity target) {
        return this.isCooledDown() && this.mob.isInAttackRange(target) && this.mob.getVisibilityCache().canSee(target);
    }

}