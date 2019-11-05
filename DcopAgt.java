import java.util.*;
import java.io.*;



public class DcopAgt
{

    public boolean isRoot;
    public boolean isLeaf;
    char id;
    public DcopAgt(char c)
    {
        this.isRoot = false;
        this.isLeaf = false;
        this.id = c;
    }




    //ToString method
    public String toString()
    {
        return "Id: "+Character.toString(this.id) + " Is Leaf: " + String.valueOf(this.isLeaf) + " Is root: "+ String.valueOf(this.isRoot);
    }
}