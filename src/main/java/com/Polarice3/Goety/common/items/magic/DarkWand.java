package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.capability.SoulUsingItemCapability;
import com.Polarice3.Goety.common.items.curios.MagicHatItem;
import com.Polarice3.Goety.common.items.curios.MagicRobeItem;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.common.magic.*;
import com.Polarice3.Goety.common.magic.spells.RecallSpell;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
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

    public DarkWand() {
        super(new Properties().stacksTo(1).setNoRepair().rarity(Rarity.RARE));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            CompoundTag compound = stack.getOrCreateTag();
            if (stack.getTag() == null) {
                compound.putInt(SOULUSE, SoulUse(livingEntity, stack));
                compound.putInt(SOULCOST, 0);
                compound.putInt(CASTTIME, CastTime(livingEntity, stack));
                compound.putInt(COOL, 0);
                compound.putInt(SECONDS, 0);
            }
            if (this.getSpell(stack) != null) {
                this.setSpellConditions(this.getSpell(stack), stack);
            } else {
                this.setSpellConditions(null, stack);
            }
            compound.putInt(SOULUSE, SoulUse(livingEntity, stack));
            compound.putInt(CASTTIME, CastTime(livingEntity, stack));
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CompoundTag compound = pStack.getOrCreateTag();
        compound.putInt(SOULUSE, SoulUse(pPlayer, pStack));
        compound.putInt(SOULCOST, 0);
        compound.putInt(CASTTIME, CastTime(pPlayer, pStack));
        compound.putInt(COOL, 0);
        compound.putInt(SECONDS, 0);
        this.setSpellConditions(null, pStack);
    }

    public boolean SoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicRobeItem);
    }

    public boolean FrostSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, ModItems.FROST_ROBE.get());
    }

    public boolean WindSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, ModItems.WIND_ROBE.get());
    }

    public boolean GeoSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, ModItems.AMETHYST_NECKLACE.get());
    }

    public boolean SoulCostUp(LivingEntity entityLiving){
        return entityLiving.hasEffect(GoetyEffects.SUMMON_DOWN.get());
    }

    public boolean ReduceCastTime(LivingEntity entityLiving, ItemStack stack){
        if (getSpell(stack) != null && getSpell(stack).getSpellType() == Spells.SpellType.NECROMANCY){
            return CuriosFinder.hasCurio(entityLiving, ModItems.NECRO_CROWN.get());
        } else {
            return CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicHatItem);
        }
    }

    public int SoulCalculation(LivingEntity entityLiving, ItemStack stack){
        if (SoulCostUp(entityLiving)){
            int amp = Objects.requireNonNull(entityLiving.getEffect(GoetyEffects.SUMMON_DOWN.get())).getAmplifier() + 2;
            return SoulCost(stack) * amp;
        } else if (SoulDiscount(entityLiving)){
            return SoulCost(stack)/2;
        } else if (FrostSoulDiscount(entityLiving) && this.getSpell(stack) != null && this.getSpell(stack).getSpellType() == Spells.SpellType.FROST){
            return SoulCost(stack)/2;
        } else if (WindSoulDiscount(entityLiving) && this.getSpell(stack) != null && this.getSpell(stack).getSpellType() == Spells.SpellType.WIND){
            return SoulCost(stack)/2;
        } else if (GeoSoulDiscount(entityLiving) && this.getSpell(stack) != null && this.getSpell(stack).getSpellType() == Spells.SpellType.GEOMANCY){
            return SoulCost(stack)/2;
        } else {
            return SoulCost(stack);
        }
    }

    public int SoulUse(LivingEntity entityLiving, ItemStack stack){
        if (getFocus(stack).isEnchanted()){
            return (int) (SoulCalculation(entityLiving, stack) * 2 * SEHelper.soulDiscount(entityLiving));
        } else {
            return (int) (SoulCalculation(entityLiving, stack) * SEHelper.soulDiscount(entityLiving));
        }
    }

    public int CastTime(LivingEntity entityLiving, ItemStack stack){
        if (ReduceCastTime(entityLiving, stack)){
            return CastDuration(stack)/2;
        } else {
            return CastDuration(stack);
        }
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
        if (!SpellConfig.OwnerHitCommand.get()) {
            if (target instanceof Summoned summonedEntity) {
                if (summonedEntity.getTrueOwner() == player || (summonedEntity.getTrueOwner() instanceof Owned owned && owned.getTrueOwner() == player)) {
                    if (player.isShiftKeyDown() || player.isCrouching()) {
                        summonedEntity.kill();
                    } else {
                        if (summonedEntity.canUpdateMove()) {
                            summonedEntity.updateMoveMode(player);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (this.getSpell(stack) instanceof TouchSpells touchSpells){
            if (this.canCastTouch(stack, player.level, player)) {
                if (player.level instanceof ServerLevel serverLevel) {
                    touchSpells.touchResult(serverLevel, player, target);
                }
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);

    }

    public InteractionResult useOn(UseOnContext p_220235_) {
        Level level = p_220235_.getLevel();
        BlockPos blockpos = p_220235_.getClickedPos();
        Player player = p_220235_.getPlayer();
        ItemStack itemstack = p_220235_.getItemInHand();
        if (player != null) {
            if (!level.isClientSide) {
                if (level.getBlockEntity(blockpos) instanceof BrewCauldronBlockEntity cauldronBlock) {
                    if (MobUtil.isShifting(player)) {
                        if (itemstack.getItem() instanceof DarkWand){
                            cauldronBlock.fullReset();
                            level.playSound(null, blockpos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                            level.playSound(null, blockpos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return super.useOn(p_220235_);
    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (!(this.getSpell(stack) instanceof InstantCastSpells) && !SpellConfig.WandCooldown.get()) {
            SoundEvent soundevent = this.CastingSound(stack);
            int CastTime = stack.getUseDuration() - count;
            if (CastTime == 1) {
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), Objects.requireNonNullElse(soundevent, SoundEvents.EVOKER_PREPARE_ATTACK), SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            if (this.getSpell(stack) instanceof RecallSpell){
                for(int i = 0; i < 2; ++i) {
                    worldIn.addParticle(ParticleTypes.PORTAL, livingEntityIn.getRandomX(0.5D), livingEntityIn.getRandomY() - 0.25D, livingEntityIn.getRandomZ(0.5D), (worldIn.random.nextDouble() - 0.5D) * 2.0D, -worldIn.random.nextDouble(), (worldIn.random.nextDouble() - 0.5D) * 2.0D);
                }
            }
            if (this.getSpell(stack) instanceof ChargingSpells) {
                if (stack.getTag() != null) {
                    stack.getTag().putInt(COOL, stack.getTag().getInt(COOL) + 1);
                    if (stack.getTag().getInt(COOL) > Cooldown(stack)) {
                        stack.getTag().putInt(COOL, 0);
                        this.MagicResults(stack, worldIn, livingEntityIn);
                    }
                }

                if (livingEntityIn instanceof Player player){
                    if (!SEHelper.getSoulsAmount(player, this.getSpell(stack).SoulCost()) && !player.isCreative()){
                        player.stopUsingItem();
                    }
                }
            }
        }
    }

    public int getUseDuration(ItemStack stack) {
        if (!SpellConfig.WandCooldown.get()) {
            if (stack.getTag() != null) {
                return stack.getTag().getInt(CASTTIME);
            } else {
                return this.CastDuration(stack);
            }
        } else {
            return 0;
        }
    }

    @Nonnull
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Nonnull
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (!(this.getSpell(stack) instanceof ChargingSpells) || !(this.getSpell(stack) instanceof InstantCastSpells) || SpellConfig.WandCooldown.get()){
            this.MagicResults(stack, worldIn, entityLiving);
        }
        if (stack.getTag() != null) {
            if (stack.getTag().getInt(COOL) > 0) {
                stack.getTag().putInt(COOL, 0);
            }
        }
        return stack;
    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (this.getSpell(itemstack) != null) {
            if (!(this.getSpell(itemstack) instanceof InstantCastSpells) && !SpellConfig.WandCooldown.get()){
                if (SEHelper.getSoulsAmount(playerIn, this.getSpell(itemstack).SoulCost()) || playerIn.getAbilities().instabuild) {
                    playerIn.startUsingItem(handIn);
                    if (worldIn.isClientSide) {
                        this.useParticles(worldIn, playerIn);
                    }
                }
            } else {
                playerIn.swing(handIn);
                this.MagicResults(itemstack, worldIn, playerIn);
            }
        }

        return InteractionResultHolder.consume(itemstack);

    }

    public void useParticles(Level worldIn, Player playerIn){
        for (int i = 0; i < playerIn.level.random.nextInt(35) + 10; ++i) {
            double d = worldIn.random.nextGaussian() * 0.2D;
            worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, playerIn.getX(), playerIn.getBoundingBox().maxY + 0.5D, playerIn.getZ(), d, d, d);
        }
    }

    public void setSpellConditions(@Nullable Spells spell, ItemStack stack){
        if (stack.getTag() != null) {
            if (spell != null) {
                stack.getTag().putInt(SOULCOST, spell.SoulCost());
                stack.getTag().putInt(DURATION, spell.CastDuration());
                if (spell instanceof ChargingSpells) {
                    stack.getTag().putInt(COOLDOWN, ((ChargingSpells) spell).Cooldown());
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

    public Spells getSpell(ItemStack stack){
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

    @Nullable
    public SoundEvent CastingSound(ItemStack stack) {
        if (this.getSpell(stack) != null) {
            return this.getSpell(stack).CastingSound();
        } else {
            return null;
        }
    }

    public static ItemStack getFocus(ItemStack itemstack) {
        SoulUsingItemHandler handler = SoulUsingItemHandler.get(itemstack);
        return handler.getSlot();
    }

    public static MagicFocus getMagicFocus(ItemStack itemStack){
        if (getFocus(itemStack) != null && !getFocus(itemStack).isEmpty() && getFocus(itemStack).getItem() instanceof MagicFocus magicFocus){
            return magicFocus;
        } else {
            return null;
        }
    }

    public Item getStaff(ItemStack stack){
        return this.getSpell(stack).getSpellType().getStaff();
    }

    public boolean hasAppropriateStaff(ItemStack stack){
        if (this.getStaff(stack) != null) {
            if (this.getSpell(stack).getSpellType() == Spells.SpellType.NECROMANCY){
                return stack.getItem() == this.getStaff(stack) || stack.getItem() == ModItems.NAMELESS_STAFF.get();
            } else {
                return stack.getItem() == this.getStaff(stack);
            }
        } else {
            return false;
        }
    }

    public boolean canCastTouch(ItemStack stack, Level worldIn, LivingEntity caster){
        Player playerEntity = (Player) caster;
        if (!worldIn.isClientSide) {
            if (this.getSpell(stack) != null) {
                if (playerEntity.isCreative()){
                    return stack.getTag() != null;
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(caster, stack))) {
                    if (stack.getTag() != null) {
                        SEHelper.decreaseSouls(playerEntity, SoulUse(caster, stack));
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
        if (!worldIn.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) worldIn;
            if (this.getSpell(stack) != null) {
                if (playerEntity.isCreative()){
                    if (stack.getTag() != null) {
                        if (hasAppropriateStaff(stack)) {
                            this.getSpell(stack).StaffResult(serverWorld, caster);
                        } else {
                            this.getSpell(stack).RegularResult(serverWorld, caster);
                        }
                        if (SpellConfig.WandCooldown.get()){
                            playerEntity.getCooldowns().addCooldown(stack.getItem(), this.CastTime(playerEntity, stack));
                        }
                    }
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(caster, stack))) {
                    boolean spent = true;
                    if (this.getSpell(stack) instanceof EverChargeSpells) {
                        if (stack.getTag() != null) {
                            stack.getTag().putInt(SECONDS, stack.getTag().getInt(SECONDS) + 1);
                            if (stack.getTag().getInt(SECONDS) != 20){
                                spent = false;
                            } else {
                                stack.getTag().putInt(SECONDS, 0);
                            }
                        }
                    }
                    if (spent){
                        SEHelper.decreaseSouls(playerEntity, SoulUse(caster, stack));
                        SEHelper.sendSEUpdatePacket(playerEntity);
                        if (MainConfig.VillagerHateSpells.get() > 0) {
                            for (Villager villager : caster.level.getEntitiesOfClass(Villager.class, caster.getBoundingBox().inflate(16.0D))) {
                                if (villager.hasLineOfSight(caster)) {
                                    villager.getGossips().add(caster.getUUID(), GossipType.MINOR_NEGATIVE, MainConfig.VillagerHateSpells.get());
                                }
                            }
                        }
                    }
                    if (stack.getTag() != null) {
                        if (hasAppropriateStaff(stack)) {
                            this.getSpell(stack).StaffResult(serverWorld, caster);
                        } else {
                            this.getSpell(stack).RegularResult(serverWorld, caster);
                        }
                        if (SpellConfig.WandCooldown.get()){
                            playerEntity.getCooldowns().addCooldown(stack.getItem(), this.CastTime(playerEntity, stack));
                        }
                    }
                } else {
                    worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            } else {
                worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
        if (worldIn.isClientSide){
            if (this.getSpell(stack) != null) {
                if (playerEntity.isCreative()){
                    if (this.getSpell(stack) instanceof BreathingSpells breathingSpells){
                        breathingSpells.showWandBreath(caster);
                    }
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(caster, stack))) {
                    if (this.getSpell(stack) instanceof BreathingSpells breathingSpells){
                        breathingSpells.showWandBreath(caster);
                    }
                } else {
                    this.failParticles(worldIn, caster);
                }
            } else {
                this.failParticles(worldIn, caster);
            }
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
                if (!(getSpell(stack) instanceof InstantCastSpells) && !(getSpell(stack) instanceof ChargingSpells)) {
                    int CastTime = stack.getTag().getInt(CASTTIME);
                    if (SpellConfig.WandCooldown.get()) {
                        tooltip.add(Component.translatable("info.goety.wand.coolDown", CastTime / 20.0F));
                    } else {
                        tooltip.add(Component.translatable("info.goety.wand.castTime", CastTime / 20.0F));
                    }
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

            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                if (!itemStack.isEmpty()) {
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
        });
    }
}
