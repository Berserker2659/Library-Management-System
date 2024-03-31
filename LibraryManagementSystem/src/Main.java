
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.*;
import library.LibrarySystem;
public class Main extends Application{

    public LibrarySystem getLibrarySystem() {
        return librarySystem;
    }

    
    private LibrarySystem librarySystem;
    public static void main(String [] args){
        launch(args);
    }
     
    public void start(Stage primaryStage) throws Exception {
        librarySystem = new LibrarySystem();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainViewController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();




    }
    
}

