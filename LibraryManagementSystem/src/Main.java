import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private LoginController loginController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        loadLoginView();
        primaryStage.show();
    }

    public void loadLoginView() throws Exception{
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
        Scene loginScene = new Scene(loginLoader.load());
        loginController = loginLoader.getController();
        loginController.setMainApp(this);
        primaryStage.setScene(loginScene);
    }

    public void loadAdminView() throws Exception {
       try{ 
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("AdminPanel.fxml"));
        Scene adminScene = new Scene(adminLoader.load());
        AdminController adminController = adminLoader.getController();
        adminController.setMainApp(this);
        primaryStage.setScene(adminScene);

       }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    public void loadUserView() throws Exception {
        FXMLLoader mainViewLoader = new FXMLLoader(getClass().getResource("MemberView.fxml"));
        Scene mainViewScene = new Scene(mainViewLoader.load());
        MemberViewController memberViewController = mainViewLoader.getController();
        memberViewController.setMainApp(this);
        primaryStage.setScene(mainViewScene);
    }
    
}
