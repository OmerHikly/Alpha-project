package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class ManageAcc extends AppCompatActivity {
    ListView users;//רשימה נגללת שתכיל את כל המשתמשים
    Button Exist_Users, Confirm_Users;// כפתורים שמאפשרים צפייה במתשמשים קיימים או במשתמשים שיש לאשר
    EditText et;//אפשרות לחיפוש התלמידים
    Toolbar toolbar;

    ArrayList<String> MainArrayList = new ArrayList<>();//רשימה שתדע להכיל בהתאם את מי שאושר ואת מי שלא בהתאם לכפתור שיילחץ
    ArrayList<String> Demo = new ArrayList<>();//רשימה מועתקת

    UsersAdapter adapter;//מתאם בין רשימת המשתמשים שאושרו או שלא אל הרשימה הנגללת



    Student student;// עצם מסוג תלמיד
    Guard guard;// עצם מסוג שומר
    Admin admin;// עצם מסוג אדמין
    Teacher teacher;// עצם מסוג מורה


    Boolean f = false;//עצם מסוג Boolean להשוואה לעצם שהועלה ל-firebase מכיוון שboolean הוא משתנה וBoolean הוא עצם אי אפשר להשוות ביניהם
    boolean confirmed = true;//המשתנה שבודק איזה משתי הכפתורים נלחץ


    String school;//string שיכיל את בית הספר
    String phone;//string שיכיל את מספר הטלפון


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_acc);
        Parcelable parcelable=getIntent().getParcelableExtra("adminM");//קבלת עצם האדמין מהאקטיביטים הקודמים
        admin= Parcels.unwrap(parcelable);//קישורו אל העצם מסוג אדמין שהגדרנו עבור המסך הזה

        phone = admin.getPhone();//השמת ערכים בתכונות האדמין
        school = admin.getSchool();

        users = findViewById(R.id.User_list); //רכיבי התצוגה
        Exist_Users = findViewById(R.id.Users);
        Confirm_Users = findViewById(R.id.ConfirmUsers);
        et = findViewById(R.id.SearchText);
        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("ניהול משתמשים");
        setSupportActionBar(toolbar);







        users.setLongClickable(true);
        users.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String str = MainArrayList.get(position);
                String[] Splitted = str.split(" ");
                String sphone = Splitted[4];
                String t = null;
                String g=Splitted[0];
                switch (g){
                    case "תלמיד:":
                        t="Student";
                        break;
                    case "מורה:":
                        t="Teacher";
                        break;
                    case "שומר:":
                        t="Guard";
                        break;

                }




