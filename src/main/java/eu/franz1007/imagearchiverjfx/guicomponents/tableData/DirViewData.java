package eu.franz1007.imagearchiverjfx.guicomponents.tableData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DirViewData {

    public StringProperty amount;
    public StringProperty displayFile;

    public String file;

    public boolean isDirectory;

    public DirViewData(String file, String amount){
        this.isDirectory = file.endsWith("/");
        if(isDirectory) {
            this.displayFile = new SimpleStringProperty(file.substring(file.lastIndexOf('/', file.length()-2) + 1, file.length()-1));
        }
        else{
            this.displayFile = new SimpleStringProperty(file.substring(file.lastIndexOf('/') + 1));
        }
        this.amount = new SimpleStringProperty(amount);
        this.file=file;
    }


    public String getDisplayFile() {
        return displayFile.get();
    }

    public StringProperty displayFileProperty() {
        return displayFile;
    }

    public String getAmount() {
        return amount.get();
    }

    public StringProperty amountProperty() {
        return amount;
    }


}
