package icbm.classic.lib.capability.ex;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.ICBMClassicHelpers;
import icbm.classic.api.caps.IExplosive;
import icbm.classic.api.reg.IExplosiveData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Dark(DarkGuardsman, Robert) on 1/7/19.
 */
public class CapabilityExplosive implements IExplosive, ICapabilitySerializable<NBTTagCompound>
{
    //Parent tag
    public static final String NBT_EXPLOSIVE = "explosive";

    //Child tags
    public static final String NBT_EXPLOSIVE_ID = "explosiveID";
    public static final String NBT_BLAST_DATA = "blastData";

    public int explosiveID; //TODO change over to resource location or include in save to check for issues using ID only for in memory
    public NBTTagCompound blastNBT;

    public CapabilityExplosive()
    {
    }

    public CapabilityExplosive(int id)
    {
        this.explosiveID = id;
    }

    @Nullable
    @Override
    public IExplosiveData getExplosiveData()
    {
        return ICBMClassicHelpers.getExplosive(explosiveID, false);
    }

    @Nullable
    @Override
    public NBTTagCompound getCustomBlastData()
    {
        if (blastNBT == null)
        {
            blastNBT = new NBTTagCompound();
        }
        return blastNBT;
    }

    public void setCustomData(NBTTagCompound data)
    {
        blastNBT = data;
    }

    @Nullable
    @Override
    public ItemStack toStack()
    {
        return null;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == ICBMClassicAPI.EXPLOSIVE_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == ICBMClassicAPI.EXPLOSIVE_CAPABILITY)
        {
            return (T) this;
        }
        return null;
    }

    @Override
    public final NBTTagCompound serializeNBT()
    {
        final NBTTagCompound tagCompound = new NBTTagCompound();
        serializeNBT(tagCompound);

        tagCompound.setInteger(NBT_EXPLOSIVE_ID, explosiveID);
        tagCompound.setTag(NBT_BLAST_DATA, getCustomBlastData());
        return tagCompound;
    }

    protected void serializeNBT(NBTTagCompound tag)
    {

    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey(NBT_EXPLOSIVE_ID))
        {
            explosiveID = nbt.getInteger(NBT_EXPLOSIVE_ID);
        }
        if (blastNBT == null || nbt.hasKey(NBT_BLAST_DATA))
        {
            blastNBT = nbt.getCompoundTag(NBT_BLAST_DATA);
        }
    }

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IExplosive.class, new Capability.IStorage<IExplosive>()
                {
                    @Nullable
                    @Override
                    public NBTBase writeNBT(Capability<IExplosive> capability, IExplosive instance, EnumFacing side)
                    {
                        if (instance instanceof CapabilityExplosive)
                        {
                            return ((CapabilityExplosive) instance).serializeNBT();
                        }
                        return null;
                    }

                    @Override
                    public void readNBT(Capability<IExplosive> capability, IExplosive instance, EnumFacing side, NBTBase nbt)
                    {
                        if (instance instanceof CapabilityExplosive && nbt instanceof NBTTagCompound)
                        {
                            ((CapabilityExplosive) instance).deserializeNBT((NBTTagCompound) nbt);
                        }
                    }
                },
                () -> new CapabilityExplosive(-1));
    }
}
