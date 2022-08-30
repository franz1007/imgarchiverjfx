module imgarchiverjfx.main {
    requires bilderarchivierung.main;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires org.jfxtras.styles.jmetro;
    requires lombok;
    opens eu.franz1007.imagearchiverjfx to javafx.graphics;
    opens eu.franz1007.imagearchiverjfx.guicomponents.tableData to javafx.base;
    exports eu.franz1007.imagearchiverjfx;
}