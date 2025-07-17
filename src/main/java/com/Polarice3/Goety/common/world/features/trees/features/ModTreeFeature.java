package com.Polarice3.Goety.common.world.features.trees.features;

import com.Polarice3.Goety.common.world.features.configs.ModTreeFeatureConfig;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Stolen from @TeamTwilight: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.20.1/src/main/java/twilightforest/world/components/feature/trees/TFTreeFeature.java">...</a>
 */
public abstract class ModTreeFeature<T extends ModTreeFeatureConfig> extends Feature<T> {
	public ModTreeFeature(Codec<T> configIn) {
		super(configIn);
	}

	@Override
	public final boolean place(FeaturePlaceContext<T> context) {
		WorldGenLevel worldgenlevel = context.level();
		RandomSource randomsource = context.random();
		BlockPos blockpos = context.origin();
		T treeconfiguration = context.config();
		Set<BlockPos> set = Sets.newHashSet();
		Set<BlockPos> set1 = Sets.newHashSet();
		Set<BlockPos> set2 = Sets.newHashSet();
		Set<BlockPos> set3 = Sets.newHashSet();
		BiConsumer<BlockPos, BlockState> biconsumer = (pos, state) -> {
			set.add(pos.immutable());
			worldgenlevel.setBlock(pos, state, 19);
		};
		BiConsumer<BlockPos, BlockState> biconsumer1 = (pos, state) -> {
			set1.add(pos.immutable());
			worldgenlevel.setBlock(pos, state, 19);
		};
		BiConsumer<BlockPos, BlockState> biconsumer2 = (pos, state) -> {
			set2.add(pos.immutable());
			worldgenlevel.setBlock(pos, state, 19);
		};
		BiConsumer<BlockPos, BlockState> biconsumer3 = (pos, state) -> {
			set3.add(pos.immutable());
			worldgenlevel.setBlock(pos, state, 19);
		};
		boolean flag = this.generate(worldgenlevel, randomsource, blockpos, biconsumer, biconsumer1, biconsumer2, treeconfiguration);
		if (flag && (!set1.isEmpty() || !set2.isEmpty())) {
			if (!treeconfiguration.decorators.isEmpty()) {
				TreeDecorator.Context treedecorator$context = new TreeDecorator.Context(worldgenlevel, biconsumer3, randomsource, set1, set2, set);
				treeconfiguration.decorators.forEach((p_225282_) -> {
					p_225282_.place(treedecorator$context);
				});
			}

			return BoundingBox.encapsulatingPositions(Iterables.concat(set, set1, set2, set3)).map((boundingBox) -> {
				DiscreteVoxelShape discretevoxelshape = updateLeaves(worldgenlevel, boundingBox, set1, set3, set);
				StructureTemplate.updateShapeAtEdge(worldgenlevel, 3, discretevoxelshape, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
				return true;
			}).orElse(false);
		} else {
			return false;
		}
	}

	/**
	 * This works akin to the AbstractTreeFeature.generate, but put our branches and roots here
	 */
	protected abstract boolean generate(WorldGenLevel world, RandomSource random, BlockPos pos, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, BiConsumer<BlockPos, BlockState> decorationPlacer, T config);

	private static DiscreteVoxelShape updateLeaves(LevelAccessor p_225252_, BoundingBox p_225253_, Set<BlockPos> p_225254_, Set<BlockPos> p_225255_, Set<BlockPos> p_225256_) {
		DiscreteVoxelShape discretevoxelshape = new BitSetDiscreteVoxelShape(p_225253_.getXSpan(), p_225253_.getYSpan(), p_225253_.getZSpan());
		int i = 7;
		List<Set<BlockPos>> list = Lists.newArrayList();

		for(int j = 0; j < 7; ++j) {
			list.add(Sets.newHashSet());
		}

		for(BlockPos blockpos : Lists.newArrayList(Sets.union(p_225255_, p_225256_))) {
			if (p_225253_.isInside(blockpos)) {
				discretevoxelshape.fill(blockpos.getX() - p_225253_.minX(), blockpos.getY() - p_225253_.minY(), blockpos.getZ() - p_225253_.minZ());
			}
		}

		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		int k1 = 0;
		list.get(0).addAll(p_225254_);

		while(true) {
			while(k1 >= 7 || !list.get(k1).isEmpty()) {
				if (k1 >= 7) {
					return discretevoxelshape;
				}

				Iterator<BlockPos> iterator = list.get(k1).iterator();
				BlockPos blockpos1 = iterator.next();
				iterator.remove();
				if (p_225253_.isInside(blockpos1)) {
					if (k1 != 0) {
						BlockState blockstate = p_225252_.getBlockState(blockpos1);
						setBlockKnownShape(p_225252_, blockpos1, blockstate.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(k1)));
					}

					discretevoxelshape.fill(blockpos1.getX() - p_225253_.minX(), blockpos1.getY() - p_225253_.minY(), blockpos1.getZ() - p_225253_.minZ());

					for(Direction direction : Direction.values()) {
						blockpos$mutableblockpos.setWithOffset(blockpos1, direction);
						if (p_225253_.isInside(blockpos$mutableblockpos)) {
							int k = blockpos$mutableblockpos.getX() - p_225253_.minX();
							int l = blockpos$mutableblockpos.getY() - p_225253_.minY();
							int i1 = blockpos$mutableblockpos.getZ() - p_225253_.minZ();
							if (!discretevoxelshape.isFull(k, l, i1)) {
								BlockState blockstate1 = p_225252_.getBlockState(blockpos$mutableblockpos);
								OptionalInt optionalint = LeavesBlock.getOptionalDistanceAt(blockstate1);
								if (!optionalint.isEmpty()) {
									int j1 = Math.min(optionalint.getAsInt(), k1 + 1);
									if (j1 < 7) {
										list.get(j1).add(blockpos$mutableblockpos.immutable());
										k1 = Math.min(k1, j1);
									}
								}
							}
						}
					}
				}
			}

			++k1;
		}
	}

	private static void setBlockKnownShape(LevelWriter p_67257_, BlockPos p_67258_, BlockState p_67259_) {
		p_67257_.setBlock(p_67258_, p_67259_, 19);
	}
}
