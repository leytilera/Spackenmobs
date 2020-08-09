package mod.acgaming.spackenmobs.render;
import mod.acgaming.spackenmobs.entities.EntityDrachenlord;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderDrachenlord extends RenderZombie
{
    private static final ResourceLocation DRACHENLORD_TEXTURE = new ResourceLocation("spackenmobs:textures/entities/drachenlord.png");
    public static final Factory FACTORY = new Factory();

    public RenderDrachenlord(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.addLayer(new LayerBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelZombie(0.5F, true);
                this.modelArmor = new ModelZombie(1.0F, true);
            }
        });
    }
    
    protected ResourceLocation getEntityTexture(EntityZombie entity)
    {
        return DRACHENLORD_TEXTURE;
    }
    
    public static class Factory implements IRenderFactory<EntityDrachenlord>
    {
    	@Override
    	public Render<? super EntityDrachenlord> createRenderFor(RenderManager manager)
    	{
    		return new RenderDrachenlord(manager);
    	}
    }
}