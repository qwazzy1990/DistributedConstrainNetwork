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
           recipient.processWeights(null);

           
           return;
        }
        for(int i = 0; i < recipient.numChildren; i++)
        {
            this.sendCollect(recipient.children.get(i), recipientID, message);
        }
        
    }

    /**Response to collect message.
     * 
     * @param recipientID
     * @param senderID
     * @param message
     */
    public void sendWeight(Character recipientID, Character senderID, String message)
    {
        DcopAgt sender = this.getAgent(senderID);
        DcopAgt recipient = this.getAgent(recipientID);
        if(recipient.isRoot)
        {
            System.out.println("At the root");
            return;
        }
        if(sender.isLeaf)
        {
            recipient.processWeights(message);
        }
        else 
        {
            
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