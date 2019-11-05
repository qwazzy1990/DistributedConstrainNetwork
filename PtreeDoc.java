
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
            System.out.println(args[0]);
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
    }
}