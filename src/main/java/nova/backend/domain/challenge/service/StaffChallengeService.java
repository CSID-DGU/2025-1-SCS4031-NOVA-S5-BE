package nova.backend.domain.challenge.service;

import lombok.RequiredArgsConstructor;
import nova.backend.domain.challenge.dto.request.ChallengeAccumulateRequestDTO;
import nova.backend.domain.challenge.dto.request.ChallengeCancelRequestDTO;
import nova.backend.domain.challenge.entity.*;
import nova.backend.domain.challenge.entity.status.ParticipationStatus;
import nova.backend.domain.challenge.repository.ChallengeAccumulationRepository;
import nova.backend.domain.challenge.repository.ChallengeParticipationRepository;
import nova.backend.domain.challenge.repository.ChallengeRepository;
import nova.backend.domain.user.entity.User;
import nova.backend.domain.user.repository.UserRepository;
import nova.backend.global.error.ErrorCode;
import nova.backend.global.error.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffChallengeService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final ChallengeAccumulationRepository accumulationRepository;

    @Transactional
    public void accumulateOngoingChallengeForCafe(Long cafeId, ChallengeAccumulateRequestDTO request) {
        // QR로 사용자 찾기
        User user = userRepository.findByQrCodeValue(request.qrCodeValue())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 오늘 날짜에 진행 중인 챌린지 찾기
        Challenge challenge = challengeRepository
                .findFirstByCafe_CafeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        cafeId, LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 참여 내역 찾거나 새로 생성
        ChallengeParticipation participation = participationRepository
                .findByChallenge_ChallengeIdAndUser_UserId(challenge.getChallengeId(), user.getUserId())
                .orElseGet(() -> participationRepository.save(ChallengeParticipation.createNew(challenge, user)));

        // 이미 완료 또는 취소된 챌린지이거나, 사용자가 자발적으로 참여 중단한 챌린지면 에러
        if (!participation.isInProgress() || participation.getParticipationStatus() == ParticipationStatus.CANCELED) {
            throw new BusinessException(ErrorCode.CHALLENGE_ALREADY_COMPLETED_OR_CANCELED);
        }

        // 적립 생성 및 저장
        ChallengeAccumulation accumulation = ChallengeAccumulation.create(participation, request.accumulateCount());
        accumulationRepository.save(accumulation);

        // 누적 총합 재계산
        int total = accumulationRepository
                .findByParticipation_ParticipationIdOrderByCreatedAtDesc(participation.getParticipationId())
                .stream()
                .mapToInt(ChallengeAccumulation::getAccumulatedCount)
                .sum();

        participation.updateCompletedCount(total);
        participationRepository.save(participation);
    }


    @Transactional
    public void cancelAccumulationsByQr(Long cafeId, ChallengeCancelRequestDTO request) {
        request.validate();

        User user = userRepository.findByQrCodeValue(request.qrCodeValue())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Challenge challenge = challengeRepository
                .findFirstByCafe_CafeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        cafeId, LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

        ChallengeParticipation participation = participationRepository
                .findByChallenge_ChallengeIdAndUser_UserId(challenge.getChallengeId(), user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_PARTICIPATION_NOT_FOUND));

        List<ChallengeAccumulation> accumulations = accumulationRepository
                .findByParticipation_ParticipationIdOrderByCreatedAtDesc(participation.getParticipationId());

        int remaining = request.count();

        for (ChallengeAccumulation accumulation : accumulations) {
            if (remaining <= 0) break;
            int value = accumulation.getAccumulatedCount();
            if (remaining >= value) {
                remaining -= value;
                accumulationRepository.delete(accumulation);
            } else {
                accumulation.decreaseAccumulatedCount(remaining);
                remaining = 0;
            }
        }

        if (remaining > 0) {
            throw new BusinessException(ErrorCode.CHALLENGE_OVER_REQUEST);
        }

        int total = accumulationRepository
                .findByParticipation_ParticipationIdOrderByCreatedAtDesc(participation.getParticipationId())
                .stream()
                .mapToInt(ChallengeAccumulation::getAccumulatedCount)
                .sum();

        participation.updateCompletedCount(total);
    }
}
