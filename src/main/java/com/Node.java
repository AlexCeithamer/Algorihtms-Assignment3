package com;


public class Node {
    int number;
    //index 0 is up, index 1 is down
    Node[] edges = new Node[2];
    int[] weights = new int[2];

    public Node(int number) {
        this.number = number;
    }

    public void setEdgeAndWeight(int index, Node node, int weight) {
        if (index >= 0 && index < edges.length) {
            this.edges[index] = node;
            this.weights[index] = weight;
        }
    }

    public Node[] getEdges() {
        return edges;
    }

    public int[] getWeights() {
        return weights;
    }

    public int getUpWeight() {
        return weights[0];
    }
    public int getUpNodeNumber() {
        return edges[0].number;
    }

    public int getDownWeight() {
        return weights[1];
    }

    public int getDownNodeNumber() {
        return edges[1].number;
    }

    public int getNumber() {
        return number;
    }
    public boolean hasUpEdge() {
        return edges[0] != null;
    }

    public boolean hasDownEdge() {
        return edges[1] != null;
    }

    public boolean hasEdge() {
        return edges[0] != null || edges[1] != null;
    }

    public boolean hasTwoEdges() {
        return edges[1] != null && edges[0] != null;
    }

    
}
