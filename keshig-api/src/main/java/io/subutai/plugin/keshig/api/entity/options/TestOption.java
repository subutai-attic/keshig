package io.subutai.plugin.keshig.api.entity.options;


import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import io.subutai.plugin.keshig.api.entity.OperationType;


public class TestOption {
    private String name;
    private boolean all;

    private List<String> targetIps;
    private List<String> playbooks;

    private boolean active;
    private int timeOut;

    private OperationType type = OperationType.TEST;


    public TestOption() {

    }


    public TestOption(final String name, final boolean all, final List<String> targetIps, final boolean isActive) {
        this.name = name;
        this.all = all;
        this.targetIps = targetIps;
        this.active = isActive;
    }


    public TestOption(final List<String> targetIps, final List<String> playbooks, final boolean isActive,
                      final String name) {
        this.targetIps = targetIps;
        this.playbooks = playbooks;
        this.active = isActive;
        this.name = name;
    }


    public int getTimeOut() {
        return timeOut;
    }


    public void setTimeOut(final int timeOut) {
        this.timeOut = timeOut;
    }


    public OperationType getType() {
        return type;
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


    public boolean isAll() {
        return all;
    }


    public void setAll(final boolean all) {
        this.all = all;
    }


    public List<String> getTargetIps() {
        return targetIps;
    }


    public void setTargetIps(final List<String> targetIps) {
        this.targetIps = targetIps;
    }


    public List<String> getPlaybooks() {
        return playbooks;
    }


    public void setPlaybooks(final List<String> playbooks) {
        this.playbooks = playbooks;
    }


    public List<String> getArgs() {
//
        List<String> args = Lists.newArrayList();
//
//        args.add("m");
//        if (targetIps.size() > 0) {
//            args.add(targetIps.get(0));
//        }
//        args.add("M");
//        if (targetIps.size() > 1) {
//            args.add(targetIps.get(1));
//        }
//        args.add("-s");
//
//        if (all) {
//            args.add("all");
//        } else {
//            args.add(String.join(" ", "\"" + playbooks + "\""));
//        }
        return args;

    }


    @Override
    public String toString() {
        return "TestOption{" +
                "name='" + name + '\'' +
                ", all=" + all +
                ", targetIps=" + targetIps +
                ", playbooks=" + playbooks +
                ", isActive=" + active +
                ", timeOut=" + timeOut +
                ", type=" + type +
                '}';
    }
}
