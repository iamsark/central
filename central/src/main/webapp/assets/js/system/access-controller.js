/**
 *
 *
 */
var leca_access_control_context = function(_leca, _config) {
	
	/* Controller object reference */
	this.controller = _leca;
	/* config object for this module */
	this.config = _config;
	/* Used to store view mode this context */
	this.viewMode = "archive";
	/* Dependency Records that this modules needs to work on */
	this.preData = {};
	/* Holds the all the grids object reference
	 * Instantiated from this module */
	this.grids = {};
	/* Holds archive list for this context */
	this.records;
	/* Holds the current record (Here it would be report object) */
	this.activeRecord = {};
	
	/**
	 * Bootstrap method for this context
	 */
	this.init = function() {
		
	};
	
	/**
	 * Called whenever user click the menu that belongs to this module
	 */
	this.menuClicked = function() {
		
	};
	
	/**
	 * You have to use jQuery 'on' method for registering custom events
	 * Before registering use 'off' to cancel the same event which might previously registered
	 */
	this.registerEvents = function() {
		
	};
	
	/**
	 * Called whenever user click on any of the filters which belongs to this module
	 * Filters are usually placed on top of data grid component */
	this.handleFilterAction = function(_filter, _option) {
		
	};
	
	/**
	 * This handler will be called
	 * Whenever user click on any button which belongs to this module */
	this.handleBtnAction = function(_target) {
		
	};
	
	/**
	 * This handler will be called whenever user pressed any tab
	 * which belongs to this context
	 */
	this.handleTabAction = function(_tab) {
		
	};
	
	/**
	 * Called whenever model dialog closed 
	 */
	this.modelDialogClosed = function(_model) {
		
	};
	
	this.doArchive = function() {
		return false;
	};
	
	/**
	 * This handler will be called from 'handleResponse' method
	 * Whenever a response arrived for any GRID view
	 * This method has not much to so except hand over the control to controller's 'loadGridRecord' method */
	this.renderArchive = function( _req, _res ) {
		
	};
	
	/**
	 * Called by the controller whenever any link field clicked on the grid view
	 */
	this.doSingle = function(_target) {
		let _key = _target.attr("data-key"),
 			_val = _target.attr("data-val");
	};
	
	/**
	 * Called by the controller for the Single View (If any archive on the Single View for this context)
	 */
	this.doSubArchive = function() {
		
	};
	
	/**
	 * Called by the controller for Cancel button click
	 */
	this.doCancel = function() {
		
	};
	
	/**
	 * Called by the controller for Add or New button click
	 */
	this.doNew = function() {
		
	};
	
	/**
	 * Called by the controller for Save or Update button click
	 */
	this.doCreate = function() {
		
	};
	
	/**
	 * Called by the controller for Delete button click
	 */
	this.doDelete = function() {
		
	};	
	
	/**
	 * Called by the controller whenever view resizing happening
	 */
	this.onViewResize = function() {
		
	};
	
	/**
	 * Called by the controller for providing the list of dependencies to load
	 */
	this.prepareDependencyRequest = function(_req) {
		return _req;
	};
	
	/**
	 * Called by the controller in the event response arrived for the given dependencies
	 */
	this.onDependencyResponse = function(_req, _res) {
		
	};
	
	/**
	 * Called by the controller, whenever there is an Ajax Response for this context
	 */
	this.handleResponse = function( _req, _res ) {
		
	};
	
	/**
	 * Called by the controller, whenever an exception while doing Ajax on behalf of this context
	 */
	this.handleErrorResponse = function( _req, _res ) {
		
	};
	
};