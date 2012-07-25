/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seb.away.datas;

/**
 *
 * @author spauwels1
 */
public class User
{
    private int id;
    private String name;
    private String pass;
    private Integer remember;

    public User()
    {
    }

    public User(String name, String pass, Integer remember)
    {
        this.name = name;
        this.pass = pass;
        this.remember = remember;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPass()
    {
        return pass;
    }

    public void setPass(String pass)
    {
        this.pass = pass;
    }
    
    
    public Integer getRemember()
    {
        return remember;
    }

    public void setRemember(Integer remember)
    {
        this.remember = remember;
    }
}
