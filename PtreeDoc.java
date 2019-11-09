
import java.io.*;
import java.util.*;



public class PtreeDoc
{
    public static void main(String args[])
    {
        File inFile = null;
        if(args.length > 0)
        {
            inFile = new File(args[0]);
            System.out.printf("Load..%s", args[0]);

        }
        else{
            System.out.println("No file has been given\n");
            return;
        }

        //Process the first file which creates the agents and sets their id
        Util u = new Util();
        u.openFileOne(inFile);
        u.readFileOne();
        u.closeFile();
        ArrayList<DcopAgt> agents = new ArrayList<DcopAgt>();
        u.proccesFileOne(agents);

        //Now for each agent, initialize it from its own file

        Messenger messenger = new Messenger(agents);
        processAgents(agents, messenger);

       messenger.startMessageProcessing(agents.get(0), "Collect");

        
        
       
    
    }



    //Get the children of the agt
    //set the agt to a leaf or not
    static void processChildren(DcopAgt agt, String fileLine)
    {
        ArrayList<String> sa = new ArrayList<String>(Arrays.asList(fileLine.split("\\s+")));

        char c = sa.get(0).charAt(0);
        agt.numChildren = Character.getNumericValue(c); 
        if(agt.numChildren > 0)
        {
            for(int i = 1; i < agt.numChildren+1; i++)
            {
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
        agt.parentMatrix = new double[agt.neighborDomainSize][agt.domainSize];
        processMatrix(agt.parentMatrix, s, agt.neighborDomainSize, agt.domainSize);
    }

    static void processMatrix(double [][] matrix, String s, int neighborDomain, int agtDomain)
    {
        //split the string representing teh weight matrix based on spaces
        ArrayList<String> sa = new ArrayList<String>(Arrays.asList(s.split(" ")));
        //allocate the matrix to the correct number of rowsn and collumns. Rows is the nieghborDomains
        //size and collumns is the agents domain size

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

        }


    }//end function


    static void processPseudoParents(DcopAgt agt, ArrayList<String> fileLines, int start)
    {
        //Get the number of pseudo parents
        String s = fileLines.get(start);
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(s.split("\\s+")));
        //get the numberof pseudo parents
        agt.numPseudoParents = Character.getNumericValue(temp.get(0).charAt(0));

        //for each pseduo parent, get its id
        for(int i = 1; i < agt.numPseudoParents+1; i++)
        {
            agt.pseudoParents.add(temp.get(i).charAt(0));
        }

        //get the domain size of the pseudo parent
        s = fileLines.get(start+1);
        temp = new ArrayList<String>(Arrays.asList(s.split("\\s+")));
        agt.neighborDomainSize = Character.getNumericValue(temp.get(1).charAt(0));

        s = fileLines.get(start+3);
        for(int i =0; i < agt.numPseudoParents; i++)
        {   
            double[][] tempMatrix = new double[agt.neighborDomainSize][agt.domainSize];
            processMatrix(tempMatrix, s, agt.neighborDomainSize, agt.domainSize);
            agt.pseudoParentsMatrix.add(tempMatrix);
        }



    }


    static void processSpecialAncestors(DcopAgt agt, ArrayList<String> fileLines, int start)
    {
        String s = fileLines.get(start);
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(s.split("\\s+")));
        agt.numSpecialAncestors = Character.getNumericValue(temp.get(0).charAt(0));
        for(int i = 0; i < agt.numSpecialAncestors; i++)
        {
            agt.specialAncestors.add(temp.get(i+1).charAt(0));
            System.out.println(agt.specialAncestors.get(0));
        }

    }


    //processes an agent by calling the other processing files
    static void processAgent(DcopAgt agt, ArrayList<String> agtFile, boolean isRoot, boolean pseudoParent, boolean specialAncestor, Messenger m)
    {
        agt.domainSize = Character.getNumericValue(agtFile.get(1).charAt(0));
        agt.setMessenger(m);
        if(isRoot)
        {
            processChildren(agt, agtFile.get(agtFile.size()-1));
            agt.isRoot = true;
            return;
        }
        if(pseudoParent && specialAncestor)
        {
            processChildren(agt, agtFile.get(agtFile.size()-1));

            processParent(agt, agtFile, 2, 5);
            processPseudoParents(agt, agtFile, 6);
            processSpecialAncestors(agt, agtFile, 7);
            return;
        }
        if(pseudoParent == false && specialAncestor)
        {
            processChildren(agt, agtFile.get(agtFile.size()-1));

            processParent(agt, agtFile, 2, 5);
            processSpecialAncestors(agt, agtFile, 7);
            return;
        }
        if(pseudoParent && specialAncestor == false)
        {
            processParent(agt, agtFile, 2, 5);
            processPseudoParents(agt, agtFile, 6);
            processChildren(agt, agtFile.get(agtFile.size()-1));
            return;
        }
        if(specialAncestor== false && pseudoParent == false)
        {
            processParent(agt, agtFile, 2, 5);
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
        String[] temp = s.split("\\s+");
        if(temp[0].equals("0"))return true; 
        return false;
    }

    static boolean hasPseudoParent(ArrayList<String> fileLines)
    {
        if(fileLines.size() <= 6)return false;
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
                
                if(s.charAt(0) == '0')return false;
                return true;
            }
        }
        return false;
    }


    //process all the agents
    static void processAgents(ArrayList <DcopAgt> agents, Messenger m)
    {
        Util u = new Util();
        for(int i = 0; i < agents.size(); i++)
        {
            File agentFile = null;
            String fileName = new String();
            fileName += Character.toString(agents.get(i).id);
            fileName+=".agt";
            agentFile = new File(fileName);
            ArrayList<String> agtFile = u.processFileTwo(agentFile);
            processAgent(agents.get(i), agtFile, isRoot(agtFile), hasPseudoParent(agtFile), hasSpecialAncestor(agtFile), m);
            System.out.println(agents.get(i).toString());
            
        }
    }

}