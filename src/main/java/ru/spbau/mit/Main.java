package ru.spbau.mit;

import ru.spbau.mit.clients.AbstractClient;
import ru.spbau.mit.clients.ClientFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Servers");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        settingsPanel.add(new JLabel("Choose type of server:"));
        JComboBox serverTypeComboBox = new JComboBox<>(new String[]{"TCP, thread for each client",
                "TCP, CachedThreadPool", "TCP, non-blocking", "TCP, one thread", "UDP, thread for each query",
                "UDP, FixedThreadPool"});
        settingsPanel.add(serverTypeComboBox);

        settingsPanel.add(new JLabel("X value:"));
        JTextField textFieldForX = new JTextField("1");
        settingsPanel.add(textFieldForX);

        settingsPanel.add(new JLabel("Choose changing parameter:"));
        JComboBox changingParameterComboBox = new JComboBox<>(new String[]{"N", "M", "delta"});
        settingsPanel.add(changingParameterComboBox);

        settingsPanel.add(new JLabel("Minimum of changing parameter:"));
        JTextField minChangingParameterTextField = new JTextField("0");
        settingsPanel.add(minChangingParameterTextField);

        settingsPanel.add(new JLabel("Maximum of changing parameter:"));
        JTextField maxChangingParameterTextField = new JTextField("0");
        settingsPanel.add(maxChangingParameterTextField);

        settingsPanel.add(new JLabel("Step of changing parameter:"));
        JTextField stepChangingParameterTextField = new JTextField("0");
        settingsPanel.add(stepChangingParameterTextField);

        settingsPanel.add(new JLabel("Set constant parameters:"));
        settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        settingsPanel.add(new JLabel("N:"));
        JTextField textFieldForN = new JTextField("0");
        settingsPanel.add(textFieldForN);

        settingsPanel.add(new JLabel("M:"));
        JTextField textFieldForM = new JTextField("0");
        settingsPanel.add(textFieldForM);

        settingsPanel.add(new JLabel("Delta:"));
        JTextField textFieldForDelta = new JTextField("0");
        settingsPanel.add(textFieldForDelta);

        ChartPanel chartTimeQueryInServer = new ChartPanel();
        ChartPanel chartTimeClientProcessingInServer = new ChartPanel();
        ChartPanel chartClientTime = new ChartPanel();

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            int cntQueriesPerClient = Integer.parseInt(textFieldForX.getText());

            java.util.List<Point> chartTimeQueryInServerData = new ArrayList<>();
            java.util.List<Point> chartTimeClientProcessingInServerData = new ArrayList<>();
            java.util.List<Point> chartClientTimeData = new ArrayList<>();

            chartTimeQueryInServerData.add(new Point(0, 0));
            chartTimeClientProcessingInServerData.add(new Point(0, 0));
            chartClientTimeData.add(new Point(0, 0));

            int cntClients, arraySize, delta;
            int minChangingParameter = Integer.parseInt(minChangingParameterTextField.getText());
            int maxChangingParameter = Integer.parseInt(maxChangingParameterTextField.getText());
            int step = Integer.parseInt(stepChangingParameterTextField.getText());
            String serverType = (String) serverTypeComboBox.getSelectedItem();
            switch ((String) changingParameterComboBox.getSelectedItem()) {
                case "N":
                    cntClients = Integer.parseInt(textFieldForM.getText());
                    delta = Integer.parseInt(textFieldForDelta.getText());
                    for (int i = minChangingParameter; i <= maxChangingParameter; i += step) {
                        int[] data = getStatisticsData(i, cntClients, delta, cntQueriesPerClient, serverType);
                        chartTimeQueryInServerData.add(new Point(i, data[0]));
                        chartTimeClientProcessingInServerData.add(new Point(i, data[1]));
                        chartClientTimeData.add(new Point(i, data[2]));
                    }
                    break;
                case "M":
                    arraySize = Integer.parseInt(textFieldForN.getText());
                    delta = Integer.parseInt(textFieldForDelta.getText());
                    for (int i = minChangingParameter; i <= maxChangingParameter; i += step) {
                        int[] data = getStatisticsData(arraySize, i, delta, cntQueriesPerClient, serverType);
                        chartTimeQueryInServerData.add(new Point(i, data[0]));
                        chartTimeClientProcessingInServerData.add(new Point(i, data[1]));
                        chartClientTimeData.add(new Point(i, data[2]));
                    }
                    break;
                default:
                    cntClients = Integer.parseInt(textFieldForM.getText());
                    arraySize = Integer.parseInt(textFieldForN.getText());
                    for (int i = minChangingParameter; i <= maxChangingParameter; i += step) {
                        int[] data = getStatisticsData(arraySize, cntClients, i, cntQueriesPerClient, serverType);
                        chartTimeQueryInServerData.add(new Point(i, data[0]));
                        chartTimeClientProcessingInServerData.add(new Point(i, data[1]));
                        chartClientTimeData.add(new Point(i, data[2]));
                    }
            }

            chartTimeQueryInServer.setData(chartTimeQueryInServerData);
            chartTimeClientProcessingInServer.setData(chartTimeClientProcessingInServerData);
            chartClientTime.setData(chartClientTimeData);


            try (PrintWriter out = new PrintWriter(new File("results.txt"))) {
                out.println(serverType);
                out.println("X: " + textFieldForX.getText());
                out.println("Changing parameter: " + changingParameterComboBox.getSelectedItem());
                out.println("Minimum of changing parameter: " + minChangingParameter);
                out.println("Maximum of changing parameter: " + maxChangingParameter);
                out.println("Step of changing parameter: " + step);
                if (!changingParameterComboBox.getSelectedItem().equals("N")) {
                    out.println("N: " + textFieldForN.getText());
                }
                if (!changingParameterComboBox.getSelectedItem().equals("M")) {
                    out.println("M: " + textFieldForM.getText());
                }
                if (!changingParameterComboBox.getSelectedItem().equals("delta")) {
                    out.println("Delta: " + textFieldForDelta.getText());
                }

                out.println("First graph:");
                for (Point point : chartTimeQueryInServerData) {
                    out.println(point.getX() + ", " + point.getY());
                }

                out.println("Second graph:");
                for (Point point : chartTimeClientProcessingInServerData) {
                    out.println(point.getX() + ", " + point.getY());
                }

                out.println("Third graph:");
                for (Point point : chartClientTimeData) {
                    out.println(point.getX() + ", " + point.getY());
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        settingsPanel.add(startButton);


        panel.add(settingsPanel);
        panel.add(chartTimeQueryInServer);
        panel.add(chartTimeClientProcessingInServer);
        panel.add(chartClientTime);

        frame.add(panel);

        frame.setSize(1300, 500);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static int[] getStatisticsData(int arraySize, int cntClients, int delta, int cntQueriesPerClient,
                                           String serverType) {
        try {
            Socket socket = new Socket(Constants.HOST, Constants.PORT_OF_MAIN_SERVER);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeByte(Constants.START_QUERY);
            outputStream.writeUTF(serverType);
            Thread.sleep(100);
            outputStream.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        ClientFactory clientFactory = new ClientFactory(arraySize, delta, cntQueriesPerClient, serverType);

        List<AbstractClient> clients = new ArrayList<>();
        List<Thread> clientThreads = new ArrayList<>();
        for (int i = 0; i < cntClients; i++) {
            AbstractClient client = clientFactory.getClient();
            clients.add(client);
            Thread clientThread = new Thread(() -> {
                try {
                    client.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            clientThreads.add(clientThread);
            clientThread.start();
        }

        long sumOfClientsTime = 0;
        for (int i = 0; i < cntClients; i++) {
            try {
                clientThreads.get(i).join();
                sumOfClientsTime += clients.get(i).getTime();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int[] data = new int[3];
        data[2] = (int) (sumOfClientsTime / cntClients);
        try {
            Socket socket = new Socket(Constants.HOST, Constants.PORT_OF_MAIN_SERVER);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeByte(Constants.STOP_QUERY);
            data[0] = inputStream.readInt();
            data[1] = (int) (inputStream.readLong() / cntClients);
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
