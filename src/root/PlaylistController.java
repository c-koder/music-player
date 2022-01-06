package root;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PlaylistController {
    @FXML
    private Button saveButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Button removeSongButton;
    @FXML
    private ListView<File> playlistView;
    @FXML
    private Button addSongButton;
    @FXML
    private Button playPlaylistButton;

    public static ArrayList<File> playlist = new ArrayList<>();

    private ObservableList<File> removeFiles;

    @FXML
    private void initialize() throws IOException {
        playlist = Controller.playlist;

        ArrayList<File> fileList = readPlaylist();
        if (fileList != null) {
            for (File file : fileList) {
                if (!playlist.contains(file)) {
                    playlist.add(file);
                }
            }
        }

        if (playlist != null) {
            for (File file : playlist) {
                playlistView.getItems().add(file);
            }
        }

        previousButton.setFocusTraversable(false);
        addSongButton.setFocusTraversable(false);
        playPlaylistButton.setFocusTraversable(false);
        removeSongButton.setFocusTraversable(false);
        nextButton.setFocusTraversable(false);

        removeSongButton.setOnAction(event -> handleRemoveSong());

        final ContextMenu menu = new ContextMenu();
        final MenuItem deleteSongMenu = new MenuItem("Delete...");
        deleteSongMenu.setOnAction(event -> handleRemoveSong());
        deleteSongMenu.disableProperty().bind(Bindings.isEmpty(playlistView.getSelectionModel().getSelectedItems()));
        menu.getItems().addAll(deleteSongMenu);
        playlistView.setContextMenu(menu);

        playlistView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        playlistView.setCellFactory(param -> new ModifiedCell());

        playlistView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                for (File file : playlist) {
                    if (playlistView.getSelectionModel().getSelectedItem().equals((file))) {
                        Controller.playPlaylist.setText(file.getAbsolutePath());
                        playlistView.getSelectionModel().select(file);
                    }
                }
            } else if (click.getClickCount() == 1) {
                removeFiles = playlistView.getSelectionModel().getSelectedItems();
            }
        });

        borderPane.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
            if (evt.getCode().equals(KeyCode.ESCAPE)) {
                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.close();
            }
        });

        borderPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.COMMA) {
                handlePrevious();
            } else if (keyEvent.getCode() == KeyCode.PERIOD) {
                handleNext();
            } else if (keyEvent.getCode() == KeyCode.DELETE) {
                handleRemoveSong();
            } else if (keyEvent.getCode() == KeyCode.ADD) {
                handleAddSong();
            } else if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.S)) {
                try {
                    handleSave();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (keyEvent.getCode() == KeyCode.V) {
                handlePlayPlaylist();
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A) {
                playlistView.getSelectionModel().selectAll();
            }
        });

        borderPane.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        borderPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                boolean fileFound = false;
                if (db.getFiles() != null) {
                    for (File checkFile : db.getFiles()) {
                        if (!playlistView.getItems().contains(checkFile)) {
                            playlistView.getItems().add(checkFile);
                            playlist.add(checkFile);
                            fileFound = false;
                        } else {
                            fileFound = true;
                        }
                    }
                }
                if (fileFound)
                    showAlert("Error!", "One or more files already found.");
                Controller.playlist = playlist;
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

        if (Controller.nowPlaying != null) {
            Controller.nowPlaying.textProperty().addListener((observableValue, oldSong, newSong) -> {
                for (File file : playlistView.getItems()) {
                    if (file.getAbsolutePath().equals(newSong)) {
                        playlistView.getSelectionModel().clearSelection();
                        playlistView.getSelectionModel().select(file);
                    }
                }
            });
        }

        hoverAnimations();
    }

    @FXML
    private void handleAddSong() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Songs");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("MP3 Files", "*.mp3");
        fileChooser.getExtensionFilters().add(extensionFilter);
        List<File> files = fileChooser.showOpenMultipleDialog(borderPane.getScene().getWindow());

        boolean fileFound = false;
        if (files != null) {
            for (File file : files) {
                if (!playlistView.getItems().contains(file)) {
                    playlistView.getItems().add(file);
                    playlist.add(file);
                    fileFound = false;
                } else {
                    fileFound = true;
                }
            }
        }
        if (fileFound)
            showAlert("Error!", "One or more files already found.");
        Controller.playlist = playlist;
    }

    @FXML
    private void handlePlayPlaylist() {
        Controller.playPlaylist.setText("Play Playlist");
    }

    @FXML
    private void handleNext() {
        String currentSongFileLocation = Controller.nowPlaying.getText();
        String nextSongFileLocation = "";
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).getAbsolutePath().equals(currentSongFileLocation)) {
                if (i + 1 < playlist.size()) {

                    nextSongFileLocation = playlist.get(i + 1).getAbsolutePath();
                } else if (i + 1 == playlist.size()) {
                    nextSongFileLocation = playlist.get(0).getAbsolutePath();
                }
            }
        }
        Controller.playPlaylist.setText(nextSongFileLocation);
    }

    @FXML
    private void handlePrevious() {
        String currentSongFileLocation = Controller.nowPlaying.getText();
        String nextSongFileLocation = "";
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).getAbsolutePath().equals(currentSongFileLocation)) {
                if (i - 1 >= 0) {
                    nextSongFileLocation = playlist.get(i - 1).getAbsolutePath();
                } else {
                    nextSongFileLocation = playlist.get(playlist.size() - 1).getAbsolutePath();
                }
            }
        }
        Controller.playPlaylist.setText(nextSongFileLocation);
    }

    @FXML
    private void handleSave() throws IOException {
        writeToPlaylist(playlist);
        showAlert("Success!", "Playlist saved.");
    }

    private void handleRemoveSong() {
        if (removeFiles != null) {
            ObservableList<File> list = FXCollections.observableArrayList(removeFiles);
            String currentSongFileLocation = Controller.nowPlaying.getText();

            if (list.size() > 1) {
                list.removeIf(file -> file.getAbsolutePath().equals(currentSongFileLocation));

                playlist.removeAll(list);
                playlistView.getItems().removeAll(list);
            } else {
                if (!playlistView.getSelectionModel().getSelectedItem().getAbsolutePath().equals(currentSongFileLocation)) {
                    playlist.remove(playlistView.getSelectionModel().getSelectedItem());
                    playlistView.getItems().remove(playlistView.getSelectionModel().getSelectedItem());
                }
            }
        }
    }

    private void hoverAnimations() {
        Controller.hoverButton(removeSongButton, "baseline_playlist_remove_white_24dp.png");
        Controller.hoverButton(addSongButton, "baseline_playlist_add_white_24dp.png");
        Controller.hoverButton(playPlaylistButton, "baseline_playlist_play_white_24dp.png");
        Controller.hoverButton(previousButton, "sharp_fast_rewind_white_24dp.png");
        Controller.hoverButton(nextButton, "sharp_fast_forward_white_24dp.png");
        Controller.hoverButton(saveButton, "baseline_save_white_24dp.png");

    }

    public static void showAlert(String title, String error) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);

        alert.setHeaderText(null);
        alert.setContentText(error);

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UTILITY);
        alert.setGraphic(null);
        alert.initStyle(StageStyle.TRANSPARENT);

        dialogPane.getStylesheets().add(Objects.requireNonNull(PlaylistController.class.getResource("styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            alert.hide();
        }
    }

    private void writeToPlaylist(ArrayList<File> list) throws IOException {
        PrintWriter dataOut = new PrintWriter(new FileOutputStream(System.getProperty("user.home") + "/playlist.dat", false));
        dataOut.print("");
        for (File file : list) {
            String filePath = file.getAbsolutePath();
            dataOut.write(filePath);
            dataOut.write("\n");
        }
        dataOut.close();
    }

    public ArrayList<File> readPlaylist() throws IOException {
        ArrayList<File> list = new ArrayList<>();
        BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.home") + "/playlist.dat")));
        String line;
        while ((line = fr.readLine()) != null) {
            list.add(new File(line));
        }
        fr.close();

        if (list.size() > 0)
            return list;
        else
            return null;
    }

    static class ModifiedCell extends ListCell<File> {
        GridPane gridPane;
        Label song;
        Label duration;

        public ModifiedCell() {
            super();
            gridPane = new GridPane();
            song = new Label();
            duration = new Label();
            song.setMaxWidth(380);
            GridPane.setHgrow(duration, Priority.ALWAYS);
            GridPane.setHalignment(duration, HPos.RIGHT);

            gridPane.setStyle("-fx-font-size: 13.5; -fx-text-fill: white");
            gridPane.setPadding(new Insets(3));

            gridPane.add(song, 0, 0);
            gridPane.add(duration, 1, 0);
        }

        @Override
        protected void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            super.setText(null);
            super.setGraphic(null);

            if (item != null && !empty) {
                try {
                    AudioFile audioFile = AudioFileIO.read(item);
                    LocalTime timeOfDay = LocalTime.ofSecondOfDay(audioFile.getAudioHeader().getTrackLength());
                    String time = timeOfDay.toString();
                    song.setText(item.getName());
                    this.duration.setText(time);
                    setGraphic(gridPane);

                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
    }
}
