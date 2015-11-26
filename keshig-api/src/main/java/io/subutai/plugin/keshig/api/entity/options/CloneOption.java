package io.subutai.plugin.keshig.api.entity.options;


import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.Lists;

import io.subutai.plugin.keshig.api.entity.Command;
import io.subutai.plugin.keshig.api.entity.OperationType;


public class CloneOption {
    @JsonIgnore
    private String output;
    @JsonIgnore
    private OperationType type = OperationType.CLONE;

    private String name;
    private String url;
    private String branch;
    private boolean active;

    private int timeOut;

    public CloneOption() {
    }


    public CloneOption(final String name, final int timeOut, final boolean isActive,
                       final String output, final String branch, final String url) {
        this.name = name;
        this.timeOut = timeOut;
        this.active = isActive;
        this.output = output;
        this.branch = branch;
        this.url = url;
    }


    public int getTimeOut() {
        return timeOut;
    }


    public void setTimeOut(final int timeOut) {
        this.timeOut = timeOut;
    }


    public boolean isActive() {
        return active;
    }


    public void setIsActive(final boolean isActive) {
        this.active = isActive;
    }


    public String getName() {
        return name;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(final String url) {
        this.url = url;
    }


    public String getBranch() {
        return branch;
    }


    public void setBranch(final String branch) {
        this.branch = branch;
    }


    public String getOutput() {
        return output;
    }


    public void setOutput(final String output) {
        this.output = output;
    }


    public List<String> getArgs() {

        return Lists
                .newArrayList(Command.branchOpt, this.branch, Command.repoOpt, this.url, Command.ouput, this.output);
    }


    public OperationType getType() {
        return type;
    }


    @Override
    public String toString() {
        return "CloneOption{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", branch='" + branch + '\'' +
                ", output='" + output + '\'' +
                ", isActive=" + active +
                ", type=" + type +
                ", timeOut=" + timeOut +
                '}';
    }
}
