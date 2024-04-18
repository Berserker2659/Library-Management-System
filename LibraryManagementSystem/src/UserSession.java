public class UserSession {
    private static UserSession instance;
    private String username;
    private double owesMoney;

    private UserSession() {}  // Private constructor to prevent instantiation

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(String username, double owesMoney) {
        this.username = username;
        this.owesMoney = owesMoney;
    }

    public String getUsername() {
        return username;
    }

    public double getOwesMoney() {
        return owesMoney;
    }
}