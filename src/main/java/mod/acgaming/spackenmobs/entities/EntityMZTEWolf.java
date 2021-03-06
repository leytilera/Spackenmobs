package mod.acgaming.spackenmobs.entities;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityMZTEWolf extends EntityWolf
{
	public EntityMZTEWolf(World worldIn)
	{
		super(worldIn);
		this.setSize(0.6F, 0.85F);
		this.setTamed(false);
	}

	@Override
	public EntityMZTEWolf createChild(EntityAgeable ageable)
	{
		EntityMZTEWolf entitymztewolf = new EntityMZTEWolf(this.world);
		UUID uuid = this.getOwnerId();

		if (uuid != null)
		{
			entitymztewolf.setOwnerId(uuid);
			entitymztewolf.setTamed(true);
		}

		return entitymztewolf;
	}

	@Override
	public boolean canMateWith(EntityAnimal otherAnimal)
	{
		if (otherAnimal == this)
		{
			return false;
		} else if (!this.isTamed())
		{
			return false;
		} else if (!(otherAnimal instanceof EntityMZTEWolf))
		{
			return false;
		} else
		{
			EntityMZTEWolf entitymztewolf = (EntityMZTEWolf) otherAnimal;

			if (!entitymztewolf.isTamed())
			{
				return false;
			} else if (entitymztewolf.isSitting())
			{
				return false;
			} else
			{
				return this.isInLove() && entitymztewolf.isInLove();
			}
		}
	}

	@Override
	public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner)
	{
		if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast))
		{
			if (target instanceof EntityMZTEWolf)
			{
				EntityMZTEWolf entitymztewolf = (EntityMZTEWolf) target;

				if (entitymztewolf.isTamed() && entitymztewolf.getOwner() == owner)
				{
					return false;
				}
			}

			if (target instanceof EntityPlayer && owner instanceof EntityPlayer
					&& !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target))
			{
				return false;
			} else
			{
				return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
			}
		} else
		{
			return false;
		}
	}
}