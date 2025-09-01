package net.ayoubmrz.muckbossesmod.entity.custom.bosses;

import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals.BigChunkMeleeAttackGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
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


public class BigChunkEntity extends HostileEntity implements GeoEntity, BaseValues {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Boolean> IS_CLUB_SWING = DataTracker.registerData(BigChunkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> IS_SHOOTING_ROCKS = DataTracker.registerData(BigChunkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final int playersCount = this.getWorld().getPlayers().size();
    private final int numberOfDays = (int) (this.getWorld().getTimeOfDay() / 24000L) + 1;
    private float dayMultiplier = (float) Math.pow(DAMAGE_MULTIPLIER, numberOfDays - 1);

    public float clubHit = BASE_CLUB_HIT * dayMultiplier;
    public float clubSwing = BASE_CLUB_SWING * dayMultiplier;
    public float rocksSpread = BASE_ROCKS_SPREAD * dayMultiplier;

    private int shootingTicks = 0;
    private int stepSoundTicks = 0;
    private boolean hasInitializedHealth = false;



    private final ServerBossBar bossBar = new ServerBossBar(Text.literal("Big Chunk"),
            BossBar.Color.PURPLE, BossBar.Style.PROGRESS);

    public BigChunkEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasInitializedHealth) {
            initializeDynamicHealth();
            hasInitializedHealth = true;
        }

        if (isShootingRocks()) {
            shootingTicks++;
            if (shootingTicks > 5) {
                setShootingRocks(false);
                shootingTicks = 0;
            }
        }

        Vec3d velocity = this.getVelocity();
        boolean isMoving = velocity.horizontalLength() > 0.01;


        if (isMoving && this.isOnGround()) {
            stepSoundTicks++;
            if (stepSoundTicks >= 20) {
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

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_CLUB_SWING, false);
        builder.add(IS_SHOOTING_ROCKS, false);
    }

    public boolean isShootingRocks() { return this.dataTracker.get(IS_SHOOTING_ROCKS); }

    public void setShootingRocks(boolean attack) { this.dataTracker.set(IS_SHOOTING_ROCKS, attack); }

    public boolean isClubSwing() { return this.dataTracker.get(IS_CLUB_SWING); }

    public void setClubSwing(boolean shooting) { this.dataTracker.set(IS_CLUB_SWING, shooting); }

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
        this.goalSelector.add(1, new BigChunkMeleeAttackGoal(this, 0.3F, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.4f, 1));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "attackController", 5, this::attackPredicate));
        controllers.add(new AnimationController<>(this, "swingAttackController", 5, this::swingAttackPredicate));
        controllers.add(new AnimationController<>(this, "shootController", 5, this::shootPredicate));
    }

    private PlayState swingAttackPredicate(AnimationState<BigChunkEntity> event) {

        if (this.isClubSwing()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.big_chunk.swing", Animation.LoopType.PLAY_ONCE)
            );
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState shootPredicate(AnimationState<BigChunkEntity> event) {

        if (this.isShootingRocks()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.big_chunk.jump", Animation.LoopType.PLAY_ONCE)
            );
            setShootingRocks(false);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState<BigChunkEntity> event) {
        if (this.handSwinging) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.big_chunk.attack", Animation.LoopType.PLAY_ONCE)
            );
            this.handSwinging = false;
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState<BigChunkEntity> animationState) {
        var controller = animationState.getController();

        if (animationState.isMoving() && !this.isShootingRocks()) {
            controller.setAnimation(RawAnimation.begin().then("animation.big_chunk.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        controller.setAnimation(RawAnimation.begin().then("animation.big_chunk.idle", Animation.LoopType.LOOP));
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