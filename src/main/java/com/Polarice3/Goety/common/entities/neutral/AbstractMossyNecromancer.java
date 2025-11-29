package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.MuckWraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.ReaperServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.MossySkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.JungleZombieServant;
import com.Polarice3.Goety.common.entities.projectiles.SoulBolt;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.SoulBoltSpell;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class AbstractMossyNecromancer extends AbstractNecromancer{

    public AbstractMossyNecromancer(EntityType<? extends AbstractSkeletonServant> type, Level level) {
        super(type, level);
    }

    public void projectileGoal(int priority) {
        this.goalSelector.addGoal(priority, new NecromancerRangedGoal(this, 1.0D, 20, 10.0F));
    }

    public void summonSpells(int priority){
        this.goalSelector.addGoal(priority, new SummonServantSpell());
        this.goalSelector.addGoal(priority + 1, new SummonUndeadGoal());
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity p_33317_, float p_33318_) {
        if (this.getNecroLevel() <= 0) {
            new SoulBoltSpell().mobSpellResult(this, ModItems.WILD_STAFF.get().getDefaultInstance());
        } else {
            for (int i = -this.getNecroLevel(); i <= this.getNecroLevel(); i++) {
                Vec3 vector3d = this.getViewVector(1.0F);
                SoulBolt soulBolt = new SoulBolt(this, vector3d.x + (i / 10.0F), vector3d.y, vector3d.z + (i / 10.0F), this.level);
                soulBolt.setPos(this.getX() + vector3d.x / 2, this.getEyeY() - 0.2, this.getZ() + vector3d.z / 2);
                if (this.level.addFreshEntity(soulBolt)) {
                    SoundUtil.playSoulBolt(this);
                    this.swing(InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    public Summoned getDefaultSummon(){
        return new JungleZombieServant(ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(), this.level);
    }

    public Summoned getSummon(){
        Summoned summoned = getDefaultSummon();
        if (this.getSummonList().stream().anyMatch(entityType -> entityType.is(ModTags.EntityTypes.ZOMBIE_SERVANTS))) {
            if (this.level.random.nextBoolean()) {
                summoned = new JungleZombieServant(ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().stream().anyMatch(entityType -> entityType.is(ModTags.EntityTypes.SKELETON_SERVANTS))) {
            if (this.level.random.nextBoolean()) {
                summoned = new MossySkeletonServant(ModEntityType.MOSSY_SKELETON_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get())) {
            if (this.level.random.nextFloat() <= 0.05F) {
                summoned = new MuckWraithServant(ModEntityType.MUCK_WRAITH_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.REAPER_SERVANT.get())) {
            if (this.level.random.nextFloat() <= 0.05F) {
                summoned = new ReaperServant(ModEntityType.REAPER_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get())){
            if (this.level.random.nextFloat() <= 0.15F) {
                summoned = new VanguardServant(ModEntityType.VANGUARD_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.BLACKGUARD_SERVANT.get())) {
            if (this.level.random.nextFloat() <= 0.05F) {
                summoned = new BlackguardServant(ModEntityType.BLACKGUARD_SERVANT.get(), this.level);
            }
        }
        return summoned;
    }

    @Override
    public boolean summonVariants() {
        return this.level.isWaterAt(this.blockPosition());
    }

    public class SummonServantSpell extends SummoningSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned && owned.getTrueOwner() == AbstractMossyNecromancer.this;
            int i = AbstractMossyNecromancer.this.level.getEntitiesOfClass(LivingEntity.class, AbstractMossyNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                    , predicate).size();
            return super.canUse() && i < 6;
        }

        protected void castSpell(){
            if (AbstractMossyNecromancer.this.level instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 2; ++i1) {
                    Summoned summonedentity = AbstractMossyNecromancer.this.getSummon();
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractMossyNecromancer.this.blockPosition(), summonedentity, serverLevel);
                    summonedentity.setTrueOwner(AbstractMossyNecromancer.this);
                    summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summonedentity);
                    if (MobsConfig.NecromancerSummonsLife.get()) {
                        summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    }
                    summonedentity.setPersistenceRequired();
                    summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(AbstractMossyNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (serverLevel.addFreshEntity(summonedentity)){
                        SoundUtil.playNecromancerSummon(summonedentity);
                        ColorUtil colorUtil = new ColorUtil(0x2ac9cf);
                        ServerParticleUtil.windShockwaveParticle(serverLevel, colorUtil, 0.1F, 0.1F, 0.05F, -1, summonedentity.position());
                    }
                }
            }
        }

        @Override
        protected NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }
    }
}
