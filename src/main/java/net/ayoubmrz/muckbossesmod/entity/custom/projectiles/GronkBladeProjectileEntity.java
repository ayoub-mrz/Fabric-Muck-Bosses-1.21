package net.ayoubmrz.muckbossesmod.entity.custom.projectiles;

import net.ayoubmrz.muckbossesmod.entity.ModEntities;
import net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals.GronkMeleeAttackGoal;
import net.ayoubmrz.muckbossesmod.entity.custom.customAttackGoals.UsefulMethods;
import net.ayoubmrz.muckbossesmod.item.ModItems;
import net.ayoubmrz.muckbossesmod.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

        if (isInsideBlock() || willHitBlock()) {
            this.noClip = true;
        } else {
            this.noClip = false;
        }

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.BLADE_MOVE, SoundCategory.NEUTRAL, 0.4f, 0.2f);

        if (this.originalVelocity != null) {
            this.setVelocity(this.originalVelocity);
        }
        this.age++;
    }

    private boolean isInsideBlock() {
        Box entityBox = this.getBoundingBox();
        return hasCollisionInBox(entityBox);
    }

    private boolean willHitBlock() {
        if (this.originalVelocity == null) return false;

        Box currentBox = this.getBoundingBox();
        Box nextBox = currentBox.offset(this.originalVelocity);

        return hasCollisionInBox(nextBox);
    }

    private boolean hasCollisionInBox(Box box) {
        World world = this.getWorld();

        int minX = (int) Math.floor(box.minX);
        int minY = (int) Math.floor(box.minY);
        int minZ = (int) Math.floor(box.minZ);
        int maxX = (int) Math.floor(box.maxX);
        int maxY = (int) Math.floor(box.maxY);
        int maxZ = (int) Math.floor(box.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState blockState = world.getBlockState(pos);

                    if (blockState.isAir()) {
                        continue;
                    }

                    VoxelShape collisionShape = blockState.getCollisionShape(world, pos);
                    if (!collisionShape.isEmpty()) {
                        VoxelShape offsetShape = collisionShape.offset(x, y, z);

                        for (Box collisionBox : offsetShape.getBoundingBoxes()) {
                            if (box.intersects(collisionBox)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setStableVelocity(Vec3d velocity) {
        this.originalVelocity = velocity;
        this.targetSpeed = velocity.length();
        this.setVelocity(velocity);
    }

    public static void spawnBladeSpread(World world, LivingEntity thrower) {
        spawnBladeSpread(world, thrower, null);
    }

    public static void spawnBladeSpread(World world, LivingEntity thrower, Vec3d targetPos) {
        if (world.isClient) return;

        float baseYaw;
        float basePitch;

        if (targetPos != null) {
            // Calculate direction from thrower to target
            Vec3d throwerPos = new Vec3d(thrower.getX(), thrower.getEyeY(), thrower.getZ());
            Vec3d direction = targetPos.subtract(throwerPos).normalize();

            // Calculate yaw and pitch based on target direction
            double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
            baseYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
            basePitch = (float) Math.toDegrees(Math.atan2(-direction.y, horizontalDistance));
        } else {
            // Use entity's facing direction
            baseYaw = thrower.getYaw();
            basePitch = thrower.getPitch();
        }

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
    protected ItemStack getDefaultItemStack() { return new ItemStack(ModItems.GRONK_SWORD); }

    public boolean isGrounded() { return inGround; }

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
            livingEntity.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);

            UsefulMethods.applyKnockback(hitEntity, this, 0.8f, 4.5f);

        }

        if (!this.getWorld().isClient) {
            this.discard();
        }

    }

}