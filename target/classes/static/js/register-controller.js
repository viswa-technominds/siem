app.controller("registerController", ['$scope', 'registerFactory','$rootScope','$timeout','$location','$uibModal', function ($scope, registerFactory,$rootScope, $timeout,$location) {


	var self = this;

	self.register = {id:'',userName:'',password:'',fullName:'',phoneNumber:'',accessType:'',companyName:''};
	self.alertMessagaes =[];
	$scope.submitData = function(){

		registerFactory.register(self.register).then(function (response) {

			if(response.data.status){
				self.alertMessagaes.push({ type: 'success', msg: 'User was successfully register please check your email to activate your account' });
				self.register = {id:'',userName:'',password:'',fullName:'',phoneNumber:'',accessType:''};
			}else if( response.data.validationErrors){
				var errors = response.data.validationErrors;
				for(var i=0;i<errors.length;i++){
					self.alertMessagaes.push({ type: 'danger', msg: errors[i].defaultMessage });
				}
				$timeout( function(){
					self.alertMessagaes =[];
				}, 5000 );
			}else{
				self.alertMessagaes.push({ type: 'danger', msg: response.data.errorMessage });
				$timeout( function(){
					self.alertMessagaes =[];
				}, 5000 );
			}


		}, function (error) {
			$scope.status = 'Unable to load customer data: ' + error.message;
		});



	}


}]);
