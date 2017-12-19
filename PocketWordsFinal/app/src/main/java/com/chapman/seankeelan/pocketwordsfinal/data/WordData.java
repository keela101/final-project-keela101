package com.chapman.seankeelan.pocketwordsfinal.data;

public class WordData
{
    private String definition;

    public WordData()
    {

        this.definition = "";
    }

    public WordData(String definition)
    {
        this.definition = definition;
    }

    public String getDefinition()
    {
        return this.definition;
    }

    public void setDefinition(String definition)
    {
        this.definition = this.definition + definition;
    }
}