if(t!=null) {
    final String finalT = t;
    refSchool.child(school).child(t).child(sphone).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Intent i = new Intent(ManageAcc.this, profile.class);
            int type=0;

            switch (finalT) {
                case "Student":
                    Student studentP = dataSnapshot.getValue(Student.class);
                    Parcelable parcelable = Parcels.wrap(studentP);
                    i.putExtra("student", parcelable);
                    type=0;
                    break;
                case "Teacher":
                    Teacher teacherP=dataSnapshot.getValue(Teacher.class);
                    Parcelable parcelable2 = Parcels.wrap(teacherP);
                    i.putExtra("teacher", parcelable2);
                    type=1;
                    break;

                case "Guard":
                    Guard guardP=dataSnapshot.getValue(Guard.class);
                    Parcelable parcelable3 = Parcels.wrap(guardP);
                    i.putExtra("guard", parcelable3);
                    type=2;
                    break;
            }


            i.putExtra("tphone", phone);
            i.putExtra("type", 3);
            i.putExtra("WatchedUserType", type);
            i.putExtra("sc", school);

            startActivity(i);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
                return true;
            }
        });



        et.addTextChangedListener(new TextWatcher() {//מאזין לשינוי בהקלדת הטקסט
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.clear();//כשהטקסט משתנה המתאם ישתנה בהתאם לStrings שיכילו את מה שהוקלד
                    adapter.getFilter().filter(s);
                }
            }



            @Override
            public void afterTextChanged(Editable s) {

            }


        });




    }



    public class UsersAdapter extends ArrayAdapter {//המחלקה של המתאם שמיועד למסך הזה
        private int layout;

        public UsersAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, 0, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return arrayfilter;
        }



        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.details = convertView.findViewById(R.id.detail);
                viewHolder.approve = convertView.findViewById(R.id.approve);
                viewHolder.remove = convertView.findViewById(R.id.remove);
                convertView.setTag(viewHolder);


            }

            mainViewholder = (ViewHolder) convertView.getTag();
            String str = MainArrayList.get(position);
            String[] Splitted = str.split(" ");
            String text = Splitted[0] + " " + Splitted[1] + " " + Splitted[2] + Splitted[3];
            mainViewholder.details.setText(text);
            ViewGroup.LayoutParams params= mainViewholder.details.getLayoutParams();
            params.height= ViewGroup.LayoutParams.MATCH_PARENT;

            if (confirmed) {

                mainViewholder.approve.setText("חסום");
                mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = MainArrayList.get(position);
                        String[] Splitted = str.split(" ");
                        String x = String.valueOf(position);


                        if (Splitted[0].equals("שומר:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Guard").child(Splitted[4]).child("activated").setValue(false);
                        } else if (Splitted[0].equals("מורה:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Teacher branch
                            refSchool.child(school).child("Teacher").child(Splitted[4]).child("activated").setValue(false);
                        }
                        if (Splitted[0].equals("תלמיד:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Student").child(Splitted[4]).child("activated").setValue(false);
                        }


                        adapter.remove(adapter.getItem(position));

                        notifyDataSetChanged();

                    }
                });


                mainViewholder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = MainArrayList.get(position);
                        String[] Splitted = str.split(" ");

                        // method that splits the string near the buttons to only words without spaces
                        if (Splitted[0].equals("שומר:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Guard").child(Splitted[4]).removeValue();
                        }
                        if (Splitted[0].equals("מורה:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Teacher branch
                            refSchool.child(school).child("Teacher").child(Splitted[4]).removeValue();
                        }
                        if (Splitted[0].equals("תלמיד:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Student").child(Splitted[4]).removeValue();
                        }

                        adapter.remove(adapter.getItem(position));
                        notifyDataSetChanged();
                    }
                });
            } else {

                mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = MainArrayList.get(position);
                        String[] Splitted = str.split(" ");
                        String x = String.valueOf(position);

                        // method that splits the string near the buttons to only words without spaces
                        if (Splitted[0].equals("שומר:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Guard").child(Splitted[4]).child("activated").setValue(true);
                        }
                        if (Splitted[0].equals("מורה:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Teacher branch

                            refSchool.child(school).child("Teacher").child(Splitted[4]).child("activated").setValue(true);

                        }
                        //


                        adapter.remove(adapter.getItem(position));
                        notifyDataSetChanged();
                    }
                });

                mainViewholder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = MainArrayList.get(position);
                        String[] Splitted = str.split(" ");

                        // method that splits the string near the buttons to only words without spaces
                        if (Splitted[0].equals("שומר:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Guard").child(Splitted[4]).removeValue();
                        }
                        if (Splitted[0].equals("מורה:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Teacher branch
                            refSchool.child(school).child("Teacher").child(Splitted[4]).removeValue();
                        }
                        //
                        adapter.remove(adapter.getItem(position));
                        notifyDataSetChanged();
                    }


                });


            }
            return convertView;
        }
    }

    public class ViewHolder {
        TextView details;
        Button approve, remove;
    }




    private Filter arrayfilter=new Filter() {//פעולה לסינון תוצאות החיפוש
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0||et.getText().toString().isEmpty()) {
                suggestions.addAll(Demo);
            } else {
                String filterpattern = constraint.toString().toLowerCase().trim();
                for (String x : Demo) {
                    if (x.toLowerCase().contains(filterpattern)) {
                        suggestions.add(x);
                    }
                }

            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {//הפעולה שמעדכנת את המתאם
            adapter.clear();
            adapter.addAll((List) results.values);
            adapter.notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {//פעולה שמחזירה את העצם resulstValue בתור String
            return ((String) resultValue);
        }
    };



















        public void Confirm_users(View view) {//פעולה שמעדכנת את המשתנה הבוליאני confirmed ומפעילה את הפעולה שמעדכנת את הרשימה של המשתמשים
            confirmed = false;
            Confirm_Users.setBackgroundColor(Color.parseColor("#14b5d0"));
            Exist_Users.setBackgroundColor(Color.parseColor("#007489"));
            UsersList();

        }

        public void Watch_users(View view) {//פעולה שמעדכנת את המשתנה הבוליאני confirmed ומפעילה את הפעולה שמעדכנת את הרשימה של המשתמשים
            confirmed = true;
            Exist_Users.setBackgroundColor(Color.parseColor("#14b5d0"));
            Confirm_Users.setBackgroundColor(Color.parseColor("#007489"));

            UsersList();

        }


        private void UsersList() {//פעולה שמוסיפה לרשימה אחת משתמשים שאושרו ולרשימה אחרת משתמשים שעוד לא אושרו (המשתמשים יכולים להיות רק שומרים או מורים)
//הפעולה הזאת מוסיפה לרשימה אחת את השומרים שאושרו ולרשימה השנייה את השומרים שלא אושרו
            refSchool.child(school).child("Guard").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> Confirmed = new ArrayList<>();
                    ArrayList<String> Unconfirmed = new ArrayList<>();

                    int index = 0;

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        if (dsp.child("activated").getValue() == f) {
                            guard = new Guard();
                            guard = dsp.getValue(Guard.class);
                            String id = guard.getId();
                            String name = guard.getName();
                            String secondname = guard.getSecondName();
                            String uphone = guard.getPhone();
                            String x = "שומר: " + name + " " + secondname + " " + id + " " + uphone;
                            Unconfirmed.add(x);
                            index++;
                        } else {
                            guard = new Guard();
                            guard = dsp.getValue(Guard.class);
                            String id = guard.getId();
                            String name = guard.getName();
                            String secondname = guard.getSecondName();
                            String uphone = guard.getPhone();
                            String x = "שומר: " + name + " " + secondname + " " + id + " " + uphone;
                            Confirmed.add(x);
                            index++;


                        }


                    }


                    Teacher_List(Unconfirmed, Confirmed);
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void Teacher_List(final ArrayList<String> Unconfirmed, final ArrayList<String> Confirmed) {
            ////הפעולה הזאת מוסיפה לרשימה אחת את המורים שאושרו ולרשימה השנייה את המורים שלא אושרו
            refSchool.child(school).child("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> TConfirmed = new ArrayList<>();
                    ArrayList<String> TUnconfirmed = new ArrayList<>();

                    int index = 0;


                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        if (dsp.child("activated").getValue() == f) {
                            teacher = new Teacher();
                            teacher = dsp.getValue(Teacher.class);
                            String id = teacher.getId();
                            String name = teacher.getName();
                            String secondname = teacher.getSecondName();
                            String uphone = teacher.getPhone();
                            String x = "מורה: " + name + " " + secondname + " " + id + " " + uphone;
                            TUnconfirmed.add(x);
                            index++;
                        } else {
                            teacher = new Teacher();
                            teacher = dsp.getValue(Teacher.class);
                            String id = teacher.getId();
                            String name = teacher.getName();
                            String secondname = teacher.getSecondName();
                            String uphone = teacher.getPhone();
                            String x = "מורה: " + name + " " + secondname + " " + id + " " + uphone;

                            TConfirmed.add(x);
                            index++;

                        }


                    }

                    Unconfirmed.addAll(TUnconfirmed);
                    Confirmed.addAll(TConfirmed);

                    Student_List(Unconfirmed,Confirmed);
                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        };









                private void Student_List(final ArrayList<String> Un, final ArrayList<String> Co) {
                    ////הפעולה הזאת מוסיפה לרשימה אחת את התלמידים שאושרו ולרשימה השנייה את התלמידים שלא אושרו
                    refSchool.child(school).child("Student").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> Sconfirmed = new ArrayList<>();
                            ArrayList<String> SUnconfirmed = new ArrayList<>();

                            int index = 0;


                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                if (dsp.child("activated").getValue() == f) {
                                } else {
                                    student = new Student();
                                    student = dsp.getValue(Student.class);
                                    String id = student.getId();
                                    String name = student.getName();
                                    String secondname = student.getSecondName();
                                    String uphone = student.getPhone();
                                    String x = "תלמיד: " + name + " " + secondname + " " + id + " " + uphone;

                                    Sconfirmed.add(x);
                                    index++;

                                }


                            }
                            Co.addAll(Sconfirmed);
                            Un.addAll(SUnconfirmed);


                            if (confirmed) {
                                MainArrayList = Co;
                            } else {
                                MainArrayList = Un;

                            }
                            Demo.clear();
                            Demo.addAll(MainArrayList);
                            Adapt(MainArrayList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });
                }


        private void Adapt(ArrayList<String> arrayList) {//פעוךה המקשרת בין ה-arraylist לרשימה שמופיעה לאדמין
            adapter = new UsersAdapter(this, R.layout.user_list_unconfirmed, arrayList);
            users.setAdapter(adapter);

        }



}


