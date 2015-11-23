package io.subutai.plugin.keshigqd.api;


import io.subutai.common.command.RequestBuilder;
import io.subutai.plugin.keshigqd.api.entity.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface Keshig {

    /*
    *   Keshig Server handlers
    * */
    void addServer(Server server) throws Exception;

    void removeServer(String serverId);

    Server getServer(String serverId);

    List<Server> getServers(ServerType serverType);

    List<Server> getServers();

    void updateServer(Server server) throws Exception;

    void setServer(String serverId, String serverType, String serverName);

    /*
    *   Keshig Option handlers
    * */
    void saveOption(Object option, OperationType type);

    void updateOption(Object option, OperationType type);

    Object getOption(String optionName, OperationType type);

    void deleteOption(String optionName, OperationType type);

    List<?> allOptionsByType(OperationType type);

    void setActive(String optionName, OperationType type);

    void deactivate(String optionName, OperationType type);

    List<Build> getBuilds();

    Build getLatestBuild();

    /*
    *   Keshig Operation Handlers
    * */

    UUID deploy(RequestBuilder requestBuilder, String serverId);

    UUID test(RequestBuilder requestBuilder, String serverId);

    UUID build(RequestBuilder requestBuilder, String serverId);

    UUID clone(RequestBuilder requestBuilder, String serverId);

    /*
    *   run defaults will initiate Keshig process that will execute each step
    *   depending on the configurations(clone/build/deploy/test) provided
    * */
    void runDefaults();

    void runOption(String optionName, String optionType);
    /*
    *  Keshig History Handlers
    * */

    List<History> listHistory();

    History getHistory(String historyId);

    /*
    *  Keshig Profile Handlers
    * */

    List<Profile> listProfiles();

    Profile getProfile(String profileName);

    void updateProfile(Profile profile);

    void deleteProfile(String profileName);

    void addProfile(Profile profile) throws Exception;
}

