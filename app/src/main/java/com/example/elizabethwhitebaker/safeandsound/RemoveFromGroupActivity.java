package com.example.elizabethwhitebaker.safeandsound;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;

public class RemoveFromGroupActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
//    private static final String TAG = "RemoveFromGroupActivity";

    private Spinner chooseGroup, chooseMember;
    private Button btnDeleteChecked, btnDeleteAll, btnRemoveFromGroup;
    private int initID;
    private ConstraintLayout scrollView;
    private DBHandler handler;
    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<String> groupNames;
    private ArrayList<String> memNames;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_mems_from_group);

        initID = getIntent().getIntExtra("initID", 0);

        checkBoxes = new ArrayList<>();
        groupNames = new ArrayList<>();
        memNames = new ArrayList<>();

        btnDeleteChecked = findViewById(R.id.deleteCheckedButton);
        btnDeleteAll = findViewById(R.id.deleteAllButton);
        btnRemoveFromGroup = findViewById(R.id.addToGroupButton);
        Button btnGoBack = findViewById(R.id.goBackButton);

        scrollView = findViewById(R.id.scrollViewConstraintLayout);

        chooseGroup = findViewById(R.id.chooseGroupSpinner);
        chooseMember = findViewById(R.id.chooseMemberSpinner);
        loadGroupSpinnerData();

        chooseMember.setEnabled(false);
        btnDeleteChecked.setEnabled(false);
        btnDeleteAll.setEnabled(false);
        btnRemoveFromGroup.setEnabled(false);

        btnDeleteChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintSet set = new ConstraintSet();
                for(CheckBox checkBox : checkBoxes) {
                    if(checkBox.isChecked()) {
                        reloadSpinnerData(true, checkBox.getText().toString());
                        scrollView.removeView(checkBox);
                        set.clone(scrollView);
                        if(checkBoxes.size() == 1) {
                            set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                                    R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                            btnDeleteChecked.setEnabled(false);
                            btnDeleteAll.setEnabled(false);
                            btnRemoveFromGroup.setEnabled(false);
                        }
                        else if(checkBoxes.indexOf(checkBox) != checkBoxes.size() - 1 && checkBoxes.indexOf(checkBox) != 0)
                            set.connect(checkBoxes.get(checkBoxes.indexOf(checkBox) + 1).getId(), ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == 0)
                            set.connect(checkBoxes.get(1).getId(), ConstraintSet.TOP,
                                    R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == checkBoxes.size() - 1)
                            set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM, 16);
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
                btnRemoveFromGroup.setEnabled(false);
                for(CheckBox checkBox : checkBoxes)
                    scrollView.removeView(checkBox);
                checkBoxes.clear();
                ConstraintSet set = new ConstraintSet();
                set.clone(scrollView);
                set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                        R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                set.applyTo(scrollView);
                handler = new DBHandler(getApplicationContext());
                Group g = handler.findHandlerGroup(groupName);
                loadMemberSpinnerData(g);
                handler.close();
            }
        });

        btnRemoveFromGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler = new DBHandler(getApplicationContext());
                Group g = handler.findHandlerGroup(groupName);
                ArrayList<GroupMember> gms = handler.findHandlerGroupMembers(g.getGroupID());
                ArrayList<String> mNames = new ArrayList<>();
                for(GroupMember gm : gms) {
                    Member m = handler.findHandlerMember(gm.getMemberID());
                    mNames.add(m.getFirstName() + " " + m.getLastName());
                }
                for(CheckBox checkBox : checkBoxes) {
                    int count = 0;
                    while (count < mNames.size()) {
                        if (checkBox.getText().toString().equals(mNames.get(count))) {
                            handler.deleteHandler(gms.get(count).getGroupMemberID(), "GroupMembers");
                            mNames.remove(checkBox.getText().toString());
                            count = mNames.size();
                        } else
                            count++;
                    }
                }
                handler.close();
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RemoveFromGroupActivity.this, HomeScreenActivity.class);
                i.putExtra("initID", initID);
                startActivity(i);
            }
        });
    }

    private void loadGroupSpinnerData() {
        handler = new DBHandler(this);
        ArrayList<Group> groups = handler.getAllGroups();
        groupNames.clear();
        groupNames.add("Select group");
        for (Group g : groups)
            groupNames.add(g.getGroupName());
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseGroup.setAdapter(groupAdapter);
        handler.close();
    }

    private void loadMemberSpinnerData(Group g) {
        handler = new DBHandler(this);
        ArrayList<GroupMember> gms = handler.findHandlerGroupMembers(g.getGroupID());
        ArrayList<Member> ms = new ArrayList<>();
        memNames.clear();
        memNames.add("Select member");
        for(GroupMember gm : gms)
            ms.add(handler.findHandlerMember(gm.getMemberID()));
        for(Member m : ms)
            memNames.add(m.getFirstName() + " " + m.getLastName());
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, memNames);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseMember.setAdapter(memberAdapter);
        handler.close();
    }

    private void reloadSpinnerData(boolean add, String name) {
        handler = new DBHandler(this);
        if(add)
            memNames.add(name);
        else
            memNames.remove(name);
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, memNames);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseMember.setAdapter(memberAdapter);
        handler.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0) {
            if(view.equals(chooseGroup)) {
                groupName = adapterView.getItemAtPosition(i).toString();
                Group g = handler.findHandlerGroup(groupName);
                loadMemberSpinnerData(g);
                if(!checkBoxes.isEmpty()) {
                    for(CheckBox checkBox : checkBoxes)
                        scrollView.removeView(checkBox);
                    checkBoxes.clear();
                }
                ConstraintSet set = new ConstraintSet();
                set.clone(scrollView);
                set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                        R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                set.applyTo(scrollView);
                chooseMember.setEnabled(true);
                btnDeleteAll.setEnabled(false);
                btnDeleteChecked.setEnabled(false);
                btnRemoveFromGroup.setEnabled(false);
            } else if(view.equals(chooseMember)) {
                btnDeleteAll.setEnabled(true);
                btnDeleteChecked.setEnabled(true);
                btnRemoveFromGroup.setEnabled(true);
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
                constraintSet.clear(R.id.deleteAllButton, ConstraintSet.TOP);
                constraintSet.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                        checkBox.getId(), ConstraintSet.BOTTOM, 16);
                if (checkBoxes.size() == 0) {
                    constraintSet.connect(checkBox.getId(), ConstraintSet.TOP,
                            R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                } else {
                    constraintSet.connect(checkBox.getId(), ConstraintSet.TOP,
                            checkBoxes.get(checkBoxes.size() - 1).getId(), ConstraintSet.BOTTOM, 16);
                }
                constraintSet.applyTo(scrollView);
                checkBoxes.add(checkBox);
            }
        } else {
            if(view.equals(chooseGroup)) {
                chooseMember.setEnabled(false);
                btnDeleteAll.setEnabled(false);
                btnDeleteChecked.setEnabled(false);
                btnRemoveFromGroup.setEnabled(false);
                if(!checkBoxes.isEmpty()) {
                    for(CheckBox checkBox : checkBoxes)
                        scrollView.removeView(checkBox);
                    checkBoxes.clear();
                    ConstraintSet set = new ConstraintSet();
                    set.clone(scrollView);
                    set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                            R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                    set.applyTo(scrollView);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // TODO Auto-generated method stub
    }
}
