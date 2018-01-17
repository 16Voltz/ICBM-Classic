package icbm.classic.content.machines.launcher.cruise;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import icbm.classic.ICBMClassic;
import icbm.classic.prefab.BlockICBM;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/15/2018.
 */
public class BlockCruiseLauncher extends BlockICBM
{
    public BlockCruiseLauncher()
    {
        super("cruiseLauncher");
        this.blockHardness = 10f;
        this.blockResistance = 10f;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileCruiseLauncher();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileCruiseLauncher)
            {
                TileCruiseLauncher launcher = (TileCruiseLauncher) tileEntity;
                ItemStack stack = player.getHeldItem(hand);
                if (stack.getItem() == Items.REDSTONE)
                {
                    if (launcher.canLaunch())
                    {
                        launcher.launch();
                    }
                    else
                    {
                        player.sendMessage(new TextComponentString(LanguageUtility.getLocal("chat.launcher.failedToFire")));
                        String translation = LanguageUtility.getLocal("chat.launcher.status");
                        translation = translation.replace("%1", launcher.getStatus());
                        player.sendMessage(new TextComponentString(translation));
                    }
                }
                else if (stack.getItem() instanceof IWorldPosItem)
                {
                    IWorldPosition location = ((IWorldPosItem) stack.getItem()).getLocation(stack);
                    if (location != null)
                    {
                        if (location.oldWorld() == world)
                        {
                            launcher.setTarget(new Pos(location.x(), location.y(), location.z()));
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
                else
                {
                    player.openGui(ICBMClassic.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }
}
