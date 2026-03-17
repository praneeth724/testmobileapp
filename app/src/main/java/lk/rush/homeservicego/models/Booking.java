package lk.rush.homeservicego.models;

public class Booking {
    private String bookingId;
    private String userId;
    private String userName;    // stored for admin display
    private String serviceId;
    private String serviceName; // stored so we don't need extra Firestore lookup
    private String bookingDate;
    private String bookingTime;
    private String status; // pending, confirmed, completed

    // No-arg constructor required for Firestore
    public Booking() {}

    public Booking(String bookingId, String userId, String userName,
                   String serviceId, String serviceName,
                   String bookingDate, String bookingTime, String status) {
        this.bookingId   = bookingId;
        this.userId      = userId;
        this.userName    = userName;
        this.serviceId   = serviceId;
        this.serviceName = serviceName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.status      = status;
    }

    public String getBookingId()             { return bookingId; }
    public void   setBookingId(String v)     { bookingId = v; }

    public String getUserId()                { return userId; }
    public void   setUserId(String v)        { userId = v; }

    public String getUserName()              { return userName; }
    public void   setUserName(String v)      { userName = v; }

    public String getServiceId()             { return serviceId; }
    public void   setServiceId(String v)     { serviceId = v; }

    public String getServiceName()           { return serviceName; }
    public void   setServiceName(String v)   { serviceName = v; }

    public String getBookingDate()           { return bookingDate; }
    public void   setBookingDate(String v)   { bookingDate = v; }

    public String getBookingTime()           { return bookingTime; }
    public void   setBookingTime(String v)   { bookingTime = v; }

    public String getStatus()                { return status; }
    public void   setStatus(String v)        { status = v; }
}
