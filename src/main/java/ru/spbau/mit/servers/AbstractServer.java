package ru.spbau.mit.servers;

import com.google.common.primitives.Ints;
import ru.spbau.mit.MessageProto;

import java.io.IOException;

public abstract class AbstractServer {
    public abstract void start() throws IOException;

    public abstract void stop() throws IOException;

    public abstract int getTimeQuery();

    public abstract long getSumOfClientsTime();

    protected MessageProto.Message sort(MessageProto.Message input) {
        int[] a = input.getNumberList().stream().mapToInt(x -> x).toArray();

        for (int i = 0; i < a.length; i++) {
            for (int j = 1; j < a.length; j++) {
                if (a[j - 1] > a[j]) {
                    int t = a[j - 1];
                    a[j - 1] = a[j];
                    a[j] = t;
                }
            }
        }

        return MessageProto.Message.newBuilder().addAllNumber(Ints.asList(a)).build();
    }
}
