package org.entityutils.entity.npc.movement;

import org.entityutils.entity.pathfind.Node;
import org.entityutils.utils.math.linearAlg.Math3D.Vector3;
import org.entityutils.utils.math.linearAlg.Matrix;
import org.entityutils.utils.math.linearAlg.Operable;
import org.entityutils.utils.math.linearAlg.OperableDouble;

import java.util.List;

public class CatmullRomInstruction implements Instruction {

    private static final double tension = 1D / 2;

    private static final Matrix catmullMatrix = new Matrix(new Operable[][]{
            {new OperableDouble(0), new OperableDouble(1), new OperableDouble(0), new OperableDouble(0)},
            {new OperableDouble(-tension), new OperableDouble(0), new OperableDouble(tension), new OperableDouble(0)},
            {new OperableDouble(2 * tension), new OperableDouble(tension - 3), new OperableDouble(3 - 2 * tension), new OperableDouble(-tension)},
            {new OperableDouble(-tension), new OperableDouble(2 - tension), new OperableDouble(tension - 2), new OperableDouble(tension)}
    });

    private final List<Node> nodes;
    private final int pathLength; //not needed, just clarity
    private final int stepsPerNode;


    public CatmullRomInstruction(List<Node> nodes, int stepsPerNode) {
        //ensure all nodes are on the same Y level
        boolean isValid = nodes.stream().map(Node::getY).allMatch(n -> n == nodes.get(0).getY());

        if (!isValid) {
            throw new IllegalArgumentException("Nodes must be on the same Y level");
        }
        this.stepsPerNode = stepsPerNode;

        this.nodes = nodes;
        this.pathLength = nodes.size();

        //Artificial nodes used to interpolate the end nodes of the path
        Node base = this.nodes.get(1).toVector3()
                .multiply(2)
                .add(
                        this.nodes.get(0).toVector3().multiply(-1)
                )
                .toNode(this.nodes.get(0).getWorld());

        Node end = this.nodes.get(this.nodes.size() - 1).toVector3()
                .multiply(2)
                .add(
                        this.nodes.get(this.nodes.size() - 1).toVector3().multiply(-1)
                )
                .toNode(this.nodes.get(0).getWorld());

        this.nodes.add(0, base);
        this.nodes.add(end);
    }

    @Override
    public List<Vector3> generateMovementVectors() {

        for (int i = 1; i < this.pathLength - 1; i++) { //i = 1 to start on second element
            Matrix p0 = this.nodes.get(i - 1).toVector3().toMatrix();
            Matrix p1 = this.nodes.get(i).toVector3().toMatrix();
            Matrix p2 = this.nodes.get(i + 1).toVector3().toMatrix();
            Matrix p3 = this.nodes.get(i + 2).toVector3().toMatrix();

            Matrix pointMatrix = new Matrix(new Operable[][]{
                    {p0},
                    {p1},
                    {p2},
                    {p3}
            });

            Operable evaluation = CatmullRomInstruction.catmullMatrix.multiply(pointMatrix);

            for (double j = 0; j < 1 - 1D / this.stepsPerNode; j += 1D / this.stepsPerNode) {
                Matrix independent = new Matrix(new Operable[][]{
                        {
                                new OperableDouble(1D),
                                new OperableDouble(j),
                                new OperableDouble(Math.pow(j, 2)),
                                new OperableDouble(Math.pow(j, 3))
                        }
                });

                Matrix dependant = (Matrix) independent.multiply(evaluation);
                //dependant should be a 1x3 Matrix (Point)

                double nxt = j + 1D / this.stepsPerNode;

                independent = new Matrix(new Operable[][]{
                        {
                                new OperableDouble(1D),
                                new OperableDouble(nxt),
                                new OperableDouble(Math.pow(nxt, 2)),
                                new OperableDouble(Math.pow(nxt, 3))
                        }
                });

                Matrix dependant2 = (Matrix) independent.multiply(evaluation);

                //lerp between dependant and dependant2
            }
        }


        return null;
    }
}
