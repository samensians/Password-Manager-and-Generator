package app;

import java.util.Arrays;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Creates a GUI to manage saved passwords. Saved passwords are hashed and
 * stored in a text file in the same file as the application. Passwords can be added,
 * removed or edited, and must be added with a unique string representing the
 * account the password is associated with. This unique string/account name is what
 * is displayed to the user so they know what passwords they are managing.
 */
public class PasswordManager extends JFrame {
    // Styling constants
    private final int BUTTONS_VGAP = 10;
    private final int BUTTONS_HGAP = 10;

    /**
     * Create a <code>JFrame</code> to add GUI components to.
     *
     * @param windowName a string used to display as the name of the window
     */
    public PasswordManager(String windowName) {
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
        String titleText = "<html><h2>Password Manager</h2></html>";
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel(titleText, JLabel.CENTER));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        GridLayout buttonsPanelLayout = new GridLayout(2, 2);
        buttonsPanelLayout.setVgap(BUTTONS_VGAP);
        buttonsPanelLayout.setHgap(BUTTONS_HGAP);
        buttonsPanel.setLayout(buttonsPanelLayout);

        // Create HashMap of account name and password pairs from hashed.txt file
        HashMap<String, String> accountPasswordPairs = readHashedFile();

        // Create and populate table of account names
        JTable accountTable = new JTable();
        DefaultTableModel accountTableModel = new DefaultTableModel();
        accountTable.setModel(accountTableModel);
        
        accountTableModel.addColumn("Accounts");
        updateAccountTable(accountPasswordPairs, accountTableModel);
        JScrollPane accountTableSP = new JScrollPane(accountTable);
        accountTableSP.setPreferredSize(new Dimension(buttonsPanel.getSize().width, 200));

        // Create buttons to manage saved passwords
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton copyButton = new JButton("Copy");
        JButton toPasswordGeneratorButton = new JButton("Generator");

