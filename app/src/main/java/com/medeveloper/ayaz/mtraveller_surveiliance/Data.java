package com.medeveloper.ayaz.mtraveller_surveiliance;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Data extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mRef;
    String STATE;
    String CITY;
    ArrayList<Ticket> Tickets;
    ArrayList<String> RouteList;
    RecyclerView rv;
    ArrayList<TicketsList> list;
    RVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar d = getSupportActionBar();//getActionBar();
        d.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();
        Tickets = new ArrayList<>();
        RouteList = new ArrayList<>();
        list = new ArrayList<>();
        STATE = "Rajasthan";
        CITY = "Jaipur";
        mRef = FirebaseDatabase.getInstance().getReference();
        LoadTickets();

        list = new ArrayList<>();
        rv = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        adapter = new RVAdapter(list);
        rv.setAdapter(adapter);


    }
    void setData()
    {
        TextView s=findViewById(R.id.state);
        TextView c=findViewById(R.id.city);
        TextView t=findViewById(R.id.city_tickets);
        s.setText(STATE);
        c.setText(CITY);
        t.setText(Tickets.size()+"");
    }

    private void LoadTickets() {
        DatabaseReference ticketsRef = mRef.child("States").child(STATE).child(CITY).child("Tickets");//Refer to city

        ticketsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren())
                        Tickets.add(d.getValue(Ticket.class));
                    setData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference routes = mRef.child("States").child(STATE).child(CITY).child("Routes");
        routes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot d : dataSnapshot.getChildren())
                        RouteList.add(d.getKey().toString());

                Ticketing(Tickets);
               // Random();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    HashMap<String, ArrayList<Ticket>> MyList;

    private void Ticketing(ArrayList<Ticket> List) {

        MyList = new HashMap<>();

        for (int i = 0; i < RouteList.size(); i++)
            MyList.put(RouteList.get(i), new ArrayList<Ticket>());

        for (int i = 0; i < List.size(); i++)
            MyList.get(List.get(i).getRoute_No()).add(List.get(i));
        for (int i = 0; i < MyList.size(); i++)
            list.add(new TicketsList(RouteList.get(i), MyList.get(RouteList.get(i)), RandomCapacity()));
        adapter.notifyDataSetChanged();

        TicketSorter c=new TicketSorter(MyList.get(RouteList.get(1)));
        int d=c.TicketBetweenHour(0,24);
        Toast.makeText(this,"Ticket is : "+d,Toast.LENGTH_SHORT).show();


    }

    private int RandomCapacity() {
        // what is our range?
        int max = 1400;
        int min = 800;
// create instance of Random class
        Random randomNum = new Random();
        int showMe = min + randomNum.nextInt(max-min);

        return (showMe-(showMe%10));
    }

    void Random()
    {  DatabaseReference ticketsRef=mRef.child("States").child(STATE).child(CITY).child("Tickets");//Refer to city
        int j=0;
        int hrs=0;
        int date=0;
        Random random =new Random();
        for(int i=0;i<10590;i++)
        {
            j = random.nextInt((RouteList.size() - 1)-0 + 1)+0;
            hrs =random.nextInt(24-5)+00;
            date=random.nextInt(20-1)+1;
            Date d=new Date(2018,3,20,hrs,3,3);
            ticketsRef.push().setValue(new Ticket("XYZ",new Station("Demo Src","demo","demo"),new Station("Demo Dest","demo","demo"),d,RouteList.get(j)));


        }

    }






    class TicketsList {
        String RouteNumber;
        ArrayList<Ticket> tickets;
        int Capacity;

        TicketsList(String name, ArrayList<Ticket> tickets, int Capacity) {
            this.RouteNumber = name;
            this.tickets = tickets;
            this.Capacity = Capacity;
        }

        public String getRouteNumber() {
            return RouteNumber;
        }

        public ArrayList<Ticket> getTickets() {
            return tickets;
        }

        public int getCapacity() {
            return Capacity;
        }
    }


    class RVAdapter extends RecyclerView.Adapter<RVAdapter.TicketViewHolder> {
        List<TicketsList> ticketsLists;

        RVAdapter(List<TicketsList> ti) {
            this.ticketsLists = ti;
        }

        @Override
        public int getItemCount() {
            return ticketsLists.size();
        }


        @Override
        public TicketViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
            TicketViewHolder pvh = new TicketViewHolder(v);
            return pvh;
        }


        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public  class TicketViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView routeName;
            TextView totalTicket;
            TextView capacity;
            TextView T1,T2,T3,T4,T5,T6,Summary;


            TicketViewHolder(View itemView) {
                super(itemView);
                cv = itemView.findViewById(R.id.cv);
                routeName = itemView.findViewById(R.id.route_name);
                totalTicket = itemView.findViewById(R.id.total_tickets);
                capacity = itemView.findViewById(R.id.capacity);
                T1=itemView.findViewById(R.id.t1);
                T2=itemView.findViewById(R.id.t2);
                T3=itemView.findViewById(R.id.t3);
                T4=itemView.findViewById(R.id.t4);
                T5=itemView.findViewById(R.id.t5);
                T6=itemView.findViewById(R.id.t6);
                Summary= itemView.findViewById(R.id.summary);



            }
        }

        @Override
        public void onBindViewHolder(TicketViewHolder personViewHolder, int i) {
            personViewHolder.routeName.setText("" + ticketsLists.get(i).RouteNumber);
            personViewHolder.totalTicket.setText("" + ticketsLists.get(i).getTickets().size());
            personViewHolder.capacity.setText("" + ticketsLists.get(i).getCapacity());
            int x = Integer.parseInt(personViewHolder.capacity.getText().toString());
            int y = Integer.parseInt(personViewHolder.totalTicket.getText().toString());
            if (x > y) {
                float per=((y-x)/x)*100;

                personViewHolder.totalTicket.setTextColor(getResources().getColor(R.color.success_green));
                personViewHolder.Summary.setTextColor(getResources().getColor(R.color.success_green));
                personViewHolder.Summary.setText(String.format("%.2f",per)+"% of buses can be rescheduled to other route");
            }
            else {
                float per=((float)(y-x)/x)*100;
                personViewHolder.totalTicket.setTextColor(getResources().getColor(R.color.fail_red));
                personViewHolder.Summary.setTextColor(getResources().getColor(R.color.fail_red));
                personViewHolder.Summary.setText(String.format("%.2f",per)+"% of buses needs to added in this route");
            }

            TicketSorter ts=new TicketSorter(ticketsLists.get(i).getTickets());

            personViewHolder.T1.setText(""+ts.TicketBetweenHour(5,9));
            personViewHolder.T2.setText(""+ts.TicketBetweenHour(9,13));
            personViewHolder.T3.setText(""+ts.TicketBetweenHour(13,17));
            personViewHolder.T4.setText(""+ts.TicketBetweenHour(17,21));
           // personViewHolder.T5.setText(""+ts.TicketBetweenHour(21,24));
            personViewHolder.T5.setText(""+ts.TicketBetweenHour(00,5));
            int a=ts.TicketBetweenHour(5,9)+ts.TicketBetweenHour(9,13)+ts.TicketBetweenHour(13,17)+ts.TicketBetweenHour(17,21)+ts.TicketBetweenHour(00,05);
            personViewHolder.T6.setText(""+(y-a));







        }

    }

}