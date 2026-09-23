package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.api.entities.ally.illager.IllagerType;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class OminousPyreBlockEntity extends BarracksBlockEntity {
    private static final long CACHE_TICKS = 100L;
    @Nullable
    private EntityType<? extends Mob> cachedTrainedMob;
    @Nullable
    private ITrainIllager cachedIllagerType;
    private long cacheExpiry = -1L;

    public OminousPyreBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.OMINOUS_PYRE.get(), p_155229_, p_155230_);
    }

    private void refreshCache(Level level, BlockPos pos) {
        long now = level.getGameTime();
        if (now >= this.cacheExpiry) {
            this.cacheExpiry = now + CACHE_TICKS;
            this.cachedTrainedMob = this.getTrainedMob(level, pos);
            this.cachedIllagerType = this.cachedTrainedMob != null ? IllagerType.getIllagerFromType(level, pos, this.getRange(), this.cachedTrainedMob) : null;
        }
    }

    public void invalidateTrainingCache() {
        this.cacheExpiry = -1L;
    }

    @Override
    public Predicate<Mob> trainingRequirements(Level level, BlockPos blockPos) {
        this.refreshCache(level, blockPos);
        EntityType<? extends Mob> target = this.cachedTrainedMob;
        ITrainIllager illagerType = this.cachedIllagerType;
        if (target == null || illagerType == null) {
            return mob -> false;
        }
        return mob -> mob instanceof AbstractIllagerServant illager
                && illagerType.mobCanTrainToType(illager, target)
                && (illager.getTrueOwner() == this.getTrueOwner() || illager.getTrueOwner() == null);
    }

    @Override
    public boolean autoMode() {
        return MobsConfig.IllagerServantAutoTrain.get();
    }

    @Override
    public void setVariant(Level level, BlockPos blockPos) {
        if (!IllagerType.getIllagerList().isEmpty()) {
            EntityType<? extends Mob> entityType = null;
            for (IllagerType illagerType : IllagerType.values()) {
                if (illagerType.getIllager() != null) {
                    if (illagerType.getIllager().canSpawn(level, blockPos, this.getRange())) {
                        entityType = illagerType.getIllager().getIllager(level, blockPos, this.getRange());
                        break;
                    }
                }
            }
            this.setEntityType(entityType);
        }
        this.invalidateTrainingCache();
    }

    @Override
    public void setRemoved() {
        this.invalidateTrainingCache();
        super.setRemoved();
    }
}
