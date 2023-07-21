module memocardbuilder.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens memocardbuilder.scenes to javafx.fxml; //where controllers are located (fxmlControllers)
    exports memocardbuilder.scenes; //uses fxml
    exports memocardbuilder; //uses Application and others


}
