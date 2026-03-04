package javaprac.dao;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class CommonDaoHibernate<T, ID> implements CommonDao<T, ID> {

    private final EntityManager entityManager;
    private final Class<T> entityClass;

    protected CommonDaoHibernate(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    protected Session session() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    @Transactional
    public T save(T entity) {
        session().persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public T update(T entity) {
        return session().merge(entity);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        session().remove(entity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        T entity = session().find(entityClass, id);
        if (entity != null) {
            session().remove(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(session().find(entityClass, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return session().createQuery("from " + entityClass.getName(), entityClass).getResultList();
    }
}
