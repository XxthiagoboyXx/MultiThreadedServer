import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MultiThreadedServer {
	private ServerSocket serverSocket;

	public MultiThreadedServer(int port) throws IOException{
		serverSocket = new ServerSocket(port);
	}

	public void start() throws IOException {
		System.out.println("Server started. Listening on port: " + serverSocket.getLocalPort());
		while (true) {
			Socket clientSocket = serverSocket.accept();
			System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
		}
	}

	private static class ClientHandler implements Runnable {
		private Socket clientSocket;

		public ClientHandler(Socket socket){
			this.clientSocket = socket;
		}

		public void run(){

			String IPClient = clientSocket.getInetAddress().getHostAddress();

			try{
				clientSocket.setSoTimeout(60000); //timeout 1 min

				BufferedReader inFromClient = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
				String fileName = inFromClient.readLine();
				System.out.println(IPClient + " - Client request file: "+ fileName);

				FileInputStream fileIn = new FileInputStream("src/arquivos/"+fileName);
				OutputStream socketOut = clientSocket.getOutputStream();

				byte[] cbuffer = new byte[1024];
				int bytesRead;

				System.out.println(IPClient + " - Uploading File: " + fileName);
				while ((bytesRead = fileIn.read(cbuffer)) != -1){
					socketOut.write(cbuffer, 0, bytesRead);
					socketOut.flush();
				}

				fileIn.close();
				clientSocket.close();



			} catch (SocketTimeoutException e){
				System.err.println(IPClient + " - Connection timed out: " + e.getMessage());
			} catch (IOException e){
				System.err.println(IPClient + " - Error handling client request " + e.getMessage());
			} finally {
				try{
					clientSocket.close();
					System.out.println(IPClient + " - Connection closed.");
				}
				catch (IOException e){
					System.err.println(IPClient + " - Error closing connection: " + e.getMessage());
				}

			}

		}
	}

	public static void main(String[] args) throws IOException{
		MultiThreadedServer server = new MultiThreadedServer(8000); //port 8000
		server.start();
	}

}