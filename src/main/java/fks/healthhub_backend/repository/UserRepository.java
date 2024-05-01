package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("User")
public interface UserRepository extends JpaRepository<User, Long> {

}
