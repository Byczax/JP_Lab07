package gui;

import apps.Stand;
import support.Description;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class StandGui {
    private JPanel mainPanel;
    private JLabel myDescription;

    public StandGui(Stand stand) {
        JPanel root = mainPanel;
        JFrame frame = new JFrame();
        frame.setTitle("StandGui");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(300, 100));
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                stand.disconnect();
                frame.dispose();
            }
        });
    }

    public void setStandContent(Description description) {
        myDescription.setText(description.description);
    }
}
