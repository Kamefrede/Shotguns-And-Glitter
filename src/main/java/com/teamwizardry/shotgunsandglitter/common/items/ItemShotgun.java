package com.teamwizardry.shotgunsandglitter.common.items;

import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.shotgunsandglitter.api.BulletType;
import com.teamwizardry.shotgunsandglitter.api.Effect;
import com.teamwizardry.shotgunsandglitter.api.EffectRegistry;
import com.teamwizardry.shotgunsandglitter.api.IGunItem;
import com.teamwizardry.shotgunsandglitter.api.util.RandUtil;
import com.teamwizardry.shotgunsandglitter.client.core.ClientEventHandler;
import com.teamwizardry.shotgunsandglitter.common.core.ModSounds;
import com.teamwizardry.shotgunsandglitter.common.entity.EntityBullet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ItemShotgun extends ItemMod implements IGunItem {

	public ItemShotgun() {
		super("shotgun");
		setMaxStackSize(1);
	}

	@Override
	@NotNull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @NotNull EnumHand handIn) {
		ItemStack offHand = playerIn.getHeldItemOffhand();
		ItemStack mainHand = playerIn.getHeldItemMainhand();

		if (reloadAmmo(worldIn, playerIn, mainHand, offHand)) {
			fireGun(worldIn, playerIn, playerIn.getHeldItem(handIn), handIn);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}


	@Override
	public void fireGun(World world, EntityPlayer player, ItemStack stack, EnumHand hand) {
		NBTTagList list = ItemNBTHelper.getList(stack, "ammo", Constants.NBT.TAG_STRING);
		if (list == null) list = new NBTTagList();
		if (list.tagCount() == 0) return;

		String effectID = list.getStringTagAt(list.tagCount() - 1);
		Effect effect = EffectRegistry.getEffectByID(effectID);

		list.removeTag(list.tagCount() - 1);
		ItemNBTHelper.setList(stack, "ammo", list);

		if (!world.isRemote) {
			for (int i = 0; i < 5; i++) {
				EntityBullet bullet = new EntityBullet(world, player, getBulletType(stack), effect, getInaccuracy(stack));
				bullet.setPosition(player.posX, player.posY + player.eyeHeight, player.posZ);
				world.spawnEntity(bullet);
			}
		} else if (effect.getFireSound() != null) {
			world.playSound(player.posX, player.posY, player.posZ, effect.getFireSound(), SoundCategory.PLAYERS, RandUtil.nextFloat(0.95f, 1.1f), RandUtil.nextFloat(0.95f, 1.1f), false);
		}

		setFireCooldown(world, player, stack);
		player.swingArm(hand);

		Vec3d normal = player.getLook(0);
		player.motionX = -normal.x * getBulletType(stack).knockbackStrength;
		player.motionY = -normal.y * getBulletType(stack).knockbackStrength;
		player.motionZ = -normal.z * getBulletType(stack).knockbackStrength;

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				BasicAnimation<EntityPlayer> anim = new BasicAnimation<>(player, "rotationPitch");
				anim.setDuration(headKnockStrength(stack) / 8);
				anim.setTo(player.rotationPitch - headKnockStrength(stack));
				anim.setEasing(Easing.easeOutCubic);
				ClientEventHandler.FLASH_ANIMATION_HANDLER.add(anim);
			}
		});
	}

	@Override
	public int headKnockStrength(ItemStack stack) {
		return 30;
	}

	@NotNull
	@Override
	public BulletType getBulletType(@NotNull ItemStack stack) {
		return BulletType.MEDIUM;
	}

	@Override
	public int getMaxAmmo(ItemStack stack) {
		return 1;
	}

	@Override
	public int getReloadCooldownTime(ItemStack stack) {
		return 10;
	}

	@Override
	public int getFireCooldownTime(ItemStack stack) {
		return 40;
	}

	@Override
	public float getInaccuracy(ItemStack stack) {
		return 25f;
	}

	@Nullable
	@Override
	public SoundEvent[] getFireSoundEvents(ItemStack stack) {
		return new SoundEvent[]{ModSounds.SHOT_SHOTGUN_COCK, ModSounds.MAGIC_SPARKLE};
	}

	@Nullable
	@Override
	public SoundEvent getReloadSoundEvent(ItemStack stack) {
		return ModSounds.RELOAD_SHOTGUN;
	}
}
