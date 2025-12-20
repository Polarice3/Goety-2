package com.Polarice3.Goety.common.items.revive;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.*;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.DrownedServant;
import com.Polarice3.Goety.common.entities.neutral.*;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class SoulJar extends ReviveServantItem {
    public static final String TAG_CAIRN = "Cairn";
    public static final String TAG_MOSSY = "Mossy";
    public static final String TAG_DROWNED = "Drowned";
    public static final String TAG_WITHER = "Wither";

    public SoulJar(){
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1)
        );
    }

    @Override
    public boolean isFireResistant() {
        return isWither(this.getDefaultInstance());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            LivingEntity livingEntity = getNecromancer(stack, worldIn);
            if (livingEntity != null) {
                if (livingEntity instanceof AbstractCairnNecromancer) {
                    if (!isCairn(stack)) {
                        setCairn(stack);
                    }
                } else if (livingEntity instanceof AbstractMossyNecromancer) {
                    if (!isMossy(stack)) {
                        setMossy(stack);
                    }
                } else if (livingEntity instanceof DrownedNecromancer) {
                    if (!isDrowned(stack)) {
                        setDrowned(stack);
                    }
                } else if (livingEntity instanceof AbstractWitherNecromancer) {
                    if (!isWither(stack)) {
                        setWither(stack);
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.getCommandSenderWorld();

        Entity entity;
        if (getSummon(stack, level) != null){
            entity = getSummon(stack, level);
        } else {
            entity = new NecromancerServant(ModEntityType.NECROMANCER_SERVANT.get(), level);
            if (isCairn(stack)){
                entity = new CairnNecromancerServant(ModEntityType.CAIRN_NECROMANCER_SERVANT.get(), level);
            } else if (isMossy(stack)){
                entity = new MossyNecromancerServant(ModEntityType.MOSSY_NECROMANCER_SERVANT.get(), level);
            } else if (isDrowned(stack)){
                entity = new DrownedNecromancer(ModEntityType.DROWNED_NECROMANCER_SERVANT.get(), level);
            } else if (isWither(stack)){
                entity = new WitherNecromancerServant(ModEntityType.WITHER_NECROMANCER_SERVANT.get(), level);
            }
            IOwned owned = (IOwned) entity;
            owned.setTrueOwner(player);
        }
        if (entity instanceof AbstractNecromancer necromancer) {
            boolean flag;
            if (necromancer instanceof DrownedNecromancer || isDrowned(stack)){
                flag = target instanceof DrownedServant || target instanceof Drowned || target.getType().is(ModTags.EntityTypes.DROWNED_CONVERT);
            } else if (necromancer instanceof AbstractWitherNecromancer || isWither(stack)){
                flag = target instanceof WitherSkeletonServant || target instanceof WitherSkeleton || target.getType().is(ModTags.EntityTypes.WITHER_CONVERT);
            } else if (necromancer instanceof AbstractMossyNecromancer || isMossy(stack)){
                flag = target instanceof MossySkeletonServant || target.getType().is(ModTags.EntityTypes.MOSSY_CONVERT);
            } else if (necromancer instanceof AbstractCairnNecromancer || isCairn(stack)){
                flag = target instanceof StrayServant || target instanceof Stray || target.getType().is(ModTags.EntityTypes.CAIRN_CONVERT);
            } else {
                flag = target instanceof SkeletonServant || target instanceof Skeleton || target.getType().is(ModTags.EntityTypes.REGULAR_CONVERT);
            }
            if (flag) {
                if (necromancer.getTrueOwner() == player) {
                    if (RitualRequirements.canSummon(level, player, ModEntityType.NECROMANCER_SERVANT.get())) {
                        necromancer.setHealth(necromancer.getMaxHealth());
                        necromancer.setPos(target.getX(), target.getY(), target.getZ());
                        necromancer.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
                        if (level.addFreshEntity(necromancer)) {
                            necromancer.spawnAnim();
                            if (level instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 8; ++i) {
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SCULK_SOUL, necromancer);
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.POOF, necromancer);
                                }
                            }
                            necromancer.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
                            if (necromancer instanceof DrownedNecromancer){
                                necromancer.playSound(ModSounds.DROWNED_NECROMANCER_AMBIENT.get(), 2.0F, 0.5F);
                            } else {
                                necromancer.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, 0.5F);
                            }
                            target.discard();
                            player.swing(hand);
                            SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(30));
                            stack.shrink(1);
                        }
                    }
                }
            }
        }

        return super.interactLivingEntity(stack, player, target, hand);
    }

    public static boolean isCairn(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return stack.getItem() instanceof SoulJar && compoundtag != null && compoundtag.contains(TAG_CAIRN);
    }

    public static void setCairn(ItemStack stack){
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putBoolean(TAG_CAIRN, true);
    }

    public static boolean isMossy(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return stack.getItem() instanceof SoulJar && compoundtag != null && compoundtag.contains(TAG_MOSSY);
    }

    public static void setMossy(ItemStack stack){
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putBoolean(TAG_MOSSY, true);
    }

    public static boolean isDrowned(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return stack.getItem() instanceof SoulJar && compoundtag != null && compoundtag.contains(TAG_DROWNED);
    }

    public static void setDrowned(ItemStack stack){
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putBoolean(TAG_DROWNED, true);
    }

    public static boolean isWither(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return stack.getItem() instanceof SoulJar && compoundtag != null && compoundtag.contains(TAG_WITHER);
    }

    public static void setWither(ItemStack stack){
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putBoolean(TAG_WITHER, true);
    }

    public static void setNecromancer(AbstractNecromancer necromancer, ItemStack stack) {
        necromancer.stopRiding();
        necromancer.ejectPassengers();

        CompoundTag entityTag = new CompoundTag();
        ResourceLocation typesKey = ForgeRegistries.ENTITY_TYPES.getKey(necromancer.getType());

        if (typesKey != null) {
            entityTag.putString("entity", typesKey.toString());
            if (necromancer.hasCustomName()) {
                entityTag.putString("name", Objects.requireNonNull(necromancer.getCustomName()).getString());
            }
            necromancer.save(entityTag);
            CompoundTag itemNBT = stack.getOrCreateTag();
            itemNBT.put("entity", entityTag);
        }
    }

    public static AbstractNecromancer getNecromancer(ItemStack stack, Level level) {
        CompoundTag itemTag = stack.getTag();

        if (itemTag != null) {
            CompoundTag entityTag = itemTag.getCompound("entity");
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityTag.getString("entity")));
            if (entityType != null) {
                Entity entity = entityType.create(level);
                if (level instanceof ServerLevel && entity != null) {
                    entity.load(entityTag);
                }

                if (entity instanceof AbstractNecromancer necromancer){
                    return necromancer;
                }
            }
        }

        return null;
    }
}
