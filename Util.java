

import java.io.*;
import java.io.BufferedReader;
import java.util.*;


public class Util
{
    private BufferedReader x; 

    public ArrayList<String> a;

    public ArrayList<String> agentNames;

    char rootId;

    public Util()
    {
        a = new ArrayList<String>();
        agentNames = new ArrayList<String>();
    }

    //Open the file
    public void openFileOne(File inFile)
    {

        try{
            x = new BufferedReader(new FileReader(inFile));
        }catch(Exception e){
            System.out.println(inFile);
            e.printStackTrace();
            return;
        }

    }
    
    //Read file one line by line
    public void readFileOne()
    {
        try{
            String fileLine = x.readLine();
            while(fileLine != null)
            {
                System.out.println(fileLine);

                a.add(fileLine);

                fileLine = x.readLine();
            }


        }catch(Exception e)
        {
            System.out.println("Cannot read the file");
        }

        //Get the list of agents
        String agents = a.get(1);
        for(int i = 0; i < agents.length(); i++)
        {
            if(Character.isLetter(agents.charAt(i)))
            {
                char c = agents.charAt(i);
                agentNames.add((Character.toString(c)));
            }
        }

        String rootLine = a.get(a.size()-1);
        rootId = rootLine.charAt(rootLine.length() - 1);
        System.out.println(rootId);

       
        


    }//end function readFileOne


    public void proccesFileOne(ArrayList<DcopAgt> agts)
    {
        for(int i = 0; i < agentNames.size(); i++)
        {
            agts.add(new DcopAgt(agentNames.get(i).charAt(0)));
            if(agentNames.get(i).charAt(0) == rootId)
            {
                agts.get(i).isRoot = true;
            }
        }
    }

    //close the file
    public void closeFile()
    {
        try{
            x.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> processFileTwo(File inFile)
    {
        ArrayList<String> fileLines = new ArrayList<String>();
        openFileOne(inFile);
        try{
            String fileLine = x.readLine();
            while(fileLine != null)
            {

                fileLines.add(fileLine);

                fileLine = x.readLine();
            }


        }catch(Exception e)
        {
            System.out.println("Cannot read the file");
        }

        closeFile();
        return fileLines;
    }



    
}