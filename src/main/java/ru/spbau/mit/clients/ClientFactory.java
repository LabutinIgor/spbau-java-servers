package ru.spbau.mit.clients;

public class ClientFactory {
    private int arraySize;
    private int delta;
    private int cntQueriesPerClient;
    private String serverType;
    public ClientFactory(int arraySize, int delta, int cntQueriesPerClient, String serverType) {
        this.arraySize = arraySize;
        this.delta = delta;
        this.cntQueriesPerClient = cntQueriesPerClient;
        this.serverType = serverType;
    }

    public AbstractClient getClient() {
        switch (serverType) {
            case "TCP, thread for each client":
                return new TCPClient(arraySize, delta, cntQueriesPerClient, false);
            case "TCP, CachedThreadPool" :
                return new TCPClient(arraySize, delta, cntQueriesPerClient, false);
            case "TCP, non-blocking" :
                return new TCPClient(arraySize, delta, cntQueriesPerClient, false);
            case "TCP, one thread" :
                return new TCPClient(arraySize, delta, cntQueriesPerClient, true);
            case "UDP, thread for each query" :
                return new UDPClient(arraySize, delta, cntQueriesPerClient);
            default:
                return new UDPClient(arraySize, delta, cntQueriesPerClient);
        }
    }
}
