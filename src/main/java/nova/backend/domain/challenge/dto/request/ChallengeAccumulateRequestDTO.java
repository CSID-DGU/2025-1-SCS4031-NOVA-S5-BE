package nova.backend.domain.challenge.dto.request;

import nova.backend.domain.challenge.entity.ChallengeAccumulation;
import nova.backend.domain.challenge.entity.ChallengeParticipation;

public record ChallengeAccumulateRequestDTO(
        String qrCodeValue,
        int accumulateCount
) {

    public ChallengeAccumulation toEntity(ChallengeParticipation participation) {
        return ChallengeAccumulation.builder()
                .participation(participation)
                .accumulatedCount(accumulateCount)
                .build();
    }
}
