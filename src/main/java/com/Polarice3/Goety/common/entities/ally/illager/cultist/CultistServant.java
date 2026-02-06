package com.Polarice3.Goety.common.entities.ally.illager.cultist;

import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class CultistServant extends RaiderServant {

    public CultistServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.getNavigation().setCanFloat(true);
    }

    public CultistServantArmPose getArmPose() {
        return CultistServantArmPose.CROSSED;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.62F;
    }

    public enum CultistServantArmPose {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        PRAYING,
        SPELL_AND_WEAPON,
        BOW_AND_ARROW,
        TORCH_AND_WEAPON,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        BOMB_AND_WEAPON,
        THROW_SPEAR,
        ITEM,
        DYING,
        NEUTRAL;
    }
}
