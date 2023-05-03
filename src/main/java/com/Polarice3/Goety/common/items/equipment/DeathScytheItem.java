package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CScytheStrikePacket;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class DeathScytheItem extends DarkScytheItem{

    public DeathScytheItem() {
        super(ModTiers.DEATH);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (MainConfig.SoulRepair.get()) {
            if (entityIn instanceof Player player) {
                if (!(player.swinging && isSelected)) {
                    if (stack.isDamaged()) {
                        if (SEHelper.getSoulsContainer(player)){
                            if (SEHelper.getSoulsAmount(player, MainConfig.ItemsRepairAmount.get())){
                                if (player.tickCount % 20 == 0) {
                                    stack.setDamageValue(stack.getDamageValue() - 1);
                                    SEHelper.decreaseSouls(player, MainConfig.ItemsRepairAmount.get());
                                }
                            }
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public static void emptyClick(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof DeathScytheItem){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CScytheStrikePacket());
        }
    }

    public static void entityClick(Player player, Level world) {
        if (player.getMainHandItem().getItem() instanceof DeathScytheItem) {
            if (!player.level.isClientSide && !player.isSpectator()) {
                strike(world, player);
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
                scytheSlash.setOwnerId(pPlayer.getUUID());
                scytheSlash.setDamage(getInitialDamage());
                scytheSlash.setTotallife(300);
                pLevel.addFreshEntity(scytheSlash);
            }
        }
    }
}
