package org.entityutils.entity.npc.movement;

import org.entityutils.entity.pathfind.Node;
import org.entityutils.utils.math.linearAlg.Math3D.Vector3;
import org.entityutils.utils.math.linearAlg.Matrix;

import java.util.List;

public class CatmullRomInstruction implements Instruction {

    private static final double tension = 1D/2;

    private static final Matrix catmullMatrix = new Matrix(new double[][]{
            {0, 1, 0, 0},
            {-tension, 0, tension ,0},
            {2*tension, tension - 3, 3 - 2*tension, -tension},
            {-tension, 2 - tension, tension - 2, tension}
    });

    private final List<Node> nodes;
    private final int pathLength; //not needed, just clarity


    public CatmullRomInstruction(List<Node> nodes){
        //ensure all nodes are on the same Y level
        boolean isValid = nodes.stream().map(Node::getY).allMatch(n -> n == nodes.get(0).getY());

        if(!isValid){
            throw new IllegalArgumentException("Nodes must be on the same Y level");
        }

        this.nodes = nodes;
        this.pathLength = nodes.size();

        //Artificial nodes used to interpolate the end nodes of the path
        Node base = this.nodes.get(1).toVector3()
                .multiply(2)
                .add(
                        this.nodes.get(0).toVector3().multiply(-1)
                )
                .toNode(this.nodes.get(0).getWorld());

        Node end = this.nodes.get(this.nodes.size()-1).toVector3()
                .multiply(2)
                .add(
                        this.nodes.get(this.nodes.size()-1).toVector3().multiply(-1)
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


        }


        return null;
    }
}
