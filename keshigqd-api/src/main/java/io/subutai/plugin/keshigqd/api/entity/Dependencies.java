package io.subutai.plugin.keshigqd.api.entity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Dependencies
{

    public static class KeshigCloneServer
    {
        //NOTE: Order of the packages matter!!!
        public static List<Dependency> requiredPackages()
        {
            //@formatter:off
             Dependency[] deps = new Dependency[]{

                     /*
                     *  @dependency openjdk
                     *  @version    >=8
                     *  @arch       amd64
                     * */
                     new Dependency("openjdk-8-jre-headless:amd64","8u*","amd64","OpenJDK Java runtime, using Hotspot "
                             + "JIT (headless)"),
                     /*
                     *  @dependency maven
                     *  @version    3.3.3-3
                     *  @arch       all
                     * */
                    new Dependency("maven","3.3.3-*","all","Java software project management and comprehension tool"),

                     /*
                     *  @dependency snappy-tools
                     *  @version    10
                     *  @arch       amd64
                     * */
                    new Dependency("snappy-tools","10","amd64","Snappy tools metapackage"),

                     /*
                     * @dependency keshig-cli
                     * @version    >=0.0.1-alpha
                     * @arch       all
                     * */
                    new Dependency("keshig-cli","0.0.1-alpha","all","Keshiqd CloneBuild CLI")
             };
            //@formatter:on

            return Arrays.asList( deps );
        }
    }


    public static class KeshigDeployServer
    {
        public static List<Dependency> requiredPackages()
        {
            //@formatter:off
        Dependency[] deps = new Dependency[]{
                /*
                * @dependency virtualbox-4.3
                * @version    >=4.3
                * @arch
                * */
                new Dependency("virtualbox-4.3","4.3.*","amd64","Oracle VM VirtualBox"),
                 /*
                * @dependency keshig-cli
                * @version    >=0.0.1-alpha
                * @arch       all
                * */
                new Dependency("keshig-cli","0.0.1-alpha","all","Keshiqd CloneBuild CLI"),
                 /*
                * @dependency
                * @version
                * @arch
                * */
                new Dependency("","","","")
        };
        //@formatter:on
            return Arrays.asList( deps );
        }
    }


    //NOTE: Order of the packages matter!!!
    public static class KeshigTestServer
    {
        public static List<Dependency> requiredPackages()
        {
            //@formatter:off
             Dependency[] deps = new Dependency[]{

                     /*
                     *  @dependency openjdk
                     *  @version    >=8
                     *  @arch       amd64
                     * */
                     new Dependency("openjdk-8-jre-headless:amd64","8u*","amd64","OpenJDK Java runtime, using Hotspot "
                             + "JIT (headless)"),

                     /*
                     *  @dependency maven
                     *  @version    3.3.3-3
                     *  @arch       all
                     * */

                     new Dependency("maven","3.3.3-*","all","Java software project management and comprehension tool"),

                     /*
                     *  @dependency snappy-tools
                     *  @version    10
                     *  @arch       amd64
                     * */
                    new Dependency("snappy-tools","10","amd64","Snappy tools metapackage"),

                     /*
                     * @dependency keshig-cli
                     * @version    >=0.0.1-alpha
                     * @arch       all
                     * */
                    new Dependency("keshig-cli","0.0.1-alpha","all","Keshiqd CloneBuild CLI")
             };
            //@formatter:on
            return Arrays.asList( deps );
        }
    }


    public static List<Dependency> missingDependencies( List<Dependency> allInstalledDependencies,
                                                     List<Dependency> requiredPakcages )
    {
        List<Dependency> missingPakcages = new ArrayList<>();
        boolean found = false;

        for ( Dependency pkg : allInstalledDependencies )
        {
            for ( Dependency rpkg : requiredPakcages )
            {
                if ( rpkg.equals( pkg ) )
                {
                    found = true;
                    break;
                }
            }
            if ( !found )
            {
                missingPakcages.add( pkg );
            }
        }
        return missingPakcages;
    }
}
