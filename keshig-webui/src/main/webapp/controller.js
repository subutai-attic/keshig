'use strict';

angular.module('subutai.plugins.keshig.controller', [])
    .controller('KeshigCtrl', KeshigCtrl);

KeshigCtrl.$inject = ['keshigSrv', 'SweetAlert'];
function KeshigCtrl(keshigSrv, SweetAlert) {
    var vm = this;

	vm.optionType = "clone";

	vm.serverName = "";
	vm.serverType = "";

	vm.updateOption = updateOption;
	vm.updateServer = updateOption;
	vm.deleteServer = deleteServer;
	vm.deleteOption = deleteOption;


	function updateOption( id )
	{
		if( id === undefined || id == null  )
		{
			keshigSrv.addOption( vm.optionType, getObject(vm.optionType) )
		}
		else
		{
			keshigSrv.updateOption()( vm.optionType, getObject(vm.optionType) )
		}
	}

	function deleteOption( id )
	{
		keshigSrv.deleteOption( id );
	}

	function deleteServer( id )
	{
		keshigSrv.deleteServer( id );
	}

	function updateServer( id )
	{
		var server = {};

		server.serverId = vm.serverId;
		server.serverName = vm.serverName;
		server.serverType = vm.serverType;

		if( id === undefined || id == null || !id )
		{
			keshigSrv.addServer( server );
		}
		else
		{
			keshigSrv.updateServer( server );
		}
	}

	function getObject( type )
	{
		switch( type.toLowerCase() )
		{
			case "clone" :
				break;
			case "deploy" :
				break;
			case "build" :
				break;
			case "test" :
				break;
		}
	}
}