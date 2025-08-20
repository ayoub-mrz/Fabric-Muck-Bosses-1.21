package net.ayoubmrz.muckbossesmod.entity.custom.projectiles;

import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class GronkBladeProjectileEntity extends PersistentProjectileEntity {
    private final Set<Entity> hitEntities = new HashSet<>();
    private boolean hasHitPlayer = false;
    private float damage = 20.0f;
    private Vec3d originalVelocity;
    private double targetSpeed = 0.8;

    public GronkBladeProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    public GronkBladeProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.GRONK_BLADE, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.setNoGravity(true);
    }

    @Override
    public void tick() {

        if (this.age > 400) {
            this.discard();
            return;
        }

        super.tick();

        if (this.originalVelocity != null) {
            this.setVelocity(this.originalVelocity);
        }

        this.age++;
    }

    public void move(MovementType movementType, Vec3d movement) {
        this.checkBlockCollision();
    }

    public void setStableVelocity(Vec3d velocity) {
        this.originalVelocity = velocity;
        this.targetSpeed = velocity.length();
        this.setVelocity(velocity);
    }

    public static void spawnBladeSpread(World world, LivingEntity thrower) {
        if (world.isClient) return;

        float baseYaw = thrower.getYaw();
        float basePitch = thrower.getPitch();
        double offsetY;
        if (thrower instanceof PlayerEntity player) {
            offsetY = 1.5;
        } else {
            offsetY = 3;
        }

        double yawRadians = Math.toRadians(baseYaw);
        double pitchRadians = Math.toRadians(basePitch);

        double sideX = -Math.sin(yawRadians + Math.PI / 2);
        double sideZ = Math.cos(yawRadians + Math.PI / 2);

        for (int i = 0; i < 5; i++) {
            GronkBladeProjectileEntity blade = new GronkBladeProjectileEntity(world, thrower);

            double sideOffset = (i - 2) * 2.0;

            double finalX = thrower.getX() + (sideX * sideOffset);
            double finalY = thrower.getEyeY() - offsetY;
            double finalZ = thrower.getZ() + (sideZ * sideOffset);

            blade.setPosition(finalX, finalY, finalZ);

            float angleOffset = (i - 2) * 15.0f;

            float bladeYaw = baseYaw + angleOffset;
            double bladeYawRadians = Math.toRadians(bladeYaw);

            double bladeForwardX = -Math.sin(bladeYawRadians) * Math.cos(pitchRadians);
            double bladeForwardZ = Math.cos(bladeYawRadians) * Math.cos(pitchRadians);

            double speed = 0.8;
            Vec3d velocity = new Vec3d(bladeForwardX * speed, 0, bladeForwardZ * speed);
            blade.setStableVelocity(velocity);

            world.spawnEntity(blade);
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.GRONK_SWORD);
    }

    public boolean isGrounded() {
        return inGround;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();

        // Prevent hitting the same entity multiple times
        if (hitEntities.contains(hitEntity)) {
            return;
        }

        hitEntities.add(hitEntity);

        if (hitEntity instanceof PlayerEntity player) {
            player.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);
            hasHitPlayer = true;

            // Play hit sound
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS,
                    0.8f, 1.0f);

            if (!this.getWorld().isClient) {
                this.discard();
            }

        } else if (hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);

            // Add knockback effect
            Vec3d knockback = this.getVelocity().normalize().multiply(0.3);
            livingEntity.addVelocity(knockback.x, 0.2, knockback.z);
            livingEntity.velocityModified = true;

            if (!this.getWorld().isClient) {
                ((ServerWorld) this.getWorld()).spawnParticles(
                        ParticleTypes.CRIT,
                        hitEntity.getX(), hitEntity.getY() + hitEntity.getHeight() / 2, hitEntity.getZ(),
                        5, 0.1, 0.1, 0.1, 0.1
                );
            }

            // Play hit sound
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_ARROW_HIT, SoundCategory.NEUTRAL,
                    1.0f, 1.0f);
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(ModItems.GRONK_SWORD);
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);
        this.noClip = true;

//        // Play block hit sound
//        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
//                SoundEvents.ENTITY_ARROW_HIT, SoundCategory.BLOCKS,
//                1.0f, 0.8f);
//        this.discard();
    }
}