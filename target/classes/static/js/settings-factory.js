app.factory('settingsFactory', ['$http', function($http) {
	var baseUrl = "/user";

	return {
		changePassword : function(data){
			return $http.post(baseUrl+"/change-password",data);
		},
		getUserInformation : function(){
			return $http.get(baseUrl+"/get-user-informaiton")
		},
		getUsersWithInCompany : function(companyName){
			return $http.get(baseUrl+"/getAllUsersWithinCompany?company="+companyName)
		},
		createUsers : function(data){
			return $http.post(baseUrl+"/createlocalusrers",data)
		},
		changePasswordAfterLogin : function(data){
			return $http.post(baseUrl+"/change-password-after-login",data)
		}
		
	}
}]);