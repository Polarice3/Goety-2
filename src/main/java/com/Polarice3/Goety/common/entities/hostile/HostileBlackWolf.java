package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.world.structures.ModStructures;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HostileBlackWolf extends BlackWolf implements Enemy {

    public HostileBlackWolf(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData finalizeSpawn = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        float f = pDifficulty.getSpecialMultiplier();
        if (this.random.nextFloat() < f * 0.05F) {
            this.setUpgraded(true);
        }
        if (pReason == MobSpawnType.SPAWNER){
            if (BlockFinder.findStructure(pLevel.getLevel(), this.blockPosition(), ModStructures.GRAVEYARD_KEY)){
                Optional<? extends Registry<Structure>> optional = pLevel.getLevel().structureManager().registryAccess().registry(Registry.STRUCTURE_REGISTRY);
                if (optional.isPresent()) {
                    Structure structure = optional.get().get(ModStructures.GRAVEYARD_KEY);
                    if (structure != null) {
                        StructureStart structureStart = pLevel.getLevel().structureManager().getStructureWithPieceAt(this.blockPosition(), structure);
                        if (!structureStart.getPieces().isEmpty()) {
                            this.setBoundPos(structureStart.getBoundingBox().getCenter());
                        }
                    }
                }
            }
        }
        return finalizeSpawn;
    }
}
