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

    ArrayList<String> childWeightMessages;

    Messenger messenger;


    /**The mailbox of the agent. This is where it will keep its messags from its parent and childrn
     * There are 4 types of messages. "HALT", "COLLECT", "VALUE..." "WEIGHT..."
     */

    ArrayList<String> mailBox;

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
        this.childWeightMessages = new ArrayList<String>();


        this.mailBox = new ArrayList<String>();
        this.messenger = new Messenger();

    
    }

    // ToString methods
    public String toString()
    {
        return "Id: "+Character.toString(this.id) + "\n"
        +"Is Leaf: " + String.valueOf(this.isLeaf) + "\n" 
        +"Is root: "+ String.valueOf(this.isRoot)+"\n"
        +"Parent: "+parentToString()+"\n"
        +"PseudoParents: "+ pseudoParentsToString()+"\n"
        +"SpecialAncestors: "+ specialAncestorsToString()+"\n"
        +"Children: "+childrenToString()+"\n"
        +"Parents Matrix:\n"+matrixToString()+"\n";

    }//end toString

    public String parentToString() {
        if (this.isRoot == true) {
            return "HAS NO PARENTS";
        } else {
            return Character.toString(this.parentId);
        }
    }//end func

    public String matrixToString()
    {
        String s = new String();

        if(this.isRoot)return "HAS NO MATRIX";
        
        s+=Arrays.deepToString(this.parentMatrix);
        
        for(int i = 0; i < this.numPseudoParents; i++)
        {
            s+=",";
            s+= Arrays.deepToString(this.pseudoParentsMatrix.get(i));
        }
        s+="\n";
        return s;
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
    }//end func

    public String pseudoParentsToString() {
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
    }//end func

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
    }//end func

    public void readMessage()
    {
        System.out.println("--READING MESSAGE--");
        String msg = this.mailBox.get(this.mailBox.size()-1);

        if(msg.contains("Collect") && this.isLeaf)
        {
            System.out.printf("Recieved Collect from parent %c\n", this.parentId);
            this.messenger.sendMessage(this.parentId, "Halt");
        }

        if(msg.contains("Collect") && this.isLeaf == false)
        {
            for(int i = 0; i < this.numChildren; i++)
            {
                System.out.printf("Sending message Collect to child %c\n", this.children.get(i));
                this.messenger.sendMessage(this.children.get(i), msg);
            }
        }

        if(this.isRoot && msg.contains("Halt"))
        {
            System.out.println("Recieved halt message from child");
        }
        if(this.isRoot == false && msg.contains("Halt"))
        {
            System.out.printf("Sending halt message to parent %c\n", this.parentId);
            this.messenger.sendMessage(this.parentId, "Halt");
        }
    
    }

    /**Setters */
    public void setMessenger(Messenger m)
    {
        this.messenger = m;
    }
   
   public String processWeights(String msg)
   {
       String result = new String();
       result+="";
       DcopAgt parent = this.messenger.getAgent(this.parentId);
       if(this.isLeaf)
       {
           this.sumParentConstraints();
           result = weightMatrixToMessage(max(this.parentMatrix[0][0], this.parentMatrix[0][1]), max(this.parentMatrix[1][0], this.parentMatrix[1][1]));
           parent.childWeightMessages.add(result);
           return result;
       }
       else 
       {
           double[] childWeights = this.weightToDouble(this.childWeightMessages.get(0));
           this.parentMatrix[0][0] += childWeights[0];
           this.parentMatrix[0][1] += childWeights[1];
           this.parentMatrix[1][0] += childWeights[0];
           this.parentMatrix[1][1] += childWeights[1];

           result = weightMatrixToMessage(max(this.parentMatrix[0][0], this.parentMatrix[0][1]), max(this.parentMatrix[1][0], this.parentMatrix[1][1]));
            if(parent.childWeightMessages.size() >= parent.numChildren)
            {
                int count = 0;

                while(parent.childWeightMessages.size() >= parent.numChildren)
                {
                    parent.childWeightMessages.remove(count);
                    count++;
                }
            }
           parent.childWeightMessages.add(result);
        

       }


       return result;
       
   }

   public double[] weightToDouble(String s)
   {
       double[] weights = new double[2];

       ArrayList<String> sa = new ArrayList<String>(Arrays.asList(s.split("\\s+")));

       
       weights[0] = Double.valueOf(sa.get(0));
       weights[1] = Double.valueOf(sa.get(1));

       return weights;
   }

   //used only for the root when it gets a weight message from its child
   public int sendValue(String s)
   {
        double[] weights = this.weightToDouble(s);
        if(weights[0] > weights[1]) return 0;
        else return 1;

   }

   public String weightMatrixToMessage(double first, double second)
   {
       String s = new String();
       
       s ="";
       s += Double.toString(first);
       s+= " ";
       s+= Double.toString(second);


       return s;
   }

   public double max(double a, double b)
   {
       if( a > b)return a;
       else return b;
   }


   public void sumParentConstraints()
   {
       int row = 0;
       int col = 0; 
       for(int i = 0; i < this.numPseudoParents; i++)
       {
            double[][] matrix = this.pseudoParentsMatrix.get(i);
            for(int j = 0; j < matrix.length; j++)
            {
                for(int k = 0; k < matrix[0].length; k++)
                {
                    this.parentMatrix[row][col] += matrix[j][k];
                    col++;
                }
                col = 0;
                row++;
            }
       }
   }

   public String maxToString()
   {
       String s = new String();
       double max1 = this.max(this.parentMatrix[0][0], this.parentMatrix[0][1]);
       double max2 = this.max(this.parentMatrix[1][0], this.parentMatrix[1][1]);
       s+="[";
       s+= Double.toString(max1);
       s+=",";
       s+=Double.toString(max2);
       s+="]";



       

       return s;
   }

    












}