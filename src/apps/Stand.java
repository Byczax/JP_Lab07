package apps;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import communication.ICenter;
import communication.IStand;
import gui.StandGui;
import support.CustomException;
import support.Description;

public class Stand implements IStand {

    private int id;
    private StandGui standGui;
    private ICenter iCenter;

    public static void main(String[] args) {
        new Stand();
    }

    Stand() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 3000);
            iCenter = (ICenter) reg.lookup("Center");
            IStand is = (IStand) UnicastRemoteObject.exportObject(this, 0);
            System.out.println("Stand is ready");
            this.id = iCenter.connect(is);
            standGui = new StandGui(this);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setContent(Description d) throws RemoteException {
        standGui.setStandContent(d);
    }

    @Override
    public int getId() throws RemoteException {
        return id;
    }

    public void disconnect() {
        try {
            iCenter.disconnect(id);
        } catch (RemoteException | CustomException e) {
            e.printStackTrace();
        }
    }
}
