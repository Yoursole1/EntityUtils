package org.entityutils.utils.math;

public class Matrix3{
    private final double[][] matx;

    public Matrix3(double[][] matx){
        if(matx.length != 3 || matx[0].length != 3) throw new IllegalArgumentException();

        this.matx = matx;
    }

    public Vector3 transform(Vector3 toMult){
        double[] out = new double[3];

        for(int i = 0; i < 3; i++) for(int j = 0; j < 3; j++) {
            out[i] += this.matx[i][j] * toMult.get(j);
        }
        return new Vector3(out[0], out[1], out[2]);
    }

    public double det(){
        double det = 0;
        for (int i = 0; i < 3; i++) {
            double[][] subMatrix = getSubmatrix(0, i);
            det += this.matx[0][i] * (subMatrix[0][0] * subMatrix[1][1] - subMatrix[0][1] * subMatrix[1][0]) * (2 * ((i + 1) % 2) - 1);
        }
        return det;
    }

    public Matrix3 inverse(){
        double det = this.det();
        double[][] inv = new double[3][3];

        for (int i = 0; i < 3; i++) for(int j = 0; j < 3; j++){
            double[][] subMatrix = getSubmatrix(i, j);

            double coFactor = subMatrix[0][0] * subMatrix[1][1] - subMatrix[0][1] * subMatrix[1][0];
            inv[j][i] = (coFactor * ( 2 * ((i + j + 1) % 2) - 1)) / det;
        }

        return new Matrix3(inv);
    }

    /**
     * note that x and y are the coordinates of the rejection set
     * @param x
     * @param y
     * @return
     */
    public double[][] getSubmatrix(int x, int y){
        double[][] sub = new double[2][2];

        int xPass = 0;
        int yPass = 0;

        for(int i = 0; i < 3; i++) {
            if (i == x) {
                xPass++;
                continue;
            }
            for (int j = 0; j < 3; j++) {
                if (j == y) {
                    yPass++;
                    continue;
                }
                sub[i - xPass][j - yPass] = this.matx[i][j];
            }
            yPass = 0;
        }
        return sub;
    }

    public double[][] getMatrix(){
        return this.matx;
    }

}
