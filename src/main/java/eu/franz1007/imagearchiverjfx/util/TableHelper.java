package eu.franz1007.imagearchiverjfx.util;

import eu.franz1007.imagearchiver.containers.FilePathsStruct;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class TableHelper {
    public static TableColumn<FilePathsStruct, ImageView> imageColumn(){
        TableColumn<FilePathsStruct, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(param -> {
            ImageView imageView = new ImageView();
            System.out.println("outdir/" + param.getValue().getRelativeStoragePath());
            Image image = new Image(new File("outdir/" + param.getValue().getRelativeStoragePath()).toURI().toString(),40,40,true, false, true);
            imageView.setImage(image);
            imageView.setFitHeight(40);
            imageView.setPreserveRatio(true);
            return new SimpleObjectProperty<>(imageView);
        });
        return imageColumn;
    }
    public static TableColumn<FilePathsStruct, String> nameColumn(){
        TableColumn<FilePathsStruct, String> nameColumn = new TableColumn<>("File name");
        nameColumn.setCellValueFactory(param -> {
            String storagePath = param.getValue().getRelativeStoragePath();
            String val = storagePath.substring(storagePath.lastIndexOf('/')+1);
            return new SimpleStringProperty(val);
        });
        return nameColumn;
    }

}
