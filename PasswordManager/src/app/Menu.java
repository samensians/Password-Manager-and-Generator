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

public class Menu extends JFrame {
    private final int BUTTON_VGAP = 20;

    public Menu(String windowName) {
        super(windowName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void addComponentsToPane(final Container pane) {
        // Create panels and set layouts
        JPanel titlePanel = new JPanel();
        String titleText = "<html><h2>Password Generator and Manager</h2></html>";
        titlePanel.add(new JLabel(titleText, JLabel.CENTER));

        GridLayout buttonLayout = new GridLayout(2, 1);
        buttonLayout.setVgap(BUTTON_VGAP);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(buttonLayout);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create buttons and add action listeners
        JButton toPasswordGeneratorButton = new JButton("Password Generator");
        toPasswordGeneratorButton.setPreferredSize(new Dimension(150, 50));
        toPasswordGeneratorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new PasswordGenerator("Password Generator").createAndShowGUI();
                    }
                });
                dispose();
            }
        });
        
        JButton toPasswordManagerButton = new JButton("Password Manager");
        toPasswordManagerButton.setPreferredSize(new Dimension(150, 50));
        toPasswordManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // new PasswordManager("Password Manager").createAndShowGUI();
                    }
                });
                dispose();
            }
        });
        
        // Add the buttons to the panel
        buttonPanel.add(toPasswordGeneratorButton);
        buttonPanel.add(toPasswordManagerButton);

        // Add panels to the pane
        pane.add(titlePanel, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void createAndShowGUI() {
        // Create the menu window 
        Menu menu = new Menu("Password Generator and Manager");
        // Add the content to the pane
        menu.addComponentsToPane(menu.getContentPane());
        // Display the window
        menu.pack();
        menu.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
