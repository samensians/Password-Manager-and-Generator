package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * <code>PasswordGenerator</code> creates a GUI that allows the user
 * to generate a random password based on selected options. The GUI
 * also allows the user to copy the generated password to clipboard,
 * and to switch over to the password manager.
 */
public class PasswordGenerator extends JFrame {
    // Styling constants
    private final int OPTIONS_VGAP = 20;
    private final int OPTIONS_HGAP = 30;
    private final int BUTTONS_VGAP = 10;
    private final int BUTTONS_HGAP = 10;

    // Character sets used in password generation
    private final char[] LOWER = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private final char[] UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final char[] NUMBERS = "0123456789".toCharArray();
    private final char[] SYMBOLS = "^$*.[]{}()?-\"!@#%&/\\,><':;_~".toCharArray();

    /**
     * Create a <code>JFrame</code> to add GUI components to.
     *
     * @param windowName a string used to display as the name of the window
     */
    public PasswordGenerator(String windowName) {
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
        // Create panels and set styling and layouts
        String titleText = "<html><h2>Password Generator</h2></html>";
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel(titleText, JLabel.CENTER));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        GridLayout optionsLayout = new GridLayout(4, 2);
        optionsLayout.setVgap(OPTIONS_VGAP);
        optionsLayout.setHgap(OPTIONS_HGAP);
        optionsPanel.setLayout(optionsLayout);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        GridLayout buttonsPanelLayout = new GridLayout(2, 2);
        buttonsPanelLayout.setVgap(BUTTONS_VGAP);
        buttonsPanelLayout.setHgap(BUTTONS_HGAP);
        buttonsPanel.setLayout(buttonsPanelLayout);

        // Create labels and their associated input components for the options
        JLabel passwordLengthLabel = new JLabel("Password Length");
        JLabel includeUppercaseLabel = new JLabel("Uppercase Letters");
        JLabel includeNumbersLabel = new JLabel("Numbers");
        JLabel includeSymbolsLabel = new JLabel("Symbols");
        
        Integer[] passwordLengthValuesArray = new Integer[96];
        for (int i = 4; i < 100; i++) {
            passwordLengthValuesArray[i - 4] = i;
        }
        JComboBox<Integer> passwordLengthOptions = new JComboBox<>(passwordLengthValuesArray);

        JCheckBox includeUppercaseCheckBox = new JCheckBox("", true);
        includeUppercaseCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        JCheckBox includeNumbersCheckBox = new JCheckBox("", true);
        includeNumbersCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        JCheckBox includeSymbolsCheckBox = new JCheckBox("", true);
        includeSymbolsCheckBox.setHorizontalAlignment(SwingConstants.CENTER);

        // Add options to the options panel
        optionsPanel.add(passwordLengthLabel);
        optionsPanel.add(passwordLengthOptions);
        optionsPanel.add(includeUppercaseLabel);
        optionsPanel.add(includeUppercaseCheckBox);
        optionsPanel.add(includeNumbersLabel);
        optionsPanel.add(includeNumbersCheckBox);
        optionsPanel.add(includeSymbolsLabel);
        optionsPanel.add(includeSymbolsCheckBox);

        // Create components to display and scroll across generated password
        JTextField generatedPasswordTextField = new JTextField();
        generatedPasswordTextField.setEditable(false);
        generatedPasswordTextField.setPreferredSize(new Dimension(150, 25));
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBar.setModel(generatedPasswordTextField.getHorizontalVisibility());
        
        // Create buttons to generate and copy the password
        JButton generateButton = new JButton("Generate");
        JButton copyButton = new JButton("Copy");
        JButton savePasswordButton = new JButton("Save Password");
        JButton toPasswordManagerButton = new JButton("Manager");

