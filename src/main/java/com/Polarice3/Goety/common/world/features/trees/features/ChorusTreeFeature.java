package com.Polarice3.Goety.common.world.features.trees.features;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.world.features.configs.ModTreeFeatureConfig;
import com.Polarice3.Goety.common.world.features.utils.ModFeatureLogic;
import com.Polarice3.Goety.common.world.features.utils.ModFeaturePlacers;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

/**
 * Stolen and based from @TeamTwilight: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.20.1/src/main/java/twilightforest/world/components/feature/trees/HollowTreeFeature.java">...</a>
 */
public class ChorusTreeFeature extends ModTreeFeature<ModTreeFeatureConfig> {
	public boolean isVoid;

	public ChorusTreeFeature(Codec<ModTreeFeatureConfig> config, boolean isVoid) {
		super(config);
		this.isVoid = isVoid;
	}

	@Override
	public boolean generate(WorldGenLevel world, RandomSource random, BlockPos pos, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, BiConsumer<BlockPos, BlockState> decorationPlacer, ModTreeFeatureConfig config) {
		int diameter = 2;
		int height = random.nextInt(2) + diameter * 4;

		// do we have enough height?
		if (world.isOutsideBuildHeight(pos.getY()) || world.isOutsideBuildHeight(pos.getY() + height + diameter)) {
			return false;
		}

		// check the top too
		int crownRadius = 8;
		for (int dx = -crownRadius; dx <= crownRadius; dx++) {
			for (int dz = -crownRadius; dz <= crownRadius; dz++) {
				for (int dy = 0; dy <= height + crownRadius; dy++) {
					Block whatsThere = world.getBlockState(pos.offset(dx, dy, dz)).getBlock();
					if (!whatsThere.defaultBlockState().isAir() && !(whatsThere instanceof LeavesBlock)) {
						return false;
					}
				}
			}
		}

		// check if we're on dirt or grass
		BlockState state = world.getBlockState(pos.below());
		if (!state.getBlock().canSustainPlant(state, world, pos.below(), Direction.UP, ModBlocks.CHORUS_SAPLING.get())) {
			return false;
		}

		// make a tree!

		// build the trunk
		buildTrunk(world, trunkPlacer, decorationPlacer, random, pos, diameter, height, config);

		// build the crown
		buildFullCrown(world, trunkPlacer, leavesPlacer, random, pos, diameter, height, config);

		// 3-5 couple branches on the way up...
		int numBranches = random.nextInt(3) + 3;
		for (int i = 0; i <= numBranches; i++) {
			int branchHeight = (int) (height * random.nextDouble() * 0.9) + (height / 10);
			double branchRotation = random.nextDouble();
			makeSmallBranch(world, trunkPlacer, leavesPlacer, random, pos, diameter, branchHeight, 4, branchRotation, 0.35D, true, config);
		}

		return true;
	}

	/**
	 * Build the crown of the tree
	 *
	 */
	protected void buildFullCrown(WorldGenLevel world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RandomSource  random, BlockPos pos, int diameter, int height, ModTreeFeatureConfig config) {
		int crownRadius = diameter * 4 + 2;
		int bvar = diameter + 2;

		// okay, let's do 3-5 main branches starting at the bottom of the crown
		// buildBranchRing(world, trunkPlacer, leavesPlacer, random, pos, diameter, height - crownRadius, 0, crownRadius, 0.35D, bvar, bvar + 2, 2, true, config);

		// then, let's do 3-5 medium branches at the crown middle
		buildBranchRing(world, trunkPlacer, leavesPlacer, random, pos, diameter, height - (crownRadius / 2), 0, crownRadius, 0.28D, bvar, bvar + 2, 0, true, config);

		// finally, let's do 2-4 main branches at the crown top
		buildBranchRing(world, trunkPlacer, leavesPlacer, random, pos, diameter, height, 0, crownRadius, 0.15D + 0.2D, 2, 4, 1, true, config);

		// and extra finally, let's do 3-6 medium branches going straight up
		// buildBranchRing(world, trunkPlacer, leavesPlacer, random, pos, diameter, height, 0, (crownRadius / 2), 0.05D, bvar, bvar + 2, 1, true, config);
	}

	/**
	 * Build a ring of branches around the tree
	 * size 0 = small, 1 = med, 2 = large, 3 = root
	 */
	protected void buildBranchRing(WorldGenLevel world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RandomSource random, BlockPos pos, int diameter, int branchHeight, int heightVar, int length, double tilt, int minBranches, int maxBranches, int size, boolean leafy, ModTreeFeatureConfig config) {
		//let's do this!
		int numBranches = random.nextInt(maxBranches - minBranches) + minBranches;
		double branchRotation = 1.0 / (numBranches + 1);
		double branchOffset = random.nextDouble();

		for (int i = 0; i <= numBranches; i++) {
			int dHeight;
			if (heightVar > 0) {
				dHeight = branchHeight - heightVar + random.nextInt(2 * heightVar);
			} else {
				dHeight = branchHeight;
			}

			if (size == 2) {
				makeLargeBranch(world, trunkPlacer, leavesPlacer, random, pos, diameter, dHeight, length - 3, i * branchRotation + branchOffset, tilt, leafy, config);
			} else if (size == 1) {
				makeMedBranch(world, trunkPlacer, leavesPlacer, random, pos, diameter, dHeight, length - 1, i * branchRotation + branchOffset, tilt, leafy, config);
			} else if (size != 3) {
				makeSmallBranch(world, trunkPlacer, leavesPlacer, random, pos, diameter, dHeight, length, i * branchRotation + branchOffset, tilt, leafy, config);
			}
		}
	}

