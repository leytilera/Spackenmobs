package mod.acgaming.spackenmobs.entities;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import mod.acgaming.spackenmobs.misc.ModConfigs;
import mod.acgaming.spackenmobs.misc.ModItems;
import mod.acgaming.spackenmobs.misc.ModLootTableList;
import mod.acgaming.spackenmobs.misc.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityJens extends EntityPig
{
	private static final DataParameter<Boolean> SADDLED = EntityDataManager.<Boolean>createKey(EntityJens.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> DIGESTING = EntityDataManager.<Boolean>createKey(EntityJens.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.<Integer>createKey(EntityJens.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DIGEST_TIME = EntityDataManager.<Integer>createKey(EntityJens.class, DataSerializers.VARINT);

	private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(ModItems.RAM);
	private static final Set<Item> FISH_ITEMS = Sets.newHashSet(Items.FISH);

	public static void registerFixesJens(DataFixer fixer)
	{
		EntityLiving.registerFixesMob(fixer, EntityJens.class);
	}

	private boolean boosting;
	public boolean digesting;

	private int boostTime;
	private int totalBoostTime;
	public int digestTime;

	@SideOnly(Side.CLIENT)
	Minecraft MINECRAFT = Minecraft.getMinecraft();

	public EntityJens(World worldIn)
	{
		super(worldIn);
		setSize(0.6F, 2.2F);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	public boolean boost()
	{
		if (this.boosting)
		{
			return false;
		}
		else
		{
			this.boosting = true;
			this.boostTime = 0;
			this.totalBoostTime = this.getRNG().nextInt(841) + 140;
			this.getDataManager().set(BOOST_TIME, Integer.valueOf(this.totalBoostTime));
			return true;
		}
	}

	@Override
	public boolean canBeSteered()
	{
		Entity entity = this.getControllingPassenger();

		if (!(entity instanceof EntityPlayer))
		{
			return false;
		}
		else
		{
			EntityPlayer entityplayer = (EntityPlayer) entity;
			return entityplayer.getHeldItemMainhand().getItem() == ModItems.RAM_ON_A_STICK || entityplayer.getHeldItemOffhand().getItem() == ModItems.RAM_ON_A_STICK;
		}
	}

	@Override
	public EntityJens createChild(EntityAgeable ageable)
	{
		return new EntityJens(this.world);
	}

	public void digestFish()
	{
		this.playSound(ModSoundEvents.ENTITY_JENS_EAT, 1.0F, 1.0F);

		this.digesting = true;
		this.dataManager.set(DIGESTING, Boolean.valueOf(true));

		this.digestTime = (ModConfigs.Jens_digest_time * 20);
		this.dataManager.set(DIGEST_TIME, Integer.valueOf(this.digestTime));

		for (int i = 0; i < 7; ++i)
		{
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			MINECRAFT.world.spawnParticle(EnumParticleTypes.HEART, this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + 0.5D + this.rand.nextFloat() * this.height,
					this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, d0, d1, d2);
		}

		this.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, ModConfigs.Jens_digest_time * 20));
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(SADDLED, Boolean.valueOf(false));
		this.dataManager.register(DIGESTING, Boolean.valueOf(false));
		this.dataManager.register(BOOST_TIME, Integer.valueOf(0));
		this.dataManager.register(DIGEST_TIME, Integer.valueOf(0));
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		return ModSoundEvents.ENTITY_JENS_AMBIENT;
	}

	@Override
	@Nullable
	public Entity getControllingPassenger()
	{
		return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.ENTITY_JENS_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
	{
		return ModSoundEvents.ENTITY_JENS_HURT;
	}

	@Override
	protected ResourceLocation getLootTable()
	{
		return ModLootTableList.ENTITIES_JENS;
	}

	@Override
	public boolean getSaddled()
	{
		return this.dataManager.get(SADDLED).booleanValue();
	}

	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
		this.tasks.addTask(2, new EntityAIEatDroppedFish(this));
		this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(4, new EntityAITempt(this, 1.2D, false, TEMPTATION_ITEMS));
		this.tasks.addTask(4, new EntityAITempt(this, 1.2D, ModItems.RAM_ON_A_STICK, false));
		this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
		this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	@Override
	public boolean isBreedingItem(ItemStack stack)
	{
		return TEMPTATION_ITEMS.contains(stack.getItem());
	}

	public boolean isFishItem(ItemStack stack)
	{
		return FISH_ITEMS.contains(stack.getItem());
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key)
	{
		if (BOOST_TIME.equals(key) && this.world.isRemote)
		{
			this.boosting = true;
			this.boostTime = 0;
			this.totalBoostTime = this.dataManager.get(BOOST_TIME).intValue();
		}

		super.notifyDataManagerChange(key);
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);

		if (!this.world.isRemote)
		{
			if (this.getSaddled())
			{
				this.dropItem(Items.SADDLE, 1);
			}
		}
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if (!this.world.isRemote && this.digesting == true && this.digestTime > 0)
		{
			this.digestTime--;
			this.dataManager.set(DIGEST_TIME, Integer.valueOf(this.digestTime));
		}

		if (!this.world.isRemote && this.digesting == true && this.digestTime <= 0)
		{
			for (int i = 0; i < 7; ++i)
			{
				double d0 = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				double d2 = this.rand.nextGaussian() * 0.02D;
				MINECRAFT.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + 0.5D + this.rand.nextFloat() * this.height,
						this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, d0, d1, d2);
			}
			this.playSound(ModSoundEvents.ENTITY_JENS_POOP, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(ModItems.SURSTROEMMING, 1);
			this.clearActivePotions();

			this.digesting = false;
			this.dataManager.set(DIGESTING, Boolean.valueOf(false));

			this.digestTime = 0;
			this.dataManager.set(DIGEST_TIME, Integer.valueOf(0));
		}
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		if (!super.processInteract(player, hand))
		{
			ItemStack itemstack = player.getHeldItem(hand);
			if (itemstack.getItem() == Items.FISH && !this.isChild() && this.digesting == false)
			{
				itemstack.shrink(1);
				digestFish();
				return true;
			}
			else if (itemstack.getItem() == Items.NAME_TAG)
			{
				itemstack.interactWithEntity(player, this, hand);
				return true;
			}
			else if (this.getSaddled() && !this.isBeingRidden())
			{
				if (!this.world.isRemote)
				{
					player.startRiding(this);
				}
				return true;
			}
			else if (itemstack.getItem() == Items.SADDLE)
			{
				itemstack.interactWithEntity(player, this, hand);
				this.setCustomNameTag("Reitbarer Jens");
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		this.setSaddled(compound.getBoolean("Saddle"));
		this.digesting = compound.getBoolean("Digesting");
		this.digestTime = compound.getInteger("DigestTime");
	}

	@Override
	public void setSaddled(boolean saddled)
	{
		if (saddled)
		{
			this.dataManager.set(SADDLED, Boolean.valueOf(true));
		}
		else
		{
			this.dataManager.set(SADDLED, Boolean.valueOf(false));
		}
	}

	@Override
	public void travel(float strafe, float vertical, float forward)
	{
		Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);

		if (this.isBeingRidden() && this.canBeSteered())
		{
			this.rotationYaw = entity.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = entity.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.renderYawOffset = this.rotationYaw;
			this.rotationYawHead = this.rotationYaw;
			this.stepHeight = 1.0F;
			this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

			if (this.boosting && this.boostTime++ > this.totalBoostTime)
			{
				this.boosting = false;
			}

			if (this.canPassengerSteer())
			{
				float f = (float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 0.225F;

				if (this.boosting)
				{
					f += f * 1.15F * MathHelper.sin((float) this.boostTime / (float) this.totalBoostTime * (float) Math.PI);
				}

				this.setAIMoveSpeed(f);
				super.travel(0.0F, 0.0F, 1.0F);
			}
			else
			{
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.posX - this.prevPosX;
			double d0 = this.posZ - this.prevPosZ;
			float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

			if (f1 > 1.0F)
			{
				f1 = 1.0F;
			}

			this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		}
		else
		{
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.travel(strafe, vertical, forward);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setBoolean("Saddle", this.getSaddled());
		compound.setBoolean("Digesting", this.digesting);
		compound.setInteger("DigestTime", this.digestTime);
	}
}