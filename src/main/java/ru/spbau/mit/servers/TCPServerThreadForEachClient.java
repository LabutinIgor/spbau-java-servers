package ru.spbau.mit.servers;

import ru.spbau.mit.Constants;
import ru.spbau.mit.MessageProto;
import ru.spbau.mit.servers.AbstractServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TCPServerThreadForEachClient extends AbstractServer {
    private ServerSocket serverSocket;
    private AtomicLong sumTimeQueries = new AtomicLong(0);
    private AtomicInteger cntQueries = new AtomicInteger(0);
    private AtomicLong sumTimeClients = new AtomicLong(0);
    private AtomicInteger cntClients = new AtomicInteger(0);
    private AtomicInteger finishedClients = new AtomicInteger(0);

    @Override
    public void start() throws IOException {
        serverSocket = new ServerSocket(Constants.PORT_OF_SORTING_SERVER, 200);
        new Thread(this::handleConnections).start();
    }

    private void handleConnections() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                if (socket == null) {
                    break;
                }
                new Thread(() -> {
                    try {
                        cntClients.getAndIncrement();
                        sumTimeClients.getAndAdd(-System.currentTimeMillis());
                        while (!socket.isClosed()) {
                            processQuery(socket);
                        }
                    } catch (IOException ignored) {
                    } finally {
                        sumTimeClients.getAndAdd(System.currentTimeMillis());
                        finishedClients.getAndIncrement();
                    }
                }).start();
            } catch (IOException e) {
                break;
            }
        }
    }

    private void processQuery(Socket socket) throws IOException {
        long timeStartQuery = System.currentTimeMillis();
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        int cnt = inputStream.readInt();
        byte[] data = new byte[cnt];
        inputStream.readFully(data);
        MessageProto.Message arrayToSort = MessageProto.Message.parseFrom(data);

        MessageProto.Message resultArray = sort(arrayToSort);
        outputStream.writeInt(resultArray.getSerializedSize());
        outputStream.write(resultArray.toByteArray());
        outputStream.flush();
        sumTimeQueries.getAndAdd(System.currentTimeMillis() - timeStartQuery);
        cntQueries.getAndIncrement();
    }

    @Override
    public void stop() throws IOException {
        while (cntClients.get() != finishedClients.get()) {
            Thread.yield();
        }
        serverSocket.close();
    }

    @Override
    public int getTimeQuery() {
        return (int) (sumTimeQueries.get() / cntQueries.get());
    }

    @Override
    public long getSumOfClientsTime() {
        return sumTimeClients.get();
    }

}
