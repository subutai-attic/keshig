package io.subutai.plugin.keshig.api;


import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshig.api.entity.*;

import java.util.List;
import java.util.UUID;


public interface Keshig
{

    /*
    *   Keshig Server handlers
    * */
    void addServer( Server server ) throws Exception;

    void removeServer( String serverName );

    Server getServer( String serverName );

    List<Server> getServers( ServerType serverType );

    List<Server> getServers();

    void updateServer( Server server ) throws Exception;

    void setServer( String serverId, String serverType, String serverName );

    //Keshig Server -> Hosting VM with Peer details
    void addKeshigServer( KeshigServer keshigServer ) throws Exception;

    void removeKeshigServer( String hostname );

    void updateKeshigServer( KeshigServer keshigServer );

    KeshigServer getKeshigServer( String hostname );

    List<KeshigServer> getAllKeshigServers();

    void dropAllServers();

    void updateKeshigServerStatuses();

    void addKeshigServers( List<KeshigServer> servers );

    void updateReserved( String hostName, String serverIp, String usedBy, String comment );

    /*
    *   Keshig Option handlers
    * */
    void saveOption( Object option, OperationType type );

    void updateOption( Object option, OperationType type );

    Object getOption( String optionName, OperationType type );

    void deleteOption( String optionName, OperationType type );

    List<?> allOptionsByType( OperationType type );

    void setActive( String optionName, OperationType type );

    void deactivate( String optionName, OperationType type );

    List<Build> getBuilds();

    Build getLatestBuild();

    UUID runCloneOption( String serverId, String optionName );

    UUID runBuildOption( String serverId, String optionName );

    UUID runDeployOption( String serverId, String optionName );

    UUID runTestOption( String serverId, String optionName );
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

    void runOption( String optionName, String optionType );

    void runProfile( String profileName );
    /*
    *  Keshig History Handlers
    * */

    List<History> listHistory();

    History getHistory( String historyId );

    List<String> getPlaybooks();

    void saveHistory( History history );
    /*
    *  Keshig Profile Handlers
    * */

    List<Profile> listProfiles();

    Profile getProfile( String profileName );

    void updateProfile( Profile profile );

    void deleteProfile( String profileName );

    void addProfile( Profile profile ) throws Exception;

    /*
    *  Release
    * */

    void export( String buildName, String serverId );

    void publish( String boxName, String serverId );

    void tpr( String serverId );

    void freeReserver( String hostname, String serverIp );
}

