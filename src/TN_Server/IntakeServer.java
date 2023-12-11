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

        while (true) {
            System.out.println("Intake: Waiting for connection...");
            Socket newClientSocket = mainSocket.accept();
            System.out.println("Intake: Connection established. Delegating to ClientHandler thread.");
            ClientHandler ch = new ClientHandler(newClientSocket);
            ch.run();
            threadList.add(ch);
        }
    }

    private static SSLServerSocket ConfigureServerSocket() throws  IOException {
        System.setProperty( "javax.net.ssl.keyStore", ConfigConstants.KeystorePath );
        System.setProperty( "javax.net.ssl.keyStorePassword", ConfigConstants.KeystorePassword );

        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket( ConfigConstants.ServerPort );
        ss.setEnabledProtocols( new String[]{"TLSv1.3", "TLSv1.2"} );

        return ss;
    }
}
