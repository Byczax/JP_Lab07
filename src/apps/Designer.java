package apps;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import communication.ICenter;
import communication.IDesigner;
import communication.IStand;
import gui.DesignerGui;
import support.Answer;
import support.CustomException;
import support.Question;

import javax.swing.event.AncestorEvent;

public class Designer implements IDesigner {

    private ICenter iCenter;
    private DesignerGui designerGui;
    private int id;

    private List<IStand> stands = new ArrayList<>();

    public static void main(String[] args) {
        new Designer();
    }

    Designer() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 3000);
            iCenter = (ICenter) reg.lookup("Center");
            IDesigner id = (IDesigner) UnicastRemoteObject.exportObject(this, 0);
            System.out.println("Designer is ready");
            this.id = iCenter.connect(id);
            designerGui = new DesignerGui(this);
            getStands();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(int standId, boolean isConnected) {
        System.out.println("standId=" + standId + " isConnected=" + isConnected);
        designerGui.addStandTable(standId);
    }

    public List<IStand> getStands() {
        try {
            stands = Arrays.asList(iCenter.getStands());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return stands;
    }

    public void addQuestion(Question[] question, Answer[] answer) {
        try {
            iCenter.addQA(question, answer);
        } catch (RemoteException | CustomException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            iCenter.disconnect(id);
        } catch (RemoteException | CustomException e) {
            e.printStackTrace();
        }
    }
}
