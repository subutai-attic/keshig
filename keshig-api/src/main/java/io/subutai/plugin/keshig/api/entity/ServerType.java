package io.subutai.plugin.keshig.api.entity;


public enum ServerType
{
    TEST_SERVER
            {
                @Override
                public String toString()
                {
                    return "TEST_SERVER";
                }
            },
    BUILD_SERVER
            {
                @Override
                public String toString()
                {
                    return "BUILD_SERVER";
                }
            },
    DEPLOY_SERVER
            {
                @Override
                public String toString()
                {
                    return "DEPLOY_SERVER";
                }
            },
    PEER_SERVER
            {
                @Override
                public String toString()
                {
                    return "PEER_SERVER";
                }
            },
}
