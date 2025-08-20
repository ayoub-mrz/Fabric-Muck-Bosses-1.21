package net.ayoubmrz.muckbossesmod.entity.custom.bosses;

import net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals.GronkMeleeAttackGoal;
import net.ayoubmrz.muckbossesmod.item.ModItems;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;


public class GronkEntity extends HostileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Boolean> IS_SHOOTING = DataTracker.registerData(GronkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> IS_SHOOTINGBLADES = DataTracker.registerData(GronkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private boolean isAttackWindingUp = false;
    private int windupTicks = 0;
    private int shootingTicks = 0;

    private final ServerBossBar bossBar = new ServerBossBar(Text.literal("Gronk"),
            BossBar.Color.PURPLE, BossBar.Style.PROGRESS);

    public GronkEntity(EntityType<? extends HostileEntity> entityType, World world) {
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

        if (isShooting()) {
            shootingTicks++;
            if (shootingTicks > 5) {
                setShooting(false);
                shootingTicks = 0;
            }
        }

        if (isShooting()) {
            shootingTicks++;
            if (shootingTicks > 5) {
                setShooting(false);
                shootingTicks = 0;
            }
        }
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
        builder.add(IS_SHOOTING, false);
        builder.add(IS_SHOOTINGBLADES, false);
    }

    public boolean isShooting() {
        return this.dataTracker.get(IS_SHOOTING);
    }

    public void setShooting(boolean shooting) {
        this.dataTracker.set(IS_SHOOTING, shooting);
    }

    public boolean isShootingBlades() {
        return this.dataTracker.get(IS_SHOOTINGBLADES);
    }

    public void setShootingBlades(boolean shooting) {
        this.dataTracker.set(IS_SHOOTINGBLADES, shooting);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0F)
//                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, (double)0.4F)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0F);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new GronkMeleeAttackGoal(this, 0.8F, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.4));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "attackController", 5, this::attackPredicate));
        controllers.add(new AnimationController<>(this, "shootController", 5, this::shootPredicate));
    }

    private PlayState shootPredicate(AnimationState<GronkEntity> event) {

        if (this.isShooting()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.gronk.shoot", Animation.LoopType.PLAY_ONCE)
            );
            setShooting(false);
            return PlayState.CONTINUE;
        }
        if (this.isShootingBlades()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(
                    RawAnimation.begin().then("animation.gronk.shoot2", Animation.LoopType.PLAY_ONCE)
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

        if (animationState.isMoving() && !this.isShooting()) {
            double speed = this.getVelocity().horizontalLength();
            controller.setAnimation(RawAnimation.begin().then("animation.gronk.run", Animation.LoopType.LOOP));

//            if (speed > 0.2) {
//                controller.setAnimation(RawAnimation.begin().then("animation.gronk.run", Animation.LoopType.LOOP));
//            } else {
//                controller.setAnimation(RawAnimation.begin().then("animation.gronk.walk", Animation.LoopType.LOOP));
//            }
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