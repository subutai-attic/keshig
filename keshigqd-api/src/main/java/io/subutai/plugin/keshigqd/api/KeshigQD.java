package io.subutai.plugin.keshigqd.api;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.subutai.plugin.keshigqd.api.entity.Build;
import io.subutai.plugin.keshigqd.api.entity.Dependency;
import io.subutai.plugin.keshigqd.api.entity.Server;


public interface KeshigQD
{
    public void addServer( Server server ) throws Exception;

    public void removeServer( Server server );

    public Server getServer( Server server );

    public List<Server> getServers();

    public List<Build> getBuilds();

    UUID deploy( Map<String, String> opts );

    UUID test( Map<String, String> opts );

    UUID build( Map<String, String> opts );

    UUID clone( Map<String, String> opts );

    Map<String, List<Dependency>> getAllPackages();

    /*
    *  Obtain list of installed packages on target server
    *  @param server id
    *  @return list of installed packages on target server
    * */
    List<Dependency> getPackages( String serverId );

    /*
    *  Obtain list of required packages by server type
    *  @param server type
    *  @return list of required packages
    * */
    List<Dependency> getRequiredPackages( String serverType );

    /*
    * Cross reference installed packages vs required packages
    * @param server
    * @return missing packages
    * */
    List<Dependency> getMissingPackages( Server server );

    /*
    * Cross reference installed packages vs required packages
    * @param server
    * @return missing packages
    * */
    List<Dependency> getMissingPackages( String serverId, String serverType );
}

