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
                    //otherwise set both edges in collapsing structure
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
        //2d array keeps track of the weight we have to that node (node 1 = index 0)
        //the 0 slot indicates the best weight from the node pointing up at it.
        int[][] dp = new int[numberOfNodes][2]; // [node][up/down]
        //initialize array since weight of 0 when starting at node 1 (which is index 0)
        dp[0][0] = 0;
        dp[0][1] = 0;
        for (int i = 1; i < dp.length; i++) {
            dp[i][0] = Integer.MIN_VALUE;
            dp[i][1] = Integer.MIN_VALUE;
        }

        //iterate through nodes
        for (int i = 0; i < numberOfNodes; i++) {
            Node currentNode = graph[i];

            //assign best path so far to connected nodes
            if (currentNode.hasUpEdge()) {
                int upNodeIndex = currentNode.getUpNodeNumber() - 1;

                /*
                 * Choose the best path to the next node (calculating it with the penalty if we turn)
                 * We check for MIN_VALUE because that indicates there is no path - and if we don't
                 * check this special case, we get integer overflow on the negative side
                 */
                if (dp[i][0] == Integer.MIN_VALUE) {
                    //we are assigning value to node up from us, but path came from down, so add penalty
                    dp[upNodeIndex][0] = dp[i][1] + currentNode.getUpWeight() + turnPenalty;
                }
                else if (dp[i][1] == Integer.MIN_VALUE) {
                    //assigning value to node up from us, path continues up so no penalty
                    dp[upNodeIndex][0] = dp[i][0] + currentNode.getUpWeight();
                }
                else {
                    //Assign value accordingly (same as the 2 above if statements, but choosing the max value path)
                    dp[upNodeIndex][0] = Math.max(dp[i][0] + currentNode.getUpWeight(), 
                                                dp[i][1] + currentNode.getUpWeight() + turnPenalty);
                }
            }
            if (currentNode.hasDownEdge()) {
                int downNodeIndex = currentNode.getDownNodeNumber() - 1;
/*
                 * Choose the best path to the next node (calculating it with the penalty if we turn)
                 * We check for MIN_VALUE because that indicates there is no path - and if we don't
                 * check this special case, we get integer overflow on the negative side
                 */                
                if (dp[i][1] == Integer.MIN_VALUE) {
                    //we are assigning value to node down form us, but path came from up, so add penalty
                    dp[downNodeIndex][1] = dp[i][0] + currentNode.getDownWeight() + turnPenalty;
                }
                else if (dp[i][0] == Integer.MIN_VALUE) {
                    //assigning value to node down from us, path continues down so no penalty
                    dp[downNodeIndex][1] = dp[i][1] + currentNode.getDownWeight();
                }
                else {
                    //Assign value accordingly (same as the 2 above if statements, but choosing the max value path)
                    dp[downNodeIndex][1] = Math.max(dp[i][1] + currentNode.getDownWeight(), 
                                                    dp[i][0] + currentNode.getDownWeight() + turnPenalty);
                }
                
            }
        }
        //at end of node, return the max of the path that comes from above or below - that is the solution
        return Math.max(dp[numberOfNodes - 1][0], dp[numberOfNodes - 1][1]);

    }

   

    
}
