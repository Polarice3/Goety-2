package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.*;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.entities.neutral.AbstractVine;
import com.Polarice3.Goety.common.events.GoetyEventFactory;
import com.Polarice3.Goety.common.items.capability.SoulUsingItemCapability;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.common.magic.spells.void_spells.RecallSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayEntitySoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Learned item capabilities from codes made by @vemerion & @MrCrayfish
 */
public class DarkWand extends Item {
    private static final String SOULUSE = "Soul Use";
    private static final String CASTTIME = "Cast Time";
    private static final String SOULCOST = "Soul Cost";
    private static final String DURATION = "Duration";
    private static final String COOLDOWN = "Cooldown";
    private static final String COOL = "Cool";
    private static final String SECONDS = "Seconds";
    private static final String SHOTS = "Shots";

    public DarkWand() {
        super(new Properties().tab(Goety.TAB).stacksTo(1).setNoRepair().rarity(Rarity.RARE));
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
            if (getFocus(stack) != null){
                getFocus(stack).inventoryTick(worldIn, entityIn, itemSlot, isSelected);
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
        if (getFocus(stack).isEnchanted()){
            return (int) (SoulCost(stack) * 2 * SEHelper.soulDiscount(entityLiving));
        } else {
            return (int) (SoulCost(stack) * SEHelper.soulDiscount(entityLiving));
        }
    }

    public boolean cannotCast(LivingEntity livingEntity, ItemStack stack){
        boolean flag = false;
        if (livingEntity.level instanceof ServerLevel serverLevel){
            if (this.getSpell(stack) != null){
                if (!this.getSpell(stack).conditionsMet(serverLevel, livingEntity)){
                    flag = true;
                }
            }
        }
        return this.isOnCooldown(livingEntity, stack) || flag;
    }

    public boolean isOnCooldown(LivingEntity livingEntity, ItemStack stack){
        if (livingEntity instanceof Player player){
            if (getFocus(stack) != null){
                Item item = getFocus(stack).getItem();
                return SEHelper.getFocusCoolDown(player).isOnCooldown(item);
            }
        }
        return false;
    }

    public boolean isNotInstant(ISpell spells){
        return spells != null && spells.defaultCastDuration() > 0;
    }

    public boolean notTouch(ISpell spells){
        return !(spells instanceof ITouchSpell) && !(spells instanceof IBlockSpell);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level.isClientSide) {
            if (entity instanceof LivingEntity target && target instanceof IOwned owned && (owned.getTrueOwner() == player || (owned.getTrueOwner() instanceof IOwned owned1 && owned1.getTrueOwner() == player))) {
                if (getFocus(stack).getItem() instanceof CallFocus && !CallFocus.hasSummon(getFocus(stack))) {
                    CompoundTag compoundTag = new CompoundTag();
                    if (getFocus(stack).hasTag()) {
                        compoundTag = getFocus(stack).getTag();
                    }
                    CallFocus.setSummon(compoundTag, target);
                    getFocus(stack).setTag(compoundTag);
                    player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                    return true;
                } else if (getFocus(stack).getItem() instanceof CommandFocus && owned instanceof IServant && !CommandFocus.hasServant(getFocus(stack))) {
                    CompoundTag compoundTag = new CompoundTag();
                    if (getFocus(stack).hasTag()) {
                        compoundTag = getFocus(stack).getTag();
                    }
                    CommandFocus.setServant(compoundTag, target);
                    getFocus(stack).setTag(compoundTag);
                    player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                    return true;
                } else {
                    if (SpellConfig.OwnerHitCommand.get()) {
                        if (owned instanceof IServant summonedEntity) {
                            if (player.isShiftKeyDown() || player.isCrouching()) {
                                entity.kill();
                                return true;
                            } else {
                                if (summonedEntity.canUpdateMove()) {
                                    summonedEntity.updateMoveMode(player);
                                    return true;
                                }
                            }
                        } else if (owned instanceof AbstractVine vine){
                            if (player.isShiftKeyDown() || player.isCrouching()) {
                                vine.kill();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
        if (getFocus(stack).getItem() instanceof CommandFocus) {
            if (CommandFocus.getServant(getFocus(stack)) instanceof IServant summoned && summoned != target){
                if (summoned.getTrueOwner() == player && target.distanceTo(player) <= 64){
                    summoned.setCommandPosEntity(target);
                    player.playSound(ModSounds.COMMAND.get(), 1.0F, 0.45F);
                    if (!player.level.isClientSide) {
                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.COMMAND.get(), 1.0F, 0.45F));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (!SpellConfig.OwnerHitCommand.get()) {
            if (target instanceof IServant summonedEntity) {
                if (summonedEntity.getTrueOwner() == player || (summonedEntity.getTrueOwner() instanceof IOwned owned && owned.getTrueOwner() == player)) {
                    if (player.isShiftKeyDown() || player.isCrouching()) {
                        target.kill();
                    } else {
                        if (summonedEntity.canUpdateMove()) {
                            summonedEntity.updateMoveMode(player);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (this.getSpell(stack) instanceof ITouchSpell touchSpells){
            if (this.canCastTouch(stack, player.level, player)) {
                if (player.level instanceof ServerLevel serverLevel) {
                    touchSpells.touchResult(serverLevel, player, target);
                }
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);

    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();
        if (player != null) {
            if (getFocus(stack).getItem() instanceof RecallFocus recallFocus){
                CompoundTag compoundTag = getFocus(stack).getOrCreateTag();
                if (!RecallFocus.hasRecall(getFocus(stack))){
                    BlockEntity tileEntity = level.getBlockEntity(blockpos);
                    if (tileEntity instanceof ArcaBlockEntity arcaTile) {
                        if (pContext.getPlayer() == arcaTile.getPlayer() && arcaTile.getLevel() != null) {
                            recallFocus.addRecallTags(arcaTile.getLevel().dimension(), arcaTile.getBlockPos(), compoundTag);
                            getFocus(stack).setTag(compoundTag);
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
                        getFocus(stack).setTag(compoundTag);
                        player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                        if (!level.isClientSide) {
                            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            } else if (getFocus(stack).getItem() instanceof CommandFocus){
                if (CommandFocus.hasServant(getFocus(stack)) && CommandFocus.getServant(getFocus(stack)) instanceof IServant summoned){
                    LivingEntity livingEntity = CommandFocus.getServant(getFocus(stack));
                    if (livingEntity != null) {
                        if (summoned.getTrueOwner() == player && livingEntity.distanceTo(player) <= 64) {
                            BlockPos above = blockpos.above();
                            boolean flag = false;
                            if (!level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
                                summoned.setCommandPos(blockpos);
                                flag = true;
                            } else if (!level.getBlockState(above).isSolidRender(level, above)) {
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
            } else if (this.getSpell(stack) instanceof IBlockSpell blockSpells){
                if (player.level instanceof ServerLevel serverLevel) {
                    if (blockSpells.rightBlock(serverLevel, player, blockpos)) {
                        if (this.canCastTouch(stack, level, player)) {
                            blockSpells.blockResult(serverLevel, player, blockpos);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            if (!level.isClientSide) {
                if (level.getBlockEntity(blockpos) instanceof BrewCauldronBlockEntity cauldronBlock) {
                    if (MobUtil.isShifting(player)) {
                        if (stack.getItem() instanceof DarkWand){
                            cauldronBlock.fullReset();
                            level.playSound(null, blockpos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                            level.playSound(null, blockpos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return super.useOn(pContext);
    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (this.cannotCast(livingEntityIn, stack)){
            return;
        }
        int CastTime = stack.getUseDuration() - count;
        if (this.getSpell(stack) != null && this.isNotInstant(this.getSpell(stack))) {
            SoundEvent soundevent = this.CastingSound(stack);
            if (CastTime == 1 && soundevent != null) {
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), soundevent, SoundSource.PLAYERS, this.castingVolume(stack), 1.0F);
            }
            if (this.getSpell(stack) instanceof RecallSpell){
                for(int i = 0; i < 2; ++i) {
                    worldIn.addParticle(ParticleTypes.PORTAL, livingEntityIn.getRandomX(0.5D), livingEntityIn.getRandomY() - 0.25D, livingEntityIn.getRandomZ(0.5D), (worldIn.random.nextDouble() - 0.5D) * 2.0D, -worldIn.random.nextDouble(), (worldIn.random.nextDouble() - 0.5D) * 2.0D);
                }
            } else {
                if (this.getSpell(stack) instanceof IChargingSpell spell
                        && spell.defaultCastUp() > 0){
                    this.useParticles(worldIn, livingEntityIn, this.getSpell(stack));
                } else if (!(this.getSpell(stack) instanceof IChargingSpell)) {
                    this.useParticles(worldIn, livingEntityIn, this.getSpell(stack));
                }
            }
            if (this.getSpell(stack) instanceof IChargingSpell spell) {
                if (stack.getTag() != null) {
                    if (CastTime == spell.defaultCastUp() || spell.defaultCastUp() <= 0) {
                        stack.getTag().putInt(COOL, stack.getTag().getInt(COOL) + 1);
                        if (stack.getTag().getInt(COOL) > Cooldown(stack)) {
                            stack.getTag().putInt(COOL, 0);
                            if (spell.shotsNumber() > 0){
                                this.increaseShots(stack);
                            }
                            this.MagicResults(stack, worldIn, livingEntityIn);
                        }
                    }
                }

                if (livingEntityIn instanceof Player player){
                    if (!SEHelper.getSoulsAmount(player, this.getSpell(stack).soulCost(player)) && !player.isCreative()){
                        player.stopUsingItem();
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
        if (this.getSpell(stack) != null) {
            if (!(this.getSpell(stack) instanceof IChargingSpell) || this.isNotInstant(this.getSpell(stack)) || this.notTouch(this.getSpell(stack))) {
                this.MagicResults(stack, worldIn, entityLiving);
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
        if (getFocus(itemstack).getItem() instanceof CommandFocus && playerIn.isCrouching()){
            if (CommandFocus.hasServant(getFocus(itemstack)) && getFocus(itemstack).getTag() != null){
                getFocus(itemstack).getTag().remove(CommandFocus.TAG_ENTITY);
                playerIn.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                if (!worldIn.isClientSide) {
                    ModNetwork.sendTo(playerIn, new SPlayEntitySoundPacket(playerIn.getUUID(), SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
        } else if (this.getSpell(itemstack) != null) {
            if (this.cannotCast(playerIn, itemstack)){
                return InteractionResultHolder.pass(itemstack);
            } else if (this.isNotInstant(this.getSpell(itemstack))){
                if (SEHelper.getSoulsAmount(playerIn, this.getSpell(itemstack).soulCost(playerIn)) || playerIn.getAbilities().instabuild) {
                    if (!worldIn.isClientSide) {
                        playerIn.startUsingItem(handIn);
                    }
                }
            } else if (this.notTouch(this.getSpell(itemstack))){
                playerIn.swing(handIn);
                this.MagicResults(itemstack, worldIn, playerIn);
            }
        }

        return InteractionResultHolder.consume(itemstack);

    }

    public void useParticles(Level worldIn, LivingEntity livingEntity, ISpell iSpell){
        double d0 = worldIn.random.nextGaussian() * 0.2D;
        double d1 = worldIn.random.nextGaussian() * 0.2D;
        double d2 = worldIn.random.nextGaussian() * 0.2D;
        if (iSpell != null){
            d0 = iSpell.particleColors(livingEntity).red();
            d1 = iSpell.particleColors(livingEntity).green();
            d2 = iSpell.particleColors(livingEntity).blue();
        }
        worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, livingEntity.getX(), livingEntity.getBoundingBox().maxY + 0.5D, livingEntity.getZ(), d0, d1, d2);
    }

    public void setSpellConditions(@Nullable ISpell spell, ItemStack stack, LivingEntity livingEntity){
        if (stack.getTag() != null) {
            if (spell != null) {
                stack.getTag().putInt(SOULCOST, spell.soulCost(livingEntity));
                stack.getTag().putInt(DURATION, spell.castDuration(livingEntity));
                if (spell instanceof IChargingSpell) {
                    stack.getTag().putInt(COOLDOWN, ((IChargingSpell) spell).Cooldown());
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

    public ISpell getSpell(ItemStack stack){
        if (getMagicFocus(stack) != null && getMagicFocus(stack).getSpell() != null){
            return getMagicFocus(stack).getSpell();
        } else {
            return null;
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
    public SoundEvent CastingSound(ItemStack stack) {
        if (this.getSpell(stack) != null) {
            return this.getSpell(stack).CastingSound();
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

    public static ItemStack getFocus(ItemStack itemstack) {
        SoulUsingItemHandler handler = SoulUsingItemHandler.get(itemstack);
        return handler.getSlot();
    }

    public static IFocus getMagicFocus(ItemStack itemStack){
        if (getFocus(itemStack) != null && !getFocus(itemStack).isEmpty() && getFocus(itemStack).getItem() instanceof IFocus magicFocus){
            return magicFocus;
        } else {
            return null;
        }
    }

    public boolean canCastTouch(ItemStack stack, Level worldIn, LivingEntity caster){
        Player playerEntity = (Player) caster;
        if (!worldIn.isClientSide) {
            if (this.getSpell(stack) != null && !this.cannotCast(caster, stack)) {
                if (playerEntity.isCreative()){
                    SEHelper.addCooldown(playerEntity, getFocus(stack).getItem(), this.getSpell(stack).spellCooldown());
                    return stack.getTag() != null;
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(caster, stack))) {
                    if (stack.getTag() != null) {
                        SEHelper.decreaseSouls(playerEntity, SoulUse(caster, stack));
                        SEHelper.addCooldown(playerEntity, getFocus(stack).getItem(), this.getSpell(stack).spellCooldown());
                        SEHelper.sendSEUpdatePacket(playerEntity);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void MagicResults(ItemStack stack, Level worldIn, LivingEntity caster) {
        Player playerEntity = (Player) caster;
        if (this.getSpell(stack) != null) {
            ISpell spell = GoetyEventFactory.onCastSpell(caster, this.getSpell(stack));
            if (spell != null) {
                if (!worldIn.isClientSide) {
                    ServerLevel serverWorld = (ServerLevel) worldIn;
                    if (playerEntity.isCreative()) {
                        if (stack.getTag() != null) {
                            spell.SpellResult(serverWorld, caster, stack);
                            boolean flag = false;
                            if (spell instanceof IChargingSpell chargingSpell && chargingSpell.shotsNumber() > 0){
                                if (this.ShotsFired(stack) >= chargingSpell.shotsNumber()){
                                    flag = true;
                                }
                            } else {
                                flag = true;
                            }
                            if (flag) {
                                this.setShots(stack, 0);
                                SEHelper.addCooldown(playerEntity, getFocus(stack).getItem(), spell.spellCooldown());
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
                            spell.SpellResult(serverWorld, caster, stack);
                            boolean flag = false;
                            if (spell instanceof IChargingSpell chargingSpell && chargingSpell.shotsNumber() > 0){
                                if (this.ShotsFired(stack) >= chargingSpell.shotsNumber()){
                                    flag = true;
                                }
                            } else {
                                flag = true;
                            }
                            if (flag) {
                                this.setShots(stack, 0);
                                SEHelper.addCooldown(playerEntity, getFocus(stack).getItem(), spell.spellCooldown());
                            }
                        }
                    } else {
                        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                }
                if (worldIn.isClientSide) {
                    if (playerEntity.isCreative()) {
                        if (spell instanceof IBreathingSpell breathingSpells) {
                            breathingSpells.showWandBreath(caster);
                        }
                    } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(caster, stack))) {
                        if (spell instanceof IBreathingSpell breathingSpells) {
                            breathingSpells.showWandBreath(caster);
                        }
                    } else {
                        this.failParticles(worldIn, caster);
                    }
                }
            } else {
                this.failParticles(worldIn, caster);
                worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        } else {
            this.failParticles(worldIn, caster);
            worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    public void failParticles(Level worldIn, LivingEntity entityLiving){
        for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
            double d = worldIn.random.nextGaussian() * 0.2D;
            worldIn.addParticle(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), d, d, d);
        }
    }

    /**
     * Found Creative Server Bug fix from @mraof's Minestuck Music Player Weapon code.
     */
    private static IItemHandler getItemHandler(ItemStack itemStack) {
        return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() ->
                new IllegalArgumentException("Expected an item handler for the Magic Focus item, but " + itemStack + " does not expose an item handler."));
    }

    public CompoundTag getShareTag(ItemStack stack) {
        IItemHandler iitemHandler = getItemHandler(stack);
        CompoundTag nbt = stack.getTag() != null ? stack.getTag() : new CompoundTag();
        if(iitemHandler instanceof ItemStackHandler itemHandler) {
            nbt.put("cap", itemHandler.serializeNBT());
        }
        return nbt;
    }

    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if(nbt == null) {
            stack.setTag(null);
        } else {
            IItemHandler iitemHandler = getItemHandler(stack);
            if(iitemHandler instanceof ItemStackHandler itemHandler)
                itemHandler.deserializeNBT(nbt.getCompound("cap"));
            stack.setTag(nbt);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt) {
        return new SoulUsingItemCapability(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getTag() != null) {
            int SoulUse = stack.getTag().getInt(SOULUSE);
            tooltip.add(Component.translatable("info.goety.wand.cost", SoulUse));
            if (getSpell(stack) != null) {
                if (this.isNotInstant(getSpell(stack)) && !(getSpell(stack) instanceof IChargingSpell)) {
                    int CastTime = stack.getTag().getInt(CASTTIME);
                    tooltip.add(Component.translatable("info.goety.wand.castTime", CastTime / 20.0F));
                }
                if (getSpell(stack).spellCooldown() > 0){
                    tooltip.add(Component.translatable("info.goety.wand.coolDown", getSpell(stack).spellCooldown() / 20.0F));
                }
            }
        } else {
            tooltip.add(Component.translatable("info.goety.wand.cost", SoulCost(stack)));
        }
        if (!getFocus(stack).isEmpty()){
            tooltip.add(Component.translatable("info.goety.wand.focus", getFocus(stack).getItem().getDescription()));
            if (getFocus(stack).getItem() instanceof RecallFocus){
                ItemStack recallFocus = getFocus(stack);
                RecallFocus.addRecallText(recallFocus, tooltip);
            }
        } else {
            tooltip.add(Component.translatable("info.goety.wand.focus", "Empty"));
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {

            private static final HumanoidModel.ArmPose WAND_POSE = HumanoidModel.ArmPose.create("WAND", false, (model, entity, arm) -> {
                float f5 = entity.animationPosition - entity.animationSpeed * (1.0F - Minecraft.getInstance().getPartialTick());
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

            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof DarkWand) {
                    if (entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0) {
                        return WAND_POSE;
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
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-13.935F));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees((float)i * 35.3F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)i * -9.785F));
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
                    poseStack.mulPose(Vector3f.YN.rotationDegrees((float)i * 45.0F));
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
                poseStack.mulPose(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
                float f1 = Mth.sin(Mth.sqrt(swingProcess) * (float)Math.PI);
                poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0F));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(f1 * -80.0F));
                poseStack.mulPose(Vector3f.YP.rotationDegrees((float)i * -45.0F));
            }
        });
    }
}
