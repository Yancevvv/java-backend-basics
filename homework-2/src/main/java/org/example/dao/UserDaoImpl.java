package org.example.dao;

import org.example.model.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements IUserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory;
    public UserDaoImpl() {
        this(HibernateUtil.getSessionFactory());
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public User save(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved successfully: {}", user);
            return user;
        } catch (Exception e) {
            rollbackQuietly(transaction);
            logger.error("Error saving user", e);
            throw new RuntimeException("Failed to save user", e);
        } finally {
            closeQuietly(session);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        } catch (Exception e) {
            logger.error("Error finding user by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User", User.class).list();
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            return List.of();
        }
    }

    @Override
    public User update(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            User merged = session.merge(user);
            transaction.commit();
            logger.info("User updated successfully: {}", merged);
            return merged;
        } catch (Exception e) {
            rollbackQuietly(transaction);
            logger.error("Error updating user", e);
            throw new RuntimeException("Failed to update user", e);
        } finally {
            closeQuietly(session);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user == null) {
                transaction.commit();
                logger.warn("User with id {} not found, nothing to delete", id);
                return false;
            }
            session.remove(user);
            transaction.commit();
            logger.info("User deleted successfully with id: {}", id);
            return true;
        } catch (Exception e) {
            rollbackQuietly(transaction);
            logger.error("Error deleting user with id: {}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        } finally {
            closeQuietly(session);
        }
    }

    private void rollbackQuietly(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception rollbackEx) {
                logger.error("Rollback failed", rollbackEx);
            }
        }
    }

    private void closeQuietly(Session session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception closeEx) {
                logger.error("Failed to close session", closeEx);
            }
        }
    }
}