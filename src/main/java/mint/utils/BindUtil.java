package mint.utils;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.lwjgl.input.Keyboard;

public class BindUtil {
    private final int key;

    public BindUtil(int key) {
        this.key = key;
    }

    public static BindUtil none() {
        return new BindUtil(-1);
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

    public static class BindConverter extends Converter<BindUtil, JsonElement> {

        public JsonElement doForward(BindUtil bindUtil) {
            return new JsonPrimitive(bindUtil.toString());
        }

        public BindUtil doBackward(JsonElement jsonElement) {
            String s = jsonElement.getAsString();

            if (s.equalsIgnoreCase("None"))
                return BindUtil.none();

            int key = -1;

            try {
                key = Keyboard.getKeyIndex(s.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (key == 0)
                return BindUtil.none();

            return new BindUtil(key);
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

            return new BindUtil(key).key;

        }
    }
}




