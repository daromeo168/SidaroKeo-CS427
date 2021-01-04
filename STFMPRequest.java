import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class STFMPRequest {
    private String action;
    private String filename;
    private String content;
    private String protocolVersion;
    private String params;


    public STFMPRequest(String protocolVersion,String action, String params){
        this.protocolVersion = protocolVersion;
        this.action = action;
        this.params = params;
        if(params != null){
            String[] listParams = params.split("#");
            if(listParams.length  == 1){
                this.filename = listParams[0];
            }else if(listParams.length == 2){
                this.filename = listParams[0];
                this.content = listParams[1];
            }
        }



    }

    public String getAction() {
        return action;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getParams() {
        return params;
    }

    public String encryptedRequest(){
        String requestLine = protocolVersion+"##"+action+"##"+params+"\r\n";
        requestLine = Constants.ENCRYPTE(requestLine);
        return requestLine;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static STFMPRequest fromEncryptedString(String encryptedRequest){
       String rawRequest = Constants.DECRYPTE(encryptedRequest);
       System.out.println("Decrypting: "+rawRequest);
       String[] parts = rawRequest.split("##");
       String protocolVersion = parts[0];
       String action = parts[1];
       String params = parts[2];

       return new STFMPRequest(protocolVersion,action,params);
    }
}
