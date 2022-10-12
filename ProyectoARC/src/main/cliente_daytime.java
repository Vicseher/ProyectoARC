import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class cliente_daytime {

  public static void main(String[] args) {

    String hostname = "hora.uv.es";

    try {
      Socket theSocket = new Socket(hostname, 13);
      InputStream timeStream = theSocket.getInputStream();
      StringBuffer time = new StringBuffer();
      int c;
      while ((c = timeStream.read()) != -1)
        time.append((char) c);
      String timeString = time.toString().trim();
      System.out.println("It is " + timeString + " at " + hostname);
    } // end try
    catch (UnknownHostException ex) {
      System.err.println(ex);
    } catch (IOException ex) {
      System.err.println(ex);
    }

  }

}
 
