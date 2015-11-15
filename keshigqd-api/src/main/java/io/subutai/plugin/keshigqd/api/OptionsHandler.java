package io.subutai.plugin.keshigqd.api;


import java.util.List;

import io.subutai.plugin.keshigqd.api.entity.OperationType;
import io.subutai.plugin.keshigqd.api.entity.options.BuildOption;
import io.subutai.plugin.keshigqd.api.entity.options.CloneOption;


public interface OptionsHandler
{
    /*
    *   Keshig Option handlers
    * */

    void saveOption(Object option,OperationType type );

    void updateOption(Object option, OperationType type);

    Object getOption(String optionName, OperationType type);

    void deleteOption(String optionName, OperationType type);

    List<?> allOptionsByType( OperationType type );

    void setActive(String optionName, OperationType type);

    void deactivate(String optionName, OperationType type);
}
