package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.SpiderMotherDenBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.BroodMother;
import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SpiderMotherDenBlock extends BaseEntityBlock {

    public SpiderMotherDenBlock() {
        super(Properties.of()
                .mapColor(MapColor.TERRACOTTA_WHITE)
                .strength(0.2F, 1200.0F)
                .sound(SoundType.WART_BLOCK)
                .noOcclusion());
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        if (!pPlayer.isCreative()) {
            BroodMother broodMother = new BroodMother(ModEntityType.BROOD_MOTHER.get(), pLevel);
            Vec3 vec3 = Vec3.atBottomCenterOf(pPos);
            broodMother.setPos(vec3);
            if (pLevel instanceof ServerLevel serverLevel) {
                broodMother.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pPos), MobSpawnType.MOB_SUMMONED, null, null);
            }
            if (MobUtil.validEntity(pPlayer)) {
                broodMother.setTarget(pPlayer);
            }
            broodMother.setPersistenceRequired();
            SummonCircleBoss summonCircle = new SummonCircleBoss(pLevel, vec3, broodMother);
            pLevel.addFreshEntity(summonCircle);
        }
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SpiderMotherDenBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof SpiderMotherDenBlockEntity denBlock)
                denBlock.tick();
        };
    }
}
