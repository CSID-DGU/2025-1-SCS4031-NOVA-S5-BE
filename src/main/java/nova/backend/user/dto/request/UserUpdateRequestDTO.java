package nova.backend.user.dto.request;

import nova.backend.user.entity.User;

public record UserUpdateRequestDTO(
        String phoneNumber,
        String profileImageUrl,
        String major
) {
    public User toEntity() {
        return User.builder()
                .profileImageUrl(profileImageUrl)
                .build();
    }
}