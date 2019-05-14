package sample;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer implements Runnable {

  @Override
  public void run() {
    int port = 17000;
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      char[] buffer = new char[2048];

      while (true) {
        System.out.print("Echo server listening on port " + port + "...");

        Socket client = serverSocket.accept();

        System.out.print("Client connected.");

        InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
            client.getOutputStream());

        while (client.isConnected()) {
          int read = inputStreamReader.read(buffer);

          String message = new String(buffer);
          System.out.print("Received: " + message);

          if (read > 0) {
            System.out.print("Echoing: " + message);

            outputStreamWriter.write(buffer);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
