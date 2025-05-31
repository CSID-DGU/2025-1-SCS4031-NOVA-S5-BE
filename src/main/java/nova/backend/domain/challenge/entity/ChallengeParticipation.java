package nova.backend.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.*;
import nova.backend.domain.challenge.entity.status.ParticipationStatus;
import nova.backend.domain.user.entity.User;
import nova.backend.global.common.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChallengeParticipation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus participationStatus; // 사용자의 참여 의사

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Challenge.ChallengeStatus challengeStatus; // 챌린지 목표 도달 여부

    @Column(nullable = false)
    private int completedCount = 0;

    public static ChallengeParticipation createNew(Challenge challenge, User user) {
        return ChallengeParticipation.builder()
                .challenge(challenge)
                .user(user)
                .participationStatus(ParticipationStatus.IN_PROGRESS)
                .challengeStatus(Challenge.ChallengeStatus.IN_PROGRESS)
                .completedCount(0)
                .build();
    }

    public void updateCompletedCount(int totalCount) {
        this.completedCount = totalCount;
        this.setUpdatedAt(LocalDateTime.now());

        if (this.completedCount >= this.challenge.getSuccessCount()) {
            this.challengeStatus = Challenge.ChallengeStatus.COMPLETED;
        } else {
            this.challengeStatus = Challenge.ChallengeStatus.IN_PROGRESS;
        }
    }

    public boolean isInProgress() {
        return this.challengeStatus == Challenge.ChallengeStatus.IN_PROGRESS;
    }
}
