package services;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

public class LoginDialog extends JDialog implements KeyListener {

    private static final Logger         LOGGER            = Logger.getLogger(LoginDialog.class);

    private static final long           serialVersionUID  = 1L;

    private static final int            BOXWIDTH          = 480, BOXHEIGHT = 160;

    private final JLabel                jlblUsername      = new JLabel("Username (Required)");

    private final JLabel                jlblPassword      = new JLabel("Password (Required)");

    private final JCheckBox             jcbxDebug         = new JCheckBox("Debug Mode");

    private final JCheckBox             jcbxForceComplete = new JCheckBox("Force-Fill All Entries Before Today");

    private final JCheckBox             jcbxCompleteAll   = new JCheckBox("Fill Entries Past Today Until End of Month.");

    // Not implemented
    private final JCheckBox             jcbxPartial       = new JCheckBox("Allow Filling of Partial (Incomplete) Attendance Times.");

    private static final JTextField     jtfUsername       = new JTextField() {

        private static final long serialVersionUID = 1L;

        @Override
        public void addNotify() {

            super.addNotify();
            requestFocusInWindow();
        }
    };

    private static final JPasswordField jpfPassword       = new JPasswordField();

    private final JButton               jbtOk             = new JButton("Ok");

    private final JButton               jbtCancel         = new JButton("Cancel");

    private boolean                     bCanceled         = false;

    public LoginDialog() {

        this(null, true);
    }

    public LoginDialog(final JFrame parent, boolean modal) {

        super(parent, modal);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("User Info Input");
        setSize(BOXWIDTH, BOXHEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel usernameLayout = new JPanel(new GridLayout(1, 2));
        JPanel passwordLayout = new JPanel(new GridLayout(1, 2));
        JPanel confirmCancelLayout = new JPanel();
        confirmCancelLayout.setLayout(new GridLayout(1, 2));

        usernameLayout.add(jlblUsername);
        usernameLayout.add(jtfUsername);

        passwordLayout.add(jlblPassword);
        passwordLayout.add(jpfPassword);

        confirmCancelLayout.add(jbtOk);
        confirmCancelLayout.add(jbtCancel);

        setLayout(new GridLayout(5, 1));
        add(usernameLayout);
        add(passwordLayout);
        add(confirmCancelLayout);

        jbtOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {

                pressOkBtn();
            }
        });
        jtfUsername.addKeyListener(this);
        jpfPassword.addKeyListener(this);

        jbtCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                bCanceled = true;
                setVisible(false);
            }
        });

    }

    public void showDialog() {

        String uname = null;

        if (uname == null) {
            uname = "";
        }

        jtfUsername.setText(uname);
        jpfPassword.setText("");
        jtfUsername.requestFocus();
        this.setModal(true);
        this.setVisible(true);
    }

    public boolean isCanceled() {

        return bCanceled;
    }

    public void setCanceled(boolean bool) {

        bCanceled = bool;
    }

    public static String getUsername() {

        return jtfUsername.getText();
    }

    public static void setUsername(String username) {

        jtfUsername.setText(username);
    }

    public static String getPassword() {

        return String.valueOf(jpfPassword.getPassword());
    }

    public static void setPassword(String password) {

        jpfPassword.setText(password);
    }

    public boolean isDebugMode() {

        return jcbxDebug.isSelected();
    }

    public boolean isForceComplete() {

        return jcbxForceComplete.isSelected();
    }

    public boolean isCompleteAll() {

        return jcbxCompleteAll.isSelected();
    }

    public boolean isPartial() {

        return jcbxPartial.isSelected();
    }

    private void pressOkBtn() {

        try {
            if (!"".equals(jtfUsername.getText().trim()) && !"".equals(String.valueOf(jpfPassword.getPassword()))) {
                bCanceled = false;
                setVisible(false);
            } else {
                jtfUsername.selectAll();
                jpfPassword.selectAll();
                jtfUsername.requestFocus();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

        return;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            pressOkBtn();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        return;
    }

    public JCheckBox getJcbxDebug() {

        return jcbxDebug;
    }

    public JCheckBox getJcbxForceComplete() {

        return jcbxForceComplete;
    }

    public JCheckBox getJcbxCompleteAll() {

        return jcbxCompleteAll;
    }

    public JCheckBox getJcbxPartial() {

        return jcbxPartial;
    }

    public JButton getJbtCancel() {

        return jbtCancel;
    }

    public JButton getJbtOk() {

        return jbtOk;
    }

    public JTextField getJtfUsername() {

        return jtfUsername;
    }

    public JPasswordField getJpfPassword() {

        return jpfPassword;
    }

}
