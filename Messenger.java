import java.util.*;
import java.io.*;

public class Messenger
{
    private ArrayList<DcopAgt> agents;

    public Messenger(ArrayList<DcopAgt> agents)
    {
        this.agents = new ArrayList<DcopAgt>();
        for(int i = 0; i < agents.size(); i++)this.agents.add(agents.get(i));
    }

    public Messenger()
    {
    }


    void sendMessage(Character recipientId, String message)
    {
        for(int i = 0; i < this.agents.size(); i++)
        {
            if(this.agents.get(i).id == recipientId)
            {
                this.deliverMessage(this.agents.get(i), message);
        
                System.out.printf("--Agents message--%s\n", this.agents.get(i));
                this.agents.get(i).readMessage();

            }
        }
    }
    void deliverMessage(DcopAgt recipient, String message)
    {
        recipient.mailBox.add(message);
    }

    void startMessageProcessing(DcopAgt root, String message)
    {
        for(int i = 0; i < root.numChildren; i++)
        {
            this.sendCollect(root.children.get(i), root.id, message);
        }
    }

    /**Get an agent from the messenger based on its id */
    public DcopAgt getAgent(Character agtID)
    {
        for(int i = 0; i < this.agents.size(); i++)
        {
            if(this.agents.get(i).id == agtID)return this.agents.get(i);
        }
        return null;
    }

    public void sendCollect(Character recipientID, Character senderId, String message)
    {
        DcopAgt recipient = this.getAgent(recipientID);
        System.out.printf("\nAgent %c recieved Collect message from %c\n", recipientID, senderId);

        if(recipient.isLeaf == true)
        {
           System.out.printf("\nAt  leaf, terminating collect message transfer\n");
           System.out.printf("\nBeginning weight transfer protocol\n");
           //if at the leaf then the recipeint of the weight message is the recipient's parent
           this.sendWeight(recipient.parentId, recipient.id, "Weight");

           
           return;
        }
        for(int i = 0; i < recipient.numChildren; i++)
        {
            //keep sendin collect to my children
            this.sendCollect(recipient.children.get(i), recipientID, message);


        }

        //this.sendWeight(recipient.parentId, recipient.id, "Weight");

        //done sending collect to my children, my children have given me their weights,
        //therefore send my weight to my parent

        
    }

    /**Response to collect message.
     * 
     * @param recipientID
     * @param senderID
     * @param message
     */
    public void sendWeight(Character recipientID, Character senderID, String message)
    {
        DcopAgt recipient = this.getAgent(recipientID);
        DcopAgt sender = this.getAgent(senderID);
        if(recipient.isRoot == true)
        {
            String s = sender.processWeights(null);
            System.out.printf("%c is sending weight %s tp %c\n", senderID, s, recipientID);


            for(int i = 0; i < recipient.childWeightMessages.size(); i++)
            {
                System.out.printf("%s ", recipient.childWeightMessages.get(i));
            }
            System.out.printf("\n");
            System.out.println("At the root. Begin transfer of values");
            
            for(int i = 0; i < recipient.numChildren; i++)
            {
                String childMsg = recipient.childWeightMessages.get(i);
                this.sendValue(recipient.children.get(i), recipient.id, Integer.toString(recipient.sendValue(childMsg)));
            }

        }
        else 
        {
            String s = sender.processWeights(null);
            System.out.printf("%c is sending weight %s tp %c\n", senderID, s, recipientID);
            this.sendWeight(recipient.parentId, recipient.id,"Weight");
        }
        
    }

    public void sendValue(Character recipientID, Character senderID, String message)
    {
       DcopAgt recipient = this.getAgent(recipientID);
       DcopAgt sender = this.getAgent(senderID);

       System.out.printf("Agent %c recived %s from agent %c\n", recipientID, message, senderID);

       if(recipient.isLeaf)
       {
           //calculate whcih value you will send
           //return halt
           System.out.println("At the leaf, sending halt msge");
           System.out.printf("Agent %c's value is %s\n", recipientID, "0");
           return;
       }
       int parentsValue = Integer.valueOf(message);
       double v0 = recipient.parentMatrix[parentsValue][0];
       double v1 = recipient.parentMatrix[parentsValue][1];

       int myVal;

       if(recipient.max(v0, v1) == v0)myVal = 0;
       else myVal = 1;

       for(int i = 0; i < recipient.numChildren; i++)
       {
            this.sendValue(recipient.children.get(i), recipient.id, Integer.toString(myVal));
       }


    }
    public void sendHalt(Character recipientID, Character senderID, String message)
    {

        DcopAgt recipient = this.getAgent(recipientID);
        DcopAgt sender = this.getAgent(senderID);
        System.out.printf("\nAgent %c recieved Halt message from agent %c\n", recipient.id, sender.id);

        if(recipient.isRoot)
        {
            System.out.printf("\nAt the root, terminating halt message transfer\n");
            return;
        }
        else
        {
           
            System.out.printf("\nAgent %c is sending Halt message to Agent %c\n", recipientID, recipient.parentId);
            this.sendHalt(recipient.parentId, recipientID, "Halt");
            
        } 
    
    }

    public void copyWeightMsg(ArrayList<double[]> a, ArrayList<double[]> b)
    {
        for(int i = 0; i < b.size(); i++)
        {
            a.add(b.get(i).clone());
        }
    }

    


}