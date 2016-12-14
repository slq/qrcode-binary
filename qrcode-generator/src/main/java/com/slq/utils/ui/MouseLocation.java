package com.slq.utils.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

public class MouseLocation extends JFrame {

    /* the resolution of the mouse motion */
    private static final int DELAY = 10;
    private Timer timer;
    private final Set<MouseMotionListener> mouseMotionListeners;

    protected MouseLocation() {
        setSize(100,100);
        Panel panel = new Panel();
        final JLabel label = new JLabel();
        panel.add(label);
        setContentPane(panel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mouseMotionListeners = new HashSet<MouseMotionListener>();

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                String msg = String.format("x=%.0f, y=%.0f", point.getX(), point.getY());
                label.setText(msg);
            }
        });

        /* poll mouse coordinates at the given rate */
        timer = new Timer(DELAY, new ActionListener() {
            private Point lastPoint = MouseInfo.getPointerInfo().getLocation();

            /* called every DELAY milliseconds to fetch the
             * current mouse coordinates */
            public synchronized void actionPerformed(ActionEvent e) {
                Point point = MouseInfo.getPointerInfo().getLocation();

                if (!point.equals(lastPoint)) {
                    fireMouseMotionEvent(point);
                }

                lastPoint = point;
            }
        });

        timer.start();
    }

    public void addMouseMotionListener(MouseMotionListener listener) {
        synchronized (mouseMotionListeners) {
            mouseMotionListeners.add(listener);
        }
    }

    public void removeMouseMotionListener(MouseMotionListener listener) {
        synchronized (mouseMotionListeners) {
            mouseMotionListeners.remove(listener);
        }
    }

    private void fireMouseMotionEvent(Point point) {
        synchronized (mouseMotionListeners) {
            for (final MouseMotionListener listener : mouseMotionListeners) {
                final MouseEvent event =
                        new MouseEvent(this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, point.x, point.y, 0, false);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        listener.mouseMoved(event);
                    }
                });
            }
        }
    }

    /* Testing the ovserver */
    public static void main(String[] args) {
        MouseLocation mouseLocation = new MouseLocation();
        mouseLocation.setVisible(true);
    }
}
