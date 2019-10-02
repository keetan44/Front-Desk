package app.nowcare4u.comm;

public class Meeting {

    String meetingId,name,mobile,toMeetPerson;
    byte[] byteArray;

    public Meeting(String meetingId,String name,String mobile,String toMeetPerson)
    {
        this.meetingId = meetingId;
        this.name= name;
        this.mobile = mobile;
        this.toMeetPerson = toMeetPerson;
    }

    public String getMeetingID()
    {
        return this.meetingId;
    }

    public String getname()
    {
        return this.name;
    }

    public String getMobile()
    {
        return this.mobile;
    }

    public String getToMeetPerson()
    {
        return this.toMeetPerson;
    }

    public byte[] getByteArray() {
        return this.byteArray;
    }
}
