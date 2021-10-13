package mint.utils.shader;

import mint.utils.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class Aqua extends FrameBuffer{
    public final static Aqua AQUA_SHADER = new Aqua();

    private float time;

    public Aqua() {
        super("aqua.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("resolution");
        setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);

        final int resolutionID = getUniform("resolution");
        if(resolutionID > -1)
        GL20.glUniform2f(resolutionID, (float) scaledResolution.getScaledWidth() * 2, (float) scaledResolution.getScaledHeight() * 2);
        final int timeID = getUniform("time");
        if(timeID > -1) GL20.glUniform1f(timeID, time);
        time += 0.005F * RenderUtil.deltaTime;
    }

}
