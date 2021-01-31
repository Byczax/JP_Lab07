package apps;

import communication.ICenter;
import communication.IMonitor;
import gui.MonitorGui;
import support.CustomException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Monitor implements IMonitor {

    private int id;
    private ICenter iCenter;
    private MonitorGui monitorGui;

    public static void main(String[] args) {
        new Monitor();
    }

    public Monitor() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 3000);
            iCenter = (ICenter) reg.lookup("Center");
            IMonitor im = (IMonitor) UnicastRemoteObject.exportObject(this, 0);
            System.out.println("Monitor is ready");
            this.id = iCenter.connect(im);
            monitorGui = new MonitorGui(this);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setScore(String s, int i) throws RemoteException {
        monitorGui.addToTable(s, i);
    }

    public void disconnect() {
        try {
            iCenter.disconnect(id);
        } catch (RemoteException | CustomException e) {
            e.printStackTrace();
        }
    }
}
