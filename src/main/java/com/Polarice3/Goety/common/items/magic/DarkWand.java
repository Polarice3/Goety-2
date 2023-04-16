package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.capability.SoulUsingItemCapability;
import com.Polarice3.Goety.common.items.curios.MagicHatItem;
import com.Polarice3.Goety.common.items.curios.MagicRobeItem;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.common.magic.*;
import com.Polarice3.Goety.common.magic.spells.*;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Learned item capabilities from codes made by @vemerion & @MrCrayfish
 */
public class DarkWand extends Item {
    private static final String SOULUSE = "Soul Use";
    private static final String CASTTIME = "Cast Time";
    private static final String SOULCOST = "Soul Cost";
    private static final String DURATION = "Duration";
    private static final String COOLDOWN = "Cooldown";
    private static final String SPELL = "Spell";
    private static final String COOL = "Cool";

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
                compound.putInt(CASTTIME, CastTime(livingEntity, stack));
                compound.putInt(COOL, 0);
            }
            if (getFocus(stack) != null && !getFocus(stack).isEmpty()) {
                this.ChangeFocus(stack);
            } else {
                compound.putInt(SPELL, -1);
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
        compound.putInt(SPELL, -1);
        this.setSpellConditions(null, pStack);
    }

    public boolean SoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicRobeItem);
    }

    public boolean SoulCostUp(LivingEntity entityLiving){
        return entityLiving.hasEffect(ModEffects.SUMMON_DOWN.get());
    }

    public boolean ReduceCastTime(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicHatItem);
    }

    public int SoulCalculation(LivingEntity entityLiving, ItemStack stack){
        if (SoulCostUp(entityLiving)){
            int amp = Objects.requireNonNull(entityLiving.getEffect(ModEffects.SUMMON_DOWN.get())).getAmplifier() + 2;
            return SoulCost(stack) * amp;
        } else if (SoulDiscount(entityLiving)){
            return SoulCost(stack)/2;
        } else {
            return SoulCost(stack);
        }
    }

    public int SoulUse(LivingEntity entityLiving, ItemStack stack){
        if (getFocus(stack).isEnchanted()){
            return SoulCalculation(entityLiving, stack) * 2;
        } else {
            return SoulCalculation(entityLiving, stack);
        }
    }

    public int CastTime(LivingEntity entityLiving, ItemStack stack){
        if (ReduceCastTime(entityLiving)){
            return CastDuration(stack)/2;
        } else {
            return CastDuration(stack);
        }
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
        if (target instanceof Summoned summonedEntity){
            if (summonedEntity.getTrueOwner() == player){
                if (player.isShiftKeyDown() || player.isCrouching()){
                    summonedEntity.kill();
                } else {
                    if (summonedEntity.getMobType() == MobType.UNDEAD) {
                        summonedEntity.updateMoveMode(player);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);

    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (!(this.getSpell(stack) instanceof InstantCastSpells)) {
            SoundEvent soundevent = this.CastingSound(stack);
            int CastTime = stack.getUseDuration() - count;
            if (CastTime == 1) {
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), Objects.requireNonNullElse(soundevent, SoundEvents.EVOKER_PREPARE_ATTACK), SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            if (this.getSpell(stack) instanceof ChargingSpells) {
                if (stack.getTag() != null) {
                    stack.getTag().putInt(COOL, stack.getTag().getInt(COOL) + 1);
                    if (stack.getTag().getInt(COOL) > Cooldown(stack)) {
                        stack.getTag().putInt(COOL, 0);
                        this.MagicResults(stack, worldIn, livingEntityIn);
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
        return UseAnim.BOW;
    }

    @Nonnull
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (!(this.getSpell(stack) instanceof ChargingSpells) || !(this.getSpell(stack) instanceof InstantCastSpells)){
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
            if (!(this.getSpell(itemstack) instanceof InstantCastSpells)){
                playerIn.startUsingItem(handIn);
                if (worldIn.isClientSide){
                    this.useParticles(worldIn, playerIn);
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

    public void ChangeFocus(ItemStack itemStack){
        if (!getFocus(itemStack).isEmpty() && getFocus(itemStack) != null) {
            Item spell = getFocus(itemStack).getItem();
            if (spell == ModItems.VEXING_FOCUS.get()) {
                this.setSpellConditions(new VexSpell(), itemStack);
                this.setSpell(0, itemStack);
            } else if (spell == ModItems.BITING_FOCUS.get()) {
                this.setSpellConditions(new FangSpell(), itemStack);
                this.setSpell(1, itemStack);
            } else if (spell == ModItems.ICEOLOGY_FOCUS.get()) {
                this.setSpellConditions(new IceChunkSpell(), itemStack);
                this.setSpell(2, itemStack);
            } else if (spell == ModItems.ILLUSION_FOCUS.get()) {
                this.setSpellConditions(new IllusionSpell(), itemStack);
                this.setSpell(3, itemStack);
            } else if (spell == ModItems.FEAST_FOCUS.get()) {
                this.setSpellConditions(new FeastSpell(), itemStack);
                this.setSpell(4, itemStack);
            } else if (spell == ModItems.SONIC_BOOM_FOCUS.get()) {
                this.setSpellConditions(new SonicBoomSpell(), itemStack);
                this.setSpell(5, itemStack);
            } else if (spell == ModItems.SOUL_LIGHT_FOCUS.get()) {
                this.setSpellConditions(new SoulLightSpell(), itemStack);
                this.setSpell(6, itemStack);
            } else if (spell == ModItems.GLOW_LIGHT_FOCUS.get()) {
                this.setSpellConditions(new GlowLightSpell(), itemStack);
                this.setSpell(7, itemStack);
            } else if (spell == ModItems.ROTTING_FOCUS.get()) {
                this.setSpellConditions(new ZombieSpell(), itemStack);
                this.setSpell(8, itemStack);
            } else if (spell == ModItems.OSSEOUS_FOCUS.get()) {
                this.setSpellConditions(new SkeletonSpell(), itemStack);
                this.setSpell(9, itemStack);
            } else if (spell == ModItems.SPOOKY_FOCUS.get()) {
                this.setSpellConditions(new WraithSpell(), itemStack);
                this.setSpell(10, itemStack);
            } else if (spell == ModItems.LAUNCH_FOCUS.get()) {
                this.setSpellConditions(new LaunchSpell(), itemStack);
                this.setSpell(11, itemStack);
            } else if (spell == ModItems.SOUL_BOLT_FOCUS.get()) {
                this.setSpellConditions(new SoulBoltSpell(), itemStack);
                this.setSpell(12, itemStack);
            } else if (spell == ModItems.LIGHTNING_FOCUS.get()) {
                this.setSpellConditions(new LightningSpell(), itemStack);
                this.setSpell(13, itemStack);
            } else if (spell == ModItems.SKULL_FOCUS.get()) {
                this.setSpellConditions(new HauntedSkullSpell(), itemStack);
                this.setSpell(14, itemStack);
            } else if (spell == ModItems.FIREBALL_FOCUS.get()) {
                this.setSpellConditions(new FireballSpell(), itemStack);
                this.setSpell(15, itemStack);
            } else if (spell == ModItems.LAVABALL_FOCUS.get()) {
                this.setSpellConditions(new LavaballSpell(), itemStack);
                this.setSpell(16, itemStack);
            } else {
                this.setSpellConditions(null, itemStack);
                this.setSpell(-1, itemStack);
            }
        } else {
            this.setSpellConditions(null, itemStack);
            this.setSpell(-1, itemStack);
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

    public void setSpell(int spell, ItemStack stack) {
        if (stack.getTag() != null) {
            stack.getTag().putInt(SPELL, spell);
        }
    }

    public Spells getSpell(ItemStack stack){
        if (stack.getTag() != null) {
            return new CastSpells(stack.getTag().getInt(SPELL)).getSpell();
        } else {
            return new CastSpells(-1).getSpell();
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

    public void MagicResults(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        Player playerEntity = (Player) entityLiving;
        if (!worldIn.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) worldIn;
            if (this.getSpell(stack) != null) {
                if (playerEntity.isCreative()){
                    if (stack.getTag() != null) {
                        if (stack.getItem() == this.getSpell(stack).getSpellType().getStaff()) {
                            this.getSpell(stack).StaffResult(serverWorld, entityLiving);
                        } else {
                            this.getSpell(stack).WandResult(serverWorld, entityLiving);
                        }
                    }
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(entityLiving, stack))) {
                    boolean spent = true;
                    int random = worldIn.random.nextInt(4);
                    if (this.getSpell(stack) instanceof SpewingSpell) {
                        if (random != 0) {
                            spent = false;
                        }
                    }
                    if (spent){
                        SEHelper.decreaseSouls(playerEntity, SoulUse(entityLiving, stack));
                        SEHelper.sendSEUpdatePacket(playerEntity);
                        if (MainConfig.VillagerHateSpells.get() > 0) {
                            for (Villager villager : entityLiving.level.getEntitiesOfClass(Villager.class, entityLiving.getBoundingBox().inflate(16.0D))) {
                                villager.getGossips().add(entityLiving.getUUID(), GossipType.MINOR_NEGATIVE, MainConfig.VillagerHateSpells.get());
                            }
                        }
                    }
                    if (stack.getTag() != null) {
                        if (stack.getItem() == this.getSpell(stack).getSpellType().getStaff()) {
                            this.getSpell(stack).StaffResult(serverWorld, entityLiving);
                        } else {
                            this.getSpell(stack).WandResult(serverWorld, entityLiving);
                        }
                    }
                } else {
                    worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            } else {
                worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
        if (worldIn.isClientSide){
            if (this.getSpell(stack) != null) {
                if (playerEntity.isCreative()){
                    if (this.getSpell(stack) instanceof SpewingSpell spewingSpell){
                        spewingSpell.showWandBreath(entityLiving);
                    }
                } else if (SEHelper.getSoulsAmount(playerEntity, SoulUse(entityLiving, stack))) {
                    if (this.getSpell(stack) instanceof SpewingSpell spewingSpell){
                        spewingSpell.showWandBreath(entityLiving);
                    }
                } else {
                    this.failParticles(worldIn, entityLiving);
                }
            } else {
                this.failParticles(worldIn, entityLiving);
            }
        }
    }

    public void failParticles(Level worldIn, LivingEntity entityLiving){
        for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
            double d = worldIn.random.nextGaussian() * 0.2D;
            worldIn.addParticle(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), d, d, d);
        }
    }

    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag result = new CompoundTag();
        CompoundTag tag = super.getShareTag(stack);
        CompoundTag cap = SoulUsingItemHandler.get(stack).serializeNBT();
        if (tag != null) {
            result.put("tag", tag);
        }
        if (cap != null) {
            result.put("cap", cap);
        }
        return result;
    }

    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if (nbt != null) {
            if (nbt.contains("tag")) {
                stack.setTag(nbt.getCompound("tag"));
            }
            if (nbt.contains("cap")) {
                SoulUsingItemHandler.get(stack).deserializeNBT(nbt.getCompound("cap"));
            }
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
        } else {
            tooltip.add(Component.translatable("info.goety.wand.cost", SoulCost(stack)));
        }
        if (!getFocus(stack).isEmpty()){
            tooltip.add(Component.translatable("info.goety.wand.focus", getFocus(stack).getItem().getDescription()));
        } else {
            tooltip.add(Component.translatable("info.goety.wand.focus", "Empty"));
        }
    }
}
