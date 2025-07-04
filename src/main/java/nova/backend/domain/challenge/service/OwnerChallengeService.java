package nova.backend.domain.challenge.service;

import lombok.RequiredArgsConstructor;
import nova.backend.domain.cafe.entity.Cafe;
import nova.backend.domain.cafe.repository.CafeRepository;
import nova.backend.domain.challenge.dto.common.ChallengeBaseDTO;
import nova.backend.domain.challenge.dto.request.ChallengeCreateRequestDTO;
import nova.backend.domain.challenge.dto.response.ChallengeSummaryDTO;
import nova.backend.domain.challenge.dto.response.OwnerChallengeDetailResponseDTO;
import nova.backend.domain.challenge.dto.response.OwnerCompletedChallengeListResponseDTO;
import nova.backend.domain.challenge.entity.Challenge;
import nova.backend.domain.challenge.repository.ChallengeRepository;
import nova.backend.global.error.ErrorCode;
import nova.backend.global.error.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerChallengeService {

    private final CafeRepository cafeRepository;
    private final ChallengeRepository challengeRepository;

    @Transactional
    public void createChallenge(Long ownerId, Long cafeId, ChallengeCreateRequestDTO request) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!cafe.getOwner().getUserId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Challenge challenge = request.toEntity(cafe);
        challengeRepository.save(challenge);
    }

    public OwnerChallengeDetailResponseDTO getChallengeDetail(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        return OwnerChallengeDetailResponseDTO.fromEntity(challenge);
    }


    public List<ChallengeSummaryDTO> getUpcomingChallenges(Long cafeId) {
        LocalDate today = LocalDate.now();
        return challengeRepository.findByCafe_CafeIdAndStartDateAfter(cafeId, today).stream()
                .map(challenge -> new ChallengeSummaryDTO(ChallengeBaseDTO.fromEntity(challenge)))
                .toList();
    }

    public List<ChallengeSummaryDTO> getOngoingChallenges(Long cafeId) {
        LocalDate today = LocalDate.now();
        return challengeRepository.findByCafe_CafeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(cafeId, today, today).stream()
                .map(challenge -> new ChallengeSummaryDTO(ChallengeBaseDTO.fromEntity(challenge)))
                .toList();
    }

    public List<OwnerCompletedChallengeListResponseDTO> getCompletedChallenges(Long cafeId) {
        return challengeRepository.findByCafe_CafeIdAndEndDateBefore(cafeId, LocalDate.now()).stream()
                .map(challenge -> new OwnerCompletedChallengeListResponseDTO(
                        ChallengeBaseDTO.fromEntity(challenge),
                        challenge.getParticipantCount()
                ))
                .toList();
    }
}
