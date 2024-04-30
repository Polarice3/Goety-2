package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.common.entities.ai.HuntDownPlayerGoal;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.listeners.IllagerAssaultListener;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.Maps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class IllagerSpawner {
    private int nextTick;

    public int tick(ServerLevel pLevel) {
        if (!MobsConfig.IllagerAssault.get()) {
            return 0;
        } else {
            RandomSource random = pLevel.random;
            --this.nextTick;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick += MobsConfig.IllagerAssaultSpawnFreq.get();
                if (random.nextInt(MobsConfig.IllagerAssaultSpawnChance.get()) != 0) {
                    return 0;
                } else {
                    int j = pLevel.players().size();
                    if (j < 1) {
                        return 0;
                    } else {
                        Player pPlayer = pLevel.players().get(random.nextInt(j));
                        int soulEnergy = Mth.clamp(SEHelper.getSoulAmountInt(pPlayer), 0, MobsConfig.IllagerAssaultSELimit.get());
                        if (pPlayer.isSpectator() || pPlayer.isCreative()) {
                            return 0;
                        } else if (SEHelper.getRestPeriod(pPlayer) > 0){
                            return 0;
                        } else if (pLevel.isCloseToVillage(pPlayer.blockPosition(), 2) && soulEnergy < MobsConfig.IllagerAssaultSELimit.get()) {
                            return 0;
                        } else if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get()) {
                            int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                            int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = pPlayer.blockPosition().mutable().move(k, 0, l);
                            if (!pLevel.isLoaded(blockpos$mutable)) {
                                return 0;
                            } else if (!pLevel.dimensionType().hasRaids()){
                                return 0;
                            } else {
                                Holder<Biome> holder = pLevel.getBiome(blockpos$mutable);
                                if (holder.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                    return 0;
                                } else if (pPlayer.blockPosition().getY() < pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY() - 32 && !pLevel.canSeeSky(pPlayer.blockPosition())){
                                    return 0;
                                } else if (!IllagerAssaultListener.ILLAGER_LIST.isEmpty()) {
                                    int i1 = 0;
                                    for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()){
                                        IllagerDataType data = IllagerAssaultListener.ILLAGER_LIST.get(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
                                        if (data != null){
                                            if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * data.thresholdTimes && pLevel.random.nextFloat() <= data.chance) {
                                                ++i1;
                                                int cost = (int) (soulEnergy / data.thresholdTimes);
                                                int total = Mth.clamp(cost / MobsConfig.IllagerAssaultSEThreshold.get(), 1, data.maxExtraAmount) + 1;
                                                int randomTotal = pLevel.random.nextInt(total) + data.initExtraAmount;
                                                for (int k1 = 0; k1 < randomTotal; ++k1) {
                                                    blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                                    if (k1 == 0) {
                                                        if (!this.spawnRaider(entityType, pLevel, blockpos$mutable, random, soulEnergy, pPlayer)) {
                                                            break;
                                                        }
                                                    } else {
                                                        this.spawnRaider(entityType, pLevel, blockpos$mutable, random, soulEnergy, pPlayer);
                                                    }

                                                    blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                                    blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                                }
                                            }
                                        }
                                    }
                                    if (soulEnergy >= MobsConfig.IllagerAssaultSELimit.get() && MobsConfig.SoulEnergyBadOmen.get()) {
                                        if (!pPlayer.hasEffect(MobEffects.BAD_OMEN)) {
                                            pPlayer.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, 0, false, false));
                                        }
                                    }
                                    if (CuriosFinder.hasCurio(pPlayer, ModItems.ALARMING_CHARM.get())){
                                        ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.RAID_HORN.get(), 64.0F, 1.0F));
                                    }
                                    return i1;
                                } else {
                                    return 0;
                                }
                            }
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }
    }

    public boolean spawnRaider(EntityType<?> entityType, ServerLevel worldIn, BlockPos pos, RandomSource random, int soulAmount, Player player){
        BlockState blockstate = worldIn.getBlockState(pos);
        if (entityType == null){
            return false;
        } else {
            Entity entity = entityType.create(worldIn);
            if (!(entity instanceof PathfinderMob)){
                return false;
            } else if (!NaturalSpawner.isValidEmptySpawnBlock(worldIn, pos, blockstate, blockstate.getFluidState(), entityType)) {
                return false;
            } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.RAVAGER, worldIn, MobSpawnType.PATROL, pos, random)) {
                return false;
            } else {
                PathfinderMob illager = (PathfinderMob) entity;
                illager.setPos((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
                ForgeEventFactory.onFinalizeSpawn(illager, worldIn, worldIn.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, null, null);
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                if (illager instanceof HuntingIllagerEntity huntingIllager && random.nextInt(4) == 0){
                    huntingIllager.setRider(true);
                }
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                    illager.setTarget(player);
                }
                this.upgradeIllagers(illager, soulAmount);
                worldIn.addFreshEntityWithPassengers(illager);
                return true;
            }
        }
    }

    public void upgradeIllagers(LivingEntity raider, int soulAmount){
        Level world = raider.level;
        if (soulAmount >= MobsConfig.IllagerAssaultSEThreshold.get() * 5) {
            ItemStack itemstack = raider.getMainHandItem().copy();
            if (itemstack.getItem() instanceof CrossbowItem){
                Map<Enchantment, Integer> map = Maps.newHashMap();
                if (world.getDifficulty() == Difficulty.HARD) {
                    map.put(Enchantments.QUICK_CHARGE, 2);
                } else {
                    map.put(Enchantments.QUICK_CHARGE, 1);
                }

                map.put(Enchantments.MULTISHOT, 1);
                EnchantmentHelper.setEnchantments(map, itemstack);
                raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
            if (itemstack.getItem() instanceof AxeItem){
                Map<Enchantment, Integer> map = Maps.newHashMap();
                int i = 1;
                if (world.getDifficulty() == Difficulty.HARD) {
                    i = 2;
                }
                map.put(Enchantments.SHARPNESS, i);
                EnchantmentHelper.setEnchantments(map, itemstack);
                raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
            if (itemstack.getItem() instanceof SwordItem){
                if (world.random.nextFloat() <= 0.25F && (!itemstack.is(Items.DIAMOND_SWORD)
                        || itemstack.getItem() instanceof SwordItem swordItem && swordItem.getDamage() < 7.0F)){
                    itemstack = new ItemStack(Items.DIAMOND_SWORD);
                }
                Map<Enchantment, Integer> map = Maps.newHashMap();
                int i = 2;
                if (world.getDifficulty() == Difficulty.HARD) {
                    i = 4;
                }
                map.put(Enchantments.SHARPNESS, i);
                map.put(Enchantments.FIRE_ASPECT, 2);
                EnchantmentHelper.setEnchantments(map, itemstack);
                raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
        }
    }

    public void forceSpawn(ServerLevel pLevel, Player pPlayer, CommandSourceStack pSource){
        RandomSource random = pLevel.random;
        int soulEnergy = SEHelper.getSoulAmountInt(pPlayer);
        if (soulEnergy > MobsConfig.IllagerAssaultSEThreshold.get()) {
            int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
            int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
            BlockPos.MutableBlockPos blockpos$mutable = pPlayer.blockPosition().mutable().move(k, 0, l);
            if (pLevel.isLoaded(blockpos$mutable)) {
                if (pPlayer.blockPosition().getY() < pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY() - 32 && !pLevel.canSeeSky(pPlayer.blockPosition())){
                    ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F));
                    pSource.sendFailure(Component.translatable("commands.goety.illager.spawn.failure_location", pPlayer.getDisplayName()));
                } else if (!IllagerAssaultListener.ILLAGER_LIST.isEmpty()){
                    for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()){
                        IllagerDataType data = IllagerAssaultListener.ILLAGER_LIST.get(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
                        if (data != null){
                            if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * data.thresholdTimes && pLevel.random.nextFloat() <= data.chance) {
                                int cost = (int) (soulEnergy / data.thresholdTimes);
                                int total = Mth.clamp(cost / MobsConfig.IllagerAssaultSEThreshold.get(), 1, data.maxExtraAmount) + 1;
                                int randomTotal = pLevel.random.nextInt(total) + data.initExtraAmount;
                                for (int k1 = 0; k1 < randomTotal; ++k1) {
                                    blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                    if (k1 == 0) {
                                        if (!this.spawnRaider(entityType, pLevel, blockpos$mutable, random, soulEnergy, pPlayer)) {
                                            break;
                                        }
                                    } else {
                                        this.spawnRaider(entityType, pLevel, blockpos$mutable, random, soulEnergy, pPlayer);
                                    }

                                    blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                    blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                }
                            }
                        }
                    }
                    if (CuriosFinder.hasCurio(pPlayer, ModItems.ALARMING_CHARM.get())){
                        ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.RAID_HORN.get(), 64.0F, 1.0F));
                    }
                    this.nextTick += MobsConfig.IllagerAssaultSpawnFreq.get();
                    pSource.sendSuccess(() -> Component.translatable("commands.goety.illager.spawn.success", pPlayer.getDisplayName()), false);
                } else {
                    ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F));
                    pSource.sendFailure(Component.translatable("commands.goety.illager.spawn.empty"));
                }
            } else {
                ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F));
                pSource.sendFailure(Component.translatable("commands.goety.illager.spawn.failure_location", pPlayer.getDisplayName()));
            }
        }
    }

    public static class IllagerDataType{
        public float thresholdTimes;
        public int maxExtraAmount;
        public int initExtraAmount;
        public float chance;

        public IllagerDataType(float thresholdTimes, int maxExtraAmount, int initExtraAmount, float chance) {
            this.thresholdTimes = thresholdTimes;
            this.maxExtraAmount = maxExtraAmount;
            this.initExtraAmount = initExtraAmount;
            this.chance = chance;
        }
    }

}
