package root;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ProgramInfoController {
    @FXML
    private Label versionLabel;
    @FXML
    private Label buildDateLabel;

    @FXML
    private void initialize() {
        versionLabel.setText("Version: 1.4.7.0");

        Path path = Paths.get("D:/Desktop Backup/Java Projects/New/MusicPlayer/out/production/MusicPlayer/root/Main.class");
        try {
            FileTime fileTime = Files.getLastModifiedTime(path);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            buildDateLabel.setText("Build Date: " + dateFormat.format(fileTime.toMillis()));
        } catch (IOException e) {
//            System.err.println("Cannot get the last modified time - " + e);
        }
    }
}
