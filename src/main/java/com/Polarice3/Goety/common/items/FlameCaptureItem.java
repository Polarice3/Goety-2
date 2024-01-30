package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.ItemConfig;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Code based off @stal111
 */
public class FlameCaptureItem extends Item {

    public FlameCaptureItem() {
        super(new Item.Properties().tab(Goety.TAB).stacksTo(1));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (this.getEntity(stack, level) != null) {
            if (level.getBlockState(pos).getBlock() == ModBlocks.CURSED_CAGE_BLOCK.get()){
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof CursedCageBlockEntity cursedCageBlock){
                    if (!cursedCageBlock.getItem().isEmpty()){
                        return InteractionResult.PASS;
                    } else {
                        if (!level.isClientSide) {
                            level.setBlockAndUpdate(pos, Blocks.SPAWNER.defaultBlockState());
                            BlockEntity blockentity = level.getBlockEntity(pos);
                            if (blockentity instanceof SpawnerBlockEntity) {
                                ((SpawnerBlockEntity) blockentity).getSpawner().setEntityId(this.getEntity(stack, level).getType());
                            }
                        }
                        this.clearEntity(stack);
                        if (player != null) {
                            player.playSound(ModSounds.FLAME_CAPTURE_RELEASE.get());
                        }
                        stack.shrink(1);

                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            }
        } else {
            if (ItemConfig.FireSpawnCage.get()) {
                if (level.getBlockState(pos).getBlock() == Blocks.SPAWNER) {
                    if (!level.isClientSide()) {
                        BlockEntity blockentity = level.getBlockEntity(pos);
                        if (blockentity instanceof SpawnerBlockEntity) {
                            Entity entity = ((SpawnerBlockEntity) blockentity).getSpawner().getOrCreateDisplayEntity(level);
                            if (entity != null) {
                                this.setEntity(entity, stack);
                                level.destroyBlock(pos, false);
                            }
                        }
                    }
                    if (player != null) {
                        player.playSound(ModSounds.FLAME_CAPTURE_CATCH.get());
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        if (ItemConfig.FireSpawnCage.get()) {
            if (level != null && this.getEntity(stack, level) != null) {
                Entity entity = this.getEntity(stack, level);

                if (entity == null) {
                    return;
                }

                MutableComponent textComponent = Component.translatable("tooltip.goety.entity")
                        .append(": ")
                        .append(Component.literal(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType())).toString()))
                        .withStyle(ChatFormatting.GREEN);

                tooltip.add(textComponent);
            }
        } else {
            MutableComponent textComponent = Component.translatable("tooltip.goety.disabled")
                    .withStyle(ChatFormatting.DARK_RED);

            tooltip.add(textComponent);
        }
    }

    public static boolean hasEntity(ItemStack itemStack){
        return itemStack.getTag() != null;
    }

    private void setEntity(Entity entity, ItemStack stack) {
        CompoundTag entityTag = stack.getOrCreateTag();
        ResourceLocation name = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());

        if (name == null) {
            return;
        }

        entityTag.putString("mob", name.toString());
    }

    private Entity getEntity(ItemStack stack, Level level) {
        CompoundTag itemTag = stack.getTag();

        if (itemTag == null) {
            return null;
        }

        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(itemTag.getString("mob")));

        if (entityType == null) {
            return null;
        }

        return entityType.create(level);
    }

    private void clearEntity(ItemStack stack) {
        stack.setTag(null);
    }

}
