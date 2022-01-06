package root;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VersionHistoryController {
    @FXML
    private Label versionHistoryLabel;

    @FXML
    private void initialize() {
        String history = "" +
                "Version History\n\n" +
                "1.4.7\n" +
                "*playlist interface updated slightly\n" +
                "\n" +
                "1.4.6\n" +
                "*code optimized.\n" +
                "*playlist functionality improved.\n" +
                "*loading times reduced.\n" +
                "*playlist song removal bug fixed.\n" +
                "\n" +
                "1.4.5\n" +
                "*a bug where a single file won't return to 0 in seek slider fixed.\n" +
                "*a bug where playback repeat won't function fixed.\n" +
                "*a bug where replay function once the song ended doesn't work fixed.\n" +
                "*a bug where volume icon won't change fixed.\n" +
                "\n" +
                "1.4.4\n" +
                "*shortcuts updated\n" +
                "*a bug where the previous/next functions in the playlist wont work, fixed.\n" +
                "\n" +
                "1.4.3\n" +
                "audio menu functions implemented.\n" +
                "program info implemented.\n" +
                "copyright info implemented." +
                "1.4.2\n" +
                "menu open files function implemented.\n" +
                "exit function implemented.\n" +
                "\n" +
                "1.4.1\n" +
                "playback repeat functionality implemented.\n" +
                "playlist repeat disabled when playback repeat is enabled and vice versa.\n" +
                "\n" +
                "1.4\n" +
                "playlist repeat functionality implemented.\n" +
                "\n" +
                "1.3.5\n" +
                "changed the sliders to change its color according to its position.\n" +
                "fixed a bug where the playlist doesn't play in order.\n" +
                "\n" +
                "1.3.4\n" +
                "added the duration of a playlist song.\n" +
                "\n" +
                "1.3.3\n" +
                "fixed a bug where dragging a song while the playlist is playing, wont play.\n" +
                "\n" +
                "1.3.2\n" +
                "ability to save the playlist.\n" +
                "ability to retrieve the playlist upon runtime if saved.\n" +
                "\n" +
                "1.3.1\n" +
                "menu bar added.\n" +
                "drag and drop files improved.\n" +
                "replay ability when song has stopped added.\n" +
                "\n" +
                "1.3\n" +
                "shortcut keys updated.\n" +
                "\n" +
                "1.2.2\n" +
                "next, previous functions implemented.\n" +
                "styling improved.\n" +
                "tooltips added.\n" +
                "\n" +
                "1.2.1\n" +
                "song addition/removal.\n" +
                "song play function.\n" +
                "song selection on list based on currently playing song.\n" +
                "\n" +
                "1.2\n" +
                "playlist implemented.\n" +
                "\n" +
                "1.1\n" +
                "playing .mp4 files.\n" +
                "resolution made to preserve window size.\n" +
                "\n" +
                "1.0\n" +
                "playing .mp3 files.\n" +
                "ability to pause/resume.\n" +
                "volume slider.\n" +
                "duration slider.\n" +
                "rewind/forward.\n" +
                "time duration.\n" +
                "\n";
        versionHistoryLabel.setText(history.toUpperCase());
    }
}
