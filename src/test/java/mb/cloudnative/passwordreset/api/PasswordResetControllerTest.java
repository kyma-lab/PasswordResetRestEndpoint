package mb.cloudnative.passwordreset.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import mb.cloudnative.passwordreset.domain.UserEntity;
import mb.cloudnative.passwordreset.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PasswordResetControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PasswordResetController instanceUnderTest;

    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(instanceUnderTest).build();
    }

    @Test
    void testRequestPasswordResetSuccess() throws Exception {

        // Mocking the user service response
        UserEntity mockedUser = UserEntity.builder().resetToken("mockedResetToken").build();
        when(userService.findByUsername("existingUser")).thenReturn(Optional.of(mockedUser));

        // Create a request with a JSON body
        PasswordResetRequest request = new PasswordResetRequest();
        request.setUsername("existingUser");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset request sent. Check your email for instructions: mockedResetToken"))
                .andReturn();

        // Verify that the userService.generateResetToken was called with the correct parameters
        verify(userService).generateResetToken(mockedUser);
    }


    // Helper method to convert objects to JSON strings
    private String asJsonString(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void testRequestPasswordResetUserNotFound() throws Exception {

        // Mocking the user service response for a non-existing user
        when(userService.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        // Create a request with a JSON body
        PasswordResetRequest request = new PasswordResetRequest();
        request.setUsername("nonExistingUser");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        // Perform the request and assert the response
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

    }
}