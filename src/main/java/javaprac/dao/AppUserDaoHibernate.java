package javaprac.dao;

import jakarta.persistence.EntityManager;
import javaprac.model.AppRole;
import javaprac.model.AppUser;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class AppUserDaoHibernate extends CommonDaoHibernate<AppUser, Long> implements AppUserDao {

    public AppUserDaoHibernate(EntityManager entityManager) {
        super(entityManager, AppUser.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findByEmail(String email) {
        return session().createQuery(
                        "select u from AppUser u where lower(u.email) = lower(:email)",
                        AppUser.class
                )
                .setParameter("email", email)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRoleAndEnabledTrue(AppRole role) {
        return session().createQuery(
                        "select count(u) from AppUser u where u.role = :role and u.enabled = true",
                        Long.class
                )
                .setParameter("role", role)
                .getSingleResult();
    }
}