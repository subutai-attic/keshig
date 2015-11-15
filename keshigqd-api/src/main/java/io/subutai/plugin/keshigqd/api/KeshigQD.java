package io.subutai.plugin.keshigqd.api;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshigqd.api.entity.Build;
import io.subutai.plugin.keshigqd.api.entity.Dependency;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.ServerType;


public interface KeshigQD
{

    /*
    *   Keshig Server handlers
    * */
    void addServer( Server server ) throws Exception;

    void removeServer( String serverId );

    Server getServer( String serverId  );

    List<Server> getServers(ServerType serverType);

    List<Server> getServers();

    /*
    *   Keshig Builds handler
    * */
    void saveOption(Object option,OperationType type );

    void updateOption(Object option, OperationType type);

    Object getOption(String optionName, OperationType type);

    void deleteOption(String optionName, OperationType type);

    List<?> allOptionsByType( OperationType type );

    void setActive(String optionName, OperationType type);

    void deactivate(String optionName, OperationType type);

    List<Build> getBuilds();

    Build getLatestBuild();

    /*
    *   Keshig Operation Handlers
    * */

    UUID deploy( RequestBuilder requestBuilder, String serverId );

    UUID test( RequestBuilder requestBuilder, String serverId );

    UUID build( RequestBuilder requestBuilder, String serverId );

    UUID clone( RequestBuilder requestBuilder, String serverId );

    /*
    *   run defaults will initiate Keshig process that will execute each step
    *   depending on the configurations(clone/build/deploy/test) provided
    * */
    void runDefaults();
    /*
    *   Keshig Dependency Handlers
    * */
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
    List<Dependency> getRequiredPackages( ServerType serverType );

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
    List<Dependency> getMissingPackages( String serverId, ServerType serverType );
}

