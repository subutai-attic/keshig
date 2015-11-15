package io.subutai.plugin.keshigqd.impl.handler;


import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import io.subutai.plugin.keshigqd.api.KeshigQDConfig;
import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.Option;


public class OptionsHandlerImpl implements io.subutai.plugin.keshigqd.api.OptionsHandler
{


    @Override
    public void addOption( final Option option ) throws Exception
    {
        Preconditions.checkNotNull( option );
        if ( !getPluginDAO().saveInfo( KeshigQDConfig.OPTION, option.getName(), option ) )
        {
            throw new Exception( "Could not save option info" );
        }
    }


    @Override
    public List<Option> getOptions()
    {
        List<Option> opts = getPluginDAO().getInfo( KeshigQDConfig.OPTION, Option.class );
        return opts;
    }


    @Override
    public List<Option> getOptionsByType( final OperationType type )
    {
        switch ( type )
        {

            case CLONE:
                List<Option> opts = getPluginDAO().getInfo( KeshigQDConfig.OPTION, Option.class );

                return opts;
            case BUILD:
                break;
            case DEPLOY:
                break;
            case TEST:
                break;
        }
        return null;
    }


    @Override
    public Map<String, Option> getActiveOptions()
    {
        return null;
    }


    @Override
    public void setActiveOption( final String optionName )
    {

    }


    @Override
    public void setDeactiveOption( final String optionName )
    {

    }


    @Override
    public void removeOption( final String optionName )
    {
        Preconditions.checkNotNull( optionName );
        pluginDAO.deleteInfo( KeshigQDConfig.PRODUCT_KEY, optionName );
    }


    @Override
    public Option getOption( final String optionName )
    {
        return pluginDAO.getInfo( KeshigQDConfig.PRODUCT_KEY, optionName, Option.class );
    }


    @Override
    public Option updateOption( final Option option ) throws Exception
    {
        Preconditions.checkNotNull( option );
        if ( !getPluginDAO().saveInfo( option.getType().toString(), option.getName(), option ) )
        {
            throw new Exception( "Could not update option info" );
        }
        return getOption( option.getName() );
    }

}
