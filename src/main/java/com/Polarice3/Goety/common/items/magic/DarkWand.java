package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.*;
import com.Polarice3.Goety.common.blocks.BrewCauldronBlock;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.entities.neutral.AbstractVine;
import com.Polarice3.Goety.common.events.spell.GoetyEventFactory;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayEntitySoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Learned item capabilities from codes made by @vemerion & @MrCrayfish
 */
public class DarkWand extends Item implements IWand {
    public SpellType spellType;

    public DarkWand(Properties properties, SpellType spellType){
        super(properties);
        this.spellType = spellType;
    }

    public DarkWand(SpellType spellType) {
        this(wandProperties(), spellType);
    }

    public DarkWand() {
        this(SpellType.NONE);
    }

    public SpellType getSpellType(){
        return this.spellType;
    }

    public static Item.Properties wandProperties(){
        return new Properties()
                .rarity(Rarity.RARE)
                .setNoRepair()
                .stacksTo(1);
    }

    @Override
    public float getWandVisualHeight(Level level, LivingEntity entity, ItemStack stack) {
        return 0.4F;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            CompoundTag compound = stack.getOrCreateTag();
            if (stack.getTag() == null) {
                compound.putInt(SOULUSE, SoulUse(livingEntity, stack));
                compound.putInt(SOULCOST, 0);
                compound.putInt(CASTTIME, CastDuration(stack));
                compound.putInt(COOL, 0);
                compound.putInt(SHOTS, 0);
                compound.putInt(SECONDS, 0);
            } else {
                if (!compound.contains(SHOTS)){
                    compound.putInt(SHOTS, 0);
                }
            }
            if (this.getSpell(stack) != null) {
                this.setSpellConditions(this.getSpell(stack), stack, livingEntity);
            } else {
                this.setSpellConditions(null, stack, livingEntity);
            }
            compound.putInt(SOULUSE, SoulUse(livingEntity, stack));
            compound.putInt(CASTTIME, CastDuration(stack));
            if (IWand.getFocus(stack) != null){
                IWand.getFocus(stack).inventoryTick(worldIn, entityIn, itemSlot, isSelected);
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CompoundTag compound = pStack.getOrCreateTag();
        compound.putInt(SOULUSE, SoulUse(pPlayer, pStack));
        compound.putInt(SOULCOST, 0);
        compound.putInt(CASTTIME, CastDuration(pStack));
        compound.putInt(COOL, 0);
        compound.putInt(SECONDS, 0);
        compound.putInt(SHOTS, 0);
        this.setSpellConditions(null, pStack, pPlayer);
    }

    public int SoulUse(LivingEntity entityLiving, ItemStack stack){
        if (IWand.getFocus(stack).isEnchanted() && SpellConfig.EnchantMultiCost.get()){
            return (int) (SoulCost(stack) * 2 * SEHelper.soulDiscount(entityLiving));
        } else {
            return (int) (SoulCost(stack) * SEHelper.soulDiscount(entityLiving));
        }
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        boolean flag = false;
        if (entity instanceof LivingEntity target && target instanceof IOwned owned && (owned.getTrueOwner() == player || (owned.getTrueOwner() instanceof IOwned owned1 && owned1.getTrueOwner() == player))) {
            if (!player.level.isClientSide) {
                if (IWand.getFocus(stack).getItem() instanceof CallFocus && !CallFocus.hasSummon(IWand.getFocus(stack))) {
                    CompoundTag compoundTag = new CompoundTag();
                    if (IWand.getFocus(stack).hasTag()) {
                        compoundTag = IWand.getFocus(stack).getTag();
                    }
                    CallFocus.setSummon(compoundTag, target);
                    IWand.getFocus(stack).setTag(compoundTag);
                    player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                    flag = true;
                } else if (IWand.getFocus(stack).getItem() instanceof TroopFocus && !TroopFocus.hasSummonType(IWand.getFocus(stack))) {
                    CompoundTag compoundTag = new CompoundTag();
                    if (IWand.getFocus(stack).hasTag()) {
                        compoundTag = IWand.getFocus(stack).getTag();
                    }
                    TroopFocus.setSummonType(compoundTag, target.getType());
                    IWand.getFocus(stack).setTag(compoundTag);
                    player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                    flag = true;
                } else if (IWand.getFocus(stack).getItem() instanceof CommandFocus && owned instanceof IServant servant && servant.canBeCommanded() && !CommandFocus.hasServant(IWand.getFocus(stack))) {
                    CompoundTag compoundTag = new CompoundTag();
                    if (IWand.getFocus(stack).hasTag()) {
                        compoundTag = IWand.getFocus(stack).getTag();
                    }
                    CommandFocus.setServant(compoundTag, target);
                    IWand.getFocus(stack).setTag(compoundTag);
                    player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                    flag = true;
                } else if (IWand.getFocus(stack).getItem() instanceof OrderFocus && owned instanceof IServant servant && servant.canBeCommanded()) {
                    List<LivingEntity> list = OrderFocus.getServants(IWand.getFocus(stack));
                    if (list.isEmpty() || list.size() < 8) {
                        if (!list.contains(target)) {
                            OrderFocus.setServants(IWand.getFocus(stack), player, target);
                            player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                            flag = true;
                        }
                    }
                }
                if (!flag){
                    if (owned instanceof IServant summonedEntity) {
                        if (player.isShiftKeyDown() || player.isCrouching()) {
                            if (SpellConfig.OwnerHitKill.get() == 0) {
                                summonedEntity.tryKill(player);
                            }
                        } else {
                            if (SpellConfig.OwnerHitCommand.get()) {
                                if (summonedEntity.canUpdateMove()) {
                                    summonedEntity.updateMoveMode(player);
                                }
                            }
                        }
                    } else if (owned instanceof AbstractVine vine){
                        if (player.isShiftKeyDown() || player.isCrouching()) {
                            if (SpellConfig.OwnerHitKill.get() == 0) {
                                vine.kill();
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
        if (IWand.getFocus(stack).getItem() instanceof CommandFocus) {
            if (CommandFocus.getServant(IWand.getFocus(stack)) instanceof IServant summoned && summoned != target){
                if (summoned.getTrueOwner() == player && target.distanceTo(player) <= 64){
                    summoned.setCommandPosEntity(target);
                    player.playSound(ModSounds.COMMAND.get(), 1.0F, 0.45F);
                    if (!player.level.isClientSide) {
                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.COMMAND.get(), 1.0F, 0.45F));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        } else if (IWand.getFocus(stack).getItem() instanceof OrderFocus) {
            if (!OrderFocus.getServants(IWand.getFocus(stack)).isEmpty()) {
                int i = 0;
                for (LivingEntity livingEntity : OrderFocus.getServants(IWand.getFocus(stack))) {
                    if (livingEntity instanceof IServant summoned && summoned != target) {
                        if (summoned.getTrueOwner() == player && target.distanceTo(player) <= 64) {
                            summoned.setCommandPosEntityOrder(target);
                            ++i;
                        }
                    }
                }
                if (i > 0){
                    player.playSound(ModSounds.COMMAND.get(), 1.0F, 0.45F);
                    if (!player.level.isClientSide) {
                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.COMMAND.get(), 1.0F, 0.45F));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (target instanceof IOwned owned) {
            if (owned.getTrueOwner() == player || (owned.getTrueOwner() instanceof IOwned owned1 && owned1.getTrueOwner() == player)) {
                if (owned instanceof IServant summonedEntity) {
                    if (player.isShiftKeyDown() || player.isCrouching()) {
                        if (SpellConfig.OwnerHitKill.get() == 1) {
                            summonedEntity.tryKill(player);
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        if (!SpellConfig.OwnerHitCommand.get()) {
                            if (summonedEntity.canUpdateMove()) {
                                summonedEntity.updateMoveMode(player);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                } else if (owned instanceof AbstractVine vine) {
                    if (player.isShiftKeyDown() || player.isCrouching()) {
                        if (SpellConfig.OwnerHitKill.get() == 1) {
                            vine.kill();
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        if (this.getSpell(stack) instanceof ITouchSpell touchSpells){
            if (this.canCastTouch(stack, player.level, player)) {
                if (player.level instanceof ServerLevel serverLevel) {
                    touchSpells.touchResult(serverLevel, player, target, stack, WandUtil.getStats(player, touchSpells));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);

    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        InteractionHand hand = pContext.getHand();
        ItemStack stack = pContext.getItemInHand();
        if (player != null) {
            if (IWand.getFocus(stack).getItem() instanceof RecallFocus recallFocus){
                CompoundTag compoundTag = IWand.getFocus(stack).getOrCreateTag();
                if (!RecallFocus.hasRecall(IWand.getFocus(stack))){
                    BlockEntity tileEntity = level.getBlockEntity(blockpos);
                    if (tileEntity instanceof ArcaBlockEntity arcaTile) {
                        if (pContext.getPlayer() == arcaTile.getPlayer() && arcaTile.getLevel() != null) {
                            recallFocus.addRecallTags(arcaTile.getLevel().dimension(), arcaTile.getBlockPos(), compoundTag);
                            IWand.getFocus(stack).setTag(compoundTag);
                            player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                            if (!level.isClientSide) {
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                            }
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                    BlockState blockstate = level.getBlockState(blockpos);
                    if (blockstate.is(ModTags.Blocks.RECALL_BLOCKS)) {
                        recallFocus.addRecallTags(level.dimension(), blockpos, compoundTag);
                        IWand.getFocus(stack).setTag(compoundTag);
                        player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                        if (!level.isClientSide) {
                            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            } else if (IWand.getFocus(stack).getItem() instanceof CommandFocus){
                if (CommandFocus.hasServant(IWand.getFocus(stack)) && CommandFocus.getServant(IWand.getFocus(stack)) instanceof IServant summoned){
                    LivingEntity livingEntity = CommandFocus.getServant(IWand.getFocus(stack));
                    if (livingEntity != null) {
                        if (summoned.getTrueOwner() == player && livingEntity.distanceTo(player) <= 64) {
                            BlockPos above = blockpos.above();
                            boolean flag = false;
                            if (summoned.canCommandToBlock(level, blockpos)) {
                                summoned.setCommandPos(blockpos);
                                flag = true;
                            } else if (summoned.canCommandToBlock(level, above)) {
                                summoned.setCommandPos(above);
                                flag = true;
                            }
                            if (flag) {
                                player.playSound(ModSounds.COMMAND.get(), 1.0F, 0.45F);
                                if (!level.isClientSide) {
                                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.COMMAND.get(), 1.0F, 0.45F));
                                }
                                return InteractionResult.sidedSuccess(level.isClientSide);
                            }
                        }
                    }
                }
            } else if (IWand.getFocus(stack).getItem() instanceof OrderFocus){
                if (!OrderFocus.getServants(IWand.getFocus(stack)).isEmpty()) {
                    int i = 0;
                    for (LivingEntity livingEntity : OrderFocus.getServants(IWand.getFocus(stack))) {
                        if (livingEntity instanceof IServant summoned && summoned.canBeCommanded()) {
                            if (summoned.getTrueOwner() == player && livingEntity.distanceTo(player) <= 64) {
                                BlockPos above = blockpos.above();
                                if (summoned.canCommandToBlock(level, blockpos)) {
                                    summoned.setCommandPos(blockpos);
                                    ++i;
                                } else if (summoned.canCommandToBlock(level, above)) {
                                    summoned.setCommandPos(above);
                                    ++i;
                                }
                            }
                        }
                    }
                    if (i > 0) {
                        player.playSound(ModSounds.COMMAND.get(), 1.0F, 0.45F);
                        if (!level.isClientSide) {
                            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.COMMAND.get(), 1.0F, 0.45F));
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            } else if (this.getSpell(stack) instanceof IBlockSpell blockSpell0){
                ISpell spell2 = GoetyEventFactory.onBlockBasedSpell(player.level, blockpos, player.level.getBlockState(blockpos), blockSpell0, pContext.getClickedFace(), player);
                if (spell2 instanceof IBlockSpell blockSpell) {
                    if (player.level instanceof ServerLevel serverLevel) {
                        if (blockSpell.rightBlock(serverLevel, player, blockpos, pContext.getClickedFace(), WandUtil.getStats(player, blockSpell))) {
                            if (this.canCastTouch(stack, level, player)) {
                                blockSpell.blockResult(serverLevel, player, stack, blockpos, pContext.getClickedFace(), WandUtil.getStats(player, blockSpell));
                            }
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            } else if (level.getBlockState(blockpos).is(BlockTags.BANNERS) && level.getBlockEntity(blockpos) instanceof BannerBlockEntity bannerBlock){
                if (!level.isClientSide){
                    CompoundTag compoundtag = BlockItem.getBlockEntityData(bannerBlock.getItem());
                    if (compoundtag != null) {
                        if (compoundtag.contains("Patterns")) {
                            SEHelper.setBannerBaseColor(player, bannerBlock.getBaseColor());
                            SEHelper.setBannerPattern(player, compoundtag.getList("Patterns", 10));
                            player.displayClientMessage(Component.translatable("info.goety.banner.add", player.getDisplayName()), true);
                            level.playSound(null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 1.0F, 0.5F);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            } else if (level.getBlockState(blockpos).getBlock() instanceof BrewCauldronBlock) {
                if (!level.isClientSide) {
                    if (level.getBlockEntity(blockpos) instanceof BrewCauldronBlockEntity cauldronBlock) {
                        if (MobUtil.isShifting(player)) {
                            if (stack.getItem() instanceof IWand){
                                cauldronBlock.fullReset();
                                level.playSound(null, blockpos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                                level.playSound(null, blockpos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            } else if (!level.getBlockState(blockpos).isAir()){
                if (!level.isClientSide){
                    return level.getBlockState(blockpos).use(level, player, hand, new BlockHitResult(pContext.getClickLocation(), pContext.getClickedFace(), pContext.getClickedPos(), pContext.isInside()));
                }
            }
        }
        return super.useOn(pContext);
    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        ISpell iSpell = GoetyEventFactory.onStartSpell(livingEntityIn, stack, this.getSpell(stack));
        if (worldIn instanceof ServerLevel && this.cannotCast(livingEntityIn, stack, iSpell)){
            livingEntityIn.stopUsingItem();
            return;
        }
        int CastTime = stack.getUseDuration() - count;
        if (livingEntityIn.getUseItem() == stack && iSpell != null && this.isNotInstant(iSpell, livingEntityIn, stack)) {
            SoundEvent soundevent = this.CastingSound(stack, livingEntityIn);
            if (CastTime == 1 && soundevent != null) {
                if (worldIn instanceof ServerLevel serverLevel) {
                    iSpell.startSpell(serverLevel, livingEntityIn, stack, WandUtil.getStats(livingEntityIn, iSpell));
                }
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), soundevent, SoundSource.PLAYERS, this.castingVolume(stack), this.castingPitch(stack));
            }
            if (worldIn instanceof ServerLevel serverLevel) {
                iSpell = GoetyEventFactory.onCastingSpell(livingEntityIn, stack, iSpell, CastTime);
                if (iSpell != null) {
                    iSpell.useSpell(serverLevel, livingEntityIn, stack, CastTime, WandUtil.getStats(livingEntityIn, iSpell));
                } else {
                    livingEntityIn.stopUsingItem();
                }
            }
            if (iSpell != null) {
                if (iSpell instanceof IChargingSpell spell
                        && spell.castUp(livingEntityIn, stack) > 0){
                    this.useParticles(worldIn, livingEntityIn, stack, iSpell);
                } else if (!(iSpell instanceof IChargingSpell)) {
                    this.useParticles(worldIn, livingEntityIn, stack, iSpell);
                }
                if (iSpell instanceof IChargingSpell spell) {
                    if (stack.getTag() != null) {
                        if (CastTime >= spell.castUp(livingEntityIn, stack) || spell.castUp(livingEntityIn, stack) <= 0) {
                            stack.getTag().putInt(COOL, stack.getTag().getInt(COOL) + 1);
                            if (stack.getTag().getInt(COOL) >= Cooldown(stack)) {
                                stack.getTag().putInt(COOL, 0);
                                if (spell.shotsNumber(livingEntityIn, stack) > 0){
                                    this.increaseShots(stack);
                                }
                                this.MagicResults(stack, worldIn, livingEntityIn, spell);
                            }
                        }
                    }

                    if (livingEntityIn instanceof Player player){
                        if (!SEHelper.getSoulsAmount(player, iSpell.soulCost(player, stack)) && !player.isCreative()){
                            player.stopUsingItem();
                        }
                    }
                }
            } else {
                livingEntityIn.stopUsingItem();
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int useTimeRemaining) {
        if (level instanceof ServerLevel serverLevel) {
            int CastTime = stack.getUseDuration() - useTimeRemaining;
            ISpell spell = GoetyEventFactory.onStopSpell(livingEntity, stack, this.getSpell(stack), CastTime, useTimeRemaining);
            if (spell != null) {
                spell.stopSpell(serverLevel, livingEntity, stack, IWand.getFocus(stack), CastTime, WandUtil.getStats(livingEntity, spell));
                if (livingEntity instanceof Player player) {
                    if (spell instanceof IChargingSpell chargeSpell) {
                        if (chargeSpell.shotsNumber(player, stack) > 0) {
                            if (this.ShotsFired(stack) > 0) {
                                float coolPercent = (float) this.ShotsFired(stack) / chargeSpell.shotsNumber(player, stack);
                                this.setShots(stack, 0);
                                if (!spell.hasCustomCooldown(player, stack, IWand.getFocus(stack), Mth.floor(chargeSpell.spellCooldown(player) * coolPercent))) {
                                    SEHelper.addCooldown(player, IWand.getFocus(stack).getItem(), Mth.floor(chargeSpell.spellCooldown(player) * coolPercent));
                                }
                            }
                        } else {
                            if (!spell.hasCustomCooldown(player, stack, IWand.getFocus(stack), Mth.floor(chargeSpell.spellCooldown(player)))) {
                                if (CastTime > chargeSpell.castUp(player, stack)) {
                                    SEHelper.addCooldown(player, IWand.getFocus(stack).getItem(), Mth.floor(chargeSpell.spellCooldown(player)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int getUseDuration(ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getTag().getInt(CASTTIME);
        } else {
            return this.CastDuration(stack);
        }
    }

    @Nonnull
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Nonnull
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        ISpell iSpell = GoetyEventFactory.onCastSpell(entityLiving, this.getSpell(stack));
        if (iSpell != null) {
            if (!(iSpell instanceof IChargingSpell) || this.isNotInstant(iSpell, entityLiving, stack) || this.notTouch(iSpell)) {
                if (!this.cannotCast(entityLiving, stack)){
                    this.MagicResults(stack, worldIn, entityLiving, iSpell);
                }
            }
        }
        if (stack.getTag() != null) {
            if (stack.getTag().getInt(COOL) > 0) {
                stack.getTag().putInt(COOL, 0);
            }
            if (stack.getTag().getInt(SHOTS) > 0) {
                stack.getTag().putInt(SHOTS, 0);
            }
        }
        return stack;
    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        ItemStack focus = IWand.getFocus(itemstack);
        if (focus.getItem() instanceof CommandFocus && playerIn.isCrouching()){
            if (CommandFocus.hasServant(focus) && focus.getTag() != null){
                focus.getTag().remove(CommandFocus.TAG_ENTITY);
                playerIn.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                if (!worldIn.isClientSide) {
                    ModNetwork.sendTo(playerIn, new SPlayEntitySoundPacket(playerIn.getUUID(), SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
        } else if (focus.getItem() instanceof OrderFocus && playerIn.isCrouching()){
            if (focus.getTag() != null){
                focus.getTag().remove(OrderFocus.SERVANT_LIST);
                focus.getTag().remove(OrderFocus.SERVANT_CLIENT_LIST);
                playerIn.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                if (!worldIn.isClientSide) {
                    ModNetwork.sendTo(playerIn, new SPlayEntitySoundPacket(playerIn.getUUID(), SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
        } else if (focus.getItem() instanceof CallFocus && playerIn.isCrouching()){
            if (CallFocus.hasSummon(focus) && focus.getTag() != null){
                focus.getTag().remove(CallFocus.TAG_ENTITY);
                playerIn.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                if (!worldIn.isClientSide) {
                    ModNetwork.sendTo(playerIn, new SPlayEntitySoundPacket(playerIn.getUUID(), SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
        } else if (this.getSpell(itemstack) != null) {
            if (this.cannotCast(playerIn, itemstack)){
                return InteractionResultHolder.pass(itemstack);
            } else if (this.isNotInstant(this.getSpell(itemstack), playerIn, itemstack)){
                if (SEHelper.getSoulsAmount(playerIn, this.getSpell(itemstack).soulCost(playerIn, itemstack)) || playerIn.getAbilities().instabuild) {
                    if (!worldIn.isClientSide) {
                        playerIn.startUsingItem(handIn);
                    }
                }
            } else if (this.notTouch(this.getSpell(itemstack))){
                playerIn.swing(handIn);
                ISpell iSpell = GoetyEventFactory.onCastSpell(playerIn, this.getSpell(itemstack));
                this.MagicResults(itemstack, worldIn, playerIn, iSpell);
            }
        }

        return InteractionResultHolder.consume(itemstack);

    }

    public void setSpellConditions(@Nullable ISpell spell, ItemStack stack, LivingEntity livingEntity){
        if (stack.getTag() != null) {
            if (spell != null) {
                stack.getTag().putInt(SOULCOST, spell.soulCost(livingEntity, stack));
                stack.getTag().putInt(DURATION, spell.castDuration(livingEntity, stack));
                if (spell instanceof IChargingSpell chargingSpell) {
                    stack.getTag().putInt(COOLDOWN, chargingSpell.Cooldown(livingEntity, stack, stack.getTag().contains(SHOTS) ? stack.getTag().getInt(SHOTS) : 0));
                } else {
                    stack.getTag().putInt(COOLDOWN, 0);
                }
            } else {
                stack.getTag().putInt(SOULCOST, 0);
                stack.getTag().putInt(DURATION, 0);
                stack.getTag().putInt(COOLDOWN, 0);
            }
        }
    }

    public int SoulCost(ItemStack itemStack) {
        if (itemStack.getTag() == null){
            return 0;
        } else {
            return itemStack.getTag().getInt(SOULCOST);
        }
    }

    public int CastDuration(ItemStack itemStack) {
        if (itemStack.getTag() != null) {
            return itemStack.getTag().getInt(DURATION);
        } else {
            return 0;
        }
    }

    public int Cooldown(ItemStack itemStack) {
        if (itemStack.getTag() != null) {
            return itemStack.getTag().getInt(COOLDOWN);
        } else {
            return 0;
        }
    }

    public int ShotsFired(ItemStack itemStack){
        if (itemStack.getTag() != null) {
            return itemStack.getTag().getInt(SHOTS);
        } else {
            return 0;
        }
    }

    public void increaseShots(ItemStack itemStack){
        if (itemStack.getTag() != null) {
            itemStack.getTag().putInt(SHOTS, ShotsFired(itemStack) + 1);
        }
    }

    public void setShots(ItemStack itemStack, int amount){
        if (itemStack.getTag() != null) {
            itemStack.getTag().putInt(SHOTS, amount);
        }
    }

    @Nullable
    public SoundEvent CastingSound(ItemStack stack, LivingEntity caster) {
        if (this.getSpell(stack) != null) {
            return this.getSpell(stack).CastingSound(caster);
        } else {
            return null;
        }
    }

    public float castingVolume(ItemStack stack){
        if (this.getSpell(stack) != null){
            return this.getSpell(stack).castingVolume();
        } else {
            return 0.5F;
        }
    }

    public float castingPitch(ItemStack stack){
        if (this.getSpell(stack) != null){
            return this.getSpell(stack).castingPitch();
        } else {
            return 1.0F;
        }
    }

    public boolean canCastTouch(ItemStack stack, Level worldIn, LivingEntity caster){
        Player playerEntity = (Player) caster;
        if (!worldIn.isClientSide) {
            ISpell spell = GoetyEventFactory.onTouchBasedSpell(caster, stack, this.getSpell(stack));
            if (spell != null && !this.cannotCast(caster, stack, spell)) {
                if (playerEntity.isCreative()){
                    if (!spell.hasCustomCooldown(caster, stack, IWand.getFocus(stack), spell.spellCooldown(playerEntity))) {
                        SEHelper.addCooldown(playerEntity, IWand.getFocus(stack).getItem(), spell.spellCooldown(playerEntity));
                    }
                    return stack.getTag() != null;
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(caster, stack))) {
                    if (stack.getTag() != null) {
                        SEHelper.decreaseSouls(playerEntity, SoulUse(caster, stack));
                        if (!spell.hasCustomCooldown(caster, stack, IWand.getFocus(stack), spell.spellCooldown(playerEntity))) {
                            SEHelper.addCooldown(playerEntity, IWand.getFocus(stack).getItem(), spell.spellCooldown(playerEntity));
                        }
                        SEHelper.sendSEUpdatePacket(playerEntity);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Deprecated
    public void MagicResults(ItemStack stack, Level worldIn, LivingEntity caster) {
        this.MagicResults(stack, worldIn, caster, this.getSpell(stack));
    }

    public void MagicResults(ItemStack stack, Level worldIn, LivingEntity caster, ISpell spell) {
        if (spell != null && caster instanceof Player playerEntity) {
            if (!worldIn.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) worldIn;
                if (playerEntity.isCreative()) {
                    if (stack.getTag() != null) {
                        spell.SpellResult(serverWorld, caster, stack, WandUtil.getStats(caster, spell));
                        boolean flag = false;
                        if (spell instanceof IChargingSpell chargingSpell) {
                            if (chargingSpell.shotsNumber(playerEntity, stack) > 0 && this.ShotsFired(stack) >= chargingSpell.shotsNumber(playerEntity, stack)) {
                                flag = true;
                            }
                        } else {
                            flag = true;
                        }
                        if (flag) {
                            this.setShots(stack, 0);
                            if (!spell.hasCustomCooldown(caster, stack, IWand.getFocus(stack), spell.spellCooldown(playerEntity))) {
                                SEHelper.addCooldown(playerEntity, IWand.getFocus(stack).getItem(), spell.spellCooldown(playerEntity));
                            }
                        }
                    }
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(caster, stack))) {
                    boolean spent = true;
                    if (spell instanceof IChargingSpell spell1 && spell1.everCharge()) {
                        if (stack.getTag() != null) {
                            stack.getTag().putInt(SECONDS, stack.getTag().getInt(SECONDS) + 1);
                            if (stack.getTag().getInt(SECONDS) != 20) {
                                spent = false;
                            } else {
                                stack.getTag().putInt(SECONDS, 0);
                            }
                        }
                    }
                    if (spent) {
                        SEHelper.decreaseSouls(playerEntity, SoulUse(caster, stack));
                        SEHelper.sendSEUpdatePacket(playerEntity);
                        if (MobsConfig.VillagerHateSpells.get() > 0) {
                            for (Villager villager : caster.level.getEntitiesOfClass(Villager.class, caster.getBoundingBox().inflate(16.0D))) {
                                if (villager.hasLineOfSight(caster)) {
                                    villager.getGossips().add(caster.getUUID(), GossipType.MINOR_NEGATIVE, MobsConfig.VillagerHateSpells.get());
                                }
                            }
                        }
                    }
                    if (stack.getTag() != null) {
                        spell.SpellResult(serverWorld, caster, stack, WandUtil.getStats(caster, spell));
                        boolean flag = false;
                        if (spell instanceof IChargingSpell chargingSpell) {
                            if (chargingSpell.shotsNumber(playerEntity, stack) > 0 && this.ShotsFired(stack) >= chargingSpell.shotsNumber(playerEntity, stack)) {
                                flag = true;
                            }
                        } else {
                            flag = true;
                        }
                        if (flag) {
                            this.setShots(stack, 0);
                            if (!spell.hasCustomCooldown(caster, stack, IWand.getFocus(stack), spell.spellCooldown(playerEntity))) {
                                SEHelper.addCooldown(playerEntity, IWand.getFocus(stack).getItem(), spell.spellCooldown(playerEntity));
                            }
                        }
                    }
                } else {
                    worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            }
            if (worldIn.isClientSide) {
                if (playerEntity.isCreative()) {
                    if (spell instanceof IBreathingSpell breathingSpells) {
                        breathingSpells.showWandBreath(caster, stack, WandUtil.getStats(caster, spell));
                    }
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(caster, stack))) {
                    if (spell instanceof IBreathingSpell breathingSpells) {
                        breathingSpells.showWandBreath(caster, stack, WandUtil.getStats(caster, spell));
                    }
                } else {
                    this.failParticles(worldIn, caster);
                }
            }
        } else {
            this.failParticles(worldIn, caster);
            worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        Player player = Goety.PROXY.getPlayer();
        if (stack.getTag() != null) {
            int SoulUse = stack.getTag().getInt(SOULUSE);
            tooltip.add(Component.translatable("info.goety.wand.cost", SoulUse));
            if (getSpell(stack) != null) {
                if (this.isNotInstant(this.getSpell(stack), player, stack) && !(getSpell(stack) instanceof IChargingSpell)) {
                    int CastTime = stack.getTag().getInt(CASTTIME);
                    tooltip.add(Component.translatable("info.goety.wand.castTime", CastTime / 20.0F));
                }
                if (getSpell(stack).spellCooldown(player) > 0){
                    tooltip.add(Component.translatable("info.goety.wand.coolDown", getSpell(stack).spellCooldown(player) / 20.0F));
                }
            }
        } else {
            tooltip.add(Component.translatable("info.goety.wand.cost", SoulCost(stack)));
        }
        if (!IWand.getFocus(stack).isEmpty()){
            tooltip.add(Component.translatable("info.goety.wand.focus", IWand.getFocus(stack).getItem().getDescription()));
            if (IWand.getFocus(stack).getItem() instanceof RecallFocus){
                ItemStack recallFocus = IWand.getFocus(stack);
                RecallFocus.addRecallText(recallFocus, tooltip);
            }
            ItemHelper.addOnShift(tooltip, () -> addInformationAfterShift(IWand.getFocus(stack).getItem(), tooltip));
        } else {
            tooltip.add(Component.translatable("info.goety.wand.focus", Component.translatable("info.goety.wand.empty")));
            if (ModKeybindings.wandSlot() != null) {
                tooltip.add(Component.translatable("info.goety.wand.open", ModKeybindings.wandSlot().getTranslatedKeyMessage()).withStyle(ChatFormatting.BLUE));
            }
        }
        if (ModKeybindings.wandCircle() != null) {
            tooltip.add(Component.translatable("info.goety.wand.switch", ModKeybindings.wandCircle().getTranslatedKeyMessage()).withStyle(ChatFormatting.BLUE));
        }
    }

    public void addInformationAfterShift(Item item, List<Component> tooltip) {
        tooltip.add(Component.translatable(item.getDescriptionId() + ".info").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new DarkWandClient());
    }

    public static class DarkWandClient implements IClientItemExtensions{

        private static final HumanoidModel.ArmPose SPELL = HumanoidModel.ArmPose.create("GOETY_SPELL", false, (model, entity, arm) -> {
            float f5 = entity.walkAnimation.position(Minecraft.getInstance().getPartialTick());
            if (arm == HumanoidArm.RIGHT) {
                model.rightArm.xRot -= MathHelper.modelDegrees(105);
                model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.25F;
                model.leftArm.xRot += MathHelper.modelDegrees(25);
            } else {
                model.leftArm.xRot -= MathHelper.modelDegrees(105);
                model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.25F;
                model.rightArm.xRot += MathHelper.modelDegrees(25);
            }
        });

        private static final HumanoidModel.ArmPose FLIGHT_POSE = HumanoidModel.ArmPose.create("GOETY_FLYING", false, (model, entity, arm) -> {
            float f5 = 1.0F;
            if (arm == HumanoidArm.RIGHT) {
                model.rightArm.xRot = -MathHelper.modelDegrees(105);
                model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.25F;
                model.leftArm.xRot = MathHelper.modelDegrees(25);
            } else {
                model.leftArm.xRot = -MathHelper.modelDegrees(105);
                model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.25F;
                model.rightArm.xRot = MathHelper.modelDegrees(25);
            }
            model.rightLeg.xRot = MathHelper.modelDegrees(17.5F);
            model.leftLeg.xRot = MathHelper.modelDegrees(17.5F);

            model.rightLeg.xRot += 1.0F * Mth.sin(Minecraft.getInstance().getPartialTick() * 0.067F) * 0.05F;
            model.leftLeg.xRot += -1.0F * Mth.sin(Minecraft.getInstance().getPartialTick() * 0.067F) * 0.05F;
        });

        private static final HumanoidModel.ArmPose HOLD_STAFF = HumanoidModel.ArmPose.create("HOLD_STAFF", false, (model, entity, arm) -> {
            float f5 = entity.walkAnimation.position(Minecraft.getInstance().getPartialTick());
            if (arm == HumanoidArm.RIGHT) {
                model.rightArm.xRot -= MathHelper.modelDegrees(90);
                model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.1F;
            } else {
                model.leftArm.xRot -= MathHelper.modelDegrees(90);
                model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.1F;
            }
        });

        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof IWand) {
                if (entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0) {
                    ISpell spell = WandUtil.getSpell(entityLiving);
                    if (spell != null){
                        return spell.getPose(entityLiving, itemStack, WandUtil.getStats(entityLiving, spell));
                    }
                }
            }
            return HumanoidModel.ArmPose.EMPTY;
        }

        @Override
        public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
            int i = arm == HumanoidArm.RIGHT ? 1 : -1;
            if (player.isUsingItem()) {
                applyItemArmTransform(poseStack, arm, equipProcess);
                poseStack.translate((double)((float)i * -0.2785682F), (double)0.18344387F, (double)0.15731531F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-13.935F));
                poseStack.mulPose(Axis.YP.rotationDegrees((float)i * 35.3F));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float)i * -9.785F));
                float f8 = (float)itemInHand.getUseDuration() - ((float)player.getUseItemRemainingTicks() - partialTick + 1.0F);
                float f12 = f8 / 20.0F;
                f12 = (f12 * f12 + f12 * 2.0F) / 3.0F;
                if (f12 > 1.0F) {
                    f12 = 1.0F;
                }

                if (f12 > 0.1F) {
                    float f15 = Mth.sin((f8 - 0.1F) * 1.3F);
                    float f18 = f12 - 0.1F;
                    float f20 = f15 * f18;
                    poseStack.translate((double)(f20 * 0.0F), (double)(f20 * 0.004F), (double)(f20 * 0.0F));
                }

                poseStack.translate((double)(f12 * 0.0F), (double)(f12 * 0.0F), (double)(f12 * 0.04F));
                poseStack.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
                poseStack.mulPose(Axis.YN.rotationDegrees((float)i * 45.0F));
            } else {
                float f5 = -0.4F * Mth.sin(Mth.sqrt(swingProcess) * (float)Math.PI);
                float f6 = 0.2F * Mth.sin(Mth.sqrt(swingProcess) * ((float)Math.PI * 2F));
                float f10 = -0.2F * Mth.sin(swingProcess * (float)Math.PI);
                poseStack.translate((double)((float)i * f5), (double)f6, (double)f10);
                this.applyItemArmTransform(poseStack, arm, equipProcess);
                this.applyItemArmAttackTransform(poseStack, arm, swingProcess);
            }
            return true;
        }

        private void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float equipProcess) {
            int i = arm == HumanoidArm.RIGHT ? 1 : -1;
            poseStack.translate((double)((float)i * 0.56F), (double)(-0.52F + equipProcess * -0.6F), (double)-0.72F);
        }

        private void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm humanoidArm, float swingProcess) {
            int i = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
            float f = Mth.sin(swingProcess * swingProcess * (float)Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
            float f1 = Mth.sin(Mth.sqrt(swingProcess) * (float)Math.PI);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)i * f1 * -20.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(f1 * -80.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees((float)i * -45.0F));
        }
    }
}
