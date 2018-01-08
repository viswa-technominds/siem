app.controller("kibanaController", ['$scope', 'kibanaFactory','$rootScope','$timeout','$location','$uibModal','$sce','$window', function ($scope, kibanaFactory,$rootScope, $timeout,$location,$uibModal,$sce,$window) {


	$scope.trustSrc = function(src) {
	    return $sce.trustAsResourceUrl(src);
	  }
	  
	  $scope.movie = {src:"http://localhost:5601/searchguard/login?token="+$window.localStorage.getItem('token'), title:"Egghead.io AngularJS Binding"};

	//$scope.token = "http://localhost:5601/searchguard/login?token="+$window.localStorage.getItem('token');



}]);
