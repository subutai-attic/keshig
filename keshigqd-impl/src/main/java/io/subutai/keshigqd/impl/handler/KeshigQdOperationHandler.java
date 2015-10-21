package io.subutai.keshigqd.impl.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.subutai.common.tracker.TrackerOperation;
import io.subutai.keshigqd.api.KeshigQDConfig;
import io.subutai.keshigqd.impl.KeshigQDImpl;
import io.subutai.plugin.common.api.ClusterOperationType;
import io.subutai.plugin.common.api.NodeType;


public class KeshigQdOperationHandler implements Runnable
{
    private static final Logger Log = LoggerFactory.getLogger( KeshigQdOperationHandler.class.getName() );

    private KeshigQDConfig config;
    private NodeType nodeType;
    protected TrackerOperation trackerOperation;


    public KeshigQdOperationHandler( final KeshigQDImpl manager, final KeshigQDConfig config,
                                     final ClusterOperationType operationType, final NodeType nodeType )
    {
    this.config = config;
        this.nodeType = nodeType;
        trackerOperation = manager.getTracker().createTrackerOperation( KeshigQDConfig.PRODUCT_KEY,
                String.format( "Starting %s operation on %s(%s) env...", operationType, config.getClusterName(),
                        config.getProductKey() ) );
    }


    @Override
    public void run()
    {

    }
}
