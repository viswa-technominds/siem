
app.controller("loginController", ['$scope','$rootScope','$timeout','$location','$uibModal','$http','$window', function ($scope,$rootScope, $timeout,$location,$uibModal,$http,$window) {

	$scope.loginData = {username:'',password:''};

	$scope.isFaileMessage = false;
	
	$scope.submitData = function(){
		$http.post("/user/custom-login",$scope.loginData).then(
				function(data){
					if(data.data.token){
						
						$window.localStorage.setItem("token",data.data.token)
						if(data.data.requiredPasswordChange==="true"){
							window.location.href = "/change_password.html"
							
						}else{
							window.location.href = "/index.html#!/kibana"
						}
						
					}
				},function(e){
					$scope.isFaileMessage = true;
				}
		);
	}

}]);
