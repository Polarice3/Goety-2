package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import com.Polarice3.Goety.common.crafting.BrewingRecipe;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.BrewEffects;
import com.Polarice3.Goety.common.effects.brew.PotionBrewEffect;
import com.Polarice3.Goety.common.effects.brew.modifiers.BrewModifier;
import com.Polarice3.Goety.common.effects.brew.modifiers.CapacityModifier;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.WitchHatItem;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Based and modified from @MoriyaShiine's Witch Cauldron codes.
 */
public class BrewCauldronBlockEntity extends BlockEntity implements Container {
    private final List<SoulCandlestickBlockEntity> candlestickBlockEntityList = Lists.newArrayList();
    private final List<BlockPos> witchPoles = Lists.newArrayList();
    public static int WATER_COLOR = 0x3F76E4, FAILED_COLOR = 0x6D4423;
    public NonNullList<ItemStack> container = NonNullList.withSize(32, ItemStack.EMPTY);
    public Mode mode = Mode.IDLE;
    public boolean isBrewing = false;
    public int liquidColor = WATER_COLOR, capacity = 0, capacityUsed = 0, duration = 0, amplifier = 0, aoe = 0, heatTime = 0, totalCost = 0, soulTime = 0, quaff = 0, takeBrew = 0, update = 0;
    public float velocity = 0;
    public float lingering = 0;

