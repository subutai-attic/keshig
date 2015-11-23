package io.subutai.plugin.keshig.impl;


import io.subutai.webui.api.WebuiModule;


public class KeshigWebModule implements WebuiModule
{
    public static String NAME = "Keshig";
    public static String IMG = "plugins/cassandra/cassandra.png";

    @Override
    public String getName()
    {
        return NAME;
    }


    @Override
    public String getModuleInfo()
    {
        return String.format( "{\"img\" : \"%s\", \"name\" : \"%s\"}", IMG, NAME );
    }


    @Override
    public String getAngularDependecyList()
    {
        return String.format( "{" +
                "name: 'subutai.blueprints', files: ["
                + "'subutai-app/blueprints/blueprints.js',"
                + "'subutai-app/blueprints/controller.js',"
                + "'subutai-app/environment/service.js'"
                + "]}" );
    }
}
