'use strict';

angular.module('subutai.plugins.keshig.service', [])
    .factory('keshigSrv', keshigSrv);

keshigSrv.$inject = ['$http'];

function keshigSrv($http) {

    var baseURL = serverUrl + 'keshig/v1';
	var serverUrl = baseUrl + 'server/';
    var optionUrl = baseURL + 'option/';
    var profilesUrl = baseURL + 'profiles/';


    var keshigSrv = {
        getProfiles : getProfiles,
		addProfile : addProfile,
		removeProfile : removeProfile,
		getServers : getServers,
		addServer : addServer,
        removeServer : removeServer,
        updateServer : updateServer,
        getServerTypes : getServerTypes,
        getAllOptions : getAllOptions,
        getOptionTypes : getOptionTypes,
        getOptionsByType : getOptionsByType,
        startOption : startOption,
        addOption : addOption,
        updateOption : updateOption
    };

    return keshigSrv;

    /*
    *   Keshig Server Services
    * */

	function getProfiles()
	{
		return $http.get(profilesUrl, {
			withCredentials: true
		});
	}

	function addProfile( profile )
	{
		return $http.post(profilesUrl, profile, {
			withCredentials: true,
			headers: {'Content-Type': 'application/json'}
		});
	}

	function removeProfile( name )
	{
		return $http.delete(profilesUrl + name, {
			withCredentials: true
		});
	}

	function getServers()
	{
		return $http.get(serverUrl, {
			withCredentials: true
		});
	}

	function addServer( server )
	{
		return $http.post(serverUrl, server, {
			withCredentials: true,
			headers: {'Content-Type': 'application/json'}
		});
	}

	function removeServer(id)
	{
		return $http.remove(serverUrl + id, {
			withCredentials: true
		});
	}


    function getServerTypes() {
        return $http.get(serverUrl + 'types', {
            withCredentials: true
        });
    }


    function updateServer( server ) {
        return $http.put(serverUrl, server, {
                withCredentials: true,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            });
    }

    function getAllOptions() {
        return $http.get(optionUrl, {
            withCredentials: true
        });
    }

    function getOptionTypes() {
        return $http.get(optionUrl + 'types', {
            withCredentials: true
        });
    }

    function getOptionsByType(type) {
        return $http.get(optionUrl + 'type/' + type, {
            withCredentials: true
        });
    }

    function startOption(type, optionName) {
        return $http.get(optionUrl + type + '/' + optionName + '/' + 'start', {
            withCredentials: true
        });
    }

    function addOption( type, object )
    {
        return $http.post(optionUrl + type.toLowerCase(), object, {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }

    function updateOption( type, object )
    {
        return $http.put(optionUrl + type.toLowerCase(), object, {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }
}
