package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main implements KeyListener {
    public static int time = (15)*60+0; //seconds
    public static int increment = 10; //seconds

    public static float whiteTime = time;
    public static float blackTime = time;

    public static int WHITE = 1;
    public static int BLACK = 2;
    public static int turn = 0;

    public static boolean running = true;

    public static JFrame frame = new JFrame("Chess Clock");
    public static Panel panel = new Panel();

    public static final int INTERVAL = 100; //milliseconds
    public static long lastClick = 0;

    final static Color BACKGROUND =  new Color(40, 40, 40);
    final static Color BUTTON_COLOR = new Color(80, 80, 80);

    public static class Task extends TimerTask {
        @Override
        public void run() {
            if (turn == 0 || !running)
                return;
            if (turn == WHITE) {
                whiteTime = (float)(Math.max(whiteTime-0.1, 0.0));
            }
            else {
                blackTime = (float)(Math.max(blackTime-0.1, 0.0));
            }
            panel.repaint();
        }
    }

    public void init() {
        TimerTask task = new Task();

        frame.add(panel);
        frame.addKeyListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND);

        JButton reset = new JButton("Reset");
        reset.setForeground(Color.WHITE);
        reset.setBackground(BUTTON_COLOR);
        reset.setPreferredSize(new Dimension(240, 80));
        reset.setFocusable(false);
        reset.addActionListener(e -> {
            turn = 0;
            whiteTime = time;
            blackTime = time;
            panel.repaint();
        });

        JButton changeTime = new JButton("Change Time");
        changeTime.setForeground(Color.WHITE);
        changeTime.setBackground(BUTTON_COLOR);
        changeTime.setPreferredSize(new Dimension(240, 80));
        changeTime.setFocusable(false);
        changeTime.addActionListener(e -> {
            if (turn != 0)
                return;

            JPanel inputPanel = new JPanel();
            JTextField minutes = new JTextField(5);
            JTextField seconds = new JTextField(5);
            JTextField incrementField = new JTextField(5);

            inputPanel.add(new JLabel("Minutes:"));
            inputPanel.add(minutes);
            inputPanel.add(new JLabel("Seconds:"));
            inputPanel.add(seconds);
            inputPanel.add(new JLabel("Increment (seconds):"));
            inputPanel.add(incrementField);

            try {
                int result = JOptionPane.showConfirmDialog(null, inputPanel,
                        "", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String minutesText = minutes.getText();
                    if (minutesText.equals(""))
                        minutesText = "0";

                    String secondsText = seconds.getText();
                    if (secondsText.equals(""))
                        secondsText = "0";

                    String incrementText = incrementField.getText();
                    if (incrementText.equals(""))
                        incrementText = "0";

                    time = Integer.parseInt(minutesText) * 60 + Integer.parseInt(secondsText);
                    whiteTime = time;
                    blackTime = time;
                    increment = Integer.parseInt(incrementText);

                    panel.repaint();
                }
            }
            catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Please enter an integer", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(reset);
        buttonPanel.add(changeTime);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(1000, 500);

        frame.setVisible(true);
        frame.setResizable(true);
        frame.setFocusable(true);

        frame.requestFocus();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Timer timer = new Timer();
        timer.schedule(task, 0, 100);
    }
    public static void main(String[] args) {
        new Main().init();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        onSwitch();
        panel.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void onSwitch() {
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime-lastClick) < INTERVAL)
            return;
        lastClick = System.currentTimeMillis();

        if (Main.turn == 0)
            Main.turn = Main.WHITE;
        else if (Main.turn == Main.WHITE) {
            if (Main.whiteTime == 0)
                return;
            Main.whiteTime += increment;
            Main.turn = Main.BLACK;
        }
        else if (Main.turn == Main.BLACK) {
            if (Main.blackTime == 0)
                return;
            Main.blackTime += increment;
            Main.turn = Main.WHITE;
        }
    }
}

class Panel extends JPanel {
    final Color BACKGROUND =  new Color(40, 40, 40);

    final Color ACTIVE = new Color(50, 151, 61);
    final Color INACTIVE = new Color(57, 57, 57);

    final Color TIME_OUT = new Color(255, 0, 0);

    final int ROUND_CONSTANT = 100;
    final int edgeLength = 30;
    final int BUTTON_PANEL_HEIGHT = 125;

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mouseClicked(e);
                Main.onSwitch();
            }
        });

        final int WIDTH = Main.frame.getWidth();
        final int HEIGHT = Main.frame.getHeight();
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor((Main.whiteTime != 0)
                ? ((Main.turn == Main.WHITE) ? ACTIVE : INACTIVE)
                : TIME_OUT);
        g.fillRoundRect(edgeLength, edgeLength,
                  WIDTH/2-edgeLength*2, HEIGHT-edgeLength*2-40-BUTTON_PANEL_HEIGHT,
                  ROUND_CONSTANT, ROUND_CONSTANT);

        g.setColor((Main.blackTime != 0)
                ? ((Main.turn == Main.BLACK) ? ACTIVE : INACTIVE)
                : TIME_OUT);
        g.fillRoundRect(edgeLength+WIDTH/2, edgeLength,
                WIDTH/2-edgeLength*2, HEIGHT-edgeLength*2-40-BUTTON_PANEL_HEIGHT,
                ROUND_CONSTANT, ROUND_CONSTANT);

        g.setColor(new Color(255, 255, 255));

        String white = convert(Main.whiteTime);
        String black = convert(Main.blackTime);

        Font font = new Font("Calibri", Font.PLAIN, 70);
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);

        int whiteWidth = metrics.stringWidth(white);
        int whiteHeight = metrics.getHeight();

        int blackWidth = metrics.stringWidth(black);
        int blackHeight = metrics.getHeight();

        g.drawString(white, WIDTH/4-whiteWidth/2, HEIGHT/2-whiteHeight/2-12);
        g.drawString(black, WIDTH/2+WIDTH/4-blackWidth/2, HEIGHT/2-blackHeight/2-12);
    }

    public String convert(float time) {
        int minutes = (int)(time/60);

        String seconds;
        if (time < 20) {
            seconds = String.valueOf((float)(Math.ceil(time * 10) / 10));
            if (seconds.length() == 3)
                seconds = "0"+seconds;
        }
        else {
            seconds = String.valueOf((int) (Math.ceil(time % 60)));
            if (seconds.length() == 1)
                seconds = "0" + seconds;
            if (seconds.equals("60"))
                return (minutes + 1) + ":00";
        }

        return ""+minutes+":"+seconds;
    }
}