    public BrewCauldronBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.BREWING_CAULDRON.get(), p_155229_, p_155230_);
    }

    public void tick(){
        if (this.level != null) {
            if (!this.level.isClientSide) {
                if (this.checkFire()) {
                    if (!this.isHeated()) {
                        ++this.heatTime;
                    } else if (this.getBlockState().getValue(ModStateProperties.LEVEL_BREW) > 0){
                        if (this.level.getGameTime() % 60 == 0 || this.isBrewing) {
                            this.level.playSound(null, this.getBlockPos(), ModSounds.CAULDRON_BUBBLES.get(), SoundSource.BLOCKS, 0.33F, this.mode == Mode.FAILED ? 0.5F : 1);
                        }
                        if (this.mode == Mode.BREWING){
                            if (this.level.getGameTime() % 60 == 0 && this.level.random.nextBoolean()){
                                this.level.playSound(null, this.getBlockPos(), ModSounds.CAULDRON_CHIMES.get(), SoundSource.BLOCKS, 0.15F, this.level.random.nextFloat() * 0.4F + 0.8F);
                            }
                        }
                        this.findCandlesticks();
                        if (this.isBrewing && mode == Mode.BREWING) {
                            if (!this.candlestickBlockEntityList.isEmpty()) {
                                if (this.soulTime < this.getBrewCost()) {
                                    for (SoulCandlestickBlockEntity candlestickBlock : this.candlestickBlockEntityList) {
                                        if (candlestickBlock.getSouls() > 0) {
                                            candlestickBlock.drainSouls(1, this.getBlockPos());
                                            this.soulTime++;
                                        }
                                    }
                                } else {
                                    this.setColor(PotionUtils.getColor(this.getBrew()));
                                    this.level.playSound(null, this.getBlockPos(), ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    this.soulTime = 0;
                                    this.totalCost = 0;
                                    this.isBrewing = false;
                                    this.mode = Mode.COMPLETED;
                                }
                            } else {
                                this.fail();
                            }
                        }
                        if (mode == Mode.COMPLETED){
                            if (this.level instanceof ServerLevel serverLevel){
                                float f = 1.0F;
                                serverLevel.sendParticles(ParticleTypes.WITCH, this.worldPosition.getX() + 0.5F + Math.cos(serverLevel.getGameTime() * 0.25) * f, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5F + Math.sin(serverLevel.getGameTime() * 0.25) * f, 0, 0, 0, 0, 0.5F);
                                serverLevel.sendParticles(ParticleTypes.WITCH, this.worldPosition.getX() + 0.5F + Math.cos(serverLevel.getGameTime() * 0.25 + Math.PI) * f, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5F + Math.sin(serverLevel.getGameTime() * 0.25 + Math.PI) * f, 0, 0, 0, 0, 0.5F);
                            }
                        }
                    } else {
                        this.reset();
                    }
                } else {
                    this.isBrewing = false;
                    this.soulTime = 0;
                    this.totalCost = 0;
                    this.mode = Mode.IDLE;
                    if (this.heatTime > 0) {
                        --this.heatTime;
                    }
                }
                if (this.update > 0){
                    --this.update;
                    if (this.update <= 1) {
                        this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ModStateProperties.LEVEL_BREW, 3));
                        this.update = 0;
                    }
                }
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ModStateProperties.FAILED, this.mode == Mode.FAILED));
                this.markUpdated();
            }
        }
    }

    public boolean isHeated(){
        return this.heatTime == MathHelper.secondsToTicks(5);
    }

    private int getFirstEmptySlot() {
        for (int i = 0; i < this.getCapacity(); i++) {
            if (this.getItem(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public void brew(){
        if (this.level != null && !this.level.isClientSide) {
            if (this.mode == Mode.BREWING && !this.isBrewing) {
                this.isBrewing = true;
                this.level.playSound(null, this.getBlockPos(), ModSounds.PREPARE_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            this.markUpdated();
        }
    }

    public Mode insertItem(ItemStack itemStack){
        if (this.level != null && !this.level.isClientSide){
            Item ingredient = itemStack.getItem();
            BrewModifier brewModifier = new BrewEffects().getModifier(ingredient);
            int modLevel = brewModifier != null ? brewModifier.getLevel() : -1;
            boolean activate = brewModifier instanceof CapacityModifier && brewModifier.getLevel() == 0;
            int firstEmpty = getFirstEmptySlot();
            if (firstEmpty != -1) {
                this.setItem(firstEmpty, itemStack);
                if (this.mode == Mode.IDLE && this.getCapacity() < 4 && activate) {
                    this.clearContent();
                    this.capacity = 4;
                    if (this.level instanceof ServerLevel serverLevel){
                        for(int k = 0; k < 20; ++k) {
                            float f2 = serverLevel.random.nextFloat() * 4.0F;
                            float f1 = serverLevel.random.nextFloat() * ((float)Math.PI * 2F);
                            double d1 = Mth.cos(f1) * f2;
                            double d2 = 0.01D + serverLevel.random.nextDouble() * 0.5D;
                            double d3 = Mth.sin(f1) * f2;
                            serverLevel.sendParticles(ParticleTypes.WITCH, (this.getBlockPos().getX() + 0.5D) + d1 * 0.1D, (this.getBlockPos().getY() + 0.5D) + 0.3D, (this.getBlockPos().getZ() + 0.5D) + d3 * 0.1D, 0, d1, d2, d3, 0.25F);
                        }
                    }
                    return Mode.BREWING;
                }
                if (this.mode == Mode.BREWING) {
                    BrewingRecipe brewingRecipe = this.level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.BREWING_TYPE.get()).stream().filter(recipe -> recipe.input.test(itemStack)).findFirst().orElse(null);
                    BrewEffect brewEffect = new BrewEffects().getEffectFromCatalyst(ingredient);
                    if (this.hasNoAugmentation()) {
                        if (brewingRecipe != null || brewEffect != null) {
                            if (brewingRecipe != null){
                                if (brewingRecipe.getCapacityExtra() + this.capacityUsed < this.getCapacity()) {
                                    this.capacityUsed += brewingRecipe.getCapacityExtra();
                                    this.addCost(brewingRecipe.soulCost);
                                    this.setColor(BrewUtils.getColor(this.getBrew()));
                                    return Mode.BREWING;
                                }
                            }
                            if (brewEffect != null){
                                if (brewEffect.getCapacityExtra() + this.capacityUsed < this.getCapacity()) {
                                    this.capacityUsed += brewEffect.getCapacityExtra();
                                    this.addCost(brewEffect.getSoulCost());
                                    this.setColor(BrewUtils.getColor(this.getBrew()));
                                    return Mode.BREWING;
                                }
                            }
                        }
                    }

                    if (brewModifier != null) {
                        if (BrewUtils.hasEffect(this.getBrew())) {
                            if (brewModifier.getId().equals(BrewModifier.HIDDEN) || brewModifier.getId().equals(BrewModifier.SPLASH) || brewModifier.getId().equals(BrewModifier.LINGERING) || brewModifier.getId().equals(BrewModifier.GAS)) {
                                if (brewModifier.getId().equals(BrewModifier.HIDDEN)) {
                                    this.addCost(10);
                                }
                                return Mode.BREWING;
                            }
                            if (brewModifier.getId().equals(BrewModifier.DURATION)) {
                                if (this.getDuration() == 0 && modLevel == 0) {
                                    this.duration++;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                                if (this.getDuration() == 1 && modLevel == 1) {
                                    this.duration++;
                                    this.multiplyCost(1.5F);
                                    return Mode.BREWING;
                                }
                                if (this.getDuration() == 2 && modLevel == 2) {
                                    this.duration++;
                                    this.multiplyCost(2.0F);
                                    return Mode.BREWING;
                                }
                            }
                            if (brewModifier.getId().equals(BrewModifier.AMPLIFIER)) {
                                if (this.getAmplifier() == 0 && modLevel == 0) {
                                    this.amplifier++;
                                    this.multiplyCost(2.0F);
                                    return Mode.BREWING;
                                }
                                if (this.getAmplifier() == 1 && modLevel == 1) {
                                    this.amplifier++;
                                    this.multiplyCost(2.5F);
                                    return Mode.BREWING;
                                }
                                if (this.getAmplifier() == 2 && modLevel == 2) {
                                    this.amplifier++;
                                    this.multiplyCost(3.0F);
                                    return Mode.BREWING;
                                }
                            }
                            if (brewModifier.getId().equals(BrewModifier.AOE)) {
                                if (this.getAoE() == 0 && modLevel == 0) {
                                    this.aoe++;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                                if (this.getAoE() == 1 && modLevel == 1) {
                                    this.aoe++;
                                    this.multiplyCost(1.5F);
                                    return Mode.BREWING;
                                }
                                if (this.getAoE() == 2 && modLevel == 2) {
                                    this.aoe++;
                                    this.multiplyCost(2.0F);
                                    return Mode.BREWING;
                                }
                            }
                            if (brewModifier.getId().equals(BrewModifier.LINGER)) {
                                if (this.getLingering() == 0 && modLevel == 0) {
                                    this.lingering++;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                                if (this.getLingering() == 1 && modLevel == 1) {
                                    this.lingering++;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                                if (this.getLingering() == 2 && modLevel == 2) {
                                    this.lingering++;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                            }
                            if (brewModifier.getId().equals(BrewModifier.QUAFF)) {
                                if (this.getQuaff() == 0 && modLevel == 0) {
                                    this.quaff += 8;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                                if (this.getQuaff() == 8 && modLevel == 1) {
                                    this.quaff += 8;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                                if (this.getQuaff() == 16 && modLevel == 2) {
                                    this.quaff += 8;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                            }
                            if (brewModifier.getId().equals(BrewModifier.VELOCITY)) {
                                if (this.getVelocity() == 0 && modLevel == 0) {
                                    this.velocity += 0.1F;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                                if (this.getVelocity() == 0.1F && modLevel == 1) {
                                    this.velocity += 0.2F;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                                if (this.getVelocity() == 0.3F && modLevel == 2) {
                                    this.velocity += 0.2F;
                                    this.multiplyCost(1.25F);
                                    return Mode.BREWING;
                                }
                            }
                        } else if (brewModifier instanceof CapacityModifier capacityModifier) {
                            if (this.getCapacity() == 4 && capacityModifier.getLevel() == 1) {
                                this.capacity += 2;
                                this.clearContent();
                                return Mode.BREWING;
                            }
                            if (this.getCapacity() == 6 && capacityModifier.getLevel() == 2) {
                                this.capacity += 2;
                                this.clearContent();
                                return Mode.BREWING;
                            }
                            if (this.getCapacity() == 8 && capacityModifier.getLevel() == 3) {
                                this.capacity += 2;
                                this.clearContent();
                                return Mode.BREWING;
                            }
                            if (this.getCapacity() == 10 && capacityModifier.getLevel() == 4) {
                                this.capacity += 2;
                                this.clearContent();
                                return Mode.BREWING;
                            }
                            if (this.getCapacity() == 12 && capacityModifier.getLevel() == 5) {
                                this.capacity += 4;
                                this.clearContent();
                                return Mode.BREWING;
                            }
                        }
                    }
                }
            } else if (this.mode == Mode.IDLE && this.getCapacity() < 4 && activate) {
                this.clearContent();
                this.capacity = 4;
                if (this.level instanceof ServerLevel serverLevel){
                    for(int k = 0; k < 20; ++k) {
                        float f2 = serverLevel.random.nextFloat() * 4.0F;
                        float f1 = serverLevel.random.nextFloat() * ((float)Math.PI * 2F);
                        double d1 = Mth.cos(f1) * f2;
                        double d2 = 0.01D + serverLevel.random.nextDouble() * 0.5D;
                        double d3 = Mth.sin(f1) * f2;
                        serverLevel.sendParticles(ParticleTypes.WITCH, (this.getBlockPos().getX() + 0.5D) + d1 * 0.1D, (this.getBlockPos().getY() + 0.5D) + 0.3D, (this.getBlockPos().getZ() + 0.5D) + d3 * 0.1D, 0, d1, d2, d3, 0.25F);
                    }
                }
                return Mode.BREWING;
            }
            this.markUpdated();
        }
        return fail();
    }

    public void commonReset(){
        if (this.level != null && !this.level.isClientSide) {
            this.setColor(WATER_COLOR, false);
            this.isBrewing = false;
            this.capacity = 0;
            this.capacityUsed = 0;
            this.soulTime = 0;
            this.totalCost = 0;
            this.duration = 0;
            this.amplifier = 0;
            this.aoe = 0;
            this.quaff = 0;
            this.velocity = 0;
            this.lingering = 0;
            this.takeBrew = 0;
            this.clearContent();
            this.markUpdated();
            this.level.setBlock(this.worldPosition, this.getBlockState().setValue(ModStateProperties.LEVEL_BREW, 0), 3);
        }
    }

    public void fullReset(){
        if (this.level != null && !this.level.isClientSide) {
            this.commonReset();
            this.mode = Mode.IDLE;
        }
    }

    public Mode reset() {
        this.commonReset();
        return Mode.IDLE;
    }

    public Mode fail() {
        if (this.level != null && !this.level.isClientSide) {
            this.setColor(FAILED_COLOR);
            this.isBrewing = false;
            this.capacity = 0;
            this.soulTime = 0;
            this.totalCost = 0;
            this.level.playSound(null, this.worldPosition, SoundEvents.NOTE_BLOCK_SNARE.get(), SoundSource.BLOCKS, 5.0F, 0.75F);
            this.markUpdated();
        }
        return Mode.FAILED;
    }

    public void setColor(int color){
        this.setColor(color, true);
    }

    public void setColor(int color, boolean update) {
        if (this.level != null) {
            this.liquidColor = color;
            if (!this.level.isClientSide) {
                if (update) {
                    if (this.level.getBlockState(this.getBlockPos()).getValue(ModStateProperties.LEVEL_BREW) == 3) {
                        this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ModStateProperties.LEVEL_BREW, 4));
                        this.update = 3;
                    }
                }
                this.markUpdated();
            }
        }
    }

    public int getColor(){
        return this.liquidColor;
    }

    public ItemStack getBrew() {
        ItemStack brew = new ItemStack(ModItems.BREW.get());
        if (this.level != null && !this.level.isClientSide) {
            List<MobEffectInstance> effects = new ArrayList<>();
            List<BrewEffectInstance> blockEffects = new ArrayList<>();
            int hidden = 0;
            for (int i = 0; i < this.getCapacity(); i++) {
                ItemStack itemStack = this.getItem(i);
                Item item = itemStack.getItem();
                BrewModifier brewModifier = new BrewEffects().getModifier(item);
                BrewEffect brewEffect = new BrewEffects().getEffectFromCatalyst(item);
                BrewingRecipe brewingRecipe = this.level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.BREWING_TYPE.get()).stream().filter(recipe -> recipe.input.test(itemStack)).findFirst().orElse(null);
                if (brewingRecipe != null && effects.stream().noneMatch(effect -> effect.getEffect() == brewingRecipe.output)) {
                    effects.add(new MobEffectInstance(brewingRecipe.output, brewingRecipe.duration));
                } else if (brewEffect != null){
                    if (brewEffect instanceof PotionBrewEffect potionBrewEffect){
                        effects.add(new MobEffectInstance(potionBrewEffect.mobEffect, potionBrewEffect.duration));
                    } else {
                        blockEffects.add(new BrewEffectInstance(brewEffect, 1));
                    }
                } else if (brewModifier != null) {
                    if (brewModifier.getId().equals(BrewModifier.HIDDEN)) {
                        hidden++;
                    } else if (brewModifier.getId().equals(BrewModifier.SPLASH) && brew.is(ModItems.BREW.get())) {
                        brew = new ItemStack(ModItems.SPLASH_BREW.get());
                    } else if (brewModifier.getId().equals(BrewModifier.LINGERING) && brew.is(ModItems.SPLASH_BREW.get())) {
                        brew = new ItemStack(ModItems.LINGERING_BREW.get());
                    } else if (brewModifier.getId().equals(BrewModifier.GAS) && brew.is(ModItems.LINGERING_BREW.get())) {
                        brew = new ItemStack(ModItems.GAS_BREW.get());
                    }
                }
            }
            for (int i = 0; i < effects.size(); i++) {
                for (int j = 0; j < this.getDuration(); j++) {
                    MobEffect type = effects.get(i).getEffect();
                    int duration = effects.get(i).getDuration();
                    effects.set(i, new MobEffectInstance(type, type.isInstantenous() ? duration : duration * 2));
                }
                for (int j = 0; j < this.getAmplifier(); j++) {
                    MobEffect type = effects.get(i).getEffect();
                    int duration = effects.get(i).getDuration();
                    effects.set(i, new MobEffectInstance(type, type.isInstantenous() ? duration : duration / 2, effects.get(i).getAmplifier() + 1));
                }
                for (int j = 0; j < hidden; j++) {
                    MobEffect type = effects.get(i).getEffect();
                    int duration = effects.get(i).getDuration();
                    int amplifier = effects.get(i).getAmplifier();
                    effects.set(i, new MobEffectInstance(type, duration, amplifier, false, false, false));
                }
            }
            for (int i = 0; i < blockEffects.size(); i++) {
                for (int j = 0; j < this.getDuration(); j++) {
                    BrewEffect type = blockEffects.get(i).getEffect();
                    int duration = blockEffects.get(i).getDuration();
                    blockEffects.set(i, new BrewEffectInstance(type, duration));
                }
                for (int j = 0; j < this.getAmplifier(); j++) {
                    BrewEffect type = blockEffects.get(i).getEffect();
                    int duration = blockEffects.get(i).getDuration();
                    blockEffects.set(i, new BrewEffectInstance(type, duration, blockEffects.get(i).getAmplifier() + 1));
                }
            }
            BrewUtils.setCustomEffects(brew, effects, blockEffects);
            BrewUtils.setAreaOfEffect(brew, this.getAoE());
            BrewUtils.setLingering(brew, this.getLingering());
            BrewUtils.setQuaff(brew, this.getQuaff());
            BrewUtils.setVelocity(brew, this.getVelocity());
            brew.getOrCreateTag().putInt("CustomPotionColor", BrewUtils.getColor(effects, blockEffects));
            brew.getOrCreateTag().putBoolean("CustomBrew", true);
            this.markUpdated();
        }
        return brew;
    }

    public boolean hasNoAugmentation(){
        return this.duration <= 0 && this.amplifier <= 0 && this.aoe <= 0 && this.quaff <= 0 && this.velocity <= 0 && this.lingering <= 0;
    }

    public void multiplyCost(float cost){
        this.totalCost *= cost;
        this.discountCost();
    }

    public void addCost(float cost){
        this.totalCost += cost;
        this.discountCost();
    }

    public void discountCost(){
        this.findWitchPoles();
        int size = Mth.clamp(this.witchPoles.size(), 0, 3);
        if (size > 0) {
            for (int i = 0; i < size; ++i) {
                totalCost *= 0.99F;
            }
        }
        if (this.level != null){
            if (this.level.getBiome(this.worldPosition).is(BiomeTags.HAS_SWAMP_HUT)){
                totalCost *= 0.99F;
            }
        }
    }

    public int getBrewCost() {
        return this.totalCost;
    }

    public int getCapacityUsed(){
        int number = this.capacityUsed;
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (!this.getItem(i).isEmpty()) {
                ++number;
            }
        }
        return number;
    }

    public int getCapacity(){
        return this.capacity;
    }

    public int getDuration(){
        return this.duration;
    }

    public int getAmplifier(){
        return this.amplifier;
    }

    public int getAoE(){
        return this.aoe;
    }

    public int getQuaff(){
        return this.quaff;
    }

    public float getVelocity(){
        return this.velocity;
    }

    public float getLingering(){
        return this.lingering;
    }

    @Override
    public int getContainerSize() {
        return container.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.container) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int pIndex) {
        return this.container.get(pIndex);
    }

    @Override
    public @NotNull ItemStack removeItem(int pIndex, int pCount) {
        return ContainerHelper.removeItem(this.container, pIndex, pCount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pIndex) {
        return ContainerHelper.takeItem(this.container, pIndex);
    }

    @Override
    public void setItem(int pIndex, @NotNull ItemStack pStack) {
        this.container.set(pIndex, pStack);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.distanceToSqr(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D, this.getBlockPos().getZ() + 0.5D) < 16.0D;
    }

    @Override
    public void clearContent() {
        this.container.clear();
    }

    public void load(CompoundTag compoundNBT) {
        this.capacity = compoundNBT.getInt("Capacity");
        this.capacityUsed = compoundNBT.getInt("CapacityUsed");
        this.duration = compoundNBT.getInt("Duration");
        this.amplifier = compoundNBT.getInt("Amplifier");
        this.aoe = compoundNBT.getInt("AreaOfEffect");
        this.quaff = compoundNBT.getInt("Quaff");
        this.heatTime = compoundNBT.getInt("HeatTime");
        this.soulTime = compoundNBT.getInt("SoulTime");
        this.takeBrew = compoundNBT.getInt("TakeBrew");
        this.totalCost = compoundNBT.getInt("TotalCost");
        this.update = compoundNBT.getInt("ColorUpdate");
        if (compoundNBT.contains("Color")) {
            this.liquidColor = compoundNBT.getInt("Color");
        }
        this.velocity = compoundNBT.getFloat("Velocity");
        this.lingering = compoundNBT.getFloat("Lingering");
        this.isBrewing = compoundNBT.getBoolean("Brewing");
        if (compoundNBT.contains("Mode")) {
            this.mode = Mode.valueOf(compoundNBT.getString("Mode"));
        }
        this.container = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundNBT, this.container);
    }

    public void saveAdditional(CompoundTag pCompound) {
        pCompound.putInt("Capacity", this.capacity);
        pCompound.putInt("CapacityUsed", this.capacityUsed);
        pCompound.putInt("Duration", this.duration);
        pCompound.putInt("Amplifier", this.amplifier);
        pCompound.putInt("AreaOfEffect", this.aoe);
        pCompound.putInt("Quaff", this.quaff);
        pCompound.putInt("HeatTime", this.heatTime);
        pCompound.putInt("SoulTime", this.soulTime);
        pCompound.putInt("TakeBrew", this.takeBrew);
        pCompound.putInt("TotalCost", this.totalCost);
        pCompound.putInt("ColorUpdate", this.update);
        pCompound.putInt("Color", this.liquidColor);
        pCompound.putFloat("Velocity", this.velocity);
        pCompound.putFloat("Lingering", this.lingering);
        pCompound.putBoolean("Brewing", this.isBrewing);
        pCompound.putString("Mode", this.mode.name());
        ContainerHelper.saveAllItems(pCompound, this.container);
    }

    public int getTargetLevel(ItemStack stack, Player player) {
        if (this.level != null) {
            Item item = stack.getItem();
            int waterLevel = this.level.getBlockState(this.worldPosition).getValue(ModStateProperties.LEVEL_BREW);
            if (mode == Mode.IDLE || mode == Mode.FAILED) {
                if (item == Items.BUCKET && waterLevel == 3) {
                    return 0;
                } else if (item == Items.WATER_BUCKET && waterLevel == 0) {
                    return 3;
                } else if (item == Items.GLASS_BOTTLE) {
                    return waterLevel - 1;
                } else if (item == Items.POTION && waterLevel < 3 && PotionUtils.getPotion(stack) == Potions.WATER) {
                    return waterLevel + 1;
                }
            } else if (mode == Mode.COMPLETED) {
                if (waterLevel >= 3 && TaglockKit.canAffect(player, stack, Vec3.atCenterOf(this.getBlockPos()), getWitchPoles(this))){
                    return 0;
                } else if (item == Items.GLASS_BOTTLE || BrewUtils.brewableFood(stack)) {
                    boolean hat = CuriosFinder.hasCurio(player, itemStack -> itemStack.getItem() instanceof WitchHatItem),
                            croneHat = CuriosFinder.hasCurio(player, ModItems.CRONE_HAT.get()),
                            robe = CuriosFinder.hasWitchRobe(player);
                    boolean blackCat = !player.level.getEntitiesOfClass(Cat.class,
                            player.getBoundingBox().inflate(16, 8, 16),
                            cat -> cat.getVariant() == BuiltInRegistries.CAT_VARIANT.get(CatVariant.ALL_BLACK) && cat.getOwner() == player).isEmpty();
                    float chance = 1.0F;
                    int times = 0;
                    if (croneHat){
                        times += 2;
                        chance -= 0.25F;
                    } else if (hat){
                        times += 1;
                        chance -= 0.25F;
                    }
                    if (robe){
                        times += 1;
                        chance -= 0.25F;
                    }
                    if (blackCat){
                        times += 1;
                        chance -= 0.25F;
                    }
                    times += SEHelper.getBottleLevel(player);
                    if (player.level.random.nextFloat() <= chance || this.takeBrew >= times) {
                        return waterLevel - 1;
                    } else {
                        this.takeBrew++;
                        return waterLevel;
                    }
                } else {
                    return waterLevel;
                }
            } else {
                return waterLevel;
            }
        }
        return -1;
    }

    private boolean checkFire() {
        assert this.level != null;
        BlockPos pos = new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() - 1, this.getBlockPos().getZ());
        BlockState blockState = this.level.getBlockState(pos);
        return blockState.getBlock() instanceof BaseFireBlock || blockState.getBlock() instanceof LiquidBlock liquidBlock && liquidBlock.getFluid() instanceof LavaFluid || blockState.getBlock() instanceof MagmaBlock || (blockState.getBlock() instanceof CampfireBlock && blockState.getValue(BlockStateProperties.LIT));
    }

    public void findWitchPoles(){
        if (this.level != null){
            this.witchPoles.clear();
            for (int i = -8; i <= 8; ++i) {
                for (int j = -8; j <= 8; ++j) {
                    for (int k = -8; k <= 8; ++k) {
                        BlockPos blockpos1 = this.getBlockPos().offset(i, j, k);
                        if (this.level.getBlockState(blockpos1).is(ModBlocks.WITCH_POLE.get())) {
                            this.witchPoles.add(blockpos1);
                        }
                    }
                }
            }
        }
    }

    public static int getWitchPoles(BrewCauldronBlockEntity cauldronBlock){
        cauldronBlock.findWitchPoles();
        return Mth.clamp(cauldronBlock.witchPoles.size(), 0, 3);
    }

    private void findCandlesticks(){
        if (this.level != null){
            this.candlestickBlockEntityList.clear();
            for (int i = -8; i <= 8; ++i) {
                for (int j = -8; j <= 8; ++j) {
                    for (int k = -8; k <= 8; ++k) {
                        BlockPos blockpos1 = this.getBlockPos().offset(i, j, k);
                        if (this.level.getBlockEntity(blockpos1) instanceof SoulCandlestickBlockEntity soulCandlestickBlockEntity) {
                            if (soulCandlestickBlockEntity.getSouls() > 0) {
                                this.candlestickBlockEntityList.add(soulCandlestickBlockEntity);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public enum Mode {
        IDLE("idle"),
        BREWING("brewing"),
        COMPLETED("completed"),
        FAILED("failed");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }

}
