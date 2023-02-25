import java.io.*;
import java.net.*;

public class ClientSocket {
	public static void main(String[] args) {
		try{

			Socket clientSocket = new Socket("localhost", 8000);
			clientSocket.setSoTimeout(60000); //timeout 1 min

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter file name: ");
			String fileName = userInput.readLine();


			outToServer.writeBytes(fileName+'\n');

			FileOutputStream fileOut = new FileOutputStream("src/output/client_"+fileName);
			InputStream socketIn = clientSocket.getInputStream();

			System.out.println("Downloading: client_" + fileName);

			byte[] cbuffer = new byte[1024];
			int bytesRead;
			while((bytesRead = socketIn.read(cbuffer)) != -1) {
				fileOut.write(cbuffer, 0, bytesRead);
			}
			fileOut.close();
			clientSocket.close();
			userInput.close();

			System.out.println("File Received: client_" + fileName);

		}
		catch (SocketTimeoutException e) {
			System.err.println("Connection timed out: " + e.getMessage());
		}
		catch (IOException e) {
			System.err.println("Error connecting to server: " + e.getMessage());
		}
	}
}