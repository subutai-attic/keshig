'use strict';
var UPDATE_NIGHTLY_BUILD_STATUS;

angular.module('subutai.plugins.keshig.controller', [])
    .controller('KeshigCtrl', KeshigCtrl)
	.directive('checkboxListDropdown', checkboxListDropdown);

KeshigCtrl.$inject = ['$scope', 'keshigSrv', 'DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder', '$resource', '$compile', 'SweetAlert', 'peerRegistrationService', 'ngDialog'];
function KeshigCtrl($scope, keshigSrv, DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $resource, $compile, SweetAlert, peerRegistrationService, ngDialog) {
    var vm = this;

	vm.activeTab = 'servers';
	vm.optionType = "DEPLOY";

	vm.servers = [];
	vm.server2Add = {};
	vm.selectedServers = [];
	vm.serverFormUpdate = false;

	vm.serverTypes = [];
	vm.resourceHosts = [];

	vm.optionsType = [];
	vm.optionsDeployBuilds = [];
	vm.option2Add = {};
	vm.option2Run = '';
	vm.options = {};
	vm.optionFormUpdate = false;

	vm.profiles2Add = {};
	vm.profilesFormUpdate = false;

	vm.serversByType = {};
	vm.optionsByType = [];
	vm.playbooks = [];
	vm.targetIPs = [];
	vm.selectedPlaybooks = [];
	vm.selectedTargetIPs = [];
	vm.additionalIPs = [];
	vm.selectedAllPlaybooks = false;

	vm.resourceHostsStatuses = [];
	vm.resourceHostsKeshig = [];
	vm.resourceHostInfo = [];
	vm.currentResourceHost = {};
	vm.currentOption = {};
	vm.currentProfile = {};
	vm.nightlyBuildStatuses = {};

	//functions
	vm.updateOption = updateOption;
	vm.updateServer = updateServer;
	vm.updateProfile = updateProfile;
	vm.editProfile = editProfile;
	vm.editOption = editOption;

	vm.deleteServer = deleteServer;
	vm.deleteOption = deleteOption;
	vm.deleteProfile = deleteProfile;

	vm.changeTab = changeTab;
	vm.changeOptionsType = changeOptionsType;
	vm.addServer2From = addServer2From;
	vm.addOption2From = addOption2From;
	vm.addProfile = addProfile;
	vm.addOption = addOption;
	vm.proceedOption = proceedOption;
	vm.proceedProfile = proceedProfile;
	vm.addServer = addServer;
	vm.runOption = runOption;
	vm.toggleItem = toggleItem;
	vm.addAllPlaybooks = addAllPlaybooks;
	vm.addCustomTargetIP = addCustomTargetIP;
	vm.runProfile = runProfile;
	vm.runOptionForm = runOptionForm;

	//vm.exportBuild = exportBuild;
	vm.getTPR = getTPR;

	vm.showPeerInfo = showPeerInfo;
	vm.getResourceHostsUpdates = getResourceHostsUpdates;
	vm.updateResourceHost = updateResourceHost;
	vm.editPeerData = editPeerData;
	vm.unapproveResourceHost = unapproveResourceHost;
	vm.updateNightlyBuildStatus = updateNightlyBuildStatus;
	vm.dtInstanceCallback = dtInstanceCallback;
	vm.openOptionForm = openOptionForm;
	vm.openProfileForm = openProfileForm;
	vm.isTestOptionFormValid = isTestOptionFormValid;

	keshigSrv.getServerTypes().success(function (data) {
		vm.serverTypes = data;
	});

	keshigSrv.getOptionTypes().success(function (data) {
		vm.optionsType = data;
	});

	//keshigSrv.getBuilds().success(function (data) {
	//	for(var i = 0; i < data.length; i++) {
	//		data[i].dateFormated = dateToFormat(data[i].date);
	//	}
	//	vm.optionsDeployBuilds = data;
	//});

	keshigSrv.getPlaybooks().success(function (data) {
		vm.playbooks = data;
	});

	keshigSrv.getAllOptions().success(function (data) {
		vm.optionsByType = data;
	});

	//function exportBuild(build) {
	//	keshigSrv.exportBuild(build).success(function (data) {
	//		SweetAlert.swal("Success!", 'Build "' + deploy.buildName + '" start export.', "success");
	//	}).error(function(error){
	//		SweetAlert.swal("ERROR!", 'Error: ' + error.replace(/\\n/g, ' '), 'error');
	//	});
	//}


	function getProfileValues() {
		vm.selectedServers = [];
		keshigSrv.getServers().success(function (data) {
			for(var item in data) {
				if(data[item].added) {
					vm.selectedServers.push(data[item]);
				}
			}
		});

		keshigSrv.getProfiles().success(function (data) {
			vm.profiles = data;
		});
	}

	peerRegistrationService.getResourceHosts().success(function (data) {
		vm.resourceHosts = [];
		for(var i = 0; i < data.length; i++) {
			if(data[i].hostname != 'management') {
				vm.resourceHosts.push(data[i]);
			}
		}
	});

	function toggleItem(item, collection) {
		if(!collection) collection = [];
		if(collection.indexOf(item) >= 0) {
			collection.splice(collection.indexOf(item), 1);
		} else {
			collection.push(item);
		}
	}

	function addCustomTargetIP(targetIP) {
		if(vm.additionalIPs.indexOf(targetIP) > -1 || vm.currentOption.targetIps.indexOf(targetIP) > -1 || vm.targetIPs.indexOf(targetIP) > -1 || !targetIP){
			return;
		} else {
			vm.additionalIPs.push(targetIP);
		}
	}

	function addAllPlaybooks() {
		if(vm.selectedAllPlaybooks) {
			vm.currentOption.playbooks= angular.copy(vm.playbooks);
		} else {
			vm.currentOption.playbooks = [];
		}
	}

	function changeTab(tab) {
		vm.activeTab = tab;
		if(vm.activeTab == 'servers') {
			serversTable();
		} else if(vm.activeTab == 'options') {
			getProfileValues();
			changeOptionsType();
		} else if(vm.activeTab == 'profiles') {
			getProfileValues();
			profilesTable();
		} else if(vm.activeTab == 'history') {
			historyTable();
		} else if(vm.activeTab == 'export') {
			getProfileValues();
		} else if(vm.activeTab == 'rh') {
			resourceHostsTable();
		}
	}

	function getTPR(serverId) {
		keshigSrv.getTPR(serverId).success(function (data) {
			SweetAlert.swal("Success!", 'Generating TPR... Link for the file will appear in History tab.', "success");
		}).error(function(error){
			SweetAlert.swal("ERROR!", 'Error: ' + error.replace(/\\n/g, ' '), 'error');
		});
	}

	function 	changeOptionsType() {
		switch (vm.optionType) {
			case 'DEPLOY':
				vm.dtInstance = {};
				vm.dtOptions = DTOptionsBuilder
						.newOptions()
						.withOption('order', [[ 0, "asc" ]])
						.withOption('stateSave', true)
						.withPaginationType('full_numbers');

				vm.dtColumnDefs = [
					DTColumnDefBuilder.newColumnDef(0),
					DTColumnDefBuilder.newColumnDef(1),
					DTColumnDefBuilder.newColumnDef(2).notSortable(),
					DTColumnDefBuilder.newColumnDef(3).notSortable(),
					DTColumnDefBuilder.newColumnDef(4).notSortable()
				];
				break;
			case 'TEST':
				vm.dtInstance = {};
				vm.dtOptions = DTOptionsBuilder
						.newOptions()
						.withOption('order', [[ 0, "asc" ]])
						.withOption('stateSave', true)
						.withPaginationType('full_numbers');

				vm.dtColumnDefs = [
					DTColumnDefBuilder.newColumnDef(0),
					DTColumnDefBuilder.newColumnDef(1),
					DTColumnDefBuilder.newColumnDef(2),
					DTColumnDefBuilder.newColumnDef(3).notSortable(),
					DTColumnDefBuilder.newColumnDef(4).notSortable(),
					DTColumnDefBuilder.newColumnDef(5).notSortable()
				];
				break;
			default:
				break;
		}
		getResourceHostsStatuses();
	}

	function serversTable() {
		vm.dtInstance = {};
		vm.dtOptions = DTOptionsBuilder
			.newOptions()
			.withOption('processing', true)
			.withOption('order', [[ 0, "asc" ]])
			.withOption('stateSave', true)
			.withPaginationType('full_numbers');

		vm.dtColumnDefs = [
			DTColumnDefBuilder.newColumnDef(0),
			DTColumnDefBuilder.newColumnDef(1),
			DTColumnDefBuilder.newColumnDef(2).notSortable(),
			DTColumnDefBuilder.newColumnDef(3).notSortable()
		];
		keshigSrv.getServers().success(function (data) {
			vm.servers = data;
		});
	}
	serversTable();

	function profilesTable() {
		vm.dtInstance = {};
		vm.dtOptions = DTOptionsBuilder
			.newOptions()
			.withOption('processing', true)
			.withOption('order', [[ 1, "asc" ]])
			.withOption('stateSave', true)
			.withPaginationType('full_numbers');

		vm.dtColumnDefs = [
			DTColumnDefBuilder.newColumnDef(0),
			DTColumnDefBuilder.newColumnDef(1),
			DTColumnDefBuilder.newColumnDef(2),
			DTColumnDefBuilder.newColumnDef(3).notSortable(),
			DTColumnDefBuilder.newColumnDef(4).notSortable(),
			DTColumnDefBuilder.newColumnDef(5).notSortable()
		];
	}

	function historyTable() {
		vm.dtInstance = {};
		vm.dtOptions = DTOptionsBuilder
			.fromFnPromise(function() {
				return $resource(keshigSrv.getHistoryUrl()).query().$promise;
			})
			.withPaginationType('full_numbers')
			.withOption('stateSave', true)
			.withOption('order', [[ 2, "desc" ]])
			.withOption('createdRow', createdRow);

		vm.dtColumns = [
			DTColumnBuilder.newColumn('type').withTitle('Type'),
			DTColumnBuilder.newColumn('server').withTitle('Server'),
			DTColumnBuilder.newColumn('startTime').withTitle('Start time').renderWith(dateToFormat),
			DTColumnBuilder.newColumn('endTime').withTitle('End time').renderWith(dateToFormat),
			DTColumnBuilder.newColumn(null).withTitle('Results').renderWith(renderHistoryOutput)
		];
	}

	function resourceHostsTable() {
		vm.dtOptions = DTOptionsBuilder
			.newOptions()
			.withOption('order', [[ 2, "asc" ]])
			.withOption('stateSave', true)
			.withPaginationType('full_numbers');

		vm.dtColumnDefs = [
			DTColumnDefBuilder.newColumnDef(0),
			DTColumnDefBuilder.newColumnDef(1),
			DTColumnDefBuilder.newColumnDef(2),
			DTColumnDefBuilder.newColumnDef(3).notSortable(),
			DTColumnDefBuilder.newColumnDef(4),
			DTColumnDefBuilder.newColumnDef(5),
			DTColumnDefBuilder.newColumnDef(6).notSortable()
		];
		getResourceHostsStatuses();
	}

    function dtInstanceCallback(instance) {
        instance.DataTable.on('draw.dt', function () {
            var api = instance.dataTable.api();
            var rows = api.rows().nodes();
            var last = null;
            api.column(0)
                .data().each(function (group, i) {
                if (last !== group) {
                    $(rows).eq(i).before(
							'<tr class="group">' +
							'<td colspan="7">' +
							'<b>' + group + '</b>' +
							'<div style="float: right">' +
							'<label for="' + group + '_group" style="padding: 0 5px">NightlyBuild</label>' +
							'<input id="' + group + '_group" type="checkbox" ' + (vm.nightlyBuildStatuses[group] ? 'checked' : '') + ' onchange="UPDATE_NIGHTLY_BUILD_STATUS(\'' +  group + '\', this)">' +
							'</div>' +
							'</td>' +
							'</tr>'
					);
                    last = group;
                }
            });
        });
        instance.dataTable.api().draw();
    };

	function getResourceHostsStatuses() {
		var temp = [];
		keshigSrv.getStatuses().then(function (response) {
			vm.resourceHostsKeshig = response.data;
			for(var resourceHost in vm.resourceHostsKeshig) {
				vm.nightlyBuildStatuses[vm.resourceHostsKeshig[resourceHost].hostname] = vm.resourceHostsKeshig[resourceHost].nightlyBuild;
				if(angular.equals(vm.resourceHostsKeshig[resourceHost].peers, {})) {
					temp.push({
						hostname: vm.resourceHostsKeshig[resourceHost].hostname,
						peer: "NONE",
						lastUpdated: moment(vm.resourceHostsKeshig[resourceHost].lastUpdated).format("YYYY, MMMM DD, HH:m"),
					});
				} else {
					for(var peer in vm.resourceHostsKeshig[resourceHost].peers) {
						temp.push({
							hostname: vm.resourceHostsKeshig[resourceHost].hostname,
							peer: vm.resourceHostsKeshig[resourceHost].peers[peer],
							lastUpdated: moment(vm.resourceHostsKeshig[resourceHost].lastUpdated).format("YYYY, MMMM DD, HH:m")
						});
						if(vm.targetIPs.indexOf(vm.resourceHostsKeshig[resourceHost].peers[peer].ip) === -1) {
							vm.targetIPs.push(vm.resourceHostsKeshig[resourceHost].peers[peer].ip)
						} else {
							continue;
						}
					}
				}
			}
			vm.resourceHostsStatuses = temp;
			LOADING_SCREEN('none');
		});
	}

	function showPeerInfo(resourceHost) {
		vm.resourceHostInfo = [];
		if(resourceHost.peer !== 'NONE') {
			for(var item in resourceHost.peer) {
				if(item !== 'details') {
					vm.resourceHostInfo.push({
						key: item,
						value: resourceHost.peer[item]
					});
				}
			}
			for(var item in resourceHost.peer.details) {
				vm.resourceHostInfo.push({
					key: item,
					value: resourceHost.peer.details[item]
				});
			}
		} else {
			for(var item in resourceHost) {
				vm.resourceHostInfo.push({
					key: item,
					value: resourceHost[item]
				});
			}
		}
		ngDialog.open({
			template: 'plugins/keshig/partials/resource-host-info.html',
			scope: $scope,
			className: 'keshigDialog'
		});
	};

	function editPeerData(resourceHost) {
		vm.currentResourceHost = angular.copy(resourceHost);
		ngDialog.open({
			template: 'plugins/keshig/partials/edit-peer-data.html',
			scope: $scope
		});
	};

	function getResourceHostsUpdates() {
		LOADING_SCREEN();
		keshigSrv.getResourceHostsUpdates().success(function (data) {
			console.log(data);
			getResourceHostsStatuses();
		}).error(function (error) {
			SweetAlert.swal("ERROR!", "Error: " + error.replace(/\\n/g, ' '), 'error');
			LOADING_SCREEN('none');
		});
	};

	function updateResourceHost() {
		LOADING_SCREEN();
		keshigSrv.updateResourceHost({
			hostname: vm.currentResourceHost.hostname,
			serverIp: vm.currentResourceHost.peer.ip,
			usedBy: vm.currentResourceHost.peer.usedBy,
			comment: vm.currentResourceHost.peer.comment
		}).success(function () {
			ngDialog.closeAll();
			getResourceHostsStatuses();
		}).error(function(error){
			SweetAlert.swal("ERROR!", "Error: " + error.replace(/\\n/g, ' '), 'error');
			LOADING_SCREEN('none');
		});
	};

	function unapproveResourceHost(resourceHost) {
		LOADING_SCREEN();
		keshigSrv.unapprovePeer({
			hostname: resourceHost.hostname,
			serverIp: resourceHost.peer.ip
		}).success(function () {
			getResourceHostsStatuses();
			LOADING_SCREEN('none');
		}).error(function (error) {
			SweetAlert.swal("ERROR!", "Error: " + error.replace(/\\n/g, ' '), 'error');
			LOADING_SCREEN('none');
		});
	}

	function updateNightlyBuildStatus(hostname, element) {
		keshigSrv.updateNightlyBuildStatus(hostname, element.checked).success(function () {
			getResourceHostsStatuses();
		}).error(function (error) {
			SweetAlert.swal("ERROR!", "Error: " + error.replace(/\\n/g, ' '), 'error');
		});
	}

	UPDATE_NIGHTLY_BUILD_STATUS = updateNightlyBuildStatus;

	function createdRow(row, data, dataIndex) {
		$compile(angular.element(row).contents())($scope);
	}

	function actionEditOption(data, type, full, meta) {
		vm.options[data.name] = data;
		return '<a href class="b-icon b-icon_edit" ng-click="keshigCtrl.addOption2From(keshigCtrl.options[\'' + data.name + '\'])"></a>';
	}

	function actionEditProfile(data, type, full, meta) {
		vm.profiles[data.name] = data;
		return '<a href class="b-icon b-icon_edit" ng-click="keshigCtrl.addProfile2From(keshigCtrl.options[\'' + data.name + '\'])"></a>';
	}

	function renderHistoryOutput(data, type, full, meta) {
		var contentOutput = '';
		if(data.type == 'TEST') {
			contentOutput = '<a href="' + getBaseUrl() + ':80' + data.id + '/serenity/index.html' + '" target="_blank">Report</a>';
		} else {
			contentOutput = data.id;
		}
		return contentOutput;
	}

	function dateToFormat(data, type, full, meta) {
		return dateToFormat(data);
	}

	function playbooksTags(data, type, full, meta) {
		var playbooksHTML = '';
		if(data !== undefined && data !== null && data.length > 0) {
			for(var i = 0; i < data.length; i++) {
				playbooksHTML += '<span class="b-tags">' + data[i] + '</span>';
			}
		}
		return playbooksHTML;
	}

	function optionStatusIcon(data, type, full, meta) {
		return '<div style="width: 100%; text-align: center;"><div class="b-status-icon b-status-icon_' + data + '" tooltips tooltip-template="' + data + '"></div></div>';
	}

	function runOptionButton(data, type, full, meta) {
		return '<a href class="b-btn b-btn_green" ng-click="keshigCtrl.runOptionForm(\'' + data.name + '\')">Run</a>';
	}

	function profileBuildButton(data, type, full, meta) {
		if(data !== undefined && data !== null) {
			return '<a href class="b-btn b-btn_blue" ng-click="keshigCtrl.runOption(\'' + data + '\', \'build\')">Build</a>';
		} else {
			return 'Empty';
		}
	}

	function profileTestButton(data, type, full, meta) {
		if(data !== undefined && data !== null) {
			return '<a href class="b-btn b-btn_blue" ng-click="keshigCtrl.runOption(\'' + data + '\', \'test\')">Test</a>';
		} else {
			return 'Empty';
		}
	}

	function profileDeployButton(data, type, full, meta) {
		if(data !== undefined && data !== null) {
			return '<a href class="b-btn b-btn_blue" ng-click="keshigCtrl.runOption(\'' + data + '\', \'deploy\')">Deploy</a>';
		} else {
			return 'Empty';
		}
	}

	function profileCloneButton(data, type, full, meta) {
		if(data !== undefined && data !== null) {
			return '<a href class="b-btn b-btn_blue" ng-click="keshigCtrl.runOption(\'' + data + '\', \'clone\')">Clone</a>';
		} else {
			return 'Empty';
		}
	}

	function runProfileButton(data, type, full, meta) {
		return '<a href class="b-btn b-btn_green" ng-click="keshigCtrl.runProfile(\'' + data + '\')">Run</a>';
	}

	function deleteAction(data, type, full, meta) {

		var action = 'deleteServer';
		var deleteId = data.serverName;
		if(vm.activeTab == 'options') {
			action = 'deleteOption';
			deleteId = data.name;
		} else if(vm.activeTab == 'profiles') {
			action = 'deleteProfile';
			deleteId = data.name;
		}

		return '<a href class="b-icon b-icon_remove" ng-click="keshigCtrl.' + action + '(\'' + deleteId + '\')"></a>';
	}

	function runProfile(profileName) {
		keshigSrv.startProfile( profileName ).success(function(data){
			SweetAlert.swal("Success!", '"' + profileName + '" profile start running.', "success");
		}).error(function (error) {
			SweetAlert.swal("ERROR!", '"' + profileName + '" profile run error. Error: ' + error.replace(/\\n/g, ' '), 'error');
		});
	}

	function addServer(serverId) {
		LOADING_SCREEN();
		keshigSrv.addServer(serverId).success(function () {
			keshigSrv.getServers().success(function (data) {
				vm.servers = data;
				LOADING_SCREEN('none');
			});
		});
	}

	function addServer2From(server) {
		vm.server2Add = server;
		vm.server2Add.serverType = server.type;
		vm.serverFormUpdate = true;
	}

	function addOption2From(option) {
		vm.option2Add = option;
		vm.optionFormUpdate = true;
	}

	function proceedOption(option) {
		switch (vm.optionType) {
			case 'DEPLOY' :
				break;
			case 'TEST':
				for(var ip in vm.additionalIPs) {
					if(option.targetIps.indexOf(vm.additionalIPs[ip]) > -1) {
						continue;
					} else {
						option.targetIps.push(vm.additionalIPs[ip]);
					}
				}
				break;
		}
		if(vm.optionFormUpdate) {
			console.log(option);
			keshigSrv.updateOption( vm.optionType, JSON.stringify(option) ).success(function(data){
				SweetAlert.swal("Success!", vm.optionType + " option successfully updated.", "success");
				keshigSrv.getAllOptions().success(function (data) {
					vm.optionsByType = data;
					vm.additionalIPs = [];
					ngDialog.closeAll();
					vm.optionFormUpdate = false;
				});
			}).error(function (error) {
				SweetAlert.swal("ERROR!", vm.optionType + " option update error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		} else {
			keshigSrv.addOption(vm.optionType, option).success(function () {
				SweetAlert.swal("Success!", vm.optionType + " option successfully created.", "success");
				keshigSrv.getAllOptions().success(function (data) {
					vm.optionsByType = data;
					vm.additionalIPs = [];
					ngDialog.closeAll();
				});
			});
		}
	}

	function openOptionForm() {
		var template;
		switch (vm.optionType) {
			case 'DEPLOY':
				template = 'plugins/keshig/partials/options/deploy-option-form.html';
				break;
			case 'TEST':
				template = 'plugins/keshig/partials/options/test-option-form.html';
				break;
			default:
				break;
		}
		ngDialog.open({
			template: template,
			scope: $scope
		});

	}

	function addProfile2From(profile) {
		vm.profiles2Add = profile;
		vm.profilesFormUpdate = true;
	}

	function proceedProfile(profile) {
		if(vm.profilesFormUpdate) {
			keshigSrv.updateProfile( JSON.stringify(vm.currentProfile) ).success(function(data){
				SweetAlert.swal("Success!", vm.currentProfile.name + " profile successfully updated.", "success");
				vm.profilesFormUpdate = false;
				ngDialog.closeAll();
			}).error(function (error) {
				SweetAlert.swal("ERROR!", vm.currentProfile.name + " profile update error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		} else {
			keshigSrv.addProfile( JSON.stringify(vm.currentProfile) ).success(function(data){
				SweetAlert.swal("Success!", vm.currentProfile.name + " profile successfully added.", "success");
				keshigSrv.getProfiles().success(function (data) {
					vm.profiles = data;
					ngDialog.closeAll();
				});
			}).error(function (error) {
				SweetAlert.swal("ERROR!", vm.currentProfile.name + " profile add error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		}
	}

	function addProfile(profile) {
		vm.profilesFormUpdate = false;
		vm.currentProfile = {};
		openProfileForm();
	}

	function editProfile(profile) {
		vm.profilesFormUpdate = true;
		vm.currentProfile = angular.copy(profile);
		openProfileForm();
	}

	function openProfileForm() {
		ngDialog.open({
			template: 'plugins/keshig/partials/profile-form.html',
			scope: $scope
		});
	}

	function runOptionForm(optionName) {
		vm.servers2Test = [];
		if(vm.optionType == 'DEPLOY') {
			vm.servers2Test = vm.serversByType['DEPLOY_SERVER'];
		} else if(vm.optionType == 'TEST') {
			vm.servers2Test = vm.serversByType['TEST_SERVER'];
		} else {
			vm.servers2Test = vm.serversByType['BUILD_SERVER'];
		}
		vm.option2Run = optionName;

		ngDialog.open({
			template: 'plugins/keshig/partials/options/option-server-select-popup.html',
			scope: $scope
		});
	}

	function runOption(optionName, customOptionType, server) {
		console.log(optionName, customOptionType, server);
		if(customOptionType === undefined || customOptionType === null) customOptionType = vm.optionType;
		if(server === undefined || server === null) server = '';
		keshigSrv.startOption( customOptionType, optionName, server ).success(function(data){
			SweetAlert.swal("Success!", '"' + optionName + '" option start running.', "success");
		}).error(function (error) {
			SweetAlert.swal("ERROR!", '"' + optionName + '" option run error. Error: ' + error.replace(/\\n/g, ' '), 'error');
		});
		ngDialog.closeAll();
	}

	function updateOption() {

		if(vm.optionType == 'TEST') {
			if(vm.option2Add.latest === true) {
				vm.option2Add.targetIps = ['LATEST'];
			} else if(vm.option2Add.targetIps !== undefined) {
				var targetIps = vm.option2Add.targetIps.split(',');
				vm.option2Add.targetIps = targetIps;
			}
		}

		if( vm.optionFormUpdate ) {
			vm.optionFormUpdate = false;
			keshigSrv.updateOption( vm.optionType, JSON.stringify(vm.option2Add) ).success(function(data){
				SweetAlert.swal("Success!", vm.optionType + " option successfully updated.", "success");
				vm.optionFormUpdate = false;
			}).error(function (error) {
				SweetAlert.swal("ERROR!", vm.optionType + " option update error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		} else {
			keshigSrv.addOption( vm.optionType, JSON.stringify(vm.option2Add) ).success(function(data){
				SweetAlert.swal("Success!", vm.optionType + " option successfully added.", "success");
				vm.optionFormUpdate = false;
			}).error(function (error) {
				SweetAlert.swal("ERROR!", vm.optionType + " option add error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		}
		vm.option2Add = {};
	}

	function addOption() {
		if (vm.optionType === 'DEPLOY') {
			vm.currentOption = {
				name: "", branch: ""
			}
		} else {
			vm.currentOption = {
				targetIps: [],
				playbooks: [],
				name: "",
				all: false
			}
			vm.additionalIPs = [];
		}
		vm.optionFormUpdate = false;
		openOptionForm();
	}

	function editOption(option) {
		vm.currentOption = angular.copy(option);
		!vm.currentOption.targetIps ? vm.currentOption.targetIps = [] : null;
		!vm.currentOption.playbooks ? vm.currentOption.playbooks = [] : null;
		vm.additionalIPs = filterArray(vm.currentOption.targetIps, vm.targetIPs);
		vm.optionFormUpdate = true;
		openOptionForm();
	}

	function filterArray(src, filt) {
		var temp = {}, i, result = [];
		for (i = 0; i < filt.length; i++) {
			temp[filt[i]] = true;
		}
		for (i = 0; i < src.length; i++) {
			if (!(src[i] in temp)) {
				result.push(src[i]);
			}
		}
		return(result);
	}

	function isTestOptionFormValid() {
		console.log(vm.currentOption.targetIps.concat(vm.additionalIPs).length > 0 && vm.currentOption.playbooks.length > 0);
		return vm.currentOption.targetIps.concat(vm.additionalIPs).length > 0 && vm.currentOption.playbooks.length > 0;
	}

	function deleteProfile(profileName) {
		if(profileName === undefined) return;
		SweetAlert.swal({
			title: "Are you sure?",
			text: "Delete profile " + profileName + "!",
			type: "warning",
			showCancelButton: true,
			confirmButtonColor: "#ff3f3c",
			confirmButtonText: "Delete",
			cancelButtonText: "Cancel",
			closeOnConfirm: false,
			closeOnCancel: true,
			showLoaderOnConfirm: true
		},
		function (isConfirm) {
			if (isConfirm) {
				keshigSrv.removeProfile(profileName).success(function (data) {
					SweetAlert.swal("Deleted!", "Your profile has been deleted.", "success");
					keshigSrv.getProfiles().success(function (data) {
						vm.profiles = data;
					});
					ngDialog.closeAll();
				}).error(function (error) {
					SweetAlert.swal("ERROR!", "Your profile is safe. Error: " + error.replace(/\\n/g, ' '), "error");
				});
			}
		});
	}

	function deleteOption( optionName )	{
		if(optionName === undefined) return;
		SweetAlert.swal({
			title: "Are you sure?",
			text: "Delete option " + optionName + "!",
			type: "warning",
			showCancelButton: true,
			confirmButtonColor: "#ff3f3c",
			confirmButtonText: "Delete",
			cancelButtonText: "Cancel",
			closeOnConfirm: false,
			closeOnCancel: true,
			showLoaderOnConfirm: true
		},
		function (isConfirm) {
			if (isConfirm) {
				keshigSrv.deleteOption(vm.optionType, optionName).success(function (data) {
					SweetAlert.swal("Deleted!", "Your option has been deleted.", "success");
					keshigSrv.getAllOptions().success(function (data) {
						vm.optionsByType = data;
						ngDialog.closeAll();
					});
				}).error(function (error) {
					SweetAlert.swal("ERROR!", "Your option is safe. Error: " + error.replace(/\\n/g, ' '), "error");
				});
			}
		});
	}

	function deleteServer(serverId)	{
		if(serverId === undefined) return;
		SweetAlert.swal({
			title: "Are you sure?",
			text: "Delete server " + serverId + "!",
			type: "warning",
			showCancelButton: true,
			confirmButtonColor: "#ff3f3c",
			confirmButtonText: "Delete",
			cancelButtonText: "Cancel",
			closeOnConfirm: false,
			closeOnCancel: true,
			showLoaderOnConfirm: true
		},
		function (isConfirm) {
			if (isConfirm) {
				keshigSrv.removeServer(serverId).success(function (data) {
					SweetAlert.swal("Deleted!", "Your server has been deleted.", "success");
					keshigSrv.getServers().success(function (data) {
						vm.servers = data;
					});
				}).error(function (error) {
					SweetAlert.swal("ERROR!", "Your server is safe. Error: " + error.replace(/\\n/g, ' '), "error");
				});
			}
		});
	}

	function updateServer( id )	{
		if(vm.server2Add.serverName === undefined) return;
		if(vm.server2Add.serverId === undefined) return;
		if(vm.server2Add.serverType === undefined) return;

		if( vm.serverFormUpdate ) {
			vm.serverFormUpdate = false;
			keshigSrv.updateServer( vm.server2Add ).success(function(data){
				SweetAlert.swal("Success!", "Keshig server successfully updated.", "success");
				vm.dtInstance.reloadData(null, false);
			}).error(function (error) {
				SweetAlert.swal("ERROR!", "Keshig server update error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		} else {
			keshigSrv.addServer( vm.server2Add ).success(function(data){
				SweetAlert.swal("Success!", "Keshig server successfully added.", "success");
				vm.dtInstance.reloadData(null, false);
			}).error(function (error) {
				SweetAlert.swal("ERROR!", "Keshig server add error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		}
		vm.server2Add = {};
	}

	function updateProfile() {
		console.log(vm.profiles2Add);
		if( vm.profilesFormUpdate ) {
			vm.profilesFormUpdate = false;
			keshigSrv.updateProfile( JSON.stringify(vm.profiles2Add) ).success(function(data){
				SweetAlert.swal("Success!", vm.profiles2Add.name + " profile successfully updated.", "success");
				vm.dtInstance.reloadData(null, false);
			}).error(function (error) {
				SweetAlert.swal("ERROR!", vm.profiles2Add.name + " profile update error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		} else {
			keshigSrv.addProfile( JSON.stringify(vm.profiles2Add) ).success(function(data){
				SweetAlert.swal("Success!", vm.profiles2Add.name + " profile successfully added.", "success");
				vm.dtInstance.reloadData(null, false);
			}).error(function (error) {
				SweetAlert.swal("ERROR!", vm.profiles2Add.name + " profile add error. Error: " + error.replace(/\\n/g, ' '), "error");
			});
		}
		//vm.profiles2Add = {};
	}

	function getOptionTableCol() {
		switch(vm.optionType) {
			case 'CLONE':
				return [
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(actionEditOption),
					DTColumnBuilder.newColumn('name').withTitle('Name'),
					DTColumnBuilder.newColumn('url').withTitle('URL'),
					DTColumnBuilder.newColumn('branch').withTitle('Branch'),
					DTColumnBuilder.newColumn('timeOut').withTitle('TimeOut'),
					DTColumnBuilder.newColumn('active').withTitle('Active').renderWith(optionStatusIcon),
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(runOptionButton),
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(deleteAction)
				];
			case 'DEPLOY':
				return [
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(actionEditOption),
					DTColumnBuilder.newColumn('name').withTitle('Name'),
					DTColumnBuilder.newColumn('timeOut').withTitle('TimeOut'),
					DTColumnBuilder.newColumn('active').withTitle('Active').renderWith(optionStatusIcon),
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(runOptionButton),
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(deleteAction)
				];
			case 'TEST':
				return [
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(actionEditOption),
					DTColumnBuilder.newColumn('name').withTitle('Name'),
					DTColumnBuilder.newColumn('timeOut').withTitle('TimeOut'),
					DTColumnBuilder.newColumn('playbooks').withTitle('Playbooks').renderWith(playbooksTags),
					DTColumnBuilder.newColumn('active').withTitle('Active').renderWith(optionStatusIcon),
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(runOptionButton),
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(deleteAction)
				];
			case 'BUILD':
				return [
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(actionEditOption),
					DTColumnBuilder.newColumn('name').withTitle('Name'),
					DTColumnBuilder.newColumn('timeOut').withTitle('TimeOut'),
					DTColumnBuilder.newColumn('runTests').withTitle('Run Tests').renderWith(optionStatusIcon),
					DTColumnBuilder.newColumn('cleanInstall').withTitle('Clean Install').renderWith(optionStatusIcon),
					DTColumnBuilder.newColumn('active').withTitle('Active').renderWith(optionStatusIcon),
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(runOptionButton),
					DTColumnBuilder.newColumn(null).withTitle('').notSortable().renderWith(deleteAction)
				];
		}
	}

	function dateToFormat(date) {
		if(date === undefined || date === null) return 'In progress';
		var dateFormat = new Date(date);
		return (dateFormat.getMonth() + 1) + '/'
			+ dateFormat.getDate() + '/'
			+ dateFormat.getFullYear() + ' '
			+ dateFormat.getHours() + ':' + dateFormat.getMinutes() + ':' + dateFormat.getSeconds();
	}

	function getBaseUrl() {
		var pathArray = location.href.split( '/' );
		//var protocol = pathArray[0];
		var protocol = 'http:';
		var hostWithPort = pathArray[2].split(':');
		var host = hostWithPort[0];
		var url = protocol + '//' + host;
		return url;
	}
}

function checkboxListDropdown() {
	return {
		restrict: 'A',
		link: function(scope, element, attr) {
			$(element).click(function () {
				if(!$(element).hasClass("is-active")) {
					$(".b-form-input_dropdown").removeClass('is-active');
					$(element).addClass('is-active');
				} else {
					$(element).toggleClass("is-active");
				}
			});

			$(".b-form-input-dropdown-list").click(function(e) {
				e.stopPropagation();
			});
		}
	}
};

