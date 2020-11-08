package web.mini.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mini.backend.model.ResetPassword;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    ResetPassword findByToken(String token);
}
