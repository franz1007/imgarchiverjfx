package eu.franz1007.imagearchiverjfx.util;

import eu.franz1007.imagearchiver.containers.FilePathsStruct;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.File;

public class TableService {
    public static TableColumn<FilePathsStruct, ImageView> imageColumn(){
        TableColumn<FilePathsStruct, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<ImageView> call(TableColumn.CellDataFeatures<FilePathsStruct, ImageView> param) {
                ImageView imageView = new ImageView();
                System.out.println("outdir/" + param.getValue().getRelativeStoragePath());
                Image image = new Image(new File("outdir/" + param.getValue().getRelativeStoragePath()).toURI().toString(),40,40,true, false, true);
                imageView.setImage(image);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
                return new SimpleObjectProperty<>(imageView);
            }
        });
        return imageColumn;
    }
}
