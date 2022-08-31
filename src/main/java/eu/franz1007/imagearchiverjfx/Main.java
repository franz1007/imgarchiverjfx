package eu.franz1007.imagearchiverjfx;


import eu.franz1007.imagearchiver.ImageArchiver;
import eu.franz1007.imagearchiverjfx.guicomponents.DirPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

import java.io.File;

public class Main extends Application {

    final Object mutex = new Object();
    File currentOutDir = null;

    private final ImageArchiver imageArchiver = new ImageArchiver();

    private final DirPane dirPane = new DirPane(imageArchiver);

    public static void main(String[] args) {
        ImageArchiver imageArchiver = new ImageArchiver();
        imageArchiver.setOutDir(new File("outdir"));
        //imageArchiver.importDirectory(new File("/mnt/d/SavesSortieren/save_SEA_DISC/Pictures/Alexfotos/Juni bis August 10/Besuch Moni Aug 2010"));

        System.out.println("Hello world!");
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);

        imageArchiver.setOutDir(new File("outdir"));
        currentOutDir = new File("outdir");
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        Button importButton = new Button("Import Directory");
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Directory to import");
        importButton.setOnAction(e -> {
            File f = dirChooser.showDialog(scene.getWindow());
            System.out.println(f);
            if (f != null) {
                Thread t = new Thread(() -> {
                    synchronized (mutex) {
                        System.out.println("Started importing");
                        imageArchiver.importDirectory(f);
                        System.out.println("Finished importing");
                        dirPane.updateItems();
                    }
                });
                t.start();
                t.interrupt();
            }
        });
        root.setCenter(dirPane);
        root.setTop(importButton);
        importButton.getStyleClass().add(JMetroStyleClass.LIGHT_BUTTONS);

        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }
}