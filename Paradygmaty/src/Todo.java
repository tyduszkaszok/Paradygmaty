import javax.swing.*;
import org.jpl7.Query;



public class Todo {
    public static void main(String[] args) {
        String consultQuery = "consult('src/resource/tasks.pl')";
        Query q1 = new Query(consultQuery);
        System.out.println(consultQuery + " " + (q1.hasSolution() ? "succeeded" : "failed"));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
}
