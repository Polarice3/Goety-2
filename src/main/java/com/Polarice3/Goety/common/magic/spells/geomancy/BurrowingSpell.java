package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Based on @direwolf20's Mining Gadget codes: <a href="https://github.com/Direwolf20-MC/MiningGadgets/blob/mc/1.20.1/src/main/java/com/direwolf20/mininggadgets/common/items/MiningGadget.java">...</a>
 */
public class BurrowingSpell extends EverChargeSpell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.BurrowingCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.BurrowingChargeUp.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.RUMBLE.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public SoundEvent loopSound(LivingEntity entityLiving) {
        return ModSounds.BURROW.get();
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.MAGNET.get());
        list.add(Enchantments.BLOCK_FORTUNE);
        list.add(Enchantments.SILK_TOUCH);
        return list;
    }

    @Override
    public void startSpell(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        if (entityLiving instanceof Player player){
            resetMiningProgress(worldIn, player);
        }
        super.startSpell(worldIn, entityLiving, staff);
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff, int useTimeRemaining) {
        if (entityLiving instanceof Player player){
            resetMiningProgress(worldIn, player);
        }
        super.stopSpell(worldIn, entityLiving, staff, useTimeRemaining);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int range = 16;
        int efficiency = 0;
        int burning = 0;
        if (WandUtil.enchantedFocus(entityLiving)){
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            efficiency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), entityLiving);
        }

        BlockHitResult blockHitResult = this.blockResult(worldIn, entityLiving, range);
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = worldIn.getBlockState(blockPos);
        if (blockState.isAir()){
            return;
        }
        float hardness = getHardness(blockPos, entityLiving, efficiency);
        hardness = (float) Math.floor(hardness);

        if (hardness == 0) {
            hardness = 1;
        }

        if (entityLiving instanceof Player player) {
            if (canMineBlock(worldIn, player, blockPos, blockState)) {
                int miningLevel = 1 + WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                Tier tier = miningLevel < 3 ? Tiers.IRON : miningLevel == 3 ? Tiers.DIAMOND : Tiers.NETHERITE;
                if (!TierSortingRegistry.isCorrectTierForDrops(tier, blockState)){
                    hardness = blockState.getDestroySpeed(worldIn, blockPos) * 5;
                }
                SoundType soundtype = blockState.getSoundType(worldIn, blockPos, null);
                BlockPos miningPos = SEHelper.getMiningPos(player);
                if (miningPos != null){
                    if (miningPos.getX() != blockPos.getX() || miningPos.getY() != blockPos.getY() || miningPos.getZ() != blockPos.getZ()){
                        SEHelper.setMiningProgress(player, 0);
                    }
                }
                SEHelper.increaseMiningProgress(player);
                SEHelper.setMiningPos(player, blockPos);
                destroyBlockProgress(worldIn, player.getId(), blockPos, (int) ((SEHelper.getMiningProgress(player) / hardness) * 10));
                if (SEHelper.getMiningProgress(player) % 4 == 0){
                    worldIn.playSound(null, blockPos, soundtype.getHitSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                }
                if (SEHelper.getMiningProgress(player) >= hardness){
                    ItemStack tempTool = new ItemStack(Items.IRON_PICKAXE);
                    int silk = WandUtil.getLevels(Enchantments.SILK_TOUCH, player);
                    int fortune = WandUtil.getLevels(Enchantments.BLOCK_FORTUNE, player);

                    if (silk > 0){
                        tempTool.enchant(Enchantments.SILK_TOUCH, silk);
                    } else if (fortune > 0){
                        tempTool.enchant(Enchantments.BLOCK_FORTUNE, fortune);
                    }
                    BlockEvent.BreakEvent breakEvent = fixForgeEventBreakBlock(blockState, player, worldIn, blockPos, silk, fortune);
                    MinecraftForge.EVENT_BUS.post(breakEvent);
                    if (breakEvent.isCanceled()) {
                        return;
                    }

                    if (TierSortingRegistry.isCorrectTierForDrops(tier, blockState)){
                        List<ItemStack> drops = Block.getDrops(blockState, worldIn, blockPos, null, player, tempTool);

                        int exp = blockState.getExpDrop(worldIn, worldIn.random, blockPos, fortune, silk);
                        boolean magnetMode = WandUtil.getLevels(ModEnchantments.MAGNET.get(), entityLiving) > 0;
                        for (ItemStack drop : drops) {
                            if (drop != null) {
                                if (burning > 0){
                                    Optional<SmeltingRecipe> optional = worldIn.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(drop.copy()), worldIn);
                                    if (optional.isPresent()){
                                        ItemStack smeltedItemStack = optional.get().getResultItem(worldIn.registryAccess()).copy();
                                        if (!smeltedItemStack.isEmpty()){
                                            drop = ItemHandlerHelper.copyStackWithSize(smeltedItemStack, drop.getCount() * smeltedItemStack.getCount());
                                        }
                                    }
                                }
                                if (magnetMode) {
                                    int wasPickedUp = ForgeEventFactory.onItemPickup(new ItemEntity(worldIn, blockPos.getX(), blockPos.getY(), blockPos.getZ(), drop), player);
                                    if (wasPickedUp == 0) {
                                        if (!player.addItem(drop)) {
                                            Block.popResource(worldIn, blockPos, drop);
                                        }
                                    }
                                } else {
                                    Block.popResource(worldIn, blockPos, drop);
                                }
                            }
                        }
                        if (magnetMode) {
                            if (exp > 0) {
                                player.giveExperiencePoints(exp);
                            }
                        } else {
                            if (exp > 0) {
                                blockState.getBlock().popExperience(worldIn, blockPos, exp);
                            }
                        }
                    }

                    worldIn.playSound(null, blockPos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                    worldIn.removeBlockEntity(blockPos);
                    worldIn.levelEvent(2001, blockPos, Block.getId(blockState));
                    worldIn.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());

                    // Add to the break stats
                    player.awardStat(Stats.BLOCK_MINED.get(blockState.getBlock()));
                    SEHelper.setMiningProgress(player, 0);
                    SEHelper.setMiningPos(player, null);
                    destroyBlockProgress(worldIn, player.getId(), blockPos, -1);
                }
            }
        }
    }

    public static void resetMiningProgress(Level level, Player player){
        if (level instanceof ServerLevel serverLevel) {
            if (SEHelper.getMiningProgress(player) > 0) {
                SEHelper.setMiningProgress(player, 0);
            }
            if (SEHelper.getMiningPos(player) != null) {
                destroyBlockProgress(serverLevel, player.getId(), SEHelper.getMiningPos(player), -1);
                SEHelper.setMiningPos(player, null);
            }
        }
    }

    public static void destroyBlockProgress(ServerLevel serverLevel, int p_8612_, BlockPos p_8613_, int p_8614_) {
        for(ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (serverplayer != null && serverplayer.level == serverLevel) {
                double d0 = (double)p_8613_.getX() - serverplayer.getX();
                double d1 = (double)p_8613_.getY() - serverplayer.getY();
                double d2 = (double)p_8613_.getZ() - serverplayer.getZ();
                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
                    serverplayer.connection.send(new ClientboundBlockDestructionPacket(p_8612_, p_8613_, p_8614_));
                }
            }
        }

    }

    public static boolean canMineBlock(Level world, Player player, BlockPos pos, BlockState state) {
        if (!player.mayBuild() || !world.mayInteract(player, pos)) {
            return false;
        }

        return isValid(pos, world) && !MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, state, player));
    }

    private static boolean isValid(BlockPos pos, Level world) {
        BlockState state = world.getBlockState(pos);

        if ((!state.getFluidState().isEmpty() && !state.hasProperty(BlockStateProperties.WATERLOGGED)) || world.isEmptyBlock(pos)) {
            return false;
        }

        if (state.getDestroySpeed(world, pos) < 0) {
            return false;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            return false;
        }

        return !(state.getBlock() instanceof DoorBlock);
    }

    private static float getHardness(BlockPos blockPos, LivingEntity player, int efficiency) {
        float hardness = 0;
        float toolSpeed = SpellConfig.BurrowingInitialSpeed.get();
        if (efficiency > 0) {
            toolSpeed += (efficiency * efficiency + 1);
        }

        if (MobEffectUtil.hasDigSpeed(player)) {
            toolSpeed *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
        }

        MobEffectInstance fatigue = player.getEffect(MobEffects.DIG_SLOWDOWN);
        if (fatigue != null) {
            float f1 = switch (fatigue.getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };

            toolSpeed *= f1;
        }

        Level world = player.level;
        BlockState state = world.getBlockState(blockPos);

        if (player instanceof Player player1) {
            toolSpeed = net.minecraftforge.event.ForgeEventFactory.getBreakSpeed(player1, state, toolSpeed, blockPos);
        }

        hardness += (state.getDestroySpeed(world, blockPos) * 30) / toolSpeed;

        return hardness;
    }

    private static BlockEvent.BreakEvent fixForgeEventBreakBlock(BlockState state, Player player, Level world, BlockPos pos, int silk, int fortune) {
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
        if (state != null) {
            event.setExpToDrop(state.getExpDrop(world, world.random, pos, fortune, silk));
        }

        return event;
    }

}
