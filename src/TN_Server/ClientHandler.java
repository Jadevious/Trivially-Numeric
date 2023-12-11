package TN_Server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            // Documenting the setup and client socket reception for this thread
            System.out.printf("ClientHandler %s: Socket received, client handover successful. \n", this.getName());
            BufferedReader in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
            PrintWriter out = new PrintWriter( clientSocket.getOutputStream(), true );	// outgoing

            // Confirmatory Handshake
            out.println(this.getName());
            String response = in.readLine();

            if (response.equals("acknowledged")) {
                System.out.printf("ClientHandler %s: Handshake successful. (Client: %s:%d) \n",
                        this.getName(), clientSocket.getInetAddress(), clientSocket.getPort());
            }

            // Closing server until further functionality

            // TODO: Begin game

            // TODO: Return results and terminate connection/thread
        } catch (IOException ex) {
            System.out.printf("Exception thrown in thread %s. ", this.getName());
            System.out.println(ex.getMessage());
        }
    }
}