package mb.cloudnative.passwordreset.service;

import lombok.extern.slf4j.Slf4j;
import mb.cloudnative.passwordreset.domain.UserEntity;
import mb.cloudnative.passwordreset.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment environment;

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserEntity> findByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }


    public void generateResetToken(UserEntity user) {
        String resetToken = generateRandomToken();
        log.debug("Generate Token for user {} with value {}",user.getUsername(),resetToken);
        int validityHours = Integer.parseInt(environment.getProperty("token.validity.hours", "2"));

        LocalDateTime expiryDateTime = LocalDateTime.now().plus(validityHours, ChronoUnit.HOURS);

        user.setResetToken(resetToken);
        user.setTokenExpiryDateTime(expiryDateTime);

        userRepository.save(user);
    }

    public void resetPassword(UserEntity user, String newPassword) {
        // Implement password reset logic here
        // For simplicity, let's assume setting the new password directly
        user.setPassword(newPassword);
        user.setResetToken(null);
        user.setTokenExpiryDateTime(null);
        userRepository.save(user);
    }

    private String generateRandomToken() {
        // Implement token generation logic (e.g., using UUID.randomUUID())
        return UUID.randomUUID().toString();
    }
}
