package ru.spbau.mit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChartPanel extends JPanel {
    private List<Point> points;
    private int maxX, maxY;

    public ChartPanel() {
        points = new ArrayList<>();
        setPreferredSize(new Dimension(300, 300));
    }

    public void setData(List<Point> points) {
        this.points = points;
        maxX = maxY = 1;
        for (Point point : points) {
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);

        g.drawLine(40, getHeight() - 40, 40, 40);
        g.drawLine(40, getHeight() - 40, getWidth() - 40, getHeight() - 40);

        if (points.size() > 0) {
            for (int i = 1; i < 5; i++) {
                g.drawString(String.valueOf(maxX / 5 * i), getXCoordinateInChart(maxX / 5 * i), getHeight() - 15);
            }

            for (int i = 1; i < 5; i++) {
                g.drawString(String.valueOf(maxY / 5 * i), 5, getYCoordinateInChart(maxY / 5 * i));
            }

            for (int i = 0; i < points.size() - 1; i++) {
                g.drawLine(getXCoordinateInChart(points.get(i).getX()),
                        getYCoordinateInChart(points.get(i).getY()),
                        getXCoordinateInChart(points.get(i + 1).getX()),
                        getYCoordinateInChart(points.get(i + 1).getY()));
            }
        }
    }

    private int getXCoordinateInChart(Integer x) {
        return x * (getWidth() - 80) / maxX + 40;
    }

    private int getYCoordinateInChart(Integer y) {
        return getHeight() - (y * (getHeight() - 80) / maxY + 40);
    }

}
