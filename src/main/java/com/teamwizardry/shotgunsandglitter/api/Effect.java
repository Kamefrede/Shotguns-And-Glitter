package com.teamwizardry.shotgunsandglitter.api;

import com.teamwizardry.shotgunsandglitter.common.entity.EntityBullet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public interface Effect {

	String getID();

	default void onImpact(@NotNull World world, @NotNull EntityBullet bullet, @NotNull RayTraceResult hit) {
		// NO-OP
	}

	default boolean onCollideEntity(@NotNull World world, @NotNull EntityBullet bullet, @NotNull Entity hitEntity, @NotNull RayTraceResult hit) {
		hitEntity.attackEntityFrom(DamageSource.causeThrownDamage(bullet, hitEntity), bullet.getBulletType().damage);
		return false;
	}

	default boolean onCollideBlock(@NotNull World world, @NotNull EntityBullet bullet, @NotNull RayTraceResult pos, @NotNull IBlockState state) {
		return false;
	}

	default void onUpdate(@NotNull World world, @NotNull EntityBullet bullet) {
		// NO-OP
	}

	default float getVelocity(@NotNull World world, @NotNull Entity caster, @NotNull BulletType bulletType) {
		return 3.0F;
	}


	// Render Methods

	@SideOnly(Side.CLIENT)
	default void renderImpact(@NotNull World world, @NotNull EntityBullet bullet, @NotNull RayTraceResult hit) {
		// NO-OP
	}

	@SideOnly(Side.CLIENT)
	default void renderUpdate(@NotNull World world, @NotNull EntityBullet bullet) {
		// NO-OP
	}

	@SideOnly(Side.CLIENT)
	default void renderCollideBlock(@NotNull World world, @NotNull EntityBullet bullet, @NotNull RayTraceResult pos, @NotNull IBlockState state) {
		// NO-OP
	}

	@SideOnly(Side.CLIENT)
	default void renderCollideEntity(@NotNull World world, @NotNull EntityBullet bullet, @NotNull Entity hitEntity, @NotNull RayTraceResult hit) {
		// NO-OP
	}

}
