package mint.security;

import org.apache.commons.codec.digest.DigestUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HWID {

    public static List<String> hwids = new ArrayList<>();

    public static void authenticate() {
        hwids = readurl();
        for (String hwid : readurl()) {
            String[] hwidSub = hwid.split("-");
            hwids.add(hwidSub[0]);
        }
        boolean isHwidPresent = hwids.contains(getID());
        if (!isHwidPresent) {
            copyToClipboard();
            throw new NoTrace("Authenticating HWID Failed.");
        }
    }

    public static void copyToClipboard() {
        StringSelection selection = new StringSelection(getID());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public static String getID() {
        return DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")).toUpperCase(Locale.ROOT);
    }

    public static List<String> readurl() {
        List<String> s = new ArrayList<>();
        try {
            final URL url = new URL("https://pastebin.com/raw/x0VbrKZJ");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String hwid;
            while ((hwid = bufferedReader.readLine()) != null) {
                s.add(hwid);
            }
        } catch (Exception ignored) {
        }
        return s;
    }
}
