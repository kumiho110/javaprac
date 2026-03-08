package javaprac.service;

import javaprac.dao.OrderDao;
import javaprac.model.AppRole;
import javaprac.model.AppUser;
import javaprac.model.OrderEntity;
import javaprac.dao.AppUserDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserAdminService {

    private final AppUserDao appUserDao;
    private final OrderDao orderDao;
    private final CartService cartService;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    public UserAdminService(AppUserDao appUserDao,
                            OrderDao orderDao,
                            CartService cartService,
                            CurrentUserService currentUserService,
                            PasswordEncoder passwordEncoder) {
        this.appUserDao = appUserDao;
        this.orderDao = orderDao;
        this.cartService = cartService;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUserByAdmin(
            String email,
            String password,
            String password2,
            String fullName,
            String phone,
            String address,
            AppRole role,
            boolean enabled
    ) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email обязателен");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен быть не короче 6 символов");
        }

        if (!password.equals(password2)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        if (role != AppRole.USER && role != AppRole.MANAGER) {
            throw new IllegalArgumentException("Можно создавать только USER или MANAGER");
        }

        String normalizedName = fullName == null ? "" : fullName.trim();
        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("ФИО обязательно");
        }

        if (appUserDao.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Такой email уже занят");
        }

        AppUser u = new AppUser();
        u.setEmail(normalizedEmail);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRole(role);
        u.setEnabled(enabled);
        u.setFullName(normalizedName);
        u.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        u.setAddress(address == null || address.isBlank() ? null : address.trim());

        appUserDao.save(u);
    }

    @Transactional
    public void deleteUser(Long userId) {
        AppUser target = appUserDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));

        AppUser current = currentUserService.currentUser();

        if (current.getId().equals(target.getId())) {
            throw new IllegalArgumentException("Нельзя удалить собственный аккаунт");
        }

        if (target.getRole() == AppRole.ADMIN && target.isEnabled()) {
            long enabledAdmins = appUserDao.countByRoleAndEnabledTrue(AppRole.ADMIN);
            if (enabledAdmins <= 1) {
                throw new IllegalArgumentException("Нельзя удалить последнего включённого администратора");
            }
        }

        var orders = orderDao.findAllByUserIdWithItems(userId);

        for (OrderEntity order : orders) {
            if (order.getStatus() == null) {
                throw new IllegalArgumentException("Обнаружен заказ с некорректным статусом");
            }

            switch (order.getStatus()) {
                case processing, packed, delivered ->
                        throw new IllegalArgumentException(
                                "Нельзя удалить пользователя, у которого есть активные или завершённые заказы"
                        );
                case cart, cancelled -> {
                }
            }
        }

        cartService.deleteCartAndReturnStock(userId);

        for (OrderEntity order : orders) {
            if (order.getStatus() == javaprac.model.OrderStatus.cancelled) {
                orderDao.delete(order);
            }
        }

        appUserDao.delete(target);
    }

    @Transactional(readOnly = true)
    public java.util.List<AppUser> listManageableUsers() {
        Long currentUserId = currentUserService.currentUser().getId();

        return appUserDao.findAll().stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .filter(u -> u.getRole() != AppRole.ADMIN)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppUser getManageableUserForEdit(Long id) {
        Long currentUserId = currentUserService.currentUser().getId();

        if (currentUserId.equals(id)) {
            throw new IllegalArgumentException("Собственный аккаунт редактируется через страницу профиля администратора");
        }

        AppUser user = appUserDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + id));

        if (user.getRole() == AppRole.ADMIN) {
            throw new IllegalArgumentException("Администратор редактируется только через страницу профиля администратора");
        }

        return user;
    }

    @Transactional
    public void updateUserProfileByAdmin(Long id, String fullName, String phone, String address, boolean enabled) {
        AppUser user = getManageableUserForEdit(id);

        String normalizedName = fullName == null ? "" : fullName.trim();
        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("ФИО обязательно");
        }

        user.setFullName(normalizedName);
        user.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        user.setAddress(address == null || address.isBlank() ? null : address.trim());
        user.setEnabled(enabled);
    }

    @Transactional
    public AppUser toggleUserEnabled(Long id) {
        AppUser user = getManageableUserForEdit(id);
        user.setEnabled(!user.isEnabled());
        return user;
    }

    @Transactional
    public void updateUserCredentialsByAdmin(Long id, String email, String resetValue1, String resetValue2) {
        AppUser user = getManageableUserForEdit(id);

        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email обязателен");
        }

        var existing = appUserDao.findByEmail(normalizedEmail);
        if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Такой email уже занят");
        }

        user.setEmail(normalizedEmail);

        String p1 = resetValue1 == null ? "" : resetValue1.trim();
        String p2 = resetValue2 == null ? "" : resetValue2.trim();

        boolean wantsPasswordChange = !p1.isBlank() || !p2.isBlank();

        if (wantsPasswordChange) {
            if (p1.length() < 6) {
                throw new IllegalArgumentException("Новый пароль должен быть не короче 6 символов");
            }
            if (!p1.equals(p2)) {
                throw new IllegalArgumentException("Подтверждение нового пароля не совпадает");
            }

            user.setPasswordHash(passwordEncoder.encode(p1));
        }
    }
}