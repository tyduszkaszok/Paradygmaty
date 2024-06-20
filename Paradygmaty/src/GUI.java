import javax.swing.*;
import org.jpl7.Query;
import org.jpl7.Term;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
class GUI extends JFrame implements ActionListener {
    private JPanel taskPanel, taskComponentPanel;
    private int taskIdCounter = 1;
    private JTextField addressField, portField, usernameField, passwordField, taskInputField;
    private JTextArea resultArea;
    private Connection connection;
    private JButton addTaskButton, executeQueryButton;
    private JTextField queryInputField;

    public GUI() {
        super("To Do List");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(540, 800));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        addGuiComponents();
        loadTasksFromProlog();
    }

    private void addGuiComponents() {
        JLabel bannerLabel = new JLabel("To Do List");
        bannerLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        bannerLabel.setBounds(
                (getPreferredSize().width - bannerLabel.getPreferredSize().width) / 2,
                15,
                540,
                50
        );

        taskPanel = new JPanel();
        taskComponentPanel = new JPanel();
        taskComponentPanel.setLayout(new BoxLayout(taskComponentPanel, BoxLayout.Y_AXIS));
        taskPanel.add(taskComponentPanel);
        JScrollPane scrollPane = new JScrollPane(taskPanel);
        scrollPane.setBounds(8, 70, 500, 300);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        scrollPane.setMaximumSize(new Dimension(500, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(20);

        taskInputField = new JTextField();
        taskInputField.setBounds(8, 380, 380, 40);
        taskInputField.setBorder(BorderFactory.createTitledBorder("New Task"));

        addTaskButton = new JButton("Add Task");
        addTaskButton.setBounds(398, 380, 110, 30);
        addTaskButton.addActionListener(this);

        addressField = new JTextField();
        addressField.setBounds(8, 420, 500, 40);
        addressField.setBorder(BorderFactory.createTitledBorder("Address"));

        portField = new JTextField("1433");
        portField.setBounds(8, 460, 500, 40);
        portField.setBorder(BorderFactory.createTitledBorder("Port"));

        usernameField = new JTextField();
        usernameField.setBounds(8, 500, 500, 40);
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        passwordField = new JPasswordField();
        passwordField.setBounds(8, 540, 500, 40);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        queryInputField = new JTextField();
        queryInputField.setBounds(8, 580, 500, 40);
        queryInputField.setBorder(BorderFactory.createTitledBorder("SQL Query"));

        executeQueryButton = new JButton("Execute Query");
        executeQueryButton.setBounds(8, 620, 500, 40);
        executeQueryButton.addActionListener(this);

        resultArea = new JTextArea();
        resultArea.setBounds(8, 660, 500, 100);
        resultArea.setBorder(BorderFactory.createTitledBorder("Query Result"));
        resultArea.setEditable(false);

        this.getContentPane().add(bannerLabel);
        this.getContentPane().add(scrollPane);
        this.getContentPane().add(taskInputField);
        this.getContentPane().add(addTaskButton);
        this.getContentPane().add(addressField);
        this.getContentPane().add(portField);
        this.getContentPane().add(usernameField);
        this.getContentPane().add(passwordField);
        this.getContentPane().add(queryInputField);
        this.getContentPane().add(executeQueryButton);
        this.getContentPane().add(resultArea);
    }

    private void loadTasksFromProlog() {
        Query query = new Query("get_tasks(Tasks)");
        if (query.hasSolution()) {
            Term[] tasks = query.oneSolution().get("Tasks").toTermArray();
            for (Term task : tasks) {
                Term[] args = task.toTermArray();
                int id = args[0].intValue();
                String description = args[1].toString();
                addTask(id, description);
            }
        }
    }

    private void addTask(int id, String description) {
        Task taskComponent = new Task(taskComponentPanel, id, description);
        taskComponentPanel.add(taskComponent);
        repaint();
        revalidate();
    }

    private void connectToDatabase() {
        String address = addressField.getText();
        String port = portField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String url = "jdbc:sqlserver://" + address + ":" + port + ";databaseName=dbad_s490128";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, username, password);
            JOptionPane.showMessageDialog(this, "Connected to the database successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addTaskButton) {
            String taskDescription = taskInputField.getText();
            if (!taskDescription.isEmpty()) {
                addTask(taskIdCounter++, taskDescription);
                Query query = new Query("add_task(" + (taskIdCounter - 1) + ", '" + taskDescription + "')");
                query.hasSolution();
                taskInputField.setText("");
            }
        } else if (e.getSource() == executeQueryButton) {
            if (connection == null) {
                connectToDatabase();
            }

            String query = queryInputField.getText();
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                StringBuilder results = new StringBuilder();
                int columnCount = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        results.append(resultSet.getString(i)).append("\t");
                    }
                    results.append("\n");
                }
                resultArea.setText(results.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                resultArea.setText("Query execution failed: " + ex.getMessage());
            }
        }
    }
}


