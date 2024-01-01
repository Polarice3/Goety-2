package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class ZombieRavager extends ModRavager {
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieRavager.class, EntityDataSerializers.BOOLEAN);
    private int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;
    
    public ZombieRavager(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 75.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONVERTING_ID, false);
    }

    public void addAdditionalSaveData(CompoundTag p_34397_) {
        super.addAdditionalSaveData(p_34397_);

        p_34397_.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            p_34397_.putUUID("ConversionPlayer", this.conversionStarter);
        }
    }

    public void readAdditionalSaveData(CompoundTag p_34387_) {
        super.readAdditionalSaveData(p_34387_);

        if (p_34387_.contains("ConversionTime", 99) && p_34387_.getInt("ConversionTime") > -1) {
            this.startConverting(p_34387_.hasUUID("ConversionPlayer") ? p_34387_.getUUID("ConversionPlayer") : null, p_34387_.getInt("ConversionTime"));
        }

    }

    @Override
    public double regularSpeed() {
        return 0.23D;
    }

    @Override
    public double aggressiveSpeed() {
        return 0.28D;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected boolean isSunSensitive() {
        return this.getArmor().isEmpty();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.ZOMBIE_RAVAGER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33359_) {
        return ModSounds.ZOMBIE_RAVAGER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.ZOMBIE_RAVAGER_DEATH.get();
    }

    protected SoundEvent getStepSound(){
        if (this.hasSaddle()) {
            return ModSounds.ZOMBIE_RAVAGER_STEP.get();
        } else {
            return SoundEvents.POLAR_BEAR_STEP;
        }
    }

    protected SoundEvent getAttackSound(){
        return ModSounds.ZOMBIE_RAVAGER_BITE.get();
    }

    protected SoundEvent getStunnedSound(){
        return ModSounds.ZOMBIE_RAVAGER_STUN.get();
    }

    protected SoundEvent getRoarSound(){
        return ModSounds.ZOMBIE_RAVAGER_ROAR.get();
    }

    @Override
    public void convertNewEquipment(Entity entity){
        if (!this.level.isClientSide) {
            if (entity instanceof Ravager) {
                this.equipSaddle(false);
                this.setHealth(((Ravager) entity).getHealth());
                this.updateArmor();
            } else if (entity instanceof ModRavager modRavager) {
                if (modRavager.hasSaddle()) {
                    this.equipSaddle(false);
                }
                this.setHealth(modRavager.getHealth());
                this.updateArmor();
            }
        }
    }

    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();
            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.villagerConversionTime = timer)) {
                this.finishConversion((ServerLevel)this.level);
            }
        }

        super.tick();
    }

    public InteractionResult mobInteract(Player p_34394_, InteractionHand p_34395_) {
        ItemStack itemstack = p_34394_.getItemInHand(p_34395_);
        if (itemstack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!p_34394_.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (!this.level.isClientSide) {
                    this.startConverting(p_34394_.getUUID(), this.random.nextInt(2401) + 3600);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(p_34394_, p_34395_);
        }
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    private void startConverting(@Nullable UUID p_34384_, int p_34385_) {
        this.conversionStarter = p_34384_;
        this.villagerConversionTime = p_34385_;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, p_34385_, Math.min(this.level.getDifficulty().getId() - 1, 0)));
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

    private void finishConversion(ServerLevel p_34399_) {
        ModRavager modRavager = this.convertTo(ModEntityType.MOD_RAVAGER.get(), false);
        if (modRavager != null) {
            modRavager.finalizeSpawn(p_34399_, p_34399_.getCurrentDifficultyAt(modRavager.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
            if (this.conversionStarter != null) {
                Player player = p_34399_.getPlayerByUUID(this.conversionStarter);
                if (player instanceof ServerPlayer) {
                    modRavager.setTrueOwner(player);
                }
            }

            if (this.hasSaddle()) {
                modRavager.equipSaddle(false);
            }
            modRavager.setHealth(this.getHealth());
            modRavager.updateArmor();

            modRavager.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            if (!this.isSilent()) {
                p_34399_.levelEvent((Player) null, 1027, this.blockPosition(), 0);
            }
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, modRavager);
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
                        BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos.set(k, l, i1));
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
}
