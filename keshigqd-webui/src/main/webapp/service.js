'use strict';

angular.module('subutai.plugins.keshig.service', [])
    .factory('keshigSrv', keshigSrv);

keshigSrv.$inject = ['$http'];

function keshigSrv($http) {

    var baseURL = serverUrl + 'keshig/';

    var optionUrl = baseURL + 'option/';

    var profileUrl = baseURL + 'profile/';
    var cloneOptionUrl = optionUrl + 'clone/';
    var buildOptionUrl = optionUrl + 'build/';
    var deployOptionUrl = optionUrl + 'deploy/';
    var testOptionUrl = optionUrl + 'test/';

    var serverUrl = baseUrl + 'server/';

    var clustersURL = baseURL + 'clusters/';
    var environmentsURL = serverUrl + 'environments_ui/';

    var keshigSrv = {
        getClusters: getClusters,
        createCassandra: createCassandra,
        changeClusterScaling: changeClusterScaling,
        deleteCluster: deleteCluster,
        addNode: addNode,
        deleteNode: deleteNode,
        startNodes: startNodes,
        stopNodes: stopNodes,
        getEnvironments: getEnvironments
    };

    return keshigSrv;

    /*
    *   Keshig Server Services
    * */

    function getServers() {
        return $http.get(serverUrl, {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }

    function getServer(serverId) {
        return $http.get(serverUrl + serverId, {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }

    function getServerTypes() {
        return $http.get(serverUrl + 'types', {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }

    function addServer(serverId, serverName, serverType) {
        var postData = 'serverId=' + serverId + '&serverName=' + serverName + '&serverType=' + serverType;
        return $http.post(serverUrl,
            postData,
            {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
    }

    function updateServer(serverId, serverName, serverType) {
        var postData = 'serverId=' + serverId + '&serverName=' + serverName + '&serverType=' + serverType;
        return $http.put(serverUrl,
            postData,
            {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
    }

    function deleteServer(serverId) {
        return $http.delete(serverUrl + serverId);
    }

    function getAllOptions() {
        return $http.get(optionUrl, {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }

    function getOptionTypes() {
        return $http.get(optionUrl + 'types', {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }

    function getOptionsByType(type) {
        return $http.get(optionUrl + 'type/' + type, {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }

    function getOption(type, optionName) {
        return $http.get(optionUrl + type + '/' + optionName, {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }

    function startOption(type, optionName) {
        return $http.get(optionUrl + type + '/' + optionName + '/' + 'start', {
            withCredentials: true,
            headers: {'Content-Type': 'application/json'}
        });
    }


    function startNodes(clusterName, nodesArray) {
        var postData = 'clusterName=' + clusterName + '&lxcHosts=' + nodesArray;
        return $http.post(
            clustersURL + 'nodes/start',
            postData,
            {withCredentials: true, headers: {'Content-Type': 'application/x-www-form-urlencoded'}}
        );
    }

    function stopNodes(clusterName, nodesArray) {
        var postData = 'clusterName=' + clusterName + '&lxcHosts=' + nodesArray;
        return $http.post(
            clustersURL + 'nodes/stop',
            postData,
            {withCredentials: true, headers: {'Content-Type': 'application/x-www-form-urlencoded'}}
        );
    }

    function changeClusterScaling(clusterName, scale) {
        return $http.post(clustersURL + clusterName + '/auto_scale/' + scale);
    }

    function deleteCluster(clusterName) {
        return $http.delete(clustersURL + clusterName);
    }

    function deleteNode(clusterName, nodeId) {
        return $http.delete(clustersURL + clusterName + '/node/' + nodeId);
    }

    function getEnvironments() {
        return $http.get(environmentsURL, {withCredentials: true, headers: {'Content-Type': 'application/json'}});
    }

    function createCassandra(cassandraJson) {
        var postData = 'clusterConfJson=' + cassandraJson;
        return $http.post(
            clustersURL + 'create',
            postData,
            {withCredentials: true, headers: {'Content-Type': 'application/x-www-form-urlencoded'}}
        );
    }
}
