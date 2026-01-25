package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class WindyRobeItem extends SingleStackItem{

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof Player player) {
            if (CuriosFinder.hasWindyRobes(player) && !player.isSpectator()){
                if (SEHelper.getSoulsAmount(player, ItemConfig.WindRobeSouls.get())
                        || player.isCreative()) {
                    Vec3 vector3d = player.getDeltaMovement();
                    if (player.hasEffect(MobEffects.SLOW_FALLING)){
                        player.removeEffect(MobEffects.SLOW_FALLING);
                    }
                    if (!player.onGround() && vector3d.y < 0.0D
                            && !player.isNoGravity()
                            && !player.getAbilities().flying
                            && !player.onClimbable()
                            && !player.isInFluidType()
                            && !player.isInWater()
                            && !player.isInLava()
                            && player.fallDistance >= 2.0F) {
                        if (player.tickCount % 20 == 0 && !player.isCreative() && player.fallDistance > 3.0F) {
                            SEHelper.decreaseSouls(player, ItemConfig.WindRobeSouls.get());
                        }
                        if (worldIn instanceof ServerLevel serverLevel){
                            ColorUtil color = new ColorUtil(0xffffff);
                            ServerParticleUtil.windParticle(serverLevel, color, 1.0F + serverLevel.random.nextFloat() * 0.5F, 0.0F, player.getId(), player.position());
                            ServerParticleUtil.circularParticles(serverLevel, ParticleTypes.CLOUD, player, 1.0F);
                            if (this == ModItems.STORM_ROBE.get()) {
                                if (serverLevel.random.nextInt(20) == 0) {
                                    Vec3 vec3 = Vec3.atCenterOf(player.blockPosition());
                                    Vec3 vec31 = vec3.add(player.getRandom().nextDouble(), 1.0D, player.getRandom().nextDouble());
                                    ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, 2));
                                }
                            }
                        }
                        if (!player.isCrouching() && !player.isShiftKeyDown()) {
                            player.setDeltaMovement(vector3d.multiply(1.0D, 0.875D, 1.0D));
                        } else {
                            player.setDeltaMovement(vector3d.multiply(1.0D, 0.99D, 1.0D));
                        }
                    }
                }
            }
        }

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        if (IronLoaded.IRON_SPELLBOOKS.isLoaded()){
            if (MainConfig.RobesIronResist.get()) {
                if (stack.is(ModItems.STORM_ROBE.get())){
                    map.put(IronAttributes.LIGHTNING_MAGIC_RESIST, new AttributeModifier(UUID.fromString("ed6bb7e0-c849-4e25-849a-e288e2b76961"), "Robes Iron Spell Resist", 0.5F, AttributeModifier.Operation.ADDITION));
                }
            }
        }
        return map;
    }
}
