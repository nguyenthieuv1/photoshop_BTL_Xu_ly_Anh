module org.example.practice_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires java.desktop;


    opens org.example.practice_javafx to javafx.fxml;
    exports org.example.practice_javafx;
    exports org.example.practice_javafx.Controller;
    opens org.example.practice_javafx.Controller to javafx.fxml;
}