

public class Messenger
{
    static void deliverMessage(DcopAgt recipient, String message)
    {
        recipient.mailBox.add(message);
    }
}