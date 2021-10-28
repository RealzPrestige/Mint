package mint.setting;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.lwjgl.input.Keyboard;

public class Bind {
    private final int key;

    public Bind(int key) {
        this.key = key;
    }

    public static Bind none() {
        return new Bind(-1);
    }

    public int getKey() {
        return key;
    }

    public boolean isEmpty() {
        return key < 0;
    }

    public String toString() {
        return isEmpty() ? "None" : (key < 0 ? "None" : Keyboard.getKeyName(key));
    }

    public boolean isDown() {
        return !isEmpty() && Keyboard.isKeyDown(getKey());
    }

    public static class BindConverter extends Converter<Bind, JsonElement> {

        public JsonElement doForward(Bind bind) {
            return new JsonPrimitive(bind.toString());
        }

        public Bind doBackward(JsonElement jsonElement) {
            String s = jsonElement.getAsString();

            if (s.equalsIgnoreCase("None"))
                return Bind.none();

            int key = -1;

            try {
                key = Keyboard.getKeyIndex(s.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (key == 0)
                return Bind.none();

            return new Bind(key);
        }

        public int doBackwardInt(JsonElement element) {
            String s = element.getAsString();

            if (s.equalsIgnoreCase("None"))
                return -1;

            int key = -1;

            try {
                key = Keyboard.getKeyIndex(s.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (key == 0)
                return -1;

            return new Bind(key).key;

        }
    }
}




