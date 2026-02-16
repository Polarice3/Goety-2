package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.WindBlowParticle;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.VoidSlash;
import com.Polarice3.Goety.common.events.TimedEvents;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CBoEStrikePacket;
import com.Polarice3.Goety.common.network.server.SPlayFollowSoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BladeOfEnderItem extends SwordItem implements IPersist, ISoulRepair {

    public BladeOfEnderItem() {
        super(ModTiers.VOID, Mth.floor(ItemConfig.BladeOfEnderDamage.get() - 1), -(4.0F - ItemConfig.BladeOfEnderAttackSpeed.get().floatValue()), new Item.Properties().durability(ItemConfig.BladeOfEnderDurability.get()).fireResistant());
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
        if (ItemConfig.BladeOfEnderPersist.get()) {
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
        return IPersist.super.isBroken(stack) && ItemConfig.BladeOfEnderPersist.get();
    }

    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        if (this.isNotBroken(stack) || !ItemConfig.BladeOfEnderPersist.get()) {
            return super.getDestroySpeed(stack, blockState);
        }
        return 1.0F;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (this.isNotBroken(stack) || !ItemConfig.BladeOfEnderPersist.get()) {
            Multimap<Attribute, AttributeModifier> map = MultimapBuilder.hashKeys().hashSetValues().build(super.getAttributeModifiers(slot, stack));
            map.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ModUUIDUtil.createUUID("item.goety.boe.reach"), "Tool Modifier", 1, AttributeModifier.Operation.ADDITION));
            return slot == EquipmentSlot.MAINHAND ? map : super.getAttributeModifiers(slot, stack);
        } else {
            return ImmutableMultimap.of();
        }
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return ItemConfig.BladeOfEnderEnchantability.get();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        InteractionHand offhand = handIn == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack offhandItem = playerIn.getItemInHand(offhand);
        if (!(ItemConfig.BladeOfEnderPersist.get() && this.isBroken(itemstack))) {
            if (!playerIn.getCooldowns().isOnCooldown(this)
                    && !offhandItem.canPerformAction(ToolActions.SHIELD_BLOCK)
                    && !(offhandItem.getItem() instanceof IWand wand && !wand.isOnCooldown(playerIn, offhandItem))) {
                int coolTick = 20;
                if (playerIn.isSprinting()) {
                    float f1 = (float) Math.cos(Math.toRadians(playerIn.getYRot() + 90));
                    float f2 = (float) Math.sin(Math.toRadians(playerIn.getYRot() + 90));
                    Vec3 vec3 = playerIn.getViewVector(1.0F);
                    float r = (float) Mth.square(playerIn.distanceToSqr(vec3));
                    r = Mth.clamp(r, 0.0F, 10.0F);
                    playerIn.push(f1 * 0.9F * r, 0, f2 * 0.9F * r);
                    if (worldIn instanceof ServerLevel serverLevel) {
                        ModNetwork.sentToTrackingEntityAndPlayer(playerIn, new SPlayFollowSoundPacket(playerIn, ModSounds.VHOE_CHARGE.get(), 3.0F, playerIn.getVoicePitch(), false));
                        TimedEvents.submitTask("goety:boe_charge", new ChargeTask(playerIn.getUUID(), serverLevel, ItemConfig.BladeOfEnderDamage.get().floatValue()));
                        coolTick = 100;
                    }
                } else {
                    float speed = -2.5F;
                    float dodgeYaw = (float) Math.toRadians(playerIn.getYRot() + 90);
                    Vec3 vec3 = playerIn.getDeltaMovement().add(speed * Math.cos(dodgeYaw), 0, speed * Math.sin(dodgeYaw));
                    if (!worldIn.isClientSide) {
                        ModNetwork.sentToTrackingEntityAndPlayer(playerIn, new SPlayWorldSoundPacket(playerIn.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, 2.0F, 1.0F));
                    } else {
                        playerIn.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 2.0F, 1.0F);
                    }
                    playerIn.setDeltaMovement(vec3.x, 0.4F, vec3.z);
                }
                if (!worldIn.isClientSide) {
                    playerIn.getCooldowns().addCooldown(this, coolTick);
                }
                playerIn.swing(handIn);
                return InteractionResultHolder.consume(itemstack);
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == ModEnchantments.VELOCITY.get() || enchantment == ModEnchantments.RADIUS.get();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(Tags.Items.INGOTS_NETHERITE);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;
        ChatFormatting secondary = ChatFormatting.BLUE;
        if (ItemConfig.BladeOfEnderPersist.get() && this.isBroken(stack)) {
            tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltip.add(Component.translatable("info.goety.blade_of_ender").withStyle(main));
            tooltip.add(Component.translatable("info.goety.blade_of_ender.1").withStyle(secondary));
            tooltip.add(Component.translatable("info.goety.blade_of_ender.2").withStyle(secondary));
        }
    }

    public static void emptyClick(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BladeOfEnderItem blade){
            if (blade.isNotBroken(stack) || !ItemConfig.BladeOfEnderPersist.get()) {
                ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CBoEStrikePacket());
            }
        }
    }

    public static void entityClick(Player player, Level world) {
        if (player.getMainHandItem().getItem() instanceof BladeOfEnderItem blade) {
            if (!player.level.isClientSide && !player.isSpectator()) {
                if (!player.getCooldowns().isOnCooldown(blade)) {
                    if (blade.isNotBroken(player.getMainHandItem()) || !ItemConfig.BladeOfEnderPersist.get()) {
                        strike(world, player);
                    }
                }
            }
        }
    }

    public static void strike(Level pLevel, Player pPlayer){
        if (pPlayer.getAttackStrengthScale(0.5F) > 0.9F) {
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), SoundSource.PLAYERS, 2.0F, pPlayer.getVoicePitch());
            if (!pLevel.isClientSide) {
                ItemStack sword = pPlayer.getMainHandItem();
                float speed = 0.0F;
                float radius = 0.0F;
                if (sword.isEnchanted()) {
                    speed += sword.getEnchantmentLevel(ModEnchantments.VELOCITY.get()) / 3.0F;
                    radius += sword.getEnchantmentLevel(ModEnchantments.RADIUS.get()) / 4.0F;
                }
                Vec3 vector3d = pPlayer.getViewVector(1.0F);
                VoidSlash slash = new VoidSlash(sword, pLevel, pPlayer);
                slash.setPos(pPlayer.getX() + vector3d.x / 2,
                        pPlayer.getEyeY() - 0.2,
                        pPlayer.getZ() + vector3d.z / 2);
                slash.setDamage(ItemConfig.BladeOfEnderDamage.get().floatValue());
                slash.setMaxLifeSpan(MathHelper.secondsToTicks(0.5F));
                slash.setRadius(slash.getRadius() + radius);
                slash.setMaxRadius(slash.getMaxRadius() + radius);
                slash.slash(vector3d, 0.5F + speed);
                slash.setVoidLevel(1);
                pLevel.addFreshEntity(slash);
            }
        }
    }

    public static class ChargeTask implements EventTask {
        UUID owner;
        ServerLevel level;
        public float damage;
        public int ticks;

        public ChargeTask(UUID owner, ServerLevel level, float damage) {
            this.owner = owner;
            this.level = level;
            this.damage = damage;
            this.ticks = 0;
        }

        @Override
        public void tickTask() {
            ++this.ticks;
            if (this.level.getEntity(this.owner) instanceof LivingEntity ownerLiving) {
                int width = this.level.getRandom().nextIntBetweenInclusive(1, 4);
                float height = this.level.getRandom().nextFloat() * 0.5F;
                Vec3 vec3 = ownerLiving.getEyePosition().offsetRandom(this.level.getRandom(), 2.0F);
                Vec3 angle = ownerLiving.getLookAngle().multiply(-1.0D, 1.0D, -1.0D);
                this.level.sendParticles(new WindBlowParticle.Option(new ColorUtil(ChatFormatting.LIGHT_PURPLE), width, height), vec3.x, vec3.y, vec3.z, 0, angle.x, angle.y, angle.z, 1.0F);

                for (LivingEntity entityHit : this.level.getEntitiesOfClass(LivingEntity.class, ownerLiving.getBoundingBox().inflate(2.0F))) {
                    if (!MobUtil.areAllies(ownerLiving, entityHit) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entityHit) && entityHit.isAttackable()) {
                        boolean flag = entityHit.hurt(this.level.damageSources().mobAttack(ownerLiving), this.damage);
                        if (entityHit.isDamageSourceBlocked(this.level.damageSources().mobAttack(ownerLiving))) {
                            MobUtil.disableShield(entityHit, 100);
                        }
                        if (flag) {
                            if (!entityHit.hasEffect(GoetyEffects.VOID_TOUCHED.get())) {
                                entityHit.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(5), 0, false, true));
                            }
                            double d0 = entityHit.getX() - ownerLiving.getX();
                            double d1 = entityHit.getZ() - ownerLiving.getZ();
                            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                            entityHit.push(d0 / d2 * 2.5D, 0.18D, d1 / d2 * 2.2D);
                        }
                    }
                }
            }
        }

        @Override
        public boolean getAsBoolean() {
            boolean flag = false;
            if (this.level.getEntity(this.owner) instanceof LivingEntity ownerLiving) {
                if (this.ticks >= 9 || ownerLiving.isDeadOrDying()) {
                    flag = true;
                }
            } else {
                flag = true;
            }
            return flag;
        }
    }
}
