
import java.io.*;
import java.util.*;



public class PtreeDoc
{
    public static void main(String args[])
    {
        File inFile = null;
        File fileTwo = null;
        if(args.length > 0)
        {
            inFile = new File(args[0]);
            fileTwo = new File(args[1]);
            System.out.println(args[0]);
            System.out.println(args[1]);
        }
        else{
            System.out.println("No file has been given\n");
            return;
        }

        Util u = new Util();
        u.openFileOne(inFile);
        u.readFileOne();
        u.closeFile();
        ArrayList<DcopAgt> agents = new ArrayList();
        u.proccesFileOne(agents);
        ArrayList <String> agentFile = u.processFileTwo(fileTwo);
        for(int i = 0; i < agents.size(); i++)
        {
            if(agents.get(i).id == 'f')
            {
                processAgent(agents.get(i), agentFile, isRoot(agentFile), isLeaf(agentFile), hasPseudoParent(agentFile), hasSpecialAncestor(agentFile));
                System.out.println(agents.get(i).toString());
            }
        }
        
    }



    //Get the children of the agt
    //set the agt to a leaf or not
    static void processChildren(DcopAgt agt, String fileLine)
    {
        ArrayList<String> sa = new ArrayList(Arrays.asList(fileLine.split("\\s+")));

        char c = sa.get(0).charAt(0);
        agt.numChildren = Character.getNumericValue(c); 
        System.out.println(agt.numChildren);
        if(agt.numChildren > 0)
        {
            for(int i = 1; i < agt.numChildren+1; i++)
            {
                System.out.println(sa.get(i).charAt(0));
                agt.children.add(sa.get(i).charAt(0));
            }
        }

        if(agt.numChildren > 0) agt.isLeaf = false;
        else agt.isLeaf = true;
    }

    static void processParent(DcopAgt agt, ArrayList<String> fileLines, int start, int end)
    {
        String s = fileLines.get(start);
        agt.parentId = s.charAt(0);
        agt.numParents = 1;
        s = fileLines.get(start+1);
        agt.neighborDomainSize = Character.getNumericValue(s.charAt(0));
        s = fileLines.get(end);
        //call process matrix
        processMatrix(agt.parentMatrix, s, agt.neighborDomainSize, agt.domainSize);
    }

    static void processMatrix(double [][] matrix, String s, int neighborDomain, int agtDomain)
    {
        //split the string representing teh weight matrix based on spaces
        ArrayList<String> sa = new ArrayList(Arrays.asList(s.split(" ")));
        //allocate the matrix to the correct number of rowsn and collumns. Rows is the nieghborDomains
        //size and collumns is the agents domain size
        matrix = new double[neighborDomain][agtDomain];

        //set the role and col
        int row = -1;
        int col = 0;

        //for each weight in the arrayList
        for(int i = 0; i < sa.size(); i++)
        {
            //if it can be modded by the agts domain, which is the number of collumns then you add a new row
            //set the collumn to 0 and add the new value to the new row
            if(i % agtDomain == 0)
            {
                row++;
                col = 0;
                matrix[row][col] = Double.valueOf(sa.get(i));
            }

            //else all the data goes in the current row and different collumns
            else 
            {
                col++;
                matrix[row][col] = Double.valueOf(sa.get(i));
            }
            System.out.println(matrix[row][col]);

        }

    }//end function


    static void processPseudoParents(DcopAgt agt, ArrayList<String> fileLines, int start)
    {
        //Get the number of pseudo parents
        String s = fileLines.get(start);
        ArrayList<String> temp = new ArrayList(Arrays.asList(s.split("\\s+")));
        //get the numberof pseudo parents
        agt.numPseudoParents = Character.getNumericValue(temp.get(0).charAt(0));

        //for each pseduo parent, get its id
        int c = 0;
        for(int i = 1; i < agt.numPseudoParents+1; i++)
        {
            agt.pseudoParents.add(temp.get(i).charAt(0));
            System.out.println(agt.pseudoParents.get(c));
        }

        //get the domain size of the pseudo parent
        s = fileLines.get(start+1);
        temp = new ArrayList(Arrays.asList(s.split("\\s+")));
        agt.neighborDomainSize = Character.getNumericValue(temp.get(1).charAt(0));

        s = fileLines.get(start+3);
        for(int i =0; i < agt.numPseudoParents; i++)
        {   
            double[][] tempMatrix = new double[agt.neighborDomainSize][agt.domainSize];
            processMatrix(tempMatrix, s, agt.neighborDomainSize, agt.domainSize);
        }



    }


    static void processSpecialAncestors(DcopAgt agt, ArrayList<String> fileLines, int start)
    {
        String s = fileLines.get(start);
        ArrayList<String> temp = new ArrayList(Arrays.asList(s.split("\\s+")));
        agt.numSpecialAncestors = Character.getNumericValue(temp.get(0).charAt(0));
        for(int i = 0; i < agt.numSpecialAncestors; i++)
        {
            agt.specialAncestors.add(temp.get(i+1).charAt(0));
            System.out.println(agt.specialAncestors.get(0));
        }

    }


    //processes an agent by calling the other processing files
    static void processAgent(DcopAgt agt, ArrayList<String> agtFile, boolean isRoot, boolean isLeaf, boolean pseudoParent, boolean specialAncestor)
    {
        agt.domainSize = Character.getNumericValue(agtFile.get(1).charAt(0));
        if(isRoot)
        {
            processChildren(agt, agtFile.get(agtFile.size()-1));
            agt.isRoot = true;
            return;
        }
        if(isLeaf && pseudoParent)
        {
            processParent(agt, agtFile, 2, 5);
            processPseudoParents(agt, agtFile, 6);
            return;
        }
        if(isLeaf && specialAncestor)
        {
            processParent(agt, agtFile, 2, 5);
            processSpecialAncestors(agt, agtFile, 7);
            return;
        }
        if(isLeaf == false && pseudoParent)
        {
            processParent(agt, agtFile, 2, 5);
            processPseudoParents(agt, agtFile, 6);
            processChildren(agt, agtFile.get(agtFile.size()-1));
            return;
        }
        if(isLeaf == false && specialAncestor)
        {
            System.out.println("HERE\n");
            processParent(agt, agtFile, 2, 5);
            processSpecialAncestors(agt, agtFile, 7);
            processChildren(agt, agtFile.get(agtFile.size()-1));
            return;
        }
    }//end function


    static boolean isRoot(ArrayList<String> fileLines)
    {

        String s = fileLines.get(2);
        if(s.contains("-1"))return true;

        return false;
    }

    static boolean isLeaf(ArrayList<String> fileLines)
    {
        String s = fileLines.get(fileLines.size()-1);
        System.out.println("CHECKING CHILDREN");
        System.out.println(s);
        String[] temp = s.split("\\s+");
        if(temp[0].equals("0"))return true; 
        return false;
    }

    static boolean hasPseudoParent(ArrayList<String> fileLines)
    {
        String s = fileLines.get(6);
        if(s.charAt(0)=='0')return false;
        return true;
    }

    static boolean hasSpecialAncestor(ArrayList<String> fileLines)
    {
        for(int i = 0; i < fileLines.size(); i++)
        {
            String s = fileLines.get(i);
            if(s.contains("special_ancestor"))
            {
                System.out.println("special ancesor line");
                System.out.println(s.charAt(0));
                if(s.charAt(0) == '0')return false;
                return true;
            }
        }
        return false;
    }

}