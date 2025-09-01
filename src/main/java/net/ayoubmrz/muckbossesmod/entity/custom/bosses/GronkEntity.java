package net.ayoubmrz.muckbossesmod.entity.custom.bosses;

import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.ayoubmrz.muckbossesmod.entity.ai.customAttackGoals.GronkMeleeAttackGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;


public class GronkEntity extends HostileEntity implements GeoEntity, BaseValues {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Boolean> IS_SHOOTING_SWORD = DataTracker.registerData(GronkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> IS_SHOOTING_BLADES = DataTracker.registerData(GronkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<String> GRONK_STATS = DataTracker.registerData(GronkEntity.class, TrackedDataHandlerRegistry.STRING);

    private final int playersCount = this.getWorld().getPlayers().size();
    private final int numberOfDays = (int) (this.getWorld().getTimeOfDay() / 24000L) + 1;
    private float dayMultiplier = (float) Math.pow(DAMAGE_MULTIPLIER, numberOfDays - 1);

    public float bladeHits = BASE_BLADE_HITS * dayMultiplier;
    public float swordThrow = BASE_SWORD_THROW * dayMultiplier;
    public float bladeSpread = BASE_BLADES_SPREAD * dayMultiplier;

    private boolean isAttackWindingUp = false;
    private int windupTicks = 0;
    private int shootingTicks = 0;
    private int stepSoundTicks = 0;
    private boolean hasInitializedHealth = false;

    private final ServerBossBar bossBar = new ServerBossBar(Text.literal("Gronk"),
            BossBar.Color.PURPLE, BossBar.Style.PROGRESS);

    public GronkEntity(EntityType<? extends HostileEntity> entityType, World world) { super(entityType, world); }

    @Override
    public void tick() {
        super.tick();

        if (!hasInitializedHealth) {
            initializeDynamicHealth();
            hasInitializedHealth = true;
        }

        if (isAttackWindingUp) {
            windupTicks--;

            if (windupTicks <= 0) {
                performAttack();
                isAttackWindingUp = false;
            }
        }

        if (isShootingSword()) {
            shootingTicks++;
            if (shootingTicks > 5) {
                setShootingSword(false);
                shootingTicks = 0;
            }
        }

        if (isShootingBlades()) {
            shootingTicks++;
            if (shootingTicks > 5) {
                setShootingBlades(false);
                shootingTicks = 0;
            }
        }

        Vec3d velocity = this.getVelocity();
        boolean isMoving = velocity.horizontalLength() > 0.01;

        if (isMoving && this.isOnGround()) {
            stepSoundTicks++;
            if (stepSoundTicks >= 10) {
                UsefulMethods.playStepSound(this);
                stepSoundTicks = 0;
            }
        } else {
            stepSoundTicks = 0;
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
    }

    public void startAttackWindup() {
        this.isAttackWindingUp = true;
        this.windupTicks = 10;
    }

    private void performAttack() {
        LivingEntity target = this.getTarget();
        if (this.isAlive() && target != null && this.canSee(target)) {
            this.tryAttack(target);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (!isAttackWindingUp) {
            startAttackWindup();
            return false;
        }
        return super.tryAttack(target);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_SHOOTING_SWORD, false);
        builder.add(IS_SHOOTING_BLADES, false);
        builder.add(GRONK_STATS, "gronk");
    }

    public boolean isShootingSword() { return this.dataTracker.get(IS_SHOOTING_SWORD); }

    public void setShootingSword(boolean shooting) { this.dataTracker.set(IS_SHOOTING_SWORD, shooting); }

    public boolean isShootingBlades() { return this.dataTracker.get(IS_SHOOTING_BLADES); }

    public void setShootingBlades(boolean shooting) { this.dataTracker.set(IS_SHOOTING_BLADES, shooting); }

    public String getGronkStats() { return this.dataTracker.get(GRONK_STATS); }

    public void setGronkStats(String stats) { this.dataTracker.set(GRONK_STATS, stats); }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0F)
                .add(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, 0.0F)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0F);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new GronkMeleeAttackGoal(this, 0.8F, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.4));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "attackController", 5, this::attackPredicate));
        controllers.add(new AnimationController<>(this, "shootSwordController", 5, this::shootSwordPredicate));
        controllers.add(new AnimationController<>(this, "shootBladeController", 5, this::shootBladePredicate));
    }

    private PlayState shootSwordPredicate(AnimationState<GronkEntity> event) {

        if (this.isShootingSword()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.gronk.shootSword", Animation.LoopType.PLAY_ONCE)
            );
            setShootingSword(false);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState shootBladePredicate(AnimationState<GronkEntity> event) {

        if (this.isShootingBlades()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.gronk.shootBlades", Animation.LoopType.PLAY_ONCE)
            );
            setShootingBlades(false);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState<GronkEntity> event) {
        if (this.handSwinging) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.gronk.attack", Animation.LoopType.PLAY_ONCE)
            );
            this.handSwinging = false;
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState animationState) {
        var controller = animationState.getController();

        if (animationState.isMoving() && !this.isShootingSword() && !this.isShootingBlades()) {
            controller.setAnimation(RawAnimation.begin().then("animation.gronk.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        controller.setAnimation(RawAnimation.begin().then("animation.gronk.idle", Animation.LoopType.LOOP));
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