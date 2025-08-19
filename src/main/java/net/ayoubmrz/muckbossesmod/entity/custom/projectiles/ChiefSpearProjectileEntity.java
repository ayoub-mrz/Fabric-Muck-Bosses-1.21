package net.ayoubmrz.muckbossesmod.entity.custom.projectiles;

import net.ayoubmrz.muckbossesmod.item.ModItems;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class ChiefSpearProjectileEntity extends PersistentProjectileEntity {
    private float rotation;
    public Vector2f groundedOffset;
    private final Set<Entity> hitEntities = new HashSet<>();
    private boolean hasHitPlayer = false;

    public ChiefSpearProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

//    public BoneProjectileEntity(World world, GoblinEntity mob) {
//        super(ModEntities.ANCIENTBONE, world);
//    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.CHIEF_SPEAR);
    }

    public boolean isGrounded() {
        return inGround;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();

        if (hitEntities.contains(hitEntity)) {
            return;
        }

        hitEntities.add(hitEntity);

        if (hitEntity instanceof PlayerEntity player) {
            player.damage(this.getDamageSources().thrown(this, this.getOwner()), 6.0f);
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
    protected ItemStack asItemStack() {
        return new ItemStack(ModItems.CHIEF_SPEAR);
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);
        Direction side = result.getSide();

        if(side == Direction.SOUTH) {
            groundedOffset = new Vector2f(215f,180f);
        }
        else if(side == Direction.NORTH) {
            groundedOffset = new Vector2f(215f, 0f);
        }
        else if(side == Direction.EAST) {
            groundedOffset = new Vector2f(215f,-90f);
        }
        else if(side == Direction.WEST) {
            groundedOffset = new Vector2f(215f,90f);
        }
        else if(side == Direction.DOWN) {
            groundedOffset = new Vector2f(115f,180f);
        }
        else if(side == Direction.UP) {
            if (groundedOffset == null) {
                System.out.println(this.getFacing());
                switch(this.getFacing()) {
                    case NORTH:
                        groundedOffset = new Vector2f(285f,0f);
                        break;
                    case SOUTH:
                        groundedOffset = new Vector2f(285f,180f);
                        break;
                    case WEST:
                        groundedOffset = new Vector2f(285f,-90f);
                        break;
                    case EAST:
                        groundedOffset = new Vector2f(285f,90f);
                        break;
                    default:
                        groundedOffset = new Vector2f(285f,180f);
                        break;
                }
            }
        }
    }
}