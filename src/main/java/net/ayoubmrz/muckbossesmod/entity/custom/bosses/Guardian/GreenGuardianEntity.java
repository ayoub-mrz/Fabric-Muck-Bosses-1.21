package net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian;

import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.BaseValues;
import net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals.GuardianMeleeAttackGoal;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;


public class GreenGuardianEntity extends HostileEntity implements GeoEntity, GuardianEntity, BaseValues {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Boolean> IS_SHOOTING_LAZER = DataTracker.registerData(GreenGuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> IS_SHOOTING_LIGHTNING = DataTracker.registerData(GreenGuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> IS_LASER_SOUND_START = DataTracker.registerData(GreenGuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final int playersCount = this.getWorld().getPlayers().size();
    private final int numberOfDays = (int) (this.getWorld().getTimeOfDay() / 24000L) + 1;
    public float dayMultiplier = (float) Math.pow(DAMAGE_MULTIPLIER, numberOfDays - 1);

    public float slap = BASE_SLAP * dayMultiplier;
    public float laser = BASE_LASER * dayMultiplier;
    public float lightning = BASE_LIGHTNING * dayMultiplier;

    private boolean isAttackWindingUp = false;
    private int windupTicks = 0;
    private int shootingTicks = 0;
    private boolean isSecondAttackPending = false;
    private boolean isSecondAttackWindingUp = false;
    private int secondAttackWindupTicks = 0;
    private boolean soundStart = false;
    private int soundTicks = 0;
    private float soundVolum = 3.0f;
    private float soundPitch = 0.6f;
    private boolean hasInitializedHealth = false;

    private final ServerBossBar bossBar = new ServerBossBar(Text.literal("Green Guardian"),
            BossBar.Color.PURPLE, BossBar.Style.PROGRESS);

    public GreenGuardianEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public float getLaser() {
        return laser;
    }

    @Override
    public float getLightning() {
        return lightning;
    }

    @Override
    public void tick() {
        ++soundTicks;
        super.tick();

        if (!hasInitializedHealth) {
            initializeDynamicHealth();
            hasInitializedHealth = true;
        }

        if (isAttackWindingUp) {
            windupTicks--;
            if (windupTicks <= 0) {
                performFirstAttack();
                isAttackWindingUp = false;
                isSecondAttackPending = true;
                isSecondAttackWindingUp = true;
                secondAttackWindupTicks = 16;
            }
        }

        if (isSecondAttackWindingUp) {
            secondAttackWindupTicks--;
            if (secondAttackWindupTicks <= 0) {
                performSecondAttack();
                isSecondAttackWindingUp = false;
                isSecondAttackPending = false;
            }
        }

        if (isShootingLazer()) {
            shootingTicks++;
            if (shootingTicks > 5) {
                setShootingLazer(false);
                shootingTicks = 0;
            }
        }

        if (isLazerSoundStart()) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.LASER_CHARGE_UP, SoundCategory.HOSTILE, 5f, 0.8f);
            setLazerSoundStart(false);
        }

        if (this.isAlive()) {
            if (!isLazerSoundStart() && soundStart && soundTicks == 25) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.GUARDIAN_AMBIENT_2, SoundCategory.HOSTILE, soundVolum, soundPitch);
            } else if (!isLazerSoundStart() && soundStart && soundTicks == 50) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.GUARDIAN_AMBIENT_4, SoundCategory.HOSTILE, soundVolum, soundPitch);
            } else if (!isLazerSoundStart() && soundStart && soundTicks == 75) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.GUARDIAN_AMBIENT_3, SoundCategory.HOSTILE, soundVolum, soundPitch);
            } else if (!isLazerSoundStart() && soundStart && soundTicks == 100) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.GUARDIAN_AMBIENT_1, SoundCategory.HOSTILE, soundVolum, soundPitch);
                this.soundTicks = 0;
            } else if (!soundStart) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.GUARDIAN_AMBIENT_3, SoundCategory.HOSTILE, soundVolum, soundPitch);
                this.soundStart = true;
            }
        }

    }

    private void initializeDynamicHealth() {
        // Each player adds 10% more health
        float playerMultiplier = 1.0f + (playersCount * 0.1f);

        // Each day adds 12% more health
        float dayMultiplier = 1.0f + ((numberOfDays - 1) * 0.12f);

        float finalHealth = baseHealth * playerMultiplier * dayMultiplier;
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(finalHealth);
        this.setHealth(finalHealth);

        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(slap);
    }

    public void startAttackWindup() {
        this.isAttackWindingUp = true;
        this.windupTicks = 20;
    }

    private void performFirstAttack() {
        LivingEntity target = this.getTarget();
        if (this.isAlive() && target != null && this.canSee(target)) {
            this.executeAttack(target);
        }
    }

    private void performSecondAttack() {
        LivingEntity target = this.getTarget();
        if (this.isAlive() && target != null && this.canSee(target)) {
            this.executeAttack(target);
        }
    }

    private void executeAttack(Entity target) {
        if (target != null && this.distanceTo(target) <= 4.0f) {
            boolean attackSuccessful = super.tryAttack(target);

            if (attackSuccessful) {
                UsefulMethods.applyKnockback(target, this, 0.2f, 1.4f);
            }
        }
        this.getWorld().playSound(
                null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_WARDEN_DEATH, SoundCategory.HOSTILE, 2.0f, 2.4f
        );
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (isAttackWindingUp || isSecondAttackWindingUp || isSecondAttackPending) {
            return false;
        }

        if (target != null && this.distanceTo(target) > 3.0f) {
            return false;
        }

        startAttackWindup();
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource.isOf(DamageTypes.IN_FIRE) ||
                damageSource.isOf(DamageTypes.ON_FIRE) ||
                damageSource.isOf(DamageTypes.LAVA) ||
                damageSource.isOf(DamageTypes.HOT_FLOOR)) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_SHOOTING_LAZER, false);
        builder.add(IS_SHOOTING_LIGHTNING, false);
        builder.add(IS_LASER_SOUND_START, false);
    }

    public boolean isShootingLazer() { return this.dataTracker.get(IS_SHOOTING_LAZER); }

    @Override
    public void setShootingLazer(boolean shooting) { this.dataTracker.set(IS_SHOOTING_LAZER, shooting); }

    public boolean isShootingLightning() { return this.dataTracker.get(IS_SHOOTING_LIGHTNING); }

    @Override
    public void setShootingLightning(boolean shooting) { this.dataTracker.set(IS_SHOOTING_LIGHTNING, shooting); }

    public boolean isLazerSoundStart() { return this.dataTracker.get(IS_LASER_SOUND_START); }

    @Override
    public void setLazerSoundStart(boolean shooting) { this.dataTracker.set(IS_LASER_SOUND_START, shooting); }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0F)
                .add(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, 0.0F)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new GuardianMeleeAttackGoal<>(this, 0.8f, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.6f, 1));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "attackController", 5, this::attackPredicate));
        controllers.add(new AnimationController<>(this, "shootLazerController", 5, this::shootLazerPredicate));
        controllers.add(new AnimationController<>(this, "shootLightningController", 5, this::shootLightningPredicate));
    }

    private PlayState shootLightningPredicate(AnimationState<GreenGuardianEntity> event) {

        if (this.isShootingLightning()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.guardian.lightning", Animation.LoopType.PLAY_ONCE)
            );
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState shootLazerPredicate(AnimationState<GreenGuardianEntity> event) {

        if (this.isShootingLazer()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.guardian.lazer", Animation.LoopType.PLAY_ONCE)
            );
            setShootingLazer(false);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState<GreenGuardianEntity> event) {
        if (this.handSwinging) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.guardian.attack", Animation.LoopType.PLAY_ONCE)
            );
            this.handSwinging = false;
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState<GreenGuardianEntity> animationState) {
        var controller = animationState.getController();

        if (animationState.isMoving() && !this.isShootingLazer() && !this.isShootingLightning()) {
            controller.setAnimation(RawAnimation.begin().then("animation.guardian.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        controller.setAnimation(RawAnimation.begin().then("animation.guardian.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

}