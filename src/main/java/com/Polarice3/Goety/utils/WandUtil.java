package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.neutral.AbstractVine;
import com.Polarice3.Goety.common.entities.neutral.TotemicBomb;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.projectiles.IceBouquet;
import com.Polarice3.Goety.common.entities.projectiles.Spike;
import com.Polarice3.Goety.common.entities.util.TridentStorm;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WandUtil {

    private static boolean isMatchingItem(ItemStack itemStack) {
        return itemStack.getItem() instanceof IWand;
    }

    public static ItemStack findWandOnHand(LivingEntity livingEntity, InteractionHand hand) {
        ItemStack foundStack = ItemStack.EMPTY;
        if (isMatchingItem(livingEntity.getItemInHand(hand))){
            foundStack = livingEntity.getItemInHand(hand);
        }

        return foundStack;
    }

    public static ItemStack findWand(LivingEntity livingEntity) {
        ItemStack foundStack = ItemStack.EMPTY;
        if (isMatchingItem(livingEntity.getMainHandItem())){
            foundStack = livingEntity.getMainHandItem();
        } else if (isMatchingItem(livingEntity.getOffhandItem())){
            foundStack = livingEntity.getOffhandItem();
        }

        return foundStack;
    }

    public static ItemStack findFocusOnHand(LivingEntity livingEntity, InteractionHand hand){
        ItemStack foundStack = ItemStack.EMPTY;
        if (!findWandOnHand(livingEntity, hand).isEmpty()){
            if (!IWand.getFocus(findWandOnHand(livingEntity, hand)).isEmpty()) {
                foundStack = IWand.getFocus(findWandOnHand(livingEntity, hand));
            }
        }

        return foundStack;
    }

    public static ItemStack findFocus(LivingEntity livingEntity){
        ItemStack foundStack = ItemStack.EMPTY;
        if (!findWand(livingEntity).isEmpty()){
            if (!IWand.getFocus(findWand(livingEntity)).isEmpty()) {
                foundStack = IWand.getFocus(findWand(livingEntity));
            }
        }

        return foundStack;
    }

    public static ItemStack findFocusInInv(@Nullable Player player){
        ItemStack foundStack = ItemStack.EMPTY;
        if (player != null) {
            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                ItemStack inSlot = player.getInventory().getItem(i);
                if (inSlot.getItem() instanceof IFocus) {
                    foundStack = inSlot;
                }
            }
        }
        return foundStack;
    }

    public static ISpell getSpellOnHand(LivingEntity livingEntity, InteractionHand hand){
        if (WandUtil.findFocusOnHand(livingEntity, hand).getItem() instanceof IFocus magicFocus){
            if (magicFocus.getSpell() != null){
                return magicFocus.getSpell();
            }
        }
        return null;
    }

    public static ISpell getSpell(LivingEntity livingEntity){
        if (WandUtil.findFocus(livingEntity).getItem() instanceof IFocus magicFocus){
            if (magicFocus.getSpell() != null){
                return magicFocus.getSpell();
            }
        }
        return null;
    }

    public static int getShots(LivingEntity livingEntity){
        if (livingEntity.isUsingItem() && livingEntity.getUseItem().getItem() instanceof IWand wand){
            return wand.ShotsFired(livingEntity.getUseItem());
        }
        return 0;
    }

    public static boolean hasFocusInInv(@Nullable Player player){
        return !findFocusInInv(player).isEmpty();
    }

    public static boolean enchantedFocus(LivingEntity livingEntity){
        return !findFocus(livingEntity).isEmpty() && findFocus(livingEntity).isEnchanted();
    }

    public static int getLevels(Enchantment enchantment, LivingEntity livingEntity){
        if (enchantedFocus(livingEntity)) {
            return findFocus(livingEntity).getEnchantmentLevel(enchantment);
        } else {
            return 0;
        }
    }

    public static float damageMultiply() {
        return SpellConfig.SpellDamageMultiplier.get() * SpellConfig.SpellDamageMultiplierDecimal.get().floatValue();
    }

    public static int getPotencyLevel(LivingEntity livingEntity) {
        return getLevels(ModEnchantments.POTENCY.get(), livingEntity) * SpellConfig.PotencyPower.get();
    }

    public static int getRangeLevel(LivingEntity livingEntity) {
        return getLevels(ModEnchantments.RANGE.get(), livingEntity) * 2;
    }

    public static SpellStat getStats(LivingEntity livingEntity, ISpell spell) {
        return spell.defaultStats()
                .increasePotency(ModAttributes.getPotency(livingEntity, spell))
                .increaseDuration(ModAttributes.getDuration(livingEntity))
                .increaseRange(ModAttributes.getRange(livingEntity))
                .increaseRadius(ModAttributes.getRadius(livingEntity))
                .increaseBurning(ModAttributes.getBurning(livingEntity))
                .increaseVelocity(ModAttributes.getVelocity(livingEntity));
    }

    public static void chainLightning(LivingEntity pTarget, @Nullable LivingEntity pAttacker, double range, float damage){
        chainLightning(pTarget, pAttacker, range, damage, false);
    }

    public static void chainLightning(LivingEntity pTarget, @Nullable LivingEntity pAttacker, ColorUtil colorUtil, double range, float damage){
        chainLightning(pTarget, pAttacker, colorUtil, range, damage, false);
    }

    public static void chainLightning(LivingEntity pTarget, @Nullable LivingEntity pAttacker, double range, float damage, boolean small) {
        chainLightning(pTarget, pAttacker, new ColorUtil(0xb1abf1), range, damage, small);
    }

    public static void chainLightning(LivingEntity pTarget, @Nullable LivingEntity pAttacker, ColorUtil colorUtil, double range, float damage, boolean small) {
        Level level = pTarget.level;

        List<Entity> harmed = new ArrayList<>();
        Predicate<Entity> selector = entity -> entity instanceof LivingEntity livingEntity && !harmed.contains(livingEntity);
        if (pAttacker != null){
            selector = selector.and(entity -> entity instanceof LivingEntity livingEntity && livingEntity != pAttacker && MobUtil.canAttack(pAttacker, livingEntity));
        }
        Entity prevTarget = pTarget;

        int hops = level.isThundering() ? 8 : 4;

        for (int i = 0; i < hops; i++) {
            AABB aabb = new AABB(Vec3Util.subtract(prevTarget.position(), range), Vec3Util.add(prevTarget.position(), range));
            Entity finalPrevTarget = prevTarget;
            List<Entity> entities = level.getEntities(prevTarget, aabb, selector.and(entity -> MobUtil.hasLineOfSight(finalPrevTarget, entity)));
            if (!entities.isEmpty()) {
                Entity target = entities.get(level.getRandom().nextInt(entities.size()));
                DamageSource damageSource = ModDamageSource.getDamageSource(pTarget.level, ModDamageSource.SHOCK);
                if (pAttacker != null){
                    damageSource = ModDamageSource.directShock(pAttacker);
                }
                if (target.hurt(damageSource, damage)) {
                    if (prevTarget != target) {
                        Vec3 vec3 = prevTarget.getEyePosition();
                        Vec3 vec31 = target.getEyePosition();
                        if (small){
                            ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, colorUtil, 5));
                        } else {
                            ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, colorUtil, 8));
                        }
                    }
                }

                harmed.add(target);
                prevTarget = target;
                damage--;
            }
        }
    }

    public static void spawnFangs(LivingEntity livingEntity, double pPosX, double pPosZ, double PPPosY, double pOPosY, float pYRot, int pWarmUp) {
        spawnFangs(livingEntity, pPosX, pPosZ, PPPosY, pOPosY, pYRot, pWarmUp, 0, 0);
    }

    public static void spawnFangs(LivingEntity livingEntity, double pPosX, double pPosZ, double PPPosY, double pOPosY, float pYRot, int pWarmUp, int pPotency, int pBurning) {
        BlockPos blockpos = BlockPos.containing(pPosX, pOPosY, pPosZ);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = livingEntity.level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(livingEntity.level, blockpos1, Direction.UP)) {
                if (!livingEntity.level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = livingEntity.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(PPPosY) - 1);

        if (flag) {
            Fangs fangEntity = new Fangs(livingEntity.level, pPosX, (double)blockpos.getY() + d0, pPosZ, pYRot, pWarmUp, livingEntity);
            if (livingEntity instanceof Player player){
                if (WandUtil.enchantedFocus(player)){
                    if (WandUtil.getLevels(ModEnchantments.ABSORB.get(), player) != 0){
                        fangEntity.setAbsorbing(true);
                    }
                    if (WandUtil.getLevels(ModEnchantments.SOUL_EATER.get(), player) > 0){
                        fangEntity.setSoulEater(WandUtil.getLevels(ModEnchantments.SOUL_EATER.get(), player));
                    }
                }
            }
            fangEntity.setDamage(pPotency);
            fangEntity.setBurning(pBurning);
            livingEntity.level.addFreshEntity(fangEntity);
        }

    }

    public static void spawnSpikes(LivingEntity livingEntity, double pPosX, double pPosZ, double PPPosY, double pOPosY, float pYRot, int pWarmUp, SpellStat spellStat) {
        BlockPos blockpos = BlockPos.containing(pPosX, pOPosY, pPosZ);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = livingEntity.level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(livingEntity.level, blockpos1, Direction.UP)) {
                if (!livingEntity.level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = livingEntity.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(PPPosY) - 1);

        if (flag) {
            Spike spike = new Spike(livingEntity.level, pPosX, (double)blockpos.getY() + d0, pPosZ, pYRot, pWarmUp, livingEntity);
            float enchantment = spellStat.getPotency();
            int burning = spellStat.getBurning();
            int soulEater = 0;
            if (WandUtil.enchantedFocus(livingEntity)) {
                enchantment = WandUtil.getPotencyLevel(livingEntity);
                burning = WandUtil.getLevels(ModEnchantments.BURNING.get(), livingEntity);
                soulEater = WandUtil.getLevels(ModEnchantments.SOUL_EATER.get(), livingEntity);
            }
            spike.setExtraDamage(enchantment);
            spike.setBurning(burning);
            spike.setSoulEater(soulEater);
            livingEntity.level.addFreshEntity(spike);
        }

    }

    public static void spawnIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        spawnIceBouquet(world, pPos, livingEntity, 0.0F, 0);
    }

    public static void spawnIceBouquet(Level level, LivingEntity casterEntity, Vec3 targetPos, double xshift, double zshift, float damage, int duration) {
        spawnIceBouquet(level, casterEntity, targetPos, xshift, zshift, damage, duration, false);
    }

    public static void spawnIceBouquet(Level level, LivingEntity casterEntity, Vec3 targetPos, double xshift, double zshift, float damage, int duration, boolean isCenter) {
        targetPos = targetPos.add(xshift, 0.5F, zshift);
        IceBouquet iceBouquet = ModEntityType.ICE_BOUQUET.get().create(level);
        if (iceBouquet != null) {
            iceBouquet.setOwner(casterEntity);
            iceBouquet.setPos(targetPos.x(), targetPos.y(), targetPos.z());
            iceBouquet.setExtraDamage(damage);
            iceBouquet.addLifeSpan(duration);
            iceBouquet.setCenter(isCenter);
            MobUtil.moveDownToGround(iceBouquet);
            level.addFreshEntity(iceBouquet);
        }
    }

    public static void spawnIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity, float damage, int duration){
        spawnIceBouquet(world, livingEntity, pPos, 0, 0, damage, duration, true);
        spawnIceBouquet(world, livingEntity, pPos, 0, 1, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, 0, -1, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, 1, 0, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, 1, 1, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, 1, -1, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, -1, 0, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, -1, 1, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, -1, -1, damage, duration);
    }

    public static void spawnCrossIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        spawnCrossIceBouquet(world, pPos, livingEntity, 0.0F, 0);
    }

    public static void spawnCrossIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity, float damage, int duration){
        spawnIceBouquet(world, livingEntity, pPos, 0, 0, damage, duration, true);
        spawnIceBouquet(world, livingEntity, pPos, 1, 0, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, -1, 0, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, 0, 1, damage, duration);
        spawnIceBouquet(world, livingEntity, pPos, 0, -1, damage, duration);
    }

    public static void spawn4x4IceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        spawn4x4IceBouquet(world, pPos, livingEntity, 0.0F, 0);
    }

    public static void spawn4x4IceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity, float damage, int duration){
        spawnIceBouquet(world, livingEntity, pPos, 0, 0, damage, duration, true);
        int random = world.random.nextInt(4);
        if (random == 0){
            spawnIceBouquet(world, livingEntity, pPos, 0, 1, damage, duration);
            spawnIceBouquet(world, livingEntity, pPos, 1, 0, damage, duration);
            spawnIceBouquet(world, livingEntity, pPos, 1, 1, damage, duration);
        } else if (random == 1){
            spawnIceBouquet(world, livingEntity, pPos, 0, -1, damage, duration);
            spawnIceBouquet(world, livingEntity, pPos, 1, 0, damage, duration);
            spawnIceBouquet(world, livingEntity, pPos, 1, -1, damage, duration);
        } else if (random == 2){
            spawnIceBouquet(world, livingEntity, pPos, 0, 1, damage, duration);
            spawnIceBouquet(world, livingEntity, pPos, -1, 0, damage, duration);
            spawnIceBouquet(world, livingEntity, pPos, -1, 1, damage, duration);
        } else {
            spawnIceBouquet(world, livingEntity, pPos, 0, -1, damage, duration);
            spawnIceBouquet(world, livingEntity, pPos, -1, 0, damage, duration);
            spawnIceBouquet(world, livingEntity, pPos, -1, -1, damage, duration);
        }
    }

    public static void summonTridentStorm(LivingEntity casterEntity, Vec3 targetPos, double xshift, double zshift, int warmUp, int potency) {
        targetPos = targetPos.add(xshift, 0.5F, zshift);
        Level level = casterEntity.level;
        TridentStorm tridentStorm = ModEntityType.TRIDENT_STORM.get().create(level);
        if (tridentStorm != null) {
            tridentStorm.setOwner(casterEntity);
            tridentStorm.setPos(targetPos.x(), targetPos.y(), targetPos.z());
            tridentStorm.setWarmUp(warmUp);
            tridentStorm.setExtraDamage(potency);
            MobUtil.moveDownToGround(tridentStorm);
            level.addFreshEntity(tridentStorm);
        }
    }

    public static void summonTridentMinor(LivingEntity casterEntity, int warmUp, int potency){
        Vec3 vec3 = casterEntity.position();
        if (casterEntity.getRandom().nextBoolean()) {
            summonTridentStorm(casterEntity, vec3, 2, 0, warmUp, potency);
            summonTridentStorm(casterEntity, vec3, 0, 2, warmUp, potency);
            summonTridentStorm(casterEntity, vec3, -2, 0, warmUp, potency);
            summonTridentStorm(casterEntity, vec3, 0, -2, warmUp, potency);
        } else {
            summonTridentStorm(casterEntity, vec3, 2, 2, warmUp, potency);
            summonTridentStorm(casterEntity, vec3, -2, 2, warmUp, potency);
            summonTridentStorm(casterEntity, vec3, 2, -2, warmUp, potency);
            summonTridentStorm(casterEntity, vec3, -2, -2, warmUp, potency);
        }
    }

    public static void summonTridentSurround(LivingEntity casterEntity, int warmUp, int potency) {
        Vec3 vec3 = casterEntity.position();

        summonTridentStorm(casterEntity, vec3, 3, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 2, 2, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -3, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -2, 2, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 0, 3, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 2, -2, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 0, -3, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -2, -2, warmUp, potency);
    }

    public static void summonTridentSquare(LivingEntity casterEntity, int warmUp, int potency) {
        Vec3 vec3 = casterEntity.position();

        summonTridentStorm(casterEntity, vec3, 2.25, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 5, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 2.25, 3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 5, 3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 2.25, -3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 5, -3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -2.25, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -5, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -2.25, 3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -5, 3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -2.25, -3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -5, -3.5, warmUp, potency);
    }

    public static void summonTridentCross(LivingEntity casterEntity, int warmUp, int potency) {
        Vec3 vec3 = casterEntity.position();

        summonTridentStorm(casterEntity, vec3, 2, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 0, 2, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -2, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 0, -2, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 3.5, -3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -3.5, -3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 3.5, 3.5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -3.5, 3.5, warmUp, potency);
    }

    public static void summonTridentWideCircle(LivingEntity casterEntity, int warmUp, int potency) {
        Vec3 vec3 = casterEntity.position();

        summonTridentStorm(casterEntity, vec3, 2, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 0, 2, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -2, 0, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 0, -2, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 0, 5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 0, -5, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 4.5, 2.25, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, 4.5, -2.25, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -4.5, 2.25, warmUp, potency);
        summonTridentStorm(casterEntity, vec3, -4.5, -2.25, warmUp, potency);
    }

    /**
     * Based off GeomancyHelper class from Dungeons Mobs: <a href="https://github.com/Infamous-Misadventures/Dungeons-Mobs/blob/1.19/src/main/java/com/infamous/dungeons_mobs/utils/GeomancyHelper.java">...</a>
     */
    public static final int[] CONFIG_1_NORTH_ROW = new int[]{2, 3, 4, 5, 6};
    public static final int[] CONFIG_1_WEST_ROW = new int[]{6, 7, 8, 9, 10};
    public static final int[] CONFIG_1_SOUTH_ROW = new int[]{10, 11, 12, 13, 14};
    public static final int[] CONFIG_1_EAST_ROW = new int[]{14, 15, 0, 1, 2};
    public static final int[][] CONFIG_1_ROWS = new int[][]{CONFIG_1_NORTH_ROW, CONFIG_1_WEST_ROW, CONFIG_1_SOUTH_ROW, CONFIG_1_EAST_ROW};

    private static boolean isValueInArray(int[] array, int toCheckValue) {
        for (int element : array) {
            if (element == toCheckValue) {
                return true;
            }
        }
        return false;
    }

    private static double getXShift(int i, double xshift) {
        if (i == 0 || i == 1 || i == 2 || i == 14 || i == 15) {
            xshift = -2.0D;
        }
        if (i >= 6 && i <= 10) {
            xshift = 2.0D;
        }

        if (i == 3 || i == 13) {
            xshift = -1.0D;
        }
        if (i == 5 || i == 11) {
            xshift = 1.0D;
        }
        return xshift;
    }

    private static double getZShift(int i, double zshift) {
        if (i >= 10 && i <= 14) {
            zshift = -2.0D;
        }
        if (i >= 2 && i <= 6) {
            zshift = 2.0D;
        }
        if (i == 9 || i == 15) {
            zshift = -1.0D;
        }
        if (i == 1 || i == 7) {
            zshift = 1.0D;
        }
        return zshift;
    }

    private static BlockPos createCenteredBlockPosOnTarget(Entity targetEntity) {
        return BlockPos.containing(
                Math.floor(targetEntity.getX()),
                Math.floor(targetEntity.getY()),
                Math.floor(targetEntity.getZ()));
    }

    public static void summonMonolith(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, double xshift, double zshift, int extra) {
        summonMonolith(casterEntity, targetPos, wallEntityType, xshift, zshift, 0, extra);
    }

    public static void summonMonolith(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, double xshift, double zshift, int delay, int extra) {
        targetPos = targetPos.offset((int) xshift, 1, (int) zshift);
        Vec3 vec3 = Vec3.atBottomCenterOf(targetPos);
        Level level = casterEntity.level;
        AbstractMonolith monolith = wallEntityType.create(level);
        if (monolith != null) {
            Player player = null;
            if (casterEntity instanceof Player player1){
                player = player1;
            }
            EntityType<?> entityType = monolith.getVariant(player, level, targetPos);
            if (entityType != null){
                monolith = (AbstractMonolith) entityType.create(level);
            }
            if (monolith != null) {
                monolith.setTrueOwner(casterEntity);
                monolith.setPos(vec3.x(), vec3.y(), vec3.z());
                if (level instanceof ServerLevel serverLevel) {
                    monolith.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(targetPos), MobSpawnType.MOB_SUMMONED, null, null);
                }
                if (monolith instanceof AbstractVine vine) {
                    vine.setWarmup(delay);
                    monolith.setLifeSpan(3 + extra);
                } else if (monolith instanceof TotemicBomb totemicBomb) {
                    totemicBomb.setExplosionPower(totemicBomb.getExplosionPower() + (extra / 4.0F));
                } else {
                    monolith.setLifeSpan(6 + extra);
                }
                level.addFreshEntity(monolith);
            }
        }
    }

    public static void summonMonolith(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> wallEntityType, double xshift, double zshift, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonMonolith(casterEntity, targetPos, wallEntityType, xshift, zshift, extra);
    }

    public static void summonTurret(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, @Nullable Entity target, int duration, int potency) {
        summonTurret(casterEntity, targetPos, wallEntityType, target, 0, duration, potency);
    }

    public static void summonTurret(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, @Nullable Entity target, int delay, int duration, int potency) {
        targetPos = targetPos.above();
        Vec3 vec3 = Vec3.atBottomCenterOf(targetPos);
        Level level = casterEntity.level;
        AbstractMonolith monolith = wallEntityType.create(level);
        if (monolith != null) {
            Player player = null;
            if (casterEntity instanceof Player player1){
                player = player1;
            }
            EntityType<?> entityType = monolith.getVariant(player, level, targetPos);
            if (entityType != null){
                monolith = (AbstractMonolith) entityType.create(level);
            }
            if (monolith != null) {
                monolith.setTrueOwner(casterEntity);
                monolith.setPos(vec3.x(), vec3.y(), vec3.z());
                if (level instanceof ServerLevel serverLevel) {
                    monolith.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(targetPos), MobSpawnType.MOB_SUMMONED, null, null);
                }
                if (monolith instanceof AbstractVine vine) {
                    vine.setWarmup(delay);
                }
                if (potency > 0 && !casterEntity.hasEffect(GoetyEffects.SUMMON_DOWN.get())) {
                    monolith.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), potency - 1, false, false));
                }
                if (target instanceof LivingEntity living) {
                    monolith.setTarget(living);
                }
                monolith.setLifeSpan(7 * (duration + 1));
                level.addFreshEntity(monolith);
            }
        }
    }

    public static void summonLesserSquareTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int[] rowToRemove, int extra) {
        for (int constructPositionIndex = 0; constructPositionIndex <= 7; constructPositionIndex++) {
            if (isValueInArray(rowToRemove, constructPositionIndex)) {
                continue;
            }
            double xshift = 0;
            double zshift = 0;
            xshift = getXShift(constructPositionIndex, xshift);
            zshift = getZShift(constructPositionIndex, zshift);
            summonMonolith(casterEntity, targetPos, entityType, xshift, zshift, extra);
        }
    }

    public static void summonSquareTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int[] rowToRemove, int extra) {
        for (int constructPositionIndex = 0; constructPositionIndex <= 15; constructPositionIndex++) {
            if (isValueInArray(rowToRemove, constructPositionIndex)) {
                continue;
            }
            double xshift = 0;
            double zshift = 0;
            xshift = getXShift(constructPositionIndex, xshift);
            zshift = getZShift(constructPositionIndex, zshift);
            summonMonolith(casterEntity, targetPos, entityType, xshift, zshift, extra);
        }
    }

    public static void summonSquareTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int[] rowToRemove, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonSquareTrap(casterEntity, targetPos, entityType, rowToRemove, extra);
    }

    public static void summonMinorSquareTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, Direction direction, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);

        if (direction.getAxis() == Direction.Axis.X){
            summonMonolith(casterEntity, targetPos, entityType, 0, -2, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 0, 2, 0, extra);
            if (casterEntity.getRandom().nextBoolean()) {
                summonMonolith(casterEntity, targetPos, entityType, -1, -2, 2, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, -2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, -1, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, 0, 8, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, 1, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, 2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, -1, 2, 2, extra);
            } else {
                summonMonolith(casterEntity, targetPos, entityType, 1, -2, 2, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, -2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, -1, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 0, 8, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 1, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, 1, 2, 2, extra);
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            summonMonolith(casterEntity, targetPos, entityType, -2, 0, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, 0, 0, extra);
            if (casterEntity.getRandom().nextBoolean()) {
                summonMonolith(casterEntity, targetPos, entityType, -2, -1, 2, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, -2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, -1, -2, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 0, -2, 8, extra);
                summonMonolith(casterEntity, targetPos, entityType, 1, -2, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, -2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, -1, 2, extra);
            } else {
                summonMonolith(casterEntity, targetPos, entityType, -2, 1, 2, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, 2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, -1, 2, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 0, 2, 8, extra);
                summonMonolith(casterEntity, targetPos, entityType, 1, 2, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 1, 2, extra);
            }
        }

    }

    public static void summonCircleTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, Direction direction, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);

        if (direction.getAxis() == Direction.Axis.X){
            summonMonolith(casterEntity, targetPos, entityType, 1, 2, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, -1, 2, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, -1, 1, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, 0, 2, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, 1, 3, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, -1, 3, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, 0, 2, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, 1, 1, extra);
            summonMonolith(casterEntity, targetPos, entityType, 1, -2, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, -1, -2, 0, extra);
        } else if (direction.getAxis() == Direction.Axis.Z){
            summonMonolith(casterEntity, targetPos, entityType, 2, 1, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, -1, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 1, 2, 1, extra);
            summonMonolith(casterEntity, targetPos, entityType, 0, 2, 2, extra);
            summonMonolith(casterEntity, targetPos, entityType, -1, 2, 3, extra);
            summonMonolith(casterEntity, targetPos, entityType, 1, -2, 3, extra);
            summonMonolith(casterEntity, targetPos, entityType, 0, -2, 2, extra);
            summonMonolith(casterEntity, targetPos, entityType, -1, -2, 1, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, 1, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, -1, 0, extra);
        }
    }

    public static void summonCubeTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        for (int constructPositionIndex = 0; constructPositionIndex <= 25; constructPositionIndex++) {
            if (constructPositionIndex != 4
                    && constructPositionIndex != 8
                    && constructPositionIndex != 12
                    && constructPositionIndex != 16){
                double xshift = 0;
                double zshift = 0;
                xshift = getXShift(constructPositionIndex, xshift);
                zshift = getZShift(constructPositionIndex, zshift);
                if (xshift != 0 && zshift != 0) {
                    summonMonolith(casterEntity, targetPos, entityType, xshift, zshift, constructPositionIndex, extra);
                }
            }
        }
    }

    public static void summonCubeTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonCubeTrap(casterEntity, targetPos, entityType, extra);
    }

    public static void summonHallTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        Direction direction = Direction.fromYRot(casterEntity.getYHeadRot());
        if (direction.getAxis() == Direction.Axis.X){
            for (int length = -2; length <= 2; length++) {
                summonMonolith(casterEntity, targetPos, entityType, 2, length, length + 2, extra);
            }
            for (int length = -2; length <= 2; length++) {
                summonMonolith(casterEntity, targetPos, entityType, -2, length, length + 2, extra);
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            for (int length = -2; length <= 2; length++) {
                summonMonolith(casterEntity, targetPos, entityType, length, 2, length + 2, extra);
            }
            for (int length = -2; length <= 2; length++) {
                summonMonolith(casterEntity, targetPos, entityType, length, -2, length + 2, extra);
            }
        }
    }

    public static void summonHallTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonHallTrap(casterEntity, targetPos, entityType, extra);
    }

    public static void summonSurroundTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonMonolith(casterEntity, targetPos, entityType, 1, 0, 0, extra);
        summonMonolith(casterEntity, targetPos, entityType, -1, 0, 1, extra);
        summonMonolith(casterEntity, targetPos, entityType, 1, 1, 2, extra);
        summonMonolith(casterEntity, targetPos, entityType, -1, 1, 3, extra);
        summonMonolith(casterEntity, targetPos, entityType, 1, -1, 4, extra);
        summonMonolith(casterEntity, targetPos, entityType, -1, -1, 5, extra);
        summonMonolith(casterEntity, targetPos, entityType, 0, 1, 6, extra);
        summonMonolith(casterEntity, targetPos, entityType, 0, -1, 7, extra);
    }

    public static void summonSurroundTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonSurroundTrap(casterEntity, targetPos, entityType, extra);
    }

    public static void summonWallTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonWallTrap(casterEntity, targetPos, entityType, 5, extra);
    }

    public static void summonWallTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int amount, int extra) {
        Direction direction = Direction.fromYRot(casterEntity.getYHeadRot());
        if (direction.getAxis() == Direction.Axis.X){
            for (int length = -amount; length < amount; length++) {
                summonMonolith(casterEntity, targetPos, entityType, 0, length, extra);
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            for (int length = -amount; length < amount; length++) {
                summonMonolith(casterEntity, targetPos, entityType, length, 0, extra);
            }
        }
    }

    public static void summonWallTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonWallTrap(casterEntity, targetEntity, entityType, 5, extra);
    }

    public static void summonWallTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int amount, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        Direction direction = Direction.fromYRot(targetEntity.getYHeadRot());
        if (targetEntity instanceof Mob){
            direction = targetEntity.getMotionDirection();
        }
        if (direction.getAxis() == Direction.Axis.X){
            if (casterEntity.getRandom().nextBoolean()) {
                for (int length = -amount; length < amount; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, -2, length, extra);
                }
            } else {
                for (int length = -amount; length < amount; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, 2, length, extra);
                }
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            if (casterEntity.getRandom().nextBoolean()) {
                for (int length = -amount; length < amount; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, length, -2, extra);
                }
            } else {
                for (int length = -amount; length < amount; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, length, 2, extra);
                }
            }
        }
    }

    public static void summonRandomPillarsTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        for (int length = 0; length < 25; length++) {
            summonMonolith(casterEntity, targetPos, entityType, -8 + casterEntity.getRandom().nextInt(16), -8 + casterEntity.getRandom().nextInt(16), extra);
        }
    }

    public static void summonRandomPillarsTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonRandomPillarsTrap(casterEntity, targetEntity, entityType, 12, extra);
    }

    public static void summonRandomPillarsTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int amount, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        for (int length = 0; length < amount; length++) {
            summonMonolith(casterEntity, targetPos, entityType, -4 + casterEntity.getRandom().nextInt(8), -4 + casterEntity.getRandom().nextInt(8), extra);
        }
    }

    public static void summonQuadOffensiveTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonMonolith(casterEntity, targetPos, entityType, -2, 0, extra);
        summonMonolith(casterEntity, targetPos, entityType, 2, 0, extra);
        summonMonolith(casterEntity, targetPos, entityType, 0, -2, extra);
        summonMonolith(casterEntity, targetPos, entityType, 0, 2, extra);
    }

    public static void summonQuadOffensiveTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonQuadOffensiveTrap(casterEntity, targetPos, entityType, extra);
    }
}
