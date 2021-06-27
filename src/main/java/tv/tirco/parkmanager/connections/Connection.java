package tv.tirco.parkmanager.connections;

import java.util.List;

import org.bukkit.entity.Player;

public interface Connection
{
    boolean isValid();
    
    ProtocolVersion getProtocol(Player player);
    
    ProtocolVersion getServerProtocol();
    
    List<ProtocolVersion> getSupportedProtocols();
}
