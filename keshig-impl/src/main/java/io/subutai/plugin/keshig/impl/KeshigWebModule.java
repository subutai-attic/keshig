package io.subutai.plugin.keshig.impl;


import io.subutai.webui.api.WebuiModule;


public class KeshigWebModule implements WebuiModule {
    public static String NAME = "Keshig";
    public static String IMG = "plugins/keshig/keshig.png";

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public String getModuleInfo() {
        return String.format("{\"img\" : \"%s\", \"name\" : \"%s\"}", IMG, NAME);
    }


    @Override
    public String getAngularDependecyList() {
        return String.format(".state('keshig', {\n" + "url: '/plugins/keshig',\n"
                + "templateUrl: 'plugins/keshig/partials/view.html',\n" + "resolve: {\n"
                + "loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {\n"
                + "return $ocLazyLoad.load([\n" + "{\n"
                + "name: 'subutai.plugins.keshig',\n" + "files: [\n"
                + "'plugins/keshig/keshig.js',\n" + "'plugins/keshig/controller.js',\n"
                + "'plugins/keshig/service.js',\n"
                + "'subutai-app/peerRegistration/service.js'\n" + "]\n"
                + "}\n" + "]);\n" + "}]\n" + "}\n" + "})" );
    }
}
