package com.example.elizabethwhitebaker.safeandsound;

class GroupMember {
    private int groupMemberID;
    private int groupID;
    private int memberID;

    GroupMember() {}

    int getGroupMemberID() {
        return groupMemberID;
    }
    void setGroupMemberID(int groupMemberID) { this.groupMemberID = groupMemberID; }
    int getGroupID() {
        return groupID;
    }
    int getMemberID() {
        return memberID;
    }
    void setGroupID(int groupID) { this.groupID = groupID; }
    void setMemberID(int memberID) { this.memberID = memberID; }
}
