var lecaSystemGrid = function( _cobj, _container, _meta, _name ) { 
	
	/* Container element where this grid instance will render the actual data grid */
	this.container = _container;
	/* Parent object to which the grid is being rendered */
	this.contextObj = _cobj;
	/* Meta data of this grid */
	this.meta = _meta;	
	/* Column list from server */
	this.columns = [];
	/* Name of this grid object - used to refer */
	this.name = _name;
	/* Used to store the column positions */
	this.positions = {};
	/* Holds the reference object of grid's header table element */
	this.gridHeader = null;
	/* Holds the reference object of actual grid table element */
	this.gridTable = null;
	/* Holds the grid records ( the data, which is about to be rendered ) */
	this.records = null;
	/* Holds the total number of pages loaded by the scroll events ( always <= this.totalPages ) */
	this.currentPage = 1;
	/* Holds the currently visible page ( If user has scrolled to some where ) */
	this.currentVisiblePage = 1;
	/* Total number of pages count - used for Lazy Loading Mechanism */
	this.totalPages = 0;
	/* Record per page - used for Lazy Loading Mechanism */
	this.recordsPerPage = 100;
	/* Holds the current height of fully loaded single record page's height 
	 * Used for Lazy Loading Performance Boosting */
	this.recordPageHeight = 1;
	/* Starting offset index of the records ( Used in Lazy Load ) */
	this.startIndex = 0;
	/* Ending offset index of the records ( Used in Lazy Load ) */
	this.endIndex = this.recordsPerPage;
	/* Flag for whether the grid has the full records loaded */
	this.isEndReached = false;
	/* Used to track scroll direction */
	this.lastScrollPos = 0;
	/**/
	this.filters = {};
	/**/
	this.entityMap = {
		'&': '&amp;',
		'<': '&lt;',
		'>': '&gt;',
		'"': '&quot;',
		"'": '&#39;',
		'/': '&#x2F;',
		'`': '&#x60;',
		'=': '&#x3D;'
	};
	
	/* Renders the skeleton structure for the Data Grid */
	this.renderDataGrid = function() { 
		var html = "",
			style = "",
			dataGrid = $( '<div class="leca-data-grid leca-data-grid-container"></div>' );		
		/* Clear the parent container */
		this.container.html( "" );		
		/* Grid header */
		this.gridHeader = $( '<div class="leca-grid-header-container"></div>' );
		/* Start of header row */
		html += '<div class="leca-data-grid-header '+ this.meta.width +'"><div class="leca-data-grid-row">';		
		/* Prepare columns */
		for( var i = 0; i < this.meta.columns.length; i++ ) {
			style = "";			
			if ( typeof this.meta.columns[i].width !== 'undefined' && this.meta.columns[i].width != "" ) {
				style = "width: " + this.meta.columns[i].width +"%;";
			}
			if ( typeof this.meta.columns[i].align !== 'undefined' ) {
				style += "text-align: " + ( ( this.meta.columns[i].align != "" ) ? this.meta.columns[i].align : "left;" );
			}		
			if( this.meta.columns[i].filterable ) {
				html += '<div data-column="'+ this.meta.columns[i].key +'" class="leca-data-grid-cell leca-grid-filterable" style="'+ style +'">';			
				html += '<span class="leca-grid-label">'+ this.meta.columns[i].label +'</span>';
				html += '<span class="leca-grid-header-icon"><i class="fa fa-filter"></i></span>';
			} else if( this.meta.columns[i].filterable ) {
				html += '<div data-column="'+ this.meta.columns[i].key +'" class="leca-data-grid-cell leca-grid-searchable" style="'+ style +'">';			
				html += '<span class="leca-grid-label">'+ this.meta.columns[i].label +'</span>';
				html += '<span class="leca-grid-header-icon"><i class="fa fa-search"></i></span>';
			} else {
				html += '<div data-column="'+ this.meta.columns[i].key +'" class="leca-data-grid-cell" style="'+ style +'">';			
				html += '<span class="leca-grid-label">'+ this.meta.columns[i].label +'</span>';				
			}			
			html += '</div>';			
		}		
		/* End of header row */
		html += '</div></div>';	
		
		this.gridHeader.append( $( html ) );		
		/* Start of the grid content container */
		this.gridTable = $( '<div class="leca-data-grid-content"></div>' );
		/* Just display  */
		html = '<h3 class="leca-loading-message"><i class="fa fa-cog fa-spin"></i> Loading ...</h3>';
		this.gridTable.append( html );
		
		/* Attach scroll event for lazy loading */
		this.gridTable.on( 'scroll', this, function (e) {
			e.data.onGridRecordScroll();			
	    });
		
		dataGrid.append( this.gridHeader );
		dataGrid.append( this.gridTable );
		
		this.container.append( dataGrid );
	};
	
	this.loadGrid = function( _columns, _records ) {
		var nodes = [];
		var colIndex = -1;
		var replaceVal = "";
		/* Row which contains header meta */
		this.columns = _columns;
		/* Actual records */
		this.records = _records;
		/* Prepare the column positions */
		this.prepareColumnPositions();
		/* Reset the grid properties */
		if( this.records.length < this.recordsPerPage ) {
			this.endIndex = this.records.length;
		} else {
			this.endIndex = this.recordsPerPage;
		}
		this.totalPages = Math.ceil( this.records.length / this.recordsPerPage );	
		/* Prepare filter columns - if any */
		for( var i = 0; i < this.meta.columns.length; i++ ) {
			if( this.meta.columns[i].filterable ) {
				nodes = [];
				colIndex = -1;
				colIndex = this.positions[ this.meta.columns[i].key ];
				for( var j = 0; j < this.records.length; j++ ) {					
					if( this.meta.columns[ i ].type == "REPLACE" ) {						
						replaceVal = this.contextObj.controller.getColumnValue(	
							this.meta.columns[ i ].replace.context,
							this.meta.columns[ i ].replace.grid,
							this.meta.columns[ i ].replace.key,
							this.records[j][colIndex],
							this.meta.columns[ i ].replace.target
						);
						if( ! replaceVal ) {
							if( this.meta.columns[ i ].replace.fallback != "" ) {
								replaceVal = this.meta.columns[ i ].replace.fallback;
							} else {
								replaceVal = this.records[j][colIndex];
							}											
						}		
						nodes.push( { key: this.records[j][colIndex], value: replaceVal } );
					} else {
						nodes.push( { key: this.records[j][ this.positions[ this.meta.columns[i].key ] ], value: this.records[j][ this.positions[ this.meta.columns[i].key ] ] } );
					}										
				}
				var temp=[];
				nodes=nodes.filter((x, i)=> {
				  if (temp.indexOf(x.key) < 0) {
				    temp.push(x.key);
				    return true;
				  }
				  return false;
				});
				this.filters[ this.meta.columns[i].key ] = nodes;
			}
		}		
		/* Clear the table container */
		this.gridTable.html( "" );
		if( this.records.length > 0 ) {
			/* Well load the record */
			this.renderPage();
			/* Set height */
			this.setGridHeight();
		} else {
			/* Looks like empty record */
			this.showEmptyMessage();
			/* We have auto width set but it has very few column which make it shorter then the actual grid width
			 * in that case make it Full width */
			if ( this.gridHeader.find( "div.leca-data-grid-header" ).width() <= $("#leca-dbexplorer-data-record-view").width() ) {
				this.gridHeader.find( "div.leca-data-grid-header" ).removeClass( "auto" ).addClass( "full" );
			}
		}		
	};
	
	/* Load next set ( next 50 records - or whatever the count ) of records ( Called from scroll event ) */
	this.loadNextPage = function() {
		if( this.records ) {
			if( this.records.length <= this.recordsPerPage || this.isEndReached ) {
				return;
			}			
			/* Increment the page number */
			++this.currentPage;			
			/* Compare whether it exceeds total number of page */
			if( this.currentPage <= this.totalPages ) {				
				/* Update start and end index of records that has to be rendered */
				this.startIndex = ( this.currentPage - 1 ) * this.recordsPerPage;
				this.endIndex = this.startIndex + this.recordsPerPage;
				/* Make sure the end index doesn't exceeds the total number of records */
				if( this.endIndex > this.records.length ) {	
					this.isEndReached = true;
					this.currentPage = this.currentPage - 1;
					this.endIndex = this.records.length;
				}
				/* Well render the records and append to the main grid table */
				var recordPage = $( '<div class="leca-data-grid-page '+ this.meta.width +'" data-loaded="yes"></div>' );
				this.gridTable.append( recordPage );
				this.renderRecords( recordPage );
				/* Sync the width of all columns */
				this.syncColumnWidth(recordPage);
			}
			if( typeof this.contextObj.onlazyRecordLoaded != "undefined" ) {
				this.contextObj.onlazyRecordLoaded();
			}			
		}		
	};
	
	/**
	 * Loads records to a given page, which would happen at scrolling event
	 * Required for performance tuning */
	this.loadPageRecord = function( dir ) {	
		var page;
		var currentPage = 1;
		
		if( this.gridTable.scrollTop() > this.recordPageHeight ) {
			currentPage = Math.ceil( this.gridTable.scrollTop() / this.recordPageHeight );
		}	
		
		if( currentPage > 0 ) {
			page = this.gridTable.find( "div.leca-data-grid-page" ).eq( currentPage - 1 );
			if( currentPage != this.totalPages && dir == "down" ) {
				if( page.attr( "data-loaded" ) == "no" ) {
					this.fillPageRecord( currentPage );
				}
				if( page.next().attr( "data-loaded" ) == "no" ) {
					this.fillPageRecord( currentPage + 1 );
				}
			} else if( dir == "up" ) {
				if( page.attr( "data-loaded" ) == "no" ) {					
					this.fillPageRecord( currentPage );
				}
				if( currentPage > 1 && page.prev().attr( "data-loaded" ) == "no" ) {					
					this.fillPageRecord( currentPage - 1 );
				}
			}
		}		
	};
	
	/**
	 * Rebuild the records from the object for lazy loading implementation
	 * Required for performance tuning */
	this.fillPageRecord = function( index ) {
		index = parseInt( index );
		/* Update  the current visible page index */
		this.currentVisiblePage = index;
		var page = this.gridTable.find( "div.leca-data-grid-page" ).eq( index - 1 );
		this.startIndex = ( ( index - 1 ) * this.recordsPerPage );
		this.endIndex = ( index * this.recordsPerPage );
		this.renderRecords( page );	
		/* Update loaded flag */
		page.attr( "data-loaded", "yes" );
		/* Sync the width of all columns */
		this.syncColumnWidth(page);
	};
	
	/**
	 * Clear records of a given page, which would happen at scrolling event
	 * Required for performance tuning */
	this.clearPageRecord = function( dir ) {
		var page;
		var sIndex = 1;
		var eIndex = 0;
		var currentPage = 1;
		
		if( this.gridTable.scrollTop() > this.recordPageHeight && this.recordPageHeight > 0 ) {
			currentPage = Math.ceil( this.gridTable.scrollTop() / this.recordPageHeight );
		}			
		if( currentPage > 3 && dir == "down" ) {
			sIndex = 1;
			eIndex = ( currentPage - 2 );
		} else if( ( ( currentPage + 2 ) < this.currentPage ) && dir == "up" ) {
			sIndex = ( currentPage + 2 );
			eIndex = this.currentPage;
		}	
		if( sIndex >= 0 && eIndex >= 0 ) {
			for( var i = sIndex; i <= eIndex; i++ ) {
				page = this.gridTable.find( "div.leca-data-grid-page:nth-child("+ i +")" );
				if( page.attr( "data-loaded" ) == "yes" ) {
					page.height( page.height() );
					page.attr( "data-loaded", "no" );
					page.html( "" );
				}
			}
		}
	};
	
	this.resetGrid = function() {
		this.currentPage = 1;
		this.currentVisiblePage = 1;
		this.totalPages = 0;
		this.recordsPerPage = 100;
		this.recordPageHeight = 1;
		this.startIndex = 0;
		this.endIndex = this.recordsPerPage;
		this.isEndReached = false;
		this.lastScrollPos = 0;
	};
	
	this.reloadGrid = function() {
		this.renderDataGrid();
		this.loadGrid(this.columns, this.records);
	};
	
	/** 
	 * Used to render the skeleton frame for the grid
	 * It also invokes 'renderRecords()' to render the actual records as grid */
	this.renderPage = function() {
		var me = this;
		var recordPage = $( '<div class="leca-data-grid-page '+ this.meta.width +'" data-loaded="yes"></div>' );	
		this.renderRecords( recordPage );
		/* Reset the grid table content */
		this.gridTable.html( "" );	
		this.gridTable.append( recordPage );
		this.recordPageHeight = recordPage.height();
		if ( this.meta.width === "full" ) {
			this.gridHeader.width( recordPage.innerWidth() );
		} else {
			/* We have auto width set but it has very few column which make it shorter then the actual grid width
			 * in that case make it Full width */
			if ( this.gridHeader.find( "div.leca-data-grid-header" ).width() > $("#leca-dbexplorer-data-record-view").width() ) {
				recordPage.width( this.gridHeader.find( "div.leca-data-grid-header" ).width() );
				this.gridTable.width( this.gridHeader.find( "div.leca-data-grid-header" ).width() );
			} else {
				this.gridHeader.find( "div.leca-data-grid-header" ).removeClass( "auto" ).addClass( "full" );
				recordPage.removeClass( "auto" ).addClass( "full" );
			}		
		}	
		this.syncColumnWidth(recordPage);
	};
	
	this.renderRecords = function( _page ) {
		var pos = -1,
			html = '',
			data = '',
			style = '',
			toggle = '',
			rKeyPos = -1,
			replaceVal = '';
		if( this.records.length > 0 && this.startIndex < this.endIndex ) {
			for( var i = this.startIndex; i < this.endIndex; i++ ) {
				html += '<div data-index="'+ i +'" class="leca-data-grid-row">';
				for( var j = 0; j < this.meta.columns.length; j++ ) {
					data = '';
					style = '';		
					toggle = '';
					if( i === (this.endIndex - 1) ) {
						/* For auto column width the sync will happen later */
						if ( typeof this.meta.columns[j].width !== 'undefined' && this.meta.width === "full" ) {
							style = "width: " + ( ( this.meta.columns[j].width != "" ) ? this.meta.columns[j].width +"%;" : "auto;" );
						} 
					}
					if ( typeof this.meta.columns[j].align !== 'undefined' ) {
						style += "text-align: " + ( ( this.meta.columns[j].align != "" ) ? this.meta.columns[j].align : "left;" );
					}	
					if( this.meta.columns[ j ].type != "INDEX" ) {
						if( this.positions[ this.meta.columns[j].key ] >= 0 ) {
							pos = this.positions[ this.meta.columns[j].key ];
							if( this.meta.columns[ j ].type !== "LINK" ) {
								if( this.meta.columns[ j ].type === "REPLACE" ) {
									if( this.meta.columns[ j ].replace.type === "dynamic" ) {
										replaceVal = this.contextObj.controller.getColumnValue(	
											this.meta.columns[ j ].replace.context,
											this.meta.columns[ j ].replace.grid,
											this.meta.columns[ j ].replace.key,
											this.records[i][pos],
											this.meta.columns[ j ].replace.target
										);
										if( ! replaceVal ) {
											if( this.meta.columns[ j ].replace.fallback != "" ) {
												replaceVal = this.meta.columns[ j ].replace.fallback;
											} else {
												replaceVal = this.records[i][pos];
											}											
										}
									} else {
										if( typeof this.meta.columns[ j ].replace[ this.records[i][pos] ] !== 'undefined' ) {
											replaceVal = this.meta.columns[ j ].replace[ this.records[i][pos] ];
										}										
									}								
									html += '<div class="leca-data-grid-cell" style="'+ style +'">'+ replaceVal +'</div>';									
								} else if( this.meta.columns[ j ].type == "TOGGLE" ) { 
									toggle = ( this.records[i][pos] ) ? "on":"";
									html += '<div class="leca-data-grid-cell" style="'+ style +'">';	
									html += '<div class="leca-grid-toggle-bar">';
									html += '<label class="leca-grid-toggle-switch '+ toggle +'" title="'+ this.meta.columns[ j ].toggle.tips +'" data-grid="'+ this.name +'" data-option="'+ this.meta.columns[ j ].key +'" data-rkey="'+ this.records[i][this.positions[ this.meta.primary_key ]] +'", data-pkey="'+ this.meta.primary_key +'" data-task="'+ this.meta.columns[ j ].toggle.task +'" >';
									html += '<span class="leca-toggle-label" data-on="'+ this.meta.columns[ j ].toggle.on.label +'" data-on-value="'+ this.meta.columns[ j ].toggle.on.value +'" data-off="'+ this.meta.columns[ j ].toggle.off.label +'" data-off-value="'+ this.meta.columns[ j ].toggle.off.value +'"></span>';
									html += '<span class="leca-toggle-handle"></span>';
									html += '</div>';
									html += '</div>';	
								} else if(this.meta.columns[ j ].type == "PASSWORD") {
									html += '<div class="leca-data-grid-cell" style="'+ style +'">**********</div>';									
								} else {
									html += '<div class="leca-data-grid-cell" style="'+ style +'">'+ this.escapeHtml(this.records[i][pos]) +'</div>';
								}								
							} else {
								rKeyPos = this.getColumnPosition( this.meta.columns[j].link );								
								html += '<div class="leca-data-grid-cell" style="'+ style +'"><a href="#" data-context="'+ this.meta.columns[j].context +'" data-key="'+ this.meta.columns[j].link +'" data-val="'+ this.records[i][rKeyPos] +'" class="leca-single-link '+ this.meta.columns[j].classes +'" title="Click to go to detailed view">'+ this.records[i][pos] +'</a></div>';
							}
						} else {
							if (this.meta.columns[ j ].type == "BUTTON") {
								rKeyPos = this.getColumnPosition( this.meta.columns[j].link );	
								html += '<div class="leca-data-grid-cell" style="'+ style +'"><button data-context="'+ this.meta.columns[j].context +'" data-key="'+ this.meta.columns[j].link +'" data-val="'+ this.records[i][rKeyPos] +'" class="'+ this.meta.columns[j].classes +'">'+ this.meta.columns[j].btn_value +'</button></div>';
							}
						}						
					} else {		
						/* Serial number cell */
						html += '<div class="leca-data-grid-cell" style="'+ style +'">'+ ( i + 1 ) +'</div>';
					}
				}
				html += '</div>';
			}
			_page.append( $( html ) );		
		}
	};
	
	/**
	 * Grid section scrolling handler
	 * Trigger the handler for loading next set of pages while scroll bar touches at the bottom
	 * Also it triggers the appropriate handler for clearing and adding pages as the scroll bar keeps moving
	 * Plays key roles in the Data Grid performance */
	this.onGridRecordScroll = function() {		
		var dir = "down",
		pos = this.gridTable.scrollTop();
		
		if ( pos > this.lastScrollPos ){
			dir = "down";
		} else {
			dir = "up";
		}
		
		this.lastScrollPos = pos;		
		this.clearPageRecord( dir );
		this.loadPageRecord( dir );
		if( dir == "down" ) {
			if( Math.ceil( this.gridTable.scrollTop() + this.gridTable.innerHeight() ) >= this.gridTable[0].scrollHeight ) {
				this.loadNextPage();
			}
		}
	};
	
	this.showEmptyMessage = function() {
		this.gridTable.html( '<h1 class="leca-empty-record-msg"><i class="fa fa-exclamation"></i> No record found</h1>' );
	};
	
	this.showErrorMessage = function() {
		this.gridTable.html( '<h1 class="leca-empty-record-msg"><i class="fa fa-exclamation"></i> Error while fetching records</h1>' );
	};
	
	this.prepareColumnPositions = function() {
		for( var i = 0; i < this.columns.length; i++ ) {			
			this.positions[ this.columns[i] ] = i;
		}
	}
	
	this.getColumnPosition = function( _column ) {
		var pos = -1; 
		if( _column != "" ) { 
			for( var i = 0; i < this.columns.length; i++ ) {
				if( this.columns[i] == _column ) {
					pos = i;
					break;
				}
			}
		}		
		return pos;
	};
	
	/**
	 * Move the vertical scroll bar to specific row */
	this.scrollVertical = function( Index ) {
		this.gridTable.parent().scrollTop( this.gridTable.find( "tbody > tr:nth-child("+ trIndex +")" ).offset().top );
	};
	
	/**
	 * Move the horizontal scroll bar to specific column */
	this.scrollHorizontal = function( Index ) {
		
	};
	
	this.setGridHeight = function() {
		var height = 0,
		calcHeight = 0,
		filterHeight = 0,
		gridHeaderHeight = 0,
		headerWrapperHeight = 0;
		/* Determine the height */
		if (this.meta.height === "full") {
			height = $( window ).height();
			headerWrapperHeight = $( ".leca-context-header-wrapper" ).height();
			filterHeight = ( this.meta.filter ) ? this.container.prev().height() : 0;
			gridHeaderHeight = this.gridHeader.height();		
			calcHeight = ( height - ( headerWrapperHeight + filterHeight + gridHeaderHeight + 85 ) );			
		} else {
			height = this.container.height();
			gridHeaderHeight = this.gridHeader.height();
			calcHeight = ( height - gridHeaderHeight - 5 );
		}				
		/* Apply the calculated height */
		if ( this.gridTable.find( "div.leca-data-grid-page" ).hasClass("auto") ) {
			/* Now subtract the scroll bar height (horizontal) */
			this.gridTable.css( "max-height", ( calcHeight - $.position.scrollbarWidth() ) + "px" );			
		} else {
			this.gridTable.css( "max-height", calcHeight + "px" );
		}
	};
	
	this.syncColumnWidth = function( _page ) {
		var width = 0;
		var firstRow = _page.find( "> div" );
		for( var i = 0; i < this.meta.columns.length; i++ ) {
			width = this.gridHeader.find( "div.leca-data-grid-row > div:nth-child("+ (i + 1) +")" ).width();
			firstRow.find( ">div:nth-child("+ (i + 1) +")" ).width( width );
		}
	};
	
	this.escapeHtml = function( _string ) {
		var me = this;
		return String(_string).replace(/[&<>"'`=\/]/g, function (s) {
		    return me.entityMap[s];
		});
	}
	
};