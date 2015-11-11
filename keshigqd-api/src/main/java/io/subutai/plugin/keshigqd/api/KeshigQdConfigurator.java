package io.subutai.plugin.keshigqd.api;


import java.util.List;
import java.util.Map;

import io.subutai.plugin.keshigqd.api.entity.Metric;
import io.subutai.plugin.keshigqd.api.entity.Dependency;
import io.subutai.plugin.keshigqd.api.entity.Server;


public interface KeshigQdConfigurator
{

    public List<Dependency> getInstalledPackages();

    public List<Dependency> getDependencies( String serverType );

    public boolean installPackages( List<Dependency> dependencies );

    public Metric getMetrics( Server server );

    public Map<Server, Metric> getAllMetrics();

    public void configKeshig( Server server );

}
