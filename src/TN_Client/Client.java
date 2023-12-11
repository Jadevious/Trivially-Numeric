package TN_Client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Client {
    public static void main(String[] args) throws IOException {
        int portNumber = 17777;
        String hostName = "127.0.0.1";

        System.out.println("Connecting...");
        SSLSocketFactory factory = ( SSLSocketFactory ) SSLSocketFactory.getDefault();
        SSLSocket s = (SSLSocket) factory.createSocket( hostName, portNumber );
        s.startHandshake(); // the SSL handshake

        // SSL Debug. TODO: Remove when complete
//        System.out.println( "Other info:" +
//                "\n >getHandshakeApplicationProtocol " + s.getHandshakeApplicationProtocol() +
//                "\n >getTrafficClass "+ s.getTrafficClass() +
//                "\n >getHandshakeSession " + s.getHandshakeSession() +
//                "\n >getEnabledCipherSuites" + s.getEnabledCipherSuites() +
//                "\n >getKeepAlive " + s.getKeepAlive() +
//                "\n >getNeedClientAuth " + s.getNeedClientAuth() +
//                "\n >getSSLParameters " + s.getSSLParameters() +
//                "\n getSupportedCipherSuites>" + s.getSupportedCipherSuites() +
//                "~~~~~~~~~" );

        System.out.printf("Connected (%s:%d)\n", s.getInetAddress(), s.getPort());

//        BufferedReader userInput = new BufferedReader( new InputStreamReader(System.in) );

        PrintWriter outgoing = new PrintWriter( s.getOutputStream(), true );
        BufferedReader incoming = new BufferedReader( new InputStreamReader( s.getInputStream() ) );

        System.out.printf( "Connection to %s confirmed!", incoming.readLine());

        outgoing.println("acknowledged");

        // Closing client until further functionality
    }
}
