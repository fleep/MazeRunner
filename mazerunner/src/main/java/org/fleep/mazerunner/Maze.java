package org.fleep.mazerunner;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Set;

public class Maze extends JPanel {
    public int[][] maze;

    /*
    // Allow diagonals
    // Uncomment this to allow diagonals. We could throw this into a static method but I don't really care right now.
    int[][] allowedDirections = new int[][] {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };
    */

    int[][] allowedDirections = new int[][] {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};

    // Current maze state
    public enum State {
        NEW,
        RUNNING,
        WON,
        UNSOLVABLE
    };
    public State state = State.NEW;

    // Maze container dimensions
    int renderWidth = 450;
    int renderHeight = 450;
    final int BORDER_SIZE = 1;

    // Calculated sizes
    int cellSize;   // The length in pixels of the side of a cell square including the border.
    int fillSize;   // The length in pixels of the side of a cell square excluding the border.

    // To simplify comparisons, store the coordinates of the start/end cells
    RowCol startCell = new RowCol(0, 0);
    RowCol finishCell;

    // Potential contents of each cell in the maze grid
    public final int OUT_OF_BOUNDS = -1;
    public final int EMPTY = 0;
    public final int WALL = 1;

    // Pathfinding algo properties
    RowCol currentNode;
    List<RowCol> path;
    Set<RowCol> visited;

    public Maze() {
        super();
        generateMaze(10, 10);
    }

    public Maze(int width, int height, int rows, int cols) {
        this.renderWidth = width;
        this.renderHeight = height;
        generateMaze(rows, cols);
    }

