package icbm.classic.content.blocks.launcher.screen;

import icbm.classic.api.IWorldPosition;
import icbm.classic.api.items.IWorldPosItem;
import icbm.classic.content.items.ItemLaserDetonator;
import icbm.classic.lib.transform.vector.Pos;
import icbm.classic.lib.LanguageUtility;
import icbm.classic.ICBMClassic;
import icbm.classic.content.items.ItemRemoteDetonator;
import icbm.classic.prefab.tile.BlockICBM;
import icbm.classic.api.EnumTier;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 *
 * Created by Dark(DarkGuardsman, Robert) on 1/16/2018.
 */
public class BlockLaunchScreen extends BlockICBM
{
    public BlockLaunchScreen()
    {
        super("launcherscreen");
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileLauncherScreen)
            {
                TileLauncherScreen screen = (TileLauncherScreen) tileEntity;

                ItemStack stack = player.getHeldItem(hand);
                if (stack.getItem() == Items.REDSTONE)
                {
                    if (screen.canLaunch())
                    {
                        screen.launch();
                    }
                    else
                    {
                        player.sendMessage(new TextComponentString(LanguageUtility.getLocal("chat.launcher.failedToFire")));

                        String translation = LanguageUtility.getLocal("chat.launcher.status"); //TODO replace %1 with %s in translation file
                        translation = translation.replace("%1", screen.getStatus());
                        player.sendMessage(new TextComponentString(translation));
                    }
                }
                else if (stack.getItem() instanceof ItemRemoteDetonator)
                {
                    ((ItemRemoteDetonator) stack.getItem()).setBroadCastHz(stack, screen.getFrequency());
                    player.sendMessage(new TextComponentString(LanguageUtility.getLocal("chat.launcher.toolFrequencySet").replace("%1", "" + screen.getFrequency())));
                }
                else if (stack.getItem() instanceof ItemLaserDetonator)
                {
                    ((ItemLaserDetonator) stack.getItem()).setBroadCastHz(stack, screen.getFrequency());
                    player.sendMessage(new TextComponentString(LanguageUtility.getLocal("chat.launcher.toolFrequencySet").replace("%1", "" + screen.getFrequency())));
                }
                else if (stack.getItem() instanceof IWorldPosItem)
                {
                    IWorldPosition location = ((IWorldPosItem) stack.getItem()).getLocation(stack);
                    if (location != null)
                    {
                        if (location.world() == world)
                        {
                            screen.setTarget(new Pos(location.x(), location.y(), location.z()));
                            player.sendMessage(new TextComponentString(LanguageUtility.getLocal("chat.launcher.toolTargetSet")));
                        }
                        else
                        {
                            player.sendMessage(new TextComponentString(LanguageUtility.getLocal("chat.launcher.toolWorldNotMatch")));
                        }
                    }
                    else
                    {
                        player.sendMessage(new TextComponentString(LanguageUtility.getLocal("chat.launcher.noTargetInTool")));
                    }
                }
                else if(screen.launcherBase == null || !screen.launcherBase.tryInsertMissile(player, hand, player.getHeldItem(hand)))
                {
                    player.openGui(ICBMClassic.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if(!world.isRemote)
        {
            TileEntity te = world.getTileEntity(pos);

            if(te instanceof TileLauncherScreen && world.isBlockPowered(pos))
            {
                if(((TileLauncherScreen)te).canLaunch())
                    ((TileLauncherScreen)te).launch();
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileLauncherScreen();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(TIER_PROP).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, ROTATION_PROP, TIER_PROP);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        EnumTier tier = EnumTier.ONE;
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileLauncherScreen)
        {
            tier = ((TileLauncherScreen) tile)._tier;
        }
        return state.withProperty(TIER_PROP, tier);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        ItemStack stack = placer.getHeldItem(hand);

        //Set tier
        return state.withProperty(TIER_PROP, EnumTier.get(stack.getItemDamage()));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileLauncherScreen)
        {
            ((TileLauncherScreen) tile)._tier = EnumTier.get(stack.getItemDamage());
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 2));
    }
}
