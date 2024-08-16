package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Title should not be blank")
    @Size(min = 2, max = 64)
    private String username;

    @Email
    @Size(max = 64)
    private String email;
}
