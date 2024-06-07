package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.UserHasWorkouts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
@Qualifier("UserHasWorkouts")
public interface UserHasWorkoutsRepository extends JpaRepository<UserHasWorkouts, Long> {

    List<UserHasWorkouts> findByUserId(Long userId);

    List<UserHasWorkouts> findByUserIdAndScheduledAtAfter(Long user_id, ZonedDateTime scheduledAt);
}