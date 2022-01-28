package com.veteam.voluminousenergy.blocks.tiles;

/**
 * Any value that is unused (for example getPowerUsage() in a generator tile)
 * should be set to 0
 * Note: These values should all be pulled from
 * @see com.veteam.voluminousenergy.tools.Config
 * Except for
 * @see #getUpgradeSlotId()
 */
public interface IVEPoweredTileEntity {

    /**
     * @return The max power the tile entity can store at one time
     */
    int getMaxPower();

    /**
     *
     * @return the power usage the entity uses per tick
     * Should be 0 if representing a tile that only produces power
     */
    int getPowerUsage();

    /**
     * The IO rate of energy from this tile
     * for tiles that generate power this value should be set to Config..._SEND
     * @return The maximum allowed throughput of a tile entity.
     */
    int getTransferRate();

    /**
     * Used in conjunction with createHandler
     * @see com.veteam.voluminousenergy.blocks.tiles.VoluminousTileEntity#createHandler(int,IVEPoweredTileEntity) ;
     * Otherwise this method does nothing
     * @return The slot id of the upgrade slot
     */
    int getUpgradeSlotId();
}
