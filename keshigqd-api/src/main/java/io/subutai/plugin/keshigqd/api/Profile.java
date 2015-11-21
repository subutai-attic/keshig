package io.subutai.plugin.keshigqd.api;


import java.util.List;

import io.subutai.plugin.keshigqd.api.entity.Server;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;


public class Profile
{
    private String name;

    private String cloneOption;
    private String buildOption;
    private String deployOption;
    private String testOption;

    private List<String> serverList;

    public Profile(String name, String cloneOption, String buildOption, String deployOption, String testOption, List<String> serverList) {
        this.name = name;
        this.cloneOption = cloneOption;
        this.buildOption = buildOption;
        this.deployOption = deployOption;
        this.testOption = testOption;
        this.serverList = serverList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCloneOption() {
        return cloneOption;
    }

    public void setCloneOption(String cloneOption) {
        this.cloneOption = cloneOption;
    }

    public String getBuildOption() {
        return buildOption;
    }

    public void setBuildOption(String buildOption) {
        this.buildOption = buildOption;
    }

    public String getDeployOption() {
        return deployOption;
    }

    public void setDeployOption(String deployOption) {
        this.deployOption = deployOption;
    }

    public String getTestOption() {
        return testOption;
    }

    public void setTestOption(String testOption) {
        this.testOption = testOption;
    }

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", cloneOption='" + cloneOption + '\'' +
                ", buildOption='" + buildOption + '\'' +
                ", deployOption='" + deployOption + '\'' +
                ", testOption='" + testOption + '\'' +
                ", serverList=" + serverList +
                '}';
    }
}
