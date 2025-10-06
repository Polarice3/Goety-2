package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Prisoner extends RaiderServant implements VillagerDataHolder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<Boolean> IS_TRADER = SynchedEntityData.defineId(Prisoner.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(Prisoner.class, EntityDataSerializers.VILLAGER_DATA);
    @Nullable
    private Tag gossips;
    @Nullable
    private CompoundTag tradeOffers;
    private int villagerXp;

    public Prisoner(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(6, new FollowOwnerGoal<>(this, 0.6D, 6.0F, 2.0F){
            @Override
            public boolean canUse() {
                if (Prisoner.this.getLeader() != null){
                    LivingEntity livingentity = Prisoner.this.getLeader();
                    if (livingentity == null) {
                        return false;
                    } else if (livingentity.isSpectator()) {
                        return false;
                    } else if (Prisoner.this.distanceToSqr(livingentity) < (double)(Mth.square(this.startDistance))) {
                        return false;
                    } else if (!Prisoner.this.isFollowing() || Prisoner.this.isCommanded()) {
                        return false;
                    } else if (Prisoner.this.getTarget() != null) {
                        return false;
                    } else {
                        this.owner = livingentity;
                        return true;
                    }
                }
                return super.canUse();
            }
        });
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_TRADER, false);
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    public void addAdditionalSaveData(CompoundTag p_34397_) {
        super.addAdditionalSaveData(p_34397_);
        VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(LOGGER::error).ifPresent((p_204072_) -> {
            p_34397_.put("VillagerData", p_204072_);
        });
        if (this.tradeOffers != null) {
            p_34397_.put("Offers", this.tradeOffers);
        }

        if (this.gossips != null) {
            p_34397_.put("Gossips", this.gossips);
        }

        p_34397_.putInt("Xp", this.villagerXp);
        p_34397_.putBoolean("Trader", this.isTrader());
    }

    public void readAdditionalSaveData(CompoundTag p_34387_) {
        super.readAdditionalSaveData(p_34387_);
        if (p_34387_.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_34387_.get("VillagerData")));
            dataresult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
        }

        if (p_34387_.contains("Offers", 10)) {
            this.tradeOffers = p_34387_.getCompound("Offers");
        }

        if (p_34387_.contains("Gossips", 9)) {
            this.gossips = p_34387_.getList("Gossips", 10);
        }

        if (p_34387_.contains("Xp", 3)) {
            this.villagerXp = p_34387_.getInt("Xp");
        }

        if (p_34387_.contains("Trader")) {
            this.setIsTrader(p_34387_.getBoolean("Trader"));
        }
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isTrader()) {
            return SoundEvents.WANDERING_TRADER_NO;
        }
        return SoundEvents.VILLAGER_NO;
    }

    protected SoundEvent getHurtSound(DamageSource p_35498_) {
        if (this.isTrader()) {
            return SoundEvents.WANDERING_TRADER_HURT;
        }
        return SoundEvents.VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        if (this.isTrader()) {
            return SoundEvents.WANDERING_TRADER_DEATH;
        }
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.getTrueOwner() instanceof RaiderServant raider) {
                if (raider.getLeader() != null && this.getLeader() != raider.getLeader()) {
                    this.setLeader(raider.getLeader());
                    if (raider.getLeader().getTrueOwner() != null) {
                        this.setTrueOwner(raider.getLeader().getTrueOwner());
                    }
                }
            }
            if (this.getLeader() == null && this.getMasterOwner() != null && this.isFollowing()) {
                float f = this.distanceTo(this.getMasterOwner());
                if (f > 6.0F) {
                    double d0 = (this.getMasterOwner().getX() - this.getX()) / (double)f;
                    double d1 = (this.getMasterOwner().getY() - this.getY()) / (double)f;
                    double d2 = (this.getMasterOwner().getZ() - this.getZ()) / (double)f;
                    this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
                    this.checkSlowFallDistance();
                }
            }
        }
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    @Override
    public void setVillagerData(VillagerData p_34376_) {
        VillagerData villagerdata = this.getVillagerData();
        if (villagerdata.getProfession() != p_34376_.getProfession()) {
            this.tradeOffers = null;
        }

        this.entityData.set(DATA_VILLAGER_DATA, p_34376_);
    }

    @Override
    public void setHostile(boolean hostile) {
    }

    @Override
    public boolean isHostile() {
        return false;
    }

    @Override
    public void onStopAttack() {
    }

    public boolean isTrader(){
        return this.entityData.get(IS_TRADER);
    }

    public void setIsTrader(boolean isTrader){
        this.entityData.set(IS_TRADER, isTrader);
    }

    public void setTradeOffers(CompoundTag p_34412_) {
        this.tradeOffers = p_34412_;
    }

    public void setGossips(Tag p_34392_) {
        this.gossips = p_34392_;
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int p_34374_) {
        this.villagerXp = p_34374_;
    }

    @Override
    public void mobSense() {
    }

    @Override
    public @Nullable LivingEntity getMarked() {
        return null;
    }

    @Override
    public @Nullable BlockPos getRaidPos() {
        return null;
    }

    @Override
    public boolean isRaiding() {
        return false;
    }

    @Override
    public boolean isCapturing() {
        return false;
    }

    public boolean canCelebrate(){
        return false;
    }

    public boolean canJoinPatrol() {
        return false;
    }

    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.6F * this.getEyeHeight(), (double)(this.getBbWidth() * 0.4F));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null) {
            boolean flag = this.getLeader() == null || this.distanceTo(this.getLeader()) > 8.0D;
            if (this.getMasterOwner() == null || this.distanceTo(this.getMasterOwner()) > 8.0D) {
                flag = true;
            }
            if (flag) {
                if ((this.getHealth() - amount) > 0.0F) {
                    if (this.level.getRandom().nextFloat() <= (amount / this.getHealth())) {
                        Player player = null;
                        if (source.getEntity() instanceof Player player1) {
                            player = player1;
                        }
                        this.unshackle(player);
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    public void unshackle(@Nullable Player player) {
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.CHAIN_BREAK, this.getSoundSource(), 1.0F, 2.0F);
            AbstractVillager villager = this.convertTo(EntityType.VILLAGER, true);
            if (this.isTrader()) {
                villager = this.convertTo(EntityType.WANDERING_TRADER, true);
            }
            if (villager == null) {
                return;
            }
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                ItemStack itemstack = this.getItemBySlot(equipmentslot);
                if (!itemstack.isEmpty()) {
                    if (EnchantmentHelper.hasBindingCurse(itemstack)) {
                        villager.getSlot(equipmentslot.getIndex() + 300).set(itemstack);
                    } else {
                        double d0 = this.getEquipmentDropChance(equipmentslot);
                        if (d0 > 1.0D) {
                            this.spawnAtLocation(itemstack);
                        }
                    }
                }
            }
            if (villager instanceof Villager villager1) {
                villager1.setVillagerData(this.getVillagerData());
                if (this.gossips != null) {
                    villager1.setGossips(this.gossips);
                }

                if (this.tradeOffers != null) {
                    villager1.setOffers(new MerchantOffers(this.tradeOffers));
                }
                villager1.setVillagerXp(this.villagerXp);
                villager1.refreshBrain(serverLevel);
                if (player instanceof ServerPlayer
                        && player != this.getMasterOwner()
                        && !MobUtil.areAllies(player, this.getMasterOwner())) {
                    serverLevel.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, player, villager1);
                    villager1.playSound(SoundEvents.VILLAGER_CELEBRATE);
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.HAPPY_VILLAGER, villager1);
                }
                if (this.getMasterOwner() instanceof Player player1) {
                    villager1.getGossips().add(player1.getUUID(), GossipType.MAJOR_NEGATIVE, 200);
                }
            } else if (villager instanceof WanderingTrader) {
                if (player instanceof ServerPlayer
                        && player != this.getMasterOwner()
                        && !MobUtil.areAllies(player, this.getMasterOwner())) {
                    villager.playSound(SoundEvents.WANDERING_TRADER_YES);
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.HAPPY_VILLAGER, villager);
                }
                if (this.getMasterOwner() instanceof Player player1) {
                    villager.setLastHurtByPlayer(player1);
                } else if (this.getMasterOwner() != null) {
                    villager.setLastHurtByMob(this.getMasterOwner());
                }
            }
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CHAIN.defaultBlockState()), villager);
            villager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
            villager.setHealth(this.getHealth());
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, villager);
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (pPlayer.isCrouching()) {
            this.unshackle(pPlayer);
            return InteractionResult.SUCCESS;
        } else if (this.getMasterOwner() != null && this.getMasterOwner() == pPlayer) {
            if (this.getLeader() != null) {
                this.setLeader(null);
                this.setFollowing();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
