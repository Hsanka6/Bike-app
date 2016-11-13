package com.creation.haasith.bicycleapp;

/**
 * Created by gangasanka on 11/12/16.
 */

public class PostedVehicle
{
    public PostedVehicle(String image, String startDate, String endDate, double price, double lat, double lon, String vehicleType, String extraNotes)
    {
        this.image = image;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.lat = lat;
        this.lon = lon;
        this.vehicleType = vehicleType;
        this.extraNotes = extraNotes;
    }

    String image;
    String startDate;
    String endDate;
    double price;
    double lat;
    double lon;
    String vehicleType;
    String extraNotes;


    public String getExtraNotes()
    {
        return extraNotes;
    }

    public void setExtraNotes(String extraNotes)
    {
        this.extraNotes = extraNotes;
    }

    public String getVehicleType()
    {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType)
    {
        this.vehicleType = vehicleType;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLon()
    {
        return lon;
    }

    public void setLon(double lon)
    {
        this.lon = lon;
    }
}
