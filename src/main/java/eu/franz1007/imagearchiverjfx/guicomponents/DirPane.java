package eu.franz1007.imagearchiverjfx.guicomponents;

import eu.franz1007.imagearchiver.ImageArchiver;
import eu.franz1007.imagearchiver.containers.FilePathsStruct;
import eu.franz1007.imagearchiverjfx.guicomponents.tableData.DirViewData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.File;
import java.nio.file.Files;
import java.util.Comparator;

public class DirPane extends BorderPane {

    private final Object mutex = new Object();

    private final ImageArchiver imageArchiver;

    String selectedDir = "";

    private final Button outdirLabel;
    private final TableView<DirViewData> center = new TableView<>();

    {
        Button dirUp = new Button("→");
        dirUp.setOnAction(e -> selectDir(false));
        dirUp.getStyleClass().add(JMetroStyleClass.LIGHT_BUTTONS);
        Button dirDown = new Button("←");
        dirDown.setOnAction(e -> selectDir(true));
        dirDown.getStyleClass().add(JMetroStyleClass.LIGHT_BUTTONS);
        HBox topLeft = new HBox(dirDown, dirUp);
        Button outdirButton = new Button("Choose output directory");
        outdirButton.setOnAction(e -> chooseOutDir());
        outdirButton.getStyleClass().add(JMetroStyleClass.LIGHT_BUTTONS);
        outdirLabel = new Button("test");
        outdirLabel.getStyleClass().add(JMetroStyleClass.LIGHT_BUTTONS);
        HBox topRight = new HBox(outdirLabel, outdirButton);
        BorderPane top = new BorderPane();
        top.setLeft(topLeft);
        top.setRight(topRight);
        TableColumn<DirViewData, String> nameColumn = new TableColumn<>("Directory");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayFile"));
        nameColumn.setPrefWidth(300);
        TableColumn<DirViewData, String> countColumn = new TableColumn<>();
        countColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        countColumn.setPrefWidth(150);
        TableColumn<DirViewData, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        center.getColumns().add(nameColumn);
        center.getColumns().add(typeColumn);
        center.getColumns().add(countColumn);
        center.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        center.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                selectDir(false);
            }
        });
        center.getStyleClass().add(JMetroStyleClass.ALTERNATING_ROW_COLORS);
        this.setTop(top);
        this.setCenter(center);
    }

    private void chooseOutDir() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(imageArchiver.getOutDir());
        File dir = dirChooser.showDialog(this.getScene().getWindow());
        if (dir != null) imageArchiver.setOutDir(dir);
        outdirLabel.setText("Output Directory: " + imageArchiver.getOutDir().toPath().toAbsolutePath());
        this.setCenter(new Label("Loading..."));
        Thread t = new Thread(()->{
            synchronized (mutex){
                Platform.runLater(()->setCenter(new Label("Loading...")));
                updateItems();
                Platform.runLater(()->setCenter(center));
            }
        });
        t.start();
    }

    private void selectDir(boolean up) {
        if (!up && center.getSelectionModel().getSelectedItem() != null && Files.isDirectory(imageArchiver.absolutize(center.getSelectionModel().getSelectedItem().file))) {
            selectedDir = center.getSelectionModel().getSelectedItem().file;
            System.out.println("new selectedDir: " + selectedDir);
            updateItems();
        }
        if (up && !selectedDir.equals("")) {
            if (selectedDir.matches("^.*/.*/$")) {
                selectedDir = selectedDir.substring(0, selectedDir.lastIndexOf('/', selectedDir.length() - 2) + 1);
            } else {
                selectedDir = "";
            }
            System.out.println("new selectedDir: " + selectedDir);
            updateItems();
        }
    }

    public void updateItems() {
        ObservableList<DirViewData> list = FXCollections.observableList(
                imageArchiver.getIndexMap().values()
                        .parallelStream()
                        .map(FilePathsStruct::getRelativeStoragePath)
                        .filter(path -> path.startsWith(selectedDir))
                        .map(str -> {
                            if (str.substring(selectedDir.length()).contains("/")) {
                                return str.substring(0, str.indexOf('/', selectedDir.length()) + 1);
                            } else {
                                return str;
                            }
                        }).distinct().map(str -> {
                            if (str.endsWith("/")) {
                                long amount = imageArchiver.getIndexMap().values()
                                        .parallelStream().filter(fps -> fps.getRelativeStoragePath().startsWith(str)).count();
                                return new DirViewData(str, "directory", Long.toString(amount));
                            } else {
                                return new DirViewData(str, "file", "");
                            }
                        }).sorted(Comparator.comparing(DirViewData::getDisplayFile)).toList()
        );
        center.setItems(list);
    }

    public DirPane(ImageArchiver imageArchiver) {
        this.imageArchiver = imageArchiver;
        updateItems();
        outdirLabel.setText("Output Directory: " + imageArchiver.getOutDir().toPath().toAbsolutePath());
    }
}