        // Add action listeners to buttons
        generateButton.addActionListener(new ActionListener() {
            /**
             * Gets values from option boxes and generates password using
             * <code>generatePassword</code>. The generated password is displayed
             * in the text field.
             * <p>
             * The state of the checkbox options are stored in a HashMap as booleans
             * with strings that denote the option that the boolean relates to. This
             * HashMap is used in the <code>generatePassword</code> function to determine
             * the character set used when generating the password.
             *
             * @param e the event being processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int passwordLength = (int) passwordLengthOptions.getSelectedItem();
                HashMap<String, Boolean> checkBoxOptions = new HashMap<String, Boolean>();
                checkBoxOptions.put("includeUppercase", includeUppercaseCheckBox.isSelected());
                checkBoxOptions.put("includeNumbers", includeNumbersCheckBox.isSelected());
                checkBoxOptions.put("includeSymbols", includeSymbolsCheckBox.isSelected());
                String generatedPassword = generatePassword(passwordLength, checkBoxOptions);
                generatedPasswordTextField.setText(generatedPassword);
            }
        });

        copyButton.addActionListener(new ActionListener() {
            /**
             * Adds the text displayed in the text field to the clipboard.
             *
             * @param e the event being processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit.getDefaultToolkit()
                       .getSystemClipboard()
                       .setContents(new StringSelection(generatedPasswordTextField.getText()), null);
            }
        });

        savePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO:
                // Give optionPane to ask for account name to associate
                // password with, then return user into this window
                System.out.println("Saving password");
            }
        });

        toPasswordManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Going to password manager");
            }
        });

        // Add buttons to the buttons panel
        buttonsPanel.add(generateButton);
        buttonsPanel.add(copyButton);
        buttonsPanel.add(savePasswordButton);
        buttonsPanel.add(toPasswordManagerButton);

        // Add options and buttons panels and generated password textfield to main panel
        mainPanel.add(optionsPanel);
        mainPanel.add(generatedPasswordTextField);
        mainPanel.add(scrollBar);
        mainPanel.add(buttonsPanel);

        // Add panels to the pane
        pane.add(titlePanel, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(mainPanel, BorderLayout.SOUTH);
    }

    /**
     * Generates a new password using the options that are currently set.
     * All generated passwords will have at least one lowercase character.
     * <p>
     * First adds one of each of the required characters to ensure the password
     * requirements are met. The required characters are determined from the
     * options chosen by the user. Random characters are then added from the
     * character pool until the defined length is reached, and the password
     * string is shuffled.
     * 
     * @param passwordLength the chosen password length
     * @param checkBoxOptions a HashMap of option name string keys and the
     *        associated combo box values
     * @return the newly generated password string
     */
    private String generatePassword(Integer passwordLength, HashMap<String, Boolean> checkBoxOptions) {
        // Define variables and add a random lowercase character to the password
        Random rand = new SecureRandom();
        char[] password = new char[passwordLength];
        password[0] = LOWER[rand.nextInt(LOWER.length)];
        int passwordIndex = 1;

        // Create set of characters that can be used in password
        String charSet = String.valueOf(LOWER);

        // Get the option value pairs from the HashMap
        boolean includeUppercase = checkBoxOptions.get("includeUppercase");
        boolean includeNumbers = checkBoxOptions.get("includeNumbers");
        boolean includeSymbols = checkBoxOptions.get("includeSymbols");

        // Include at least one character from required character sets
        // and add required character sets to the global character set
        if (includeUppercase) {
            password[passwordIndex] = UPPER[rand.nextInt(UPPER.length)];
            charSet += String.valueOf(UPPER);
            passwordIndex++;
        }
        if (includeNumbers) {
            password[passwordIndex] = NUMBERS[rand.nextInt(NUMBERS.length)];
            charSet += String.valueOf(NUMBERS);
            passwordIndex++;
        }
        if (includeSymbols) {
            password[passwordIndex] = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            charSet += String.valueOf(SYMBOLS);
            passwordIndex++;
        }

        // Add random characters from entire character set until password length is reached
        char[] CHAR_SET = charSet.toCharArray();
        while (passwordIndex < passwordLength) {
            password[passwordIndex] = CHAR_SET[rand.nextInt(CHAR_SET.length)];
            passwordIndex++;
        }

        // Shuffle the password
        List<Character> shuffleList = new ArrayList<Character>();
        for (char c : password) {
            shuffleList.add(c);
        }
        Collections.shuffle(shuffleList);
        StringBuilder builder = new StringBuilder();
        for (char c : shuffleList) {
            builder.append(c);
        }
        String shuffledPassword = String.valueOf(builder);
        
        return shuffledPassword;
    }

    /**
     * Creates an instance of the GUI and adds components, then displays the window.
     */
    public void createAndShowWindow() {
        // Create generator window
        PasswordGenerator passwordGenerator = new PasswordGenerator("Password Generator");
        // Add the content to the pane
        passwordGenerator.addComponents(passwordGenerator.getContentPane());
        // Display the window
        passwordGenerator.pack();
        passwordGenerator.setVisible(true);
    }
}
