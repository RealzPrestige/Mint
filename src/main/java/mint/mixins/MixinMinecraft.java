package mint.mixins;

import mint.Mint;
import mint.modules.miscellaneous.SignExploit;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileWriter;

@Mixin(value = {Minecraft.class})
public abstract class MixinMinecraft {

    @Shadow public CrashReport crashReporter;

    @Inject(method = {"shutdownMinecraftApplet"}, at = {@At(value = "HEAD")})
    private void stopClient(CallbackInfo callbackInfo) {
        Mint.onUnload();
    }

    @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
        File crashFile = new File("mint/latestCrash.txt/");
        try {
            if (crashFile.exists()) {
                FileWriter writer = new FileWriter(crashFile);
                writer.write(crashReport.getCompleteReport());
                crashReport.saveToFile(crashFile);
            } else {
                crashFile.createNewFile();
                FileWriter writer = new FileWriter(crashFile);
                writer.write(crashReport.getCompleteReport());
                crashReport.saveToFile(crashFile);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Mint.onUnload();
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        SignExploit.nullCheck();
    }

    private long lastFrame = getTime();

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void runGameLoop(final CallbackInfo callbackInfo) {
        final long currentTime = getTime();
        final int deltaTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;

        RenderUtil.deltaTime = deltaTime;
    }

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

}

