package tv.tirco.parkmanager.connections;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;

public class ViaConnection implements Connection{
	private boolean valid;
    @SuppressWarnings("rawtypes")
    private ViaAPI api;
    
    // Constructor
    
    public ViaConnection()
    {
        valid = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        if (valid)
        {
            api = Via.getAPI();
        }
    }
    
    // isValid
    
    public boolean isValid()
    {
        return valid;
    }
    
    // getProtocol
    
    @SuppressWarnings("unchecked")
    public ProtocolVersion getProtocol(Player player)
    {
        ProtocolVersion protocol = new ProtocolVersion();
        
        if (api != null)
        {
            protocol.id = api.getPlayerVersion(player);
            protocol.name = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getProtocol(protocol.id).getName();
        }
        
        return protocol;
    }
    
    // getServerProtocol
    
    public ProtocolVersion getServerProtocol()
    {
        ProtocolVersion protocol = new ProtocolVersion();
        
        protocol.id = api.getServerVersion().highestSupportedVersion();
        protocol.name = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getProtocol(protocol.id).getName();
        
        return protocol;
    }
    
    // getSupportedProtocols();
    
    public List<ProtocolVersion> getSupportedProtocols()
    {
        List<ProtocolVersion> versions = new ArrayList<ProtocolVersion>();
        
        if (api != null)
        {
            @SuppressWarnings("unchecked")
            SortedSet<Integer> ids = api.getSupportedVersions();
            
            for (Integer id : ids)
            {
                versions.add(new ProtocolVersion(id, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getProtocol(id).getName()));
            }
        }
        
        return versions;
    }
}
