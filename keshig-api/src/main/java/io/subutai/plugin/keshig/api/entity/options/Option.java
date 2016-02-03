package io.subutai.plugin.keshig.api.entity.options;


import java.util.List;


public interface Option
{
    String getName();

    String getCommand();

    List<String> getArgs();

    int getTimeOut();

    String getType();

    String getRunAs();

}
