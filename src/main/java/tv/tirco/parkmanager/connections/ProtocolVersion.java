package tv.tirco.parkmanager.connections;

public class ProtocolVersion
{
    int id;
    String name;
    
    // Constructors
    
    ProtocolVersion()
    {
        id = -1;
        name = "UNKNOWN";
    }
    
    ProtocolVersion(int id, String name)
    {
        this.id = id;
        this.name = name;
    }
    
    // Public methods
    
    public int getId()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String toString()
    {
        return name + "(" + id + ")";
    }
    
}