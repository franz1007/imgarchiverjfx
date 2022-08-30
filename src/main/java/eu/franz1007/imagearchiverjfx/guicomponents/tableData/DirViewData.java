package eu.franz1007.imagearchiverjfx.util.tableData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DirViewData {

    public StringProperty amount;
    public StringProperty directory;

    public DirViewData(String directory, String amount){
        this.directory = new SimpleStringProperty(directory);
        this.amount = new SimpleStringProperty(amount);
    }


    public String getDirectory() {
        return directory.get();
    }

    public StringProperty directoryProperty() {
        return directory;
    }

    public String getAmount() {
        return amount.get();
    }

    public StringProperty amountProperty() {
        return amount;
    }


}
