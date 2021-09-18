/**
 *
 *
 */
var leca_report_designer_context = function(_leca, _config) {
	
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
	/* Holds the current report type object */
	this.activeReportType = {};
	/* Holds the current report template object */
	this.activeReportTemplate = {};
	/* Holds the current report lines object */
	this.activeReportLines = {};
	/* Holds the current report fields object */
	this.activeReportFields = {};	
	/* Mode flaq for New or Update */
	this.mode = "new";
	/* Report archive grid container */
	this.reportGridView = $("#leca-report_designer-archive-report-grid");
	/* Report type archive grid container */
	this.reportTypeGridView = $("#leca-report-type-list");
	/* Used to set the method select box value - when in the Widget Edit mode */
	this.setMethodPending = false;
	
	/* Characters per Inch */
	this.cpi = 12;
	/* Lines per Inch */
	this.lpi = 6;
	/* Report Name */
	this.name = "Un-named Report";
	/* Paper size type */
	this.pageType = "A4";
	/* Report format */
	this.format = "text";
	/* Printer type */
	this.printerType = "dmp";
	/* Page layout width (inch) */
	this.pageWidth = 8;
	/* Page layout height (inch) */
	this.pageHeight = 11;
	/* Designer pad object */
	this.dpad = $("#leca-rd-designer-design-pad");	
	/* Preview pad object */
	this.preview = $("#leca-rd-designer-preview-iframe");
	/* Line mapping reference */
	this.lines = [];
	/* Holds the header line mappings */
	this.headerLines = [];
	/* Holds the footer line mappings */
	this.footerLines = [];
	/* Holds the list droped fields */
	this.fields = {};
	/* Hold the list of header child fields uids */
	this.headerFields = [];
	/* Hold the list of footer child fields uids */
	this.footerFields = [];
	/**/
	this.updatedFields = [];
	/**/
	this.removedFields = [];
	/* Top margin of designer pad */
	this.marginTop = 1;
	/* Right margin of designer pad */
	this.marginRight = 1;
	/* Bottom margin of designer pad */
	this.marginBottom = 1;
	/* Left margin of designer pad */
	this.marginLeft = 1;
	/* Total number of lines per page - LPP */
	this.totalRows = 0;
	/* Total number of characters per line - CPL */
	this.totalColumns = 0;
	/* Current grid item's row index */
	this.currentRow = 0;
	/* Current grid item's column index */
	this.currentColumn = 0;
	/* Current cursor type */
	this.cursor = "";
	/* Used to holds the start X position for mouse event */
	this.startPosX = 0;
	/* Used to holds the start Y position for mouse event */
	this.startPosY = 0;
	/* Width of the single Grid Item */
	this.gItemWidth = 0;
	/* Height of the single Grid Item */
	this.gItemHeight = 0;
	/* Active item used for move and resize */
	this.currentItem = null;
	/* UID of the selected widget on the designer pad */
	this.currentUid = null
	/* Used to holds the selected DataGrid's row  */
	this.selectedRow = null;
	/*  */
	this.selectedColumn = null;
	/* Flag for mouse down mark */
	this.isMouseDown = false;
	/* Flag for resize mode */
	this.isResizeMode = false;
	
	/* could be 'dpad', 'page_header' or 'page_footer' */
	this.dropZone = "dpad";
	
	/* Ref object of the item that is being dragged */
	this.draggedWidget = null;
	/**/
	this.cwTopRatio = 0;
	/**/
	this.cwLeftRatio = 0;
	/**/
	this.cwRightRatio = 0;
	/**/
	this.cwBottomRatio = 0;
	/**/
	this.contextMenu = null;
	/**/
	this.selector = $("#leca-rd-selector-bar");
	/**/
	this.highlighter = $("#leca-rd-highlight-bar");
	/**/
	this.styleHead = $("#leca-rd-css-overrides");
	/**/
	this.rawWidgetList = {	
		chart: "Chart",
		image: "Image",		
		separator: "Separator",
		sub_report: "Sub Report",
		page_header: "Page Header",
		page_footer: "Page Footer",		
		page_number: "Page Number",
		static_text: "Static Text",
		dynamic_text: "Dynamic Text",
		record_table: "Record Table"		
	};
	/**/
	this.pageSizes = {
		A0 : {width: 33, height: 46},
		A1 : {width: 23, height: 33},
		A2 : {width: 16, height: 23},
		A3 : {width: 11, height: 16},
		A4 : {width: 8, height: 11},
		A5 : {width: 5, height: 8},
		A6 : {width: 4, height: 5},
		A7 : {width: 3, height: 4},				
		letter : {width: 8, height: 11},
		legal : {width: 8, height: 14},
		ledger : {width: 11, height: 17}
	};
	
	/**
	 * Bootstrap method for this context
	 */
	this.init = function() {
	
		/* Register all events */
		this.registerEvents();	
		/* Load user list */
		this.controller.request = this.controller.prepareRequest("/visualizer/rf/list", this.controller.context, "USER_LIST", 0, {});
		/* Initiate the request */
		this.controller.dock();
	};
	
	/**
	 * Called whenever user click the menu that belongs to this module
	 */
	this.menuClicked = function() {
		this.doArchive();
	};
	
	/**
	 * You have to use jQuery 'on' method for registering custom events
	 * Before registering use 'off' to cancel the same event which might previously registered
	 */
	this.registerEvents = function() {
		
		$(document).on("change", "#leca-report_designer-single-bean-select, #leca-report_designer-create-bean-select", this, function(e) {
			if ($(this).val() !== "")  {
				/* Prepare the request object */
				e.data.controller.request = e.data.controller.prepareRequest("/visualizer/rf/list", e.data.controller.context, "METHOD", 0, {CLASS: $(this).val()});
				/* Initiate the request */
				e.data.controller.dock();
			} else {
				$("#leca-report_designer-"+ e.data.viewMode +"-method-select").html("");
			}
		});
		
		$(document).on("click", "button.leca-report-type-save-action", this, function(e) {
			e.data.publishReportType(false);
		});
		
		$(document).on("click", "button.leca-report-type-update-action", this, function(e) {
			e.data.publishReportType(true);
		});
		
		$(document).on("click", "button.leca-report-type-delete-action", this, function(e) {
			/* Prepare the request object */
			e.data.controller.request = e.data.controller.prepareRequest("/visualizer/rf/delete", e.data.controller.context, "REPORT_TYPES", 0, {
				REPORT_TYPE_ID: e.data.activeReportType["REPORT_TYPE_ID"],
				REPORT_TYPE_NAME: e.data.activeReportType["REPORT_TYPE_NAME"]
			});
			/* Initiate the request */
			e.data.controller.dock();
		});		
		
		$(document).on("click", "button.leca-report-type-clear-action", this, function(e) {
			e.data.clearReportSection();	
		});
		
		$(document).on("click", "button.leca-update-template-action", this, function(e) {
			e.data.updateTemplate();
		});
		
		$(document).on("click", "button.leca-report-template-model-save-btn", this, function(e) {
			e.data.publishTemplate();
		});
		
		$(document).on("click", "button.leca-report-template-model-clone-btn", this, function(e) {
			
		});
		
		$(document).on("click", "button.leca-clone-template-action", this, function(e) {
			e.data.createCloneTemplatePopup();
		});		
		
		$(document).on("change", "select.leca-report-template-selector", this, function(e) {
			e.data.onTemplateSelectorChange($(this));
		});
		
		$(document).on("click", "button.leca-cancel-template-action", this, function(e) {
			/* Reset the template properties */						
			e.data.activeReportLines = {};			
			e.data.activeReportFields = {};
			e.data.activeReportTemplate = {};
			
			e.data.fields = {};
			e.data.lines = [];	
			e.data.headerLines = [];
			e.data.footerLines = [];
			e.data.headerFields = [];
			e.data.footerFields = [];			
			e.data.currentUid = null;
			/* Switch views */			
			$("#leca-workspace-report_designer-designer").hide();
			$("#leca-workspace-report_designer-single").show();			
				
			/* Also update action bar */
			e.data.controller.topActionBtnHolder.html("");		
			if (!$.isEmptyObject(lecaSystemConfig[e.data.controller.context][e.data.controller.viewMode].actions)) {
				let actions = lecaSystemConfig[e.data.controller.context][e.data.controller.viewMode].actions;
				for (let i = 0; i < actions.length; i++) {					
					btnClass = "leca-btn leca-action-btn "+ actions[i].state +" leca-"+ actions[i].action +"-action";					
					if (actions[i].icon != "") {
						icon = '<i class="material-icons">'+ actions[i].icon +'</i>&nbsp;';
					}
					e.data.controller.topActionBtnHolder.append($('<button class="'+ btnClass +'">'+ icon + actions[i].label +'</button>'));
				}			
			}	
			/* Update the breadcrumb */					
			e.data.controller.breadCrumb.html(e.data.controller.currentMenu.html() +"&nbsp;/&nbsp;<span>"+ e.data.activeRecord["REPORT_NAME"] +"</span>");
		});
		
		$(document).on("click", this, function(e) {
			if(e.data.contextMenu) {
				e.data.contextMenu.remove();
				e.data.contextMenu = null;
			}
		});
		
		
		$(document).on("click", "button.leca-designer-action", this, function(e) {
			e.data.prepareReportDesignerView();
		});
		
		/**
		 * Prevent default click behavior of A tag
		 */
		$(document).on("click", "a", function(e) {
			e.preventDefault();
		});
		
		/* Disable browsers default context menu event */
		document.oncontextmenu = function() {
			return false;
		};
		
		/**
		 * Global 'keydown' listener for catching ESC and NAV keys
		 */
		$(document).on("keydown", this, function(e) {
			let code = e.keyCode || e.which;
		    if (code == 27) {
		    	e.data.clearState();   	
		    }
		    //e.data.navigateWidgetSelector(code);		    
		});		
		
		/**
		 * Ikea RD Accordian Headedr's click event listener
		 */
		$(document).on("click", "div.leca-accordian-header", function() {
			$(this).toggleClass("active").next().toggle("fast");
			$(this).find("i.arrow").toggleClass("fa-chevron-down fa-chevron-up");
		});
		
		/**
		 * Ikea RD Unit Field Selector's click event listener
		 */
		$(document).on("click", "div.leca-rd-unit-field.selector > label", this, function(e) {
			if (!$(this).parent().hasClass("multi")) {
				$(this).addClass("selected").siblings().removeClass();				
			} else {				
				$(this).toggleClass("selected");
				if ($(this).is(":first-child") && $(this).hasClass("selected")) {
					$(this).siblings().removeClass();
				} else {
					$(this).siblings(":first").removeClass();
				}
			}			
			if (e.data.currentUid) {
				e.data.handleSelectorChange($(this));
			}				
		});
		
		$(document).on("click", "div.leca-rd-unit-field.selector > label > input", this, function(e) {
			e.stopPropagation();
		});
		
		/**
		 * dragstart event listener for Widgets
		 */
		$(document).on("dragstart", "#leca-rd-widget-list > a", this, function(e) {
			e.data.draggedWidget = $(this);			
			$("div.page-header-widgets-holder").css("background", "#eee");
			$("div.page-footer-widgets-holder").css("background", "#eee");
		});

		/**
		 * dragend event listener for Widgets
		 */
		$(document).on("dragend", "#leca-rd-widget-list > a", this, function(e) {			
			$("div.page-header-widgets-holder").css("background", "#fff");
			$("div.page-footer-widgets-holder").css("background", "#fff");
		});

		/**
		 * dragenter, dragover, dragleave events listener for Designer Pad
		 */
		$(document).on("dragenter dragover dragleave", "#leca-rd-designer-design-pad, div.page-header-widgets-holder, div.page-footer-widgets-holder", this, function(e) {
			e.preventDefault();  
		    e.stopPropagation();
		});

		/**
		 * drop event listener for Designer Pad
		 */
		$(document).on("drop dragdrop", "#leca-rd-designer-design-pad, div.page-header-widgets-holder, div.page-footer-widgets-holder", this, function(e) {
			
			e.preventDefault();	    
			e.stopPropagation();
			
			let offset = 0,
				target = $(e.target);
			
			if (e.data.draggedWidget) {				
				
				if (target.hasClass("page-header-widgets-holder") || target.hasClass("page-footer-widgets-holder")) {
					e.data.dropZone = target;	
					offset = target.offset();
				} else {
					e.data.dropZone = e.data.dpad;
					offset = target.offset();
				}
								
				/* Calculate the current grid lines */
				e.data.currentRow = Math.ceil((e.originalEvent.clientY - offset.top) / e.data.gItemHeight);
				e.data.currentColumn = Math.ceil((e.originalEvent.clientX - offset.left) / e.data.gItemWidth);
				e.data.dropWidget(e.data.draggedWidget.attr("data-type"));
				e.data.draggedWidget = null;
				
			}
			
		});		
		
		/**
		 * mousedown event listener for Widgets on the Designer Pad
		 */
		$(document).on("mousedown", "div.leca-rd-widget", this, function(e) {
			if (!$(this).hasClass("edit")) {
				e.data.isMouseDown = true;
				e.data.currentItem = $(this);
				e.data.startPosX = e.offsetX;
				e.data.startPosY = e.offsetY;				
				e.data.currentItem.addClass("current-item");				
				
				e.data.currentRow = Math.ceil((e.data.currentItem.position().top + e.offsetY) / e.data.gItemHeight);
				e.data.currentColumn = Math.ceil((e.data.currentItem.position().left + e.offsetX) / e.data.gItemWidth);
				
				e.data.cwTopRatio = e.data.currentRow - (parseInt(e.data.currentItem.css("grid-row-start"), 10));
				e.data.cwLeftRatio = e.data.currentColumn - (parseInt(e.data.currentItem.css("grid-column-start"), 10));
				e.data.cwRightRatio = (parseInt(e.data.currentItem.css("grid-column-end"), 10)) - e.data.currentColumn;
				e.data.cwBottomRatio = (parseInt(e.data.currentItem.css("grid-row-end"), 10)) - e.data.currentRow;
							
				if (e.data.cursor == "left" || e.data.cursor == "right" || e.data.cursor == "top" || e.data.cursor == "bottom") {
					e.data.isResizeMode = true;		
				}
			}			
		});

		/**
		 * Global mouseup event listener for house keeping works
		 */
		$(document).on("mouseup", this, function(e) {
			e.data.cursor = "";
			e.data.startPosX = 0;
			e.data.startPosY = 0;
			e.data.dpad.removeClass("panning");
			if (e.data.currentItem) {
				e.data.currentItem.removeClass("current-item");
			}			
			e.data.currentItem = null;
			e.data.isMouseDown = false;
			e.data.isResizeMode = false;			
			document.body.style.cursor = "auto";
			$("div.leca-rd-widget.page_number.edit").removeClass("resize");
			$("div.leca-rd-widget.dynamic_text.edit").removeClass("resize");
			
			$("div.leca-rd-widget.record_table div.drow > div.dcolumn").removeClass("mute");
			$("div.leca-rd-widget.record_table div.drow > span.resize-handle").removeClass("drag");
			$("div.leca-rd-widget.record_table div.drow").off("mousemove");
			
			/* Reposition the resize handler */			
			e.data.reOrderColumns();					
		});

		/**
		 * mousemove event listener for Designer Pad
		 */
		$(document).on("mousemove", "#leca-rd-designer-design-pad", this, function(e) {
			e.data.handleMouseMove(e);						
		});
		
		/**
		 * mousemove event listener for Designer Pad
		 */
		$(document).on("mousemove", "div.page-header-widgets-holder, div.page-footer-widgets-holder", this, function(e) {
			e.data.handleMouseMove(e);						
		});

		/**
		 * change event listener for Report Setup's select tags
		 */
		$(document).on("change", "select.leca-rd-report-setup-field", this, function(e) {
			e.data.handleSetupSelectChange($(this));
		});
		
		$(document).on("change", "select.leca-report-template-model-field", this, function(e) {
			e.data.handleTemplateModelSelectChange($(this));
		});

		/**
		 * keyup event listener for Widget Property's input tags
		 */		
		$(document).on("keyup", "input.leca-rd-unit-field", this, function(e) {
			if (e.data.currentUid) {
				e.data.handlePropertyInputChange($(this));
			}			
		});
		
		$(document).on("change", "input.leca-rd-unit-field", this, function(e) {
			if (e.data.currentUid) {
				e.data.handlePropertyInputChange($(this));
			}			
		});
		
		$(document).on("change", "select.leca-rd-unit-field", this, function(e) {
			if (e.data.currentUid) {
				e.data.handlePropertySelectChange($(this));
			}
		});

		/**
		 * mouseover event listener for Widgets on the Designer Pad
		 */
		$(document).on("mouseover", "div.leca-rd-widget", this, function(e) {
			e.data.highlightWidget($(this));
		});
		
		/**
		 * mouseout event listener for Widgets on the Designer Pad
		 */
		$(document).on("mouseout", "div.leca-rd-widget", this, function(e) {
			e.data.highlighter.hide();
		});
		
		/**
		 * click event listener for Widgets on the Designer Pad
		 */
		$(document).on("click", "div.leca-rd-widget", this, function(e) {
			e.data.selectWidget($(this));
			e.data.loadWidgetProperties($(this));
		});
		
		/**
		 * click event listener for Widgets on the Designer Pad
		 */
		$(document).on("click", "div.page_header.edit div.leca-rd-widget, div.page_footer.edit div.leca-rd-widget", this, function(e) {
			e.data.selectWidget($(this));
			e.data.loadWidgetProperties($(this));
			e.stopPropagation();
		});
		
		/**
		 * click event listener for Edit Button - Quick Action Bar 
		 */
		$(document).on("click", "#leca-rd-widget-edit-btn", this, function(e) {
			if (e.data.currentUid) {
				if ($(this).hasClass("active")) {
					$(this).removeClass("active");
					e.data.selector.removeClass("edit");
					e.data.fields[e.data.currentUid].target.removeClass("edit");
				} else {
					$(this).addClass("active");
					e.data.selector.addClass("edit");
					e.data.fields[e.data.currentUid].target.addClass("edit");
				}				
				if (e.data.fields[e.data.currentUid].FIELD_TYPE == "record_table") {
					e.data.selectedColumn = null;
					e.data.fields[e.data.currentUid].target.find("div.dcolumn.selected").removeClass("selected");
					e.data.fields[e.data.currentUid].target.trigger("click");
				}
			}
		});
		
		/**
		 * click event listener for Clone Button - Quick Action Bar 
		 */
		$(document).on("click", "#leca-rd-widget-clone-btn", this, function(e) {
			e.data.cloneWidget();
		});
		
		/**
		 * click event listener for Delete Button - Quick Action Bar 
		 */
		$(document).on("click", "#leca-rd-widget-delete-btn", this, function(e) {
			/* Makesure Ajax handler is free */
			if (e.data.controller.ajaxFlaQ) {
				e.data.removeWidget();	
			} else {
				e.data.controller.flashAlert("Please wait while finishing current request.!");
			}			
		});
				
		/**
		 * mousedown event listener for Dynamic Text resize feature
		 */
		$(document).on("mousedown", "div.leca-rd-widget.page_number.edit > div.delimiter", this, function(e) {
			e.data.isMouseDown = true;
			e.data.startPosX = $(this).position().top + e.pageX;
			e.data.startPosY = $(this).position().left + e.pageY;			
			$(this).parent().addClass("resize");
			e.stopPropagation();
		});
		
		/**
		 * mousemove event listener for Dynamic Text resize feature
		 */
		$(document).on("mousemove", "div.leca-rd-widget.page_number.edit", this, function(e) {
			if (e.data.isMouseDown) {
				e.data.handleDelimiterResize(e); 
			}		
			e.stopPropagation();
		});
		
		/**
		 * mousedown handler registration for DPAD
		 */
		$(document).on("mousedown", "#leca-rd-designer-design-pad", this, function(e) {		
			if (e.button == 2) { 
				let offset = $(e.target).offset();
				/* Calculate the current grid lines */
				e.data.currentRow = Math.ceil((e.originalEvent.clientY - offset.top) / e.data.gItemHeight);
				e.data.currentColumn = Math.ceil((e.originalEvent.clientX - offset.left) / e.data.gItemWidth);
				e.data.handleRightClick(e, $(this), "dpad");
			}
		});
		
		/**
		 * mousedown handler registration for Page Header Widget
		 */
		$(document).on("mousedown", "div.page_header.edit > div",  this, function(e) {
			if (e.button == 2) {
				e.data.dropZone = $(this);					
				e.data.handleRightClick(e, $(this), "page_header");
				e.stopPropagation();
			}
		});
		
		/**
		 * mousedown handler registration for Page Footer Widget
		 */
		$(document).on("mousedown", "div.page_footer.edit > div",  this, function(e) {
			if (e.button == 2) {
				e.data.dropZone = $(this);				
				e.data.handleRightClick(e, $(this), "page_footer");
				e.stopPropagation();
			}
		});
		
		$(document).on("mousedown", "div.leca-rd-widget",  this, function(_e) {
			if (_e.button == 2) {		
				_e.stopPropagation();
			}			
		});
		
		/**
		 * mousedown handler registration for DataGrid Row
		 */
		$(document).on("mousedown", "div.leca-rd-widget.record_table.edit div.drow",  this, function(e) {
			if (e.button == 2) { 
				e.data.selectedRow = $(this);
				e.data.handleRightClick(e, $(this), "record_table_row");
				e.stopPropagation();
			}
		});
		
		/**
		 * mousedown handler registration for DataGrid Column
		 */
		$(document).on("mousedown", "div.leca-rd-widget.record_table.edit div.drow > div",  this, function(e) {
			if (e.button == 2) { 
				e.data.selectedColumn = $(this);
				e.data.selectedRow = $(this).parent();
				e.data.handleRightClick(e, $(this), "record_table_column");
				e.stopPropagation();
			}
		});	
		
		/**
		 * mousedown handler registration for Page Footer Widget
		 */
		$(document).on("mousedown", "div.page_footer",  this, function(e) {
			if (e.button == 2) {				
				e.data.handleRightClick(e, $(this), "page_footer");
				e.stopPropagation();
			}
		});
		
		$(document).on("click", "div.leca-rd-widget.page_header.edit div.leca-rd-widget",  this, function(e) {
			e.stopPropagation();
		});
		
		$(document).on("click", "div.leca-rd-widget.record_table.edit div.header",  this, function(e) {			
			e.data.selectedColumn = null;
			$(this).find("div.dcolumn.selected").removeClass("selected");
			$("#leca-rd-widget-properties-section-label").html("Data Grid");
		});
		
		$(document).on("click", "#leca-rd-context-menu-box > a", this, function(e) {
			e.data.handleContextMenu($(this));
			e.preventDefault();
			e.stopPropagation();
		});
		
		$(document).on("click", "div.leca-rd-widget.record_table div.drow > div.dcolumn", this, function(e) {
			if ($(this).hasClass("selected")) {
				$(this).removeClass("selected");
				e.data.selectedColumn = null;
			} else {
				$(this).closest(".leca-rd-widget").find("div.dcolumn").removeClass("selected");
				$(this).addClass("selected");
				e.data.selectedColumn = $(this);
				e.data.loadRecordTableColumnProperties($(this).attr("data-uid"));
				e.stopPropagation();
			}						
		});
		
		$(document).on("mousedown", "div.leca-rd-widget.record_table div.drow > span.resize-handle", this, function(e) {
			let me = e.data,
				dr = $(this),
				height = dr.outerHeight(),
				width = dr.outerWidth(),
				max_left = dr.parent().offset().left + dr.parent().width() - dr.width(),
				max_top = dr.parent().offset().top + dr.parent().height() - dr.height(),
				min_left = dr.parent().offset().left,
				min_top = dr.parent().offset().top;
			
			let ypos = dr.offset().top + height - e.pageY,
				xpos = dr.offset().left + width - e.pageX;
			
			let oldLeft = dr.css("left");
			oldLeft = parseInt(oldLeft.substring(0, (oldLeft.length - 2)), 10);
			dr.addClass("drag");
			
			$($(this).parent()).on('mousemove', function(e) {
				//let itop = e.pageY + ypos - height;
				let nWidth = 0, 
					pWidth = 0, 
					ileft = e.pageX + xpos - width;			
				if(dr.hasClass("drag")){
					if(ileft <= min_left ) { ileft = min_left; }
					if(ileft >= max_left ) { ileft = max_left; }
					dr.offset({left: ileft});
				}
				/**/
				let currentLeft = dr.css("left");
				currentLeft = parseInt(currentLeft.substring(0, (currentLeft.length - 2)), 10);
				if (oldLeft > currentLeft) {
					/* Moving left */
					if(Math.abs(oldLeft - currentLeft) >= me.gItemWidth) {
						if (dr.prev().outerWidth() < me.gItemWidth) {
							dr.prev().outerWidth(me.gItemWidth);
							return;
						}
						oldLeft = currentLeft;
						if (dr.next().length > 0) {
							dr.prev().outerWidth(parseFloat(dr.prev().outerWidth() - me.gItemWidth));
							dr.next().outerWidth(parseFloat(dr.next().outerWidth() + me.gItemWidth));
							/* Update the width property value */
							pWidth = lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.prev().attr("data-uid")].width;
							nWidth = lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.next().attr("data-uid")].width;
							lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.prev().attr("data-uid")].width = (pWidth - 1);
							lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.next().attr("data-uid")].width = (nWidth + 1);
						} else {
							dr.prev().outerWidth(parseFloat(dr.prev().outerWidth() - me.gItemWidth));
							pWidth = lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.prev().attr("data-uid")].width;
							lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.prev().attr("data-uid")].width = (pWidth - 1);
						}
					}
				} else {
					/* Moving right */
					if(Math.abs(oldLeft - currentLeft) >= me.gItemWidth) {
						if (dr.next().outerWidth() < me.gItemWidth) {
							dr.next().outerWidth(me.gItemWidth);
							return;
						}
						oldLeft = currentLeft;
						if (dr.next().length > 0) {
							dr.prev().outerWidth(parseFloat(dr.prev().outerWidth() + me.gItemWidth));
							dr.next().outerWidth(parseFloat(dr.next().outerWidth() - me.gItemWidth));
							/* Update the width property value */
							pWidth = lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.prev().attr("data-uid")].width;
							nWidth = lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.next().attr("data-uid")].width;
							lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.prev().attr("data-uid")].width = (pWidth + 1);
							lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.next().attr("data-uid")].width = (nWidth - 1);
						} else {
							dr.prev().outerWidth(parseFloat(dr.prev().outerWidth() + me.gItemWidth));
							pWidth = lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.prev().attr("data-uid")].width;
							lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].columns[dr.prev().attr("data-uid")].width = (pWidth + 1);
						}
					}
				}
			});			
			$(this).siblings(".dcolumn").addClass("mute");
		});
		
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
		
		if (_tab.parent().attr("data-tab") === "report-tab") {
			if (_tab.attr("data-target") === "preview") {
				this.refreshPreview();
			} else if (_tab.attr("data-target") === "design") {
				this.reloadReport();
			} else if (_tab.attr("data-target") === "data") {
				this.showDataBucketStructure();
			}		
		} 
		
	};	
	
	this.handleGridRecordAction = function(_target) {
			
	};
	
	this.prepareReportDesignerView = function() {
		/* Right in report context only template load has been mapped for grid action
		 * So no worry about checking */
		$("#leca-workspace-report_designer-single").hide();
		$("#leca-workspace-report_designer-designer").show();
		
		/* Reset the report designer tab */
		//$("div.leca-rd-designer-pad-header > a:first-child").trigger("click");
				
		if (!$.isEmptyObject(this.activeRecord)) {
			this.controller.topActionBtnHolder.html("");	
			this.controller.topActionBtnHolder.append($('<select class="leca-report-template-selector"></select>'));
			this.controller.topActionBtnHolder.append($('<button class="leca-btn leca-action-btn primary leca-clone-template-action"><i class="material-icons">content_copy</i>&nbsp;Clone</button>'));
			this.controller.topActionBtnHolder.append($('<button class="leca-btn leca-action-btn primary leca-update-template-action"><i class="material-icons">save</i>&nbsp;Update</button>'));
			this.controller.topActionBtnHolder.append($('<button class="leca-btn leca-action-btn warning leca-delete-template-action"><i class="material-icons">delete</i>&nbsp;Delete</button>'));
			this.controller.topActionBtnHolder.append($('<button class="leca-btn leca-action-btn secondary leca-cancel-template-action"><i class="material-icons">close</i>&nbsp;Cancel</button>'));
			/* Load templates */
			this.controller.request = this.controller.prepareRequest("/visualizer/rf/list", this.controller.context, "REPORT_TEMPLATES", 0, {REPORT_ID: parseInt(this.activeRecord.REPORT_ID, 10)});		
			this.controller.dock();
			/* Well init the designer view */
			this.initDesigner();
		}	
	};
	
	this.refreshPreview = function() {
		
		this.preview.height($(window).height() - 140);
		this.preview.attr("src", lecaOpt.docker +"/visualizer/rf/compose?TEMPLATE_ID="+ this.activeReportTemplate.TEMPLATE_ID +"&REPORT_ID="+ this.activeRecord.REPORT_ID +"&FORMAT=PDF&TYPE=STREAM");
		
	};
	
	this.showDataBucketStructure = function() {
		
		let dgKey,
			cKeys,
			rowObj,
			dStructure = {},
			keys = Object.keys(this.fields);
		for (let i = 0; i < keys.length; i++) {
			if (this.fields[keys[i]].FIELD_TYPE == "record_table") {
				dgKey = this.fields[keys[i]]["OPTIONS"]["handle"]; 
				dStructure[dgKey] = [];
				//dStructure[dgKey]["records"] = [];				
				cKeys = Object.keys(this.fields[keys[i]]["OPTIONS"]["columns"]);
				rowObj = {};
				for (let j = 0; j < cKeys.length; j++) {
					rowObj[this.fields[keys[i]]["OPTIONS"]["columns"][cKeys[j]].handle] = "";
				}
				dStructure[dgKey].push(rowObj);				
				//dStructure[dgKey]["footer"] = {};
				//cKeys = Object.keys(this.fields[keys[i]]["OPTIONS"]["footer"]);
			} else if (this.fields[keys[i]].FIELD_TYPE == "sub_report") {
				dStructure[this.fields[keys[i]]["OPTIONS"]["handle"]] = {};
			} else {
				if (this.fields[keys[i]]["OPTIONS"]["handle"] && this.fields[keys[i]]["OPTIONS"]["handle"] != "") {
					dStructure[this.fields[keys[i]]["OPTIONS"]["handle"]] = "";
				}
			}
		}
		$("#leca-rd-designer-data-blue-print").html(JSON.stringify(dStructure, null, '  '));	 
		
	};
	
	this.loadTemplates = function(_payload) {
		
		let tSelector = this.controller.topActionBtnHolder.find("select");
		if (_payload.length > 0) {
			for (let i = 0; i < _payload.length; i++) {
				tSelector.append($('<option value="'+ _payload[i]["TEMPLATE_ID"] +'">'+ _payload[i]["TEMPLATE_NAME"] +'</option>'));
			}
		} else {
			/* Hide the top action buttons */
			this.controller.topActionBtnHolder.find("button").hide();
			this.controller.topActionBtnHolder.find("button.leca-cancel-template-action").show();
		}				
		tSelector.append($('<option value="*new">New Template *</option>'));
		tSelector.prop('selectedIndex',0);
		tSelector.trigger("change");
		
	};
	
	this.loadTemplate = function() {
		
		/* Add remove button if it is not Factory Template */
		if (!this.activeReportTemplate.IS_FACTORY_TEMPLATE) {
			this.controller.topActionBtnHolder.find("button.leca-delete-template-action").show();
		} else {
			this.controller.topActionBtnHolder.find("button.leca-delete-template-action").hide();
		}
		this.controller.topActionBtnHolder.find("button.leca-clone-template-action").show();
		this.controller.topActionBtnHolder.find("button.leca-update-template-action").show();
		
		let me = this,
			options = JSON.parse(this.activeReportTemplate.OPTIONS);
			
		/* Load template setup properties */
		$(".leca-rd-report-setup-field").each(function() {
			if ($(this).attr("data-key") != "name") {
				$(this).val(options[$(this).attr("data-key")]);
			} else {
				$(this).val(me.activeReportTemplate.TEMPLATE_NAME);
			}					
		});
		
		this.reloadReport();
		
	};
	
	this.createNewTemplatePopup = function() {
		
		let html = '<div class="leca-model-dialog leca-model-ghost-back">';		
		html += '<div class="leca-report-template-create-model" data-model="template-create-nodel">';
		
		html += '<div class="leca-report-template-model-header"><h1>New Template</h1><a href="#" class="leca-model-close-btn"><i class="material-icons">close</i></a></div>';		
		html += '<div class="leca-report-template-model-content">';
				
		html += '<div class="leca-report-template-model-grid"><div>';
		
		html += '<div class="form-row"><label>Name</label><input type="text" class="leca-report-template-model-field" data-key="name" data-type="text"></div>';
		
		html += '<div class="form-row" style="display: none;"><label>Title Page</label>';												
		html += '<select class="leca-report-template-model-field" data-key="title_page" data-type="select">';
		html += '<option value="none">No Title</option>';
		html += '<option value="first">First Page</option>';
		html += '<option value="separate">Separate Page</option>';
		html += '</select></div>';
		
		html += '<div class="form-row" style="display: none;"><label>Title</label>';
		html += '<input type="text" class="leca-report-template-model-field" data-key="title" data-type="text"></div>';
			
		html += '<div class="form-row" style="display: none;"><label>Sub Title</label>';
		html += '<input type="text" class="leca-report-template-model-field" data-key="subtitle" data-type="text"></div>';
			
		html += '<div class="form-row"><label>Format</label>';												
		html += '<select class="leca-report-template-model-field" data-key="format" data-type="select">';
		html += '<option value="text">Text</option>';
		html += '<option value="media">Multi Media</option>';
		html += '</select></div>';
		
		html += '<div class="form-row"><label>Printer</label>';												
		html += '<select class="leca-report-template-model-field" data-key="printer_type" data-type="select">';
		html += '<option value="dmp">Dot Matrix</option>';
		html += '<option value="other">Ink or Laser</option>';
		html += '</select></div>';
		
		html += '<div class="form-row leca-rd-cpi-form-row" style="display: none;"><label>Characters Per Inch</label>';													
		html += '<select class="leca-report-template-model-field" data-key="printer_cpi" data-type="select">';
		html += '<option value="10">10 CPI</option>';
		html += '<option value="12" selected>12 CPI</option>';
		html += '<option value="15">15 CPI</option>';		
		html += '</select></div>';
				
		html += '<div class="form-row leca-rd-lpi-form-row" style="display: none;"><label>Lines Per Inch</label>';	
		html += '<select class="leca-report-template-model-field" data-key="printer_lpi" data-type="select">';
		html += '<option value="6">6 Lines</option>';												
		html += '<option value="8">8 Lines</option>';
		html += '<option value="7/72">7/72 Lines</option>';
		html += '<option value="n/72">n/72 Lines</option>';
		html += '<option value="n/216">n/216 Lines</option>';
		html += '</select>';
		html += '<input type="number" data-key="lpi-divisor" placeholder="nth divisor" ></div>';
		
		html += '<div class="form-row" style="display: none;">';
		html += '<label>Margin Left</label><input type="number" class="leca-report-template-model-field" value="0" data-key="margin_left" data-type="number">';
		html += '</div>';
		
		html += '<div class="form-row" style="display: none;">';
		html += '<label>Margin Right</label><input type="number" class="leca-report-template-model-field" value="0" data-key="margin_right" data-type="number">';
		html += '</div>';
		
		html += '<div class="form-row" style="display: none;">';
		html += '<label>Padding Left</label><input type="number" class="leca-report-template-model-field" value="0" data-key="padding_left" data-type="number">';
		html += '</div>';
		
		html += '<div class="form-row" style="display: none;">';
		html += '<label>Padding Right</label><input type="number" class="leca-report-template-model-field" value="0" data-key="padding_right" data-type="number">';
		html += '</div>';
		
		html += '<div class="form-row" style="display: none;">';
		html += '<select class="leca-report-template-model-field" data-key="index_mode" data-type="select">';
		html += '<option value="continuous">Continuous</option>';
		html += '<option value="collection">Landscape</option>';
		html += '</select>';
		html += '</div>';
		
		html += '</div><div>';
		
		html += '<div class="form-row"><label>Font Family</label>';													
		html += '<select class="leca-report-template-model-field" data-key="font" data-type="select">';		
		html += '<option value="courier">Courier</option>';	
		html += '<option value="courier-bold">Courier Bold</option>';	
		html += '<option value="courier-italic">Courier Italic</option>';	
		html += '<option value="courier-bold-italic">Courier Bold Oblique</option>';																		
		html += '<option value="helvetica">Helvetica</option>';	
		html += '<option value="helvetica-bold">Helvetica Bold</option>';	
		html += '<option value="helvetica-italic">Helvetica Italic</option>';	
		html += '<option value="helvetica-bold-italic">Helvetica Bold Oblique</option>';																		
		html += '<option value="roman">Roman</option>';	
		html += '<option value="roman-bold">Roman Bold</option>';	
		html += '<option value="roman-italic">Roman Italic</option>';	
		html += '<option value="roman-bold-italic">Roman Bold Oblique</option>';																		
		html += '<option value="symbol">Symbol</option>';	
		html += '<option value="zapfdingbats">Zapfdingbats</option>';											
		html += '</select></div>';
	
		html += '<div class="form-row"><label>Font Size</label>';
		html += '<input type="text" class="leca-report-template-model-field" data-key="font_size" data-type="number" value="9" /></div>';
		
		html += '<div class="form-row" style="display: none;"><label>Char Spacing</label>';
		html += '<input type="text" class="leca-report-template-model-field" data-key="char_space" data-type="number" value="1" /></div>';
	
		html += '<div class="form-row"><label>Paper</label>';													
		html += '<select class="leca-report-template-model-field" data-key="paper" data-type="select">';
		html += '<option value="A0">A0</option>';
		html += '<option value="A1">A1</option>';
		html += '<option value="A2">A2</option>';
		html += '<option value="A3">A3</option>';
		html += '<option value="A4" selected>A4</option>';
		html += '<option value="A5">A5</option>';
		html += '<option value="A6">A6</option>';
		html += '<option value="A7">A7</option>';
		html += '<option value="A8">A8</option>';
		html += '<option value="A9">A9</option>';
		html += '<option value="A10">A10</option>';
		html += '<option value="Legal">Legal</option>';
		html += '<option value="Letter">Letter</option>';
		html += '<option value="Ledger">Ledger</option>';
		html += '</select></div>';
	
		html += '<div class="form-row" style="display: none;"><label>Orientation</label>';													
		html += '<select class="leca-report-template-model-field" data-key="orientation" data-type="select">';
		html += '<option value="portrait">Portrait</option>';
		html += '<option value="landscape">Landscape</option>';
		html += '</select></div>';
				
		html += '<div class="form-row leca-rd-margin-top-form-row" style="display: none;">';
		html += '<label>Margin Top</label><input type="number" class="leca-report-template-model-field" value="0" data-key="margin_top" data-type="number">';
		html += '</div>';
		
		html += '<div class="form-row leca-rd-margin-bottom-form-row" style="display: none;">';
		html += '<label>Margin Bottom</label><input type="number" class="leca-report-template-model-field" value="0" data-key="margin_bottom" data-type="number">';
		html += '</div>';	
				
		html += '<div class="form-row leca-rd-margin-top-form-row" style="display: none;">';
		html += '<label>Padding Top</label><input type="number" class="leca-report-template-model-field" value="0" data-key="padding_top" data-type="number">';
		html += '</div>';
		
		html += '<div class="form-row leca-rd-margin-bottom-form-row" style="display: none;">';
		html += '<label>Padding Bottom</label><input type="number" class="leca-report-template-model-field" value="0" data-key="padding_bottom" data-type="number">';
		html += '</div>';
		
		html += '</div></div>';
		html += '</div>';
		
		html += '<div class="leca-report-template-model-footer">';
		html += '<button class="leca-report-template-model-save-btn"><i class="material-icons">save</i>&nbsp;Save</button>';
		html += '</div>';
		
		html += '</div>';		
		html += '</div>';
		
		$('body').append(html);
		
	};
	
	this.createCloneTemplatePopup = function() {
		
		let html = '<div class="leca-model-dialog leca-model-ghost-back">';		
		html += '<div class="leca-report-template-clone-model">';
		
		html += '<div class="leca-report-template-model-header"><h1>Clone Template</h1><a href="#" class="leca-model-close-btn"><i class="fa fa-times"></i></a></div>';		
		html += '<div class="leca-report-template-model-content">';
		html += '<div class="form-row"><label>Name</label><input type="text" class="leca-report-template-model-field" data-key="name" data-type="text"></div>';
		html += '</div>';		
		html += '<div class="leca-report-template-model-footer">';
		html += '<button class="leca-report-template-model-clone-btn"><i class="material-icons">content_copy</i>&nbsp;Clone</button>';
		html += '</div>';
		
		html += '</div>';		
		html += '</div>';
		
		$('body').append(html);
		
	};
	
	this.onTemplateSelectorChange = function(_target) {	
		
		if (_target.val() == "*new") {
			this.createNewTemplatePopup();
			/* Hide the top action buttons */
			this.controller.topActionBtnHolder.find("button").hide();
			this.controller.topActionBtnHolder.find("button.leca-cancel-template-action").show();
		} else {
			/* Reset template related properties */
			this.lines = [];			
			this.fields = {};						
			
			/* Reset the template record */
			this.activeReportTemplate = {};
			
			/* Update the breadcrumb */					
			this.controller.breadCrumb.html(this.controller.currentMenu.html() +"&nbsp;/&nbsp;<span>"+ this.activeRecord["REPORT_NAME"] +"</span>&nbsp;/&nbsp;<span>"+ _target.children(':selected').text() +"</span>");
			/* Load template */
			this.controller.request = this.controller.prepareRequest("/visualizer/rf/get", this.controller.context, "REPORT_TEMPLATE", 0, {
				TEMPLATE_ID: parseInt(_target.val()),
				REPORT_ID: this.activeRecord.REPORT_ID
			});
			/* Initiate the request */
			this.controller.dock();
		}
		
	};
	
	/**
	 * Called whenever model dialog closed 
	 */
	this.modelDialogClosed = function(_model) {
		if (_model == "template-create-nodel") {			
			if (this.activeReportTemplate) {			
				$("select.leca-report-template-selector").val(this.activeReportTemplate.TEMPLATE_ID);
				$("select.leca-report-template-selector").trigger("change");
			}			
		}
	};
	
	this.doArchive = function() {
		
		/* Make sure the designer is hidden */
		$("#leca-workspace-report-designer").hide();
		/* Load report list */
		this.controller.request = this.controller.prepareRequest("/visualizer/rf/list", this.controller.context, "REPORTS", 0, {});
		/* Initiate the request */
		this.controller.dock();
		
	};
	
	/**
	 * This handler will be called from 'handleResponse' method
	 * Whenever a response arrived for any GRID view
	 * This method has not much to so except hand over the control to controller's 'loadGridRecord' method */
	this.renderArchive = function(_req, _res) {
		
	};
	
	/**
	 * Called by the controller whenever any link field clicked on the grid view
	 */
	this.doSingle = function(_target) {
		
		let records = [],
			footer = $("#leca-report-ds-footer-section");		
		/* Clear report type section */
		this.clearReportSection();
		
		let _key = _target.attr("data-key"),
	 		_val = _target.attr("data-val");
		
		/* Report Grid */
		this.activeRecord = {};
		this.activeReportType = {};
		
		records = this.grids["report_grid"].records;
		for (let i = 0; i < records.length; i++) { 
			if (parseInt(records[i][this.grids["report_grid"].getColumnPosition(_key)]) === parseInt(_val)) {
				this.activeRecord["REPORT_ID"] = records[i][this.grids["report_grid"].getColumnPosition("REPORT_ID")];
				this.activeRecord["REPORT_NAME"] = records[i][this.grids["report_grid"].getColumnPosition("REPORT_NAME")];			
				this.activeRecord["IS_FROM_JASPER"] = records[i][this.grids["report_grid"].getColumnPosition("IS_FROM_JASPER")];
				this.activeRecord["BEAN"] = records[i][this.grids["report_grid"].getColumnPosition("BEAN")];
				this.activeRecord["METHOD"] = records[i][this.grids["report_grid"].getColumnPosition("METHOD")];
				this.activeRecord["OKEY"] = records[i][this.grids["report_grid"].getColumnPosition("OKEY")];
				this.activeRecord["TARGET"] = records[i][this.grids["report_grid"].getColumnPosition("TARGET")];
				this.activeRecord["CSV_EXPORT_AVAILABLE"] = records[i][this.grids["report_grid"].getColumnPosition("CSV_EXPORT_AVAILABLE")];
				this.activeRecord["XLS_EXPORT_AVAILABLE"] = records[i][this.grids["report_grid"].getColumnPosition("XLS_EXPORT_AVAILABLE")];
				this.activeRecord["TXT_EXPORT_AVAILABLE"] = records[i][this.grids["report_grid"].getColumnPosition("TXT_EXPORT_AVAILABLE")];
				this.activeRecord["PDF_EXPORT_AVAILABLE"] = records[i][this.grids["report_grid"].getColumnPosition("PDF_EXPORT_AVAILABLE")];			
				this.activeRecord["STATUS"] = records[i][this.grids["report_grid"].getColumnPosition("STATUS")];				
				break;
			}
		}
		
		if (!$.isEmptyObject(this.activeRecord)) {
			
			/* Update the breadcrumb */					
			this.controller.breadCrumb.html(this.controller.currentMenu.html() +"&nbsp;/&nbsp;<span>"+ this.activeRecord["REPORT_NAME"] +"</span>");
			
			$("#leca-report_designer-single-report_name").val(this.activeRecord["REPORT_NAME"]);
			$("#leca-report_designer-single-bean-select").val(this.activeRecord["BEAN"]);
			$("#leca-report_designer-single-bean-select").trigger("change");			
			this.setMethodPending = true;		
			
			if (this.activeReportType["TARGET"] && this.activeReportType["TARGET"] != "") {
				/* Load the targeted user list */			
				let users = this.activeReportType.TARGET.split(',');				
				$("input.leca-report_designer-single-target-user-check").each(function() {
					$(this).prop("checked", false);
					for (let i = 0; i < users.length; i++) {					
						if (users[i] == $(this).val()) {
							$(this).prop("checked", true);
							break;
						}
					}
				});							
			}	
			if (this.activeReportType.FORMAT_CSV) {
				$("input.report_designer-single-format-csv").prop('checked', true);
			}
			if (this.activeReportType.FORMAT_XLS) {
				$("input.report_designer-single-format-xls").prop('checked', true);	
			}
			if (this.activeReportType.FORMAT_TXT) {
				$("input.report_designer-single-format-txt").prop('checked', true);
			}
			if (this.activeReportType.FORMAT_PDF) {
				$("input.report_designer-single-format-pdf").prop('checked', true);
			}
			
			$("#leca-report_designer-single-name").focus();
			
		}
				
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
		/* Make sure the designer is hidden */
		$("#leca-workspace-report-designer").hide();
	};
	
	/**
	 * Called by the controller for Add or New button click
	 */
	this.doNew = function() {
		$("#leca-report_designer-create-report_name").focus().val("");
	};
	
	/**
	 * Called by the controller for Save or Update button click
	 */
	this.doCreate = function(_isUpdate) {		
		let me = this,
			proceed = true,
			name = $("#leca-report_designer-"+ this.viewMode +"-report_name"),
			bean = $("#leca-report_designer-"+ this.viewMode +"-bean-select"),
			okey = $("#leca-report_designer-"+ this.viewMode +"-ds-object-key"),
			method = $("#leca-report_designer-"+ this.viewMode +"-method-select"),
			payload = {};
			
		if (name.val() == "") {
			proceed = false;
			this.controller.notifyFlash.html("Please specify a name for the report");			
		}		
		if (!bean.val() || bean.val() == "") {
			proceed = false;
			this.controller.notifyFlash.html("Please specify the bean name");			
		}		
		if (!method.val() || method.val() == "") {
			proceed = false;
			this.controller.notifyFlash.html("Please specify the method name");				
		}
		
		if (!proceed) {
			this.controller.notifyFlash.show();
			setTimeout(function() {me.controller.notifyFlash.hide();}, 5000);
			return;	
		}		
			
		payload["REPORT_NAME"] = name.val();
		payload["BEAN"] = bean.val();
		payload["METHOD"] = method.val();
		payload["OKEY"] = okey.val();
		payload["STATUS"] = true;
		payload["IS_FROM_JASPER``"] = false;
		payload["CSV_EXPORT_AVAILABLE"] = $("input.report_designer-"+ this.viewMode +"-format-csv").is(":checked");
		payload["TXT_EXPORT_AVAILABLE"] = $("input.report_designer-"+ this.viewMode +"-format-txt").is(":checked");
		payload["PDF_EXPORT_AVAILABLE"] = $("input.report_designer-"+ this.viewMode +"-format-pdf").is(":checked");
		payload["XLS_EXPORT_AVAILABLE"] = $("input.report_designer-"+ this.viewMode +"-format-xls").is(":checked");
		payload["TARGET"] = $("input.leca-report_designer-"+ this.viewMode +"-target-user-check:checked").map(function() {return this.value;}).get().join(',');
		
		if (_isUpdate) {
			payload["REPORT_ID"] = this.activeRecord.REPORT_ID;
			this.controller.request = this.controller.prepareRequest("/visualizer/rf/update", this.controller.context, "REPORTS", 0, payload);
		} else {
			this.controller.request = this.controller.prepareRequest("/visualizer/rf/create", this.controller.context, "REPORTS", 0, payload);	
		}		
		/* Initiate the request */
		this.controller.dock();
				
	};	
	
	this.doUpdate = function() {
		this.doCreate(true);		
	};
	
	this.clearReportSection = function() {
		
		this.activeReportType = {};
		this.setMethodPending = true;
		
		$("#leca-report_designer-create-name").val("");				
		$("#leca-report_designer-create-bean-select")[0].selectedIndex = 0;
		$("#leca-report_designer-create-bean-select").trigger("change");		
		$("#leca-report_designer-create-ds-object-key").val("");		
		$("input.leca-report_designer-single-format-check").prop('checked', false);
		$("input.report_designer-single-format-pdf").prop('checked', true);		
		$("input.leca-report_designer-create-target-user-check").prop('checked', true);		
		$("#leca-report_designer-create-name").focus();
		
	};
	
	this.publishTemplate = function() {
		
		let payload = {},
			options = this.fetchTemplateProperties(false);		
		if (!$.isEmptyObject(options)) {			
			payload["TEMPLATE_NAME"] = options["name"];
			delete options["name"];
			payload["OPTIONS"] = options;
			payload["IS_FACTORY_TEMPLATE"] = false;
			payload["REPORT_ID"] = this.activeRecord["REPORT_ID"];			
			this.controller.request = this.controller.prepareRequest("/visualizer/rf/create", this.controller.context, "REPORT_TEMPLATES", 0, payload);
			this.controller.dock();
		}		
		
	};
	
	this.updateTemplate = function() {
		
		let payload = {},
			options = this.fetchTemplateProperties(true);	
				
		if (!$.isEmptyObject(options)) {	
			this.prepareLineMapping();
			payload["TEMPLATE_NAME"] = options["name"];
			delete options["name"];			
			if ($.isEmptyObject(options)) {
				payload["OPTIONS"] = JSON.parse(this.activeReportTemplate.OPTIONS);
			} else {
				payload["OPTIONS"] = options;
			}
			payload["LINES"] = this.lines;
			payload["HEADER_LINES"] = this.headerLines;
			payload["FOOTER_LINES"] = this.footerLines;			
			payload["FIELDS"] = this.stripFields();
			payload["STATUS"] = this.activeReportTemplate["STATUS"];
			payload["TEMPLATE_ID"] = this.activeReportTemplate["TEMPLATE_ID"];
			payload["REPORT_ID"] = this.activeRecord["REPORT_ID"];
			payload["IS_FACTORY_TEMPLATE"] = this.activeReportTemplate["IS_FACTORY_TEMPLATE"];
			
			this.controller.request = this.controller.prepareRequest("/visualizer/rf/update", this.controller.context, "REPORT_TEMPLATES", 0, payload);			 
			this.controller.dock();
		}
		
	};
	
	this.stripFields = function() {
		var striped = {};
		let keys = Object.keys(this.fields);
		for (let i = 0; i < keys.length; i++) {
			striped[keys[i]] = {
				FIELD_TYPE: this.fields[keys[i]].FIELD_TYPE,
				IS_EDITABLE: this.fields[keys[i]].IS_EDITABLE,
				IS_REMOVABLE: this.fields[keys[i]].IS_REMOVABLE,
				OPTIONS: this.fields[keys[i]].OPTIONS
			};
			if (typeof this.fields[keys[i]]["FIELD_ID"] !== 'undefined') {
				striped[keys[i]]["FIELD_ID"] = this.fields[keys[i]]["FIELD_ID"];
			}
			if (this.fields[keys[i]].FIELD_TYPE == "record_table") {
				let cols = Object.keys(striped[keys[i]].OPTIONS.columns);
				for (let j = 0; j < cols.length; j++) {
					let col = striped[keys[i]].OPTIONS.columns[cols[j]];
					delete col["target"];
					striped[keys[i]].OPTIONS.columns[cols[j]] = col;
				}
			}
		}
		return striped;
	};
	
	this.fetchTemplateProperties = function(_isUpdate) {
		
		let me = this,			
			options = {},			
			nameField = null,
			selector = ".leca-report-template-model-field";
		
		if (_isUpdate) {
			selector = ".leca-rd-report-setup-field";
		}		
		$(selector).each(function() {
			if ($(this).attr("data-key") == "name") {
				if ($(this).val() != "") {
					options[$(this).attr("data-key")] = $(this).val();
				} else {
					nameField = $(this);
					nameField.css("background", "peachpuff");
					me.controller.notifyFlash.html("Please specify a name for this template.!");
					me.controller.notifyFlash.show();
					setTimeout(function() {me.controller.notifyFlash.hide();nameField.css("background", "white");}, 5000);
					return {};
				}				
			} else {
				if ($(this).is(":visible")) {
					options[$(this).attr("data-key")] = $(this).val();
				}
			}		
		});	
		return options;
		
	};
	
	/**
	 * Called by the controller for Delete button click
	 */
	this.doDelete = function() {
		
		if (!$.isEmptyObject(this.activeRecord)) {
			this.controller.request = this.controller.prepareRequest("/visualizer/rf/delete", this.controller.context, "REPORTS", 0, {
				REPORT_ID: this.activeRecord["REPORT_ID"], 
				REPORT_NAME: this.activeRecord["REPORT_NAME"]
			});
			this.controller.dock();
		}	
		
	};
	
	/**
	 * Called by the controller whenever view resizing happening
	 */
	this.onViewResize = function() {
		
		let keys = Object.keys(this.grids);
		for (let i = 0; i < keys.length; i++) {
			this.grids[keys[i]].reloadGrid();
		}		
		this.resizeDesignerLayout();
		this.reloadReport();
		
	};
	
	this.resizeDesignerLayout = function() {
		//$("div.leca-rd-sidebar-panel").width($("div.leca-rd-sidebar-panel").parent().width());
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
	
	this.systemLayoutChange = function() {		
		this.reloadReport();			
	}
	
	this.loadUserList = function(_payload) {
		
		let container = $("#leca-report-ds-users-config").find("> div");
		container.html("");
		for (let j = 0; j < _payload.length; j++) {
			container.append($('<label><input type="checkbox" class="leca-report-target-user-check" value="'+ _payload[j]["UMS_USER_ID"] +'" checked /> '+ _payload[j]["UMS_USER_NAME"] +'</label>'));
		}	
		
	};
	
	this.loadBeanList = function(_beanList) {
		
		$("#leca-report_designer-single-bean-select").html($('<option value="">-- Select Your Data Source Bean --</option>'));	
		$("#leca-report_designer-create-bean-select").html($('<option value="">-- Select Your Data Source Bean --</option>'));
			
		for (let i = 0; i < _beanList.length; i++) {
			$("#leca-report_designer-single-bean-select").append($('<option value="'+ _beanList[i] +'">'+ _beanList[i].substr(_beanList[i].lastIndexOf(".") + 1) +'</option>'));
			$("#leca-report_designer-create-bean-select").append($('<option value="'+ _beanList[i] +'">'+ _beanList[i].substr(_beanList[i].lastIndexOf(".") + 1) +'</option>'));			
		}	
		
	};
	
	this.loadMethghodList = function(_bean, _methodList) {	
		let bean = $("#leca-report_designer-"+ this.viewMode +"-bean-select").val(),
			methodSelect = $("#leca-report_designer-"+ this.viewMode +"-method-select");
		if (bean === _bean) {
			methodSelect.html("");
			for (let i = 0; i < _methodList.length; i++) {
				methodSelect.append($('<option value="'+ _methodList[i] +'">'+ _methodList[i] +'</option>'));
			}
			if (this.setMethodPending) {
				this.setMethodPending = false;
				methodSelect.val(this.activeRecord.METHOD);
			}
		}		
	};
	
	/**
	 * Initialize the designer pad
	 */
	this.initDesigner = function() {
		
		let me = this;
		
		/* Clear the designer pad */
		this.dpad.html("");		
		/* Determine the total number of rows & columns */
		this.totalRows = this.getLineCount();
		this.totalColumns = this.getCharacterCount();	
		
		/* Config the Grid Layout Property */
		this.setLayoutGrid();
		/* For sub report */
		this.loadReportList();		
		
		setTimeout(function() {
			/* Determine the single grid item's height and width */
			let size = me.dpad.css("grid-template-rows").split(' ')[0];		
			if (size.indexOf("px") !== -1) {
				me.gItemHeight = parseFloat(size.substring(0, (size.length - 2)));
			} else {
				me.gItemHeight = parseFloat(size);
			}
			
			/**/
			size = me.dpad.css("grid-template-columns").split(' ')[0];
			
			if (size.indexOf("px") !== -1) {
				me.gItemWidth = parseFloat(size.substring(0, (size.length - 2)));
			} else {
				me.gItemWidth = parseFloat(size);
			}
			
			me.resizeDesignerLayout();
			/* Setup fields */
			me.toggleReportFormat();
			/* Setup style rules for CPI and LPI */
			me.setStyleHead();	
		}, 200);	
		
	};
	
	/**
	 * Insert typography styles for widgets
	 */
	this.setStyleHead = function() {
		
		let rules = '#leca-rd-designer-design-pad {line-height: '+ this.gItemHeight +'px;}';
		rules += 'div.leca-rd-widget.record_table div.content {text-align: center;}';
		rules += 'div.leca-rd-widget.record_table div.line > span,';
		rules += 'div.leca-rd-widget.record_table div.content > span,';
		rules += 'div.leca-rd-widget.record_table div.drow > div.dcolumn > span,';		
		rules += 'div.leca-rd-widget.dynamic_text > span, div.leca-rd-widget.static_text > span,';		
		rules += 'div.leca-rd-widget.page_number span';
		rules += '{width:'+ this.gItemWidth +'px; height: '+ this.gItemHeight +'px;}';
		this.styleHead.text(rules);
		
	};
	
	this.prepareLineMapping = function() {
		
		this.lines = [];
		
		let header = null,
			footer = null,		
			tempFields = [],	
			container_height = 0,
			uids = Object.keys(this.fields);
			
		for (let i = 1; i <= this.totalRows; i++) {
			
			tempFields = [];
			this.lines.push([]);
			
			for (let j = 0; j < uids.length; j++) {
				if (i >= this.fields[uids[j]]["OPTIONS"].row_start && i < (this.fields[uids[j]]["OPTIONS"].row_end)) {
					if (!this.isAChildField(uids[j])) {				
						
						tempFields.push(this.fields[uids[j]]);
						tempFields[tempFields.length - 1]["FIELD_KEY"] = uids[j];
											
						if (this.fields[uids[j]].FIELD_TYPE == "page_header") {
							header = this.fields[uids[j]];
						}
						if (this.fields[uids[j]].FIELD_TYPE == "page_footer") {
							footer = this.fields[uids[j]];
						}							
					}					
				}
			}
			
			/* Sorting the line mapping */
			tempFields.sort(function(a, b) {				
				if (a.OPTIONS.column_start < b.OPTIONS.column_start) {
					return -1;
				}
				if (a.OPTIONS.column_start > b.OPTIONS.column_start) {
					return 1;
				}					
				return 0;
			});
			
			for (let x = 0; x < tempFields.length; x++) {
				this.lines[(i-1)].push(tempFields[x].FIELD_KEY);
			}
				
		}
		
		/* Crop lines */
		for (let i = (this.totalRows - 1); i > 0; i--) {		
			if (this.lines[i].length == 0) {
				this.lines.splice(i, 1);
			} else {
				break;
			}
		}
		
		/* Prepare header fields */
		tempFields = [];
		this.headerLines = [];
		
		if (header) {			
			container_height = header.OPTIONS.row_end - header.OPTIONS.row_start;
			for (let i = 1; i <= container_height; i++) {
				tempFields = [];	
				this.headerLines.push([]);						
				for (let j = 0; j < this.headerFields.length; j++) {
					if (i >= this.fields[this.headerFields[j]]["OPTIONS"].row_start && i < (this.fields[this.headerFields[j]]["OPTIONS"].row_end)) {
						//this.headerLines[(i-1)].push(this.headerFields[j]);
						tempFields.push(this.fields[this.headerFields[j]]);						
						tempFields[tempFields.length - 1]["FIELD_KEY"] = this.headerFields[j];
					}		
				}
				/* Sorting the line mapping */
				tempFields.sort(function(a, b) {				
					if (a.OPTIONS.column_start < b.OPTIONS.column_start) {
						return -1;
					}
					if (a.OPTIONS.column_start > b.OPTIONS.column_start) {
						return 1;
					}					
					return 0;
				});
				for (let x = 0; x < tempFields.length; x++) {
					this.headerLines[(i-1)].push(tempFields[x].FIELD_KEY);
				}
				
			}
		}		
		
		/* Prepare header fields */
		this.FooterLines = [];
		if (footer) {			
			container_height = footer.OPTIONS.row_end - footer.OPTIONS.row_start;
			for (let i = 1; i <= container_height; i++) {
				this.footerLines.push([]);
				for (let j = 0; j < this.footerFields.length; j++) {
					if (i >= this.fields[this.footerFields[j]]["OPTIONS"].row_start && i < (this.fields[this.footerFields[j]]["OPTIONS"].row_end)) {
						this.footerLines[(i-1)].push(this.footerFields[j]);
						/* Sorting the line mapping */
						
					}		
				}
			}
		}
		
	};
	
	this.isAChildField = function(_fkey) {
		if (this.headerFields.indexOf(_fkey) !== -1 || this.footerFields.indexOf(_fkey) !== -1) {
			return true;
		}
		return false;
	};
	
	/**
	 * Handles the Report Setup's select box change event
	 */
	this.handleSetupSelectChange = function(_target) {
		
		if (_target.attr("data-key") == "format") {
			this.toggleReportFormat();
		} else if (_target.attr("data-key") == "printer_type") {
			this.togglePrinterType(_target.val());
		} else if (_target.attr("data-key") == "printer_cpi") {
			this.toggleCPISelect(_target.val());
		} else if (_target.attr("data-key") == "printer_lpi") {
			this.toggleLPISelect(_target.val());
		} else if (_target.attr("data-key") == "font") {
			this.toggleReportFont(_target.val());
		} else if(_target.attr("data-key") == "orientation") {
			this.toggleOrientation(_target.val());
		}
		
	};
	
	this.handleTemplateModelSelectChange = function(_target) {
		
	};
	
	/**
	 * Handles report format chnage event
	 */
	this.toggleReportFormat = function() {
		
		let format = $("select[data-key=format]").val();		
		/* Toggle widget list */
		
		$("#leca-rd-widget-list > a").removeClass("disabled");
		$("#leca-rd-widget-list > a").each(function() {			
			if ($(this).attr("data-support").indexOf(format) == -1) {
				$(this).addClass("disabled");
			}
		});
			
		$("div.leca-accordian-header").removeClass("disabled");
		$("div.leca-accordian-header").each(function() {
			if ($(this).attr("data-support").indexOf(format) == -1) {
				$(this).addClass("disabled");
			}
		});
		
		$("tr.leca-unit-table-tr").removeClass("disabled");
		$("tr.leca-unit-table-tr").each(function() {
			if ($(this).attr("data-support").indexOf(format) == -1) {
				$(this).addClass("disabled");
			}
		});
		
		if (format == "media") {
			$("select[data-key=printer_type]").val("other");
			$("select[data-key=printer_type] option[value=dmp]").prop("disabled", true);
			$("select[data-key=printer_type] option[value=other]").attr('selected', 'selected');
			$("select[data-key=printer_type] option[value=other]").trigger("change");
		} else {
			this.marginTop = 0;
			this.marginBottom = 0;
			$("select[data-key=printer_type] option[value=dmp]").prop("disabled", false);
		}
		
		$("input.leca-rd-unit-field.margin-top").val(this.marginTop);
		$("input.leca-rd-unit-field.margin-bottom").val(this.marginBottom);
		$("input.leca-rd-unit-field.margin-left").val(this.marginLeft);
		$("input.leca-rd-unit-field.margin-right").val(this.marginRight);
		
		$("select[data-key=printer_type]").trigger("change");
		
	};
	
	this.togglePrinterType = function(_option) {
		
		if (_option == "dmp") {
			$("div.leca-rd-cpi-form-row").show();
			$("div.leca-rd-lpi-form-row").show();
			//$("tr.leca-rd-margin-top-bottom-form-row").hide();
		} else {
			$("div.leca-rd-cpi-form-row").hide();
			$("div.leca-rd-lpi-form-row").hide();
			//$("tr.leca-rd-margin-top-bottom-form-row").show();
		}
		
	};
	
	/**
	 * Handles CPI select change event
	 */
	this.toggleCPISelect = function(_option) {
		
		this.cpi = parseInt(_option);
		this.reloadReport();
		
	};
	
	/**
	 * Handles LPI select change event
	 */
	this.toggleLPISelect = function(_option) {
		
		let divisor = $("input[data-key=lpi-divisor]");
		divisor.show();
		if (_option == "n/72") {
			divisor.attr("placeholder", "n = 0....85");
		} else if (_option == "n/216") {
			divisor.attr("placeholder", "n = 0....255");
		} else {
			divisor.hide();
		}		
		if (_option == 6 || _option == 8) {
			this.lpi = parseInt(_option);
		} else if (_option == "7/72") {
			this.lpi = 72/7;
		} else if (_option == "n/72") {
			if ($("input[data-key='lpi-divisor']").val() != "") {
				this.lpi = 72/parseFloat($("input[data-key='lpi-divisor']").val());
			}
		} else if (_option == "n/216") {
			if ($("input[data-key='lpi-divisor']").val() != "") {
				this.lpi = 216/parseFloat($("input[data-key='lpi-divisor']").val());
			}
		}	
		this.reloadReport();
		
	};
	
	/**
	 * Handles Font Family select change
	 */
	this.toggleReportFont = function(_option) {
		
		if (_option == "roman") {
			this.dpad.css("font-family", '"Times New Roman", Times, serif');
		} else {
			this.dpad.css("font-family", 'Arial, Helvetica, sans-serif');
		}
		
	};
	
	/**
	 * 
	 */
	this.toggleOrientation = function(_option) {
		
	};
	
	/**
	 * Handles Widget Property's input fields change event
	 */
	this.handlePropertyInputChange = function(_target) {
		
		if (_target.attr("data-key") == "use_global_padding") {
			this.fields[this.currentUid]["OPTIONS"]["use_global_padding"] = _target.is(":checked");
		} else if (_target.attr("data-key") == "padding_top" || 
			_target.attr("data-key") == "padding_right" || 
			_target.attr("data-key") == "padding_bottom" || 
			_target.attr("data-key") == "padding_left") {			
			this.fields[this.currentUid]["OPTIONS"][_target.attr("data-key")] = _target.val();			
		} else if (_target.attr("data-key") == "label") {
			if (this.fields[this.currentUid].FIELD_TYPE === "static_text" || this.fields[this.currentUid].FIELD_TYPE === "page_number") {
				this.fields[this.currentUid]["OPTIONS"].label = _target.val();
				this.fields[this.currentUid].target.html(this.spanifyChar(_target.val()));
			} else if (this.fields[this.currentUid].FIELD_TYPE === "record_table") {
				/* This must be for the data grid column */
				this.fields[this.currentUid]["OPTIONS"].columns[this.selectedColumn.attr("data-uid")].label = _target.val();
				this.selectedColumn.html(this.spanifyChar(_target.val()));
			}
		} else if (_target.attr("data-key") == "delimiter") {
			if (this.fields[this.currentUid].FIELD_TYPE === "page_number") {
				this.fields[this.currentUid]["OPTIONS"].delimiter = _target.val();
				this.fields[this.currentUid].target.find("div.delimiter").html(this.spanifyChar(_target.val()));
			}
		} else if (_target.attr("data-key") == "handle") {
			if (this.fields[this.currentUid].FIELD_TYPE === "static_text") {				
				this.fields[this.currentUid]["OPTIONS"].handle = _target.val();
			} else if (this.fields[this.currentUid].FIELD_TYPE === "dynamic_text") {				
				this.fields[this.currentUid]["OPTIONS"].handle = _target.val();
				this.fields[this.currentUid].target.html(this.spanifyChar("${"+_target.val()+"}"));				
			} else if(this.fields[this.currentUid].FIELD_TYPE === "record_table") {
				if (this.selectedColumn) {
					/* this is for column */
					this.fields[this.currentUid]["OPTIONS"].columns[this.selectedColumn.attr("data-uid")].handle = _target.val();
				} else {
					/* this is for data grid itself */
					this.fields[this.currentUid]["OPTIONS"].handle = _target.val();
				}
			} else if (this.fields[this.currentUid].FIELD_TYPE === "sub_report") {
				this.fields[this.currentUid]["OPTIONS"].handle = _target.val();
			}
		}
		
	};
	
	this.handlePropertySelectChange = function(_target) {
		
		if (_target.attr("data-key") == "has_footer") {
			/* Just in case, make sure it is for Data Grid */
			if (this.fields[this.currentUid].FIELD_TYPE === "record_table") {
				this.fields[this.currentUid]["OPTIONS"].has_footer = _target.val();
				let rowE = parseInt(this.fields[this.currentUid].target.css("grid-row-end"), 10);				
				if (_target.val() === "yes") {					
					this.fields[this.currentUid].target.css("grid-row-end", (rowE + 3));
					this.fields[this.currentUid].target.find("div.footer-wrapper").show();
				} else {
					this.fields[this.currentUid].target.css("grid-row-end", (rowE - 3));
					this.fields[this.currentUid].target.find("div.footer-wrapper").hide();
				}
				/* Update highlighter and selector */
				this.highlightWidget(this.fields[this.currentUid].target);
				this.refreshSelector(this.fields[this.currentUid].target);
			}
		} else if (_target.attr("data-key") == "is_editable") {
			if (_target.val() === "yes") {
				this.fields[this.currentUid].IS_EDITABLE = true;
			} else {
				this.fields[this.currentUid].IS_EDITABLE = false;
			}
		} else if (_target.attr("data-key") == "is_removable") {
			if (_target.val() === "yes") {
				this.fields[this.currentUid].IS_REMOVABLE = true;
			} else {
				this.fields[this.currentUid].IS_REMOVABLE = false;
			}
		} else if (_target.attr("data-key") == "stretch_with_overflow") {
			if (_target.val() === "yes") {
				this.fields[this.currentUid].OPTIONS.stretch_with_overflow = true;
			} else {
				this.fields[this.currentUid].OPTIONS.stretch_with_overflow = false;
			}
		} else if (_target.attr("data-key") == "remove_line_when_blank") {
			if (_target.val() === "yes") {
				this.fields[this.currentUid].OPTIONS.remove_line_when_blank = true;
			} else {
				this.fields[this.currentUid].OPTIONS.remove_line_when_blank = false;
			}
		} else if (_target.attr("data-key") == "repeat_field") {
			if (_target.val() === "yes") {
				this.fields[this.currentUid].OPTIONS.repeat_field = true;
			} else {
				this.fields[this.currentUid].OPTIONS.repeat_field = false;
			}
		} else {
			this.fields[this.currentUid].OPTIONS[_target.attr("data-key")] = _target.val();
			if (_target.attr("data-key") == "report") {
				this.fields[this.currentUid].target.html(this.spanifyChar("[ --- "+_target.find("option:selected").text()+" --- ]"));	
			}
		} 
				
	};
	
	/**
	 * Handles Selector Type fields change event
	 */
	this.handleSelectorChange = function(_target) {
		
		let key = _target.closest("div.leca-rd-unit-field").attr("data-key");
		if (key === "text_align") {			
			
			this.fields[this.currentUid]["OPTIONS"].alignment = _target.attr("data-option");
			this.fields[this.currentUid].target.css("text-align", _target.attr("data-option"));
						
		} else if(key === "column_align" || key === "record_align") {
			/* Well this must be for Data Grid Column */
			if (this.selectedColumn) {
				if (key === "column_align") { console.log(this.fields[this.currentUid]["OPTIONS"].columns[this.selectedColumn.attr("data-uid")].target);
					this.fields[this.currentUid]["OPTIONS"].columns[this.selectedColumn.attr("data-uid")].column_align = _target.attr("data-option");
					this.fields[this.currentUid]["OPTIONS"].columns[this.selectedColumn.attr("data-uid")].target.css("text-align", _target.attr("data-option"));
				} else {
					this.fields[this.currentUid]["OPTIONS"].columns[this.selectedColumn.attr("data-uid")].record_align = _target.attr("data-option");
				}
			}
		} else if (key === "text_decoration") {		
							
			if (_target.attr("data-option") == "none") {					
				this.fields[this.currentUid]["OPTIONS"].font_style = "normal";
				this.fields[this.currentUid]["OPTIONS"].font_weight = "normal";
				this.fields[this.currentUid]["OPTIONS"].text_decoration = "none";					
				this.fields[this.currentUid].target.css("font-weight", "normal");
				this.fields[this.currentUid].target.css("font-style", "normal");
				this.fields[this.currentUid].target.css("text-decoration", "none");
			} else if(_target.attr("data-option") == "bold") {
				this.fields[this.currentUid]["OPTIONS"].font_weight = (_target.hasClass("selected") ? "bold" : "normal");
				this.fields[this.currentUid].target.css("font-weight", (_target.hasClass("selected") ? "bold" : "normal"));
			} else if (_target.attr("data-option") == "italic") {
				this.fields[this.currentUid]["OPTIONS"].font_style = (_target.hasClass("selected") ? "italic" : "normal");
				this.fields[this.currentUid].target.css("font-style", (_target.hasClass("selected") ? "italic" : "normal"));
			} else if (_target.attr("data-option") == "underline") {
				this.fields[this.currentUid]["OPTIONS"].text_decoration = (_target.hasClass("selected") ? "underline" : "none");
				this.fields[this.currentUid].target.css("text-decoration", (_target.hasClass("selected") ? "underline" : "none"));
			}	
			
			
			/* If all disabled then enable the none option */
			if (_target.parent().find("label.selected").length == 0) {
				_target.parent().find("label:first").addClass("selected");
			}
		}
		
	};
	
	/**
	 * Handles Margin Fields change event
	 */
	this.updatePageMargin = function() {
		
		this.marginTop = $("input.leca-rd-unit-field.margin-top").val();
		this.marginRight = $("input.leca-rd-unit-field.margin-right").val();
		this.marginBottom = $("input.leca-rd-unit-field.margin-bottom").val();
		this.marginLeft = $("input.leca-rd-unit-field.margin-left").val();
		
	};
	
	this.clearState = function() {
		
		this.selector.hide();
    	this.currentUid = null;
    	this.currentItem = null;				    			    	
    	$("#leca-rd-widget-edit-btn").removeClass("active");
    	$("#leca-rd-sidebar-properties-panel > div").hide();		    	
    	$("#leca-rd-properties-empty-msg").show();	
    	$("#leca-rd-widget-properties-section-label").html("Widget");
    	$("div.leca-rd-widget.dynamic_text.edit").removeClass("edit");
    	$("div.leca-rd-widget.dynamic_text > div").removeClass("active");
    	$("div.leca-rd-widget.page_number.edit").removeClass("edit");
    	$("div.leca-rd-widget.page_number > div").removeClass("active");
		
	};
	
	this.reloadReport = function() {
		let me = this;
		/* Initialize the designer agina */
		this.initDesigner();
				
		/* Since initDesginer has  timeout, we need to wait here for it to finish */
		setTimeout(function() {		
				
			let cUid,				
				widget = null,				
				tempUid = me.currentUid,
				keys = Object.keys(me.fields);
			
			me.clearState();
			
			/* Get the header & footer field's child's */
			for (let x = 0; x < keys.length; x++) {
				if (me.fields[keys[x]].FIELD_TYPE == "page_header") {
					pHeader = me.fields[keys[x]];
					me.headerFields = JSON.parse(JSON.stringify(me.fields[keys[x]].OPTIONS.fields));
				}
				if (me.fields[keys[x]].FIELD_TYPE == "page_footer") {
					me.footerFields = JSON.parse(JSON.stringify(me.fields[keys[x]].OPTIONS.fields));
				}
			}				
				
			for (let i = 0; i < keys.length; i++) {
				
				if (me.isAChildField(keys[i])) {
					continue;
				}
				
				/* Update the current UID */
				me.currentUid = keys[i];
				
				if (me.fields[keys[i]].FIELD_TYPE == "static_text") {
					widget = me.renderStaticText(keys[i]);					
				} else if (me.fields[keys[i]].FIELD_TYPE == "dynamic_text") {
					widget = me.renderDynamicText(keys[i]);					
				} else if (me.fields[keys[i]].FIELD_TYPE == "record_table") {					
					widget = me.renderRecordTable(keys[i]);									
				} else if (me.fields[keys[i]].FIELD_TYPE == "sub_report") {
					widget = me.renderSubReport(keys[i]);
				} else if (me.fields[keys[i]].FIELD_TYPE == "page_number") {
					widget = me.renderPageNumber(keys[i]);					
				} else if (me.fields[keys[i]].FIELD_TYPE == "page_header") {
					widget = me.renderPageHeader(keys[i]);
					/* Disable Page Header widget as only single instance allowed */
					$('#leca-rd-widget-list a[data-type="page_header"]').addClass("disabled");						
				} else if (me.fields[keys[i]].FIELD_TYPE == "page_footer") {					
					widget = me.renderPageFooter(keys[i]);
					/* Disable Page Footer widget as only single instance allowed */
					$('#leca-rd-widget-list a[data-type="page_footer"]').addClass("disabled");						
				} else if (me.fields[keys[i]].FIELD_TYPE == "separator") {
					widget = me.renderSeparator(keys[i]);
				} else if (me.fields[keys[i]].FIELD_TYPE == "chart") {
					/* Yet to implement */
				} else if (me.fields[keys[i]].FIELD_TYPE == "image") {
					/* Yet to implement */
				} else {
					/* Ignore it */
				}			
				
				me.dpad.append(widget);
				me.fields[keys[i]]["target"] = widget;
				
				if (me.fields[keys[i]].FIELD_TYPE == "record_table") {
					me.fields[keys[i]]["target"].find("div.drow").sortable({containment: 'document', update: this.reOrderColumns}).disableSelection();
					cUid = 0;
					/* Render the Row */
					for (let j = 0; j < me.fields[keys[i]]["OPTIONS"].header.length; j++) {
						if (j > 0) {
							me.addRecordTableRow();
						}
						me.selectedRow = me.fields[keys[i]]["target"].find("div.header > div.drow").eq(j);
						/* Now render the columns */
						for (let k = 0; k < me.fields[keys[i]]["OPTIONS"].header[j].length; k++) {
							cUid = me.fields[keys[i]]["OPTIONS"].header[j][k];
							me.renderRecordTableColumn(cUid, me.fields[keys[i]]["OPTIONS"].columns[cUid]);
						}
					}
					if (me.fields[keys[i]]["OPTIONS"].has_footer == "yes") {						
						for (let j = 0; j < me.fields[keys[i]]["OPTIONS"].footer.length; j++) {
							if (j > 0) {
								me.addRecordTableRow();
							}
							me.selectedRow = me.fields[keys[i]]["target"].find("div.footer > div.drow").eq(j);
							/* Now render the columns */
							for (let k = 0; k < me.fields[keys[i]]["OPTIONS"].footer[j].length; k++) {
								cUid = me.fields[keys[i]]["OPTIONS"].footer[j][k];
								me.renderRecordTableColumn(cUid, me.fields[keys[i]]["OPTIONS"].columns[cUid]);
							}
						}
					}
				} else if (me.fields[keys[i]].FIELD_TYPE == "page_header") {					
					me.fields[keys[i]].target.find("> div").css("grid-template-rows", "repeat("+ Math.ceil(me.fields[keys[i]].target.height() / me.gItemHeight) +", 1fr)");														
					for (let j = 0; j < me.headerFields.length; j++) {
						if (me.fields[me.headerFields[j]].FIELD_TYPE == "static_text") {
							widget = me.renderStaticText(me.headerFields[j]);
						} else if (me.fields[me.headerFields[j]].FIELD_TYPE == "dynamic_text") {
							widget = me.renderDynamicText(me.headerFields[j]);	
						} else if (me.fields[me.headerFields[j]].FIELD_TYPE == "separator") {
							widget = me.renderSeparator(me.headerFields[j]);	
						} else if (me.fields[me.headerFields[j]].FIELD_TYPE == "page_number") {
							widget = me.renderPageNumber(me.headerFields[j]);	
						} else {
							/* Ignore it */
						}						
						me.fields[me.headerFields[j]]["target"] = widget;
						me.fields[keys[i]]["target"].find(".page-header-widgets-holder").append(widget);						
					}					
				} else if (me.fields[keys[i]].FIELD_TYPE == "page_footer") {
					me.fields[keys[i]].target.find("> div").css("grid-template-rows", "repeat("+ Math.ceil(me.fields[keys[i]].target.height() / me.gItemHeight) +", 1fr)");
					for (let j = 0; j < me.footerFields.length; j++) {
						if (me.fields[me.footerFields[j]].FIELD_TYPE == "static_text") {
							widget = me.renderStaticText(me.footerFields[j]);
						} else if (me.fields[me.footerFields[j]].FIELD_TYPE == "dynamic_text") {
							widget = me.renderDynamicText(me.footerFields[j]);	
						} else if (me.fields[me.footerFields[j]].FIELD_TYPE == "separator") {
							widget = me.renderSeparator(me.footerFields[j]);	
						} else if (me.fields[me.footerFields[j]].FIELD_TYPE == "page_number") {
							widget = me.renderPageNumber(me.footerFields[j]);	
						} else {
							/* Ignore it */
						}
						
						me.fields[me.footerFields[j]]["target"] = widget;
						me.fields[keys[i]]["target"].find(".page-footer-widgets-holder").append(widget);
					}
				} else {
					/* Ignore it */
				}
				
			}
			
			/* Restore the currentUid from before reload */
			me.currentUid = tempUid;
			
			/* Update highlighter and selector */		
			if (me.currentUid) {
				let citem = $('div[data-uid="'+ me.currentUid +'"]');
				me.highlightWidget(citem);
				me.selectWidget(citem);	
				me.loadWidgetProperties(citem);
			}
			
		}, 250);	
		
	};
	
	/**
	 * Widget drop event handler
	 */
	this.dropWidget = function(_type) {
		
		if ($.isEmptyObject(this.activeReportTemplate)) {
			this.createNewTemplatePopup();
			/* Hide the top action buttons */
			this.controller.topActionBtnHolder.find("button").hide();
			this.controller.topActionBtnHolder.find("button.leca-cancel-template-action").show();
			return;
		}
		
		/* Generate Unique ID */
		let uid = '_' + Math.random().toString(36).substr(2, 9);
		/* Get the initial meta */
		let meta = this.getInitMeta(_type);
		/* */
		let widget = null;
				
		/* update the initial location */
		meta["OPTIONS"]["column_start"] = this.currentColumn;
		meta["OPTIONS"]["column_end"] = (this.currentColumn + meta["OPTIONS"].width);
		meta["OPTIONS"]["row_start"] = this.currentRow;		
		
		if (_type == "record_table") {			
			meta["OPTIONS"]["row_end"] = this.currentRow + 4;
		} else if (_type == "page_header" || _type == "page_footer") {		
			meta["OPTIONS"]["row_end"] = this.currentRow + 5;
		} else {
			meta["OPTIONS"]["row_end"] = this.currentRow + 1;
		}
		
		/* Adjust the column_start property for DataGrid */
		if (_type == "record_table" || _type == "page_header" || _type == "page_footer" || _type == "sub_report" || _type == "separator") {
			meta["OPTIONS"]["column_start"] = this.marginLeft + 1;
			meta["OPTIONS"]["column_end"] = meta["OPTIONS"].width + this.marginLeft + 1;
		}
		
		this.fields[uid] = meta;
		
		if (_type == "static_text") {
			widget = this.renderStaticText(uid);
		} else if (_type == "dynamic_text") {
			widget = this.renderDynamicText(uid);
		} else if (_type == "record_table") {
			widget = this.renderRecordTable(uid);
		} else if (_type == "page_header") {			
			widget = this.renderPageHeader(uid);	
			/* Disable Page Header widget as only single instance allowed */
			$('#leca-rd-widget-list a[data-type="page_header"]').addClass("disabled");		
		} else if (_type == "page_footer") {			
			widget = this.renderPageFooter(uid);
			/* Disable Page Footer widget as only single instance allowed */
			$('#leca-rd-widget-list a[data-type="page_footer"]').addClass("disabled");
		} else if (_type == "separator") {
			widget = this.renderSeparator(uid);
		} else if (_type == "sub_report") {
			widget = this.renderSubReport(uid);
		} else if (_type == "page_number") {
			widget = this.renderPageNumber(uid);
		} else if (_type == "image") {
			/* Yet to implement */
		} else if (_type == "chart") {
			/* Yet to implement */
		} else {
			/* Unlikely */
		}
			
		this.dropZone.append(widget);
		
		if (this.dropZone.hasClass("page-header-widgets-holder")) {
			this.headerFields.push(uid);
			this.fields[this.dropZone.parent().attr("data-uid")].OPTIONS.fields.push(uid);						
		} else if (this.dropZone.hasClass("page-footer-widgets-holder")) {
			this.footerFields.push(uid);
			this.fields[this.dropZone.parent().attr("data-uid")].OPTIONS.fields.push(uid);
		}		
		
		if (_type == "record_table") {
			widget.find("div.drow").sortable({containment: 'document', update: this.reOrderColumns}).disableSelection();
		}
		
		this.fields[uid]["target"] = widget;
		
	};
	
	/**
	 * Render Static Text Widget
	 */
	this.renderStaticText = function(_uid) {
		
		let html = '<div class="leca-rd-widget static_text" data-uid="'+ _uid +'" data-width="'+ this.fields[_uid]["OPTIONS"].width +'" data-type="static_text" ';
			html += 'style="grid-area: '+ this.fields[_uid]["OPTIONS"].row_start +'/'+ this.fields[_uid]["OPTIONS"].column_start +'/'+ this.fields[_uid]["OPTIONS"].row_end +'/'+ this.fields[_uid]["OPTIONS"].column_end +';';				
			html += 'font-weight: '+ this.fields[_uid]["OPTIONS"].font_weight +'; font-style: '+ this.fields[_uid]["OPTIONS"].font_style +'; text-align: '+ this.fields[_uid]["OPTIONS"].alignment +';">';			
			html += this.spanifyChar(this.fields[_uid]["OPTIONS"].label);
			html += '</div>';
			
		return $(html);
		
	};
	
	/**
	 * Render Dynamic Text Widget
	 */
	this.renderDynamicText = function(_uid) {
		
		let html = '<div class="leca-rd-widget dynamic_text" data-uid="'+ _uid +'" data-width="'+ this.fields[_uid]["OPTIONS"].width +'" data-type="dynamic_text" ';	
			html += 'style="grid-area: '+ this.fields[_uid]["OPTIONS"].row_start +'/'+ this.fields[_uid]["OPTIONS"].column_start +'/'+ this.fields[_uid]["OPTIONS"].row_end +'/'+ this.fields[_uid]["OPTIONS"].column_end +';';	
			html += 'font-weight: '+ this.fields[_uid]["OPTIONS"].font_weight +'; font-style: '+ this.fields[_uid]["OPTIONS"].font_style +'; text-align: '+ this.fields[_uid]["OPTIONS"].alignment +';">';			
			html += this.spanifyChar("${"+this.fields[_uid]["OPTIONS"].handle+"}");			
			html += '</div>';
		
		return $(html);
		
	};
	
	/**
	 * Render Data Grid widget
	 */
	this.renderRecordTable = function(_uid) {
		
		if (this.fields[_uid]["OPTIONS"].has_footer == "yes") {			
			lineCount = 7;
		} else {
			lineCount = 4;
		}
		
		this.fields[_uid]["OPTIONS"].row_end = this.fields[_uid]["OPTIONS"].row_start + lineCount;
		
		let line = '',
			html = '<div class="leca-rd-widget record_table" data-uid="'+ _uid +'" data-width="'+ this.fields[_uid]["OPTIONS"].width +'" data-type="record_table" ';
		html += 'style="grid-area: '+ this.fields[_uid]["OPTIONS"].row_start +'/'+ this.fields[_uid]["OPTIONS"].column_start +'/'+ this.fields[_uid]["OPTIONS"].row_end +'/'+ this.fields[_uid]["OPTIONS"].column_end +';">';	
		for (let i = 0; i < this.fields[_uid]["OPTIONS"].width; i++) {
			line += '<span>-</span>';
		}		
		html += '<div class="header-wrapper">';
		html += '<div class="line">'+ line +'</div>';
		html += '<div class="header"><div class="drow" style="height: '+ this.gItemHeight +'px;"></div></div>';
		html += '<div class="line">'+ line +'</div>';
		html += '</div>';
		
		html += '<div class="content" style="height: '+ this.gItemHeight +'px;">'+ this.spanifyChar("Record Content") +'</div>';
		
		html += '<div class="footer-wrapper" style="display: '+ ((this.fields[_uid]["OPTIONS"].has_footer == "yes") ? "block" : "none") +';">';
		html += '<div class="line">'+ line +'</div>';
		html += '<div class="footer"><div class="drow" style="height: '+ this.gItemHeight +'px;"></div></div>';
		html += '<div class="line">'+ line +'</div>';
		html += '</div>';		
		html += '</div>';	
		
		return $(html);
		
	};

	/**
	 * Render Repeater widget
	 */
	this.renderSubReport = function(_uid) {
		
		let html = '<div class="leca-rd-widget sub_report" data-uid="'+ _uid +'" data-width="'+ this.fields[_uid]["OPTIONS"].width +'" data-type="sub_report" ';
		html += 'style="grid-area: '+ this.fields[_uid]["OPTIONS"].row_start +'/'+ this.fields[_uid]["OPTIONS"].column_start +'/'+ this.fields[_uid]["OPTIONS"].row_end +'/'+ this.fields[_uid]["OPTIONS"].column_end +'; text-align: center;">';	
		
		let report_name = (this.fields[_uid]["OPTIONS"].report != "") ? $("#leca-rd-report-list-select option[value="+ this.fields[_uid]["OPTIONS"].report +"]").text() : "Select a subreport from property section";

		html += '<div class="subreport-placeholder" style="height: '+ this.gItemHeight +'px;">'+ this.spanifyChar( "[ --- "+ report_name + " --- ]") +'</div>';		
		html += '</div>';	
		
		return $(html);
		
	};
		
	/**
	 * Render Place Holder widgte
	 */
	this.renderPageHeader = function(_uid) {
		
		let html = '<div class="leca-rd-widget page_header" data-uid="'+ _uid +'" data-width="'+ this.fields[_uid]["OPTIONS"].width +'" data-type="page_header" ';
		html += 'style="grid-area: '+ this.fields[_uid]["OPTIONS"].row_start +'/'+ this.fields[_uid]["OPTIONS"].column_start +'/'+ this.fields[_uid]["OPTIONS"].row_end +'/'+ this.fields[_uid]["OPTIONS"].column_end +';">';		
		html += '<div class="page-header-widgets-holder" style="grid-template-columns: repeat('+ ((parseInt(this.totalColumns, 10)) - (this.marginRight + this.marginLeft)) +', 1fr);"></div>';		
		html += '</div>';	

		return $(html);
		
	};
	
	/**
	 * Render Page Number widget
	 */
	this.renderPageFooter = function(_uid) {
		
		let html = '<div class="leca-rd-widget page_footer" data-uid="'+ _uid +'" data-width="'+ this.fields[_uid]["OPTIONS"].width +'" data-type="page_footer" ';
		html += 'style="grid-area: '+ this.fields[_uid]["OPTIONS"].row_start +'/'+ this.fields[_uid]["OPTIONS"].column_start +'/'+ this.fields[_uid]["OPTIONS"].row_end +'/'+ this.fields[_uid]["OPTIONS"].column_end +';">';					
		html += '<div class="page-footer-widgets-holder" style="grid-template-columns: repeat('+ ((parseInt(this.totalColumns, 10)) - (this.marginRight + this.marginLeft)) +', 1fr);"></div>';		
		html += '</div>';	

		return $(html);
		
	};
	
	/**
	 * Render Page Break widget
	 */
	this.renderSeparator = function(_uid) {
		
		let line = '',
			html = '<div class="leca-rd-widget record_table" data-uid="'+ _uid +'" data-width="'+ this.fields[_uid]["OPTIONS"].width +'" data-type="record_table" ';
		html += 'style="grid-area: '+ this.fields[_uid]["OPTIONS"].row_start +'/'+ this.fields[_uid]["OPTIONS"].column_start +'/'+ this.fields[_uid]["OPTIONS"].row_end +'/'+ this.fields[_uid]["OPTIONS"].column_end +';">';	
		for (let i = 0; i < this.fields[_uid]["OPTIONS"].width; i++) {
			line += '<span>-</span>';
		}
		
		html += '<div class="line">'+ line +'</div></div>';
		
		return $(html);
		
	};
	
	/**
	 * Render Page Number widget
	 */
	this.renderPageNumber = function(_uid) {
		
		var html = '<div class="leca-rd-widget page_number" data-uid="'+ _uid +'" data-width="'+ this.fields[_uid]["OPTIONS"].width +'" data-type="page_number" ';	
		html += 'style="grid-area: '+ this.fields[_uid]["OPTIONS"].row_start +' / '+ this.fields[_uid]["OPTIONS"].column_start +' / '+ this.fields[_uid]["OPTIONS"].row_end +' / '+ this.fields[_uid]["OPTIONS"].column_end +';';	
		html += 'font-weight: '+ this.fields[_uid]["OPTIONS"].font_weight +'; font-style: '+ this.fields[_uid]["OPTIONS"].font_style +'; text-align: '+ this.fields[_uid]["OPTIONS"].alignment +';">';	
		html += this.spanifyChar(this.fields[_uid]["OPTIONS"].label);
		html += '</div>';
		
		return $(html);
	};
	
	/**
	 * Render Image widget
	 */
	this.renderImage = function() {
	
	};
	
	/**
	 * Render Chart widget
	 */
	this.renderChart = function() {
		
	};
	
	/**
	 * Gives the initial meta for Widgets
	 */
	this.getInitMeta = function(_type) {
		
		let meta = {},
			_options = {},
			gWidth = this.totalColumns - (this.marginLeft + this.marginRight);
			
		if (_type === "page_header") {
			_options = {
				handle: "",
				width: gWidth,
				height: 5,				
				fields: []
			};
		} if (_type === "page_footer") {
			_options = {	
				handle: "",
				width: gWidth,
				height: 5,			
				fields: []
			};
		} if (_type === "separator") {
			_options = {	
				handle: "",
				width: gWidth,
				height: 1				
			};
		} else if (_type === "static_text") {
			_options = {				
				label: "Your Label",
				width: 20,
				height: 1,							
				padding_top: 0,
				padding_right: 0,
				padding_bottom: 0,
				repeat_field: false,
				padding_left: 0,
				alignment:"left",
				font_style: "normal",
				font_weight: "normal",
				text_decoration: "none",
				use_global_padding: true
								
			};
		} else if (_type === "dynamic_text") {
			_options = {				
				handle: "",
				width: 20,
				height: 1,
				label: "${}",
				padding_top: 0,
				padding_right: 0,
				padding_bottom: 0,
				repeat_field: false,
				padding_left: 0,				
				alignment : "left",
				font_style: "normal",
				font_weight: "normal",
				text_decoration: "none",
				stretch_with_overflow: true,
				remove_line_when_blank: true,
				use_global_padding: true								
			};
		} else if(_type === "record_table") {			
			_options = {				
				handle: "records",
				width: gWidth,
				height: 1,
				header: [[]],
				footer: [[]],
				columns: {},
				has_footer: "no"
			};
		} else if(_type === "sub_report") {
			_options = {				
				handle: "",				
				label: "Sub Report",
				width: gWidth,
				height: 1,
				report: ""	
			}
		} else if (_type === "page_number") {
			_options = {				
				width: 20,
				label: "Page 1",
				delimiter: ":",
				padding_top: 0,
				padding_right: 0,
				padding_bottom: 0,
				padding_left: 0,							
				repeat_field: false,
				alignment : "left",
				font_style: "normal",
				font_weight: "normal",
				text_decoration: "none",
				use_global_padding: true				
			}
		} else if(_type === "image") {
			_options = {				
				handle: "",
				label: "Img Alt Label",
				width: 20,
				height: 1,	
			}
		} else if(_type === "chart") {
			_options = {				
				handle: "",
				label: "Chart Label",
				width: 20,
				height: 1,	
			}
		}
		
		meta = {
			FIELD_TYPE: _type,
			IS_EDITABLE: true,
			IS_REMOVABLE: true,
			OPTIONS: _options	
		};
		
		return meta;
		
	};
	
	this.reRenderRecordTableBorder = function(_widget) {
		
		let lines = '', 
			width = parseInt(_widget.attr("data-width"));
			
		for (let i = 0; i < width; i++) {
			lines += '<span>-</span>';
		}	
			
		_widget.find("div.line").html(lines);		
		
	};
	
	this.addRecordTableColumn = function() {
		
		/* Calculate the width of the column */
		let _width = this.gItemWidth * 10;
		/* Generate a unique id for the column */
		let col_count = this.selectedRow.find(">div").length;
		/* Generate Unique ID */
		let uid = '_' + Math.random().toString(36).substr(2, 9);
		
		let me = this, size = 0, cWidth = 10, remWidth = 0;
		this.selectedRow.find("> div.dcolumn").each(function() {
			size += me.fields[me.currentUid]["OPTIONS"].columns[$(this).attr("data-uid")].width;
		});
		remWidth = (this.totalColumns - (this.marginLeft + this.marginRight)) - size;
		cWidth = remWidth > 10 ? 10 : remWidth;
		
		/* Assemble the meta */
		this.fields[this.currentUid]["OPTIONS"].columns[uid] = {
			width: cWidth,
			label: "COLUMN"+ (col_count + 1),
			handle: "",
			target: null,
			type: "column",
			visible: true,
			record_align: "left",
			column_align: "left",
			font_weight: "normal",
			font_style: "normal",
			text_decoration: "none",
			padding_top: 0,
			padding_right: 0,
			padding_bottom: 0,
			padding_left: 0,
			use_global_padding: true
		};
		if (this.selectedRow.parent().hasClass("header")) {
			this.fields[this.currentUid]["OPTIONS"].header[this.selectedRow.index()].push(uid);
		} else {
			this.fields[this.currentUid]["OPTIONS"].footer[this.selectedRow.index()].push(uid);
		}		
		this.renderRecordTableColumn(uid, this.fields[this.currentUid]["OPTIONS"].columns[uid]);
		
	};
	
	this.renderRecordTableColumn = function(_uid, _meta) {
		
		/* Resize handle */
		let resizeHandle = $('<span class="resize-handle"></span>');
		/* Column Label */
		let label = "";
		for (let i = 0; i < _meta.label.length; i++) {
			label += '<span>'+ _meta.label[i] +'</span>';
		}
		/* Column */		
		let column = $('<div class="dcolumn" data-uid="'+ _uid +'" data-width="'+ _meta.width +'" style="width: '+ (_meta.width * this.gItemWidth) +'px; text-align: '+ _meta.column_align +';">'+ label +'</div>');
		this.selectedRow.append(column);
		this.selectedRow.append(resizeHandle);		
		this.fields[this.currentUid]["OPTIONS"].columns[_uid].target = column;
		resizeHandle.css("left", (column.position().left + column.outerWidth()) - 2);		
		this.selectedRow.sortable('enable').sortable({cancel: '.resize-handle'});
		
	};
	
	this.reOrderColumns = function() {
		
		$("div.leca-rd-widget.record_table div.drow > span.resize-handle").remove();
		$("div.leca-rd-widget.record_table div.drow > div.dcolumn").each(function() {
			$(this).after($('<span class="resize-handle" style="left: '+ (($(this).position().left + $(this).outerWidth()) - 2) +'px;"></span>'));
		});
		
		if (lecaSystemObj.contextObj.currentUid && lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]) {
			/* Update the header meta */
			lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid].target.find("div.header > div.drow").each(function() {
				let rIndex = $(this).index();
				lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].header[$(this).index()] = [];
				$(this).find("> div.dcolumn").each(function() {
					lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].header[rIndex].push($(this).attr("data-uid"));
				});			
			});
						
			lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid].target.find("div.footer > div.drow").each(function() {
				let rIndex = $(this).index();
				lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].footer[$(this).index()] = [];
				$(this).find("> div.dcolumn").each(function() {
					lecaSystemObj.contextObj.fields[lecaSystemObj.contextObj.currentUid]["OPTIONS"].footer[rIndex].push($(this).attr("data-uid"));
				});			
			});
		}		
		
	};
	
	this.removeRecordTableColumn = function() {
		
		let cUid = this.selectedColumn.attr("data-uid");
		/* Remove from row Array */
		if (this.selectedRow.parent().hasClass("header")) {
			this.fields[this.currentUid]["OPTIONS"].header[this.selectedRow.index()].splice(this.fields[this.currentUid]["OPTIONS"].header[this.selectedRow.index()].indexOf(cUid), 1);
		} else {
			this.fields[this.currentUid]["OPTIONS"].footer[this.selectedRow.index()].splice(this.fields[this.currentUid]["OPTIONS"].footer[this.selectedRow.index()].indexOf(cUid), 1);
		}	
		/* Remove from columns object */
		delete this.fields[this.currentUid]["OPTIONS"].columns[cUid];
		/* Remove from the DOM */
		this.selectedColumn.next().remove();
		this.selectedColumn.remove();
		this.selectedColumn = null;
		
	};
	
	this.addRecordTableRow = function() {
		
		if (this.selectedRow) {
			
			let rEnd = parseInt(this.fields[this.currentUid].target.css("grid-row-end"), 10);
			/* Append the new row */
			let row = $('<div class="drow" style="height: '+ this.gItemHeight +'px;"></div>');
			if (this.selectedRow.parent().hasClass("header")) {
				this.fields[this.currentUid].target.find("div.header").append(row);
			} else {
				this.fields[this.currentUid].target.find("div.footer").append(row);
			}			
			/* Adjust the height */
			this.fields[this.currentUid].target.css("grid-row-end", (rEnd + 1));
			this.refreshSelector(this.fields[this.currentUid].target);
			//this.fields[this.currentUid]["OPTIONS"].header.push([]);
			row.sortable({containment: 'document', update: this.reOrderColumns}).disableSelection().sortable({ cancel: '.resize-handle' });
			
		}	
		
	};
	
	this.removeRecordTableRow = function(_index) {
		
		let rEnd = parseInt(this.fields[this.currentUid].target.css("grid-row-end"), 10);
		if (this.selectedRow.parent().hasClass("header")) {
			this.fields[this.currentUid]["OPTIONS"].header.splice(parseInt(_index, 10), 1);
			this.fields[this.currentUid].target.find("div.header > div:nth-child("+ (parseInt(_index, 10) + 1) +")").remove();
		} else {
			this.fields[this.currentUid]["OPTIONS"].footer.splice(parseInt(_index, 10), 1);
			this.fields[this.currentUid].target.find("div.footer > div:nth-child("+ (parseInt(_index, 10) + 1) +")").remove();
		}		
		/* Adjust the height */
		this.fields[this.currentUid].target.css("grid-row-end", (rEnd - 1));
		this.refreshSelector(this.fields[this.currentUid].target);
		
	};
	
	/**
	 * Duplicate the selected widget
	 */
	this.cloneWidget = function() { console.log("CLone widget called");
		
		if (this.currentUid && this.fields[this.currentUid]) {
			let pid = 0,
				uid = '_' + Math.random().toString(36).substr(2, 9),
				cloned = this.fields[this.currentUid].target.clone(true).off();
				
			this.fields[uid] = JSON.parse(JSON.stringify(this.fields[this.currentUid]));
			this.fields[uid].target = cloned;
			this.fields[uid].target.attr("data-uid", uid);
			if (typeof this.fields[uid]["OPTIONS"].handle !== "undefined") {
				this.fields[uid]["OPTIONS"].handle = "";
			}		
			
			if (this.fields[this.currentUid].target.parent().hasClass("page-header-widgets-holder")) {
				pid = this.fields[this.currentUid].target.parent().parent().attr("data-uid");
				this.fields[this.currentUid].target.parent().append(cloned);
				this.headerFields.push(uid);
				this.fields[pid].OPTIONS.fields.push(uid);	
			} else if (this.fields[this.currentUid].target.parent().hasClass("page-footer-widgets-holder")) {
				pid = this.fields[this.currentUid].target.parent().parent().attr("data-uid");
				this.fields[this.currentUid].target.parent().append(cloned);
				this.footerFields.push(uid);
				this.fields[pid].OPTIONS.fields.push(uid);
			} else {
				this.dpad.append(cloned);	
			}		
				
			this.fields[uid].target.trigger("click");
		}	
		
	};
	
	/**
	 * Remove the selected widget
	 */
	this.removeWidget = function() {
		
		if (this.currentUid && this.fields[this.currentUid]) {		
			
			if (this.fields[this.currentUid]["FIELD_TYPE"] == "page_header") {
				/* Enable Page Header widget as only single instance allowed */
				$('#leca-rd-widget-list a[data-type="page_header"]').removeClass("disabled");
				/* Remove child fields as well */
				for (let i = 0; i < this.headerFields.length; i++) {
					delete this.fields[this.headerFields[i]];	
				}
				this.headerFields = [];
			}
			if (this.fields[this.currentUid]["FIELD_TYPE"] == "page_footer") {
				/* Enable Page Footer widget as only single instance allowed */
				$('#leca-rd-widget-list a[data-type="page_footer"]').removeClass("disabled");
				/* Remove child fields as well */
				for (let i = 0; i < this.footerFields.length; i++) {
					delete this.fields[this.footerFields[i]];	
				}
				this.footerFields = [];
			}				
			
			/* If the field belongs to header or footer, then remove that entry as well */
			let pid = null,
				index = this.headerFields.indexOf(this.currentUid); 
			if (index !== -1) {
				this.headerFields.splice(index, 1);
				/* Also remove it from page header fields list */
				pid = this.fields[this.currentUid].target.parent().parent().attr("data-uid");
				index = this.fields[pid].OPTIONS.fields.indexOf(this.currentUid);
				if (index !== -1) {
					this.fields[pid].OPTIONS.fields.splice(index, 1);
				}
			}
			index = this.footerFields.indexOf(this.currentUid); 
			if (index !== -1) {
				this.footerFields.splice(index, 1);
				/* Also remove it from page footer fields list */
				pid = this.fields[this.currentUid].target.parent().parent().attr("data-uid");
				index = this.fields[pid].OPTIONS.fields.indexOf(this.currentUid);
				if (index !== -1) {
					this.fields[pid].OPTIONS.fields.splice(index, 1);
				}
			}
			
			let fid = this.fields[this.currentUid]["FIELD_ID"];
			this.fields[this.currentUid].target.remove();
			delete this.fields[this.currentUid];	
			this.currentUid = null;
			this.selector.hide();
			
			/* Show the property selector probe message */
			$("#leca-rd-sidebar-properties-panel > div").hide();
			$("#leca-rd-properties-empty-msg").show();		
			
			if (typeof fid !== 'undefined') {
				/* Update the DB */
				this.controller.request = this.controller.prepareRequest("/visualizer/rf/delete", this.controller.context, "REPORT_FIELD", 0, {FIELD_ID: fid});			 
				this.controller.dock();	
			}				
		}
		
	};
	
	/**
	 * Handles the widget selector navigation events
	 */
	this.navigateWidgetSelector = function(_code) {
		
		let index = 0,
			cIndex = 0,
			uids = Object.keys(this.fields);
			
		if (this.currentUid && uids.length > 1) {
			cIndex = uids.indexOf(this.currentUid);
			if (cIndex !== -1) {
				if (_code == 37 || _code == 38) {
			    	/* Top - Left */					
					index = (cIndex >= 1) ? --cIndex : (uids.length - 1);
					this.fields[uids[index]].target.trigger("click");
			    } else if(_code == 39 || _code == 40) {
			    	/* Bottom - Right */			    	
			    	index = (cIndex < (uids.length - 1)) ? ++cIndex : 0;
			    	this.fields[uids[index]].target.trigger("click");
			    } else {
			    	/* Unlikely */
			    }			
			}			
		}	
		
	};
	
	/**
	 * 
	 */
	this.loadWidgetProperties = function(_target) {
		
		//$("#leca-rd-property-panel-tab-item").trigger("click");
		$("#leca-rd-sidebar-properties-panel > div").hide();
		let section = $("#leca-rd-widget-"+ _target.attr("data-type") +"-properties");
		/* Reset Properties */
		this.resetPropertySection(section);
		/* Load properties */
		this.loadPropertySection(section);
		/* Show the section */
		section.show();
		
	};
	
	this.loadWidgetSectionProperties = function(_type) {
		
		var me = this,
			key = "",
			section = $("#leca-rd-"+ _type +"-section-style");
		if (this.fields[this.currentUid].target.find(">div.active").hasClass("label")) {
			key = "label_style";
		} else {
			key = "value_style";
		}		
		section.find(".leca-rd-unit-field").each(function() {			
			/* And it's going to be selector field type only */
			$(this).find("label").removeClass();
			if ($(this).attr("data-key") == "text_decoration") {				
				/* This is a multi choice selector */
				if (me.fields[me.currentUid]["OPTIONS"][key].font_weight == "bold") {
					$(this).find("label[data-option='bold']").addClass("selected");
				}
				if (me.fields[me.currentUid]["OPTIONS"][key].font_style == "italic") {
					$(this).find("label[data-option='italic']").addClass("selected");
				}
				if (me.fields[me.currentUid]["OPTIONS"][key].text_decoration == "underline") {
					$(this).find("label[data-option='underline']").addClass("selected");
				}
				/* If nothing is selected, then select the none option */
				if ($(this).find("label.selected").length == 0) {
					$(this).find("label:first").addClass("selected");
				}
			} else {
				$(this).find("label[data-option='"+ me.fields[me.currentUid]["OPTIONS"][key].alignment +"']").addClass("selected");
			}
		});
		section.show();
		
	};
	
	this.loadRecordTableColumnProperties = function(_uid) {
		
		$("#leca-rd-sidebar-properties-panel > div").hide();
		let me = this,
			section = $("#leca-rd-widget-record_table_column-properties");
		$("#leca-rd-widget-properties-section-label").html("Column");
		this.resetPropertySection(section);		
		if (this.currentUid && this.fields[this.currentUid] && this.fields[this.currentUid]["OPTIONS"].columns[_uid]) {
			let meta = this.fields[this.currentUid]["OPTIONS"].columns[_uid];			
			section.find(".leca-rd-unit-field").each(function() {
				if (meta[$(this).attr("data-key")]) {
					if ($(this).attr("data-type") === "text" || 
						$(this).attr("data-type") === "number" ||
						$(this).attr("data-type") === "email" ||
						$(this).attr("data-type") === "color" ||
						$(this).attr("data-type") === "password" ||
						$(this).attr("data-type") === "select") {
						
						$(this).val(meta[$(this).attr("data-key")]);
						
					} else if ($(this).attr("data-type") === "radio") {
						
					} else if($(this).attr("data-type") === "checkbox") {
						$(this).prop('checked', me.fields[me.currentUid]["OPTIONS"].columns[_uid][$(this).attr("data-key")]);		
					}  else if ($(this).attr("data-type") === "selector") {						
						$(this).find("label").removeClass();
						$(this).find("label").removeClass();						
						$(this).find("label[data-option='"+ me.fields[me.currentUid]["OPTIONS"].columns[_uid][$(this).attr("data-key")] +"']").addClass("selected");
					} else {
						/* Unlikely */
					}
				}
			});
		}		
		/* Show the section */
		section.show();
		
	};
	
	this.highlightWidget = function(_target) {		
		if (_target.length > 0) {
			this.highlighter.css("top", _target.position().top +"px");
			this.highlighter.css("left", _target.position().left +"px");
			this.highlighter.height(_target.outerHeight());
			this.highlighter.width(_target.outerWidth());
			$("#leca-rd-highlight-widget-name").html(this.rawWidgetList[_target.attr("data-type")]);
			this.highlighter.show();	
		}			
	};
	
	this.selectWidget = function(_target) {
		
		if (_target.length == 0) return;
		
		/* Check if any widget selected and its in Edit mode */
		if (this.currentUid && (this.currentUid != _target.attr("data-uid"))) {
			
			/* Don't remove edit class for page header or footer */
			if (this.fields[this.currentUid] && this.fields[this.currentUid].target 
				&& this.fields[this.currentUid].target.closest(".page_header").length == 0 
				&& this.fields[this.currentUid].target.closest(".page_footer").length == 0) {
				/* Safer side remove the Edit Class */
				this.fields[this.currentUid].target.removeClass("edit");					
			}
			
			/* Reset the edit button */
			$("#leca-rd-widget-edit-btn").removeClass("active");
			
			if (_target.hasClass("page_header")
				|| _target.hasClass("page_footer")) {
				if (_target.hasClass("edit")) {
					$("#leca-rd-widget-edit-btn").addClass("active");
				}
			}		
			
			/* Also reset the selected column, if it is data grid */
			if (this.fields[this.currentUid] && this.fields[this.currentUid].target.attr("data-type") == "record_table") {
				this.selectedColumn = null;
				this.fields[this.currentUid].target.find("div.dcolumn.selected").removeClass("selected");
			}
		}		
		/* If it is Data Grid then check for any selected column */
		if (_target.attr("data-type") == "record_table" && _target.hasClass("edit")) {
			return;
		}

		/* If the target widget is Page Break then we can hide edit and copy buttons */
		if (_target.attr("data-type") == "page_break") {
			$("#leca-rd-widget-edit-btn").hide();
			$("#leca-rd-widget-clone-btn").hide();
		} else {
			$("#leca-rd-widget-edit-btn").show();
			$("#leca-rd-widget-clone-btn").show();
		}
		this.currentUid = _target.attr("data-uid");			
		this.refreshSelector(_target);
		$("#leca-rd-selector-widget-name").html(this.rawWidgetList[_target.attr("data-type")]);
		$("#leca-rd-widget-properties-section-label").html(this.rawWidgetList[_target.attr("data-type")]);
		this.selector.show();	
		
		if (this.currentUid) {
			/* Hide the property selector probe message */
			$("#leca-rd-properties-empty-msg").hide();	
		}		
	};
	
	this.refreshSelector = function(_target) {
		
		this.selector.css("top", _target.position().top +"px");
		this.selector.css("left", _target.position().left +"px");
		this.selector.height(_target.outerHeight());
		this.selector.width(_target.outerWidth());
		
	};
	
	this.setLayoutGrid = function() {	
		
		let cPage = $("").val();		
		if (cPage && this.pageSizes[cPage]) {
			this.pageWidth = this.pageSizes[cPage].width;
			this.pageHeight = this.pageSizes[cPage].height; 
		} else {
			this.pageWidth = 8;
			this.pageHeight = 11;
		}	
		/* Main page designer */
		this.dpad.css("height", ((this.pageHeight / this.pageWidth) * this.dpad.width()) +"px");	
		this.dpad.css("grid-template-rows", "repeat("+ parseInt(this.totalRows, 10) +", 1fr)");
		this.dpad.css("grid-template-columns", "repeat("+ parseInt(this.totalColumns, 10) +", 1fr)");
				
	};
	
	this.spanifyChar = function(_val) {
		let i = 0, spans = "";
		for (i = 0; i < _val.length; i++) {
			spans += '<span>'+ _val[i] +'</span>';
		}
		return spans;
	};
	
	this.getCharacterCount = function() {
		if (this.pageWidth > 0) {
			return (Math.round(this.pageWidth) * this.cpi);
		}
		return 0;
	};
	
	this.getLineCount = function() {
		if (this.pageHeight > 0) {
			return (Math.round(this.pageHeight) * this.lpi);
		}
		return 0;
	};
	
	this.getInchToPixel = function(_inch) {
		/* 1 inch equals 96 pixel */
		return (_inch * 96);
	};
	
	this.loadReportList = function() {
		let rSelect = $("#leca-rd-report-list-select");
		rSelect.html("");
		
		/* Since we know the report list column position, we are hard coading it here */
		for (let i = 0;i < this.grids.report_grid.records.length; i++) {
			if (this.activeRecord["REPORT_ID"] != this.grids.report_grid.records[i][0]) {
				rSelect.append($('<option value="'+ this.grids.report_grid.records[i][0] +'">'+ this.grids.report_grid.records[i][1] +'</option>'));	
			}			
		}
		
	};
	
	this.loadPropertySection = function(_target, _fmeta) {
		
		if (this.currentUid && this.fields[this.currentUid]) {
			let me = this;			
			
			_target.find(".leca-rd-unit-field").each(function() {
			
				if ($(this).attr("data-type") == "text" || $(this).attr("data-type") == "number") {				
					$(this).val( me.fields[me.currentUid]["OPTIONS"][$(this).attr("data-key")] );				
				} else if ($(this).attr("data-type") == "checkbox") {		
					$(this).prop('checked', me.fields[me.currentUid]["OPTIONS"][$(this).attr("data-key")]);				
				} else if ($(this).attr("data-type") == "select") {
									
					if ($(this).attr("data-key") == "is_editable") {
						if (me.fields[me.currentUid].IS_EDITABLE) {
							$(this).val("yes");
						} else {
							$(this).val("no");
						}
					} else if($(this).attr("data-key") == "is_removable") {
						if (me.fields[me.currentUid].IS_REMOVABLE) {
							$(this).val("yes");
						} else {
							$(this).val("no");
						}
					} else if ($(this).attr("data-key") == "repeat_field"
					 	|| $(this).attr("data-key") == "stretch_with_overflow" || $(this).attr("data-key") == "remove_line_when_blank") {
						
						if ($(this).attr("data-key") == "repeat_field") {
							/* Check whether this is a header field */
							if(me.fields[me.currentUid].target.parent().hasClass("page-header-widgets-holder")) {
								$(this).parent().show();
							}
						}
																
						if (me.fields[me.currentUid]["OPTIONS"][$(this).attr("data-key")]) {
							$(this).val("yes");
						} else {
							$(this).val("no");
						}											
					} else {						
						$(this).val(me.fields[me.currentUid]["OPTIONS"][$(this).attr("data-key")]);
					}
									
				} else if ($(this).attr("data-type") == "selector") {
					
					/* Reset the selector */
					$(this).find("label").removeClass("selected");
					
					/* This is a multi choice selector */				
					if ($(this).attr("data-key") == "text_decoration") {
								
						$(this).find("label:first").removeClass("selected");
											
						if (me.fields[me.currentUid]["OPTIONS"].font_weight == "bold") {
							$(this).find("label[data-option='bold']").addClass("selected");
						}
						if (me.fields[me.currentUid]["OPTIONS"].font_style == "italic") {
							$(this).find("label[data-option='italic']").addClass("selected");
						}
						if (me.fields[me.currentUid]["OPTIONS"].text_decoration == "underline") {
							$(this).find("label[data-option='underline']").addClass("selected");
						}
						
						/* If nothing is selected, then select the none option */
						if (me.fields[me.currentUid]["OPTIONS"].font_weight != "bold" 
							&& me.fields[me.currentUid]["OPTIONS"].font_style != "italic" 
							&& me.fields[me.currentUid]["OPTIONS"].text_decoration != "underline") {
							$(this).find("label:first").addClass("selected");
						}								
						
					} else {			
						$(this).find("label[data-option='"+ me.fields[me.currentUid]["OPTIONS"].alignment +"']").addClass("selected");						
					}					
					
				} else {
					/* Safe to ignore */				
				}
							
			});
			
		}
		
	};
	
	this.resetPropertySection = function(_target) {
		
		_target.find(".leca-rd-unit-field").each(function() {
			if ($(this).attr("data-type") === "text" || 
				$(this).attr("data-type") === "number" ||
				$(this).attr("data-type") === "email" ||
				$(this).attr("data-type") === "color" ||
				$(this).attr("data-type") === "password") {
				$(this).val("");
			} else if ($(this).attr("data-type") === "radio" || 
					$(this).attr("data-type") === "checkbox") {
				$(this).prop("checked", false);
			} else if ($(this).attr("data-type") === "select") {
				$(this).val($(this).find("option:first").val());
			} else if ($(this).attr("data-type") === "selector") {
				$(this).find("label").removeClass();
				$(this).find("input").prop("checked", false);
				$(this).find("label:first").addClass("selected");
				$(this).find("label:first").prop("checked", true);
			} else {
				/* Unlikely */
			}
			
			if ($(this).attr("data-key") == "repeat_field") {
				$(this).parent().hide();
			}
			
		});
		
	};
	
	this.handleRightClick = function(_e, _target, _type) {
		
		if (this.contextMenu) {
			this.contextMenu.remove();
			this.contextMenu = null;
		}
		let me = this, uid = 0, size = 0, isFull = false, parent = null;
		this.contextMenu = '<div id="leca-rd-context-menu-box" style="top:'+ (_e.pageY) +'px; left:'+ (_e.pageX) +'px;">';
		
		/* If the target is row or column then make sure the row is not full */
		if (_type === "record_table_row" || _type === "record_table_column"){
			this.selectedRow.find("> div.dcolumn").each(function() {
				size += me.fields[me.currentUid]["OPTIONS"].columns[$(this).attr("data-uid")].width;
			});
			if (size > (this.totalColumns - (this.marginLeft + this.marginRight)) - 2) {
				isFull = true;
			}
		}
		
		if (_type === "dpad" || _type === "page_header" || _type === "page_footer") {
			let widgets = [];
			$("#leca-rd-widget-list > a").each(function() {
				if (!$(this).hasClass("disabled")) {
					widgets.push({"type": $(this).attr("data-type"), "text": $(this).html()});
				}
			});
			for (let i = 0; i < widgets.length; i++) {
				this.contextMenu += '<a href="#" data-action="insert" data-type="'+ widgets[i].type +'">'+ widgets[i].text +'</a>';
			}
		} else if (_type === "record_table_row") {		
			if (!isFull) {
				this.contextMenu += '<a href="#" data-uid="'+ this.currentUid +'" data-action="add_column"><i class="fa fa-plus"></i>Column</a>';
			}			
			this.contextMenu += '<a href="#" data-uid="'+ this.currentUid +'" data-action="add_row"><i class="fa fa-plus"></i>Row</a>';			
			if ($(_e.target).parent().find("> div.drow").length > 1) {
				this.contextMenu += '<a href="#" data-uid="'+ this.currentUid +'" data-index="'+ _target.index() +'" data-action="delete_row"><i class="fa fa-times"></i>Delete Row</a>';
			}
		} else if (_type === "record_table_column") {
			if (!isFull) {
				this.contextMenu += '<a href="#" data-uid="'+ this.currentUid +'" data-action="add_column"><i class="fa fa-plus"></i>Column</a>';
			}
			this.contextMenu += '<a href="#" data-uid="'+ this.currentUid +'" data-action="delete_column"><i class="fa fa-times"></i>Delete Column</a>';	
		} else if (_type === "table") {
			this.contextMenu += '<a href="#" data-uid="'+ this.currentUid +'" data-action="add_table_column"><i class="fa fa-plus"></i>Add Column</a>';
		} else {
			/* Unlikely */
		}
				
		this.contextMenu += '</div>';
		this.contextMenu = $(this.contextMenu);
		$('body').append(this.contextMenu);
		
	};
	
	this.handleContextMenu = function(_menu) {	
		
		if (_menu.attr("data-action") == "insert") {
			this.dropWidget(_menu.attr("data-type"));
		} else if (_menu.attr("data-action") == "add_column") {
			this.addRecordTableColumn();
		} else if (_menu.attr("data-action") == "add_row") {
			this.addRecordTableRow();
			if (this.selectedRow.parent().hasClass("header")) {
				/* Push the row into the header meta */
				this.fields[this.currentUid]["OPTIONS"].header.push([]);
			} else {
				/* Push the row into the footer meta */
				this.fields[this.currentUid]["OPTIONS"].footer.push([]);
			}			
		} else if (_menu.attr("data-action") == "delete_row") {
			this.removeRecordTableRow(_menu.attr("data-index"));
		} else if (_menu.attr("data-action") == "delete_column") {
			this.removeRecordTableColumn();
		} else if (_menu.attr("data-action") == "add_table_column") {
			
		}
		if (this.contextMenu) {
			this.contextMenu.remove();
			this.contextMenu = null;
		}	
		
	};
	
	this.handleDelimiterResize = function(e) {
		
		let diffX = e.pageX - this.startPosX;			
		if (Math.abs(diffX) > this.gItemWidth) {
			if (diffX > 0) {		
				this.resizeDynamicTextSections("right", $(e.target));
			} else {
				this.resizeDynamicTextSections("left", $(e.target));
			}
			this.startPosX = e.pageX;
		}	
		
	};
	
	this.resizeDynamicTextSections = function(_dir, _widget) { 	
		
		_widget = _widget.is("span") ? _widget.closest("div.leca-rd-widget") : _widget		
		let wWidth = parseInt(_widget.attr("data-width"), 10),
			lWidth = parseInt(_widget.find("> div.label").attr("data-width"), 10),
			vWidth = parseInt(_widget.find("> div.value").attr("data-width"), 10);
					
		if (_dir == "left") {			
			_widget.find("> div.value").width((vWidth + 1) * this.gItemWidth).attr("data-width", (vWidth + 1));
			_widget.find("> div.label").width((lWidth - 1) * this.gItemWidth).attr("data-width", (lWidth - 1));
			this.fields[this.currentUid]["OPTIONS"].label_style.width = (lWidth - 1);
			this.fields[this.currentUid]["OPTIONS"].value_style.width = (vWidth + 1);
		} else if (_dir == "right") {		
			_widget.find("> div.label").width((lWidth + 1) * this.gItemWidth).attr("data-width", (lWidth + 1));
			_widget.find("> div.value").width((vWidth - 1) * this.gItemWidth).attr("data-width", (vWidth - 1));
			this.fields[this.currentUid]["OPTIONS"].label_style.width = (lWidth + 1);
			this.fields[this.currentUid]["OPTIONS"].value_style.width = (vWidth - 1);
		}
		
	};
	
	this.handleMouseMove = function(e) {
		
		if ($(e.target).hasClass("leca-rd-widget")) {			
			if (e.offsetX < 10 || e.offsetX + 10 > $(e.target).innerWidth() || e.offsetY < 5 || e.offsetY + 5 > $(e.target).innerHeight()) {	
				if (!this.isMouseDown) {
			        if (e.offsetX > -1 && e.offsetX < 10){
			        	this.cursor = "left";
			        	document.body.style.cursor = "col-resize";
					} else if (e.offsetX < $(e.target).innerWidth() && e.offsetX - 1 > ($(e.target).innerWidth() - 10)) {
						this.cursor = "right";
						document.body.style.cursor = "col-resize";
					} else if (e.offsetY > -1 && e.offsetY < 5) {
						this.cursor = "top";
						document.body.style.cursor = "row-resize";
					} else if (e.offsetY < $(e.target).innerHeight() && e.offsetY > ($(e.target).innerHeight() - 5)) {						
						this.cursor = "bottom";
						document.body.style.cursor = "row-resize";
					} else {
						/* Unlikely */
					}
				}
		    } else {
		    	if (!this.isMouseDown) {
		    		this.cursor = "";
		    		document.body.style.cursor = "move";
		    	}
		    }
		} else {
			if (!this.isMouseDown && !this.isResizeMode) {
				this.cursor = "";
				document.body.style.cursor = "auto";
			}			
		}	
		if (this.currentItem) {
			if (this.isMouseDown && !this.isResizeMode) {	
				this.dpad.addClass("panning");
				this.handleWidgetPan(e);
			} else if(this.isMouseDown && this.isResizeMode) {
				this.handleWidgetResize(e);
			}
		}		
		
	};
	
	this.handleWidgetResize = function(e) {
		
		let rect,
			changed = false,
			diffX = (e.pageX - this.startPosX),
			diffY = (e.pageY - this.startPosY),
			rowS = parseInt(this.currentItem.css("grid-row-start"), 10),
			rowE = parseInt(this.currentItem.css("grid-row-end"), 10),
			colS = parseInt(this.currentItem.css("grid-column-start"), 10),
			colE = parseInt(this.currentItem.css("grid-column-end"), 10);
		
		/* When the resize happening prior to select, then the following statement is must */
		let uid = this.currentItem.attr("data-uid");
		
		if (this.cursor == "left") {			
			if(Math.abs(diffX) > this.gItemWidth) {
				changed = true;				
				colS = (diffX > 0) ? (colS + 1) : (colS - 1);	
				colS = (colS >= colE) ? (colS - 1) : colS;
				colS = (colS < (this.marginLeft + 1)) ? (this.marginLeft + 1) : colS;
			}
		} else if(this.cursor == "right") {			
			if(Math.abs(diffX) > this.gItemWidth) {
				changed = true;				
				colE = (diffX > 0) ? (colE + 1) : (colE - 1);	
				colE = (colE <= colS) ? (colE + 1) : colE;
				colE = (colE > (this.totalColumns + 1) - this.marginRight) ? ((this.totalColumns + 1) - this.marginRight) : colE;
			}
		} else if(this.cursor == "top") {
			if(Math.abs(diffY) > this.gItemHeight) {
				changed = true;				
				rowS = (diffY > 0) ? (rowS + 1) : (rowS - 1);	
				rowS = (rowS >= rowE) ? (rowS - 1) : rowS;
				rowS = (rowS < (this.marginTop + 1)) ? (this.marginTop + 1) : rowS;
			}
		} else if(this.cursor == "bottom") {
			if(Math.abs(diffY) > this.gItemHeight) {
				changed = true;				
				rowE = (diffY > 0) ? (rowE + 1) : (rowE - 1);	
				rowE = (rowE <= rowS) ? (rowE + 1) : rowE;
				rowE = (rowE > (this.totalRows + 1) - this.marginBottom) ? ((this.totalRows + 1) - this.marginBottom) : rowE;
			}
		}
		if (changed) {
			/* Resizing */
			this.currentItem[0].style.gridArea = rowS +" / "+ colS +" / "+ rowE +" / "+ colE;
			rect = this.currentItem[0].getBoundingClientRect();	
			this.startPosX = (this.cursor == "left" || this.cursor == "top") ? rect.left + this.gItemWidth : rect.right - this.gItemWidth;
			this.startPosY = (this.cursor == "left" || this.cursor == "top") ? rect.top + this.gItemHeight : rect.bottom - this.gItemHeight;	
			
			/* Update highlighter and selector */
			this.highlightWidget(this.currentItem);
			this.refreshSelector(this.currentItem);		
			
			if (uid && this.fields[uid]) {
				/* Update the postion properties */
				this.fields[uid]["OPTIONS"]["row_end"] = rowE;
				this.fields[uid]["OPTIONS"]["row_start"] = rowS;
				this.fields[uid]["OPTIONS"]["column_end"] = colE;
				this.fields[uid]["OPTIONS"]["column_start"] = colS;
				/* Update the width attr */				
				this.currentItem.attr("data-width", (colE - colS));
				this.fields[uid]["OPTIONS"]["width"] = (colE - colS);
				if (this.fields[uid].FIELD_TYPE == "record_table") {
					this.reRenderRecordTableBorder(this.currentItem);
				} else if (this.fields[uid].FIELD_TYPE == "page_header" || this.fields[uid].FIELD_TYPE == "page_footer") {						 			
					/* Update the grid template */					
					let width = this.currentItem.width(),
						height = this.currentItem.height(),
						holder = this.fields[uid].FIELD_TYPE == "page_header" ? ".page-header-widgets-holder" : ".page-footer-widgets-holder";					
							
					this.currentItem.find(holder).css("grid-template-columns", "repeat("+ Math.ceil(width / this.gItemWidth) +", 1fr)");
					this.currentItem.find(holder).css("grid-template-rows", "repeat("+ Math.ceil(height / this.gItemHeight) +", 1fr)");					
				}				
			}		
		}
		
	};
	
	this.handleWidgetPan = function(e) {
		
		if ($(e.target).hasClass("leca-rd-widget")) {
			this.currentRow = Math.ceil((this.currentItem.position().top + e.offsetY) / this.gItemHeight);
			this.currentColumn = Math.ceil((this.currentItem.position().left + e.offsetX) / this.gItemWidth);
		} else {
			this.currentRow = Math.ceil(e.offsetY / this.gItemHeight);
			this.currentColumn = Math.ceil(e.offsetX / this.gItemWidth);
		}
		
		let isNested = false;
		
		if (this.currentItem.parent().hasClass("page-header-widgets-holder") || this.currentItem.parent().hasClass("page-footer-widgets-holder")) {
			isNested = true;
		}
		
		let mLeft = isNested ? 0 : this.marginLeft;			
		
		let rowS = Math.max((this.marginTop + 1), this.currentRow - this.cwTopRatio),
			rowE = Math.min(((this.totalRows + 1) - this.marginBottom), this.currentRow + this.cwBottomRatio),
			colS = Math.max((mLeft + 1), this.currentColumn - this.cwLeftRatio),
			colE = Math.min(((this.totalColumns + 1) - this.marginRight), this.currentColumn + this.cwRightRatio);
		
		/* When the paning happening prior to select, then the following statement is must */
		let uid = this.currentItem.attr("data-uid");						
		if (uid && this.fields[uid]) {			
			/* Update the postion properties */
			this.fields[uid]["OPTIONS"]["row_start"] = rowS;
			this.fields[uid]["OPTIONS"]["row_end"] = rowE;
			this.fields[uid]["OPTIONS"]["column_start"] = colS;
			this.fields[uid]["OPTIONS"]["column_end"] = colE;
			
			/* Panning */
			this.currentItem[0].style.gridArea = rowS +" / "+ colS +" / "+ rowE +" / "+ colE;
				
			/* Update width attr */
			this.currentItem.attr("data-width", (this.fields[uid]["OPTIONS"]["column_end"] - this.fields[uid]["OPTIONS"]["column_start"]));
			this.fields[uid]["OPTIONS"]["width"] = (this.fields[uid]["OPTIONS"]["column_end"] - this.fields[uid]["OPTIONS"]["column_start"]);
			if (this.fields[uid].FIELD_TYPE == "record_table") {
				this.reRenderRecordTableBorder(this.currentItem);
			}
		}
		
		/* Update highlighter and selector */
		this.highlightWidget(this.currentItem);
		this.refreshSelector(this.currentItem);
		
	};
	
	this.prepareReportMeta = function(_fields, _lines) {
		
		let keys = Object.keys(_fields);
		for (let i = 0; i < keys.length; i++) {
			_fields[keys[i]].OPTIONS = JSON.parse(_fields[keys[i]].OPTIONS); 
		}
		for (let i = 0; i < _lines.length; i++) {
			_lines[i].OPTIONS = JSON.parse(_lines[i].OPTIONS);
		}
		this.lines = _lines;			
		this.fields = _fields;
		
	};
	
	/**
	 * Called by the controller, whenever there is an Ajax Response for this context
	 */
	this.handleResponse = function(_req, _res) {
		
		if(_req.action === "/visualizer/rf/list" && _req.task === "USER_LIST") {
			this.loadUserList(_res.payload);
			/* Prepare the request object */
			this.controller.request = this.controller.prepareRequest("/visualizer/rf/list", this.controller.context, "BEAN", 0, {});
			/* Initiate the request */
			this.controller.dock();
		} else if (_req.action === "/visualizer/rf/list" && _req.task === "BEAN") {
			this.loadBeanList(_res.payload);			
			this.doArchive();
		} else if(_req.action === "/visualizer/rf/list" && _req.task === "METHOD") {
			this.loadMethghodList(_req.payload["CLASS"], _res.payload);
		} else if (_req.action === "/visualizer/rf/list" && _req.task === "REPORTS") {
			this.grids["report_grid"] = new lecaSystemGrid( this, this.reportGridView, this.config.archive.grids.report, "report_grid" );
			/* Inflate the grid */
			this.grids["report_grid"].renderDataGrid();
			/* Load records */
			let cols = _res.payload.splice( 0, 1 );
			this.grids["report_grid"].loadGrid( cols[0], _res.payload );
		} else if(_req.action === "/visualizer/rf/create" && _req.task === "REPORTS") {
			this.controller.flashAlert(_req.payload.REPORT_NAME +" has been saved.!");
			this.controller.doCancel();
		} else if(_req.action === "/visualizer/rf/update" && _req.task === "REPORTS") {
			this.controller.flashAlert(_req.payload.REPORT_NAME +" has been updated.!");
			this.activeRecord["REPORT_NAME"] = _req.payload.REPORT_NAME;
		} else if(_req.action === "/visualizer/rf/delete" &&  _req.task === "REPORTS") {
			this.controller.flashAlert(_req.payload.REPORT_NAME +" has been deleted.!");
			this.controller.doCancel();
		} else if(_req.action === "/visualizer/rf/list" && _req.task === "REPORT_TEMPLATES") {
			this.loadTemplates(_res.payload);
		} else if (_req.action === "/rf/create" && _req.task === "REPORT_TEMPLATES") {
			$("div.leca-model-dialog").remove();
			$("select.leca-report-template-selector option:last").before('<option value="'+ _res.payload +'">'+ _req.payload.TEMPLATE_NAME +'</option>');
			$("select.leca-report-template-selector option:last").prev().attr("selected", "selected");
			$("select.leca-report-template-selector").trigger("change");			
		} else if (_req.action === "/visualizer/rf/update" && _req.task === "REPORT_TEMPLATES") {
			
		} else if (_req.action === "/visualizer/rf/get" && _req.task === "REPORT_TEMPLATE") {
			this.prepareReportMeta(_res.payload.FIELDS, _res.payload.LINES);
			this.activeReportTemplate = _res.payload.TEMPLATE;		
			this.loadTemplate();
		} else if (_req.action === "/visualizer/rf/delete" && _req.task === "REPORT_FIELD") {
			this.updateTemplate();
		}
		
	};
	
	/**
	 * Called by the controller, whenever an exception while doing Ajax on behalf of this context
	 */
	this.handleErrorResponse = function(_req, _res) {
		
	};

};