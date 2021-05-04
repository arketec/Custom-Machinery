package fr.frinn.custommachinery.common.util;

import fr.frinn.custommachinery.common.init.CustomMachineTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class FuelManager implements INBTSerializable<CompoundNBT> {

    private CustomMachineTile tile;
    private int fuel;
    private int maxFuel;

    public FuelManager(CustomMachineTile tile) {
        this.tile = tile;
    }

    public int getFuel() {
        return this.fuel;
    }

    public int getMaxFuel() {
        return this.maxFuel;
    }

    public void addFuel(int fuel) {
        this.fuel += fuel;
        this.maxFuel = fuel;
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public boolean consume() {
        if(this.fuel > 0) {
            this.fuel--;
            return true;
        }
        return false;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("fuel", this.fuel);
        nbt.putInt("maxFuel", this.maxFuel);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(nbt.contains("fuel", Constants.NBT.TAG_INT))
            this.fuel = nbt.getInt("fuel");
        if(nbt.contains("maxFuel", Constants.NBT.TAG_INT))
            this.maxFuel = nbt.getInt("maxFuel");
    }
}
