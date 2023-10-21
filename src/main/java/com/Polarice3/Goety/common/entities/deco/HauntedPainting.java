package com.Polarice3.Goety.common.entities.deco;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HauntedPainting extends Painting {
    public HauntedPainting(EntityType<? extends Painting> p_31904_, Level p_31905_) {
        super(p_31904_, p_31905_);
    }

    public HauntedPainting(EntityType<? extends Painting> p_31706_, Level p_31707_, BlockPos p_31708_) {
        this(p_31706_, p_31707_);
        this.pos = p_31708_;
    }

    public HauntedPainting(Level p_218874_, BlockPos p_218875_) {
        this(ModEntityType.MOD_PAINTING.get(), p_218874_, p_218875_);
    }

    public HauntedPainting(Level level, BlockPos blockPos, Direction direction, Holder<PaintingVariant> holder) {
        this(ModEntityType.MOD_PAINTING.get(), level);
        this.setVariant(holder);
        this.setDirection(direction);
        this.pos = blockPos;
    }

    private void setVariant(Holder<PaintingVariant> p_218892_) {
        this.entityData.set(DATA_PAINTING_VARIANT_ID, p_218892_);
    }

    public static Optional<HauntedPainting> createModded(Level p_218888_, BlockPos p_218889_, Direction p_218890_) {
        HauntedPainting painting = new HauntedPainting(p_218888_, p_218889_);
        List<Holder<PaintingVariant>> list = new ArrayList<>();
        Registry.PAINTING_VARIANT.getTagOrEmpty(ModTags.Paintings.MODDED_PAINTINGS).forEach(list::add);
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            painting.setDirection(p_218890_);
            list.removeIf((p_218886_) -> {
                painting.setVariant(p_218886_);
                return !painting.survives();
            });
            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                int i = list.stream().mapToInt(HauntedPainting::variantArea).max().orElse(0);
                list.removeIf((p_218883_) -> {
                    return variantArea(p_218883_) < i;
                });
                Optional<Holder<PaintingVariant>> optional = Util.getRandomSafe(list, painting.random);
                if (optional.isEmpty()) {
                    return Optional.empty();
                } else {
                    painting.setVariant(optional.get());
                    painting.setDirection(p_218890_);
                    return Optional.of(painting);
                }
            }
        }
    }

    private static int variantArea(Holder<PaintingVariant> p_218899_) {
        return p_218899_.value().getWidth() * p_218899_.value().getHeight();
    }

    public void dropItem(@Nullable Entity p_31925_) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (p_31925_ instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    return;
                }
            }

            this.spawnAtLocation(ModItems.HAUNTED_PAINTING.get());
        }
    }

    public ItemStack getPickResult() {
        return new ItemStack(ModItems.HAUNTED_PAINTING.get());
    }
}
