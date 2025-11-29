package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVindicatorServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class VindicatorServant extends AbstractIllagerServant {
    private static final String TAG_JOHNNY = "Johnny";
    static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (p_34082_) -> p_34082_ == Difficulty.NORMAL || p_34082_ == Difficulty.HARD;
    boolean isJohnny;

    public VindicatorServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new VindicatorBreakDoorGoal(this));
        this.goalSelector.addGoal(4, new VindicatorMeleeAttackGoal(this));
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(4, new VindicatorJohnnyAttackGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double)0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.VindicatorServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.VindicatorServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.VindicatorServantDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.VindicatorServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.VindicatorServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.VindicatorServantDamage.get());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(TAG_JOHNNY, 99)) {
            this.isJohnny = compound.getBoolean(TAG_JOHNNY);
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.isJohnny) {
            compound.putBoolean(TAG_JOHNNY, true);
        }
    }

    @Override
    public boolean canOpenDoors() {
        return true;
    }

    public IllagerServantArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerServantArmPose.ATTACKING;
        } else {
            return this.isCelebrating() ? IllagerServantArmPose.CELEBRATING : IllagerServantArmPose.CROSSED;
        }
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34088_, DifficultyInstance p_34089_, MobSpawnType p_34090_, @Nullable SpawnGroupData p_34091_, @Nullable CompoundTag p_34092_) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(p_34088_, p_34089_, p_34090_, p_34091_, p_34092_);
        RandomSource randomsource = p_34088_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_34089_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_34089_);
        return spawngroupdata;
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219149_, DifficultyInstance p_219150_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public void setCustomName(@Nullable Component p_34096_) {
        super.setCustomName(p_34096_);
        if (!this.isJohnny && p_34096_ != null && p_34096_.getString().equals(TAG_JOHNNY)) {
            this.isJohnny = true;
        }
    }

    @Nullable
    @Override
    public LivingEntity getTrueOwner() {
        if (this.isJohnny){
            return null;
        } else {
            return super.getTrueOwner();
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource p_34103_) {
        return SoundEvents.VINDICATOR_HURT;
    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.level.isClientSide) {
            if (this.getIdol() == null) {
                if (this.getTrueOwner() != null) {
                    if (CuriosFinder.hasNamelessSet(this.getTrueOwner())) {
                        ZombieVindicatorServant servant = this.convertTo(ModEntityType.ZOMBIE_VINDICATOR_SERVANT.get(), true);
                        if (servant != null) {
                            servant.setTrueOwner(this.getTrueOwner());
                            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, servant);
                            if (!this.isSilent()) {
                                this.level.levelEvent((Player) null, 1026, this.blockPosition(), 0);
                            }
                        }
                    }
                }
            }
        }
        super.die(pCause);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!(pPlayer.getOffhandItem().getItem() instanceof IWand)) {
                if (item instanceof AxeItem || itemstack.is(ItemTags.AXES) || itemstack.is(ModTags.Items.VINDICATOR_WEAPONS)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    static class VindicatorBreakDoorGoal extends BreakDoorGoal {
        public VindicatorBreakDoorGoal(Mob p_34112_) {
            super(p_34112_, 6, VindicatorServant.DOOR_BREAKING_PREDICATE);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canContinueToUse() {
            VindicatorServant vindicator = (VindicatorServant)this.mob;
            return vindicator.isRaiding() && super.canContinueToUse();
        }

        public boolean canUse() {
            VindicatorServant vindicator = (VindicatorServant)this.mob;
            return vindicator.isRaiding() && vindicator.random.nextInt(reducedTickDelay(10)) == 0 && super.canUse();
        }

        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }

    static class VindicatorJohnnyAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
        public VindicatorJohnnyAttackGoal(VindicatorServant p_34117_) {
            super(p_34117_, LivingEntity.class, 0, true, true, LivingEntity::attackable);
        }

        public boolean canUse() {
            return ((VindicatorServant)this.mob).isJohnny && super.canUse();
        }

        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }

    static class VindicatorMeleeAttackGoal extends ModMeleeAttackGoal {
        public VindicatorMeleeAttackGoal(VindicatorServant p_34123_) {
            super(p_34123_, 1.0D, false);
        }
    }
}
