package mod.acgaming.spackenmobs.entities;
import mod.acgaming.spackenmobs.Spackenmobs;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityMrBean extends EntityZombie
{  
	public EntityMrBean(World worldIn)
	{
        super(worldIn);
        this.setSize(0.6F, 1.95F);
    }
	
    protected SoundEvent getAmbientSound()
    {
        return Spackenmobs.ENTITY_MRBEAN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return Spackenmobs.ENTITY_MRBEAN_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return Spackenmobs.ENTITY_MRBEAN_DEATH;
    }
}