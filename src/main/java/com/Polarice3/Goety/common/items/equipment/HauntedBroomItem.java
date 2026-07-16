package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.enchantments.BroomEnchantment;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.vehicle.HauntedBroom;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModUUIDUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class HauntedBroomItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public HauntedBroomItem() {
        super(new Item.Properties().stacksTo(1));
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 4.0D - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -3.1D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(UUID.fromString(ModUUIDUtil.uuidString("item.goety.haunted_broom.knockback")), "Tool modifier", 1.0D, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        if (stack.getTag() != null) {
            if (stack.getTag().contains("owner")) {
                if (stack.getEnchantmentLevel(ModEnchantments.FEALTY.get()) <= 0) {
                    stack.getTag().remove("owner");
                    stack.getTag().remove("owner_name");
                }
            }
        }
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.is(Blocks.COBWEB)) {
            if (pLevel instanceof ServerLevel serverLevel) {
                pState.getBlock().popExperience(serverLevel, pPos, 1 + pLevel.getRandom().nextInt(5));
            }
        }
        return true;
    }

    public float getDestroySpeed(ItemStack p_43288_, BlockState p_43289_) {
        if (p_43289_.is(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            return super.getDestroySpeed(p_43288_, p_43289_);
        }
    }

    public boolean isCorrectToolForDrops(BlockState p_43298_) {
        return p_43298_.is(Blocks.COBWEB);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide) {
            if (pPlayer.getCooldowns().isOnCooldown(this) || SEHelper.isOnCooldown(pPlayer, itemstack)) {
                return InteractionResultHolder.pass(itemstack);
            }
            if (getOwnerID(itemstack) != null && getOwner(pLevel, getOwnerID(itemstack)) != pPlayer) {
                return InteractionResultHolder.pass(itemstack);
            }
            HitResult hitResult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.ANY);
            if (hitResult.getType() == HitResult.Type.MISS) {
                return InteractionResultHolder.pass(itemstack);
            } else {
                Vec3 vector3d = pPlayer.getViewVector(1.0F);
                double d0 = 5.0D;
                List<Entity> list = pLevel.getEntities(pPlayer, pPlayer.getBoundingBox().expandTowards(vector3d.scale(d0)).inflate(1.0D), ENTITY_PREDICATE);
                if (!list.isEmpty()) {
                    Vec3 vector3d1 = pPlayer.getEyePosition(1.0F);

                    for(Entity entity : list) {
                        AABB axisalignedbb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
                        if (axisalignedbb.contains(vector3d1)) {
                            return InteractionResultHolder.pass(itemstack);
                        }
                    }
                }

                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    HauntedBroom broom = new HauntedBroom(itemstack, pLevel, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z);
                    broom.setYRot(pPlayer.getYRot());
                    if (itemstack.getEnchantmentLevel(ModEnchantments.FEALTY.get()) > 0) {
                        broom.setOwner(pPlayer);
                    }
                    int hardy = itemstack.getEnchantmentLevel(ModEnchantments.HARDY.get());
                    if (hardy > 0) {
                        broom.setDamageThreshold(broom.getDamageThreshold() + (hardy * 20));
                    }
                    if (!pLevel.noCollision(broom, broom.getBoundingBox().inflate(-0.1D))) {
                        return InteractionResultHolder.fail(itemstack);
                    } else {
                        pLevel.addFreshEntity(broom);
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }

                        pPlayer.awardStat(Stats.ITEM_USED.get(this));
                        return InteractionResultHolder.success(itemstack);
                    }
                }
            }
        }
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pAttacker.level.playSound(null, pAttacker.getX(), pAttacker.getY(), pAttacker.getZ(), ModSounds.BROOM_SWING.get(), pAttacker.getSoundSource(), 1.0F, 1.0F);
        pTarget.level.playSound(null, pTarget.getX(), pTarget.getY(), pTarget.getZ(), ModSounds.BROOM_IMPACT.get(), pAttacker.getSoundSource(), 1.0F, 1.0F);
        return true;
    }

    public static void setOwner(@Nullable LivingEntity entity, ItemStack stack) {
        CompoundTag entityTag = stack.getOrCreateTag();
        if (entity != null) {
            entityTag.putUUID("owner", entity.getUUID());
            entityTag.putString("owner_name", entity.getDisplayName().getString());
        }
    }

    @Nullable
    public static UUID getOwnerID(ItemStack stack){
        CompoundTag entityTag = stack.getTag();
        if (entityTag != null){
            if (entityTag.contains("owner")) {
                return entityTag.getUUID("owner");
            }
        }
        return null;
    }

    @Nullable
    public static Entity getOwner(Level level, UUID uuid) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getEntity(uuid);
        }
        return null;
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return this.getMaxStackSize(p_41456_) == 1;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == ModEnchantments.VELOCITY.get()
                || enchantment == ModEnchantments.BURNING.get()
                || enchantment instanceof BroomEnchantment;
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
        if (pEquipmentSlot == EquipmentSlot.MAINHAND){
            return this.defaultModifiers;
        }
        return super.getAttributeModifiers(pEquipmentSlot, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("owner_name")) {
                tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + stack.getTag().getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
            }
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
