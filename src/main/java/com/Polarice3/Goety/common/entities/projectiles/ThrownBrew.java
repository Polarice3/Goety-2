package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.util.BrewEffectCloud;
import com.Polarice3.Goety.common.entities.util.BrewGas;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SAddBrewParticlesPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BrewUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class ThrownBrew extends ThrowableItemProjectile implements ItemSupplier {
    public static final double SPLASH_RANGE = 4.0D;
    private static final double SPLASH_RANGE_SQ = 16.0D;
    public static final Predicate<LivingEntity> WATER_SENSITIVE = LivingEntity::isSensitiveToWater;

    public ThrownBrew(EntityType<? extends ThrownBrew> p_37527_, Level p_37528_) {
        super(p_37527_, p_37528_);
    }

    public ThrownBrew(Level p_37535_, LivingEntity p_37536_) {
        super(ModEntityType.BREW.get(), p_37536_, p_37535_);
    }

    public ThrownBrew(Level p_37530_, double p_37531_, double p_37532_, double p_37533_) {
        super(ModEntityType.BREW.get(), p_37531_, p_37532_, p_37533_, p_37530_);
    }

    protected Item getDefaultItem() {
        return ModItems.SPLASH_BREW.get();
    }

    protected float getGravity() {
        return 0.05F;
    }

    @Override
    public boolean isInWater() {
        if (BrewUtils.getAquatic(this.getItem())){
            return false;
        }
        return super.isInWater();
    }

    @Override
    public boolean fireImmune() {
        if (BrewUtils.getFireProof(this.getItem())){
            return true;
        }
        return super.fireImmune();
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        if (BrewUtils.getAquatic(this.getItem()) && type == ForgeMod.WATER_TYPE.get()){
            return false;
        } else if (BrewUtils.getFireProof(this.getItem()) && type == ForgeMod.LAVA_TYPE.get()){
            return false;
        } else {
            return super.isPushedByFluid(type);
        }
    }

    protected void onHitBlock(BlockHitResult p_37541_) {
        super.onHitBlock(p_37541_);
        if (!this.level.isClientSide) {
            if (!this.isGas()) {
                ItemStack itemstack = this.getItem();
                Potion potion = PotionUtils.getPotion(itemstack);
                List<BrewEffectInstance> list = BrewUtils.getBrewEffects(itemstack);
                boolean flag = potion == Potions.WATER && list.isEmpty();
                Direction direction = p_37541_.getDirection();
                BlockPos blockpos = p_37541_.getBlockPos();
                BlockPos blockpos1 = blockpos.relative(direction);
                if (flag) {
                    this.dowseFire(blockpos1);
                    this.dowseFire(blockpos1.relative(direction.getOpposite()));

                    for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                        this.dowseFire(blockpos1.relative(direction1));
                    }
                }
                for (BrewEffectInstance brewEffectInstance : list) {
                    LivingEntity livingEntity = this.getOwner() instanceof LivingEntity living ? living : null;
                    brewEffectInstance.getEffect().applyDirectionalBlockEffect(this.level, p_37541_.getBlockPos(), p_37541_.getDirection(), livingEntity, brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(itemstack));
                    brewEffectInstance.getEffect().applyBlockEffect(this.level, p_37541_.getBlockPos(), livingEntity, brewEffectInstance.getDuration(), brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(itemstack));
                }
            }
        }
    }

    protected void onHitEntity(EntityHitResult p_37259_) {
        super.onHitEntity(p_37259_);
        if (!this.level.isClientSide) {
            if (!this.isGas()) {
                ItemStack itemstack = this.getItem();
                List<BrewEffectInstance> list = BrewUtils.getBrewEffects(itemstack);
                for (BrewEffectInstance brewEffectInstance : list) {
                    LivingEntity livingEntity = this.getOwner() instanceof LivingEntity living ? living : null;
                    brewEffectInstance.getEffect().applyBlockEffect(this.level, p_37259_.getEntity().blockPosition(), livingEntity, brewEffectInstance.getDuration(), brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(itemstack));
                }
            }
        }
    }

    protected void onHit(HitResult p_37543_) {
        super.onHit(p_37543_);
        if (!this.level.isClientSide) {
            ItemStack itemstack = this.getItem();
            Potion potion = PotionUtils.getPotion(itemstack);
            List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
            List<BrewEffectInstance> list1 = BrewUtils.getBrewEffects(itemstack);
            boolean flag = potion == Potions.WATER && list.isEmpty();
            if (flag) {
                this.applyWater();
            } else if (!list.isEmpty() || !list1.isEmpty()) {
                if (this.isGas()){
                    this.makeBrewGas(itemstack, p_37543_);
                } else if (this.isLingering()) {
                    this.makeAreaOfEffectCloud(itemstack);
                } else {
                    this.applySplash(list, list1, p_37543_.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)p_37543_).getEntity() : null);
                }
            }

            ModNetwork.sendToALL(new SAddBrewParticlesPacket(itemstack, this.blockPosition(), potion.hasInstantEffects(), BrewUtils.getColor(itemstack)));
            this.discard();
        }
    }

    private void applyWater() {
        ItemStack itemstack = this.getItem();
        int area = BrewUtils.getAreaOfEffect(itemstack) + 4;
        int areaSqr = Mth.square(area);
        AABB aabb = this.getBoundingBox().inflate(area, area / 2.0D, area);
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, aabb, WATER_SENSITIVE);
        if (!list.isEmpty()) {
            for(LivingEntity livingentity : list) {
                double d0 = this.distanceToSqr(livingentity);
                if (d0 < areaSqr && livingentity.isSensitiveToWater()) {
                    livingentity.hurt(damageSources().indirectMagic(this, this.getOwner()), 1.0F);
                }
            }
        }

        for(Axolotl axolotl : this.level.getEntitiesOfClass(Axolotl.class, aabb)) {
            axolotl.rehydrate();
        }

    }

    private void applySplash(List<MobEffectInstance> mobEffectInstances, List<BrewEffectInstance> brewEffectInstances, @Nullable Entity hitEntity) {
        ItemStack itemstack = this.getItem();
        int area = BrewUtils.getAreaOfEffect(itemstack) + 4;
        int areaSqr = Mth.square(area);
        AABB aabb = this.getBoundingBox().inflate(area, area / 2.0D, area);
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, aabb);
        if (!list.isEmpty()) {
            Entity entity = this.getEffectSource();

            for(LivingEntity livingentity : list) {
                if (livingentity.isAffectedByPotions()) {
                    double d0 = this.distanceToSqr(livingentity);
                    if (d0 < areaSqr) {
                        double d1 = 1.0D - Math.sqrt(d0) / areaSqr;
                        if (livingentity == hitEntity) {
                            d1 = 1.0D;
                        }

                        if (!mobEffectInstances.isEmpty()) {
                            for (MobEffectInstance mobeffectinstance : mobEffectInstances) {
                                MobEffect mobeffect = mobeffectinstance.getEffect();
                                if (mobeffect.isInstantenous()) {
                                    mobeffect.applyInstantenousEffect(this, this.getOwner(), livingentity, mobeffectinstance.getAmplifier(), d1);
                                } else {
                                    int i = (int) (d1 * (double) mobeffectinstance.getDuration() + 0.5D);
                                    if (i > 20) {
                                        livingentity.addEffect(new MobEffectInstance(mobeffect, i, mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()), entity);
                                    }
                                }
                            }
                        }
                        if (!brewEffectInstances.isEmpty()) {
                            for (BrewEffectInstance brewEffectInstance : brewEffectInstances) {
                                BrewEffect brewEffect = brewEffectInstance.getEffect();
                                if (brewEffect.isInstantenous()) {
                                    brewEffect.applyInstantenousEffect(this, this.getOwner(), livingentity, brewEffectInstance.getAmplifier(), d1);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void makeAreaOfEffectCloud(ItemStack p_37538_) {
        ItemStack itemstack = this.getItem();
        int h = BrewUtils.getAreaOfEffect(itemstack);
        float i = BrewUtils.getLingering(itemstack);
        BrewEffectCloud brewEffectCloud = new BrewEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity) {
            brewEffectCloud.setOwner((LivingEntity)entity);
        }

        brewEffectCloud.setRadius(3.0F + h);
        if (i < 0.3) {
            brewEffectCloud.setRadiusOnUse(-(0.5F - Mth.square(i)));
        }
        brewEffectCloud.setWaitTime(10);
        brewEffectCloud.setRadiusPerTick(-brewEffectCloud.getRadius() / (float)brewEffectCloud.getDuration());

        for(MobEffectInstance mobeffectinstance : PotionUtils.getCustomEffects(p_37538_)) {
            brewEffectCloud.addEffect(new MobEffectInstance(mobeffectinstance));
        }

        for(BrewEffectInstance brewEffectInstance : BrewUtils.getCustomEffects(p_37538_)) {
            if (brewEffectInstance.getEffect().canLinger()) {
                brewEffectCloud.addBrewEffect(new BrewEffectInstance(brewEffectInstance));
            }
        }

        CompoundTag compoundtag = p_37538_.getTag();
        if (compoundtag != null && compoundtag.contains("CustomPotionColor", 99)) {
            brewEffectCloud.setFixedColor(compoundtag.getInt("CustomPotionColor"));
        }

        this.level.addFreshEntity(brewEffectCloud);
    }

    private void makeBrewGas(ItemStack itemStack, HitResult hitResult){
        ItemStack itemstack = this.getItem();
        int h = BrewUtils.getAreaOfEffect(itemstack);
        int i = (int) BrewUtils.getLingering(itemstack);
        BlockPos blockPos = BlockPos.containing(hitResult.getLocation());
        if (hitResult.getType() == HitResult.Type.ENTITY){
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            blockPos = entityHitResult.getEntity().blockPosition();
        }
        BrewGas brewGas = new BrewGas(this.level, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        LivingEntity livingEntity = null;
        if (this.getOwner() instanceof LivingEntity livingEntity1) {
            livingEntity = livingEntity1;
        }
        brewGas.setGas(PotionUtils.getCustomEffects(itemStack), BrewUtils.getCustomEffects(itemStack),
                120 * (i + 1), 3 * (h + 1), livingEntity);

        this.level.addFreshEntity(brewGas);

        if (!this.level.isClientSide){
            ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockPos, ModSounds.BREW_GAS.get(), 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F));
        }
    }

    private boolean isLingering() {
        return this.getItem().is(ModItems.LINGERING_BREW.get());
    }

    private boolean isGas() {
        return this.getItem().is(ModItems.GAS_BREW.get());
    }

    private void dowseFire(BlockPos p_150193_) {
        BlockState blockstate = this.level.getBlockState(p_150193_);
        if (blockstate.is(BlockTags.FIRE)) {
            this.level.removeBlock(p_150193_, false);
        } else if (AbstractCandleBlock.isLit(blockstate)) {
            AbstractCandleBlock.extinguish((Player)null, blockstate, this.level, p_150193_);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            this.level.levelEvent((Player)null, 1009, p_150193_, 0);
            CampfireBlock.dowse(this.getOwner(), this.level, p_150193_, blockstate);
            this.level.setBlockAndUpdate(p_150193_, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
        }

    }
}
