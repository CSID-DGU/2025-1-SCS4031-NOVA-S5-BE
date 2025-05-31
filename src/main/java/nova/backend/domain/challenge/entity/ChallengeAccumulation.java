package nova.backend.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.*;
import nova.backend.domain.challenge.repository.ChallengeAccumulationRepository;
import nova.backend.global.common.BaseTimeEntity;
import nova.backend.global.error.ErrorCode;
import nova.backend.global.error.exception.BusinessException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChallengeAccumulation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accumulationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false)
    private ChallengeParticipation participation;

    @Column(nullable = false)
    private int accumulatedCount;

    public static ChallengeAccumulation create(
            ChallengeParticipation participation,
            int accumulateCount
    ) {
        return ChallengeAccumulation.builder()
                .participation(participation)
                .accumulatedCount(accumulateCount)
                .build();
    }

    public void decreaseAccumulatedCount(int count) {
        if (count <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (count > this.accumulatedCount) {
            throw new BusinessException(ErrorCode.CHALLENGE_OVER_REQUEST);
        }
        this.accumulatedCount -= count;
    }
}
