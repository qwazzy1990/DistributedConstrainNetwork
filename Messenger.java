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
            System.out.println("--SENDING MESSAGE--");
            this.sendMessage(root.children.get(i), message);
        }
    }


}