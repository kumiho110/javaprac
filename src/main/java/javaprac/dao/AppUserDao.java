package javaprac.dao;

import javaprac.model.AppRole;
import javaprac.model.AppUser;

import java.util.Optional;

public interface AppUserDao extends CommonDao<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    long countByRoleAndEnabledTrue(AppRole role);
}