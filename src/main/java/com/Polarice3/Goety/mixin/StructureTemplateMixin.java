package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.world.processors.ModProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Based from @TelepathicGrunt's codes "<a href="https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.19.0-Forge/src/main/java/com/telepathicgrunt/repurposedstructures/mixin/structures/StructureTemplateMixin.java">...</a>".
 */
@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @Inject(
            method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z",
            at = @At(value = "HEAD")
    )
    private void repurposedstructures_preventAutoWaterlogging(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos1,
                                                              BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings,
                                                              RandomSource random, int flag, CallbackInfoReturnable<Boolean> cir) {

        if(structurePlaceSettings.getProcessors().stream().anyMatch(processor ->
                ((StructureProcessorAccessor)processor).callGetType() == ModProcessors.WATERLOGGING_STOP_PROCESSOR.get())) {
            structurePlaceSettings.setKeepLiquids(false);
        }
    }
}
