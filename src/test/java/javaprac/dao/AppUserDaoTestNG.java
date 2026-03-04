package javaprac.dao;

import javaprac.model.AppRole;
import javaprac.model.AppUser;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

public class AppUserDaoTestNG extends AbstractEntityManagerTest {

    private AppUserDaoHibernate dao;

    @BeforeMethod
    public void setUp() {
        openEntityManager();
        dao = new AppUserDaoHibernate(em);
    }

    @Test
    public void findByEmailAndCountByRoleAndEnabledTrueMustCoverPositiveAndNegativeCases() {
        AppUser enabledUser = new AppUser();
        enabledUser.setEmail("  USER1@Example.com  ");
        enabledUser.setPasswordHash("hash1");
        enabledUser.setFullName("  User One  ");
        enabledUser.setRole(AppRole.USER);
        enabledUser.setEnabled(true);
        dao.save(enabledUser);

        AppUser disabledUser = new AppUser();
        disabledUser.setEmail("user2@example.com");
        disabledUser.setPasswordHash("hash2");
        disabledUser.setFullName("User Two");
        disabledUser.setRole(AppRole.USER);
        disabledUser.setEnabled(false);
        dao.save(disabledUser);

        AppUser adminUser = new AppUser();
        adminUser.setEmail("admin@example.com");
        adminUser.setPasswordHash("hash3");
        adminUser.setFullName("Admin");
        adminUser.setRole(AppRole.ADMIN);
        adminUser.setEnabled(true);
        dao.save(adminUser);

        flushAndClear();

        Optional<AppUser> found = dao.findByEmail("user1@example.com");
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get().getEmail(), "user1@example.com");
        Assert.assertEquals(found.get().getFullName(), "User One");
        Assert.assertNotNull(found.get().getCreatedAt());
        Assert.assertEquals(found.get().getRole(), AppRole.USER);
        Assert.assertTrue(found.get().isEnabled());

        Assert.assertTrue(dao.findByEmail("missing@example.com").isEmpty());

        Assert.assertEquals(dao.countByRoleAndEnabledTrue(AppRole.USER), 1L);
        Assert.assertEquals(dao.countByRoleAndEnabledTrue(AppRole.ADMIN), 1L);
        Assert.assertEquals(dao.countByRoleAndEnabledTrue(AppRole.MANAGER), 0L);
    }

    @Test
    public void persistMustNotAutofillFullNameFromEmail() {
        AppUser user = new AppUser();
        user.setEmail("  NOFULLNAME@Example.com  ");
        user.setPasswordHash("hash-no-fullname");
        user.setFullName("   ");
        user.setRole(AppRole.USER);
        user.setEnabled(true);
        dao.save(user);

        flushAndClear();

        AppUser persisted = dao.findById(user.getId()).orElseThrow();
        Assert.assertEquals(persisted.getEmail(), "nofullname@example.com");
        Assert.assertEquals(persisted.getFullName(), "");
        Assert.assertNotEquals(persisted.getFullName(), persisted.getEmail());
        Assert.assertNotNull(persisted.getCreatedAt());
    }
}