package io.subutai.plugin.keshig.impl;


import org.apache.openjpa.lib.rop.AbstractResultList;
import serp.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class PlaybookTest {

    public static void main (String []args){


        List<String> playbooks = new ArrayList<>();
        playbooks.add("GeneralPlaybook.story");
        playbooks.add("LocalPlaybook.story");

        List<String> args1 = new ArrayList<>();
        args1.add(String.join(" ",playbooks));

        System.out.println(args1.get(0));
    }
}

