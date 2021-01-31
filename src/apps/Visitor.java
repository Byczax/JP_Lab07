package apps;

import communication.ICenter;
import gui.VisitorGui;
import support.Answer;
import support.CustomException;
import support.Question;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Visitor {
    private int id;
    private boolean checkedAnswers = false;
    private ICenter iCenter;

    public static void main(String[] args) {
        new Visitor();
    }

    public Visitor() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 3000);
            iCenter = (ICenter) reg.lookup("Center");
            new VisitorGui(this);
            signIn();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isCheckedAnswers() {
        return checkedAnswers;
    }

    public void checkedAnswer() {
        checkedAnswers = true;
    }

    public void signIn() {
        var visitorName = JOptionPane.showInputDialog("Write your name");
        try {
            id = iCenter.signIn(visitorName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void checkAnswers(Answer[] answers) {
        try {
            iCenter.checkAnswers(id, answers);
        } catch (RemoteException | CustomException e) {
            e.printStackTrace();
        }
    }

    public Question[] getQuestions() {
        try {
            return iCenter.getQuestions();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new Question[0];
    }

    public void disconnect() {
        try {
            iCenter.signOut(id);
        } catch (RemoteException | CustomException e) {
            e.printStackTrace();
        }
    }
}
