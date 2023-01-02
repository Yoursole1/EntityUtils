package org.entityutils.utils.math.Math3D;

import lombok.Getter;

public record Matrix3(@Getter double[][] matrix) {

    public Matrix3 {
        if (matrix.length != 3 || matrix[0].length != 3) throw new IllegalArgumentException();
    }

    /**
     * Transforms the given 3D vector by this matrix
     *
     * @param toMult The vector to transform
     * @return The transformed vector
     */
    public Vector3 transform(Vector3 toMult) {
        double[] out = new double[3];

        // Loop through the rows and columns of this matrix and the vector
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Multiply each element of the matrix row by the corresponding element in the vector column, and add it to the output vector
                out[i] += this.matrix[i][j] * toMult.get(j);
            }
        }

        // Return the transformed vector
        return new Vector3(out[0], out[1], out[2]);
    }

    /**
     * Calculates the determinant of this matrix
     *
     * @return The determinant
     */
    public double det() {
        double det = 0;
        // Loop through the first row of this matrix
        for (int i = 0; i < 3; i++) {
            // Calculate the 2x2 sub-matrix by removing the first row and the ith column of this matrix
            double[][] subMatrix = getSubmatrix(0, i);

            // The determinant is the sum of the product of each element in the first row and the determinant of its corresponding sub-matrix
            // The sign of each element depends on its position in the row (1st, 2nd, or 3rd)
            det += this.matrix[0][i] * (subMatrix[0][0] * subMatrix[1][1] - subMatrix[0][1] * subMatrix[1][0]) * (2 * ((i + 1) % 2) - 1);
        }
        return det;
    }

    /**
     * Calculates the inverse of this matrix
     *
     * @return The inverse matrix
     */
    public Matrix3 inverse() {
        // Calculate the determinant of this matrix
        double det = this.det();

        // Initialize the inverse matrix
        double[][] inv = new double[3][3];

        // Loop through the rows and columns of this matrix
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Calculate the 2x2 sub-matrix by removing the ith row and the jth column of this matrix
                double[][] subMatrix = getSubmatrix(i, j);

                // The inverse matrix is the transpose of the cofactor matrix divided by the determinant
                // The cofactor matrix is the matrix of minors, where each element is the determinant of its corresponding sub-matrix
                // The sign of each element depends on its position in the matrix (1st, 2nd, or 3rd row and column)
                double coFactor = subMatrix[0][0] * subMatrix[1][1] - subMatrix[0][1] * subMatrix[1][0];
                inv[j][i] = (coFactor * (2 * ((i + j + 1) % 2) - 1)) / det;
            }
        }

        // Return the inverse matrix
        return new Matrix3(inv);
    }

    /**
     * Returns the specified sub-matrix of this matrix
     *
     * @param x The row coordinate of the rejection set
     * @param y The column coordinate of the rejection set
     * @return The 2x2 sub-matrix
     */
    public double[][] getSubmatrix(int x, int y) {
        // Initialize the sub-matrix
        double[][] sub = new double[2][2];

        // Variables for keeping track of which elements to skip
        int xPass = 0;
        int yPass = 0;

        // Loop through the rows and columns of this matrix
        for (int i = 0; i < 3; i++) {
            // Skip the specified row
            if (i == x) {
                xPass++;
                continue;
            }
            for (int j = 0; j < 3; j++) {
                // Skip the specified column
                if (j == y) {
                    yPass++;
                    continue;
                }
                // Copy the element to the sub-matrix
                sub[i - xPass][j - yPass] = this.matrix[i][j];
            }
            // Reset the column skip tracker for the next row
            yPass = 0;
        }
        // Return the sub-matrix
        return sub;
    }


}
