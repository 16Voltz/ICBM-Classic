package icbm.common.daodan;

import icbm.common.ZhuYao;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItTeBieDaoDan extends ItDaoDan
{
	public static String[] names = { ZhuYao.getLocal("icbm.missile.missileModule"), ZhuYao.getLocal("icbm.missile.antiBallistic") + " " + ZhuYao.getLocal("icbm.missile"), ZhuYao.getLocal("icbm.missile.cluster") + " " + ZhuYao.getLocal("icbm.missile"), ZhuYao.getLocal("icbm.missile.nuclearCluster") + " " + ZhuYao.getLocal("icbm.missile"), ZhuYao.getLocal("icbm.missile.homing") + " " + ZhuYao.getLocal("icbm.missile") };

	public ItTeBieDaoDan(String name, int id, int texture)
	{
		super(name, id, texture);
		this.setCreativeTab(ZhuYao.TAB);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return names[itemstack.getItemDamage()];
	}

	@Override
	public int getIconFromDamage(int i)
	{
		return this.iconIndex;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < names.length; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}