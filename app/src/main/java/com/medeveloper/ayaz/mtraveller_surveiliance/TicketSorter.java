package com.medeveloper.ayaz.mtraveller_surveiliance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ayaz on 3/20/2018.
 */

public class TicketSorter {

    ArrayList<Ticket> ticketArrayList;
    TicketSorter(ArrayList<Ticket> arrayList)
    {
        ticketArrayList=arrayList;
    }

    int TicketBetweenHour(int start,int end)
    {
        int numberOfTicket=0;
        Date d;
        Calendar date=Calendar.getInstance();
        int hour;
        for(int i=0;i<ticketArrayList.size();i++)
        {

           d=ticketArrayList.get(i).getTime();
           date.setTime(d);
           hour=date.get(Calendar.HOUR_OF_DAY);
           if(hour>start&&hour<=end)
               numberOfTicket++;

        }
        return numberOfTicket;
    }


}
