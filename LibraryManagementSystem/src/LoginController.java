import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {
    
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passwordTF;
    @FXML
    private Button loginAdmBttn;
    @FXML
    private Button loginUsrBttn;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public String getMemberUsername() {
        return usernameTF.getText();
    }

    public double getMemberOwesMoney() {
        double owes = 0.0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT owes_money FROM MEMBER WHERE username = ?")) {
            stmt.setString(1, usernameTF.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                owes = rs.getDouble("owes_money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return owes;
    }

    @FXML
    private void initialize() {
        loginAdmBttn.setOnAction(event -> loginAsAdmin());
        loginUsrBttn.setOnAction(event -> loginAsUser());
    }
    
    private boolean isValidLogin(String username, String password, String userType) {
        String sql = userType.equals("ADMIN") ?
            "SELECT * FROM ADMIN WHERE username = ? AND password = ?" :
            "SELECT * FROM MEMBER WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @FXML
    private void loginAsAdmin() {
        String username = usernameTF.getText();
        String password = passwordTF.getText();

        if (isValidLogin(username, password, "ADMIN")) {
            try {
                mainApp.loadAdminView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            displayInvalidCredentialsAlert();
        }
    }
    @FXML
    private void loginAsUser() {
        String username = usernameTF.getText();
        String password = passwordTF.getText();
    
        if (isValidLogin(username, password, "MEMBER")) {
            try {
                UserSession.getInstance().setUser(username, getMemberOwesMoney());
                mainApp.loadUserView();  // No need to pass parameters here
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            displayInvalidCredentialsAlert();
        }
    }
    private void displayInvalidCredentialsAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Credentials");
        alert.setHeaderText(null);
        alert.setContentText("Invalid username or password. Please try again.");
        alert.showAndWait();
    }
}
