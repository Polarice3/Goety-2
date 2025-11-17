package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.NeutralZombieAttackGoal;
import com.Polarice3.Goety.common.entities.ally.illager.VindicatorServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class AbstractZombieVindicator extends ZombieServant {
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(AbstractZombieVindicator.class, EntityDataSerializers.BOOLEAN);
    public int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;

    public AbstractZombieVindicator(EntityType<? extends ZombieServant> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ZombieVindicatorHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ZombieVindicatorDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.ZombieVindicatorArmor.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ZombieVindicatorHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ZombieVindicatorArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.ZombieVindicatorDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONVERTING_ID, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            pCompound.putUUID("ConversionPlayer", this.conversionStarter);
        }
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ConversionTime", 99) && pCompound.getInt("ConversionTime") > -1) {
            this.startConverting(pCompound.hasUUID("ConversionPlayer") ? pCompound.getUUID("ConversionPlayer") : null, pCompound.getInt("ConversionTime"));
        }
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();
            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, ModEntityType.VINDICATOR_SERVANT.get(), (timer) -> this.villagerConversionTime = timer)) {
                this.finishConversion(serverLevel);
            }
        }

        super.tick();
    }

    public boolean canConvert(){
        return this.isNatural();
    }

    public boolean isUnderWaterConverting() {
        return false;
    }

    public boolean isBaby() {
        return false;
    }

    public void setBaby(boolean pChildZombie) {
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected boolean convertsInWater() {
        return false;
    }

    protected void handleAttributes(float difficulty) {
        Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).addPermanentModifier(new AttributeModifier("random spawn bonus", this.random.nextDouble() * (double)0.05F, AttributeModifier.Operation.ADDITION));
        double d0 = this.random.nextDouble() * 1.5D * (double)difficulty;
        if (d0 > 1.0D) {
            Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE)).addPermanentModifier(new AttributeModifier("random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219149_, DifficultyInstance p_219150_) {
        if (this.canSpawnArmor()){
            super.populateDefaultEquipmentSlots(p_219149_, p_219150_);
        }
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    private void startConverting(@Nullable UUID p_34384_, int p_34385_) {
        this.conversionStarter = p_34384_;
        this.villagerConversionTime = p_34385_;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, p_34385_, Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level.broadcastEntityEvent(this, (byte)16);
    }

    public void handleEntityEvent(byte p_34372_) {
        if (p_34372_ == 16) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

        } else {
            super.handleEntityEvent(p_34372_);
        }
    }

    private void finishConversion(ServerLevel serverLevel) {
        Player player = null;
        if (this.conversionStarter != null) {
            player = serverLevel.getPlayerByUUID(this.conversionStarter);
        }
        Entity servant = MobUtil.convertTo(this, ModEntityType.VINDICATOR_SERVANT.get(), true, player);
        if (servant instanceof VindicatorServant vindicator){
            vindicator.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(servant.blockPosition()), MobSpawnType.CONVERSION, null, null);
            vindicator.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            if (!this.isSilent()) {
                serverLevel.levelEvent((Player)null, 1027, this.blockPosition(), 0);
            }
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, vindicator);
        }
    }

    private int getConversionProgress() {
        int i = 1;
        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; ++k) {
                for(int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; ++l) {
                    for(int i1 = (int)this.getZ() - 4; i1 < (int)this.getZ() + 4 && j < 14; ++i1) {
                        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos.set(k, l, i1));
                        if (blockstate.is(Blocks.IRON_BARS) || blockstate.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    public InteractionResult mobInteract(Player p_34394_, InteractionHand pHand) {
        ItemStack itemstack = p_34394_.getItemInHand(pHand);
        if (!this.level.isClientSide) {
            if (this.canConvert() && itemstack.is(Items.GOLDEN_APPLE)) {
                if (this.hasEffect(MobEffects.WEAKNESS)) {
                    if (!p_34394_.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    this.startConverting(p_34394_.getUUID(), this.random.nextInt(2401) + 3600);

                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.mobInteract(p_34394_, pHand);
    }

    public static class VindicatorMeleeAttackGoal extends NeutralZombieAttackGoal {
        public VindicatorMeleeAttackGoal(AbstractZombieVindicator p_34123_) {
            super(p_34123_, 1.0D, false);
        }

        protected double getAttackReachSqr(LivingEntity p_34125_) {
            if (this.mob.getVehicle() instanceof IRavager) {
                float f = this.mob.getVehicle().getBbWidth() - 0.1F;
                return (double)(f * 2.0F * f * 2.0F + p_34125_.getBbWidth());
            } else {
                return super.getAttackReachSqr(p_34125_);
            }
        }
    }
}
