package main;

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

public class Main extends JFrame {
    final static int BUTTON_VGAP = 20;

    public Main(String windowName) {
        super(windowName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void addComponentsToPane(final Container pane) {
        // Create panels and set layouts
        GridLayout buttonLayout = new GridLayout(2, 1);
        buttonLayout.setVgap(BUTTON_VGAP);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(buttonLayout);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel titlePanel = new JPanel();
        String titleText = "<html><h2>Password Generator and Manager</h2></html>";
        titlePanel.add(new JLabel(titleText, JLabel.CENTER));

        // Create buttons and add action listeners
        JButton toPasswordGeneratorButton = new JButton("Password Generator");
        toPasswordGeneratorButton.setPreferredSize(new Dimension(150, 50));
        toPasswordGeneratorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton pressed = (JButton) (e.getSource());
                System.out.println("Pressed " + pressed.getText());
            }
        });
        
        JButton toPasswordManagerButton = new JButton("Password Manager");
        toPasswordManagerButton.setPreferredSize(new Dimension(150, 50));
        toPasswordManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton pressed = (JButton) (e.getSource());
                System.out.println("Pressed " + pressed.getText());
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

    private static void createAndShowMenu() {
        // Create the menu window 
        Main menu = new Main("Password Generator and Manager");
        // Add the content to the pane
        menu.addComponentsToPane(menu.getContentPane());
        // Display the window
        menu.pack();
        menu.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowMenu();
            }
        });
    }
}
