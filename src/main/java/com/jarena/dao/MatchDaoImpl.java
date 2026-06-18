package com.jarena.dao;

import com.jarena.model.Match;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class MatchDaoImpl implements MatchDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Match match) {
        sessionFactory.getCurrentSession().save(match);
    }

    @Override
    public Match findById(long id) {
        return sessionFactory.getCurrentSession().get(Match.class, id);
    }

    @Override
    public List<Match> findByTournament(long tournamentId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Match m WHERE m.tournament.id = :tid ORDER BY m.matchDate ASC, m.matchTime ASC", Match.class)
                .setParameter("tid", tournamentId)
                .getResultList();
    }

    @Override
    public List<Match> findByTeam(long teamId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Match m WHERE m.homeTeam.id = :tid OR m.awayTeam.id = :tid ORDER BY m.matchDate ASC", Match.class)
                .setParameter("tid", teamId)
                .getResultList();
    }

    @Override
    public void update(Match match) {
        sessionFactory.getCurrentSession().update(match);
    }

    @Override
    public void delete(long id) {
        Match match = sessionFactory.getCurrentSession().get(Match.class, id);
        if (match != null) {
            sessionFactory.getCurrentSession().delete(match);
        }
    }

    @Override
    public List<Match> findUpcoming() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Match m WHERE m.status = 'SCHEDULED' AND m.matchDate >= :today ORDER BY m.matchDate ASC, m.matchTime ASC", Match.class)
                .setParameter("today", LocalDate.now())
                .getResultList();
    }
}
