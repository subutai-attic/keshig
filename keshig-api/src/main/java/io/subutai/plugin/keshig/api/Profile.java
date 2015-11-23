package io.subutai.plugin.keshig.api;


public class Profile {
    private String name;

    private String cloneOption;
    private String buildOption;
    private String deployOption;
    private String testOption;

    private String cbServer;
    private String deployServer;
    private String testServer;


    public Profile( final String name, final String cloneOption, final String buildOption, final String deployOption,
                    final String testOption, final String cbServer, final String deployServer, final String testServer )
    {
        this.name = name;
        this.cloneOption = cloneOption;
        this.buildOption = buildOption;
        this.deployOption = deployOption;
        this.testOption = testOption;
        this.cbServer = cbServer;
        this.deployServer = deployServer;
        this.testServer = testServer;
    }


    public Profile( String name, String cloneOption, String buildOption, String deployOption, String testOption ) {
        this.name = name;
        this.cloneOption = cloneOption;
        this.buildOption = buildOption;
        this.deployOption = deployOption;
        this.testOption = testOption;


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

    public String getCbServer() {
        return cbServer;
    }

    public void setCbServer(String cbServer) {
        this.cbServer = cbServer;
    }

    public String getDeployServer() {
        return deployServer;
    }

    public void setDeployServer(String deployServer) {
        this.deployServer = deployServer;
    }

    public String getTestServer() {
        return testServer;
    }

    public void setTestServer(String testServer) {
        this.testServer = testServer;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", cloneOption='" + cloneOption + '\'' +
                ", buildOption='" + buildOption + '\'' +
                ", deployOption='" + deployOption + '\'' +
                ", testOption='" + testOption + '\'' +
                ", cbServer='" + cbServer + '\'' +
                ", deployServer='" + deployServer + '\'' +
                ", testServer='" + testServer + '\'' +
                '}';
    }
}
