package nova.backend.domain.challenge.entity.status;

/**
 * 사용자의 의지에 따른 챌린지 상태
 */
public enum ParticipationStatus {
    IN_PROGRESS, // 참여 중 (유저가 중단하지 않은 상태)
    CANCELED     // 유저가 중단한 상태
}

