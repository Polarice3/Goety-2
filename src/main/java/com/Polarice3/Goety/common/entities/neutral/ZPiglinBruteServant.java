package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class ZPiglinBruteServant extends ZPiglinServant {
    public boolean summonExplosion;

    public ZPiglinBruteServant(EntityType<? extends ZPiglinServant> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ZPiglinBruteServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ZPiglinBruteServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.ZPiglinBruteServantArmor.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ZPiglinBruteServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ZPiglinBruteServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.ZPiglinBruteServantDamage.get());
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    public void setBaby(boolean pChildZombie) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.summonExplosion){
            if (this.tickCount % 20 == 0){
                this.summonExplosion = false;
            }
        }
    }

    public int xpReward(){
        return 20;
    }

    @Override
    public boolean ignoreExplosion() {
        if (summonExplosion){
            return true;
        }
        return super.ignoreExplosion();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);
        if (pReason == MobSpawnType.MOB_SUMMONED){
            this.summonExplosion = true;
        }
        for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
            this.setDropChance(equipmentslottype, 0.0F);
        }

        return pSpawnData;
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
    }
}
