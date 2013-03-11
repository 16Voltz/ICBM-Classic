package icbm.zhapin.render;

import icbm.core.ZhuYao;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class RItDaoDan implements IItemRenderer
{

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return this.shouldUseRenderHelper(type, item, null);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return item.itemID == ZhuYaoZhaPin.itDaoDan.itemID || (item.itemID == ZhuYaoZhaPin.itTeBieDaoDan.itemID && item.getItemDamage() > 0);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		if (this.shouldUseRenderHelper(type, item, null))
		{
			float scale = 0.4f;
			float right = 0.15f;

			if (ZhaPin.list[item.getItemDamage()].getTier() == 2 || item.itemID == ZhuYaoZhaPin.itTeBieDaoDan.itemID)
			{
				scale = 0.3f;
			}
			else if (ZhaPin.list[item.getItemDamage()].getTier() == 3)
			{
				scale = 0.25f;
				right = 0.4f;
			}

			if (type == ItemRenderType.INVENTORY)
			{
				GL11.glTranslatef(right, 0f, 0f);
			}

			if (type == ItemRenderType.EQUIPPED)
			{
				GL11.glTranslatef(-0.15f, 1.2f, 0.5f);
				GL11.glRotatef(180, 0, 0, 1f);
			}
			else
			{
				GL11.glRotatef(-90, 0, 0, 1f);
			}

			GL11.glScalef(scale, scale, scale);

			if (item.itemID == ZhuYaoZhaPin.itTeBieDaoDan.itemID)
			{
				if (item.getItemDamage() - 1 < RDaoDan.specialModels.length)
				{
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.MODEL_PATH + RDaoDan.specialModels[Math.max(item.getItemDamage() - 1, 0)].texture + ".png"));
					RDaoDan.specialModels[item.getItemDamage() - 1].model.render(0.0625F);
				}
			}
			else
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.MODEL_PATH + RDaoDan.models[item.getItemDamage()].texture + ".png"));
				RDaoDan.models[item.getItemDamage()].model.render(0.0625F);
			}
		}
	}
}
