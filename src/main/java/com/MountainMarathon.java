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
        //parse our string array into an int array without the first two elements (turn penalty and number of nodes)
        for (int i = 0; i < input.length - 2; i++) {
            data[i] = Integer.parseInt(input[i + 2]);
        }

        turnPenalty = Integer.parseInt(input[0]);
        numberOfNodes = Integer.parseInt(input[1]);

        CreateGraph(data);
        //PrintGraph();
    }

    public void PrintGraph() {
        for (Node i : graph) {
            System.out.print("Node: " + i.getNumber());
            if (i.hasEdge()) {
                System.out.print(" | " + "Up: " + i.getUpNodeNumber() + " " + i.getUpWeight());
            } else {
                System.out.print(" | " + "Up: " + "null" + " " + "null");
            }

            if (i.hasTwoEdges()) {
                System.out.print(" | " + "Down: " + i.getDownNodeNumber() + " " + i.getDownWeight());
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

    public int FindBestRoute() {
        //2d array keeps track of the weight we have to that node if we come from above or below
        int[] dp = new int[numberOfNodes]; // [node][up/down]
        //keep track of the current direction, so that if we switch direction, we can add the turn penalty
        int[] direction = new int[numberOfNodes]; // 0 = up, 1 = down
        //initialize array since weight of 0 when starting at node 1 (which is index 0)
        dp[0] = 0;
        direction[0] = -1;
        for (int i = 1; i < dp.length; i++) {
            dp[i] = Integer.MIN_VALUE;
            direction[i] = -1;
        }

        //iterate through nodes
        for (int i = 0; i < numberOfNodes; i++) {
            Node currentNode = graph[i];

            //assign best path so far to connected nodes
            if (currentNode.hasUpEdge()) {
                int upNodeIndex = currentNode.getUpNodeNumber() - 1;
                //update weight of next node depending on if we are going up or down and if our current path is better
                if (dp[i] + currentNode.getUpWeight() > dp[upNodeIndex] && direction[i] != 1) {
                    //current node is going up, so no penalty for continuing upward direction
                    dp[upNodeIndex] = dp[i] + currentNode.getUpWeight();
                    direction[upNodeIndex] = 0;
                }
                else if (dp[i] + currentNode.getUpWeight() + turnPenalty > dp[upNodeIndex] && direction[i] == 1) {
                    //current node is going down, so penalty for switching directions
                    dp[upNodeIndex] = Math.max(dp[upNodeIndex], dp[i] + currentNode.getUpWeight() + turnPenalty);
                    direction[upNodeIndex] = 0;
                    
                }
            }
            if (currentNode.hasDownEdge()) {
                int downNodeIndex = currentNode.getDownNodeNumber() - 1;
                //update weight of next node depending on if we are going up or down and if our current path is better
                if (dp[i] + currentNode.getUpWeight() > dp[downNodeIndex] && direction[i] != 0) {
                    //current node is going down, so no penalty for continuing downward direction
                    dp[downNodeIndex] = dp[i] + currentNode.getDownWeight();
                    direction[downNodeIndex] = 1;
                }
                else if (dp[i] + currentNode.getDownWeight() + turnPenalty > dp[downNodeIndex] && direction[i] == 0) {
                    //current node is going up, so penalty for switching directions
                    dp[downNodeIndex] = Math.max(dp[downNodeIndex], dp[i] + currentNode.getDownWeight() + turnPenalty);
                    direction[downNodeIndex] = 1;
                }
                
            }
        }
        return dp[numberOfNodes - 1];

    }
    /* 
    // This method should contain the algorithm to find the best route
    public int FindBestRoute() {
        int[][] dp = new int[numberOfNodes][2]; // [node][up/down

        //initialize dp array with base cases (0 because thats what we start off with)
        dp[0][0] = 0;
        dp[0][1] = 0;
        
        for (int i = 0; i < numberOfNodes; i++) {
            Node currentNode = graph[i];
            int upWeight = currentNode.getUpWeight();
            int downWeight = currentNode.getDownWeight();

            if (currentNode.hasUpEdge()) {
                //upNodeIndex is set to the number of the next node, which when putting into an array
                //it should subtract 1 to put into the array. 
                int upNodeIndex = currentNode.getUpNode() - 1;
                dp[upNodeIndex][0] = Math.max(dp[upNodeIndex][0], dp[i][0] + upWeight);
                dp[upNodeIndex][1] = Math.max(dp[upNodeIndex][1], dp[i][1] + upWeight + turnPenalty);
            }

            if (currentNode.hasDownEdge()) {
                int downNodeIndex = currentNode.getDownNode() - 1;
                dp[downNodeIndex][0] = Math.max(dp[downNodeIndex][0], dp[i][0] + downWeight + turnPenalty);
                dp[downNodeIndex][1] = Math.max(dp[downNodeIndex][1], dp[i][1] + downWeight);
            }

        }
        return Math.max(dp[numberOfNodes - 1][0], dp[numberOfNodes - 1][1]);
    } 
    */

   

    
}
