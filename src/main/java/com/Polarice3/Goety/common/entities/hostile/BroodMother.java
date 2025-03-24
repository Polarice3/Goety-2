package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.AbstractBroodMother;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;

public class BroodMother extends AbstractBroodMother {
    private final ModServerBossInfo bossInfo;

    public BroodMother(EntityType<? extends AbstractBroodMother> type, Level worldIn) {
        super(type, worldIn);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
        this.setHostile(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity == this) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof Spider) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        this.alertSpooders();
        return super.hurt(pSource, pAmount);
    }

    protected void alertSpooders(){
        double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB axisalignedbb = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
        List<Mob> list = this.level.getEntitiesOfClass(Mob.class, axisalignedbb);

        for (Mob mob : list){
            if (mob.getTarget() == null && this.getLastHurtByMob() != null && !(this.getLastHurtByMob() instanceof Spider) && !MobUtil.areAllies(this.getLastHurtByMob(), this)) {
                if (mob instanceof Spider && !mob.getType().is(ModTags.EntityTypes.MINI_BOSSES) && !mob.getType().is(Tags.EntityTypes.BOSSES) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.getLastHurtByMob())) {
                    mob.setTarget(this.getLastHurtByMob());
                }
            }
        }
    }
}
