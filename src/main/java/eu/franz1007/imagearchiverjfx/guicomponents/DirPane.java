package eu.franz1007.imagearchiverjfx.guicomponents;

import eu.franz1007.imagearchiver.ImageArchiver;
import eu.franz1007.imagearchiver.containers.FilePathsStruct;
import eu.franz1007.imagearchiverjfx.guicomponents.tableData.DirViewData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.nio.file.Files;

public class DirPane extends BorderPane {

    private final ImageArchiver imageArchiver;

    String selectedDir = "";

    private TableView<DirViewData> center = new TableView<>();

    {
        Button dirUp = new Button("→");
        dirUp.setOnAction(e -> selectDir(false));
        dirUp.getStyleClass().add(JMetroStyleClass.LIGHT_BUTTONS);
        Button dirDown = new Button("←");
        dirDown.setOnAction(e -> selectDir(true));
        dirDown.getStyleClass().add(JMetroStyleClass.LIGHT_BUTTONS);
        HBox top = new HBox(dirDown, dirUp);
        TableColumn<DirViewData, String> nameColumn = new TableColumn<>("Directory");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayFile"));
        nameColumn.setPrefWidth(300);
        TableColumn<DirViewData, String> countColumn = new TableColumn<>("Type");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        countColumn.setPrefWidth(150);
        center.getColumns().add(nameColumn);
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

    private void updateItems() {
        ObservableList<DirViewData> list = FXCollections.observableList(
                imageArchiver.getIndexMap().values()
                        .stream()
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
                                return new DirViewData(str, "directory");
                            } else {
                                return new DirViewData(str, "file");
                            }
                        }).toList()
        );
        center.setItems(list);
    }

    public DirPane(ImageArchiver imageArchiver) {
        this.imageArchiver = imageArchiver;
        updateItems();
    }
}
