package icbm.classic.content.machines;

import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import icbm.classic.ICBMClassic;
import icbm.classic.Reference;
import icbm.classic.client.render.tile.RenderMissileCoordinator;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Missile Coordinator
 *
 * @author Calclavia
 */
public class TileMissileCoordinator extends TileModuleMachine implements ISimpleItemRenderer, IRecipeContainer
{
    public TileMissileCoordinator()
    {
        super("missileCoordinator", Material.iron);
        addInventoryModule(2);
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public String getInventoryName()
    {
        return LanguageUtility.getLocal("gui.coordinator.name");
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return itemstack.getItem() instanceof IWorldPosItem;
    }

    @Override
    public boolean onPlayerActivated(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, Reference.PREFIX + "interface", 1, (float) (this.worldObj.rand.nextFloat() * 0.2 + 0.9F));
            openGui(player, ICBMClassic.INSTANCE);
        }

        return true;
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(0f, 1.1f, 0f);
        GL11.glRotatef(180f, 0f, 0f, 1f);
        GL11.glRotatef(180f, 0f, 1f, 0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderMissileCoordinator.TEXTURE_FILE);
        RenderMissileCoordinator.MODEL.render(0, 0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMClassic.blockMissileCoordinator, 1, 12),
                "R R", "SCS", "SSS",
                'C', UniversalRecipe.CIRCUIT_T2.get(),
                'S', UniversalRecipe.PRIMARY_PLATE.get(),
                'R', ICBMClassic.itemRemoteDetonator));
    }
}
