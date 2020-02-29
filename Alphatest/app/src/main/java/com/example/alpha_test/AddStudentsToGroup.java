package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class AddStudentsToGroup extends AppCompatActivity {
    TextView Shown_Students;
    ListView students_options;
    EditText search_students;

    LinearLayout linearLayout;

    ArrayList<String> Students = new ArrayList<>();
    ArrayList<String> Options = new ArrayList<>();
    ArrayList<String> Demo = new ArrayList<>();
    ArrayList<String> Choosen = new ArrayList<>();


    Teacher teacher;
    Student student;

    String school,phone,cls;
    String GroupName;
    String Stu;
    Boolean f=false;

    NotInGroupAdapter adapter;
    DatabaseReference refGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students_to_group);
        students_options= findViewById(R.id.Show_Universal_Students);
        search_students=findViewById(R.id.Search_Universal_Students);
        Shown_Students=findViewById(R.id.ChoosenText);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);

        Intent gi=getIntent();
        GroupName=gi.getStringExtra("name");
        Students=gi.getStringArrayListExtra("Stu");
        Stu=Students.toString();

        school=teacher.getSchool();
        phone=teacher.getPhone();
        cls=teacher.getCls();

        Toast.makeText(getApplicationContext(),Stu,Toast.LENGTH_SHORT).show();
        refGroup=refSchool.child(school).child("Teacher").child(phone).child("zgroups").child(GroupName);
      SetList();

    }

    private void SetList() {
        refSchool.child(school).child("Student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Options.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    if ((dsp.child("activated").getValue() != f)&&(!Stu.contains(dsp.getKey()))) {

                            student = new Student();
                            student = dsp.getValue(Student.class);
                            String id = student.getId();
                            String name = student.getName();
                            String secondname = student.getSecondName();
                            String uphone = student.getPhone();
                            String x = "תלמיד: " + name + " " + secondname + " " + id + " " + uphone;
                            Options.add(x);



                    }
                    Demo.clear();
                    Demo.addAll(Options);
                    Adapt();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }



    public void Add(View view) {
        String ChoosensStudents=Choosen.toString();
        ChoosensStudents=ChoosensStudents.substring(1,ChoosensStudents.length()-1);
        //שורה זו מסירה את ה'[' וה-']' שמופיעם כאשר עושים toString() ל-Arraylist -
        String ExsistStudents=Students.toString();
        ExsistStudents=ExsistStudents.substring(1,ExsistStudents.length()-1);
        //שורה זו מסירה את ה'[' וה-']' שמופיעם כאשר עושים toString() ל-Arraylist -
        linearLayout.removeAllViews();
        Students.addAll(Choosen);
        Choosen.clear();
        String UpdatedGroup=ExsistStudents+" "+ChoosensStudents;
        refGroup.setValue(UpdatedGroup);
        Adapt();

        Intent i=new Intent(this,Team.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        i.putExtra("name",GroupName);
        startActivity(i);    }


    public  class NotInGroupAdapter extends ArrayAdapter {
        private int layout;

        public NotInGroupAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, 0, objects);
            layout = resource;

        }




        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
               ViewHolder viewHolder = new ViewHolder();
                viewHolder.approve = convertView.findViewById(R.id.approve);
                viewHolder.details = convertView.findViewById(R.id.detail);
                viewHolder.remove = convertView.findViewById(R.id.remove);
                convertView.setTag(viewHolder);


            }

            mainViewholder = (ViewHolder) convertView.getTag();
            final String str = Options.get(position);
            final String[] Splitted = str.split(" ");
            String text = Splitted[0] + " " + Splitted[1] + " " + Splitted[2] + Splitted[3];
            mainViewholder.details.setText(text);
            mainViewholder.remove.setVisibility(View.GONE);
            mainViewholder.approve.setText("הוסף לקבוצה");
            mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Options.remove(position);
                    Demo.remove(position);
                    Adapt();
                    if(Shown_Students.getText() == ""){
                        Choosen.add(str);
                        final TextView textView = new TextView(AddStudentsToGroup.this);
                        textView.setText(Splitted[1]+" "+Splitted[2]+", ");
                        linearLayout.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Choosen.remove(position);
                                linearLayout.removeView(textView);
                                Options.add(str);
                                Demo.add(str);
                                Adapt();

                            }
                        });



                    }
                    else{
                        Choosen.add(str);
                        final TextView textView = new TextView(AddStudentsToGroup.this);
                        textView.setText(Splitted[1]+Splitted[2]+" ");
                        linearLayout.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Choosen.remove(position);
                                linearLayout.removeView(textView);
                                Options.add(str);
                                Demo.add(str);
                                Adapt();
                            }
                        });


                    }

                }

                ;






            });
            return convertView;

        }
    }


    public class ViewHolder {
        TextView details;
        Button approve, remove;
    }

    private void Adapt() {
        adapter = new NotInGroupAdapter(this, R.layout.user_list_unconfirmed, Options);
        students_options.setAdapter(adapter);
    }



    public void Accep_pupils(View view) {
        Intent i=new Intent(this,Acept_pupils.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }

    public void Groups(View view) {
        Intent i=new Intent(this,Groups.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }
    public void Open_Group(View view) {
        Intent i=new Intent(this,New_group.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }


}