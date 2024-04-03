package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.utils.ConstantPaths;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ArrowRainTrap extends AbstractTrap {

    public ArrowRainTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ParticleTypes.CRIT);
    }

    public ArrowRainTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.ARROW_RAIN_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    public void tick() {
        super.tick();
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(this.getX(), this.getY(), this.getZ());

        while(blockpos$mutable.getY() < this.getY() + 32.0D && !this.level.getBlockState(blockpos$mutable).blocksMotion()) {
            blockpos$mutable.move(Direction.UP);
        }
        if (this.getOwner() != null) {
            if (this.getOwner() instanceof Apostle apostle){
                ItemStack itemstack = apostle.getProjectile(apostle.getItemInHand(ProjectileUtil.getWeaponHoldingHand(apostle, item -> item instanceof BowItem)));
                for(int i = 0; i < 3; ++i) {
                    AbstractArrow abstractarrowentity = apostle.getArrow(itemstack, Math.max(AttributesConfig.ApostleBowDamage.get() / 2.0F, 1.0F));
                    abstractarrowentity.addTag(ConstantPaths.rainArrow());
                    abstractarrowentity.setPos(this.getX() + this.random.nextIntBetweenInclusive(-3, 3), blockpos$mutable.getY(), this.getZ() + this.random.nextIntBetweenInclusive(-3, 3));
                    abstractarrowentity.shoot(0, -900, 0, 2, 10);
                    abstractarrowentity.setOwner(apostle);
                    this.level.addFreshEntity(abstractarrowentity);
                }
            } else {
                ItemStack itemStack = new ItemStack(Items.ARROW);
                ArrowItem arrowitem = (ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                for(int i = 0; i < 3; ++i) {
                    AbstractArrow abstractArrowEntity = arrowitem.createArrow(this.level, itemStack, this.getOwner());
                    abstractArrowEntity.addTag(ConstantPaths.rainArrow());
                    abstractArrowEntity.setPos(this.getX() + this.random.nextIntBetweenInclusive(-3, 3), blockpos$mutable.getY(), this.getZ() + this.random.nextIntBetweenInclusive(-3, 3));
                    abstractArrowEntity.shoot(0, -900, 0, 2, 0);
                    abstractArrowEntity.setOwner(this.getOwner());
                    this.level.addFreshEntity(abstractArrowEntity);
                }
            }
        } else {
            this.discard();
        }
        if (this.tickCount >= this.getDuration()) {
            this.discard();
        }
    }

}
