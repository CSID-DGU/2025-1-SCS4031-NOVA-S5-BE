package nova.backend.domain.challenge.repository;

import nova.backend.domain.challenge.entity.Challenge;
import nova.backend.domain.cafe.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByCafe(Cafe cafe);

    @Query("""
        SELECT c FROM Challenge c
        WHERE c.cafe = :cafe
          AND c.startDate > :today
    """)
    List<Challenge> findUpcomingChallenges(Cafe cafe, LocalDate today);

    @Query("""
        SELECT c FROM Challenge c
        WHERE c.cafe = :cafe
          AND c.startDate <= :today
          AND c.endDate >= :today
    """)
    List<Challenge> findOngoingChallenges(Cafe cafe, LocalDate today);

    @Query("""
        SELECT c FROM Challenge c
        WHERE c.cafe = :cafe
          AND c.endDate < :today
    """)
    List<Challenge> findCompletedChallenges(Cafe cafe, LocalDate today);


    List<Challenge> findByCafe_CafeIdAndStartDateAfter(Long cafeId, LocalDate today);

    List<Challenge> findByCafe_CafeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long cafeId, LocalDate todayStart, LocalDate todayEnd);

    List<Challenge> findByCafe_CafeIdAndEndDateBefore(Long cafeId, LocalDate today);

    boolean existsByCafe_CafeId(Long cafeId);

    Optional<Challenge> findFirstByCafe_CafeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long cafeId, LocalDate startDate, LocalDate endDate);

}