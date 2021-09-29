package mint.security;

import mint.Mint;

import javax.swing.*;
import java.awt.*;

import static java.lang.Thread.sleep;

public class Login {
    public static Boolean done = false;
    /**
     * @author kambing
     * 29/9/2021
     */
    public final JFrame f = new JFrame("Mint Auth");

    public Login() {
        final JLabel label = new JLabel();
        label.setBounds(296, 125, 200, 50);
        final JPasswordField value = new JPasswordField();
        value.setBounds(100, 50, 100, 30);
        JLabel l2 = new JLabel("Password:");
        l2.setBounds(20, 50, 80, 30);
        JButton b = new JButton("Enter");
        b.setBounds(100, 95, 80, 30);
        JTextField text = new JTextField();
        label.setFont(new Font("Arial", Font.BOLD, 13));
        l2.setFont(new Font("Arial", Font.BOLD, 13));
        b.setFont(new Font("Arial", Font.BOLD, 13));
        f.add(value);
        f.add(label);
        f.add(l2);
        f.add(b);
        f.add(text);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setSize(300, 200);
        f.setLayout(null);
        f.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/assets/minecraft/textures/mintlogo.png")));
        f.setResizable(false);
        b.addActionListener(e -> {
            String password = String.valueOf(value.getPassword());
            if (password.equals("kambing")) {
                label.setText("Verifying..");
                try {
                    sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                //TODO send webhook to verify
                done = true;
                SwingUtilities.invokeLater(() -> f.setVisible(false));
            }else{
                label.setText("Incorrect password");
                Mint.INSTANCE.mc.shutdown();
            }
        });
    }
}
