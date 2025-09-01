package net.ayoubmrz.muckbossesmod.entity.custom.projectiles;

import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.BigChunkEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.ChiefEntity;
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
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class ChunkyRockProjectileEntity extends PersistentProjectileEntity {
    private float rotation;
    private final Set<Entity> hitEntities = new HashSet<>();
    private boolean hasHitPlayer = false;
    private float damage = 20.0f;
    private Vec3d originalVelocity;
    private double targetSpeed = 0.8;
    private int soundTick = 0;
    private boolean hasHitBlock = false;

    public ChunkyRockProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    public ChunkyRockProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.CHUNKY_ROCK, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.setNoGravity(true);
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    @Override
    public void tick() {
        this.soundTick++;
        this.age++;

        if (this.age > 400) {
            this.discard();
            return;
        }
        super.tick();

        if (soundTick == 2) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_ANCIENT_DEBRIS_BREAK, SoundCategory.NEUTRAL, 1.4f, 0.3f);
            if (this.getWorld().isClient) {

                BlockPos belowPos = this.getBlockPos().down();
                BlockState blockBelow = this.getWorld().getBlockState(belowPos);

                if (!blockBelow.isAir()) {
                    for(int i=0; i < 10; i++) {
                        this.getWorld().addParticle(
                                new BlockStateParticleEffect(ParticleTypes.BLOCK, blockBelow),
                                this.getBlockPos().getX() + 0.5, this.getBlockPos().getY(), this.getBlockPos().getZ() + 0.5,
                                (this.random.nextFloat() - 0.5) * 0.5 + i,
                                0.1,
                                (this.random.nextFloat() - 0.5) * 0.5 + i
                        );
                    }
                }

            }
            soundTick = 0;
        }

        if (this.originalVelocity != null) {
            this.setVelocity(this.originalVelocity);
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
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if(false) {
            super.playSound(sound, volume, pitch);
        }
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

            if (this.getOwner() instanceof BigChunkEntity bigChunk) {
                damage = bigChunk.rocksSpread;
            }
            livingEntity.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);

            UsefulMethods.applyKnockback(hitEntity, this, 0.8f, 4.5f);

        }

        if (!this.getWorld().isClient) {
            this.discard();
        }

    }

}