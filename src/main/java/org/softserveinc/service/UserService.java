package org.softserveinc.service;

import org.softserveinc.domain.Team;
import org.softserveinc.domain.User;
import org.softserveinc.domain.UserTeam;
import org.softserveinc.repository.HibernateDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by vv on 01.12.2014.
 */
@Service
public class UserService {

    @Autowired
    private HibernateDAO hibernateDAO;

    public void saveUserIntoDB(User user) {
        hibernateDAO.saveUserIntoDB(user);
    }

    public User findUserByUsername(String username) {
        return hibernateDAO.findUserByUsername(username);
    }

    public void saveTeamIntoDB(Team team) {
        hibernateDAO.saveTeamIntoDB(team);
    }

    public void saveUserTeamIntoDB(UserTeam userTeam) {
        hibernateDAO.saveUserTeamIntoDB(userTeam);
    }

    public Map<String, String> getIdsAndNamesOfNotMembersByTeamId(String teamId) {
        Map<String, String> idsAndNames = new HashMap<String, String>();

        List<User> users = getNotMembersByTeamId(teamId);

        if(!users.isEmpty()) {
            for (User user : users) {
                idsAndNames.put(String.valueOf(user.getUserId()),user.getUsername());
            }
        } else {
            idsAndNames.put("-1", "no new users");
        }

        return idsAndNames;
    }

    public List<User> getNotMembersByTeamId(String teamId) {
        Set<Integer> idsOfMembers = new HashSet<Integer>();

        List<UserTeam> userTeams = hibernateDAO.getUserTeamsByTeamId(Integer.parseInt(teamId));
        for (UserTeam userTeam : userTeams) {
            idsOfMembers.add(userTeam.getUserId());
        }

        List<User> users = hibernateDAO.getUsersExceptGivenUserIds(idsOfMembers);

        return users;
    }

    public Team getTeamById(String teamId) {
        Team team = hibernateDAO.getTeamById(teamId);
        return team;
    }

    public List<Team> findTeamsByUsername(String userName) {
        List<Team> teams = new ArrayList<Team>();
        List<Integer> teamsIds = new ArrayList<Integer>();

        User user = hibernateDAO.findUserByUsername(userName);
        Integer userId = user.getUserId();

        List<UserTeam> userTeams = hibernateDAO.findUserTeamsByUserId(userId);
        for (UserTeam userTeam : userTeams) {
            teamsIds.add(userTeam.getTeamId());
        }

        teams = hibernateDAO.getTeamsByTeamIds(teamsIds);

        return teams;
    }

    public Map <Integer, Team> getInvitationsByUsername(String userName) {
        Map <Integer, Team> invitations = new HashMap<Integer, Team>();

        User user = hibernateDAO.findUserByUsername(userName);
        Integer userId = user.getUserId();

        List<UserTeam> userTeamsWithInvitations = hibernateDAO.getUserTeamsWithInvitationsOnlyByUserId(userId);
        for (UserTeam userTeam : userTeamsWithInvitations) {

            Team team =  hibernateDAO.getTeamById(userTeam.getTeamId().toString());
            invitations.put(userTeam.getUserTeamId(), team);
        }

        return invitations;
    }

    public void inviteUserToTeam(String teamId, String userId) {
        Team team = hibernateDAO.getTeamById(teamId);

        UserTeam userTeam = new UserTeam();
        userTeam.setStatus("invited");
        userTeam.setUserId(Integer.parseInt(userId));
        userTeam.setTeamId(Integer.parseInt(teamId));

        hibernateDAO.saveUserTeamIntoDB(userTeam);
    }

    public Team createNewTeam(String userName, Team newTeam) {
        Team team = new Team();
        team.setTeamName(newTeam.getTeamName());

        hibernateDAO.saveTeamIntoDB(team);

        User user = hibernateDAO.findUserByUsername(userName);

        UserTeam userTeam = new UserTeam();
        userTeam.setStatus("owner");
        userTeam.setUserId(user.getUserId());
        userTeam.setTeamId(team.getTeamId());

        hibernateDAO.saveUserTeamIntoDB(userTeam);

        return team;
    }

    public void acceptInvitation(String userTeamId) {
        UserTeam userTeam = hibernateDAO.getUserTeamById(userTeamId);
        userTeam.setStatus("accepted");
        hibernateDAO.updateUserTeamInDB(userTeam);
    }

    public void rejectedInvitation(String userTeamId) {
        UserTeam userTeam = hibernateDAO.getUserTeamById(userTeamId);
        userTeam.setStatus("rejected");
        hibernateDAO.updateUserTeamInDB(userTeam);
    }

}
