package mint.events;

import net.minecraft.util.math.BlockPos;

/**
 * @author kambing
 */

public class CrystalPlaceEvent extends EventProcessor {
    BlockPos pos;
    public CrystalPlaceEvent(BlockPos pos) {
        this.pos = pos;
    }
    public BlockPos getPos() {
        return pos;
    }
}