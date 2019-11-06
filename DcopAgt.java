import java.util.*;
import java.io.*;

public class DcopAgt {

    public boolean isRoot;
    public boolean isLeaf;
    char id;
    char parentId;

    int domainSize;
    int neighborDomainSize;

    int numParents;
    int numChildren;
    int numPseudoParents;
    int numSpecialAncestors;

    ArrayList<Character> children;
    ArrayList<Character> pseudoParents;
    ArrayList<Character> specialAncestors;

    double[][] parentMatrix;

    ArrayList<double[][]> pseudoParentsMatrix;
    ArrayList<Integer> specialAncestroDomainSize;

    public DcopAgt(char c) {
        this.isRoot = false;
        this.isLeaf = false;
        this.id = c;
        this.numParents = 0;
        this.numPseudoParents = 0;
        this.numChildren = 0;
        this.numSpecialAncestors = 0;
        this.domainSize = 0;
        this.neighborDomainSize = 0;

        this.children = new ArrayList<Character>();
        this.pseudoParents = new ArrayList<Character>();
        this.specialAncestors = new ArrayList<Character>();

        this.pseudoParentsMatrix = new ArrayList<double[][]>();
        this.specialAncestroDomainSize = new ArrayList<Integer>();
    }

    // ToString method
    public String toString()
    {
        return "Id: "+Character.toString(this.id) + "\n"
        +"Is Leaf: " + String.valueOf(this.isLeaf) + "\n" 
        +"Is root: "+ String.valueOf(this.isRoot)+"\n"
        +"Parent: "+parentToString()+"\n"
        +"PseudoParents: "+ psedoParentsToString()+"\n"
        +"SpecialAncestors: "+ specialAncestorsToString()+"\n"
        +"Children: "+childrenToString()+"\n";

    }

    public String parentToString() {
        if (this.isRoot == true) {
            return "HAS NO PARENTS";
        } else {
            return Character.toString(this.parentId);
        }
    }

    public String childrenToString()
    {
        if(this.isLeaf)return "HAS NO CHILDREN";

        String s = new String();

        for(int i = 0; i < this.children.size(); i++)
        {
            s+= " ";
            s+=Character.toString(this.children.get(i));
        }
        return s;
    }

    

    public String psedoParentsToString() {
        String s = new String();
        if (this.pseudoParents.size() == 0) {
            s += "HAS NO PSEUDO PARENTS";
        } else {
            for (int i = 0; i < this.numPseudoParents; i++) {
                s += " ";
                char c = this.pseudoParents.get(i);
                s += Character.toString(c);
            }
        }
        return s;
    }

    public String specialAncestorsToString() {
        String s = new String();

        if (this.specialAncestors.size() == 0) {
            return "HAS NO SPECIAL ANCESTORS";
        } else {
            for (int i = 0; i < this.specialAncestors.size(); i++) {
                s += " ";
                s += Character.toString(this.specialAncestors.get(i));
            }
        }
        return s;
    }
}