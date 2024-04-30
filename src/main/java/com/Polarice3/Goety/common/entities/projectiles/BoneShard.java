package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class BoneShard extends AbstractArrow {
    public BoneShard(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public BoneShard(double p_36712_, double p_36713_, double p_36714_, Level p_36715_) {
        super(ModEntityType.BONE_SHARD.get(), p_36712_, p_36713_, p_36714_, p_36715_);
    }

    public BoneShard(LivingEntity p_36718_, Level p_36719_) {
        super(ModEntityType.BONE_SHARD.get(), p_36718_, p_36719_);
    }

    protected boolean tryPickup(Player p_150196_) {
        return false;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return ModSounds.BONE_SHARD_IMPACT.get();
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
