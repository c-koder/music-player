package root;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShortcutsController {
    @FXML
    private Label keysLabel;
    @FXML
    private Label shortcutsLabel;

    @FXML
    private void initialize() {
        String shortcuts = "" +
                "Menu Bar\n" +
                "------------\n" +
                "Open File(s)\n" +
                "Exit\n" +
                "Close Playback\n" +
                "Repeat Playback\n" +
                "Repeat Playlist\n" +
                "Mute\n" +
                "Increase Volume\n" +
                "Decrease Volume\n" +
                "Shortcuts\n\n" +

                "Playback\n" +
                "------------\n" +
                "Play/Pause\n" +
                "Rewind\n" +
                "Forward\n" +
                "Mute\n" +
                "Open\n" +
                "Playlist\n\n" +

                "Playlist\n" +
                "------------\n" +
                "Play Playlist\n" +
                "Previous\n" +
                "Next\n" +
                "Add Song\n" +
                "Remove Song\n" +
                "Save List\n";

        String keys = "" +
                "\n\n" +
                "Ctrl+Alt+O" + "\n" +
                "Ctrl+O" + "\n" +
                "F4" + "\n" +
                "Ctrl+P" + "\n" +
                "Ctrl+L" + "\n" +
                "M" + "\n" +
                "Num+" + "\n" +
                "Num-" + "\n" +
                "S" + "\n\n" +

                "\n\n" +
                "Space" + "\n" +
                "Left Arrow" + "\n" +
                "Right Arrow" + "\n" +
                "M" + "\n" +
                "Ctrl+O" + "\n" +
                "P" + "\n\n" +

                "\n\n" +
                "V" + "\n" +
                "," + "\n" +
                "." + "\n" +
                "Num+" + "\n" +
                "Del" + "\n" +
                "Ctrl+S" + "\n";
        shortcutsLabel.setText(shortcuts);
        keysLabel.setText(keys);
    }
}
