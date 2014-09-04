package networks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Network {

    private Socket conn;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private NoTimeOut noTimeOut;
    private Thread noTimeOutThread;
    private MessageSender sender;
    private MessageReceiver receiver;
    private Thread receiverThread;
    private MessageParser parser;

    public Network(InetAddress host, int port) throws IOException {
        if (!setClientConnection(host, port)) setServerConnection(port);
//        System.out.println( "Пытаюсь создать исходящий поток");
        out = new ObjectOutputStream(conn.getOutputStream());
//        System.out.println( "Иcходящий поток успешно создан " + out);
//        System.out.println( "Пытаюсь создать входящий поток");
        in = new ObjectInputStream(conn.getInputStream());
//        System.out.println( "Входящий поток успешно создан " + in);
        sender = new MessageSender(out);
        parser = new MessageParser();
        receiver = new MessageReceiver(in, parser);
        receiverThread = new Thread(receiver);
        noTimeOut = new NoTimeOut(sender);
        noTimeOutThread = new Thread(noTimeOut);
        noTimeOutThread.start();
        System.out.println("network (" + Thread.currentThread().getName() + ") starts noTimeOutThread (" + noTimeOutThread.getName() + ")");
        receiverThread.start();
        System.out.println("network (" + Thread.currentThread().getName() + ") starts receiverThread (" + receiverThread.getName() + ")");
    }

    public MessageSender getSender() {
        return sender;
    }

    public MessageParser getParser() {
        return parser;
    }

    private boolean setClientConnection(InetAddress host, int port) {
//        System.out.println("Устанавливаю клиентское соединение");
        try {
            conn = new Socket(host, port);

        } catch (IOException e) {
//            System.err.println("Клиентское соединение установить невозможно \n" + e);
            return false;
        }
        System.out.println("Клиентское соединение установлено " + conn);
        return true;
    }

    private void setServerConnection(int port) {
        System.out.println("Устанавливаю серверное соединение");
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Unable start server on port " + port + "\n" + e);
        }
        try {
            conn = server.accept();
            System.out.println("Серверное соединение установлено " + conn);
        } catch (IOException e) {
        }
    }

    public void close(){
        System.out.println("network (" + Thread.currentThread().getName() + ") try to interrupt noTimeOutThread (" + noTimeOutThread.getName() + ")");
        noTimeOut.interrupt();
        noTimeOutThread.interrupt();
        System.out.println("network (" + Thread.currentThread().getName() + ") try to interrupt receiverThread (" + receiverThread.getName() + ")");
        receiver.interrupt();
        receiverThread.interrupt();
        try {
            System.out.println("network (" + Thread.currentThread().getName() + ") try to join noTimeOutThread (" + noTimeOutThread.getName() + ")");
            noTimeOutThread.join();
            System.out.println("network (" + Thread.currentThread().getName() + ") noTimeOutThread (" + noTimeOutThread.getName() + ") joined");
        } catch (InterruptedException e) {
            System.out.println("network (" + Thread.currentThread().getName() + ") noTimeOutThread (" + noTimeOutThread.getName() + ") join error");
//            e.printStackTrace();
        }
        try {
            System.out.println("network (" + Thread.currentThread().getName() + ") try to join receiverThread (" + receiverThread.getName() + ")");
            receiverThread.join();
            System.out.println("network (" + Thread.currentThread().getName() + ") receiverThread (" + receiverThread.getName() + ") joined");
        } catch (InterruptedException e) {
            System.out.println("network (" + Thread.currentThread().getName() + ") receiverThread (" + receiverThread.getName() + ") join error");
//            e.printStackTrace();
        }
        try {
            in.close();
            out.close();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
