package app;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.BorderFactory;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Creates a GUI to manage saved passwords. Saved passwords are encrypted and
 * stored in a text file in the same file as the application. Passwords are
 * encrypted using a secret key generated from a static password, meaning that
 * the encrypted passwords are likely not secure. The goal of the encryption is
 * to prevent the passwords from being read by someone looking in the accounts.txt
 * file. Passwords can be added, removed or edited, and must be added with a unique
 * string representing the account the password is associated with. This unique
 * string/account name is what is displayed to the user so they know what passwords
 * they are managing.
 */
public class PasswordManager extends JFrame {
    // Styling constants
    private final int BUTTONS_VGAP = 10;
    private final int BUTTONS_HGAP = 10;

    // Password used in encryption
    private final String CIPHER_PASS = "passwordToTestEncryption";

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

        // Create HashMap of account name and password pairs from accounts.txt file
        HashMap<String, String> accountPasswordPairs = readAccountsFile();

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
            /**
             * Calls <code>createAddAccountOptionPane</code> which creates a window for the user to
             * add a new account and password. This function validates the users input and updates
             * the accounts file and accounts table as needed.
             * 
             * @param e the event being processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                createAddAccountOptionPane(accountPasswordPairs, accountTableModel);
            }
        });

        removeButton.addActionListener(new ActionListener() {
            /**
             * Removes the selected account from the manager. When the user selects an account/row from
             * the table and presses the remove button, the account name is taken and the matching key-value
             * pair is removed from the accountPasswordPairs HashMap. The accounts file is then updated
             * followed by the accounts table being updated. The function returns if a row is not selected.
             * 
             * @param e the event being processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = accountTable.getSelectedRow();
                if (selectedRowIndex == -1)
                    return;
                
                String accountToRemove = accountTable.getValueAt(selectedRowIndex, 0).toString();
                accountPasswordPairs.remove(accountToRemove);
                updateAccountFile(accountPasswordPairs);
                updateAccountTable(accountPasswordPairs, accountTableModel);
            }
        });

        copyButton.addActionListener(new ActionListener() {
            /**
             * When the user selects an account/row from the table and presses the copy button, the account
             * name is used to retrieve the associated value (the password) from the accountPasswordPairs
             * HashMap. The password is passed through the decryption method to retrieve the plaintext
             * version of the password which is then copied to the clipboard.
             * 
             * @param e the event being processed
             */
            @Override
            public void actionPerformed(ActionEvent event) {
                int selectedRowIndex = accountTable.getSelectedRow();
                if (selectedRowIndex == -1)
                    return;

                String selectedAccountName = accountTable.getValueAt(selectedRowIndex, 0).toString();
                try {
                    Toolkit.getDefaultToolkit()
                           .getSystemClipboard()
                           .setContents(new StringSelection(
                               decryptPassword(accountPasswordPairs.get(selectedAccountName), getSecretKey())), null);
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(null, createErrorTextArea(err, "Decryption failed, password not copied."),
                        "Password decryption failure", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        toPasswordGeneratorButton.addActionListener(new ActionListener() {
            /**
             * Creates a <code>PasswordGenerator</code> instance and creates
             * and shows the password manager GUI. Then closes/disposes the
             * password generator window.
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
     * Generates a secret key from the password that the user sets when they first launch the
     * application. The secret key is used to encrypt the passwords before they are written
     * to the accounts file.
     *
     * @return the secret key generated from the cipher password
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    SecretKey getSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        String salt = "testSalt";
        KeySpec secretSpec = new PBEKeySpec(CIPHER_PASS.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(secretSpec).getEncoded(), "AES");
        return secretKey;
    }

    /**
     * Encrypts a password/string using the AES algorithm.
     *
     * @param password the password/string to be encrypted
     * @param secretKey the secret key to be used in encryption
     * @return the encrypted password as a string
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    String encryptPassword(String password, SecretKey secretKey)
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
        IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes()));
    }

    /**
     * Decrypts an encrypted password into plaintext. The password was previously encrypted using the
     * <code>encryptPassword</code> method.
     *
     * @param encryptedPassword the password encrypted using <code>encryptPassword</code>
     * @param secretKey the secret key that was used in encryption
     * @return the decrypted password
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private String decryptPassword(String encryptedPassword, SecretKey secretKey)
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
        IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedPassword)));
    }

    /**
     * Reads the account names their respective accounts passwords into a hashmap
     * as pairs. If the file "accounts.txt" does not exist, it is created and an
     * empty hashmap is returned.
     * 
     * @return accountPasswordPairs, a HashMap containing account names as keys
     *         and the accounts assoicated password as the value
     */
    HashMap<String, String> readAccountsFile() {
        // Create HashMap of account name and their associated passwords
        HashMap<String, String> accountPasswordPairs = new HashMap<>();

        // Assert that accounts.txt exists and create it if not
        File curDir = new File("." + File.separator + "PasswordManager");
        String[] fileNames = curDir.list();
        boolean accountsFileExists = Arrays.stream(fileNames).anyMatch("accounts.txt"::equals);
        if (!accountsFileExists) {
            File passwordFile = new File("." + File.separator + "PasswordManager" + File.separator + "accounts.txt");
            try {
                passwordFile.createNewFile();
            } catch (IOException err) {
                JOptionPane.showMessageDialog(null, createErrorTextArea(err, "File accounts.txt creation failed, closing program."),
                    "Password file creation failure", JOptionPane.ERROR_MESSAGE);
                
                // Wait for the window creation to finish before disposing it
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dispose();
                    }
                });
            }
            return accountPasswordPairs;
        }

        // Read account names and accounts passwords and store in arrays
        // An account name is related to a password by having matching indexes
        try {
            BufferedReader reader = new BufferedReader(new FileReader("." + File.separator + "PasswordManager/accounts.txt"));
            String accountName = reader.readLine();
            while (accountName != null) {
                if (accountName.isEmpty()) {
                    accountName = reader.readLine();
                }
                String accountsPassword = reader.readLine();
                accountPasswordPairs.put(accountName, accountsPassword);
                accountName = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException err) {
            JOptionPane.showMessageDialog(null, createErrorTextArea(err, "Error locating accounts.txt. File should exist, closing program."),
                    "File accounts.txt not found", JOptionPane.ERROR_MESSAGE);
                            
            // Wait for the window creation to finish before disposing it
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dispose();
                }
            });
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, createErrorTextArea(err, "Error accessing accounts.txt, closing program."),
                    "File accounts.txt unaccessible", JOptionPane.ERROR_MESSAGE);
                            
            // Wait for the window creation to finish before disposing it
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dispose();
                }
            });
        }
        return accountPasswordPairs;
    }

    /**
     * Updates the account table when an account is added or removed. All rows
     * are removed from the table and then the table is repopulated using the
     * account names in the HashMap. The account names are sorted before they
     * are added to the table.
     * 
     * @param accountPasswordPairs the HashMap created by the <code>readAccountsFile</code>
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
     * When a password is added or removed, update the accounts.txt file with the account name
     * and password pairs in the HashMap.
     * 
     * @param accountPasswordPairs the HashMap created by the <code>readAccountsFile</code>
     *                             function containing the current state of the saved account
     *                             names and passwords
     */
    void updateAccountFile(HashMap<String, String> accountPasswordPairs) {
        // Write over accounts.txt with the account names and passwords currently in the HashMap
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("." + File.separator + "PasswordManager/accounts.txt", false));
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
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, createErrorTextArea(err, "Error writing to accounts.txt, closing program."),
                    "Writing to accounts.txt error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    /**
     * Creates and shows a JOptionPane that takes an account name and password
     * as inputs which are to be stored by the application. If the account name is
     * unique (is not already stored by the application), it will be stored when the
     * user presses the OK button. If not, a dialog box will appear with a message
     * telling the user that their account name must be unique.
     * 
     * @param accountPasswordPairs the HashMap created by the <code>readAccountsFile</code>
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
            int input = JOptionPane.showConfirmDialog(null, message, "Add account", JOptionPane.OK_CANCEL_OPTION);

            // Do nothing if cancel button is pressed
            if (input == JOptionPane.CANCEL_OPTION || input == JOptionPane.CLOSED_OPTION)
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

        try {
            accountPasswordPairs.put(accountNameTextField.getText(), encryptPassword(passwordTextField.getText(), getSecretKey()));
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, createErrorTextArea(err, "Encryption failed, password not saved."),
                "Password save failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        updateAccountFile(accountPasswordPairs);
        JOptionPane.showMessageDialog(getContentPane(), "Password successfully added", "Password saved", JOptionPane.INFORMATION_MESSAGE);
        updateAccountTable(accountPasswordPairs, accountTableModel);
    }

    /**
     * Converts the backtrace of an exception to a string, which is then placed into a <code>JTextArea</code>
     * so that the user can be displayed the error as well as copy the error backtrace.
     * 
     * @param e the caught exception
     * @param message a string to display to the user telling them about the error
     * @return a <code>JTextArea</code> with the error information
     */
    JTextArea createErrorTextArea(Exception e, String message) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));

        JTextArea errorTextArea = new JTextArea();
        errorTextArea.setText(message + " Backtrace for developers:\n\n" + stringWriter.toString());
        errorTextArea.setBorder(BorderFactory.createEtchedBorder());
        errorTextArea.setEditable(false);

        return errorTextArea;
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
