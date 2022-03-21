package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingUtilities;

/**
 * Creates a menu that is used to go to either the password generator
 * or password manager. This is the entry point of the program.
 * After selecting a menu option, the menu closes and can only be
 * re-opened by starting the program again.
 */
public class Menu extends JFrame {
    private final int BUTTONS_VGAP = 20;

    /**
     * Create a <code>JFrame</code> to add GUI components to.
     *
     * @param windowName a string used to display as the name of the window
     */
    public Menu(String windowName) {
        super(windowName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Creates all the components required for the GUI, and adds them to the
     * pane. Also adds styling and button functionality.
     *
     * @param pane the container that the components are added to
     */
    private void addComponents(final Container pane) {
        // Create panels and set layouts
        JPanel titlePanel = new JPanel();
        String titleText = "<html><h2>Password Generator and Manager</h2></html>";
        titlePanel.add(new JLabel(titleText, JLabel.CENTER));

        GridLayout buttonsLayout = new GridLayout(2, 1);
        buttonsLayout.setVgap(BUTTONS_VGAP);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(buttonsLayout);
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create buttons and add action listeners
        JButton toPasswordGeneratorButton = new JButton("Password Generator");
        toPasswordGeneratorButton.setPreferredSize(new Dimension(150, 50));
        toPasswordGeneratorButton.addActionListener(new ActionListener() {
            /**
             * Creates a <code>PasswordGenerator</code> instance and creates
             * and shows the password generator GUI. Then closes/disposes the menu.
             *
             * @param e the event being processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new PasswordGenerator("Password Generator").createAndShowWindow();
                    }
                });
                dispose();
            }
        });
        
        JButton toPasswordManagerButton = new JButton("Password Manager");
        toPasswordManagerButton.setPreferredSize(new Dimension(150, 50));
        toPasswordManagerButton.addActionListener(new ActionListener() {
            /**
             * Creates a <code>PasswordManager</code> instance and creates
             * and shows the password manager GUI. Then closes/disposes the menu.
             *
             * @param e the event being processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new PasswordManager("Password Manager").createAndShowWindow();
                    }
                });
                dispose();
            }
        });
        
        // Add the buttons to the panel
        buttonsPanel.add(toPasswordGeneratorButton);
        buttonsPanel.add(toPasswordManagerButton);

        // Add panels to the pane
        pane.add(titlePanel, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates an instance of the GUI and adds components, then displays the window.
     */
    public static void createAndShowWindow() {
        // Create the menu window 
        Menu menu = new Menu("Password Generator and Manager");
        // Add the content to the pane
        menu.addComponents(menu.getContentPane());
        // Display the window
        menu.pack();
        menu.setVisible(true);
    }

    /**
     * Shows the menu when the program is run.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowWindow();
            }
        });
    }
}
