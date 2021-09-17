package mint.clickgui.setting;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.lwjgl.input.Keyboard;

public class BindSetting {
    private int key;

    public BindSetting(int key) {
        this.key = key;
    }

    public static BindSetting none() {
        return new BindSetting(-1);
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isEmpty() {
        return this.key < 0;
    }

    public String toString() {
        return this.isEmpty() ? "None" : (this.key < 0 ? "None" : this.capitalise(Keyboard.getKeyName(this.key)));
    }

    public boolean isDown() {
        return !this.isEmpty() && Keyboard.isKeyDown(this.getKey());
    }

    private String capitalise(String str) {
        if (str.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + (str.length() != 1 ? str.substring(1).toLowerCase() : "");
    }

    public static class BindConverter
            extends Converter<BindSetting, JsonElement> {
        public JsonElement doForward(BindSetting bind) {
            return new JsonPrimitive(bind.toString());
        }

        public BindSetting doBackward(JsonElement jsonElement) {
            String s = jsonElement.getAsString();
            if (s.equalsIgnoreCase("None")) {
                return BindSetting.none();
            }
            int key = -1;
            try {
                key = Keyboard.getKeyIndex(s.toUpperCase());
            } catch (Exception exception) {
                // empty catch block
            }
            if (key == 0) {
                return BindSetting.none();
            }
            return new BindSetting(key);
        }
    }
}

