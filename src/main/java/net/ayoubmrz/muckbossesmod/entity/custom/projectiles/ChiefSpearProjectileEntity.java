package net.ayoubmrz.muckbossesmod.entity.custom.projectiles;

import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.entity.custom.bosses.ChiefEntity;
import net.ayoubmrz.muckbossesmod.entity.custom.UsefulMethods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class ChiefSpearProjectileEntity extends PersistentProjectileEntity {
    private final Set<Entity> hitEntities = new HashSet<>();
    private boolean hasHitPlayer = false;

    public ChiefSpearProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public ChiefSpearProjectileEntity(World world, ChiefEntity mob) {
        super(ModEntities.CHIEF_SPEAR, world);
        this.setOwner(mob);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age > 200 && this.getWorld().isClient) {
            this.discard();
        }
        ++this.age;
    }

    @Override
    protected ItemStack getDefaultItemStack() { return new ItemStack(Items.STICK); }

    public boolean isGrounded() { return inGround; }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if(false) {
            super.playSound(sound, volume, pitch);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();

        if (hitEntities.contains(hitEntity) || this.getOwner() == hitEntity) {
            return;
        }
        if (!(hitEntity instanceof PlayerEntity player) && hitEntity.getClass() == this.getOwner().getClass()) {
            return ;
        }

        hitEntities.add(hitEntity);

        if (hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.damage(this.getDamageSources().thrown(this, this.getOwner()), 6.0f);
            hasHitPlayer = true;

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.NEUTRAL, 0.2f, 0.2f);

            UsefulMethods.applyKnockback(hitEntity, this, 0.4f, 4.0f);

            if (!this.getWorld().isClient) {
                this.discard();
            }

        }
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.NEUTRAL, 0.2f, 0.2f);

        if (!this.getWorld().isClient) {
            this.discard();
        }
    }
}