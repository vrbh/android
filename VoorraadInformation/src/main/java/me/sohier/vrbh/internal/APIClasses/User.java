package me.sohier.vrbh.internal.APIClasses;

import java.io.Serializable;

public class User implements Serializable{

    public Usr user;

    public class Usr implements Serializable {
        public Organisation.Org orgs[];
        public String username;
        public int id;
    }
}
