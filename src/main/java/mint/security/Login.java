package mint.security;

import mint.modules.Module;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

import static java.lang.Thread.sleep;

public class Login {
    public static Boolean done = false;
    /**
     * @author kambing
     * 29/9/2021
     */
    public final JFrame frame = new JFrame("Mint Auth");

    public Login() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.put("control", new Color(29, 25, 25));
            UIManager.put("info", new Color(42, 39, 39));
            UIManager.put("nimbusBase", new Color(54, 54, 62));
            UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new Color(10, 15, 23));
            UIManager.put("nimbusOrange", new Color(191, 98, 4));
            UIManager.put("nimbusRed", new Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
            UIManager.put("text", new Color(230, 230, 230));
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (UnsupportedLookAndFeelException exc) {
            System.err.println("Nimbus: Unsupported Look and feel!");
        }
        final JLabel label = new JLabel();
        label.setBounds(20, 125, 200, 50);
        final JPasswordField value = new JPasswordField();
        value.setBounds(100, 50, 100, 30);
        JLabel passString = new JLabel("Password:");
        JLabel welcomer = new JLabel("Welcome, " + Module.mc.getSession().getProfile().getName() + " !");
        welcomer.setBounds(100, 10, 800, 50);
        passString.setBounds(20, 50, 80, 30);
        JButton enter = new JButton("Enter");
        enter.setBounds(100, 95, 80, 30);
        JTextField text = new JTextField();
        label.setFont(new Font("Arial", Font.BOLD, 13));
        passString.setFont(new Font("Arial", Font.BOLD, 13));
        enter.setFont(new Font("Arial", Font.BOLD, 13));
        welcomer.setFont(new Font("Arial", Font.BOLD, 15));
        frame.add(value);
        frame.add(label);
        frame.add(passString);
        frame.add(enter);
        frame.add(text);
        frame.add(welcomer);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setSize(300, 200);
        frame.setLayout(null);
        enter.addActionListener(e -> {
            String password = String.valueOf(value.getPassword());
            if (password.equals("kambing")) { //TODO add auth key thing
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
                label.setText("YO RATTED LOLOLOL");
                // webhook
            }
        });
    }
}
