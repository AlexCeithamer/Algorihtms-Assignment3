package com;

import java.io.*;
import java.util.*;

public class MountainMarathon {

    private int turnPenalty;
    private int numberOfNodes;
    private Node[] graph;


    public static void main(String[] args) {
        MountainMarathon marathon = new MountainMarathon();

        try {
            marathon.ReadInput("input.txt");
            System.out.println("Best route: " + marathon.FindBestRoute());
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    public void ReadInput(String filename) throws IOException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        //parse file into array split by spaces
        String[] input = scanner.nextLine().split(" ");
        scanner.close();
        int[] data = new int[input.length - 2];
        for (int i = 0; i < input.length - 2; i++) {
            data[i] = Integer.parseInt(input[i + 2]);
        }

        turnPenalty = Integer.parseInt(input[0]);
        numberOfNodes = Integer.parseInt(input[1]);

        CreateGraph(data);
        PrintGraph();
    }

    public void PrintGraph() {
        for (Node i : graph) {
            System.out.print("Node: " + i.getNumber());
            if (i.hasEdge()) {
                System.out.print(" | " + "Up: " + i.getUpNode() + " " + i.getUpWeight());
            } else {
                System.out.print(" | " + "Up: " + "null" + " " + "null");
            }

            if (i.hasTwoEdges()) {
                System.out.print(" | " + "Down: " + i.getDownNode() + " " + i.getDownWeight());
            } else {
                System.out.print(" | " + "Down: " + "null" + " " + "null");
            }
            System.out.println();
        }
    }


    public void CreateGraph(int[] data) {
        graph = new Node[numberOfNodes];

        // Create nodes
        for (int i = 0; i < numberOfNodes; i++) {
            graph[i] = new Node(i + 1);
        }

        // Connect nodes with edges
        int weightIndex = 0; // Start with the first weight in the data array after turnPenalty and numberOfNodes
        int nodeIndex = 0;
        int level;
        int midLevel = (int)Math.sqrt((double)numberOfNodes);
        boolean pastHalfway = false;

        for (level = 1; level != 0;) {
            for (int i = 0; i < level; i++, nodeIndex++) {
                Node currentNode = graph[nodeIndex];
                //if not past halfway, set both edges in expanding structure
                if (!pastHalfway) {
                    currentNode.setEdgeAndWeight(0, graph[nodeIndex + level], data[weightIndex++]);
                    currentNode.setEdgeAndWeight(1, graph[nodeIndex + level + 1], data[weightIndex++]);
                    //at end of incrementing through the level, increment level or declare past halfway
                    if (i == level - 1) {
                        //if not at the middle level, increment level
                        if (level < midLevel - 1) {
                            level++;
                            nodeIndex++;
                            break;
                        } 
                        //if at the middle level, set only the down edge so we start collapsing
                        else {
                            level ++;
                            nodeIndex++;
                            pastHalfway = true;
                            break;
                        }
                    }
                } else {
                    //if top node, set only the down edge
                     if (i == 0) {
                        currentNode.setEdgeAndWeight(1, graph[nodeIndex + level], data[weightIndex++]);
                    } 
                    //if bottom node set only the up edge
                    else if (i == level - 1) {
                        currentNode.setEdgeAndWeight(0, graph[nodeIndex + level - 1], data[weightIndex++]);
                    }
                    //othereise set both edges in collapsing structure
                    else {
                        currentNode.setEdgeAndWeight(0, graph[nodeIndex + level - 1], data[weightIndex++]);
                        currentNode.setEdgeAndWeight(1, graph[nodeIndex + level], data[weightIndex++]);
                    }
                    //end of iterating through the level, so we either decrement level or set it to 0 if we have reached the end
                    if (i == level - 1) {
                        level--;
                        nodeIndex++;
                        if (level == 1) {
                            level = 0;
                        }
                        break;
                    }
                }
            }
        }
    }


    // This method should contain the algorithm to find the best route
    public int FindBestRoute() {
        // Placeholder for the path-finding algorithm (e.g., Dijkstra's or Bellman-Ford)
        
        return Integer.MIN_VALUE;
    }

   

    
}
