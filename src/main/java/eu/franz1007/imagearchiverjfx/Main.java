package eu.franz1007.imagearchiverjfx;


import eu.franz1007.imagearchiver.ImageArchiver;
import eu.franz1007.imagearchiverjfx.guicomponents.DirPane;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Main extends Application {

    final Object mutex = new Object();
    File currentOutDir = null;

    private final ImageArchiver imageArchiver = new ImageArchiver();

    private final DirPane dirPane = new DirPane(imageArchiver);

    private final ProgressBar progressBar = new ProgressBar(0f);

    private final Label copyProgressLabel = new Label();
    private final Label hashProgressLabel = new Label();

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

        HBox progressBox = new HBox(new VBox(hashProgressLabel, copyProgressLabel), progressBar);
        BorderPane bottom = new BorderPane();
        bottom.setRight(progressBox);
        root.setBottom(bottom);

        importButton.setOnAction(e -> {
            File f = dirChooser.showDialog(scene.getWindow());
            System.out.println(f);
            if (f != null) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        synchronized (mutex) {
                            FutureTask<Void> futureTask = new FutureTask<>(() -> null);
                            Task<Void> task2 = new Task<>() {
                                @Override
                                protected Void call() {
                                    System.out.println("importing");
                                    imageArchiver.importDirectory(f);
                                    dirPane.updateItems();
                                    System.out.println("imported");
                                    return null;
                                }
                            };
                            Executors.newFixedThreadPool(1).execute(task2);
                            int i = 1;
                            while (++i < 100) {
                                System.out.println(i);
                                System.out.println(task2.getState());
                                int count = imageArchiver.status.count;
                                int hashed = imageArchiver.status.hashed;
                                int copied = imageArchiver.status.copied;
                                this.updateProgress(hashed + copied, count);
                                this.updateMessage("Files to hash: " + (count - hashed));
                                this.updateTitle("Files to copy: " + (count - copied));
                                System.out.println(copied);

                                this.updateProgress(imageArchiver.status.count, imageArchiver.status.count);
                                this.updateMessage("Files to hash: " + 0);
                                this.updateTitle("Files to copy: " + 0);
                            }
                            System.out.println("task not running");
                            return null;
                        }
                    }
                };
                Thread t = new Thread(task);
                copyProgressLabel.textProperty().bind(task.titleProperty());
                hashProgressLabel.textProperty().bind(task.messageProperty());
                progressBar.progressProperty().bind(task.progressProperty());
                t.start();
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