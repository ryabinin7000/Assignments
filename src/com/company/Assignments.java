package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Assignments {

    //BRANCH AND BOUND METHOD

    private class TreeNode {
        int lb; //lower bound of the cost for a node
        ArrayList<Integer> assignments; //current assignments for a node
        ArrayList<TreeNode> children; //direct descendants
        int[][] matrix; //initial assignment matrix

        //root constructor
        TreeNode(int[][] matrix) {
            assignments = new ArrayList<>();
            this.matrix = matrix;
            lb = getLB(this);
            //System.out.println("\nRoot: " + lb);
        }

        //node constructor
        TreeNode(TreeNode parent, int work) {
            assignments = new ArrayList<>(parent.assignments);
            assignments.add(work);
            matrix = parent.matrix;
            lb = getLB(this);
            //System.out.print("New node: ");
            //writeNode(this);
        }
    }

    //search result for the element w/ the minimal lower bound
    private static class Result {
        ArrayList<Integer> assignments;
        Integer min;
    }

    //defining the lower boundary for a node
    private int getLB(TreeNode node) {
        int lb = 0;
        Integer min = null;
        if (!node.assignments.isEmpty())
            for (int i = 0; i < node.assignments.size(); i++)
                lb += node.matrix[i][node.assignments.get(i)];

        for (int i = node.assignments.size(); i < node.matrix.length; i++) {
            for (int j = 0; j < node.matrix[i].length; j++) {
                if (node.assignments.contains(j))
                    continue;
                else {
                    min = node.matrix[i][j];
                    break;
                }
            }

            if (min != null) {
                for (int j = 0; j < node.matrix[i].length; j++)
                    if (!node.assignments.contains(j) && node.matrix[i][j] < min)
                        min = node.matrix[i][j];
                lb += min;
            }
        }
        return lb;
    }

    //creating descendants for the most promising node
    private void makeChildren(TreeNode node, Result result) {
        if (node.children == null) {
            if (node.assignments == result.assignments) {
                node.children = new ArrayList<>();
                for (int i = 0; i < node.matrix.length; i++)
                    if (!node.assignments.contains(i))
                        node.children.add(new TreeNode(node, i));
                if(node.assignments.size() == node.matrix.length - 2){
                    int min = node.children.get(0).lb;
                    for(int i = 1; i<node.children.size(); i++)
                        if(node.children.get(i).lb < min)
                            min = node.children.get(i).lb;
                    for(int i = 0; i<node.children.size(); i++)
                        if(node.children.get(i).lb != min) {
                            //System.out.print("Remote node: ");
                            //writeNode(node.children.get(i));
                            node.children.remove(i);
                        }
                }
            }
        }
        else
            for (TreeNode child : node.children)
                makeChildren(child, result);
    }

    //defining the most promising node (which has a minimal lb)
    private Result findPromisingNode(TreeNode node, Result result) {
        if (node.children == null) {
            if (result.min == null) {
                result.min = node.lb;
                result.assignments = node.assignments;
            }
            else if (node.lb < result.min || (node.lb == result.min &&
                    node.assignments.size() > result.assignments.size())) {
                result.min = node.lb;
                result.assignments = node.assignments;
            }
        }
        else {
            for (TreeNode child : node.children)
                findPromisingNode(child, result);
        }
        return result;
    }

    private void branchAndBoundMethod(int[][] c) {
        System.out.println("\nBRANCH AND BOUND METHOD\n" +
                "Assignment matrix:");
        for(int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[i].length; j++)
                System.out.print(c[i][j] + " ");
            System.out.println();
        }
        TreeNode node = new TreeNode(c);
        Result result = new Result();
        do {
            makeChildren(node, result);
            result = new Result();
            result = findPromisingNode(node, result);
            //System.out.println("Minimum per iteration: " + result.min);
            //System.out.println();
        }
        while (result.assignments.size() < c.length - 1);
        System.out.println("Optimal task assignment:");
        for(int i = 0; i<node.matrix.length; i++){
            if(!result.assignments.contains(i))
                result.assignments.add(i);
            writeNumber(result.assignments.get(i));
        }
        System.out.println("\nTotal cost: " + result.min);
    }

    //output of assignments and lb
    private static void writeNode(TreeNode node){
        for(int i = 0; i<node.assignments.size(); i++)
            System.out.print((char)('a'+i) + "-" +
                    (node.assignments.get(i)+1) + ", ");
        System.out.println("lb=" + node.lb);
    }

    //BRUTE FORCE METHOD
    //uses Narayana's algorithm to generate permutations

    private void bruteForceMethod(int[][] c){
        int j, l, temp, minSum = 0, currentSum,
                n = c.length;
        long swapCount = 1;
        int[] currentSwap = new int[n],
                minSwap = new int[n], tempArray;

        for(int i = 0; i<n; i++) {
            currentSwap[i] = i;
            minSwap[i] = i;
            minSum += c[i][i];
        }

        for(int i = 2; i<=n; i++)
            swapCount*=i;

        for(int i = 1; i<swapCount; i++){
            for(j = n-2; j>=0; j--)
                if(currentSwap[j] < currentSwap[j+1])
                    break;
            for(l = n-1; l>j; l--)
                if(currentSwap[l] > currentSwap[j])
                    break;

            temp = currentSwap[j];
            currentSwap[j]=currentSwap[l];
            currentSwap[l]=temp;

            tempArray = new int[n-j-1];
            for(int a = 0; a<n-j-1; a++)
                tempArray[a] = currentSwap[n-a-1];
            for(int a = j+1; a<n; a++)
                currentSwap[a] = tempArray[a-j-1];

            currentSum = 0;
            for(int a = 0; a<n; a++)
                currentSum += c[a][currentSwap[a]];
            if(currentSum < minSum){
                minSum = currentSum;
                for(int a = 0; a<n; a++)
                    minSwap[a]=currentSwap[a];
            }
        }
        System.out.println("\nBRUTE FORCE METHOD\n" +
                "Assignment matrix:");
        for(int i = 0; i < c.length; i++) {
            for (int k = 0; k < c[i].length; k++)
                System.out.print(c[i][k] + " ");
            System.out.println();
        }
        System.out.println("Optimal task assignment:");
        for(int i = 0; i<n; i++)
            writeNumber(minSwap[i]);
        System.out.println("\nTotal cost: " + minSum);
    }

    //number output when counting from one (lol)
    private static void writeNumber(int a){
        System.out.print(++a + " ");
        a--;
    }

    //reading a matrix from a .txt file
    private static int[][] readMatrix(){
        try {
            File input = new File("input.txt");
            Scanner scanner = new Scanner(input);
            String[] temp = scanner.nextLine().split(" ");
            int n = temp.length;
            int[][] c = new int[n][n];
            for(int i = 0; i<n; i++)
                c[0][i] = Integer.parseInt(temp[i]);
            for(int i = 1; i<n; i++) {
                temp = scanner.nextLine().split(" ");
                for (int j = 0; j < n; j++)
                    c[i][j] = Integer.parseInt(temp[j]);
            }
            return c;
        }
        catch (Exception e){
            System.out.println(e.toString());
            return new int[0][0];
        }
    }

    //creating a random matrix of size n for testing
    static int[][] getRandomMatrix(int n){
        Random random = new Random();
        int[][] c = new int[n][n];
        for(int i = 0; i<n; i++)
            for(int j = 0; j<n; j++)
                c[i][j] = random.nextInt(1, 20);
        return c;
    }

    //method for analysing time costs of 2 methods
    //maxSize - maximum size of tested matrices (starting from 3)
    //accuracy - number of tested matrices of the same size
    static void timeAnalyses(int maxSize, int accuracy){
        Assignments a = new Assignments();
        ArrayList<Long> BFTestsTime, BABTestsTime;
        ArrayList<Double> BFFinalTime = new ArrayList<>(),
                BABFinalTime = new ArrayList<>();
        int[][] randomCMatrix;
        long startTime, endTime;
        for(int i = 3; i<=maxSize; i++){
            BFTestsTime = new ArrayList<>();
            BABTestsTime = new ArrayList<>();
            for(int j = 0; j<accuracy; j++){
                randomCMatrix = getRandomMatrix(i);
                startTime = System.currentTimeMillis();
                a.bruteForceMethod(randomCMatrix);
                endTime = System.currentTimeMillis();
                BFTestsTime.add(endTime - startTime);
                startTime = System.currentTimeMillis();
                a.branchAndBoundMethod(randomCMatrix);
                endTime = System.currentTimeMillis();
                BABTestsTime.add(endTime - startTime);
            }
            int sumBF = 0, sumBAB = 0;
            for (int j = 0; j<accuracy; j++) {
                sumBF += BFTestsTime.get(j);
                sumBAB += BABTestsTime.get(j);
            }
            BFFinalTime.add((double)sumBF/accuracy);
            BABFinalTime.add((double)sumBAB/accuracy);
        }
        System.out.println("\nBrute force method / Branch and bound method");
        for(int i = 3; i<=maxSize; i++)
            System.out.println("Size " + i + ": " +
                    BFFinalTime.get(i-3) + " / " +
                    BABFinalTime.get(i-3));
    }

    public static void main(String[] args) {
        Assignments assignments = new Assignments();
        int[][] C = readMatrix();
        assignments.bruteForceMethod(C);
        assignments.branchAndBoundMethod(C);
        //timeAnalyses(10, 8);
    }
}