import java.io.*;
import java.util.Scanner;

public class STFMPActions {

    public static final String WRITE = "write";
    public static final String VIEW = "view";
    public static final String CLOSE = "close";


    public static int searchFile(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            return 200;
        }else{
            return 404;
        }
    }

    public static void createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
        } else {
            System.out.println("File already exists.");
        }
    }

    public static void writeFile(String filePath, String message) throws IOException {
        if(searchFile(filePath) == 404){
            createFile(filePath);
        }
        OutputStream outputStream = new FileOutputStream(filePath);
        PrintWriter printWriter = new PrintWriter(outputStream,true);
        printWriter.write(message);

        printWriter.flush();
        System.out.println("The data has been written!");

    }

    public static String readFile(String filePath) {
        try {
            InputStream inputStream = new FileInputStream(filePath);
            Scanner scanner = new Scanner(inputStream);
            if(scanner.hasNext()) {
                String line = scanner.nextLine();
                System.out.println("Line: " + line);
                return line;
            }
            scanner.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }



}
