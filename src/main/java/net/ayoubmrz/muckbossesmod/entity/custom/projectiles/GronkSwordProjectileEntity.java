package net.ayoubmrz.muckbossesmod.entity.custom.projectiles;

import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.GronkEntity;
import net.ayoubmrz.muckbossesmod.item.ModItems;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class GronkSwordProjectileEntity extends PersistentProjectileEntity {
    private float rotation;
    public Vector2f groundedOffset;
    private final Set<Entity> hitEntities = new HashSet<>();
    private boolean hasHitPlayer = false;
    private int currentTick = 0;

    public GronkSwordProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public GronkSwordProjectileEntity(World world, GronkEntity mob) {
        super(ModEntities.GRONK_SWORD, world);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return ItemStack.EMPTY;
    }

    public boolean isGrounded() {
        return inGround;
    }

    @Override
    public boolean hasNoGravity() {
        return super.hasNoGravity();
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
        ++currentTick;
        if (currentTick == 3) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.SWORD_SPINNING, SoundCategory.NEUTRAL, 1.8f, 1.0f);
            currentTick = 0;
        }
        super.tick();
    }

    // Stop Sound on block hit
    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if(false) {
            super.playSound(sound, volume, pitch);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();

        if (hitEntities.contains(hitEntity)) {
            return;
        }

        hitEntities.add(hitEntity);

        if (hitEntity instanceof PlayerEntity player) {
            float damage = 14.0f;

            if (this.getOwner() instanceof GronkEntity gronk) {
                damage = gronk.swordThrow;
            }

            player.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);
            hasHitPlayer = true;

            if (!this.getWorld().isClient) {
                this.discard();
            }

        } else if (hitEntity instanceof LivingEntity livingEntity) {
            if (!this.getWorld().isClient) {
                ((ServerWorld) this.getWorld()).spawnParticles(
                        ParticleTypes.CRIT,
                        hitEntity.getX(), hitEntity.getY() + hitEntity.getHeight() / 2, hitEntity.getZ(),
                        5, 0.1, 0.1, 0.1, 0.1
                );
            }

        }
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);
        this.discard();
    }
}