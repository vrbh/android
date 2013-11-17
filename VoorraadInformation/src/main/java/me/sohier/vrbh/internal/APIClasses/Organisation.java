package me.sohier.vrbh.internal.APIClasses;

import java.io.Serializable;

public class Organisation  implements Serializable {
    public Org organisation;

    public class Org  implements Serializable{
        public String name;
        public int id;
    }
}
