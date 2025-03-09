module com.example.module3_playingcards {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.scripting;
    requires java.xml;


    opens com.example.module3_playingcards to javafx.fxml;
    exports com.example.module3_playingcards;
}