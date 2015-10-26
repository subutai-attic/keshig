package io.subutai.plugin.keshigqd.api;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.subutai.plugin.keshigqd.api.entity.Server;


public interface KeshigQD
{
    public void addServer( Server server ) throws Exception;

    public void removeServer( Server server );

    public Server getServer( Server server );

    public List<Server> getServers();

    UUID deploy( Map<String, String> opts );

    UUID test( Map<String, String> opts );

    UUID build( Map<String, String> opts );

    UUID clone( Map<String, String> opts );
}

