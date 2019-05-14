package sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

  private Socket client;
  private InputStream server;

  @Override
  public void start(Stage primaryStage) throws Exception {
    new Thread(new EchoServer()).start();

    Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
    primaryStage.setTitle("Hello World");

    TextField textFieldChat = new TextField();
    textFieldChat.setLayoutX(10);
    textFieldChat.setLayoutY(0);
    textFieldChat.setMinHeight(100);
    textFieldChat.setAlignment(Pos.TOP_LEFT);

    TextField textFieldToSend = new TextField();
    textFieldToSend.setLayoutX(10);
    textFieldToSend.setLayoutY(110);
    textFieldToSend.setMinHeight(20);

    Label labelIsConnected = new Label("Not connected to server.");
    labelIsConnected.setLayoutX(10);
    labelIsConnected.setLayoutY(140);

    Button buttonSend = new Button("Send");
    buttonSend.setLayoutX(200);
    buttonSend.setLayoutY(110);
    buttonSend.setMaxSize(100, 200);
    buttonSend.setOnAction(actionEvent -> {
      try {
        PrintStream output = new PrintStream(client.getOutputStream());
        output.print(textFieldToSend.getText());
        textFieldChat.appendText("Me: " + textFieldToSend.getText());
        textFieldToSend.setText("");
      } catch (IOException ex) {
        System.out.print("Hiba a kuldes kozben.");
      }
    });

    Button buttonConnect = new Button("Connect");
    buttonConnect.setLayoutX(200);
    buttonConnect.setLayoutY(50);
    buttonConnect.setMaxSize(100, 200);
    buttonConnect.setOnAction(actionEvent -> {
      try {

        client = new Socket("127.0.0.1", 17000);
        labelIsConnected.setText("Client successfully connected to server!");
        server = client.getInputStream();
        new Thread(new ReceivedMessagesHandler(textFieldChat)).start();
      } catch (IOException e) {
        e.printStackTrace();
      }

    });

    Pane pane = new Pane(labelIsConnected, buttonSend, buttonConnect, textFieldToSend,
        textFieldChat);
    pane.setMinWidth(800);
    pane.setMinHeight(600);
    Scene scene = new Scene(pane, 200, 100);

    primaryStage.setScene(scene);
    primaryStage.setMinWidth(800);
    primaryStage.setMinHeight(600);
    primaryStage.show();
  }


  public static void main(String[] args) {
    launch(args);
  }

  private class ReceivedMessagesHandler implements Runnable {

    private char[] buffer = new char[2048];
    private TextField textFieldChat;

    public ReceivedMessagesHandler(TextField textFieldChat) {
      this.textFieldChat = textFieldChat;
    }

    public void run() {
      /*
      InputStreamReader inputStreamReader = new InputStreamReader(server);
      try {
        while (!inputStreamReader.ready()) {
          Thread.sleep(10L);
        }
        inputStreamReader.read(buffer);
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }

       */

      Scanner scanner = new Scanner(server);


      //String messageFromServer = new String(buffer);
      while (scanner.hasNext()) {
        String messageFromServer = scanner.next();
        System.out.print("Received from server: " + messageFromServer);

        Platform.runLater(() -> {
          textFieldChat.appendText("SERVER: " + messageFromServer);
        });
      }

    }
  }
}
