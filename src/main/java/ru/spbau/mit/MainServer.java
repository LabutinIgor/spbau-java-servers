package ru.spbau.mit;

import ru.spbau.mit.servers.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    private ServerSocket serverSocket;
    private AbstractServer sortingServer;

    public static void main(String[] args) throws IOException {
        new MainServer().start();
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(Constants.PORT_OF_MAIN_SERVER);
        new Thread(this::handleConnections).start();
    }

    private void handleConnections() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                if (socket == null) {
                    break;
                }
                processQuery(socket);
                socket.close();
            } catch (IOException e) {
                System.err.println("Error in handling connection in main server");
                break;
            }
        }
    }

    private void processQuery(Socket socket) {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
            byte request = inputStream.readByte();
            switch (request) {
                case Constants.START_QUERY:
                    String serverType = inputStream.readUTF();
                    processStartQuery(serverType);
                    break;
                case Constants.STOP_QUERY:
                    processStopQuery(outputStream);
                    break;
                default:
                    System.err.println("Incorrect query to server: " + request);
            }
        } catch (IOException e) {
            System.err.println("Error in processing query in main server");
        }
    }

    private void processStartQuery(String serverType) throws IOException {
        switch (serverType) {
            case "TCP, thread for each client":
                sortingServer = new TCPServerThreadForEachClient();
                break;
            case "TCP, CachedThreadPool":
                sortingServer = new TCPServerCachedThreadPool();
                break;
            case "TCP, non-blocking":
                sortingServer = new TCPServerNonBlocking();
                break;
            case "TCP, one thread":
                sortingServer = new TCPServerOneThread();
                break;
            case "UDP, thread for each query":
                sortingServer = new UDPServerThreadForEachQuery();
                break;
            default:
                sortingServer = new UDPServerFixedThreadPool();

        }
        sortingServer.start();
    }

    private void processStopQuery(DataOutputStream outputStream) throws IOException {
        sortingServer.stop();
        outputStream.writeInt(sortingServer.getTimeQuery());
        outputStream.writeLong(sortingServer.getSumOfClientsTime());
    }
}
