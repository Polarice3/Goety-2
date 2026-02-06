package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModShriekParticleOption;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class SignalerServant extends AbstractIllagerServant{
    public int hornUse;
    public int changeTick;
    public List<LivingEntity> hordeSaved = new ArrayList<>();

    public SignalerServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.MAX_HEALTH, AttributesConfig.SignalerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SignalerServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, 5.0F)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.SignalerServantFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SignalerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SignalerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.SignalerServantFollowRange.get());
    }

    public IllagerServantArmPose getArmPose() {
        if (this.isCelebrating()) {
            return IllagerServantArmPose.CELEBRATING;
        } else {
            return IllagerServantArmPose.NEUTRAL;
        }
    }

    @Override
    public double getAttributeValue(Attribute attribute) {
        double original = super.getAttributeValue(attribute);
        if (attribute == Attributes.FOLLOW_RANGE) {
            if (this.getAttribute(Attributes.FOLLOW_RANGE) == null) {
                original = AttributesConfig.SignalerServantFollowRange.get();
            }
            if (this.isUsingItem() && this.getUseItem().is(Items.SPYGLASS)) {
                original *= 2.0D;
            }
            double heightMap = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.blockPosition()).getY();
            if (original > heightMap) {
                double diff = original - heightMap;
                original += diff;
            }
        }
        return original;
    }

    @Override
    public ItemStack getUseItem() {
        if (this.getMainHandItem().is(Items.SPYGLASS) || this.getMainHandItem().is(Items.GOAT_HORN)) {
            if (!this.isCelebrating()) {
                return this.getMainHandItem();
            }
        }
        return super.getUseItem();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.isCelebrating()) {
                this.stopUsingItem();
            } else {
                if (this.hornUse > 0) {
                    --this.hornUse;
                    if (!this.getMainHandItem().is(Items.GOAT_HORN)) {
                        this.swapItemHand();
                        this.changeTick = 5;
                    }
                } else {
                    if (!this.getMainHandItem().is(Items.SPYGLASS)) {
                        this.swapItemHand();
                        this.changeTick = 5;
                    }
                }
                if (this.changeTick > 0) {
                    --this.changeTick;
                }
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_33306_) {
        return SoundEvents.PILLAGER_HURT;
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.populateDefaultWeapons(pLevel.getRandom(), pDifficulty);
        return data;
    }

    @Override
    public void populateDefaultWeapons(RandomSource randomSource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SPYGLASS));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.GOAT_HORN));
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && this.hornUse <= 0) {
            List<LivingEntity> hordeList = this.hordeOfTargets(target);
            if (this.getTarget() == null && !(!this.hordeSaved.isEmpty() && this.hordeSaved.contains(target))) {
                if (target instanceof Player || target.getMaxHealth() > this.getMaxHealth() * 2 || this.quantityOfTargets(target) >= 15) {
                    hordeList.sort(Comparator.comparingDouble(living -> living.distanceTo(this)));
                    Predicate<Mob> predicate = mob -> mob instanceof IServant servant && servant.isGuardingArea() && servant.getMasterOwner() == this.getMasterOwner() && mob != this;
                    Predicate<Mob> predicate2 = mob -> mob instanceof RaiderServant servant && servant.getLeader() != null && servant.getLeader().isGuardingArea() && servant.getMasterOwner() == this.getMasterOwner() && mob != this;
                    Predicate<Mob> mainPredicate = predicate.or(predicate2);
                    List<Mob> alliedList = this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE)), mainPredicate);
                    if (hordeList.stream().filter(livingEntity -> livingEntity instanceof Mob mob && mob.getTarget() instanceof Mob ally && mainPredicate.test(ally)).toList().size() >= hordeList.size()) {
                        return;
                    }
                    for (int i = 0; i < alliedList.size(); ++i) {
                        try {
                            Mob follower = alliedList.get(i);
                            int pick = hordeList.size() > 1 ? i % (hordeList.size() - 1) : 0;
                            LivingEntity target1 = hordeList.get(pick);
                            if (target1 != null && follower instanceof IServant servant) {
                                servant.setPriorityTarget(target1);
                            }
                        } catch (IndexOutOfBoundsException ignored) {
                            break;
                        }
                    }
                    for (Mob ally : alliedList) {
                        if (ally instanceof SignalerServant) {
                            ally.setTarget(target);
                        }
                    }
                    this.setPriorityTarget(target);
                    MobUtil.instaLook(this, target);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for(int j1 = 0; j1 < 5; ++j1) {
                            serverLevel.sendParticles(new ModShriekParticleOption(j1 * 5), this.getX(), this.getY() + this.getBbHeight(), this.getZ(), 0, 1.0D, 1.0D, 1.0D, 1.0F);
                        }
                    }
                    if (!hordeList.isEmpty()) {
                        this.hordeSaved = hordeList;
                    }
                    this.playSound(ModSounds.INTRUDER_ALERT.get(), (float) (this.getAttributeValue(Attributes.FOLLOW_RANGE) / 2.0F), this.getVoicePitch());
                    this.hornUse = 60;
                }
            }
        }
    }

    public int quantityOfTargets(LivingEntity mainTarget) {
        int size = 0;
        if (!this.hordeOfTargets(mainTarget).isEmpty()) {
            for (LivingEntity livingEntity : this.hordeOfTargets(mainTarget)) {
                if (livingEntity.getMaxHealth() >= this.getMaxHealth()) {
                    size += 3;
                } else if (livingEntity instanceof OwnableEntity ownable && ownable.getOwner() instanceof Player && MobUtil.isOwnedTargetable(this, ownable.getOwner())) {
                    size += 3;
                } else {
                    ++size;
                }
            }
        }
        return size;
    }

    public List<LivingEntity> hordeOfTargets(LivingEntity mainTarget) {
        return this.level.getEntitiesOfClass(LivingEntity.class, mainTarget.getBoundingBox().inflate(16.0F), MobUtil.ownedPredicate(this));
    }

    public void swapItemHand() {
        ItemStack toOffHand = this.getMainHandItem();
        ItemStack toMainHand = this.getOffhandItem();
        this.setItemInHand(InteractionHand.OFF_HAND, toOffHand);
        this.setItemInHand(InteractionHand.MAIN_HAND, toMainHand);
        this.stopUsingItem();
    }
}
