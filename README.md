# MazeRunner

A toy / palette cleaner project to learn Java Swing (java-native GUI library) and give some visuals to pathfinding algorithms.

It lets you generate a mazes, and then run the mazes either step-by-step, or automatically.

![image](https://github.com/user-attachments/assets/6e761f4b-15a9-4be9-8d57-0850aa992d23)

## Interesting files

- [MainWindow.java](mazerunner/src/main/java/org/fleep/mazerunner/ui/MainWindow.java): builds all the UI components with Swing, and wires up their actions. Instantiates a Maze component.
- [Maze.java](mazerunner/src/main/java/org/fleep/mazerunner/Maze.java): generates and renders a maze, holds maze state, and implements pathfinding.
