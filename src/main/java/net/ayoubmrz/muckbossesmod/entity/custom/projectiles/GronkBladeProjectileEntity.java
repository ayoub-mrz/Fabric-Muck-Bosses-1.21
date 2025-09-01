package net.ayoubmrz.muckbossesmod.entity.custom.projectiles;

import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.GronkEntity;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class GronkBladeProjectileEntity extends PersistentProjectileEntity {
    private final Set<Entity> hitEntities = new HashSet<>();
    private boolean hasHitPlayer = false;
    private Vec3d originalVelocity;
    private double targetSpeed = 0.8;
    private boolean hasHitBlock = false;

    public GronkBladeProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    public GronkBladeProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.GRONK_BLADE, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.setNoGravity(true);
        this.getWorld().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                SoundCategory.HOSTILE,
                1.0f,
                0.8f
        );
    }

    @Override
    public void tick() {

        if (this.age > 200) {
            this.discard();
            return;
        }

        super.tick();

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.BLADE_MOVE, SoundCategory.NEUTRAL, 4f, 0.2f);

        if (this.originalVelocity != null) {
            this.setVelocity(this.originalVelocity);
        }
        this.age++;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        BlockPos hitPos = blockHitResult.getBlockPos();
        World world = this.getWorld();

        if (isBlockOneTallObstacle(hitPos)) {
            hasHitBlock = false;
            this.inGround = false;

            this.setPosition(this.getX(), hitPos.getY() + 1.5, this.getZ());

            if (this.originalVelocity != null) {
                this.setVelocity(this.originalVelocity);
            }

        } else {
            // Create explosion for tall obstacles
            if (!world.isClient) {
                world.createExplosion(
                        this,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        3.4f,
                        false,
                        World.ExplosionSourceType.MOB
                );
            }

            this.discard();
        }
    }

    private boolean isBlockOneTallObstacle(BlockPos hitPos) {
        World world = this.getWorld();

        if (!world.getBlockState(hitPos).isSolidBlock(world, hitPos)) {
            return false;
        }

        BlockPos abovePos = hitPos.up();
        return world.getBlockState(abovePos).isAir();
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if(false) {
            super.playSound(sound, volume, pitch);
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() { return new ItemStack(Items.STICK); }

    public boolean isGrounded() { return inGround; }

    public void setStableVelocity(Vec3d velocity) {
        this.originalVelocity = velocity;
        this.targetSpeed = velocity.length();
        this.setVelocity(velocity);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();

        if (!(hitEntity instanceof PlayerEntity player) && hitEntity.getClass() == this.getOwner().getClass()) {
            return ;
        }

        // Prevent hitting the same entity multiple times
        if (hitEntities.contains(hitEntity)) {
            return;
        }

        hitEntities.add(hitEntity);

        if (hitEntity instanceof LivingEntity livingEntity) {
            float damage = 14.0f;

            if (this.getOwner() instanceof GronkEntity gronk) {
                damage = gronk.bladeSpread;
            }
            livingEntity.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);

            UsefulMethods.applyKnockback(hitEntity, this, 0.8f, 4.5f);

        }

        if (!this.getWorld().isClient) {
            this.discard();
        }

    }

}