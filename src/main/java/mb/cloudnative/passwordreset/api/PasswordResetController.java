package mb.cloudnative.passwordreset.api;

import mb.cloudnative.passwordreset.domain.UserEntity;
import mb.cloudnative.passwordreset.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset( @RequestBody PasswordResetRequest request) {
        Optional<UserEntity> userOptional = userService.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            userService.generateResetToken(user);
            return ResponseEntity.ok("Password reset request sent. Check your email for instructions: "+user.getResetToken());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        Optional<UserEntity> userOptional = userService.findByResetToken(request.getResetToken());

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            if (user.getTokenExpiryDateTime().isAfter(LocalDateTime.now())) {
                userService.resetPassword(user, request.getNewPassword());
                return ResponseEntity.ok("Password reset successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reset token has expired");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid reset token");
        }
    }
}

