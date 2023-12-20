package mb.cloudnative.passwordreset.api;

import lombok.Data;

@Data
public class PasswordResetRequest {

    private String username;

    private String newPassword;
    private String resetToken;
}
