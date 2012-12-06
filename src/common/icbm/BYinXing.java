package icbm;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.BlockMachine;

public class BYinXing extends BlockMachine
{
	protected BYinXing(int id)
	{
		super("camouflage", id, Material.cloth);
		this.setHardness(0.3F);
		this.setResistance(1F);
		this.setStepSound(this.soundClothFootstep);
		this.setCreativeTab(ZhuYao.TAB);
	}

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z,
	 * side
	 */
	@Override
	public int getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		TileEntity t = par1IBlockAccess.getBlockTileEntity(x, y, z);

		if (t != null)
		{
			if (t instanceof TYinXing)
			{
				TYinXing tileEntity = (TYinXing) par1IBlockAccess.getBlockTileEntity(x, y, z);

				if (tileEntity.getQing(ForgeDirection.getOrientation(side))) { return Block.glass.blockIndexInTexture; }

				try
				{
					return Block.blocksList[tileEntity.getJiaHaoMa()].getBlockTexture(par1IBlockAccess, x, y, z, side);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (par5EntityPlayer.getCurrentEquippedItem() != null)
		{
			Block block = Block.blocksList[par5EntityPlayer.getCurrentEquippedItem().itemID];

			if (block != null)
			{
				if ((block.getRenderType() == 0 || block.getRenderType() == 31) && block.blockID <= 145)
				{
					((TYinXing) par1World.getBlockTileEntity(x, y, z)).setJiaHaoMa(block.blockID);
					par1World.markBlockForRenderUpdate(x, y, z);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity t = par1World.getBlockTileEntity(x, y, z);

		if (t != null)
		{
			if (t instanceof TYinXing)
			{
				((TYinXing) par1World.getBlockTileEntity(x, y, z)).setQing(ForgeDirection.getOrientation(side));
				par1World.markBlockForRenderUpdate(x, y, z);
			}
		}

		return true;
	}

	@Override
	public boolean onSneakUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity t = par1World.getBlockTileEntity(x, y, z);

		if (t != null)
		{
			if (t instanceof TYinXing)
			{
				((TYinXing) par1World.getBlockTileEntity(x, y, z)).setYing();
			}
		}

		return true;
	}

	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color.
	 * Note only called when first determining what to render.
	 */
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int x, int y, int z)
	{
		try
		{
			Block block = Block.blocksList[((TYinXing) par1IBlockAccess.getBlockTileEntity(x, y, z)).getJiaHaoMa()];

			if (block == Block.grass)
			{
				int redColor = 0;
				int greenColor = 0;
				int blueColork = 0;

				for (int izy = -1; izy <= 1; izy++)
				{
					for (int ix = -1; ix <= 1; ix++)
					{
						int grassColor = par1IBlockAccess.getBiomeGenForCoords(x + ix, z + izy).getBiomeGrassColor();
						redColor += ((grassColor & 0xFF0000) >> 16);
						greenColor += ((grassColor & 0xFF00) >> 8);
						blueColork += (grassColor & 0xFF);
					}
				}

				return (redColor / 9 & 0xFF) << 16 | (greenColor / 9 & 0xFF) << 8 | blueColork / 9 & 0xFF;
			}

			return block.colorMultiplier(par1IBlockAccess, x, y, x);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return 16777215;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		TileEntity t = par1World.getBlockTileEntity(x, y, z);

		if (t != null)
		{
			if (t instanceof TYinXing)
			{
				if (((TYinXing) t).getYing()) { return super.getCollisionBoundingBoxFromPool(par1World, x, y, z); }
			}
		}

		return null;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
		return var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TYinXing();
	}
}
