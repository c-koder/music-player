package root;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class Main extends Application {
    static Controller myControllerHandle;

    public void start(final Stage primaryStage) throws IOException, URISyntaxException {
        Font.loadFont(getClass().getResourceAsStream("font/Karla.ttf"), 16);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/base.fxml"));
        Parent root = loader.load();
        myControllerHandle = loader.getController();

        primaryStage.setTitle("Music Playah!");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("media/gradients/baseline_music_note_white_24dp.png")).toURI().toString()));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(220);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(true);
        primaryStage.show();

        if (getParameters().getRaw().size() > 0) {
            String path = getParameters().getRaw().get(0);
            myControllerHandle.setFile(new File(path));
        }
        primaryStage.setOnCloseRequest(event -> Platform.exit());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
