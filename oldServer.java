import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server for receiving messages
 *
 * @author Matthew England
 *
 */
public class server
{

    private final static int PORT = 8233;

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException
    {
        ServerSocket s;
        try
        {
            s = new ServerSocket(Integer.parseInt(args[0]));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            s = new ServerSocket(PORT);
        }

        while (true)
        {
            Socket incoming = s.accept();
                //System.out.println("Connected.");
            BufferedReader fromClient = new BufferedReader(
                    new InputStreamReader(incoming.getInputStream()));
            DataOutputStream toClient = new DataOutputStream(
                    incoming.getOutputStream());
            String clientString = fromClient.readLine();

            boolean result = execute(clientString);
			     if (!result)
                toClient.writeBytes("Failed");
            else
                toClient.writeBytes("Success");

            incoming.close();
        }

    }

    public static boolean execute(String input)
    {
        try
        {
            switch (input)
            {
            case "lock":
                Runtime.getRuntime().exec("sudo python /cycurity/run.py lock");
            System.out.println("Locking...");
                return true;
            case "unlock":
                Runtime.getRuntime().exec("sudo python /cycurity/run.py unlock");
            System.out.println("Unlocking...");
                return true;

            }
        }
        catch (IOException e)
        {
            return false;
        }

        return false;
    }

}


