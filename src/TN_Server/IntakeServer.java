package TN_Server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class IntakeServer {
    private static ArrayList<ClientHandler> threadList;

    public static void main(String[] args) throws IOException {
        threadList = new ArrayList<>();
        SSLServerSocket mainSocket = ConfigureServerSocket();

        System.out.println("Intake: Server started successfully, waiting for connections...");

        // Indefinitely checks for new connection attempts, accepts them, then creates a new ClientHandler thread.
        // The thread is then added to the thread arraylist, to keep track of all active sockets in the session
        while (true) {
            Socket newClientSocket = mainSocket.accept();
            System.out.printf("Intake: new connection established. Delegating to ClientHandler thread. (%s:%s)\n", newClientSocket.getInetAddress(), newClientSocket.getPort());
            ClientHandler ch = new ClientHandler(newClientSocket);
            ch.start();
            threadList.add(ch);
        }
    }

    // ConfigureServerSocket: Uses the provided certificate and JKS to create and SSLServerSocket instance.
    // This allows the server to protect traffic through TLS and prevent MitM attacks
    private static SSLServerSocket ConfigureServerSocket() throws IOException {
        System.setProperty( "javax.net.ssl.keyStore", ConfigConstants.KeystorePath );
        System.setProperty( "javax.net.ssl.keyStorePassword", ConfigConstants.KeystorePassword );

        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket( ConfigConstants.ServerPort );
        ss.setEnabledProtocols( new String[]{"TLSv1.3", "TLSv1.2"} );

        return ss;
    }
}