        // Add action listeners to buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAddAccountOptionPane(accountPasswordPairs, accountTableModel);
                // enter account name and password
                // check for unique account name (check for inclusion in hashmap keys)
            }
        });
        // TODO: add action listeners to buttons

        // Add buttons to the buttons panel
        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(copyButton);
        buttonsPanel.add(toPasswordGeneratorButton);

        // Add account table and buttons panel to the main panel
        mainPanel.add(accountTableSP);
        mainPanel.add(buttonsPanel);

        // Add panels to the pane
        pane.add(titlePanel, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(mainPanel, BorderLayout.SOUTH);
    }

    /**
     * Reads the account names their respective hashed passwords into a hashmap
     * as pairs. If the file "hashed.txt" does not exist, it is created and an
     * empty hashmap is returned.
     * 
     * @return accountPasswordPairs, a HashMap containing account names as keys
     *         and the accounts assoicated password as the value
     */
    private HashMap<String, String> readHashedFile() {
        // Create HashMap of account name and their associated passwords
        HashMap<String, String> accountPasswordPairs = new HashMap<>();

        // Assert that hashed.txt exists and create it if not
        File curDir = new File("." + File.separator + "PasswordManager");
        String[] fileNames = curDir.list();
        boolean hashedFileExists = Arrays.stream(fileNames).anyMatch("hashed.txt"::equals);
        if (!hashedFileExists) {
            File passwordFile = new File("hashed.txt");
            try {
                passwordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return accountPasswordPairs;
        }

        // Read account names and hashed passwords and store in arrays
        // An account name is related to a password by having matching indexes
        try {
            BufferedReader reader = new BufferedReader(new FileReader("." + File.separator + "PasswordManager/hashed.txt"));
            String accountName = reader.readLine();
            while (accountName != null) {
                if (accountName.isEmpty()) {
                    accountName = reader.readLine();
                }
                String hashedPassword = reader.readLine();
                accountPasswordPairs.put(accountName, hashedPassword);
                accountName = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountPasswordPairs;
    }

    /**
     * Updates the account table when an account is added or removed. All rows
     * are removed from the table and then the table is repopulated using the
     * account names in the HashMap. The account names are sorted before they
     * are added to the table.
     * 
     * @param accountPasswordPairs the HashMap created by the <code>readHashedFile</code>
     *                             function containing the current state of the saved account
     *                             names and passwords
     * @param accountTableModel the table model that displays the currently stored
     *                          account names
     */
    private void updateAccountTable(HashMap<String, String> accountPasswordPairs, DefaultTableModel accountTableModel) {
        accountTableModel.setRowCount(0);
        Object[] accountNames = accountPasswordPairs.keySet().toArray();
        Arrays.sort(accountNames);
        for (Object account:accountNames) {
            accountTableModel.addRow(new Object[] {account});
        }
    }

    /**
     * When a password is added or removed, update the hashed.txt file with the account name
     * and password pairs in the HashMap.
     * 
     * @param accountPasswordPairs the HashMap created by the <code>readHashedFile</code>
     *                             function containing the current state of the saved account
     *                             names and passwords
     */
    private void updateHashedFile(HashMap<String, String> accountPasswordPairs) {
        // Write over hashed.txt with the account names and passwords currently in the HashMap
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("." + File.separator + "PasswordManager/hashed.txt", false));
            Object[] accountNames = accountPasswordPairs.keySet().toArray();
            for (int i = 0; i < accountNames.length; i++) {
                writer.write((String) accountNames[i]);
                writer.newLine();
                writer.write((String) accountPasswordPairs.get(accountNames[i]));

                // Avoid writing newlines at the end of the file
                if (!(i == accountNames.length - 1)) {
                    writer.newLine();
                    writer.newLine();
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and shows a JOptionPane that takes an account name and password
     * as inputs which are to be stored by the application. If the account name is
     * unique (is not already stored by the application), it will be stored when the
     * user presses the OK button. If not, a dialog box will appear with a message
     * telling the user that their account name must be unique.
     * 
     * @param accountPasswordPairs the HashMap created by the <code>readHashedFile</code>
     *                             function
     * @param accountTableModel the table model that displays the currently stored
     *                          account names
     */
    private void createAddAccountOptionPane(HashMap<String, String> accountPasswordPairs, DefaultTableModel accountTableModel) {
        // Create the option pane content
        JTextField accountNameTextField = new JTextField(20);
        JTextField passwordTextField = new JPasswordField(20);
        Object[] message = {
            "Account name:", accountNameTextField,
            "Password:", passwordTextField
        };

        // Show the option pane and carry out input validation
        // Only close when the user has entered valid inputs or pressed the cancel button
        while (true) {
            int input = JOptionPane.showConfirmDialog(null, message, "Option", JOptionPane.OK_CANCEL_OPTION);

            // Do nothing if cancel button is pressed
            if (input == JOptionPane.CANCEL_OPTION)
                return;

            // Get the inputs from the text fields
            String inputAccountName = accountNameTextField.getText();
            String inputPassword = passwordTextField.getText();

            // Validate that the user has entered something in both inputs fields
            if (inputAccountName.isBlank() || inputPassword.isBlank()) {
                JOptionPane.showMessageDialog(
                    null, "Account name and password must contain characters", "Invalid input(s)", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Validate that the entered account name is unique
            if (accountPasswordPairs.get(inputAccountName) != null) {
                JOptionPane.showMessageDialog(
                    null, "Account name must be unique", "Invalid account name", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Exit loop once inputs are valid
            break;
        }
        accountPasswordPairs.put(accountNameTextField.getText(), passwordTextField.getText());
        JOptionPane.showMessageDialog(getContentPane(), "Password successfully added", "Password saved", JOptionPane.INFORMATION_MESSAGE);
        updateHashedFile(accountPasswordPairs);
        updateAccountTable(accountPasswordPairs, accountTableModel);
        // TODO: Hash passwords
    }

    /**
     * Creates an instance of the GUI and adds components, then displays the window.
     */
    public void createAndShowWindow() {
        // Create the password manager window
        PasswordManager passwordManager = new PasswordManager("Password Manager");
        // Add the content to the pane
        passwordManager.addComponents(passwordManager.getContentPane());
        // Display the window
        passwordManager.pack();
        passwordManager.setVisible(true);
    }    
}
