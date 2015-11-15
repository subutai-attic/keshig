package io.subutai.plugin.keshigqd.impl.handler;


import java.util.List;

import io.subutai.plugin.keshigqd.api.KeshigQD;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;
import io.subutai.plugin.keshigqd.api.entity.options.DeployOption;
import io.subutai.plugin.keshigqd.api.entity.options.TestOption;
import io.subutai.plugin.keshigqd.impl.KeshigQDImpl;


public class OptionsHandlerImpl implements io.subutai.plugin.keshigqd.api.OptionsHandler
{

    private KeshigQDImpl keshigQD;


    public OptionsHandlerImpl( final KeshigQD keshigQD )
    {
        this.keshigQD = ( KeshigQDImpl ) keshigQD;
    }


    @Override
    public void saveOption( final Object option, final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
                CloneOption cloneOption = ( CloneOption ) option;
                keshigQD.getPluginDAO()
                        .saveInfo( cloneOption.getType().toString(), cloneOption.getName(), CloneOption.class );
                break;
            case BUILD:
                BuildOption buildOption = ( BuildOption ) option;
                keshigQD.getPluginDAO()
                        .saveInfo( buildOption.getType().toString(), buildOption.getName(), BuildOption.class );
                break;
            case DEPLOY:
                DeployOption deployOption = ( DeployOption ) option;
                keshigQD.getPluginDAO()
                        .saveInfo( deployOption.getType().toString(), deployOption.getName(), DeployOption.class );
                break;
            case TEST:
                TestOption testOption = ( TestOption ) option;
                keshigQD.getPluginDAO()
                        .saveInfo( testOption.getType().toString(), testOption.getName(), TestOption.class );
                break;
        }
    }


    public Object getActiveOption( final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
                List<CloneOption> cloneOptions = keshigQD.getPluginDAO().getInfo( type.toString(), CloneOption.class );
                for ( CloneOption option : cloneOptions )
                {
                    if ( option.isActive() )
                    {
                        return option;
                    }
                }
                break;
            case BUILD:
                List<BuildOption> buildOptions = keshigQD.getPluginDAO().getInfo( type.toString(), BuildOption.class );
                for ( BuildOption option : buildOptions )
                {
                    if ( option.isActive() )
                    {
                        return option;
                    }
                }
                break;
            case DEPLOY:
                List<DeployOption> deployOptions =
                        keshigQD.getPluginDAO().getInfo( type.toString(), DeployOption.class );
                for ( DeployOption option : deployOptions )
                {
                    if ( option.isActive() )
                    {
                        return option;
                    }
                }
                break;
            case TEST:
                List<TestOption> testOptions = keshigQD.getPluginDAO().getInfo( type.toString(), TestOption.class );
                for ( TestOption option : testOptions )
                {
                    if ( option.isActive() )
                    {
                        return option;
                    }
                }
                break;
        }
        return null;
    }


    @Override
    public void updateOption( final Object option, final OperationType type )
    {
        saveOption( option, type );
    }


    @Override
    public Object getOption( final String optionName, final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
                return keshigQD.getPluginDAO().getInfo( type.toString(), optionName, CloneOption.class );

            case BUILD:
                return keshigQD.getPluginDAO().getInfo( type.toString(), optionName, BuildOption.class );

            case DEPLOY:
                return keshigQD.getPluginDAO().getInfo( type.toString(), optionName, DeployOption.class );

            case TEST:
                return keshigQD.getPluginDAO().getInfo( type.toString(), optionName, TestOption.class );
        }
        return null;
    }


    @Override
    public void deleteOption( final String optionName, final OperationType type )
    {
        keshigQD.getPluginDAO().deleteInfo( type.toString(), optionName );
    }


    @Override
    public List<?> allOptionsByType( final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
                return keshigQD.getPluginDAO().getInfo( type.toString(), CloneOption.class );

            case BUILD:
                return keshigQD.getPluginDAO().getInfo( type.toString(), BuildOption.class );

            case DEPLOY:
                return keshigQD.getPluginDAO().getInfo( type.toString(), DeployOption.class );

            case TEST:
                return keshigQD.getPluginDAO().getInfo( type.toString(), TestOption.class );
        }
        return null;
    }


    @Override
    public void setActive( final String optionName, final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
                CloneOption cloneOption = ( CloneOption ) getOption( optionName, type );
                if ( !cloneOption.isActive() )
                {
                    cloneOption.setIsActive( true );
                    saveOption( cloneOption, cloneOption.getType() );
                }
                break;
            case BUILD:
                BuildOption buildOption = ( BuildOption ) getOption( optionName, type );
                if ( !buildOption.isActive() )
                {
                    buildOption.setIsActive( true );
                    saveOption( buildOption, buildOption.getType() );
                }
                break;
            case DEPLOY:
                DeployOption deployOption = ( DeployOption ) getOption( optionName, type );
                if ( !deployOption.isActive() )
                {
                    deployOption.setIsActive( true );
                    saveOption( deployOption, deployOption.getType() );
                }
                break;
            case TEST:
                TestOption testOption = ( TestOption ) getOption( optionName, type );
                if ( !testOption.isActive() )
                {
                    testOption.setIsActive( true );
                    saveOption( testOption, testOption.getType() );
                }
                break;
        }
    }


    @Override
    public void deactivate( final String optionName, final OperationType type )
    {
        switch ( type )
        {
            case CLONE:
                CloneOption cloneOption = ( CloneOption ) getOption( optionName, type );
                if ( cloneOption.isActive() )
                {
                    cloneOption.setIsActive( false );
                    saveOption( cloneOption, cloneOption.getType() );
                }
                break;
            case BUILD:
                BuildOption buildOption = ( BuildOption ) getOption( optionName, type );
                if ( buildOption.isActive() )
                {
                    buildOption.setIsActive( false );
                    saveOption( buildOption, buildOption.getType() );
                }
                break;
            case DEPLOY:
                DeployOption deployOption = ( DeployOption ) getOption( optionName, type );
                if ( deployOption.isActive() )
                {
                    deployOption.setIsActive( false );
                    saveOption( deployOption, deployOption.getType() );
                }
                break;
            case TEST:
                TestOption testOption = ( TestOption ) getOption( optionName, type );
                if ( testOption.isActive() )
                {
                    testOption.setIsActive( false );
                    saveOption( testOption, testOption.getType() );
                }
                break;
        }
    }
}
