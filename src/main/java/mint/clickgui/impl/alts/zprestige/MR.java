package mint.clickgui.impl.alts.zprestige;

import mint.clickgui.impl.alts.tools.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import java.lang.reflect.Field;

public class MR {

	public static void init(){
		Config.load();
	}

	public static void setSession(Session s) throws Exception {
		Class<? extends Minecraft> mc = Minecraft.getMinecraft().getClass();
		try {
			Field session = null;

			for (Field f : mc.getDeclaredFields()) {
				if (f.getType().isInstance(s)) {
					session = f;
					System.out.println("Found field " + f + ", injecting...");
				}
			}

			if (session == null) {
				throw new IllegalStateException("No field of type " + Session.class.getCanonicalName() + " declared.");
			}

			session.setAccessible(true);
			session.set(Minecraft.getMinecraft(), s);
			session.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
