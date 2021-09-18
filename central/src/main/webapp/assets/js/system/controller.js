/**
*
*
*/
var lecaSystemObj = null;

var leca_system_controller = function() {
	
	/* Used to holds last operation's response from server */
	this.request = null;
	/* Used to holds last operation's response from server */
	this.response = null;	
	/* Holds the current name of the context */
	this.context = null;
	/* Holds the context objects ( controller may have N number of active context, earlier it was as singleton ) */
	this.contexts = {};
	/* Object which manage all action for current context */
	this.contextObj = null;
	/* Current view mode (archive, single or create) */
	this.viewMode = null;
	/* Previous view mode ( some obvious reason i need this - especially to preserve local cache ) */
	this.lastViewMode = null;	
	/* Element Id of the top main app container */
	this.appHolder = "leca-system-workspace";	
	/* Holds the bread crumb container */
	this.breadCrumb = $("#leca-header-breadcrumb");
	/* jQuery object reference of the top action buttons container */
	this.topActionBtnHolder = $("#leca-top-action-bar"); 
	/* jQuery object reference of the top notification container */
	this.notifyFlash = $("#leca-system-loading-notification");
	/* jQuery object reference bottom alert bar */
	this.alertBar = $("#leca-system-global-notification");
	/* prevent user from doing anything while any communication between server & client is active. */
	this.ajaxFlaQ = true;
	/* Holds the active main menu's reference */
	this.currentMenu = null;
	/* Used to store dependencies data
	 * Act as a common data pool
	 * Usually it contains  */
	this.bucket = {};
	/**/
	this.alertTimer = null;
	/* */
	this.prefetchIndex = 0;
	
	this.init = function() {
		
		this.registerEvents();
		$("#leca-primary-navigation > li:first > a").trigger("click");
				
	};
	
	this.registerEvents = function() {
		
		$(document).on("click", "a", function(e) {
			e.preventDefault();
		});
		
		$(document).on("click", "#leca-menu-collapse-btn", this, function(e) {
			$("#leca-master-container").toggleClass("expanded");
			if (typeof e.data.contextObj.systemLayoutChange !== 'undefined') {				
				e.data.contextObj.systemLayoutChange();
			}
		});
		
		$(document).on("click", "#leca-primary-navigation a", this, function(e) {
			$(this).closest("ul").find("a").removeClass("selected");
			$(this).addClass("selected");	
			e.data.currentMenu = $(this);			
			$("#leca-workarea-content > div").hide();
			$("#leca-workspace-"+ $(this).attr("data-context")).show();
			e.data.switchContext($(this).attr("data-context"));							
		});	
		
		/* Tab widget */
		$(document).on("click", "a.leca-tab-btn", this, function(e) {
			$(this).parent().find("a").removeClass("selected");
			/* For report designer */
			$(this).closest("div.leca-tab-header").find("a").removeClass("selected");
			
			$(this).addClass("selected");
			$($(this).attr("href")).parent().find(">div").hide();
			$($(this).attr("href")).show();
			if (e.data.contextObj != 'undefined') {
				e.data.contextObj.handleTabAction($(this));
			}
		});
		
		/* Register action for 'new' record */
		$(document).on("click", "button.leca-create-action", this, function(e) {
			if (!$(this).hasClass("disabled")) {
				e.data.doNew("create");				
				/* Hide the validation message - might be displayed with previous attempt */
				$("span.leca-field-message").hide();
			}			
		});	

		/* Register action for 'save' record */
		$(document).on("click", "button.leca-save-action", this, function(e) {
			if (!$(this).hasClass("disabled")) {
				e.data.doCreate();				
			}
		});

		/* Register action for 'update' record */
		$(document).on("click", "button.leca-update-action", this, function(e) {
			if (!$(this).hasClass("disabled")) {
				e.data.doUpdate(); 
			}
		});		
		
		/* Register action for 'delete' record */
		$(document).on("click", "button.leca-delete-action", this, function(e) {
			if (!$(this).hasClass("disabled")) {
				e.data.doDelete(); 
			}
		});
		
		/* Register action for 'back' record */
		$(document).on("click", "button.leca-cancel-action", this, function(e) {	
			if (!$(this).hasClass("disabled")) {
				e.data.doCancel();
			}
		});
		
		/* Register action for 'meta' record */
		$(document).on("click", "button.leca-meta-action", this, function(e) {	
			if (!$(this).hasClass("disabled")) {
				e.data.switchView( "meta" );
			}
		});

		/* Register action for single view */
		$(document).on("click", "a.leca-single-link", this, function(e) {
			if (!$(this).hasClass("disabled")) {
				e.data.doSingle($(this));
			}
		});	
		
		$(document).on("click", "button.leca-grid-record-btn-action", this, function(e) {
			e.data.contextObj.handleGridRecordAction($(this));
		});
		
		/* Register single views local actions ( context specific eg. add delete product in collection single view )*/
		$(document).on("click", "button.leca-sub-action-btn", this, function(e) {
			if (!$(this).hasClass("disabled")) {
				e.data.ContextObj.handleBtnAction($(this));
			}
		});
		
		$(document).on("change", "select.leca-meta-option-type", this, function(e) {	
			if (!$(this).hasClass("disabled")) {
				e.data.ContextObj.handleEvent($(this));
			}
		});
		
		$(document).on("click", "button.leca-meta-add-btn", this, function(e) {
			if (!$(this).hasClass("disabled")) {
				e.data.ContextObj.handleEvent($(this));
			}
		});
		
		/* Hide validation message when user start to type on the field
		 * Validation message might be visible due previous form submit attempt */
		$(document).on("keyup", ".leca-form-field", this, function(e) {
			if ($(this).next().length > 0 && $(this).next().hasClass("leca-field-message")) {
				$(this).next().hide();
			}
		});
		
		/* Click event for access control switch widget */
		$(document).on("click", "label.leca-grid-toggle-switch", this, function(e) {
			e.stopPropagation();
			if (e.data.ajaxFlaQ) {
				$(this).toggleClass("on");
				e.data.handleGridToggle($(this));
			} else {
				/* Ajax operation is pending - alert the user */
				e.data.notifyFlash.html("Another operation is running, please wait.!");
			}
			return false;
		});
		
		$(document).on("click", "a.leca-model-close-btn", this, function(e) {
			e.data.contextObj.modelDialogClosed($(this).parent().parent().attr("data-model"));
			$(this).closest("div.leca-model-dialog").remove();			
		});
		
		$(document).on("keyup", this, function(e) {
			if (e.key === "Escape" && $("div.leca-model-dialog").length > 0) {
				e.data.contextObj.modelDialogClosed($("div.leca-model-dialog > div").attr("data-model"));
				$("div.leca-model-dialog").remove();
			}
		});
		
	};
	
	this.switchContext = function( _context ) {	
		
		this.context = _context;
		
		/**
		 * creating dynamic context object. according to left menu bar selection this.contextObj 
		 * can be 'user', 'role', 'module' or 'capability'
		 **/
		if( this.contexts[ this.context ] ) {
			/* Well the contextObj is already created
			 * so just make it as an active context */
			this.contextObj = this.contexts[ this.context ];			
			/* Most of the time the view would be there visible
			 * Just to make sure all the relevant properties are updated */
			if( this.contextObj.viewMode != "create" ) {
				this.switchView( "archive" );
			} else {
				this.switchView( this.contextObj.viewMode );
			}	
			/* Let the module knows that the user has clicked their menu */
			this.contextObj.menuClicked();
		} else {
			/* Looks like this is the first time the user trying this context */
			if( typeof lecaSystemConfig[ this.context ] !== 'undefined' ) {
				this.contextObj = new window[ "leca_"+ this.context +"_context" ]( this, lecaSystemConfig[ this.context ] );
				/* Store the context object for later usage */
				this.contexts[ this.context ] = this.contextObj;
				/* Archive view is the default view whenever a context is switched */	
				this.switchView( "archive" );
				if( lecaSystemConfig[ this.context ].dependencies.length == 0 ) {					
					/* Start the context object */
					this.contextObj.init();
				} else {
					/* Looks like it need some data pre loaded
					 * before its get initiated */
					this.prefetchIndex = 0;
					this.loadDependencies();
				}				
			} else {
				/* Looks like the configuration is missing */
				alert( "Configuration for " + this.context + " not found" );
			}			
		}
		
	};
	
	this.switchView = function( _view ) {
		/* Update global last view mode property */
		this.lastViewMode = this.viewMode;
		/* Update global current view mode property */
		this.viewMode = _view;
		/* Update context specific view mode property */
		this.contextObj.viewMode = _view;
		/* Now hide all the view belongs to this context */
		$("#leca-workspace-"+ this.context +"-archive").hide();	
		$("#leca-workspace-"+ this.context +"-single").hide();	
		$("#leca-workspace-"+ this.context +"-create").hide();	
		$("#leca-workspace-"+ this.context +"-meta").hide();
		/* Update the top action bar */
		this.updateActionBar();
		/* Show the concerned view */
		$("#leca-workspace-"+ this.context +"-" + _view).show();
	};
	
	this.updateActionBar = function() {
		
		let icon = "",
			btnClass = "";
		
		this.breadCrumb.html(this.currentMenu.html());
		
		/* Clear the top action bar */
		this.topActionBtnHolder.html("");		
		if (!$.isEmptyObject(lecaSystemConfig[this.context][this.viewMode].actions)) {
			var actions = lecaSystemConfig[this.context][this.viewMode].actions;
			for (let i = 0; i < actions.length; i++) {				
				btnClass = "leca-btn leca-action-btn "+ actions[i].state +" leca-"+ actions[i].action +"-action";				
				if (actions[i].icon != "") {
					icon = '<i class="material-icons">'+ actions[i].icon +'</i>&nbsp;';
				}
				this.topActionBtnHolder.append($('<button class="'+ btnClass +'">'+ icon + actions[i].label +'</button>'));
			}			
		}	
		if (this.viewMode == "create") {
			this.breadCrumb.html(this.breadCrumb.html() +" <span> / New *</span>");
		}
			
		
	};
	
	this.loadDependencies = function() {
		let depMeta = lecaSystemConfig[this.context].dependencies;
		if (this.prefetchIndex < depMeta.length) {
			if (!this.bucket[depMeta[this.prefetchIndex].context] 
				|| !this.bucket[depMeta[this.prefetchIndex].context][depMeta[this.prefetchIndex].grid]
				|| depMeta[this.prefetchIndex].invalidate) {
				this.request = this.prepareRequest("/system/"+ depMeta[this.prefetchIndex].action, depMeta[this.prefetchIndex].context, depMeta[this.prefetchIndex].task, 0, {"dependency": true, grid: depMeta[this.prefetchIndex].grid});
				this.request = this.contextObj.prepareDependencyRequest(this.request);
				this.dock();
			} else {
				/* This means the bucket already has the records - so skip to next dependency */
				this.prefetchIndex++;
				this.loadDependencies();
			}			
		} else {
			/* This means we have fetched all the pre data so now safe to initiate the Context Object */
			this.contextObj.init();
		}		
	};
	
	/**
	 * Instantiate the AcmGrid object
	 * It doesn't load the data, just Inflating the grid using the Configuration
	 * @param _context		: Context name where the grid belongs to
	 * @param _view 		: View mode
	 * @param _gname		: Grid name ( grid's key to be exact )
	 */
	this.inflateGridView = function(_context, _view, _gname, _isSubGrid, _id) {
		if (typeof this.contexts[_context] != 'undefined') {			
			if (!_isSubGrid) {
				/* For main archive grids */
				if (this.contexts[_context].doArchive(_gname)) {
					/* Inflate the grid */				
					var gMeta = this.contexts[_context].config[_view].grids[_gname];
					var container = $("#leca-"+ _context +"-"+ _view +"-"+ _gname +"-grid");
					if (container.length > 0) {
						this.contexts[_context].grids[_gname] = new lecaSystemGrid(this.contexts[_context], container, gMeta, _gname);
						this.contexts[_context].grids[_gname].renderDataGrid();
						/* Prepare the ajax request */
						this.request = this.prepareRequest("/system/" + gMeta.source.action, _context, gMeta.source.task, 0, {view: _view, grid: _gname, isSubGrid: false});
						/* Time to initiate Ajax for archive list */	
						this.dock();
					} else {
						alert("Parant container for grid : "+ _gname +" not found");
					}												
				}
			} else {
				/* For sub archive grids */
				/* Inflate the grid */				
				var gMeta = this.contexts[_context].config["single"].sub_grids[_gname];
				var container = $("#leca-"+ _context +"-sub-archive-"+ _gname +"-grid");
				if (container.length > 0) {
					this.contexts[_context].grids[_gname] = new ikeaAcmGrid( this.contexts[ _context ], container, gMeta, _gname );
					this.contexts[ _context ].grids[ _gname ].renderDataGrid();
					/* Prepare the ajax request */
					this.request = this.prepareRequest( "/system/" + gMeta.source.action, _context, gMeta.source.task, 0, { view: _view, grid: _gname, ID: _id, isSubGrid: true } );
					/* Time to initiate Ajax for archive list */	
					this.dock();
				} else {
					alert( "Parant container for grid : "+ _gname +" not found" );
				}
			}
		} else {
			alert( "Context not initialized" );
		}
	};
	
	/**
	 * Data loading utility for Acm Grid
	 * @param _context		: Context name where the grid belongs to
	 * @param _gname		: Grid name ( grid's key to be exact ) */
	this.loadGridRecord = function(_context, _gname) {
		if(  typeof this.bucket[ _context ] != 'undefined' && typeof this.bucket[ _context ][ _gname ] != 'undefined' ) {
			this.contexts[ _context ].grids[ _gname ].loadGrid( this.bucket[ _context ][ _gname ].columns, this.bucket[ _context ][ _gname ].records );
		} else {
			alert( "Bucket doesn't have Records" );
		}	
	};
	
	this.doSingle = function(_target, _key, _val) { 
		if (!this.ajaxFlaQ) {			
			var notifyText = this.notifyFlash.html();				
			if (notifyText.indexOf("Please wait while ") == -1) {
				this.notifyFlash.html("Please wait while "+ notifyText);
			}			
			return;
		}
		let _context = _target.attr("data-context");
		/* if this.Context != target then it's a cross context single view request */
		if (this.context != _context) {
			
		} else {			
			if (this.context == "dashboard") {
				/* For dashboard we use same view for both Create & Single */
				this.switchView("create");
			} else {
				this.switchView("single");
			}				
			this.contextObj.doSingle(_target);
		}
	};
	
	this.doCancel = function() {
		this.breadCrumb.html(this.currentMenu.html());  
		this.switchView("archive");
		this.contextObj.menuClicked();
		this.contextObj.doCancel();
	};
	
	this.doNew = function() {
		/* Just switch the view to create mode */
		this.switchView("create");		
		/* Clear the create view's fields */
		this.clearFields( this.context, "create", "ikea-acm-"+ this.context +"-create-" );
		/* Call the context specific doNew() */
		this.contextObj.doNew();		
	};
	
	this.doCreate = function() {
		/* Just forward it to the context object */
		this.contextObj.doCreate();
	};
	
	this.doUpdate = function() {
		/* Just forward it to the context object */
		this.contextObj.doUpdate();
	};
	
	this.doDelete = function() {
		/* Just forward it to the context object */
		this.contextObj.doDelete();
	};
	
	this.handleGridToggle = function( _switch ) {
		var payload = {},
		task = _switch.attr("data-task"),
		grid = _switch.attr("data-grid"),
		option = _switch.attr("data-option");		
		
		payload[_switch.attr("data-pkey")] = _switch.attr("data-rkey");	
		if( _switch.hasClass( "on" ) ) {
			payload[ option ] = _switch.find( "span.leca-toggle-label" ).attr( "data-on-value" );
		} else {
			payload[ option ] = _switch.find( "span.leca-toggle-label" ).attr( "data-off-value" );
		}
		/* Mark the payload so that we can update the local archive list, once the records are successfully updated on server */
		payload[ "ARCHIVE_UPDATE" ] = true;
		payload[ "BUCKET_KEY" ] = grid;
		/* Prepare the Ajax request */		
		this.request = this.prepareRequest("/visualizer/rf/update", this.context, task, 0, payload);		
		/* Time to initiate Ajax for update this record */	
		this.dock();
	};
	
	this.doAlert = function(_msg, _force) {
		
	};
	
	this.flashAlert = function(_msg, _duration) {
		var me = this;
		var duration = 3000;
		if (_duration) {
			duration = _duration;
		}
		this.notifyFlash.html(_msg);
		this.notifyFlash.show();
		/* Now hide it */
		setTimeout(function() {me.notifyFlash.hide();}, duration);
	};
	
	/**
	 * Common method for clearing form fields
	 * This will be called from 'doNew' of controller object itself
	 * This method can also be used independently by any context object */
	this.clearFields = function(_context, _view, _namespace) {
		if (typeof this.contexts[_context] != 'undefined') {
			if (typeof d_config != 'undefined' 
				&& typeof d_config[ _view ] != 'undefined'   
				&& typeof d_config[ _view ].fields != 'undefined') {
				var fields = d_config[ _view ].fields;
				for (let i = 0; i < fields.length; i++) {
					if (fields[i].type == "TEXT" || fields[i].type == "PASSWORD" 
						|| fields[i].type == "NUMBER" || fields[i].type == "SELECT") {
						$("#"+_namespace + fields[i].key.toLowerCase()).val("");
					}
				}
			}
		}		
	};
	
	/**
	 * Fetch and form a single row of records
	 * from the create View
	 * Mostly used by the 'doCreate' method of Context Objects */
	this.fetchRecord = function(_context, _view, _namespace) {
		let payload = null;
		if (typeof this.contexts[_context] != 'undefined') {
			if (typeof d_config != 'undefined' 
				&& typeof d_config[_view] != 'undefined'   
				&& typeof d_config[_view].fields != 'undefined') {
				var fields = d_config[_view].fields;
				if (this.getValidate(_context, _view, _namespace, fields)) {
					payload = {};
					for (let i = 0; i < fields.length; i++) {
						if (fields[i].type == "TEXT" || fields[i].type == "PASSWORD" 
							|| fields[i].type == "NUMBER" || fields[i].type == "SELECT"
							|| fields[i].type == "TEXTAREA" || fields[i].type == "EMAIL") {
							payload[fields[i].key] = $("#"+_namespace + (fields[i].key.toLowerCase())).val();
						}
					}
				} else {
					payload = null;
				}				
			}
		}
		return payload;
	};
	
	/**
	 * Retrieve single record from the archive list
	 * Used extensively by the 'doSingle' method
	 * ( Whenever user click any link on grid view, which will take to single record view ) */
	this.getRecord = function(_context, _grid, _key, _val) {
		let record = null;
		/* Find the column position */
		let cPos = this.getColumnIndex(_context, _grid, _key);
		if (cPos != -1 && typeof this.bucket[_context] != 'undefined' && typeof this.bucket[_context][_grid] != 'undefined') {
			for (let i = 0; i < this.bucket[_context][_grid].records.length; i++) {
				if (this.bucket[_context][_grid].records[i][cPos] == _val) {
					record = this.bucket[_context][_grid].records[i];
					break;
				}
			}
		} else {
			alert("Looks like bucket doesn't have the records");
		}
		return record;
	};
	
	/**
	 * Get the column position
	 * From the Data Bucket */
	this.getColumnIndex = function(_context, _grid, _ckey) {
		let cIndex = -1;
		if (typeof this.bucket[_context] != 'undefined' && typeof this.bucket[_context][_grid] != 'undefined') {
			for (let i = 0; i < this.bucket[_context][_grid].columns.length; i++) {
				if (this.bucket[_context][_grid].columns[i] == _ckey) {
					cIndex = i;
					break;
				}
			}
		}
		return cIndex;
	};
	
	this.getCustomColumnIndex = function(_header, _key) {
		for (let i = 0; i < _header.length; i++) {
			if (_header[i] == _key) {
				return i;
			}
		}
		return -1;
	};
	
	/**
	 * Fetch the single value of particular column
	 * From the Data Bucket */
	this.getColumnValue = function(_context, _grid, _ckey, _cval, _target) {
		let cIndex = -1,
			tIndex = -1,
			columnList = [],
			recordList = [];		
		if (typeof this.bucket[_context] != 'undefined' && typeof this.bucket[_context][_grid] != 'undefined') {
			res = [];			
			columnList = this.bucket[_context][_grid].columns;
			recordList = this.bucket[_context][_grid].records;
			for (let i = 0; i < columnList.length; i++) {
				if (columnList[i] == _ckey) {
					cIndex = i;
				}
				if ( columnList[i] == _target) {
					tIndex = i;
				}
			}
			if (cIndex != -1 && tIndex != -1) {
				for (var i = 0; i < recordList.length; i++) {
					if (recordList[i][cIndex] == _cval) {						
						return recordList[i][tIndex];
					}
				}
			}
		}
		return null;
	};
	
	/**
	 * Fetch the entire set of particular column
	 * Act only on the bucket records */
	this.getColumnValues = function(_context, _grid, _key) {
		let res = null,
			cIndex = -1,		
			recordlist = [];
		if (typeof this.bucket[_context] != 'undefined' 
			&& typeof this.bucket[_context][_grid] != 'undefined') {
			res = [];			
			recordlist = this.bucket[_context][_grid].records;
			cIndex = this.getColumnIndex(_context, _grid, _key);
			if (cIndex != -1) {
				for (let i = 0; i < recordlist.length; i++) {
					res.push(recordlist[i][cIndex]);
				}
			}
		}
		return res;
	};
	
	/**
	 * Performs filter operation on a mentions records on the bucket */
	this.filterRecords = function(_context, _grid, _key, _val) {
		let res = null,
			cIndex = -1,			
			recordlist = [];
		if (typeof this.bucket[_context] != 'undefined' 
			&& typeof this.bucket[_context][_grid] != 'undefined') {
			res = [];			
			recordlist = this.bucket[_context][_grid].records;
			cIndex = this.getColumnIndex(_context, _grid, _key);
			if (cIndex != -1) {
				for (let i = 0; i < recordlist.length; i++) {
					if (recordlist[i][cIndex] == _val) {
						res.push(recordlist[i]);
					}					
				}
			}
		}
		return res;
	};
	
	/**
	 * Used to update the a record on the local archive list 
	 * Used especially while archive level update operation */
	this.updateRecord = function(_req) {
		let index = -1,
			context = _req.entity,
			record = _req.payload,
			grid = _req.payload["BUCKET_KEY"];
		/* Find the column position */
		let cPos = this.getColumnIndex(context, grid, "ID");
		if (cPos != -1 && typeof this.bucket[context] != 'undefined' && typeof this.bucket[context][grid] != 'undefined') {
			for (let i = 0; i < this.bucket[context][grid].records.length; i++) {
				if (this.bucket[context][grid].records[i][cPos] == record["ID"]) {					
					for (let j = 0; j < this.bucket[context][grid].columns.length; j++) {
						index = this.getColumnIndex(context, grid, this.bucket[ context][grid].columns[j]);
						if (index != -1) {
							this.bucket[context][grid].records[i][index] = record[this.bucket[context][grid].columns[j]];
						}						
					}		
					break;
				}
			}
		}
	};
	
	this.prepareRequest = function(_action, _entity, _task, _page, _payload, _dtype, _ctype) {	
		/* Default expecting data type is 'json' */
		var dtype = (typeof _dtype !== "undefined") ? _dtype : "json";
		/* 'application/json' will be the default content type, can be overridden otherwise */
		var ctype = (typeof _ctype !== "undefined") ? _ctype : "application/json; charset=utf-8";
		
		return {
			action: _action,
			entity: _entity,
			task: _task,
			page: _page,
			payload: _payload,
			data_type: dtype,
			content_type: ctype
		};
	};
	
	this.prepareResponse = function( _res ) {
		return {
			status: _res.status,
			message: _res.message,
			payload: _res.payload
		};
	};
	
	this.dock = function() {
		
		/* Store 'this' reference */
		var me = this;		
		/* see the Ajax handler is free */
		if (!this.ajaxFlaQ) {					
			this.notifyFlash.html("Please wait while processing");						
			return;
		}
		
		// Prepare Ajax object
		var param = {  
			type       : "POST",  
			data       : {leca_request_body: JSON.stringify(this.request)},   
			dataType   : this.request.data_type,
			contentType: this.request.content_type,
			url        : lecaOpt.docker + this.request.action,
			beforeSend : function() {
				/* Disable Ajax flag */
				me.ajaxFlaQ = false;				
				/* Disable all action buttons in the top bar */
				me.topActionBtnHolder.find("button").addClass("disabled");
				/* Notify the user regarding Ajax operation */			
				me.notifyFlash.html("Processing...");
				me.notifyFlash.show();			
			},
			success    : function(data) {	
				me.notifyFlash.hide();	
				/* Store the server's response */
				me.response = me.prepareResponse(data);
				/* Delegate to responseSuccessHandler for further processing */
				me.responseHandler();
			},
			error      : function(jqXHR, textStatus, errorThrown) {
				/* Disable the Ajax lock */
				me.ajaxFlaQ = true;				
				/* Enable all action buttons in the top bar */				
				me.topActionBtnHolder.find("button").removeClass("disabled");
				/* Hide the notification bar */
				me.notifyFlash.hide();				
			}, 
			complete   : function() {
				/* Enable all action buttons in the top bar */
				me.topActionBtnHolder.find("button").removeClass("disabled");
			}
		};
		
		// Boom.........
		$.ajax( param );
		
	};
	
	this.responseHandler = function() {
		/* Clone the Ajax request object - before it being updated for another operation
		 * Could happens on continuous ajax operation */
		var req = JSON.parse( JSON.stringify( this.request ) );
		var res = JSON.parse( JSON.stringify( this.response ) );
		
		/* Disable the ajax lock */
		this.ajaxFlaQ = true;
		if( res.status ) {
			
			if( typeof req.payload.dependency !== 'undefined' && typeof req.payload.dependency ) {
				var depMeta = lecaSystemConfig[ this.context ].dependencies;
				if( $.isEmptyObject( this.bucket[ depMeta[ this.prefetchIndex ].context ] ) ) {
					this.bucket[ depMeta[ this.prefetchIndex ].context ] = {};
				}
				if( $.isEmptyObject( this.bucket[ depMeta[ this.prefetchIndex ].context ][ depMeta[ this.prefetchIndex ].grid ] ) ) {
					this.bucket[ depMeta[ this.prefetchIndex ].context ][ depMeta[ this.prefetchIndex ].grid ] = {};
				}
				/* Because we have two types of LIST patterns 'diet' & 'keyval' */
				if( depMeta[ this.prefetchIndex ].diet ) {
					/* Splice the column row and store it */
					this.bucket[ depMeta[ this.prefetchIndex ].context ][ depMeta[ this.prefetchIndex ].grid ][ "columns" ] = res.payload.splice( 0, 1 );
					this.bucket[ depMeta[ this.prefetchIndex ].context ][ depMeta[ this.prefetchIndex ].grid ][ "columns" ] = this.bucket[ depMeta[ this.prefetchIndex ].context ][ depMeta[ this.prefetchIndex ].grid ][ "columns" ][0];
				}				
				/* Store the records in separate property */
				this.bucket[ depMeta[ this.prefetchIndex ].context ][ depMeta[ this.prefetchIndex ].grid ][ "records" ] = res.payload;
				/* Increment the prefetchIndex */
				this.prefetchIndex++;
				/* Notify the context object too - they might have to do something with this */
				this.contextObj.onDependencyResponse( req, res );
				/* Call the loadPreData for fetching next set of pre data */
				this.loadDependencies();
			} else if( typeof req.payload.view !== 'undefined' && typeof req.payload.grid !== 'undefined' ) {
				if( ! req.payload.isSubGrid ) {
					/* Since these are primary archives
					 * It goes to controller's archive pool
					 * from there the respective modules start to utilize these data
					 * In that way we can manage dependencies without pain */
					if( $.isEmptyObject( this.bucket[ req.entity ] ) ) {
						this.bucket[ req.entity ] = {};
					}
					if( $.isEmptyObject( this.bucket[ req.entity ][ req.payload.grid ] ) ) {
						this.bucket[ req.entity ][ req.payload.grid ] = {};
					}
					/* Splice the column row and store it */
					this.bucket[ req.entity ][ req.payload.grid ][ "columns" ] = res.payload.splice( 0, 1 );
					this.bucket[ req.entity ][ req.payload.grid ][ "columns" ] = this.bucket[ req.entity ][ req.payload.grid ][ "columns" ][0];
					/* Store the records in separate property */
					this.bucket[ req.entity ][ req.payload.grid ][ "records" ] = res.payload;
					/* Then hand over to context object */
					this.contexts[req.entity].handleResponse( req, res );
				} else {
					this.contexts[req.entity].handleResponse( req, res );
				}				
			} else if( typeof req.payload[ "ARCHIVE_UPDATE" ] !== 'undefined' && req.payload[ "ARCHIVE_UPDATE" ]
				&& typeof req.payload[ "BUCKET_KEY" ] !== 'undefined' && req.payload[ "BUCKET_KEY" ] ) {
				/* Well time to update bucket record */
				this.updateRecord( req );
			} else {
				this.doAlert( null, false );		
				this.contexts[req.entity].handleResponse( req, res );
			}
			
		} else {
			if( this.response.message === "signin" ) {
				/* Session timeout ( probably ), just reload the page */
				document.location.href = lecaOpt.docker;
			} else {
				this.doAlert( null, false );
				/**/
				this.contexts[req.entity].handleErrorResponse( req, res );
			}
		}
	};
	
};

$(document).ready(function() {
	lecaSystemObj = new leca_system_controller();
	lecaSystemObj.init();
});