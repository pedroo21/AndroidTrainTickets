package com.example.users;

import com.example.tickets.TicketModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class UserModel {

    private int id;
    private String username;
    private String email;
    private String password;
    private int roleUser;
    private List<TicketModel> tickets;

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public int getRoleUser() {
        return roleUser;
    }
    public String getEmail() {
        return email;
    }
    public List<TicketModel> getTickets() {
        return tickets;
    }

    public UserModel() {

    }

    public UserModel(int id, String username, String email, String password, int role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleUser = role;
        this.tickets = new ArrayList<TicketModel>();
    }

    public UserModel(int id, String username, String email, String password, int roleUser, List<TicketModel> tickets) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleUser = roleUser;
        this.tickets = tickets;
    }
}
