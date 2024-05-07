package org.example;

import mpi.MPI;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();

        String matriceA = "A.txt";
        String matriceB = "B.txt";
        String matriceResult = "src/main/resources/Result.txt";

        if (rank == 0) {
            // Chargement des matrices pour le test
            double[][] A = readMatrixFromResource(matriceA);
            double[][] B = readMatrixFromResource(matriceB);
            mult(matriceA, matriceB, matriceResult);
        }

        MPI.Finalize();
    }

    public static double[][] readMatrixFromResource(String resourcePath) throws IOException {
        InputStream in = Main.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
        }
        List<double[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                double[] row = Arrays.stream(line.trim().split("\\s+"))
                        .mapToDouble(Double::parseDouble)
                        .toArray();
                lines.add(row);
            }
        }
        return lines.toArray(new double[lines.size()][]);
    }

    public static void mult(String matriceA, String matriceB, String matriceResult) throws Exception {
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        double[][] A = readMatrixFromResource(matriceA);
        double[][] B = readMatrixFromResource(matriceB);

        int aRows = A.length;
        int aCols = A[0].length;
        int bCols = B[0].length;

        double[][] result = new double[aRows][bCols];

        // matrix multiplication
        for (int i = rank; i < aRows; i += size) {
            for (int j = 0; j < bCols; j++) {
                for (int k = 0; k < aCols; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        // resultat de la multplication
        if (rank == 0) {
            for (int source = 1; source < size; source++) {
                double[][] temp = new double[aRows][bCols];
                MPI.COMM_WORLD.Recv(temp, 0, aRows, MPI.OBJECT, source, 99);
                // Combine results
                for (int i = 0; i < aRows; i++) {
                    System.arraycopy(temp[i], 0, result[i], 0, bCols);
                }
            }
            writeMatrixToFile(result, matriceResult);
        } else {
            MPI.COMM_WORLD.Send(result, 0, aRows, MPI.OBJECT, 0, 99);
        }
    }

    public static void writeMatrixToFile(double[][] matrix, String resourcePath) throws IOException {
        // enregistrement des resultats
        File file = new File(resourcePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (double[] row : matrix) {
                for (double val : row) {
                    writer.write(val + " ");
                }
                writer.newLine();
            }
        }
    }
}
