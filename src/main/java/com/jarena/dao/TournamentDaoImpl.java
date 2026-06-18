package com.jarena.dao;

import com.jarena.model.Tournament;
import com.jarena.model.TournamentRegistration;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TournamentDaoImpl implements TournamentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Tournament tournament) {
        sessionFactory.getCurrentSession().save(tournament);
    }

    @Override
    public Tournament findById(long id) {
        return sessionFactory.getCurrentSession().get(Tournament.class, id);
    }

    @Override
    public List<Tournament> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Tournament t ORDER BY t.createdAt DESC", Tournament.class)
                .getResultList();
    }

    @Override
    public List<Tournament> findByStatus(String status) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Tournament t WHERE t.status = :status ORDER BY t.startDate ASC", Tournament.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public void update(Tournament tournament) {
        sessionFactory.getCurrentSession().update(tournament);
    }

    @Override
    public void delete(long id) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM Match m WHERE m.tournament.id = :id")
                .setParameter("id", id)
                .executeUpdate();
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM TournamentRegistration tr WHERE tr.tournament.id = :id")
                .setParameter("id", id)
                .executeUpdate();
        Tournament t = sessionFactory.getCurrentSession().get(Tournament.class, id);
        if (t != null) {
            sessionFactory.getCurrentSession().delete(t);
        }
    }

    @Override
    public void saveRegistration(TournamentRegistration registration) {
        sessionFactory.getCurrentSession().save(registration);
    }

    @Override
    public boolean isTeamRegistered(long tournamentId, long teamId) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(tr) FROM TournamentRegistration tr WHERE tr.tournament.id = :tid AND tr.team.id = :teamId", Long.class)
                .setParameter("tid", tournamentId)
                .setParameter("teamId", teamId)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public long countRegistrations(long tournamentId) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(tr) FROM TournamentRegistration tr WHERE tr.tournament.id = :tid", Long.class)
                .setParameter("tid", tournamentId)
                .uniqueResult();
        return count != null ? count : 0L;
    }

    @Override
    public List<TournamentRegistration> findRegistrationsByTournament(long tournamentId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM TournamentRegistration tr WHERE tr.tournament.id = :tid ORDER BY tr.registeredAt ASC", TournamentRegistration.class)
                .setParameter("tid", tournamentId)
                .getResultList();
    }

    @Override
    public void deleteRegistration(long tournamentId, long teamId) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM TournamentRegistration tr WHERE tr.tournament.id = :tid AND tr.team.id = :teamId")
                .setParameter("tid", tournamentId)
                .setParameter("teamId", teamId)
                .executeUpdate();
    }
}
