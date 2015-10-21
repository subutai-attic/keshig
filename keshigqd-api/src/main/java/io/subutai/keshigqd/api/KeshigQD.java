package io.subutai.keshigqd.api;


import java.util.List;
import java.util.Map;

import io.subutai.keshigqd.api.entity.Server;


public interface KeshigQD
{
    public void addServer( Server server ) throws Exception;

    public void removeServer( Server server );

    public Server getServer( Server server );

    public List<Server> getServers( String product );

    String deploy( Map<String, String> opts );

    String test( Map<String, String> opts );

    String build( Map<String, String> opts );
}

