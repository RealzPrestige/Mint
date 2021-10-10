package mint.commands;

import mint.Mint;
import net.minecraft.network.play.client.CPacketPlayer;

public class Kick extends Command {

    public Kick() {
        super("kick");
    }

    @Override
    public void execute(String[] commands) {
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, !Mint.INSTANCE.mc.player.onGround));
    }
}