/*
* TCSS-554A : Homework 02
* Implementing page rank algorithm
* Pradnya Bhumkar
* */

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;


public class Assignment2 {

    double BITA = 0.85; //Bita value common for all functions in class.

    //This function reads the graph file and created the adjacency matrix.
    public double[][] readAdjacencyMatrix()
    {
        String line = null;
        int i=0,j = 0;
        double adjacencyMatrix[][] = new double[6][6];
        File adjMatrix = new File("graph.txt");
        try {
            Scanner s = new Scanner(adjMatrix);
            while (s.hasNext()) {
                line = s.nextLine();
                String tempArray[] = line.split(" ");

                adjacencyMatrix[Integer.parseInt(tempArray[1])][Integer.parseInt(tempArray[0])] = Integer.parseInt(tempArray[2]);

            }

        } catch (IOException e) {
            System.out.println("Error accessing input file!");
        }
        return adjacencyMatrix;
    }


    //This function calculates the number of out going links for each nodes in graph.
    //Each node represent the links going out from a node represented by array index.
    // Index 0=A, 1=B, 2=C, 3=D, 4=E, 5=F
    public int[] numberOfOutLinks(double[][] adjacencyMatrix)
    {
        int numberOfOutLinks[] = {0,0,0,0,0,0};

        for(int i = 0; i < adjacencyMatrix.length; i++)
        {
            for(int j = 0; j < adjacencyMatrix.length; j++)
            {
               if(adjacencyMatrix[i][j] == 1)
               {
                   numberOfOutLinks[j] = numberOfOutLinks[j] + 1;
               }
            }
        }
        return numberOfOutLinks;
    }

    //This function convert the input adjacency matrix values based on the number of out links for that node.
    //and return the converted adjacency matrix M.
    public double[][] convertMatrixValue(double[][] adjacencyMatrix)
    {
        NumberFormat nf = new DecimalFormat("#0.0000");
        int[] numberOfOutLinks =  numberOfOutLinks(adjacencyMatrix);

        for(int i = 0; i < adjacencyMatrix.length; i++)
        {
            for(int j = 0; j < adjacencyMatrix.length; j++)
            {
                if(adjacencyMatrix[i][j] == 1)
                {
                    adjacencyMatrix[i][j] = Double.parseDouble (nf.format ((adjacencyMatrix[i][j])/numberOfOutLinks[j]));
                }
            }
        }
        return adjacencyMatrix;
    }

    //this function creates Matrix A = 𝐴 = 𝛽*𝑀 + (1 − 𝛽)*(1/n)*e
    //note that we are using converted matrix M
    //e is unit matrix which has all element as 1
    public double[][] matrixA(double[][] adjacencyMatrix)
    {
        NumberFormat nf = new DecimalFormat("#0.0000");
        double[][] matrixA = new double[adjacencyMatrix.length][adjacencyMatrix.length];

        for(int i = 0; i < adjacencyMatrix.length; i++)
        {
            for(int j = 0; j < adjacencyMatrix.length; j++)
            {
                matrixA[i][j] = Double.parseDouble(nf.format ((this.BITA * adjacencyMatrix[i][j])+((1-this.BITA)*(1/(double)adjacencyMatrix.length)*1)));
            }
        }
        return matrixA;
    }

    //This function will print the matrix
    //it also round up the decimal point up to 4 digit.
    public void printMatrix(double[][] matrix)
    {
        NumberFormat nf = new DecimalFormat("#0.0000");

        for(int i = 0; i<matrix.length; i++)
        {
            System.out.print("[");
            for(int j = 0; j<matrix.length;j++)
            {
                System.out.print(nf.format(matrix[i][j]) + "\t");
            }
            System.out.print("]\n");
        }
    }

    //This function creates the base rank vector by giving all elements value as 1/n
    public double[] baseRankVector(double[][] adjacencyMatrix)
    {
        NumberFormat nf = new DecimalFormat("#0.0000");
        double[] baseRankVector = new double[adjacencyMatrix.length];
        for(int i =0; i<adjacencyMatrix.length; i++)
        {
            baseRankVector[i] = Double.parseDouble(nf.format (1/(double)adjacencyMatrix.length));
        }
        return baseRankVector;
    }

