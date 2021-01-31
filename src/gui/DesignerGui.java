package gui;

import apps.Designer;
import communication.IStand;
import support.Answer;
import support.Description;
import support.Question;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignerGui {
    private JPanel mainPanel;
    private JTextField descriptionField;
    private JButton addEditDescriptionButton;
    private JTable standsTable;
    private JTable questionsTable;
    private JTextField questionField;
    private JTextField answerField;
    private JButton addQuestionButton;

    private List<IStand> stands = new ArrayList<>();
    private final Map<IStand, String> standsWithDescriptions = new HashMap<>();
    Designer myDesigner;

    public DesignerGui(Designer designer) {
        myDesigner = designer;
        JPanel root = mainPanel;
        JFrame frame = new JFrame();
        frame.setTitle("DesignerGui");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setContentPane(root);
        createQuestionTable();
        createStandsTable();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(300, 100));
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                designer.disconnect();
                frame.dispose();
            }
        });
        addEditDescriptionButton.addActionListener(e -> {
            int myRow = standsTable.getSelectedRow();
            Description myDescription = new Description();
            stands = myDesigner.getStands();
            for (IStand stand : stands
            ) {
                try {
                    if (stand.getId() == Integer
                            .parseInt(String
                                    .valueOf(standsTable
                                            .getValueAt(myRow, 0)))) {
                        myDescription.description = descriptionField.getText();
                        standsWithDescriptions.put(stand, myDescription.description);
                        stand.setContent(myDescription);
                        editStandTable();
                        break;
                    }
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        addQuestionButton.addActionListener(e -> {
            addToQuestionTable(questionField.getText(), answerField.getText());
            Question question = new Question();
            Answer answer = new Answer();
            question.question = questionField.getText();
            answer.answer = answerField.getText();
            designer.addQuestion(new Question[]{question}, new Answer[]{answer});
        });
    }

    private void createQuestionTable() {
        questionsTable.setDefaultEditor(Object.class, null);
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Question", "Answer"});
        questionsTable.setModel(tableModel);
    }

    public void createStandsTable() {
        standsTable.setDefaultEditor(Object.class, null);
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Stand", "Description"});
        standsTable.setModel(tableModel);
    }

    public void editStandTable() throws RemoteException {
        var myRow = standsTable.getSelectedRow();
        var myStand = stands.get(myRow);
        Description myDescription = new Description();
        myDescription.description = descriptionField.getText();
        myStand.setContent(myDescription);
        standsTable.setValueAt(myDescription.description, myRow, 1);
    }

    public void addStandTable(int standId) {
        var myModel = (DefaultTableModel) standsTable.getModel();
        myModel.addRow(new Object[]{standId, "null"});
    }

    private void addToQuestionTable(String question, String answer) {
        var myModel = (DefaultTableModel) questionsTable.getModel();
        myModel.addRow(new Object[]{question, answer});
    }
}
