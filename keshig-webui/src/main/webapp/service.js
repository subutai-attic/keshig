'use strict';

angular.module('subutai.plugins.keshig.service', [])
	.factory('keshigSrv', keshigSrv);

keshigSrv.$inject = ['$http'];

function keshigSrv($http) {

	var baseURL = SERVER_URL + 'v1/keshig/';
	var serversUrl = baseURL + 'server/';
	var optionUrl = baseURL + 'option/';
	var profilesUrl = baseURL + 'profiles/';


	var keshigSrv = {
		getProfiles : getProfiles,
		addProfile : addProfile,
		updateProfile : updateProfile,
		removeProfile : removeProfile,
		startProfile : startProfile,
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
		deleteOption : deleteOption,
		getBuilds: getBuilds,
		getPlaybooks: getPlaybooks,
		updateOption : updateOption
	};

	return keshigSrv;

	/*
	 *   Keshig Server Services
	 * */

	function getBuilds() {
		return $http.get(baseURL + 'build', {withCredentials: true, headers: {'Content-Type': 'application/json'}});
	}

	function getPlaybooks() {
		return $http.get(baseURL + 'tests', {withCredentials: true, headers: {'Content-Type': 'application/json'}});
	}

	function getProfiles() {
		return $http.get(profilesUrl, {
			withCredentials: true
		});
	}

	function startProfile(profileName) {
		return $http.get(profilesUrl + profileName + '/start', {
			withCredentials: true
		});
	}

	function addProfile( profile ) {
		return $http.post(profilesUrl, profile, {
			withCredentials: true,
			headers: {'Content-Type': 'application/json'}
		});
	}

	function updateProfile( profile ) {
		return $http.put(profilesUrl, profile, {
			withCredentials: true,
			headers: {'Content-Type': 'application/json'}
		});
	}

	function removeProfile( name ) {
		return $http.delete(profilesUrl + name, {
			withCredentials: true
		});
	}

	function getServers() {
		return $http.get(serversUrl, {
			withCredentials: true
		});
	}

	function addServer( server ) {
		var postData = 'serverName=' + server.serverName 
			+ '&serverType=' + server.serverType 
			+ '&serverId=' + server.serverId;
		return $http.post(serversUrl, postData, {
			withCredentials: true,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		});
	}

	function removeServer(hostId) {
		return $http.delete(serversUrl + hostId, {
			withCredentials: true
		});
	}


	function getServerTypes() {
		return $http.get(serversUrl + 'types', {
			withCredentials: true
		});
	}

	function updateServer( server ) {
		var postData = 'serverName=' + server.serverName 
			+ '&serverType=' + server.serverType 
			+ '&serverId=' + server.serverId;		
		return $http.put(serversUrl, postData, {
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
		return $http.get(optionUrl + 'type/' + type.toLowerCase(), {
			withCredentials: true
		});
	}

	function deleteOption(type, optionName) {
		return $http.delete(optionUrl + type.toLowerCase() + '/' + optionName, {
			withCredentials: true
		});
	}

	function startOption(type, optionName) {
		return $http.get(optionUrl + type.toLowerCase() + '/' + optionName + '/' + 'start', {
			withCredentials: true
		});
	}

	function addOption( type, object ) {
		return $http.post(optionUrl + type.toLowerCase(), object, {
			withCredentials: true,
			headers: { 'Content-Type': 'application/json' }
		});
	}

	function updateOption( type, object ) {
		return $http.put(optionUrl + type.toLowerCase(), object, {
			withCredentials: true,
			headers: { 'Content-Type': 'application/json' }
		});
	}
}
