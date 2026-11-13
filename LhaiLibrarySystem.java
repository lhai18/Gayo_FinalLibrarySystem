
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class LhaiLibrarySystem extends JFrame {

    // =================================
    // COLORS
    // =================================

    Color navy = new Color(20, 43, 72);
    Color blue = new Color(39, 103, 170);
    Color lightBlue = new Color(231, 240, 250);
    Color background = new Color(244, 247, 251);
    Color white = Color.WHITE;
    Color darkText = new Color(35, 48, 65);
    Color grayText = new Color(115, 128, 145);
    Color green = new Color(39, 150, 105);
    Color red = new Color(205, 75, 75);

    // =================================
    // BOOK CLASS
    // =================================

    static class Book {
        int id;
        String title;
        String author;
        String category;
        String borrowedBy;

        Book(int id, String title, String author,
             String category) {

            this.id = id;
            this.title = title;
            this.author = author;
            this.category = category;
            this.borrowedBy = "";
        }
    }

    // =================================
    // STUDENT CLASS
    // =================================

    static class Student {
        String id;
        String name;
        String username;
        String salt;
        String password;

        Student(String id, String name, String username,
                String salt, String password) {

            this.id = id;
            this.name = name;
            this.username = username;
            this.salt = salt;
            this.password = password;
        }
    }

    // =================================
    // DATA
    // =================================

    ArrayList<Book> books = new ArrayList<>();
    ArrayList<Student> students = new ArrayList<>();

    Student currentStudent;

    // =================================
    // MAIN PANELS
    // =================================

    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    CardLayout pageLayout = new CardLayout();
    JPanel pagePanel = new JPanel(pageLayout);

    // =================================
    // LOGIN COMPONENTS
    // =================================

    JTextField username;
    JPasswordField password;

    // =================================
    // REGISTRATION COMPONENTS
    // =================================

    JTextField studentId;
    JTextField fullName;
    JTextField newUsername;

    JPasswordField newPassword;
    JPasswordField confirmPassword;

    // =================================
    // DASHBOARD COMPONENTS
    // =================================

    JLabel totalBooksLabel;
    JLabel availableBooksLabel;
    JLabel borrowedBooksLabel;
    JLabel pageTitle;

    JTable bookTable;
    DefaultTableModel bookModel;

    JTextField searchField;

    // =================================
    // CONSTRUCTOR
    // =================================

    public LhaiLibrarySystem() {

        setTitle("LHAI LIBRARY SYSTEM");

        setSize(1100, 700);

        setMinimumSize(new Dimension(900, 600));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        loadStudents();
        loadBooks();

        if (books.isEmpty()) {

            books.add(new Book(
                1,
                "Introduction to Programming",
                "John Smith",
                "Technology"
            ));

            books.add(new Book(
                2,
                "English Literature",
                "William Shakespeare",
                "Literature"
            ));

            books.add(new Book(
                3,
                "Basic Mathematics",
                "Robert Johnson",
                "Mathematics"
            ));

            books.add(new Book(
                4,
                "The Science Book",
                "DK Publishing",
                "Science"
            ));

            books.add(new Book(
                5,
                "World History",
                "Philip Parker",
                "History"
            ));

            saveBooks();
        }

        mainPanel.add(loginPanel(), "Login");

        mainPanel.add(registerPanel(), "Register");

        add(mainPanel);

        cardLayout.show(mainPanel, "Login");

        setVisible(true);
    }

    // =================================
    // MAIN METHOD
    // =================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LhaiLibrarySystem();
        });
    }

    // =================================
    // DESIGN HELPERS
    // =================================

    JLabel createLabel(String text, int size, Color color) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, size));
        label.setForeground(color);

        return label;
    }

    JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(white);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    void styleTextField(JTextField field) {

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(darkText);
        field.setBackground(white);

        field.setBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 220, 232), 1, true),
                new EmptyBorder(10, 12, 10, 12)
            )
        );
    }

    // =================================
    // PASSWORD EYE BUTTON
    // =================================

    JPanel passwordFieldWithEye(JPasswordField field) {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(white);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(10, 12, 10, 12));

        JButton eye = new JButton("Show");

        eye.setForeground(blue);
        eye.setBackground(white);
        eye.setFocusPainted(false);
        eye.setBorder(new EmptyBorder(5, 10, 5, 10));

        char defaultEcho = field.getEchoChar();

        eye.addActionListener(e -> {

            if (field.getEchoChar() == 0) {
                field.setEchoChar(defaultEcho);
                eye.setText("Show");
            } else {
                field.setEchoChar((char) 0);
                eye.setText("Hide");
            }
        });

        panel.setBorder(
            new LineBorder(new Color(210, 220, 232), 1, true)
        );

        panel.add(field, BorderLayout.CENTER);
        panel.add(eye, BorderLayout.EAST);

        return panel;
    }

    // =================================
    // LOGIN PAGE
    // =================================

    JPanel loginPanel() {

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(background);

        JPanel container = new JPanel(new GridLayout(1, 2));

        container.setPreferredSize(new Dimension(850, 480));

        // LEFT SIDE

        JPanel left = new JPanel();

        left.setLayout(
            new BoxLayout(left, BoxLayout.Y_AXIS)
        );

        left.setBackground(navy);

        left.setBorder(
            new EmptyBorder(70, 40, 50, 40)
        );

        JLabel logo = createLabel(
            "LHAI", 38, white
        );

        JLabel system = createLabel(
            "LIBRARY SYSTEM", 23, white
        );

        JLabel description = createLabel(
            "<html>Discover new ideas,<br>" +
            "explore knowledge, and<br>" +
            "make learning meaningful.</html>",
            18, white
        );

        left.add(Box.createVerticalGlue());
        left.add(logo);
        left.add(Box.createVerticalStrut(5));
        left.add(system);
        left.add(Box.createVerticalStrut(35));
        left.add(description);
        left.add(Box.createVerticalGlue());

        // RIGHT SIDE

        JPanel right = new JPanel();

        right.setLayout(
            new BoxLayout(right, BoxLayout.Y_AXIS)
        );

        right.setBackground(white);

        right.setBorder(
            new EmptyBorder(55, 40, 45, 40)
        );

        JLabel title = createLabel(
            "Welcome Back!", 28, darkText
        );

        JLabel subtitle = createLabel(
            "Sign in to access your library account.",
            13, grayText
        );

        username = new JTextField();
        password = new JPasswordField();

        styleTextField(username);

        JButton login = createButton("SIGN IN", blue);

        JButton register = new JButton(
            "Create a Student Account"
        );

        register.setForeground(blue);
        register.setBackground(white);
        register.setFocusPainted(false);
        register.setBorderPainted(false);

        right.add(title);
        right.add(Box.createVerticalStrut(8));
        right.add(subtitle);

        right.add(Box.createVerticalStrut(30));

        right.add(createLabel("Username", 13, darkText));

        right.add(Box.createVerticalStrut(7));

        username.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        right.add(username);

        right.add(Box.createVerticalStrut(18));

        right.add(createLabel("Password", 13, darkText));

        right.add(Box.createVerticalStrut(7));

        JPanel passPanel = passwordFieldWithEye(password);

        passPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        right.add(passPanel);

        right.add(Box.createVerticalStrut(25));

        login.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 48)
        );

        right.add(login);

        right.add(Box.createVerticalStrut(12));

        register.setAlignmentX(Component.CENTER_ALIGNMENT);

        right.add(register);

        right.add(Box.createVerticalGlue());

        JLabel footer = createLabel(
            "LHAI LIBRARY SYSTEM",
            11, grayText
        );

        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        right.add(footer);

        container.add(left);
        container.add(right);

        outer.add(container);

        login.addActionListener(e -> signIn());

        password.addActionListener(e -> signIn());

        register.addActionListener(e ->
            cardLayout.show(mainPanel, "Register")
        );

        return outer;
    }

    // =================================
    // REGISTRATION PAGE
    // =================================

    JPanel registerPanel() {

        JPanel outer = new JPanel(new GridBagLayout());

        outer.setBackground(background);

        JPanel form = new JPanel();

        form.setLayout(
            new BoxLayout(form, BoxLayout.Y_AXIS)
        );

        form.setBackground(white);

        form.setBorder(
            new EmptyBorder(25, 40, 25, 40)
        );

        form.setPreferredSize(
            new Dimension(450, 590)
        );

        JLabel title = createLabel(
            "Create Your Account", 25, darkText
        );

        JLabel subtitle = createLabel(
            "Register as a library student.",
            13, grayText
        );

        studentId = new JTextField();
        fullName = new JTextField();
        newUsername = new JTextField();

        newPassword = new JPasswordField();
        confirmPassword = new JPasswordField();

        styleTextField(studentId);
        styleTextField(fullName);
        styleTextField(newUsername);

        form.add(title);
        form.add(Box.createVerticalStrut(5));
        form.add(subtitle);

        form.add(Box.createVerticalStrut(18));

        addRegisterField(form, "Student ID", studentId);

        addRegisterField(form, "Full Name", fullName);

        addRegisterField(form, "Username", newUsername);

        addRegisterField(
            form, "Password",
            passwordFieldWithEye(newPassword)
        );

        addRegisterField(
            form, "Confirm Password",
            passwordFieldWithEye(confirmPassword)
        );

        JButton create = createButton(
            "CREATE ACCOUNT", blue
        );

        create.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        JButton back = new JButton("Back to Login");

        back.setForeground(blue);
        back.setBackground(white);
        back.setFocusPainted(false);
        back.setBorderPainted(false);

        back.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(Box.createVerticalStrut(15));
        form.add(create);
        form.add(Box.createVerticalStrut(10));
        form.add(back);

        outer.add(form);

        create.addActionListener(e -> createAccount());

        back.addActionListener(e ->
            cardLayout.show(mainPanel, "Login")
        );

        return outer;
    }

    void addRegisterField(
        JPanel panel, String label, JComponent field
    ) {

        panel.add(createLabel(label, 13, darkText));

        panel.add(Box.createVerticalStrut(5));

        field.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );

        panel.add(field);

        panel.add(Box.createVerticalStrut(10));
    }

    // =================================
    // PASSWORD HASHING
    // =================================

    String hash(String pass, String salt) throws Exception {

        PBEKeySpec spec = new PBEKeySpec(
            pass.toCharArray(),
            Base64.getDecoder().decode(salt),
            120000,
            256
        );

        try {

            return Base64.getEncoder().encodeToString(
                SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded()
            );

        } finally {
            spec.clearPassword();
        }
    }

    String generateSalt() {

        byte[] bytes = new byte[16];

        new SecureRandom().nextBytes(bytes);

        return Base64.getEncoder().encodeToString(bytes);
    }

    // =================================
    // CREATE ACCOUNT
    // =================================

    void createAccount() {

        String id = studentId.getText().trim();
        String name = fullName.getText().trim();
        String user = newUsername.getText().trim();

        String pass = new String(newPassword.getPassword());

        String confirm = new String(
            confirmPassword.getPassword()
        );

        if (id.isEmpty() || name.isEmpty() ||
            user.isEmpty() || pass.isEmpty() ||
            confirm.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Please complete all fields."
            );

            return;
        }

        if (!pass.equals(confirm)) {

            JOptionPane.showMessageDialog(
                this, "Passwords do not match!"
            );

            return;
        }

        if (pass.length() < 8) {

            JOptionPane.showMessageDialog(
                this, "Password must have at least 8 characters."
            );

            return;
        }

        for (Student s : students) {

            if (s.id.equalsIgnoreCase(id) ||
                s.username.equalsIgnoreCase(user)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Student ID or username already exists!"
                );

                return;
            }
        }

        try {

            String salt = generateSalt();

            Student student = new Student(
                id, name, user, salt, hash(pass, salt)
            );

            students.add(student);

            saveStudents();

            JOptionPane.showMessageDialog(
                this, "Account created successfully!"
            );

            studentId.setText("");
            fullName.setText("");
            newUsername.setText("");
            newPassword.setText("");
            confirmPassword.setText("");

            cardLayout.show(mainPanel, "Login");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this, "Registration error: " + e.getMessage()
            );
        }
    }

    // =================================
    // SIGN IN
    // =================================

    void signIn() {

        String user = username.getText().trim();

        String pass = new String(password.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Enter your username and password."
            );

            return;
        }

        for (Student s : students) {

            try {

                if (s.username.equalsIgnoreCase(user) &&
                    MessageDigest.isEqual(
                        Base64.getDecoder().decode(
                            hash(pass, s.salt)
                        ),
                        Base64.getDecoder().decode(s.password)
                    )) {

                    currentStudent = s;

                    buildDashboard();

                    cardLayout.show(mainPanel, "Dashboard");

                    return;
                }

            } catch (Exception e) {
                // Continue checking accounts.
            }
        }

        JOptionPane.showMessageDialog(
            this, "Invalid username or password!"
        );
    }

    // =================================
    // DASHBOARD
    // =================================

    JPanel dashboardScreen;

    DefaultTableModel myBooksModel;

    void buildDashboard() {

        if (dashboardScreen != null) {
            mainPanel.remove(dashboardScreen);
        }

        dashboardScreen = new JPanel(new BorderLayout());

        dashboardScreen.setBackground(background);

        // SIDEBAR

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(new Dimension(230, 0));

        sidebar.setBackground(navy);

        sidebar.setLayout(
            new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );

        sidebar.setBorder(
            new EmptyBorder(25, 15, 20, 15)
        );

        JLabel logo = createLabel(
            "LHAI LIBRARY", 22, white
        );

        JLabel subtitle = createLabel(
            "STUDENT PORTAL", 11,
            new Color(180, 200, 225)
        );

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(40));

        addNavigationButton(sidebar, "Dashboard", "Dashboard");
        addNavigationButton(sidebar, "Book Catalog", "Catalog");
        addNavigationButton(sidebar, "My Borrowed Books", "MyBooks");
        addNavigationButton(sidebar, "Add New Book", "AddBook");
        addNavigationButton(sidebar, "My Profile", "Profile");

        sidebar.add(Box.createVerticalGlue());

        JButton logout = createButton("LOG OUT", red);

        logout.setAlignmentX(Component.LEFT_ALIGNMENT);

        logout.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        sidebar.add(logout);

        logout.addActionListener(e -> logout());

        // RIGHT CONTENT

        JPanel content = new JPanel(new BorderLayout());

        content.setBackground(background);

        content.setBorder(
            new EmptyBorder(20, 25, 20, 25)
        );

        // HEADER

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(white);

        header.setBorder(
            new EmptyBorder(15, 20, 15, 20)
        );

        pageTitle = createLabel(
            "Dashboard", 22, darkText
        );

        JLabel studentName = createLabel(
            "Student: " + currentStudent.name,
            13, blue
        );

        header.add(pageTitle, BorderLayout.WEST);

        header.add(studentName, BorderLayout.EAST);

        content.add(header, BorderLayout.NORTH);

        // PAGES

        pagePanel = new JPanel(pageLayout);

        pagePanel.setBackground(background);

        pagePanel.add(createDashboardPage(), "Dashboard");

        pagePanel.add(createCatalogPage(), "Catalog");

        pagePanel.add(createMyBooksPage(), "MyBooks");

        pagePanel.add(createAddBookPage(), "AddBook");

        pagePanel.add(createProfilePage(), "Profile");

        content.add(pagePanel, BorderLayout.CENTER);

        dashboardScreen.add(sidebar, BorderLayout.WEST);

        dashboardScreen.add(content, BorderLayout.CENTER);

        mainPanel.add(dashboardScreen, "Dashboard");

        refreshDashboard();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // =================================
    // SIDEBAR NAVIGATION
    // =================================

    void addNavigationButton(
        JPanel sidebar, String text, String page
    ) {

        JButton button = new JButton(text);

        button.setFont(
            new Font("Segoe UI", Font.PLAIN, 14)
        );

        button.setForeground(white);
        button.setBackground(navy);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);

        button.setBorder(
            new EmptyBorder(13, 12, 13, 8)
        );

        button.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 48)
        );

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        button.addActionListener(e -> {

            pageLayout.show(pagePanel, page);

            if (page.equals("Dashboard")) {
                pageTitle.setText("Dashboard");
                refreshDashboard();
            }

            else if (page.equals("Catalog")) {
                pageTitle.setText("Book Catalog");
                refreshCatalog();
            }

            else if (page.equals("MyBooks")) {
                pageTitle.setText("My Borrowed Books");
                refreshMyBooks();
            }

            else if (page.equals("AddBook")) {
                pageTitle.setText("Add New Book");
            }

            else if (page.equals("Profile")) {
                pageTitle.setText("My Profile");
            }
        });

        sidebar.add(button);

        sidebar.add(Box.createVerticalStrut(8));
    }

    // =================================
    // DASHBOARD PAGE
    // =================================

    JPanel createDashboardPage() {

        JPanel panel = new JPanel(new BorderLayout(15, 20));

        panel.setBackground(background);

        panel.setBorder(
            new EmptyBorder(25, 5, 5, 5)
        );

        JPanel top = new JPanel();

        top.setLayout(
            new BoxLayout(top, BoxLayout.Y_AXIS)
        );

        top.setBackground(background);

        top.add(createLabel(
            "Welcome to LHAI Library System!",
            24, darkText
        ));

        top.add(Box.createVerticalStrut(8));

        top.add(createLabel(
            "Explore books and manage your borrowing activities.",
            13, grayText
        ));

        // STATISTICS

        JPanel statistics = new JPanel(
            new GridLayout(1, 3, 15, 15)
        );

        statistics.setBackground(background);

        totalBooksLabel = createLabel(
            "0", 30, blue
        );

        availableBooksLabel = createLabel(
            "0", 30, green
        );

        borrowedBooksLabel = createLabel(
            "0", 30, red
        );

        statistics.add(createStatCard(
            "Total Books", totalBooksLabel
        ));

        statistics.add(createStatCard(
            "Available Books", availableBooksLabel
        ));

        statistics.add(createStatCard(
            "My Borrowed Books", borrowedBooksLabel
        ));

        // QUICK ACCESS

        JPanel quick = new JPanel(
            new GridLayout(1, 3, 15, 15)
        );

        quick.setBackground(background);

        quick.add(createQuickCard(
            "Book Catalog",
            "Explore the available books.",
            "Browse Books",
            "Catalog"
        ));

        quick.add(createQuickCard(
            "My Borrowed Books",
            "View the books you borrowed.",
            "View Books",
            "MyBooks"
        ));

        quick.add(createQuickCard(
            "Add New Book",
            "Add a new book to the library.",
            "Add Book",
            "AddBook"
        ));

        JPanel center = new JPanel(new BorderLayout(15, 20));

        center.setBackground(background);

        center.add(statistics, BorderLayout.NORTH);

        center.add(quick, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);

        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    // =================================
    // STATISTICS CARD
    // =================================

    JPanel createStatCard(String title, JLabel number) {

        JPanel card = new JPanel();

        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBackground(white);

        card.setBorder(
            new CompoundBorder(
                new LineBorder(
                    new Color(225, 231, 239), 1, true
                ),
                new EmptyBorder(18, 18, 18, 18)
            )
        );

        card.add(createLabel(title, 14, grayText));

        card.add(Box.createVerticalStrut(12));

        card.add(number);

        return card;
    }

    // =================================
    // QUICK ACCESS CARD
    // =================================

    JPanel createQuickCard(
        String title,
        String description,
        String buttonText,
        String page
    ) {

        JPanel card = new JPanel();

        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBackground(white);

        card.setBorder(
            new CompoundBorder(
                new LineBorder(
                    new Color(225, 231, 239), 1, true
                ),
                new EmptyBorder(18, 15, 18, 15)
            )
        );

        card.add(createLabel(title, 17, darkText));

        card.add(Box.createVerticalStrut(12));

        card.add(createLabel(description, 12, grayText));

        card.add(Box.createVerticalGlue());

        JButton button = createButton(buttonText, blue);

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(button);

        button.addActionListener(e -> {

            pageLayout.show(pagePanel, page);

            pageTitle.setText(title);

            if (page.equals("Catalog")) {
                refreshCatalog();
            }

            if (page.equals("MyBooks")) {
                refreshMyBooks();
            }
        });

        return card;
    }

    // =================================
    // UPDATE DASHBOARD NUMBERS
    // =================================

    void refreshDashboard() {

        if (totalBooksLabel == null) return;

        int available = 0;
        int mine = 0;

        for (Book b : books) {

            if (b.borrowedBy.isEmpty()) {
                available++;
            }

            if (currentStudent != null &&
                b.borrowedBy.equals(currentStudent.id)) {
                mine++;
            }
        }

        totalBooksLabel.setText(
            String.valueOf(books.size())
        );

        availableBooksLabel.setText(
            String.valueOf(available)
        );

        borrowedBooksLabel.setText(
            String.valueOf(mine)
        );
    }

    // =================================
    // LOGOUT
    // =================================

    void logout() {

        int answer = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {

            currentStudent = null;

            username.setText("");
            password.setText("");

            cardLayout.show(mainPanel, "Login");
        }
    }

    // =================================
    // BOOK CATALOG PAGE
    // =================================

    JPanel createCatalogPage() {

        JPanel panel = new JPanel(new BorderLayout(10, 15));

        panel.setBackground(background);

        panel.setBorder(
            new EmptyBorder(20, 5, 5, 5)
        );

        JPanel top = new JPanel(new BorderLayout(10, 10));

        top.setBackground(background);

        JLabel heading = createLabel(
            "Explore Our Collection", 21, darkText
        );

        searchField = new JTextField();

        styleTextField(searchField);

        searchField.setPreferredSize(
            new Dimension(230, 40)
        );

        JButton search = createButton("SEARCH", blue);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));

        searchPanel.setBackground(background);

        searchPanel.add(searchField, BorderLayout.CENTER);

        searchPanel.add(search, BorderLayout.EAST);

        top.add(heading, BorderLayout.WEST);

        top.add(searchPanel, BorderLayout.EAST);

        String[] columns = {
            "Book ID",
            "Title",
            "Author",
            "Category",
            "Status"
        };

        bookModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(bookModel);

        styleTable(bookTable);

        JScrollPane scroll = new JScrollPane(bookTable);

        scroll.setBorder(
            new LineBorder(new Color(225, 231, 239))
        );

        JButton borrow = createButton(
            "BORROW SELECTED BOOK", green
        );

        JPanel bottom = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        bottom.setBackground(background);

        bottom.add(borrow);

        search.addActionListener(e -> refreshCatalog());

        searchField.addActionListener(e -> refreshCatalog());

        borrow.addActionListener(e -> borrowSelectedBook());

        panel.add(top, BorderLayout.NORTH);

        panel.add(scroll, BorderLayout.CENTER);

        panel.add(bottom, BorderLayout.SOUTH);

        refreshCatalog();

        return panel;
    }

    // =================================
    // TABLE DESIGN
    // =================================

    void styleTable(JTable table) {

        table.setFont(
            new Font("Segoe UI", Font.PLAIN, 13)
        );

        table.setRowHeight(38);

        table.setSelectionBackground(lightBlue);

        table.setSelectionForeground(darkText);

        table.setGridColor(
            new Color(235, 238, 243)
        );

        table.setShowVerticalLines(false);

        table.setBackground(white);

        JTableHeader header = table.getTableHeader();

        header.setBackground(navy);

        header.setForeground(white);

        header.setFont(
            new Font("Segoe UI", Font.BOLD, 13)
        );

        header.setPreferredSize(
            new Dimension(100, 42)
        );
    }

    // =================================
    // REFRESH CATALOG
    // =================================

    void refreshCatalog() {

        if (bookModel == null) return;

        bookModel.setRowCount(0);

        String search = "";

        if (searchField != null) {
            search = searchField.getText()
                .trim().toLowerCase();
        }

        for (Book b : books) {

            String status = b.borrowedBy.isEmpty()
                ? "Available" : "Borrowed";

            String details = (
                b.id + " " +
                b.title + " " +
                b.author + " " +
                b.category
            ).toLowerCase();

            if (details.contains(search)) {

                bookModel.addRow(new Object[] {
                    b.id,
                    b.title,
                    b.author,
                    b.category,
                    status
                });
            }
        }
    }

    // =================================
    // BORROW BOOK
    // =================================

    void borrowSelectedBook() {

        int row = bookTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                this, "Please select a book first."
            );

            return;
        }

        int id = Integer.parseInt(
            bookModel.getValueAt(row, 0).toString()
        );

        Book book = findBook(id);

        if (book == null) return;

        if (!book.borrowedBy.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "This book is already borrowed."
            );

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Do you want to borrow " + book.title + "?",
            "Confirm Borrowing",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            book.borrowedBy = currentStudent.id;

            saveBooks();

            refreshCatalog();
            refreshMyBooks();
            refreshDashboard();

            JOptionPane.showMessageDialog(
                this, "Book borrowed successfully!"
            );
        }
    }

    // =================================
    // MY BORROWED BOOKS PAGE
    // =================================

    JPanel createMyBooksPage() {

        JPanel panel = new JPanel(new BorderLayout(10, 15));

        panel.setBackground(background);

        panel.setBorder(
            new EmptyBorder(20, 5, 5, 5)
        );

        JLabel heading = createLabel(
            "Books Borrowed by Me", 21, darkText
        );

        String[] columns = {
            "Book ID",
            "Title",
            "Author",
            "Category"
        };

        myBooksModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(myBooksModel);

        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);

        JButton returnButton = createButton(
            "RETURN SELECTED BOOK", red
        );

        JPanel bottom = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        bottom.setBackground(background);

        bottom.add(returnButton);

        returnButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                    this, "Please select a book to return."
                );

                return;
            }

            int id = Integer.parseInt(
                myBooksModel.getValueAt(row, 0).toString()
            );

            Book book = findBook(id);

            if (book == null) return;

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Return " + book.title + "?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                book.borrowedBy = "";

                saveBooks();

                refreshMyBooks();
                refreshCatalog();
                refreshDashboard();

                JOptionPane.showMessageDialog(
                    this, "Book returned successfully!"
                );
            }
        });

        panel.add(heading, BorderLayout.NORTH);

        panel.add(scroll, BorderLayout.CENTER);

        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    // =================================
    // REFRESH MY BORROWED BOOKS
    // =================================

    void refreshMyBooks() {

        if (myBooksModel == null ||
            currentStudent == null) {
            return;
        }

        myBooksModel.setRowCount(0);

        for (Book b : books) {

            if (b.borrowedBy.equals(currentStudent.id)) {

                myBooksModel.addRow(new Object[] {
                    b.id,
                    b.title,
                    b.author,
                    b.category
                });
            }
        }
    }

    // =================================
    // ADD BOOK PAGE
    // =================================

    JPanel createAddBookPage() {

        JPanel outer = new JPanel(new GridBagLayout());

        outer.setBackground(background);

        JPanel form = new JPanel();

        form.setLayout(
            new BoxLayout(form, BoxLayout.Y_AXIS)
        );

        form.setBackground(white);

        form.setBorder(
            new EmptyBorder(30, 35, 30, 35)
        );

        form.setPreferredSize(
            new Dimension(450, 430)
        );

        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField categoryField = new JTextField();

        styleTextField(idField);
        styleTextField(titleField);
        styleTextField(authorField);
        styleTextField(categoryField);

        form.add(createLabel(
            "Add a New Book", 24, darkText
        ));

        form.add(Box.createVerticalStrut(8));

        form.add(createLabel(
            "Enter the information of the new book.",
            13, grayText
        ));

        form.add(Box.createVerticalStrut(20));

        addRegisterField(form, "Book ID", idField);

        addRegisterField(form, "Book Title", titleField);

        addRegisterField(form, "Author", authorField);

        addRegisterField(form, "Category", categoryField);

        JButton addButton = createButton(
            "ADD BOOK", blue
        );

        addButton.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        form.add(Box.createVerticalStrut(10));

        form.add(addButton);

        addButton.addActionListener(e -> {

            String idText = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String category = categoryField.getText().trim();

            if (idText.isEmpty() || title.isEmpty() ||
                author.isEmpty() || category.isEmpty()) {

                JOptionPane.showMessageDialog(
                    this, "Please complete all book details."
                );

                return;
            }

            try {

                int id = Integer.parseInt(idText);

                if (findBook(id) != null) {

                    JOptionPane.showMessageDialog(
                        this, "Book ID already exists!"
                    );

                    return;
                }

                books.add(new Book(
                    id, title, author, category
                ));

                saveBooks();

                idField.setText("");
                titleField.setText("");
                authorField.setText("");
                categoryField.setText("");

                refreshCatalog();
                refreshDashboard();

                JOptionPane.showMessageDialog(
                    this, "Book added successfully!"
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    this, "Book ID must be a number."
                );
            }
        });

        outer.add(form);

        return outer;
    }

    // =================================
    // STUDENT PROFILE
    // =================================

    JPanel createProfilePage() {

        JPanel outer = new JPanel(new GridBagLayout());

        outer.setBackground(background);

        JPanel profile = new JPanel();

        profile.setLayout(
            new BoxLayout(profile, BoxLayout.Y_AXIS)
        );

        profile.setBackground(white);

        profile.setBorder(
            new EmptyBorder(35, 40, 35, 40)
        );

        profile.setPreferredSize(
            new Dimension(450, 390)
        );

        JLabel title = createLabel(
            "STUDENT PROFILE", 25, blue
        );

        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        profile.add(title);

        profile.add(Box.createVerticalStrut(30));

        profile.add(createProfileRow(
            "Full Name", currentStudent.name
        ));

        profile.add(createProfileRow(
            "Student ID", currentStudent.id
        ));

        profile.add(createProfileRow(
            "Username", currentStudent.username
        ));

        int count = 0;

        for (Book b : books) {

            if (b.borrowedBy.equals(currentStudent.id)) {
                count++;
            }
        }

        profile.add(createProfileRow(
            "Borrowed Books", String.valueOf(count)
        ));

        profile.add(Box.createVerticalStrut(25));

        JLabel note = createLabel(
            "Welcome to LHAI Library System!",
            13, grayText
        );

        note.setAlignmentX(Component.CENTER_ALIGNMENT);

        profile.add(note);

        outer.add(profile);

        return outer;
    }

    // =================================
    // PROFILE INFORMATION ROW
    // =================================

    JPanel createProfileRow(String label, String value) {

        JPanel row = new JPanel(new BorderLayout());

        row.setBackground(white);

        row.setBorder(
            new EmptyBorder(12, 5, 12, 5)
        );

        row.add(
            createLabel(label, 13, grayText),
            BorderLayout.WEST
        );

        row.add(
            createLabel(value, 14, darkText),
            BorderLayout.EAST
        );

        return row;
    }

    // =================================
    // FIND BOOK BY ID
    // =================================

    Book findBook(int id) {

        for (Book b : books) {

            if (b.id == id) {
                return b;
            }
        }

        return null;
    }

    // =================================
    // ENCODE TEXT
    // =================================

    String enc(String value) {

        return Base64.getEncoder().encodeToString(
            value.getBytes(
                java.nio.charset.StandardCharsets.UTF_8
            )
        );
    }

    // =================================
    // DECODE TEXT
    // =================================

    String dec(String value) {

        return new String(
            Base64.getDecoder().decode(value),
            java.nio.charset.StandardCharsets.UTF_8
        );
    }

    // =================================
    // SAVE STUDENTS
    // =================================

    void saveStudents() throws IOException {

        try (
            PrintWriter writer = new PrintWriter(
                new FileWriter("students.txt")
            )
        ) {

            for (Student s : students) {

                writer.println(
                    enc(s.id) + "|" +
                    enc(s.name) + "|" +
                    enc(s.username) + "|" +
                    s.salt + "|" +
                    s.password
                );
            }
        }
    }

    // =================================
    // LOAD STUDENTS
    // =================================

    void loadStudents() {

        File file = new File("students.txt");

        if (!file.exists()) return;

        try (
            BufferedReader reader = new BufferedReader(
                new FileReader(file)
            )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length == 5) {

                    students.add(new Student(
                        dec(data[0]),
                        dec(data[1]),
                        dec(data[2]),
                        data[3],
                        data[4]
                    ));
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                null,
                "Unable to load student accounts."
            );
        }
    }

    // =================================
    // SAVE BOOKS
    // =================================

    void saveBooks() {

        try (
            PrintWriter writer = new PrintWriter(
                new FileWriter("book.txt")
            )
        ) {

            for (Book b : books) {

                writer.println(
                    b.id + "|" +
                    enc(b.title) + "|" +
                    enc(b.author) + "|" +
                    enc(b.category) + "|" +
                    enc(b.borrowedBy)
                );
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to save book records."
            );
        }
    }

    // =================================
    // LOAD BOOKS
    // =================================

    void loadBooks() {

        File file = new File("book.txt");

        if (!file.exists()) return;

        try (
            BufferedReader reader = new BufferedReader(
                new FileReader(file)
            )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length == 5) {

                    Book b = new Book(
                        Integer.parseInt(data[0]),
                        dec(data[1]),
                        dec(data[2]),
                        dec(data[3])
                    );

                    b.borrowedBy = dec(data[4]);

                    books.add(b);
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                null,
                "Unable to load book records."
            );
        }
    }

}
