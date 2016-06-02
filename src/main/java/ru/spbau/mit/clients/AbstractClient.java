package ru.spbau.mit.clients;

import java.io.IOException;

public abstract class AbstractClient {
    public abstract void start() throws IOException;

    public abstract int getTime();
}
