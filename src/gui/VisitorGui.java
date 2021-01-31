package gui;

import apps.Visitor;
import support.Answer;
import support.Question;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class VisitorGui {
    private JPanel mainPanel;
    private JButton checkAnswersButton;
    private JButton answerForSelectedButton;
    private JButton disconnectButton;
    private JTable questionsTable;
    private final Visitor visitor;


    public VisitorGui(Visitor visitor) {
        this.visitor = visitor;
        JPanel root = mainPanel;
        JFrame frame = new JFrame();
        frame.setTitle("VisitorGui");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setContentPane(root);
        createTable();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        addQuestionsToTable();
        frame.setMinimumSize(new Dimension(300, 100));
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                visitor.disconnect();
                frame.dispose();
            }
        });
        disconnectButton.addActionListener(e -> {
            visitor.disconnect();
            frame.dispose();
        });
        answerForSelectedButton.addActionListener(e -> {
            int idRow = questionsTable.getSelectedRow();
            var myModel = questionsTable.getModel();
            var answer = JOptionPane.showInputDialog(questionsTable.getValueAt(idRow, 0));
            myModel.setValueAt(answer, idRow, 1);
        });
        checkAnswersButton.addActionListener(e -> {
            if (!visitor.isCheckedAnswers()) {
                Answer[] myAnswers = new Answer[questionsTable.getRowCount()];

                for (int i = 0; i < questionsTable.getRowCount(); i++) {
                    Answer answer = new Answer();
                    answer.answer = String.valueOf(questionsTable.getValueAt(i, 1));
                    myAnswers[i] = answer;
                }
                visitor.checkAnswers(myAnswers);
                visitor.checkedAnswer();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Already checked answers",
                        "ERROR!",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void createTable() {
        questionsTable.setDefaultEditor(Object.class, null);
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Question", "Answer"});
        questionsTable.setModel(tableModel);
    }

    public void addQuestionsToTable() {
        var questions = visitor.getQuestions();
        var myModel = (DefaultTableModel) questionsTable.getModel();
        for (Question question : questions
        ) {
            myModel.addRow(new Object[]{question.question, "Null"});
        }
    }
}
