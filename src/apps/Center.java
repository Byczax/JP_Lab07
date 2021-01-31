package apps;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


import communication.ICenter;
import communication.IDesigner;
import communication.IMonitor;
import communication.IStand;
import support.Answer;
import support.CustomException;
import support.Question;

public class Center implements ICenter {

    private static IDesigner id = null;

    private static int identifier = 0;
    private static int visitorIdentifier = 0;

    Map<Integer, IMonitor> monitors = new HashMap<>();
    Map<Integer, IStand> stands = new HashMap<>();
    Map<Integer, IDesigner> designers = new HashMap<>();
    Map<Integer, String> visitors = new HashMap<>();

    private final List<Question> questions = new ArrayList<>();
    private final List<Answer> answers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Registry reg = LocateRegistry.createRegistry(3000);
            reg.rebind("Center", UnicastRemoteObject.exportObject(new Center(), 0));
            System.out.println("Center is ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int signIn(String visitorName) throws RemoteException {
        visitorIdentifier++;
        visitors.put(visitorIdentifier, visitorName);
        return visitorIdentifier;
    }

    @Override
    public void signOut(int visitorId) throws RemoteException, CustomException {
        if (!visitors.containsKey(visitorId)) {
            throw new CustomException("ERROR;visitor is a ghost.");
        }
        visitors.remove(visitorId);
    }

    @Override
    public Question[] getQuestions() throws RemoteException {
        return questions.toArray(Question[]::new);
    }

    @Override
    public boolean[] checkAnswers(int userId, Answer[] a) throws RemoteException, CustomException {
        if (!visitors.containsKey(userId)) {
            throw new CustomException("ERROR;visitor is a ghost.");
        }
        boolean[] correctAnswers = new boolean[a.length];
        int correctAmount = 0;
        for (int i = 0; i < a.length; i++) {
            correctAnswers[i] = a[i].answer.equals(answers.get(i).answer);
            if (correctAnswers[i]) {
                correctAmount++;
            }
        }
        for (IMonitor monitor : monitors.values()
        ) {
            monitor.setScore(visitors.get(userId), (correctAmount * 100) / (a.length));
        }
        return correctAnswers;
    }

    @Override
    public void addQA(Question[] q, Answer[] a) throws RemoteException, CustomException {
        if (q.length == 0 || a.length == 0) {
            throw new CustomException("ERROR;No added question(s) and answer(s)");
        }
        questions.addAll(Arrays.asList(q));
        answers.addAll(Arrays.asList(a));
    }

    @Override
    public IStand[] getStands() throws RemoteException {
        return stands.values().toArray(IStand[]::new);
    }

    @Override
    public int connect(IDesigner id) throws RemoteException {
        Center.id = id;
        identifier++;
        designers.put(identifier, id);
        return identifier;
    }

    @Override
    public int connect(IStand is) throws RemoteException {
        identifier++;
        stands.put(identifier, is);
        id.notify(identifier, true);
        return identifier;
    }

    @Override
    public int connect(IMonitor im) throws RemoteException {
        identifier++;
        monitors.put(identifier, im);
        return identifier;
    }

    @Override
    public void disconnect(int identifier) throws RemoteException, CustomException {
        if (stands.containsKey(identifier)) {
            stands.remove(identifier);
            id.notify(identifier, false);
        } else if (designers.containsKey(identifier)) {
            designers.remove(identifier);
        } else if (monitors.containsKey(identifier)) {
            monitors.remove(identifier);
        } else
            throw new CustomException("ERROR;Something went wrong with disconnection.");
    }
}
