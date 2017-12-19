package com.chapman.seankeelan.pocketwordsfinal.api;

import com.google.gson.annotations.SerializedName;
/**
 * Created by seankeelan on 12/15/17.
 */

public class LookupResult
{
    @SerializedName("Symbol")
    public String symbol;

    @SerializedName("Name")
    public String name;

    @SerializedName("Exchange")
    public String exchange;
}
