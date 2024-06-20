import javax.swing.*;
import org.jpl7.Query;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Task extends JPanel implements ActionListener {
    private int id;
    private JTextPane taskField;
    private JButton deleteButton;
    private JPanel parentPanel;

    public Task(JPanel parentPanel, int id, String description) {
        this.parentPanel = parentPanel;
        this.id = id;

        taskField = new JTextPane();
        taskField.setBorder(BorderFactory.createLineBorder(Color.RED));
        taskField.setPreferredSize(new Dimension(300, 50));
        taskField.setContentType("text/html");
        taskField.setText(description);

        JCheckBox checkBox = new JCheckBox();
        checkBox.setPreferredSize(new Dimension(50, 50));
        checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkBox.addActionListener(this);

        deleteButton = new JButton("X");
        deleteButton.setPreferredSize(new Dimension(50, 50));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(this);

        add(checkBox);
        add(taskField);
        add(deleteButton);
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return taskField.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            if (checkBox.isSelected()) {
                String taskText = taskField.getText().replaceAll("<[^>]*>", "");
                taskField.setText("<html><s>" + taskText + "</s></html>");
            } else {
                String taskText = taskField.getText().replaceAll("<[^>]*>", "");
                taskField.setText(taskText);
            }
        } else if (e.getSource() instanceof JButton) {
            parentPanel.remove(this);
            parentPanel.repaint();
            parentPanel.revalidate();
            Query query = new Query("remove_task(" + id + ")");
            query.hasSolution();
        }
    }
}


