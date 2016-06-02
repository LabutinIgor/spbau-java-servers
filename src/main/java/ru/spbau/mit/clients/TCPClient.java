package ru.spbau.mit.clients;

import com.google.common.primitives.Ints;
import ru.spbau.mit.Constants;
import ru.spbau.mit.MessageProto;
import ru.spbau.mit.clients.AbstractClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class TCPClient extends AbstractClient {
    private int arraySize;
    private int delta;
    private int cntQueriesPerClient;
    private boolean newConnectionForEachQuery;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Random random = new Random();
    private int time;

    public TCPClient(int arraySize, int delta, int cntQueriesPerClient, boolean newConnectionForEachQuery) {
        this.arraySize = arraySize;
        this.delta = delta;
        this.cntQueriesPerClient = cntQueriesPerClient;
        this.newConnectionForEachQuery = newConnectionForEachQuery;
    }

    @Override
    public void start() throws IOException {
        long beginTime = System.currentTimeMillis();
        if (!newConnectionForEachQuery) {
            socket = new Socket(Constants.HOST, Constants.PORT_OF_SORTING_SERVER);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        }
        for (int i = 0; i < cntQueriesPerClient; i++) {
            if (newConnectionForEachQuery) {
                socket = new Socket(Constants.HOST, Constants.PORT_OF_SORTING_SERVER);
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
            }

            MessageProto.Message arrayToSort = MessageProto.Message.newBuilder()
                    .addAllNumber(Ints.asList(random.ints(arraySize).toArray())).build();
            outputStream.writeInt(arrayToSort.getSerializedSize());
            outputStream.write(arrayToSort.toByteArray());
            outputStream.flush();

            int cnt = inputStream.readInt();
            byte[] data = new byte[cnt];
            inputStream.readFully(data);

            MessageProto.Message resultArray = MessageProto.Message.parseFrom(data);

            if (resultArray.getNumberCount() != arrayToSort.getNumberCount()) {
                System.err.println("Wrong array size");
            }
            for (int j = 0; j < resultArray.getNumberCount() - 1; j++) {
                if (resultArray.getNumber(j) > resultArray.getNumber(j + 1)) {
                    System.err.println("Wrong array");
                    break;
                }
            }

            if (newConnectionForEachQuery) {
                inputStream.close();
                outputStream.close();
                socket.close();
            }
            try {
                Thread.sleep(delta);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!newConnectionForEachQuery) {
            inputStream.close();
            outputStream.close();
            socket.close();
        }
        time = (int) (System.currentTimeMillis() - beginTime);
    }

    @Override
    public int getTime() {
        return time;
    }
}
