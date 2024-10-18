module com.mgt103.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires okhttp3;


    opens com.mgt103.demo1 to javafx.fxml;
    exports com.mgt103.demo1;
}