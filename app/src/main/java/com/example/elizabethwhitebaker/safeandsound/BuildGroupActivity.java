package com.example.elizabethwhitebaker.safeandsound;

import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.os.Build;
import android.os.Bundle;
//import android.provider.ContactsContract;
//import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

//import static android.Manifest.permission.READ_CONTACTS;

public class BuildGroupActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
//    private static final String TAG = "BuildGroupActivity";

//    private static final int CONTACTS = 1234;

    private DBHandler handler;
    private EditText groupNameET;
    private Button btnDone, btnDeleteChecked, btnDeleteAll;
    private Spinner memberSpinner;
    private ConstraintLayout scrollView;
    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<String> memNames;
//    private ArrayList<Member> contacts;
    private int initID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_group);

//        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            int hasContactPermission = checkSelfPermission(READ_CONTACTS);
//            if(hasContactPermission != PackageManager.PERMISSION_GRANTED)
//                requestPermissions(new String[]{READ_CONTACTS}, CONTACTS);
//        }

        initID = getIntent().getIntExtra("initID", 0);

        checkBoxes = new ArrayList<>();
        memNames = new ArrayList<>();
//        contacts = new ArrayList<>();

        groupNameET = findViewById(R.id.groupNameEditText);

        btnDeleteChecked = findViewById(R.id.deleteCheckedButton);
        btnDeleteAll = findViewById(R.id.deleteAllButton);
        btnDone = findViewById(R.id.doneButton);
        Button btnGoBack = findViewById(R.id.goBackButton);

        memberSpinner = findViewById(R.id.memberSpinner);

        scrollView = findViewById(R.id.scrollViewConstraintLayout);

        memberSpinner.setOnItemSelectedListener(this);
        loadSpinnerData();

        btnDeleteChecked.setEnabled(false);
        btnDeleteAll.setEnabled(false);
        btnDone.setEnabled(false);

//        if(groupNameET.isFocused())
//            groupNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean b) {
//
//                }
//            });

        btnDeleteChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox[] checks = new CheckBox[checkBoxes.size()];
                for(int i = 0; i < checkBoxes.size(); i++)
                    checks[i] = checkBoxes.get(i);
                for(CheckBox checkBox : checks) {
                    if(checkBox.isChecked()) {
                        ConstraintSet set = new ConstraintSet();
                        reloadSpinnerData(true, checkBox.getText().toString());
                        set.clone(scrollView);
                        if(checkBoxes.size() == 1) {
                            set.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
                                    R.id.chosenMembersTextView, ConstraintSet.BOTTOM, 16);
                            btnDeleteChecked.setEnabled(false);
                            btnDeleteAll.setEnabled(false);
                            btnDone.setEnabled(false);
                        }
                        else if(checkBoxes.indexOf(checkBox) != checkBoxes.size() - 1 && checkBoxes.indexOf(checkBox) != 0)
                            set.connect(checkBoxes.get(checkBoxes.indexOf(checkBox) + 1).getId(), ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == 0)
                            set.connect(checkBoxes.get(1).getId(), ConstraintSet.TOP,
                                    R.id.chosenMembersTextView, ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == checkBoxes.size() - 1)
                            set.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM, 16);
                        scrollView.removeView(checkBox);
                        set.applyTo(scrollView);
                        checkBoxes.remove(checkBox);
                    }
                }
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteChecked.setEnabled(false);
                btnDeleteAll.setEnabled(false);
                btnDone.setEnabled(false);
                for(CheckBox checkBox : checkBoxes)
                    scrollView.removeView(checkBox);
                checkBoxes.clear();
                ConstraintSet set = new ConstraintSet();
                set.clone(scrollView);
                set.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
                        R.id.chosenMembersTextView, ConstraintSet.BOTTOM, 16);
                set.applyTo(scrollView);
                loadSpinnerData();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!groupNameET.getText().toString().isEmpty() ) {
                    handler = new DBHandler(getApplicationContext());
                    boolean same = false;
                    ArrayList<Group> gs = handler.getAllGroups();
                    for(Group g : gs)
                        if(groupNameET.getText().toString().equals(g.getGroupName()))
                            same = true;
                    if(!same) {
                        Group g = new Group(groupNameET.getText().toString());
                        handler.addHandler(g);
                        Group group = handler.findHandlerGroup(g.getGroupName());
                        for (CheckBox checkBox : checkBoxes) {
                            String name = checkBox.getText().toString();
                            Member m = handler.findHandlerMember(name.substring(0, name.indexOf(" ")),
                                    name.substring(name.indexOf(" ") + 1));
                            GroupMember gM = new GroupMember(group.getGroupID(), m.getMemberID());
                            handler.addHandler(gM);
                        }
                        GroupLeader gL = new GroupLeader(initID, g.getGroupID());
                        handler.addHandler(gL);
                        handler.close();
                        Intent i = new Intent(BuildGroupActivity.this, HomeScreenActivity.class);
                        i.putExtra("initID", initID);
                        startActivity(i);
                    }
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BuildGroupActivity.this, HomeScreenActivity.class);
                i.putExtra("initID", initID);
                startActivity(i);
            }
        });
    }

    private void loadSpinnerData() {
//        getContactList();
//        populateMembersTable();
        handler = new DBHandler(this);
        ArrayList<Member> ms = handler.getAllMembers();
        memNames.clear();
        memNames.add("Select name");
        for(Member m : ms)
            memNames.add(m.getFirstName() + " " + m.getLastName());
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, memNames);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(memberAdapter);
        handler.close();
    }

    private void reloadSpinnerData(boolean add, String name) {
        if(add)
            memNames.add(name);
        else
            memNames.remove(name);
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, memNames);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(memberAdapter);
    }

//    private void getContactList() {
//        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                null, null, null, null);
//        if (c != null) {
//            while(c.moveToNext()) {
//                String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                contacts.add(name + " : " + phone);
//            }
//            c.close();
//        }
//    }

//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == CONTACTS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show();
//                getContactList();
//            } else {
//                Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and sms", Toast.LENGTH_SHORT).show();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//                        requestPermissions(new String[]{READ_CONTACTS}, CONTACTS);
//                    }
//                }
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0) {
            if(checkBoxes.size() == 0) {
                btnDeleteAll.setEnabled(true);
                btnDeleteChecked.setEnabled(true);
                btnDone.setEnabled(true);
            }
            String memberName = adapterView.getItemAtPosition(i).toString();
            reloadSpinnerData(false, memberName);
            CheckBox checkBox = new CheckBox(this);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                    (ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);
            checkBox.setId(View.generateViewId());
            scrollView.addView(checkBox, params);
            checkBox.setText(memberName);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(scrollView);
            constraintSet.connect(checkBox.getId(), ConstraintSet.LEFT,
                    R.id.scrollViewConstraintLayout, ConstraintSet.LEFT, 32);
            constraintSet.clear(R.id.deleteCheckedButton, ConstraintSet.TOP);
            constraintSet.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
                    checkBox.getId(), ConstraintSet.BOTTOM, 16);
            if (checkBoxes.size() == 0)
                constraintSet.connect(checkBox.getId(), ConstraintSet.TOP,
                        R.id.chosenMembersTextView, ConstraintSet.BOTTOM, 16);
            else
                constraintSet.connect(checkBox.getId(), ConstraintSet.TOP,
                        checkBoxes.get(checkBoxes.size() - 1).getId(), ConstraintSet.BOTTOM, 16);
            constraintSet.applyTo(scrollView);
            checkBoxes.add(checkBox);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // TODO Auto-generated method stub
    }
}