    /**
     * Regenerate a maze and reset all state.
     * @param rows
     * @param cols
     */
    public void generateMaze(int rows, int cols) {
        System.out.println("Generating new maze!");
        maze = new int[rows][cols];
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                // 30% chance of a wall
                maze[row][col] = (Math.floor(Math.random() * 100) < 30) ? 1 : 0;
            }
        }

        // First and last must be empty
        maze[0][0] = 0;
        maze[maze.length - 1][maze[0].length - 1] = 0;

        // reinitialize everything
        state = State.NEW;
        currentNode = startCell;
        finishCell = new RowCol(rows - 1, cols - 1);
        path = new ArrayList<RowCol>();
        path.add(currentNode);
        visited = new HashSet<RowCol>();
        visited.add(currentNode);

        cellSize = renderWidth / maze.length;
        fillSize = cellSize - BORDER_SIZE * 2;
    }


    /**
     * Take another step in solving this maze. Main implementation of pathfinding
     * algorithms.
     */
    public void takeStep() {
        if (state == State.UNSOLVABLE || state == State.WON) {
            return;
        }

        state = Maze.State.RUNNING;

        // TODO: replace with logging
        System.out.println("Next step!");
        System.out.println( "Current: "+ currentNode);
        System.out.println( "Current: "+ currentNode);

        // Find a next place to go and advance there (random for now)
        // Generate a list of all valid possible directions to go
        List<RowCol> neighbors = new ArrayList<>();
        for (int[] direction : allowedDirections) {
            System.out.println("direction: (" + direction[0] + "," + direction[1] + ")");
            RowCol tryDir = new RowCol(currentNode.getRow() + direction[0], currentNode.getCol() + direction[1]);
            int cellVal = getCell(tryDir);
            if (cellVal != -1  // Out of bounds
                && cellVal != WALL
                && !visited.contains(tryDir)
            ) {
                System.out.println("Valid! " + tryDir);
                neighbors.add(tryDir);
            }
        }

        // If there are no available neighbors, back up the path by 1 and try again with any unvisited nodes.
        // There is never a need to revisit a node outside of backtracking the path.
        if (neighbors.size() == 0) {
            path.removeLast();
            if (path.isEmpty()) {
                state = State.UNSOLVABLE;
                // todo show an "unsolvable" message
                System.out.println("Unsolvable!");
            }
            else {
                currentNode = path.getLast();
            }

            invalidate();
            repaint();
            return;
        }

        // For now, choose a direction at random. Changing this is one of the key differences between algos.
        RowCol nextNode = neighbors.get((int) Math.floor(Math.random() * neighbors.size()));
        System.out.println(neighbors);
        System.out.println(nextNode);

        path.add(nextNode);
        visited.add(nextNode);
        currentNode = nextNode;

        // If we've reached the end, change state
        if (currentNode.equals(finishCell)) {
            System.out.println("Solved!");
            state = State.WON;
            // todo show a success message
        }

        repaint();
    }


    /**
     * Draw the maze. This is largely using Graphics2D, part of Java's native AWT (Abstract Window Toolkit) library.
     * Currently, any repaint will redraw the entire maze.
     *
     * todo Consider breaking each cell down into smaller JPanel subcomponents so we can redraw only the parts that are necessary.
     *
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Draw the grid.
        for (int row = 0, rows = maze.length; row < rows; ++row) {
            for (int col = 0, cols = maze[0].length; col < cols; ++col) {
                g2d.setColor(Color.BLACK);
                g2d.fill(new Rectangle(getX(col), getY(row), cellSize, cellSize));
                if (maze[row][col] == 0) {
                    g2d.setColor(Color.WHITE);
                    g2d.fill(new Rectangle(getX(col) + BORDER_SIZE, getY(row) + BORDER_SIZE, cellSize - BORDER_SIZE * 2, cellSize - BORDER_SIZE * 2));
                }
            }
        }

        // Draw origin
        g2d.setColor(Color.BLUE);
        g2d.fill(new Rectangle(0, 0, cellSize, cellSize));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Start", 8, cellSize - 5);

        // Draw destination
        Color darkGreen = new Color(29, 130, 0);
        g2d.setColor(darkGreen);
        g2d.fill(new Rectangle(getX(finishCell.getCol()), getY(finishCell.getRow()), cellSize, cellSize));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Finish", getX(finishCell.getCol()) + 6, getY(finishCell.getRow()) + cellSize - 10);

        // Draw the current node
        g2d.setColor(Color.RED);
        int playerOffset = cellSize / 4;
        g2d.fill(new Ellipse2D.Double(getX(currentNode.getCol()) + playerOffset, getY(currentNode.getRow()) + playerOffset, cellSize - playerOffset * 2, cellSize - playerOffset * 2));

        // Draw the current path
        Stroke stroke = new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,10.0f, new float[] {10.0f}, 0);
        g2d.setStroke(stroke);
        for (int i = 1; i < path.size(); ++i) {
            int[] current = centerXY(path.get(i));
            int[] prev = centerXY(path.get(i - 1));

            g2d.draw(new Line2D.Double(prev[0], prev[1], current[0], current[1]));
        }
    }

    private int getX(int col) {
        return col * cellSize;
    }

    private int getY(int row) {
        return row * cellSize;
    }

    private int getFillX(int col) {
        return getX(col) + BORDER_SIZE;
    }

    private int getFillY(int row) {
        return getY(row) + BORDER_SIZE;
    }

    /**
     * Return the value in the cell, or a -1 (OUT_OF_BOUNDS) if it's out of bounds
      * @param rowCol
     * @return
     */
    private int getCell(RowCol rowCol) {
        if (rowCol.getRow() < 0 || rowCol.getCol() < 0
                || rowCol.getRow() >= maze.length
                || rowCol.getCol() >= maze[0].length) {
            return OUT_OF_BOUNDS;
        }
        return maze[rowCol.getRow()][rowCol.getCol()];
    }

    /**
     * Find the center of a cell. Useful for drawing lines and positioning the runner.
     * @param rowCol
     * @return
     */
    private int[] centerXY(RowCol rowCol) {
        return new int[] {
                getX(rowCol.getCol()) + (cellSize / 2),
                getX(rowCol.getRow()) + (cellSize / 2)
        };
    }
}
