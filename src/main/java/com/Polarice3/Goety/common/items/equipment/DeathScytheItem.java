package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CScytheStrikePacket;
import com.Polarice3.Goety.config.ItemConfig;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Consumer;

public class DeathScytheItem extends DarkScytheItem implements ISoulRepair, IPersist {

    public DeathScytheItem() {
        super(ModTiers.DEATH);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.isDamaged(stack);
    }

    public int getBarColor(ItemStack stack) {
        if (this.isBroken(stack)) {
            return 0x800000;
        }
        return super.getBarColor(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (this.isBroken(stack)) {
            return 13;
        }
        return super.getBarWidth(stack);
    }

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (ItemConfig.DeathScythePersist.get()) {
            if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
                if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                    stack.setDamageValue(stack.getMaxDamage() - 1);
                    onBroken.accept(entity);
                }
                return 0;
            }
        }
        return amount;
    }

    @Override
    public boolean isBroken(ItemStack stack) {
        return IPersist.super.isBroken(stack) && ItemConfig.DeathScythePersist.get();
    }

    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        if (this.isNotBroken(stack) || !ItemConfig.DeathScythePersist.get()) {
            return super.getDestroySpeed(stack, blockState);
        }
        return 1.0F;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (this.isNotBroken(stack) || !ItemConfig.DeathScythePersist.get()) {
            return super.getAttributeModifiers(slot, stack);
        } else {
            return ImmutableMultimap.of();
        }
    }

    public static void emptyClick(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof DeathScytheItem scytheItem){
            if (scytheItem.isNotBroken(stack) || !ItemConfig.DeathScythePersist.get()) {
                ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CScytheStrikePacket());
            }
        }
    }

    public static void entityClick(Player player, Level world) {
        if (player.getMainHandItem().getItem() instanceof DeathScytheItem scytheItem) {
            if (!player.level.isClientSide && !player.isSpectator()) {
                if (scytheItem.isNotBroken(player.getMainHandItem()) || !ItemConfig.DeathScythePersist.get()) {
                    strike(world, player);
                }
            }
        }
    }

    public static void strike(Level pLevel, Player pPlayer){
        if (pPlayer.getAttackStrengthScale(0.5F) > 0.9F) {
            pLevel.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 2.0F, 0.4F / (pLevel.random.nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                Vec3 vector3d = pPlayer.getViewVector(1.0F);
                ScytheSlash scytheSlash = new ScytheSlash(pPlayer.getMainHandItem(),
                        pLevel,
                        pPlayer.getX() + vector3d.x / 2,
                        pPlayer.getEyeY() - 0.2,
                        pPlayer.getZ() + vector3d.z / 2,
                        vector3d.x,
                        vector3d.y,
                        vector3d.z);
                scytheSlash.setOwner(pPlayer);
                scytheSlash.setDamage(getInitialDamage());
                scytheSlash.setTotalLife(300);
                pLevel.addFreshEntity(scytheSlash);
            }
        }
    }
}
