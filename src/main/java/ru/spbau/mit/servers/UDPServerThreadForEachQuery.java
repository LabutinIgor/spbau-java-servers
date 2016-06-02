package ru.spbau.mit.servers;

import java.io.IOException;

public class UDPServerThreadForEachQuery extends AbstractServer {
    @Override
    public void start() throws IOException {
    }

    @Override
    public void stop() throws IOException {
    }

    @Override
    public int getTimeQuery() {
        return 1;
    }

    @Override
    public long getSumOfClientsTime() {
        return 1;
    }
}
