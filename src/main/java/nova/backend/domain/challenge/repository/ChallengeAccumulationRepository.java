package nova.backend.domain.challenge.repository;

import nova.backend.domain.challenge.entity.ChallengeAccumulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeAccumulationRepository extends JpaRepository<ChallengeAccumulation, Long> {

    List<ChallengeAccumulation> findByParticipation_ParticipationIdOrderByCreatedAtDesc(Long participationId);



}
