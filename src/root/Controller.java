package root;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Controller {
    @FXML
    private MenuItem closePlaybackMenu;
    @FXML
    private MenuItem repeatPlaybackMenu;
    @FXML
    private MenuItem repeatPlaylistMenu;
    @FXML
    private Button playlistButton;
    @FXML
    private Button rewindButton;
    @FXML
    private Button forwardButton;
    @FXML
    private Button volumeButton;
    @FXML
    private Button playButton;
    @FXML
    private Label durationLabel;
    @FXML
    private Label nowPlayingLabel;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider durationSlider;
    @FXML
    private Button openButton;
    @FXML
    private MediaView mediaView;

    private Stage playlistStage = new Stage();
    private int i = 0;

    public static MediaPlayer mediaPlayer;
    public static ArrayList<File> playlist = new ArrayList<>();
    public static Label playPlaylist = new Label();
    public static Label nowPlaying = new Label();

    @FXML
    private void initialize() {
        borderPane.getBottom().minHeight(100);

        durationSlider.setFocusTraversable(true);
        volumeSlider.setFocusTraversable(false);
        forwardButton.setFocusTraversable(false);
        playButton.setFocusTraversable(false);
        volumeButton.setFocusTraversable(false);
        rewindButton.setFocusTraversable(false);
        playlistButton.setFocusTraversable(false);
        openButton.setFocusTraversable(false);

        durationSlider.setValue(0);

        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.setValue(100);

        hoverAnimations();
        sliderColorFunction();

        repeatPlaylistMenu.setOnAction(event -> {
            if (repeatPlaylistMenu.getText().equals("Repeat Playlist") && !repeatPlaybackMenu.getText().equals("Repeat Playback ✓")) {
                repeatPlaylistMenu.setText("Repeat Playlist ✓");
            } else if (repeatPlaylistMenu.getText().equals("Repeat Playlist ✓")) {
                repeatPlaylistMenu.setText("Repeat Playlist");
            } else {
                PlaylistController.showAlert("Error!", "Repeat Playlist is disabled since Repeat Playback is already enabled.");
            }
        });

        repeatPlaybackMenu.setOnAction(event -> {
            if (repeatPlaybackMenu.getText().equals("Repeat Playback") && !repeatPlaylistMenu.getText().equals("Repeat Playlist ✓")) {
                repeatPlaybackMenu.setText("Repeat Playback ✓");
            } else if (repeatPlaybackMenu.getText().equals("Repeat Playback ✓")) {
                repeatPlaybackMenu.setText("Repeat Playback");
            } else {
                PlaylistController.showAlert("Error!", "Repeat Playback is disabled since Repeat Playlist is already enabled.");
            }
        });

        closePlaybackMenu.setOnAction(event -> closePlayback());

        playPlaylist.textProperty().addListener((observableValue, oldText, newText) -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            if (newText.equals("Play Playlist")) {
                songFunction(playlist.get(0));
            } else {
                songFunction(new File(newText));
                for (int j = 0; j < playlist.size(); j++) {
                    if (playlist.get(j).getAbsolutePath().equals(newText)) {
                        i = j;
                    }
                }
            }
        });

        borderPane.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        borderPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                playlist.addAll(db.getFiles());
                if (db.getFiles().size() > 1)
                    songFunction(playlist.get(0));
                else
                    songFunction(db.getFiles().get(0));
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

        volumeIconFunction();
    }

    @FXML
    private void handleOpen() {
        if (mediaPlayer != null)
            mediaPlayer.pause();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select MP3/MP4");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("MP3/MP4 Files", "*.mp3", "*.mp4");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showOpenDialog(borderPane.getScene().getWindow());
        if (file != null) {
            if (playlist.size() == 1) {
                playlist.clear();
            }
            playlist.add(file);
        }
        songFunction(file);
    }

    public void setFile(File file) {
        playlist.add(file);
        songFunction(playlist.get(0));
    }

    private void songFunction(File file) {
        if (file != null) {
            durationSlider.requestFocus();
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setTitle(file.getName() + " - Music Playah!");
            nowPlayingLabel.setText("Now Playing: " + file.getName());

            String filepath = file.getAbsolutePath();
            Media media = new Media(new File(filepath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();

            nowPlaying.setText(file.getAbsolutePath());

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                if (repeatPlaybackMenu.getText().equals("Repeat Playback ✓")) {
                    songFunction(file);
                } else {
                    if (playlist.size() > 1) {
                        i++;
                        try {
                            if (i < playlist.size()) {
                                songFunction(playlist.get(i));
                            } else if (i == playlist.size()) {
                                if (repeatPlaylistMenu.getText().equals("Repeat Playlist ✓")) {
                                    i = 0;
                                    songFunction(playlist.get(i));
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {/*e.printStackTrace();*/}
                    } else {
                        mediaPlayer.stop();
                        try {
                            Image img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/baseline_replay_white_24dp.png")).toURI().toString());
                            ImageView view = new ImageView(img);
                            playButton.setGraphic(view);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            if (media.getSource().endsWith(".mp4")) {
                mediaView.setManaged(true);
                mediaView.setMediaPlayer(mediaPlayer);

                mediaView.fitWidthProperty().bind(borderPane.widthProperty());
                mediaView.fitHeightProperty().bind(borderPane.heightProperty());

                mediaView.setPreserveRatio(true);

            } else {
                borderPane.setCenter(null);
                mediaView.setManaged(false);
                mediaView.setMediaPlayer(null);

                borderPane.getScene().getWindow().setWidth(600);
                borderPane.getScene().getWindow().setHeight(220);
            }

            if (mediaPlayer.getStatus() == MediaPlayer.Status.UNKNOWN) {
                mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                    if (newStatus == MediaPlayer.Status.READY) {
                        durationSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                    }
                });
            }

            mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> {
                durationLabel.setText(getTimeString((long) newTime.toMillis()) + " / " + getTimeString((long) mediaPlayer.getTotalDuration().toMillis()));
                durationSlider.setValue(newTime.toSeconds());
            });

            durationSlider.valueProperty().addListener(ov -> {
                if (durationSlider.isPressed()) {
                    mediaPlayer.seek(Duration.millis(durationSlider.getValue()).multiply(1000));
                }
            });

            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));

            durationSlider.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    handleRewind();
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                    handleForward();
                }
            });

            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                Image img;
                ImageView view;
                if (newStatus == MediaPlayer.Status.PLAYING) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_pause_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (newStatus == MediaPlayer.Status.PAUSED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_play_arrow_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (newStatus == MediaPlayer.Status.STOPPED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/baseline_replay_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (newStatus == MediaPlayer.Status.DISPOSED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_pause_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    public void handlePause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                mediaPlayer.pause();
            } else if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)) {
                mediaPlayer.play();
            } else if (mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED) || mediaPlayer.getStatus().equals(MediaPlayer.Status.UNKNOWN)) {
                if (playlist != null) {
                    songFunction(playlist.get(0));
                }
            }
        }
    }

    @FXML
    private void handleVolumeScroll(ScrollEvent scrollEvent) {
        double zoomFactor = 1.05;
        double deltaY = scrollEvent.getDeltaY();
        if (deltaY < 0) {
            zoomFactor = 2.0 - zoomFactor;
        }
        if (zoomFactor > 1) {
            volumeSlider.setValue(volumeSlider.getValue() + 2);
        } else {
            volumeSlider.setValue(volumeSlider.getValue() - 2);
        }
    }

    @FXML
    private void handleKeyPress(KeyEvent keyEvent) throws IOException, URISyntaxException {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.SPACE)
            handlePause();
        else if (keyCode == KeyCode.M)
            handleMute();
        else if (keyCode == KeyCode.P && !keyEvent.isControlDown())
            handleOpenPlaylist();
        if (keyEvent.isControlDown() && keyCode.equals(KeyCode.O) && !keyEvent.isAltDown()) {
            handleOpen();
        }
        if (keyEvent.isControlDown() && keyEvent.isAltDown() && keyCode.equals(KeyCode.O)) {
            handleOpenFiles();
        }
        if (keyCode.equals(KeyCode.F4)) {
            closePlayback();
        }
        if (keyEvent.isControlDown() && keyCode.equals(KeyCode.P)) {
            if (repeatPlaybackMenu.getText().equals("Repeat Playback") && !repeatPlaylistMenu.getText().equals("Repeat Playlist ✓")) {
                repeatPlaybackMenu.setText("Repeat Playback ✓");
            } else if (repeatPlaybackMenu.getText().equals("Repeat Playback ✓")) {
                repeatPlaybackMenu.setText("Repeat Playback");
            } else {
                PlaylistController.showAlert("Error!", "Repeat Playback is disabled since Repeat Playlist is already enabled.");
            }
        }
        if (keyEvent.isControlDown() && keyCode.equals(KeyCode.L)) {
            if (repeatPlaylistMenu.getText().equals("Repeat Playlist") && !repeatPlaybackMenu.getText().equals("Repeat Playback ✓")) {
                repeatPlaylistMenu.setText("Repeat Playlist ✓");
            } else if (repeatPlaylistMenu.getText().equals("Repeat Playlist ✓")) {
                repeatPlaylistMenu.setText("Repeat Playlist");
            } else {
                PlaylistController.showAlert("Error!", "Repeat Playlist is disabled since Repeat Playback is already enabled.");
            }
        }
        if (keyCode.equals(KeyCode.ADD)) {
            handleIncreaseVolume();
        }
        if (keyCode.equals(KeyCode.SUBTRACT)) {
            handleDecreaseVolume();
        }
        if (keyCode.equals(KeyCode.S)) {
            handleViewShortcuts();
        }
    }

    @FXML
    private void handleRewind() {
        if (mediaPlayer != null)
            mediaPlayer.seek(Duration.seconds(durationSlider.getValue() - 7));
    }

    @FXML
    private void handleForward() {
        if (mediaPlayer != null)
            mediaPlayer.seek(Duration.seconds(durationSlider.getValue() + 7));
    }

    @FXML
    private void handleMute() throws URISyntaxException {
        if (volumeSlider.getValue() != 0) {
            Image img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/baseline_volume_off_white_24dp.png")).toURI().toString());
            ImageView view = new ImageView(img);
            volumeButton.setGraphic(view);

            volumeSlider.setValue(0);
        } else {
            Image img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_volume_up_white_24dp.png")).toURI().toString());
            ImageView view = new ImageView(img);
            volumeButton.setGraphic(view);

            volumeSlider.setValue(100);
        }
    }

    @FXML
    private void handleOpenPlaylist() throws IOException, URISyntaxException {
        if (!playlistStage.isShowing()) {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/root/fxml/playlist.fxml")));
            playlistStage.setTitle("Playlist - Music Playah!");
            playlistStage.setResizable(true);
            playlistStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("media/gradients/baseline_music_note_white_24dp.png")).toURI().toString()));

            playlistStage.setWidth(520);
            playlistStage.setHeight(300);

            playlistStage.setMinWidth(520);
            playlistStage.setMinHeight(300);

            playlistStage.setMaxWidth(520);
            playlistStage.setMaxHeight(600);

            playlistStage.setScene(new Scene(parent));
            playlistStage.centerOnScreen();
            playlistStage.show();

            PlaylistController.playlist = playlist;
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleOpenFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Songs");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("MP3 Files", "*.mp3");
        fileChooser.getExtensionFilters().add(extensionFilter);
        List<File> files = fileChooser.showOpenMultipleDialog(borderPane.getScene().getWindow());

        boolean fileFound = false;
        if (files != null) {
            for (File file : files) {
                if (!playlist.contains(file)) {
                    playlist.add(file);
                    fileFound = false;
                } else {
                    fileFound = true;
                }
            }
            songFunction(files.get(0));
        }
        if (fileFound)
            PlaylistController.showAlert("Error!", "One or more files already found.");
    }

    @FXML
    private void handleIncreaseVolume() {
        volumeSlider.setValue(volumeSlider.getValue() + 10);
    }

    @FXML
    private void handleDecreaseVolume() {
        volumeSlider.setValue(volumeSlider.getValue() - 10);
    }

    @FXML
    private void handleViewProgramInfo() throws IOException, URISyntaxException {
        loadScene("programInfo.fxml", "Program Info");
    }

    @FXML
    private void handleViewCopyrightInfo() throws IOException, URISyntaxException {
        loadScene("copyrightInfo.fxml", "Copyright Info");
    }

    @FXML
    private void handleViewVersionHistory() throws IOException, URISyntaxException {
        loadScene("versionHistory.fxml", "Version History");
    }

    @FXML
    private void handleViewShortcuts() throws IOException, URISyntaxException {
        loadScene("shortcuts.fxml", "Shortcuts");
    }

    private void sliderColorFunction() {
        durationSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = (durationSlider.getValue() - durationSlider.getMin()) / (durationSlider.getMax() - durationSlider.getMin()) * 100.0;
            return String.format("-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
                            + "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);",
                    percentage, percentage);
        }, durationSlider.valueProperty(), durationSlider.minProperty(), durationSlider.maxProperty()));

        volumeSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = (volumeSlider.getValue() - volumeSlider.getMin()) / (volumeSlider.getMax() - volumeSlider.getMin()) * 100.0;
            return String.format("-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
                            + "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);",
                    percentage, percentage);
        }, volumeSlider.valueProperty(), volumeSlider.minProperty(), volumeSlider.maxProperty()));
    }

    private void hoverAnimations() {
        volumeButton.setOnMouseEntered(mouseEvent -> {
            Image img;
            ImageView view;
            if (volumeSlider.getValue() >= 75) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/gradients/sharp_volume_up_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (volumeSlider.getValue() < 75 && volumeSlider.getValue() > 0) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/gradients/sharp_volume_down_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (volumeSlider.getValue() == 0) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/gradients/baseline_volume_off_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        volumeButton.setOnMouseExited(mouseEvent -> {
            Image img;
            ImageView view;
            if (volumeSlider.getValue() >= 75) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_volume_up_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (volumeSlider.getValue() < 75 && volumeSlider.getValue() > 0) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_volume_down_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (volumeSlider.getValue() == 0) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/baseline_volume_off_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        playButton.setOnMouseEntered(mouseEvent -> {
            if (mediaPlayer == null) {
                Image img;
                ImageView view;
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/gradients/sharp_pause_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    playButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                Image img;
                ImageView view;
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/gradients/sharp_pause_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/gradients/sharp_play_arrow_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/gradients/baseline_replay_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (mediaPlayer.getStatus() == MediaPlayer.Status.DISPOSED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/gradients/sharp_pause_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        playButton.setOnMouseExited(mouseEvent -> {
            if (mediaPlayer == null) {
                Image img;
                ImageView view;
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_pause_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    playButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                Image img;
                ImageView view;
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_pause_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_play_arrow_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/baseline_replay_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (mediaPlayer.getStatus() == MediaPlayer.Status.DISPOSED) {
                    try {
                        img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_pause_white_24dp.png")).toURI().toString());
                        view = new ImageView(img);
                        playButton.setGraphic(view);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        hoverButton(rewindButton, "sharp_fast_rewind_white_24dp.png");
        hoverButton(forwardButton, "sharp_fast_forward_white_24dp.png");
        hoverButton(openButton, "baseline_open_in_new_white_24dp.png");
        hoverButton(playlistButton, "baseline_featured_play_list_white_24dp.png");
    }

    public static void hoverButton(Button button, String icon) {
        button.setOnMouseEntered(mouseEvent -> {
            Image img;
            ImageView view;
            try {
                img = new Image(Objects.requireNonNull(Controller.class.getResource("media/gradients/" + icon)).toURI().toString());
                view = new ImageView(img);
                button.setGraphic(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        button.setOnMouseExited(mouseEvent -> {
            Image img;
            ImageView view;
            try {
                img = new Image(Objects.requireNonNull(Controller.class.getResource("media/1x/" + icon)).toURI().toString());
                view = new ImageView(img);
                button.setGraphic(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void volumeIconFunction() {
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            Image img;
            ImageView view;
            if (newValue.doubleValue() >= 75) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_volume_up_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (newValue.doubleValue() < 75 && newValue.doubleValue() > 0) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/sharp_volume_down_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (newValue.doubleValue() == 0) {
                try {
                    img = new Image(Objects.requireNonNull(getClass().getResource("media/1x/baseline_volume_off_white_24dp.png")).toURI().toString());
                    view = new ImageView(img);
                    volumeButton.setGraphic(view);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void closePlayback() {
        if (mediaPlayer != null) {
            File file = new File(mediaPlayer.getMedia().getSource());
            playlist.removeIf(checkFile -> checkFile == file);

            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setTitle("Music Playah!");

            nowPlayingLabel.setText("Now Playing: ");
            durationLabel.setText("00:00:00 / 00:00:00");

            mediaPlayer.dispose();
            durationSlider.setValue(0);
        }
    }

    public static String getTimeString(long millis) {

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        return String.format("%02d", hours) +
                ":" +
                String.format("%02d", minutes) +
                ":" +
                String.format("%02d", seconds);
    }

    private void loadScene(String fxml, String title) throws IOException, URISyntaxException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/root/fxml/" + fxml)));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(borderPane.getScene().getWindow());
        stage.setTitle(title);
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("media/gradients/baseline_music_note_white_24dp.png")).toURI().toString()));
        if (fxml.equals("copyrightInfo.fxml")) {
            stage.setScene(new Scene(parent, 600, 600));
        } else {
            stage.setScene(new Scene(parent));
        }
        stage.centerOnScreen();
        stage.show();
    }
}
