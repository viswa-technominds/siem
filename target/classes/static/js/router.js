app.config(function($routeProvider) {

	$routeProvider
	.when("/kibana", {
		templateUrl : "templates/kibana.html",
		//controller : "incidentController"
	}).when("/incidents-details", {
		templateUrl : "templates/incidents-details.html"
		
	})
	.when("/settings", {
		templateUrl : "templates/settings.html",
		
	}).when("/logingest", {
		templateUrl : "templates/log-ingest.html",
		
	}).when("/feedstats", {
		templateUrl : "templates/feed.html"
		
	}).when("/feedmanagement", {
		templateUrl : "templates/feedmanagement.html",
		
	}).when("/firewall", {
		templateUrl : "templates/paloalto-dashboard.html"
		
	}).when("/iac-dashboard", {
		templateUrl : "templates/iac-dashboard.html"
		
	}).when("/whitelist", {
		templateUrl : "templates/whitelist.html"
		
	}).when("/add-indicator", {
		templateUrl : "templates/add-indicator.html"
		
	}).when("/windows-dashboard", {
		templateUrl : "templates/windows-dashboard.html"
		
	}).when("/search", {
		templateUrl : "templates/search.html"
		
	}).when("/users", {
		templateUrl : "templates/users.html"
		
	}).when("/themes", {
		templateUrl : "templates/themes-editor.html"
		
	}).when("/user-prefrence", {
		templateUrl : "templates/user-prefrence.html"
		
	});
	
	
});