package com.jarena.dao;

import com.jarena.model.Team;
import com.jarena.model.TeamMember;
import com.jarena.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class TeamDaoImpl implements TeamDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Team team) {
        sessionFactory.getCurrentSession().save(team);
    }

    @Override
    public Team findById(long id) {
        return sessionFactory.getCurrentSession().get(Team.class, id);
    }

    @Override
    public List<Team> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Team t ORDER BY t.name ASC", Team.class)
                .getResultList();
    }

    @Override
    public void update(Team team) {
        sessionFactory.getCurrentSession().update(team);
    }

    @Override
    public void delete(long id) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM TeamMember tm WHERE tm.team.id = :id")
                .setParameter("id", id)
                .executeUpdate();
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM TournamentRegistration tr WHERE tr.team.id = :id")
                .setParameter("id", id)
                .executeUpdate();
        Team team = sessionFactory.getCurrentSession().get(Team.class, id);
        if (team != null) {
            sessionFactory.getCurrentSession().delete(team);
        }
    }

    @Override
    public boolean isUserInAnyTeam(long userId) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(tm) FROM TeamMember tm WHERE tm.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public Team findByUser(long userId) {
        List<TeamMember> members = sessionFactory.getCurrentSession()
                .createQuery("FROM TeamMember tm WHERE tm.user.id = :userId", TeamMember.class)
                .setParameter("userId", userId)
                .getResultList();
        return members.isEmpty() ? null : members.get(0).getTeam();
    }

    @Override
    public void addMember(Team team, User user) {
        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setUser(user);
        member.setJoinedAt(LocalDateTime.now());
        sessionFactory.getCurrentSession().save(member);
    }

    @Override
    public void removeMember(long teamId, long userId) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.user.id = :userId")
                .setParameter("teamId", teamId)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public List<TeamMember> findMembersByTeam(long teamId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM TeamMember tm WHERE tm.team.id = :teamId ORDER BY tm.joinedAt ASC", TeamMember.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }

    @Override
    public boolean isMember(long teamId, long userId) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(tm) FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.user.id = :userId", Long.class)
                .setParameter("teamId", teamId)
                .setParameter("userId", userId)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public List<Team> findLeaderboard() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Team t ORDER BY t.points DESC, t.wins DESC, t.draws DESC", Team.class)
                .getResultList();
    }
}
