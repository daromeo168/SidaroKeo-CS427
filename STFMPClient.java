import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class STFMPClient {
    public static void main(String[] args) {
        try (Socket connection = new Socket("localhost", 9999);){

            //Successful connection message
            System.out.println("The connection to the server was established.");
            System.out.println("Start sending the data to the server");


// Read input from users, what they want to do
            InputStream keyboardInputStream = System.in;
            Scanner keyboadScanner = new Scanner(keyboardInputStream);
            STFMPRequest request = new STFMPRequest(null,null,null);
            while (true) {
                System.out.print("Type your operation here: ");
                String userInput = keyboadScanner.nextLine();
                if (userInput.equals(STFMPActions.WRITE)) {
                    request = new STFMPRequest(Constants.PROTOCOL_VERSION,STFMPActions.WRITE,"Dog2.txt#kokokokok");
                    send(connection,request);
                } else if (userInput.equals(STFMPActions.VIEW)) {
                    request = new STFMPRequest(Constants.PROTOCOL_VERSION,STFMPActions.VIEW,"Dog1.txt");
                    send(connection,request);
                   } else if (userInput.equals(STFMPActions.CLOSE)) {
                    keyboadScanner.close();
                    send(connection,request);
                    break;
                } else {
                    continue;
                }
                read(connection);
            }


        } catch (IOException e) {
            System.out.println("Unable to connect to the server. " + e.getMessage());
        }
    }
    static void send (Socket connection, STFMPRequest request)throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        //Encryption
        String encryptedRequest = request.encryptedRequest();
        printWriter.write(encryptedRequest);
        printWriter.flush();
        System.out.println("Requesting:" + encryptedRequest);
    }
    static void read (Socket connection)throws IOException {
        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        String encryptedResponse = scanner.nextLine();
        System.out.println("Receiving message in encryption form: " + encryptedResponse);
        STFMPResponse response = STFMPResponse.fromEncryptedString(encryptedResponse);
        System.out.println("Output: " + response.getMessage());
    }
}
