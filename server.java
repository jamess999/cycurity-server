import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.*;

/**
 * A server for receiving messages
 *
 * @author James McGinnis
 *
 */
public class server
{

	private final static int PORT = 8244;

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
			System.out.println("Waiting...");
			Socket incoming = s.accept();
			System.out.println("Connected.");
			BufferedReader fromClient = new BufferedReader(
					new InputStreamReader(incoming.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(
					incoming.getOutputStream());
			String clientString = fromClient.readLine();

			System.out.println("Got");
			System.out.println(clientString);
			String result = execute(clientString);
			System.out.println("Writing the following");
			if(result != null){
				System.out.println(result);
				toClient.writeBytes(result);
			}
			else
				System.out.println("Ignored");

			incoming.close();
		}

	}

	public static String execute(String input)
	{
		try
		{
			String[] inputs = input.split(",");
			if(inputs.length == 2)
			switch (inputs[0])
			{
				case "lock":
					if(auth(inputs[1]))
					{
						Runtime.getRuntime().exec("sudo python /cycurity/run.py lock");
						System.out.println("Locking for " + inputs[1]);
						return "Locked";
					}
					else
					{
						System.out.println("NOT Locking for " + inputs[1]);
						return "Failed";
					}
				case "unlock":
					if(auth(inputs[1]))
					{
						Runtime.getRuntime().exec("sudo python /cycurity/run.py unlock");
						System.out.println("Unlocking for " + inputs[1]);
						return "Unlocked";
					}
					else
					{
						System.out.println("NOT Unlockin for " + inputs[1]);
						return "Failed";
					}
				case "requests":
					String[] secondIn = inputs[1].split(";;");
					String req;
					if(isOwner(secondIn[1]))
					{
						req = getRequests();
					}
					else
					{
						return null;
					}
					System.out.println(req);
					return secondIn[0] + "," + req;
				case "accept":
					System.out.println("accepting " + inputs[1]);
					handleRequest(inputs[1],true);
					return "OK";
				case "deny":
					System.out.println("denying " + inputs[1]);
					handleRequest(inputs[1],false);
					return "OK";
				case "request":
					System.out.println("request from " + inputs[1]);
					request(inputs[1]);
					return "OK";
			}
		}
		catch (IOException e)
		{
			return "Failed";
		}
		System.out.println("Failed Request");
		return "Failed";
	}

	public static String getRequests()
	{
		StringBuilder sb = readFile("/cycurity/requests.conf");
		return  sb.toString();
	}

	public static boolean isOwner(String userPass)
	{

		StringBuilder sb = readFile("/cycurity/users.conf");
		String[] users = sb.toString().split(",");
		if(!sb.toString().contains(",") && sb.toString().contains(userPass))
		{
			return true;
		}
		else
		{
			return (users[0].equals(userPass));
		}
	}

	public static boolean auth(String userPass)
	{
		StringBuilder sb = readFile("/cycurity/users.conf");
		return  sb.toString().contains(userPass);
	}


	public static void handleRequest(String userPass, boolean pass)
	{

		StringBuilder sb = readFile("/cycurity/requests.conf");
		int i = sb.indexOf(userPass);
		if (i != -1) {
			sb.delete(i, i + userPass.length());
		}
		
		replaceAll(sb,",,",",");
		String s = sb.toString();
		s = s.replaceAll(",$","");
		writeFile("/cycurity/requests.conf",s);
		if(pass)
		{
			
			sb = readFile("/cycurity/users.conf");
			s = sb.toString();
			s = s + "," + userPass;
			s = s.replaceAll("^,","");
			s = s.replace("\n", "").replace("\r", "");
			writeFile("/cycurity/users.conf",s);
		}
	}

	public static void request(String userPass)
	{
		StringBuilder sb = readFile("/cycurity/requests.conf");
		String s = sb.toString();
		s = s + "," + userPass;
		s = s.replace("\n", "").replace("\r", "");
		s = s.replaceAll("^,","");
		writeFile("/cycurity/requests.conf",s);
	}


	public static void replaceAll(StringBuilder builder, String from, String to)
	{
		int index = builder.indexOf(from);
		while (index != -1)
		{
			builder.replace(index, index + from.length(), to);
			index += to.length(); // Move to the end of the replacement
			index = builder.indexOf(from, index);
		}
	}
	
	public static StringBuilder readFile(String input)
	{
		try(BufferedReader br = new BufferedReader(new FileReader(input))) {
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		return sb;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void writeFile(String file, String contents)
	{
		try(BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
			out.write(contents);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}			


