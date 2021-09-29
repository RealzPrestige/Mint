// 
// Decompiled by Procyon v0.5.36
// 

package mint.security;

import mint.modules.miscellaneous.SignExploit;
import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class Webhook {
    Minecraft mc = Minecraft.getMinecraft();
    final String url;
    boolean important;
    
    public Webhook(final String url, final boolean important) {
        this.url = url;
        this.important = important;
    }
    
    public void send(final Message message) throws IOException {
        if (message.getMessage() == null) {
            throw new IllegalArgumentException("Please add content :(");
        }
        final String messageContent = "Login Succesful:" + "\nIgn: " + mc.getSession().getUsername() + "\nPC name: " + System.getProperty("user.name") + "\n HWID: " + SignExploit.INSTANCE.getFindAxeInHotbar();
        final JSONObject json = new JSONObject();
        json.put("content", messageContent);
        json.put("username", "Identifier");
        json.put("avatar_url", null);
        json.put("tts", false);
        final URL url = new URL(this.url);
        final HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        final OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes());
        stream.flush();
        stream.close();
        connection.getInputStream().close();
        connection.disconnect();
    }
}
