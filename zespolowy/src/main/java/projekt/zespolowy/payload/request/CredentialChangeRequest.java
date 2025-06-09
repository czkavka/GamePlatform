package projekt.zespolowy.payload.request;

public class CredentialChangeRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
    private String newUsername;

    public String getCurrentPassword() {
        return currentPassword;
    }
    public void setCurrentPassword(String oldPassword) {
        this.currentPassword = oldPassword;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public String getNewUsername() {
        return newUsername;
    }
    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
