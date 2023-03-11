package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
                        stack.shrink(1);

                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            }
        } else {
            if (level.getBlockState(pos).getBlock() == Blocks.SPAWNER){
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
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        if (level != null && getEntity(stack, level) != null)  {
            Entity entity = this.getEntity(stack, level);

            if (entity == null) {
                return;
            }

            MutableComponent textComponent = Component.translatable("tooltip.goety.entity")
                    .append(": ")
                    .append(Component.literal(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType())).toString()));

            if (this.getEntityName(stack) != null)  {
                textComponent.append(" (").append(Objects.requireNonNull(this.getEntityName(stack))).append(")");
            }

            textComponent.withStyle(ChatFormatting.GRAY);

            tooltip.add(textComponent);
        }
    }

    public static boolean hasEntity(ItemStack itemStack){
        return itemStack.getTag() != null;
    }

    private void setEntity(Entity entity, ItemStack stack) {
        entity.stopRiding();
        entity.ejectPassengers();

        CompoundTag entityTag = new CompoundTag();
        ResourceLocation name = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());

        if (name == null) {
            return;
        }

        entityTag.putString("entity", name.toString());
        if (entity.hasCustomName()) {
            entityTag.putString("name", Objects.requireNonNull(entity.getCustomName()).getString());
        }
        entity.save(entityTag);

        CompoundTag itemNBT = stack.getOrCreateTag();
        itemNBT.put("entity", entityTag);
    }

    private Entity getEntity(ItemStack stack, Level level) {
        CompoundTag itemTag = stack.getTag();

        if (itemTag == null) {
            return null;
        }

        CompoundTag entityTag = itemTag.getCompound("entity");
        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityTag.getString("entity")));

        if (entityType == null) {
            return null;
        }

        Entity entity = entityType.create(level);

        if (level instanceof ServerLevel && entity != null) {
            entity.load(entityTag);
        }

        return entity;
    }

    private Component getEntityName(ItemStack stack) {
        CompoundTag itemTag = stack.getTag();

        if (itemTag == null) {
            return null;
        }

        if (itemTag.contains("entity")) {
            CompoundTag entityTag = itemTag.getCompound("entity");

            if (entityTag.contains("name")) {
                return Component.literal(entityTag.getString("name"));
            }
        }
        return null;
    }

    private void clearEntity(ItemStack stack) {
        stack.setTag(null);
    }

}
