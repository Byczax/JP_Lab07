package gui;

import apps.Monitor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MonitorGui {
    private JPanel mainPanel;
    private JTable quizResultsTable;

    public MonitorGui(Monitor monitor) {
        JPanel root = mainPanel;
        JFrame frame = new JFrame();
        frame.setTitle("MonitorGui");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(root);
        createTable();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                monitor.disconnect();
                frame.dispose();
            }
        });
    }

    private void createTable() {
        quizResultsTable.setDefaultEditor(Object.class, null);
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"User", "Score"});
        quizResultsTable.setModel(tableModel);
    }

    public void addToTable(String user, int value) {
        var myModel = (DefaultTableModel) quizResultsTable.getModel();
        myModel.addRow(new Object[]{user, value + "%"});
    }
}