	/**
	 * This function builds the hollow trunk of the tree
	 */
	protected void buildTrunk(LevelAccessor world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> decoPlacer, RandomSource random, BlockPos pos, int diameter, int height, ModTreeFeatureConfig config) {
		final int hollow = diameter >> 1;

		// go down 4 squares and fill in extra trunk as needed, in case we're on uneven terrain
		for (int dx = -diameter; dx <= diameter; dx++) {
			for (int dz = -diameter; dz <= diameter; dz++) {
				for (int dy = -4; dy < 0; dy++) {
					// determine how far we are from the center.
					int ax = Math.abs(dx);
					int az = Math.abs(dz);
					int dist = Math.max(ax, az) + (Math.min(ax, az) >> 1);

					if (dist <= diameter) {
						BlockPos dPos = pos.offset(dx, dy, dz);
						if (ModFeaturePlacers.hasAirAround(world, dPos)) {
							if (dist > hollow) {
								ModFeaturePlacers.placeIfValidTreePos(world, trunkPlacer, random, dPos, config.trunkProvider);
							} else {
								ModFeaturePlacers.placeIfValidTreePos(world, trunkPlacer, random, dPos, config.branchProvider);
							}
						}
					}
				}
			}
		}

		// build the trunk upwards
		for (int dx = -diameter; dx <= diameter; dx++) {
			for (int dz = -diameter; dz <= diameter; dz++) {
				for (int dy = 0; dy <= height; dy++) {
					BlockPos dPos = pos.offset(dx, dy, dz);
					// determine how far we are from the center.
					int ax = Math.abs(dx);
					int az = Math.abs(dz);
					int dist = (int) (Math.max(ax, az) + (Math.min(ax, az) * 0.5));

					// make a trunk!
					if (dist <= diameter && dist > hollow) {
						ModFeaturePlacers.placeIfValidTreePos(world, trunkPlacer, random, dPos, config.trunkProvider);
					}

					// fill it with lava!
					if (dist <= hollow) {
						if (this.isVoid) {
							world.setBlock(dPos, ModBlocks.VOID_FLUID.get().defaultBlockState(), 3);
						} else {
							world.setBlock(dPos, ModBlocks.CORRUPT_CHORUS_LOG.get().defaultBlockState(), 3);
						}
					}
				}
			}
		}
	}

	/**
	 * Make a branch!
	 */
	protected void makeMedBranch(LevelAccessor world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RandomSource  random, BlockPos pos, int diameter, int branchHeight, double length, double angle, double tilt, boolean leafy, ModTreeFeatureConfig config) {
		BlockPos src = ModFeatureLogic.translate(pos.above(branchHeight), diameter, angle, 0.5);
		makeMedBranch(world, trunkPlacer, leavesPlacer, random, src, length, angle, tilt, leafy, config);
	}

	/**
	 * Make a branch!
	 */
	protected void makeMedBranch(LevelAccessor world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RandomSource  random, BlockPos src, double length, double angle, double tilt, boolean leafy, ModTreeFeatureConfig config) {
		BlockPos dest = ModFeatureLogic.translate(src, length, angle, tilt);

		ModFeaturePlacers.drawBresenhamBranch(world, trunkPlacer, random, src, dest, config.branchProvider);

		// with leaves!

		if (leafy) {
			// and a blob at the end
			makeLeafBlob(world, leavesPlacer, random, dest, config);
			// ModFeaturePlacers.placeSpheroid(world, leavesPlacer, ModFeaturePlacers.VALID_TREE_POS, random, dest, 1.5F, 1.5F, config.leavesProvider);
		}

		// and several small branches

		int numShoots = random.nextInt(2) + 1;
		double angleInc, angleVar, outVar, tiltVar;

		angleInc = 0.8 / numShoots;

		for (int i = 0; i <= numShoots; i++) {

			angleVar = (angleInc * i) - 0.4;
			outVar = (random.nextDouble() * 0.8) + 0.2;
			tiltVar = (random.nextDouble() * 0.75) + 0.15;

			BlockPos bsrc = ModFeatureLogic.translate(src, length * outVar, angle, tilt);
			double slength = length * 0.4;

			makeSmallBranch(world, trunkPlacer, leavesPlacer, random, bsrc, slength, angle + angleVar, tilt * tiltVar, leafy, config);
		}
	}

