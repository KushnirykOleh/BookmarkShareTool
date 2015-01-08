package org.softserveinc.repository;

import org.apache.maven.artifact.versioning.Restriction;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.softserveinc.domain.Team;
import org.softserveinc.domain.User;
import org.softserveinc.domain.UserTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by vv on 01.12.2014.
 */
@Repository
@Transactional
public class HibernateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveUserIntoDB(User user) {
        Session session = getSessionFactory().getCurrentSession();
        session.save(user);
    }

    public void saveTeamIntoDB(Team team) {
        Session session = getSessionFactory().getCurrentSession();
        session.save(team);
    }

    public User findUserByUsername(String username) {
        Session session = getSessionFactory().getCurrentSession();
        Query query = session.createQuery("from User where username=?").setParameter(0, username);
        List<User> usersList = (List<User>) query.list();

        if(usersList.size() > 0) {
            return usersList.get(0);
        }
        return null;
    }

    public void updateUserInDB(User user) {
        Session session = getSessionFactory().getCurrentSession();
        session.update(user);
    }

    public void saveUserTeamIntoDB(UserTeam userTeam) {
        Session session = getSessionFactory().getCurrentSession();
        session.save(userTeam);
    }

//    public List<String> getIdsOfNotBelongingUsersForTeam(String teamId) {
//        Session session = getSessionFactory().getCurrentSession();
//        Query query = session.createSQLQuery("SELECT DISTINCT userId FROM users_teams WHERE teamId != " + teamId);
//        List<String> idsOfNotBelongingUsers = query.list();
//
//        return idsOfNotBelongingUsers;
//    }

//    public List<User> getAllUsers() {
//        Session session = getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("from User");
//        List<User> allUsers = (List<User>) query.list();
//        return allUsers;
//    }

    public List<UserTeam> getAllUserTeamObjectsNotBelongingForTeam(String teamId) {
        Session session = getSessionFactory().getCurrentSession();

        Query query = session.createSQLQuery("SELECT * FROM users_teams WHERE teamId!=:ID").addEntity(UserTeam.class);
        query.setInteger("ID", Integer.parseInt(teamId));

        List<UserTeam> notBelongingUserTeamObjects = (List<UserTeam>) query.list();

        return notBelongingUserTeamObjects;
    }

    public List<User> getUsersByUserIds(Collection<Integer> userIds) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.in("userId", userIds));

        List<User> users = (List<User>) criteria.list();

        return users;
    }

    public List<Team> getTeamsByTeamIds(List<Integer> teamsIds) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Team.class);

        criteria.add(Restrictions.in("teamId", teamsIds));

        List<Team> teams = (List<Team>) criteria.list();

        return teams;
    }

    public Team getTeamById(String teamId) {
        Session session = getSessionFactory().getCurrentSession();
        Team team = (Team) session.get(Team.class, Integer.parseInt(teamId));
        return team;
    }

    public List<User> getUsersExceptGivenUserIds(Set<Integer> userIds) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(User.class);

        criteria.add(Restrictions.not(Restrictions.in("userId", userIds)));

        List<User> users = (List<User>) criteria.list();

        return users;
    }

    public List<UserTeam> findUserTeamsByUserId(Integer userId) {

        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(UserTeam.class);
        criteria.add(Restrictions.eq("userId", userId));

        List<UserTeam> userTeams = (List<UserTeam>) criteria.list();

        return userTeams;
    }

    public List<UserTeam> getUserTeamsByTeamId(Integer teamId) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(UserTeam.class);
        criteria.add(Restrictions.eq("teamId", teamId));

        List<UserTeam> userTeams = (List<UserTeam>) criteria.list();

        return userTeams;
    }
}
