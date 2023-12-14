package TN_Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private int score;
    private Random rand;
    private HttpClient httpClient;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        httpClient = HttpClient.newHttpClient();
    }

    public void run() {
        try {
            // Documenting the setup and client socket reception for this thread
            System.out.printf("ClientHandler %s: Socket received, client handover successful. \n", this.getName());
            BufferedReader incoming = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
            PrintWriter outgoing = new PrintWriter( clientSocket.getOutputStream(), true );	// outgoing

            // Confirmatory Handshake
            outgoing.println(this.getName());
            String response = incoming.readLine();

            if (!response.equals("acknowledged")) {
                System.out.printf("ClientHandler %s: Handshake failed. Terminating connection. (Client: %s:%d)\n",
                        this.getName(), clientSocket.getInetAddress(), clientSocket.getPort());
                return;
            }

            System.out.printf("ClientHandler %s: Handshake successful. (Client: %s:%d) \n",
                    this.getName(), clientSocket.getInetAddress(), clientSocket.getPort());

            // Waiting for client to respond with "begin"
            incoming.readLine();

            score = 0;
            rand = new Random();

            // Start of game
            while (true) {
                int answer = rand.nextInt(100) + 1;
                String questionString = retrieveQuestion(answer);
//                String answer = questionString.split(" ")[0];

                questionString = String.format("Question %s: What is %s?", score+1, questionString);

                outgoing.println(questionString);

                if (!incoming.readLine().equals("" + answer)) {
                    outgoing.printf("Incorrect, the  answer was %s! Nice try, your score was %s\n", answer, score);
                    System.out.printf("ClientHandler %s: Player finished with a score of %s. Terminating connection. (Client: %s:%d)\n",
                            this.getName(), score, clientSocket.getInetAddress(), clientSocket.getPort());


                    return;
                }
                else {
                    score++;
                    outgoing.printf("Correct! Your score is %s\n", score);
                }
            }
        } catch (IOException ex) {
            System.out.printf("Exception thrown in %s. %s", this.getName(), ex.getMessage());
        }
    }

    private String retrieveQuestion(int answer) {
        // TODO: Call API to retrieve question
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://numbersapi.com/" + answer + "/trivia?fragment"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