	/**
	 * Make a small branch with a leaf blob at the end
	 */
	protected void makeSmallBranch(LevelAccessor world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RandomSource  random, BlockPos src, double length, double angle, double tilt, boolean leafy, ModTreeFeatureConfig config) {
		BlockPos dest = ModFeatureLogic.translate(src, length, angle, tilt);

		ModFeaturePlacers.drawBresenhamBranch(world, trunkPlacer, random, src, dest, config.branchProvider);

		if (leafy) {
			float leafRad = random.nextInt(2) + 1.5f;
			makeLeafBlob(world, leavesPlacer, random, dest, config);
			// ModFeaturePlacers.placeSpheroid(world, leavesPlacer, ModFeaturePlacers.VALID_TREE_POS, random, dest, leafRad, leafRad, config.leavesProvider);
		}
	}

	/**
	 * Make a small branch at a certain height
	 */
	protected void makeSmallBranch(LevelAccessor world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RandomSource  random, BlockPos pos, int diameter, int branchHeight, double length, double angle, double tilt, boolean leafy, ModTreeFeatureConfig config) {
		BlockPos src = ModFeatureLogic.translate(pos.above(branchHeight), diameter, angle, 0.5);
		makeSmallBranch(world, trunkPlacer, leavesPlacer, random, src, length, angle, tilt, leafy, config);
	}

	/**
	 * Make a large, branching "base" branch in a specific location.
	 * <p>
	 * The large branch will have 1-4 medium branches and several small branches too
	 */
	protected void makeLargeBranch(WorldGenLevel world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RandomSource  random, BlockPos src, double length, double angle, double tilt, boolean leafy, ModTreeFeatureConfig config) {
		BlockPos dest = ModFeatureLogic.translate(src, length, angle, tilt);

		// draw the main branch
		ModFeaturePlacers.drawBresenhamBranch(world, trunkPlacer, random, src, dest, config.branchProvider);

		// reinforce it
		int reinforcements = random.nextInt(3);
		for (int i = 0; i <= reinforcements; i++) {
			int vx = (i & 2) == 0 ? 1 : 0;
			int vy = (i & 1) == 0 ? 1 : -1;
			int vz = (i & 2) == 0 ? 0 : 1;
			ModFeaturePlacers.drawBresenhamBranch(world, trunkPlacer, random, src.offset(vx, vy, vz), dest, config.branchProvider);
		}

		if (leafy) {
			// add a leaf blob at the end
			ModFeaturePlacers.placeSpheroid(world, leavesPlacer, ModFeaturePlacers.VALID_TREE_POS, random, dest.above(), 3.5f, 3.5f, config.leavesProvider);
		}

		// go about halfway out and make a few medium branches.
		// the number of medium branches we can support depends on the length of the big branch
		// every other branch switches sides
		int numMedBranches = random.nextInt((int) (length / 6)) + random.nextInt(2) + 1;

		for (int i = 0; i <= numMedBranches; i++) {

			double outVar = (random.nextDouble() * 0.3) + 0.3;
			double angleVar = random.nextDouble() * 0.225 * ((i & 1) == 0 ? 1.0 : -1.0);
			BlockPos bsrc = ModFeatureLogic.translate(src, length * outVar, angle, tilt);

			makeMedBranch(world, trunkPlacer, leavesPlacer, random, bsrc, length * 0.6, angle + angleVar, tilt, leafy, config);
		}

		// make 1-2 small ones near the base
		int numSmallBranches = random.nextInt(2) + 1;
		for (int i = 0; i <= numSmallBranches; i++) {

			double outVar = (random.nextDouble() * 0.25) + 0.25;
			double angleVar = random.nextDouble() * 0.25 * ((i & 1) == 0 ? 1.0 : -1.0);
			BlockPos bsrc = ModFeatureLogic.translate(src, length * outVar, angle, tilt);

			makeSmallBranch(world, trunkPlacer, leavesPlacer, random, bsrc, Math.max(length * 0.3, 2), angle + angleVar, tilt, leafy, config);
		}
	}

	/**
	 * Make a large, branching "base" branch off of the tree
	 */
	protected void makeLargeBranch(WorldGenLevel world, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RandomSource  random, BlockPos pos, int diameter, int branchHeight, double length, double angle, double tilt, boolean leafy, ModTreeFeatureConfig config) {
		BlockPos src = ModFeatureLogic.translate(pos.above(branchHeight), diameter, angle, 0.5);
		makeLargeBranch(world, trunkPlacer, leavesPlacer, random, src, length, angle, tilt, leafy, config);
	}

	public static void makeLeafBlob(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> leafPlacer, RandomSource random, BlockPos leafPos, ModTreeFeatureConfig config) {
		ModFeaturePlacers.placeCircleOdd(world, leafPlacer, ModFeaturePlacers.VALID_TREE_POS, random, leafPos.below(), 2.0F, config.leavesProvider);
		ModFeaturePlacers.placeCircleOdd(world, leafPlacer, ModFeaturePlacers.VALID_TREE_POS, random, leafPos, 3.0F, config.leavesProvider);
		ModFeaturePlacers.placeCircleOdd(world, leafPlacer, ModFeaturePlacers.VALID_TREE_POS, random, leafPos.above(), 2.0F, config.leavesProvider);
	}
}
