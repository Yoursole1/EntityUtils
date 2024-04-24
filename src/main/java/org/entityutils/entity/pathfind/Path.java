package org.entityutils.entity.pathfind;

import lombok.Getter;
import org.bukkit.Location;
import org.entityutils.entity.npc.movement.CenterInstruction;
import org.entityutils.entity.npc.movement.Instruction;
import org.entityutils.entity.npc.movement.JumpInstruction;
import org.entityutils.entity.npc.movement.WalkInstruction;
import org.entityutils.utils.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Path {

    @Getter
    private Node tip;

    public Path() {
        this.tip = null;
    }

    public void addNode(Node node) {
        node.setParent(this.tip);
        this.tip = node;
    }

    public void reverse() {
        this.tip = reverse(this.tip);
    }

    private Node reverse(Node head){
        Node first;

        if (head == null || head.getParent() == null){
            return head;
        }

        first = reverse(head.getParent());
        head.getParent().setParent(head);
        head.setParent(null);

        return first;
    }

    public List<Instruction> generateInstructions(Location start, Location target, int ticksPerBlock) {

        List<Instruction> movement = new ArrayList<>();
        Node init = new Node(this.tip);
        Node curr = this.tip;
        while (curr.getParent() != null) {
            Node nxt = curr.getParent();

            Vector3 offset = new Vector3((double)nxt.getX() - curr.getX(), (double)nxt.getY() - curr.getY(), (double)nxt.getZ() - curr.getZ());

            if(offset.getY() != 0){
                movement.add(new JumpInstruction(offset, 8));
            }else{
                Vector3 currVec = new Vector3(curr.getX(), curr.getY(), curr.getZ());
                Vector3 nxtVec = new Vector3(nxt.getX(), nxt.getY(), nxt.getZ());
                movement.add(new WalkInstruction(currVec, nxtVec, ticksPerBlock));
            }

            curr = curr.getParent();
        }

        Vector3 finalVec = new Vector3(curr.getX(), curr.getY(), curr.getZ());
        Vector3 initVec = new Vector3(-init.getX(), -init.getY(), -init.getZ());
        finalVec.add(initVec); // offset from movement
        Vector3 startingLoc = new Vector3(start);
        startingLoc.add(finalVec); // location from naive movement

        movement.add(new WalkInstruction(startingLoc, new Vector3(target), ticksPerBlock));

        return movement;
    }
}
