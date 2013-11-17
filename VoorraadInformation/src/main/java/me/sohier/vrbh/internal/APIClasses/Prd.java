package me.sohier.vrbh.internal.APIClasses;

import java.io.Serializable;
import java.util.Date;

public class Prd implements Serializable
{
    public int id;

    public String name;
    public String description;
    public String orderNumber;
    public int ean;
    public String stockUnit;
    public String orderUnit;
    public int minStock;
    public int maxStock;

    public Date created;
    public Date updated;

    public String toString()
    {
        return name;
    }
}