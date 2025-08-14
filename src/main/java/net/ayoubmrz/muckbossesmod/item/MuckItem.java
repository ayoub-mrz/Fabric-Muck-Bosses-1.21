package net.ayoubmrz.muckbossesmod.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MuckItem extends Item {
    private final float attackDamage;
    private final float attackSpeed;

    public MuckItem(Item.Settings settings, Float attackDamage, Float attackSpeed) {
        super(settings);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public static ToolComponent createToolComponent() {
        return new ToolComponent(List.of(), 1.0F, 2);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
    }

    public int getEnchantability() {
        return 1;
    }
}