    //this function is used for printing the vector array.
    //it also round up the decimal point up to 4 digit.
    public void printVector(double[] vector)
    {
        NumberFormat nf = new DecimalFormat("#0.0000");
        System.out.print("[");
        for(int i = 0; i<vector.length;i++)
        {
            System.out.print(nf.format(vector[i]) + "\t");
        }
        System.out.print("]\n");
    }

    //this function multiply the rank vector and input matrix(can be A or M)
    //it returns the new Rank vector.
    public double[] multipyRankVectorM(double[][] adjacencyMatrix,double[] vector)
    {
        NumberFormat nf = new DecimalFormat("#0.0000");
        double[] RankVectorM = new double[adjacencyMatrix.length];
        double sum=0;

        for (int i =0; i<adjacencyMatrix.length;i++)
        {

            for (int j =0; j<adjacencyMatrix.length;j++)
            {
                sum = sum + Double.parseDouble (nf.format((double) adjacencyMatrix[i][j] * (double) vector[j]));
            }
            RankVectorM[i] = sum;
            sum = 0;
        }
        return RankVectorM;
    }

    //check if the both vectors are same or not.
    //calculate the difference.
    public double matrixEquality(double[] vector1, double[] vector2 )
    {
        NumberFormat nf = new DecimalFormat("#0.00000");
        double diff=0;
        for (int i=0;i<vector1.length;i++)
        {
            diff = diff + Double.parseDouble(nf.format(Math.abs((double) vector1[i] - (double) vector2[i])));
            //diff = diff + ((double) vector1[i] - (double) vector2[i]);
        }
        return Math.abs(diff);
    }

    //this function checks if vector at i and i++ are same or not.
    //if same we get our R' else we multiply rank vector i+1 and M/A to get next vector i+1
    // calculate the no of iteration required to get vector 1 nearly equal to vector2
    public double[] convertRankVector(double[][] adjacencyMatrix, double[] rankVector)
    {
        int noOfIteration = 0;

        double[] vectorR =  multipyRankVectorM(adjacencyMatrix,rankVector);
        noOfIteration++;
        while(matrixEquality(rankVector,vectorR) >= 0.0001)
        {
            noOfIteration = noOfIteration +1;
            rankVector = vectorR;
            vectorR =  multipyRankVectorM(adjacencyMatrix,rankVector);
        }
        rankVector = vectorR;

        System.out.println("No of Iterations : " +noOfIteration);

        return rankVector;
    }


    public static void main(String[] args)
    {
        double adjacencyMatrix[][];
        double convertedAdjacencyMatrix[][];
        double matrixA[][];
        double baseRankVector[];
        double rankVectorForM[];
        double rankVectorForA[];

        Assignment2 as = new Assignment2();
        adjacencyMatrix = as.readAdjacencyMatrix();
        convertedAdjacencyMatrix = as.convertMatrixValue(adjacencyMatrix);
        matrixA = as.matrixA(convertedAdjacencyMatrix);

        System.out.println("What is the output for Matrix M?");
        as.printMatrix(convertedAdjacencyMatrix);

        System.out.println("\nWhat is the output of Matrix A? After applying teleportation");
        as.printMatrix(matrixA);

        System.out.println("\nWhat is the original rank vector (R)?");
        baseRankVector = as.baseRankVector(adjacencyMatrix);
        as.printVector(baseRankVector);


        System.out.println("\nHow many  iterations did it take to get the convergence? When you use Matrix M");
        rankVectorForM = as.convertRankVector(convertedAdjacencyMatrix,baseRankVector);
        System.out.println("\nWhat is the Converged rank vector (R')? When you use Matrix M");
        as.printVector(rankVectorForM);


        System.out.println("\nHow many  iterations did it take to get the convergence? When you use Matrix A");
        rankVectorForA = as.convertRankVector(matrixA,baseRankVector);
        System.out.println("\nWhat is the Converged rank vector (R')? When you use Matrix A");
        as.printVector(rankVectorForA);

    }
}
