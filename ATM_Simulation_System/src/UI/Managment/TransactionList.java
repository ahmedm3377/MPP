package UI.Managment;

import Business.SystemModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TransactionList extends JFrame {
    private JPanel panel1;
    private JTable table1;
    private JButton backButton;

    TransactionList(){
        setContentPane(panel1);
        // Default visibility is false. You have enabled visibility true
        setVisible(true);
        // Terminates the Application when the frame is closed.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Account List");
        // Provide the frame width and height
        setSize(500, 500);
        // Make your screen center
        setLocationRelativeTo(null);
        setResizable(false);// If you wish
        getList();

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OptionsPage();
                dispose();
            }
        });
    }
    public  void getList()  {
        List<Object[]> list =  SystemModel.getTransactions();
        String[] columnNames = {"Index" ,"TransactionType","Date","Amount","Account"};
        Object[][] data = new Object[list.size()][5];

        for(int i=0; i<list.size();i++){
            data[i] = list.get(i);
        }
        DefaultTableModel model = new DefaultTableModel(data,columnNames);
        System.out.println(data);
        table1.setModel(model);

    }
    public static void main(String[] args) {
 /*
 While it is not mandatory to use EventQueue.invokeLater,
 it is a best practice for all Swing applications to ensure
 thread safety and avoid potential concurrency issues.
 */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransactionList();
            }
        });
    }
}
