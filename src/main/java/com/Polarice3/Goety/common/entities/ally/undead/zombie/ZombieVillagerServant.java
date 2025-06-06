package com.Polarice3.Goety.common.entities.ally.undead.zombie;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

public class ZombieVillagerServant extends ZombieServant implements InventoryCarrier, VillagerDataHolder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieVillagerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(ZombieVillagerServant.class, EntityDataSerializers.VILLAGER_DATA);
    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);
    public int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;
    @Nullable
    private Tag gossips;
    @Nullable
    private CompoundTag tradeOffers;
    private int villagerXp;
    private final SimpleContainer inventory = new SimpleContainer(8);

    public ZombieVillagerServant(EntityType<? extends ZombieVillagerServant> p_34368_, Level p_34369_) {
        super(p_34368_, p_34369_);
        BuiltInRegistries.VILLAGER_PROFESSION.getRandom(this.random).ifPresent((p_255550_) -> {
            this.setVillagerData(this.getVillagerData().setProfession(p_255550_.value()));
        });
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        /*this.goalSelector.addGoal(0, new HarvestGoal(this));
        this.goalSelector.addGoal(1, new BonemealGoal(this));*/
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONVERTING_ID, false);
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

        p_34397_.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            p_34397_.putUUID("ConversionPlayer", this.conversionStarter);
        }

        p_34397_.putInt("Xp", this.villagerXp);
        this.writeInventoryToTag(p_34397_);
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

        if (p_34387_.contains("ConversionTime", 99) && p_34387_.getInt("ConversionTime") > -1) {
            this.startConverting(p_34387_.hasUUID("ConversionPlayer") ? p_34387_.getUUID("ConversionPlayer") : null, p_34387_.getInt("ConversionTime"));
        }

        if (p_34387_.contains("Xp", 3)) {
            this.villagerXp = p_34387_.getInt("Xp");
        }

        this.readInventoryFromTag(p_34387_);
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();
            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.villagerConversionTime = timer)) {
                this.finishConversion(serverLevel);
            }
        }

        super.tick();
    }

    public boolean canConvert(){
        return this.isNatural();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!this.level.isClientSide) {
            if (this.canConvert() && itemstack.is(Items.GOLDEN_APPLE)) {
                if (this.hasEffect(MobEffects.WEAKNESS)) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    this.startConverting(pPlayer.getUUID(), this.random.nextInt(2401) + 3600);

                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    protected boolean convertsInWater() {
        return false;
    }

    public boolean removeWhenFarAway(double p_34414_) {
        return !this.isConverting() && this.villagerXp == 0;
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

    private void finishConversion(ServerLevel p_34399_) {
        Villager villager = this.convertTo(EntityType.VILLAGER, false);

        if (villager == null){
            return;
        }
        for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            ItemStack itemstack = this.getItemBySlot(equipmentslot);
            if (!itemstack.isEmpty()) {
                if (EnchantmentHelper.hasBindingCurse(itemstack)) {
                    villager.getSlot(equipmentslot.getIndex() + 300).set(itemstack);
                } else {
                    double d0 = (double)this.getEquipmentDropChance(equipmentslot);
                    if (d0 > 1.0D) {
                        this.spawnAtLocation(itemstack);
                    }
                }
            }
        }

        villager.setVillagerData(this.getVillagerData());
        if (this.gossips != null) {
            villager.setGossips(this.gossips);
        }

        if (this.tradeOffers != null) {
            villager.setOffers(new MerchantOffers(this.tradeOffers));
        }

        villager.setVillagerXp(this.villagerXp);
        villager.finalizeSpawn(p_34399_, p_34399_.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData)null, (CompoundTag)null);
        villager.refreshBrain(p_34399_);
        if (this.conversionStarter != null) {
            Player player = p_34399_.getPlayerByUUID(this.conversionStarter);
            if (player instanceof ServerPlayer) {
                p_34399_.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, player, villager);
            }
        }

        villager.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        if (!this.isSilent()) {
            p_34399_.levelEvent((Player)null, 1027, this.blockPosition(), 0);
        }
        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, villager);
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

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public SlotAccess getSlot(int p_149995_) {
        int i = p_149995_ - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149995_);
    }

    protected void pickUpItem(ItemEntity p_35467_) {
        InventoryCarrier.pickUpItem(this, this, p_35467_);
    }

    @Override
    public boolean canPickUpLoot() {
        return super.canPickUpLoot() || this.getVillagerData().getProfession() == VillagerProfession.FARMER;
    }

    public boolean wantsToPickUp(ItemStack p_35543_) {
        Item item = p_35543_.getItem();
        return (WANTED_ITEMS.contains(item) || this.getVillagerData().getProfession().requestedItems().contains(item)) && this.getInventory().canAddItem(p_35543_);
    }

    public boolean hasFarmSeeds() {
        return this.getInventory().hasAnyMatching((p_281096_) -> {
            return p_281096_.is(ItemTags.VILLAGER_PLANTABLE_SEEDS);
        });
    }

    public float getVoicePitch() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    public SoundEvent getHurtSound(DamageSource p_34404_) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    public SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    public void setTradeOffers(CompoundTag p_34412_) {
        this.tradeOffers = p_34412_;
    }

    public void setGossips(Tag p_34392_) {
        this.gossips = p_34392_;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34378_, DifficultyInstance p_34379_, MobSpawnType p_34380_, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag p_34382_) {
        spawnDataIn = super.finalizeSpawn(p_34378_, p_34379_, p_34380_, spawnDataIn, p_34382_);
        this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(p_34378_.getBiome(this.blockPosition()))));
        return spawnDataIn;
    }

    public void setVillagerData(VillagerData p_34376_) {
        VillagerData villagerdata = this.getVillagerData();
        if (villagerdata.getProfession() != p_34376_.getProfession()) {
            this.tradeOffers = null;
        }

        this.entityData.set(DATA_VILLAGER_DATA, p_34376_);
    }

    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int p_34374_) {
        this.villagerXp = p_34374_;
    }
}
