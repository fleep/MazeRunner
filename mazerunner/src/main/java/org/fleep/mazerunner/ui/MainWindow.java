package org.fleep.mazerunner.ui;

import org.fleep.mazerunner.Maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {

    private final JButton btnCreateMaze;
    private final JButton btnRunMaze;
    private final JButton btnAutoRun;
    private final JComboBox comboAlgos;
    private final JTextField runSpeed;

    private Timer autoRunTimer;

    public MainWindow()
    {
        super();

        setTitle("Maze Runner!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 660);
        setLayout(null);

        // Create buttons
        btnCreateMaze = new JButton(" Generate Maze");
        btnCreateMaze.setBounds(5, 5, 150, 50);
        add(btnCreateMaze);

        btnRunMaze = new JButton(" Next Step");
        btnRunMaze.setBounds(160, 5, 150, 50);
        add(btnRunMaze);

        btnAutoRun = new JButton(" Auto Solve");
        btnAutoRun.setBounds(315, 5, 150, 50);
        add(btnAutoRun);

        comboAlgos = new JComboBox(new AlgoOption[] {
                new AlgoOption(1, "Random DFS w/ Backtracking"),
                new AlgoOption(2, "Dijkstra's Algo"),
                new AlgoOption(3, "A* Algorithm")
        });
        comboAlgos.setBounds(5, 60, 200, 25);
        add(comboAlgos);

        JLabel labelRunSpeed = new JLabel("Run speed (seconds): ");
        labelRunSpeed.setBounds(215, 60, 150, 25);
        add(labelRunSpeed);

        runSpeed = new JTextField("0.5");
        runSpeed.setBounds(375, 60, 30, 25);
        add(runSpeed);

        // Create the area where the maze will appear
        Maze maze = new Maze();
        maze.setBounds(5, 100, 450, 450);
        maze.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(maze);

        // making the window visible
        setVisible(true);

        btnCreateMaze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maze.generateMaze(10, 10);
                maze.repaint();
            }
        });

        btnRunMaze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maze.nextStep();
            }
        });

        btnAutoRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the current latency
                int speed = Double.valueOf(Double.parseDouble(runSpeed.getText()) * 1000).intValue();
                autoRunTimer.setDelay(speed);
                autoRunTimer.setRepeats(true);
                autoRunTimer.start();
            }
        });

        autoRunTimer = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (maze.state == Maze.State.WON || maze.state == Maze.State.UNSOLVABLE) {
                    System.out.println("Maze won or unsolvable.");
                    autoRunTimer.setRepeats(false);
                    autoRunTimer.stop();
                    return;
                }
                maze.nextStep();
            }
        });
    }

    class AlgoOption {
        int id;
        String label;

        public AlgoOption(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
