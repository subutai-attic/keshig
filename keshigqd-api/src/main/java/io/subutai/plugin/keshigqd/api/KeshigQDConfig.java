package io.subutai.plugin.keshigqd.api;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.subutai.common.settings.Common;


public class KeshigQDConfig
{


    public static final String PRODUCT_KEY = "KESHIGQD";
    private String clusterName, domainName;

    private List<UUID> servers;
    private UUID environmnetId;


    public KeshigQDConfig()
    {
        domainName = Common.DEFAULT_DOMAIN_NAME;
        servers = new ArrayList<>();
    }


    public String getProductKey()
    {
        return PRODUCT_KEY;
    }


    public String getClusterName()
    {
        return clusterName;
    }


    public void setClusterName( final String clusterName )
    {
        this.clusterName = clusterName;
    }


    public String getDomainName()
    {
        return domainName;
    }


    public void setDomainName( final String domainName )
    {
        this.domainName = domainName;
    }


    public List<UUID> getServers()
    {
        return servers;
    }


    public void setServers( final List<UUID> servers )
    {
        this.servers = servers;
    }


    public UUID getEnvironmnetId()
    {
        return environmnetId;
    }


    public void setEnvironmnetId( final UUID environmnetId )
    {
        this.environmnetId = environmnetId;
    }
}
