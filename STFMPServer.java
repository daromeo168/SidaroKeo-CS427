import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class STFMPServer {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(9999)) {

            System.out.println("Bind to port " + serverSocket.getLocalPort());

            while(true){
                System.out.println("Ready to accept the client request");
                Socket connection = serverSocket.accept();
                while (true){
              
                    System.out.println("Read request from the clients");
                    InputStream inputStream = connection.getInputStream();
                    Scanner scanner = new Scanner(inputStream);
                    //Get Encryption
                    String encryptedRequest = scanner.nextLine();
                    System.out.println("Encryption request: "+encryptedRequest);
                    //Creating Request
                    STFMPRequest request = STFMPRequest.fromEncryptedString(encryptedRequest);
                 
                    if(request.getAction().equals(STFMPActions.WRITE)){
                        STFMPWrite(connection,request);
                    }else if(request.getAction().equals(STFMPActions.VIEW)){
                        STFMPView(connection,request);
                    }else if(request.getAction().equals(STFMPActions.CLOSE)){
                        STFMPClose(connection,scanner,inputStream);
                        break;
                    }
                }
            }
           


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void sendResponse(Socket connection,STFMPResponse response) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        String encryptedResponse = response.encryptedResponse();
        System.out.println("Responding to client in decryption texts: "+encryptedResponse);
        printWriter.write(encryptedResponse);
        printWriter.flush();
    }
    private static void STFMPClose(Socket connection, Scanner scanner,InputStream inputStream) throws IOException {
        System.out.println("Shutdown the connection");
        STFMPResponse response = new STFMPResponse(Constants.PROTOCOL_VERSION,STFMPStatus.OK,STFMPMessage.CLOSE);
        sendResponse(connection,response);
        scanner.close();
        inputStream.close();
        connection.close();
    }


    private static void STFMPView(Socket connection, STFMPRequest request) throws IOException {
        System.out.println("Looking for the data inside the file...");
        STFMPResponse response;
        String params = request.getParams();

        if (params.split("#").length !=  1 || params.split("#")[0] == null) {
            response = new STFMPResponse(Constants.PROTOCOL_VERSION,STFMPStatus.INVALID,STFMPMessage.INVALID);
        }else{
            String filename = request.getFilename();
            filename = filename.trim();
            if(STFMPActions.searchFile(filename) == 200){
                String content = STFMPActions.readFile(filename);
                response = new STFMPResponse(Constants.PROTOCOL_VERSION,STFMPStatus.OK,content);
            }else{
                response = new STFMPResponse(Constants.PROTOCOL_VERSION,STFMPStatus.NOT_FOUND,STFMPMessage.NOT_FOUND);
            }


        }
        sendResponse(connection,response);
    }
    private static void STFMPWrite(Socket connection, STFMPRequest request) throws IOException{

        STFMPResponse response;
        String params = request.getParams();
        if (params.split("#").length != 2) {
            response = new STFMPResponse(Constants.PROTOCOL_VERSION,STFMPStatus.INVALID,STFMPMessage.INVALID);
        }else{
            System.out.println("Writing...");
            String filename = request.getFilename();
            String content = request.getContent();
            STFMPActions.writeFile(filename, content);
            response = new STFMPResponse(Constants.PROTOCOL_VERSION,STFMPStatus.OK,STFMPMessage.SUCCESS);
        }
        sendResponse(connection,response);
    }


}
