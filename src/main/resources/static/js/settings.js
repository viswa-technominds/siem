app.controller("settings", ['$scope', 'settingsFactory','$rootScope','$timeout','$location','$uibModal','$sce','$window', function ($scope, settingsFactory,$rootScope, $timeout,$location,$uibModal,$sce,$window) {

	var self = this;
	var strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})");
	
    $scope.userSettings = {password:'',newPassword:'',confirmPassword:'',userName:'',fullName:'',accessToken:'',company:'',planType:''};
    self.alertMessagaes = [];

	
    $scope.closeAlert = function(index) {
		self.alertMessagaes.splice(index, 1);
	};
	
	settingsFactory.getUserInformation().then(function (response) {
		self.userDetails = response.data;
	}, function (error) {
		$scope.status = 'Unable to load customer data: ' + error.message;
	});
	
	
	$scope.changePasswordAfterLogin = function(){
		self.alertMessagaes = [];
		if($scope.userSettings.newPassword!=$scope.userSettings.confirmPassword){
			self.alertMessagaes.push({ type: 'danger', msg: "New Password and Confirm Password must match." });
			return false;
		}
		if(!strongRegex.test($scope.userSettings.newPassword)){
			self.alertMessagaes.push({ type: 'danger', msg: "This password does not meet all the requirements. Please enter another password." });
			return false;
		}
		
		settingsFactory.changePasswordAfterLogin($scope.userSettings).then(function (response) {
			
			if(response.data.status){
				window.location.href = "/index.html#!/kibana"
			}else{
				self.alertMessagaes.push({ type: 'danger', msg:  response.data.error});
			}
			
		}, function (error) {
			$scope.status = 'Unable to load customer data: ' + error.message;
		});
	}
    
    $scope.saveChanges = function(){
    		self.alertMessagaes = [];
    		if($scope.userSettings.newPassword!=$scope.userSettings.confirmPassword){
    			self.alertMessagaes.push({ type: 'danger', msg: "New Password and Confirm Password must match." });
    			return false;
    		}
    		if(!strongRegex.test($scope.userSettings.newPassword)){
    			self.alertMessagaes.push({ type: 'danger', msg: "This password does not meet all the requirements. Please enter another password." });
    			return false;
    		}
    		
    		settingsFactory.changePassword($scope.userSettings).then(function (response) {
    			
    			if(response.data.status){
    				self.alertMessagaes.push({ type: 'success', msg: "Successfully changed the password" });
    				 $scope.userSettings = {password:'',newPassword:'',confirmPassword:'',userName:'',fullName:'',accessToken:'',company:'',planType:''};
    				$("#change-password-modal").hide();
    				$("div.modal-backdrop").remove();
    			}else{
    				self.alertMessagaes.push({ type: 'danger', msg:  response.data.error});
    			}
    			
    		}, function (error) {
    			$scope.status = 'Unable to load customer data: ' + error.message;
    		});
    		
    		
    }
    
    $scope.closeDailog = function(){
    		
    	
    		 $scope.userSettings = {password:'',newPassword:'',confirmPassword:''};
    		
    }

	$scope.currentTab = "home";
	$scope.active = "active"
	
	$scope.openActiveTab = function(tab){
		if(tab==="users"){
			
			
			settingsFactory.getUsersWithInCompany(self.userDetails.companyName).then(function (response) {
				self.allUsers = response.data;
			}, function (error) {
				$scope.status = 'Unable to load customer data: ' + error.message;
			});
			
		}
		$scope.currentTab = tab;
	}
	
	$scope.createUsers = function(){
		
		$scope.userSettings.planType = self.userDetails.planName;
		$scope.userSettings.accessToken = self.userDetails.accessToken;
		$scope.userSettings.company = self.userDetails.companyName;
		
		settingsFactory.createUsers($scope.userSettings).then(function (response) {
			//self.allUsers = response.data;
		}, function (error) {
			$scope.status = 'Unable to load customer data: ' + error.message;
		});
	}
	
	$scope.displayCreateUserTemplate = function(){
		
		$("#create-user-modal").modal();
	}

	$scope.openPasswordDialog = function(){
		//$("#change-password-modal").
		
		$("#change-password-modal").modal();
	}


}]);