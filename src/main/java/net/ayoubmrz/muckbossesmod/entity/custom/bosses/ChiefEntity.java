package net.ayoubmrz.muckbossesmod.entity.custom.bosses;

import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals.ChiefMeleeAttackGoal;
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


public class ChiefEntity extends HostileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Boolean> IS_SHOOTING_SPEAR = DataTracker.registerData(ChiefEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<String> CHIEF_STATS = DataTracker.registerData(ChiefEntity.class, TrackedDataHandlerRegistry.STRING);

    private static final TrackedData<Boolean> IS_SPIN_ATTACK = DataTracker.registerData(ChiefEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> IS_JUMP_ATTACK = DataTracker.registerData(ChiefEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private boolean isAttackWindingUp = false;
    private int windupTicks = 0;
    private int shootingTicks = 0;
    private int stepSoundTicks = 0;

    private final ServerBossBar bossBar = new ServerBossBar(Text.literal("Chief"),
            BossBar.Color.PURPLE, BossBar.Style.PROGRESS);

    public ChiefEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (isAttackWindingUp) {
            windupTicks--;

            if (windupTicks <= 0) {
                performAttack();
                isAttackWindingUp = false;
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

        if (isShootingSpear()) {
            shootingTicks++;
            if (shootingTicks > 5) {
                setShootingSpear(false);
                shootingTicks = 0;
            }
        }
    }

    public void startAttackWindup() {
        this.isAttackWindingUp = true;
        this.windupTicks = 15;
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

        if (target != null && this.distanceTo(target) > 5.0f) {
            return false;
        }

        boolean attackSuccessful = super.tryAttack(target);

        if (attackSuccessful) {
            UsefulMethods.applyKnockback(target, this, 0.2f, 1.4f);
        }

        return attackSuccessful;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_SHOOTING_SPEAR, false);
        builder.add(IS_SPIN_ATTACK, false);
        builder.add(CHIEF_STATS, "chief_with_spear");
        builder.add(IS_JUMP_ATTACK, false);
    }

    public boolean isShootingSpear() { return this.dataTracker.get(IS_SHOOTING_SPEAR); }

    public void setShootingSpear(boolean shooting) { this.dataTracker.set(IS_SHOOTING_SPEAR, shooting); }

    public String isChiefStats() { return this.dataTracker.get(CHIEF_STATS); }

    public void setChiefStats(String stats) { this.dataTracker.set(CHIEF_STATS, stats); }

    public boolean isSpinAttack() { return this.dataTracker.get(IS_SPIN_ATTACK); }

    public void setSpinAttack(boolean spin) { this.dataTracker.set(IS_SPIN_ATTACK, spin); }

    public boolean isJumpAttack() { return this.dataTracker.get(IS_JUMP_ATTACK); }

    public void setJumpAttack(boolean jumping) { this.dataTracker.set(IS_JUMP_ATTACK, jumping); }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0F)
                .add(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, 0.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ChiefMeleeAttackGoal(this, 0.8f, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.6f, 1));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 3, this::predicate));
        controllers.add(new AnimationController<>(this, "attackController", 3, this::attackPredicate));
        controllers.add(new AnimationController<>(this, "shootController", 3, this::shootPredicate));
        controllers.add(new AnimationController<>(this, "spinAttackController", 0, this::spinAttackPredicate));
        controllers.add(new AnimationController<>(this, "jumpAttackController", 0, this::jumpAttackPredicate));
    }

    private PlayState shootPredicate(AnimationState<ChiefEntity> event) {

        if (this.isShootingSpear()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.chief.spear_throw", Animation.LoopType.PLAY_ONCE)
            );
            setShootingSpear(false);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState jumpAttackPredicate(AnimationState<ChiefEntity> event) {

        if (this.isJumpAttack()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.chief.jump_attack", Animation.LoopType.PLAY_ONCE)
            );
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState spinAttackPredicate(AnimationState<ChiefEntity> event) {

        if (this.isSpinAttack()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.chief.spin", Animation.LoopType.PLAY_ONCE)
            );
//            setSpinAttack(false);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState<ChiefEntity> event) {
        if (this.handSwinging) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.chief.attack", Animation.LoopType.PLAY_ONCE)
            );
            this.handSwinging = false;
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState<ChiefEntity> animationState) {
        var controller = animationState.getController();

        if (animationState.isMoving() && !this.isShootingSpear()) {
            controller.setAnimation(RawAnimation.begin().then("animation.chief.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        controller.setAnimation(RawAnimation.begin().then("animation.chief.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

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