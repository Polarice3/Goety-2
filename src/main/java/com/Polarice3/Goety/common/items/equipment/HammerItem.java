package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.Tags;

public class HammerItem extends TieredItem implements Vanishable {
    private static float initialDamage = ItemConfig.HammerBaseDamage.get().floatValue();
    private final Multimap<Attribute, AttributeModifier> hammerAttributes;
    protected final float speed;

    public HammerItem(Tier itemTier) {
        super(itemTier, new Item.Properties().rarity(Rarity.UNCOMMON).durability(itemTier.getUses()).tab(Goety.TAB));
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        initialDamage = ItemConfig.HammerBaseDamage.get().floatValue() + itemTier.getAttackDamageBonus();
        double attackSpeed = 4.0D - ItemConfig.HammerAttackSpeed.get();
        this.speed = itemTier.getSpeed() - 2.0F;
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", initialDamage - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -attackSpeed, AttributeModifier.Operation.ADDITION));
        this.hammerAttributes = builder.build();
    }

    public HammerItem(){
        this(Tiers.IRON);
    }

    public static float getInitialDamage() {
        return initialDamage;
    }

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, (p_220045_0_) ->
                p_220045_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        if (pAttacker instanceof Player player){
            float f2 = player.getAttackStrengthScale(0.5F);
            if (f2 > 0.9F){
                this.attackMobs(pTarget, player);
                player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.HAMMER_SWING.get(), player.getSoundSource(), 1.0F, 1.0F);
                if (pTarget.isOnGround()) {
                    player.level.playSound((Player) null, pTarget.getX(), pTarget.getY(), pTarget.getZ(), ModSounds.DIRT_DEBRIS.get(), player.getSoundSource(), 1.0F, 1.0F);
                }
                if (player.level instanceof ServerLevel serverLevel){
                    BlockPos blockPos = new BlockPos(pTarget.getX(), pTarget.getY() - 1.0F, pTarget.getZ());
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, pTarget.getX(), pTarget.getY() + 0.25D, pTarget.getZ(), 1.5F);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {
        Level level = p_41427_.getLevel();
        BlockPos blockpos = p_41427_.getClickedPos();
        Player player = p_41427_.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        if (player != null) {
            ItemStack itemStack = player.getUseItem();
            if (blockstate.is(Tags.Blocks.STORAGE_BLOCKS_IRON)) {
                itemStack.hurtAndBreak(5, player, (p_220045_0_) ->
                        p_220045_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                level.destroyBlock(blockpos, false);
                level.setBlockAndUpdate(blockpos, Blocks.DAMAGED_ANVIL.defaultBlockState());
            }
        }
        return super.useOn(p_41427_);
    }

    public boolean getMineBlocks(BlockState pState){
        return pState.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return pState.is(BlockTags.MINEABLE_WITH_PICKAXE) ? this.speed : 1.0F;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(this.getMineBlocks(pState) ? 1 : 2, pEntityLiving, (p_220044_0_) ->
                    p_220044_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        if (this.getMineBlocks(pState)){
            pLevel.playSound((Player) null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.DIRT_DEBRIS.get(), pEntityLiving.getSoundSource(), 1.0F, 1.0F);
            for (BlockPos blockPos : BlockFinder.multiBlockBreak(pEntityLiving, pPos, 1, 1, 1)){
                BlockState blockstate = pLevel.getBlockState(blockPos);
                if (this.getMineBlocks(blockstate)){
                    if (this.destroyBlock(pLevel, blockPos, pEntityLiving, pStack)){
                        if (blockstate.getDestroySpeed(pLevel, blockPos) != 0) {
                            pStack.hurtAndBreak(1, pEntityLiving, (p_220044_0_)
                                    -> p_220044_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean destroyBlock(Level level, BlockPos blockPos, Entity entity, ItemStack itemStack) {
        BlockState blockstate = level.getBlockState(blockPos);
        if (blockstate.isAir()) {
            return false;
        } else {
            FluidState fluidstate = level.getFluidState(blockPos);
            if (!(blockstate.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(2001, blockPos, Block.getId(blockstate));
            }

            BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            Block.dropResources(blockstate, level, blockPos, blockentity, entity, itemStack);

            boolean flag = level.setBlock(blockPos, fluidstate.createLegacyBlock(), 3, 512);
            if (flag) {
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(entity, blockstate));
            }

            return flag;
        }
    }

    public void attackMobs(LivingEntity pTarget, Player pPlayer){
        float f = (float)pPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = EnchantmentHelper.getDamageBonus(pPlayer.getMainHandItem(), pTarget.getMobType());
        float f2 = pPlayer.getAttackStrengthScale(0.5F);
        f = f * (0.2F + f2 * f2 * 0.8F);
        f1 = f1 * f2;
        f = f + f1;

        if (f > 0.5F || f1 > 0.5F) {
            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(pPlayer) * f;
            int j = EnchantmentHelper.getFireAspect(pPlayer);
            double area = 1.0D;
            if (f2 > 0.9F) {
                area = 1.75D;
            }
            area += EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RADIUS.get(), pPlayer);
            for (LivingEntity livingentity : pPlayer.level.getEntitiesOfClass(LivingEntity.class, pTarget.getBoundingBox().inflate(area, 0.25D, area))) {
                if (livingentity != pPlayer && livingentity != pTarget && !pPlayer.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && pPlayer.distanceToSqr(livingentity) < 16.0D && livingentity != pPlayer.getVehicle()) {
                    livingentity.knockback(0.4F, (double) Mth.sin(pPlayer.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(pPlayer.getYRot() * ((float) Math.PI / 180F))));
                    if (livingentity.hurt(DamageSource.playerAttack(pPlayer), f3)) {
                        if (j > 0) {
                            livingentity.setSecondsOnFire(j * 4);
                        }
                        EnchantmentHelper.doPostHurtEffects(livingentity, pPlayer);
                        EnchantmentHelper.doPostDamageEffects(pPlayer, livingentity);
                    }
                }
            }
        }

        pPlayer.level.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.HAMMER_IMPACT.get(), pPlayer.getSoundSource(), 1.0F, 1.0F);
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return (enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.DIGGER
                || enchantment == ModEnchantments.RADIUS.get()
                || super.canApplyAtEnchantingTable(stack, enchantment));
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.hammerAttributes : super.getDefaultAttributeModifiers(equipmentSlot);
    }
}
