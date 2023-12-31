package TN_Client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Client {
    public static void main(String[] args) throws IOException {
        // Using the tn.youniverse.uk domain to resolve the exist IP remotely
        int portNumber = 17777;
        String hostName = "tn.youniverse.uk";

        // Initiating TLS connection with server, every step documented
        // Roughly based on code provided in CS2SNS Lab 6, Vitor Jesus, Aston University
        System.out.println("Connecting...");
        SSLSocketFactory factory = ( SSLSocketFactory ) SSLSocketFactory.getDefault();
        SSLSocket s = (SSLSocket) factory.createSocket( hostName, portNumber );
        s.startHandshake();

        System.out.printf("Connected (%s:%d)\n", s.getInetAddress(), s.getPort());

        // Configuring TCP communication and user input
        // Based on code provided in CS2SNS Lab 3, Vitor Jesus, Aston University
        PrintWriter outgoing = new PrintWriter( s.getOutputStream(), true );
        BufferedReader incoming = new BufferedReader( new InputStreamReader( s.getInputStream() ) );
        BufferedReader userInput = new BufferedReader( new InputStreamReader(System.in) );

        System.out.printf( "Connection to %s confirmed!\n", incoming.readLine());

        System.out.println("****************************************************************************");

        // Confirmatory handshake, to let the server know configuration is complete
        outgoing.println("acknowledged");

        System.out.print("You will now receive a series of questions with numeric answers. \n" +
                "The game will continue for as long as you answer correctly. \n" +
                "Please press enter to begin!");

        // Signals server to begin game
        userInput.readLine();
        outgoing.println("Begin");

        System.out.println("****************************************************************************");

        boolean continueGame = true;
        String answer;
        String result;

        // Loops indefinitely, until client gives an incorrect answer
        while (continueGame) {
            System.out.println(incoming.readLine());

            System.out.print("Your answer (1-100): ");
            answer = userInput.readLine();

            outgoing.println(answer);
            result = incoming.readLine();

            if (!result.startsWith("Correct!")) {
                continueGame = false;
            }

            System.out.println(result);
            System.out.println("****************************************************************************");
        }

        System.out.println("Thank you for playing!");
    }
}
