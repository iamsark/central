<!DOCTYPE html>
<html>

	<head>
	
		<meta charset="utf-8">	
		<meta name="HandheldFriendly" content="True">
		<meta name="MobileOptimized" content="320">
		<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no"/>

		<title>Lever Edge - Central Application System Manager</title>
	
		<link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favico.png">	
		<link rel="preconnect" href="https://fonts.googleapis.com">
		<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
		<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">	
		<link type="text/css" href="<%=request.getContextPath()%>/assets/css/leca-system-core.css" rel="stylesheet">		
		
		<style type="text/css" id="leca-rd-css-overrides"></style>
		
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/libs/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/libs/jquery-ui.min.js"></script>
		
		<script type="text/javascript">
			var lecaOpt = {				
				docker: "<%=request.getContextPath()%>",
				socket_endpoint: "",
				roles: [],
				hv_details: {}
			};
		</script>
		
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/libs/ace/ace.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/system/config.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/utils/system-grid.js"></script>
		
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/system/report-designer.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/system/controller.js"></script>
		
	</head>
	
	<body>
	
		<div class="leca-master-container expanded" id="leca-master-container">
		
			<header class="leca-top-section">
			
				<div class="leca-header-wrapper">
					<div class="leca-top-header">
						<ul class="leca-header-brand-ul">
							<li class="logo"><img src="/central/assets/img/logo.png"></li>
							<li class="brand"><label>Central App</label></li>
							<li class="toggle"><a href="#" id="leca-menu-collapse-btn"><i class="material-icons">menu</i></a></li>							
						</ul>
					</div>
					<div class="leca-top-content">
						
						<div class="leca-header-area">						
							<div><h1 id="leca-header-breadcrumb"></h1></div>
							<div id="leca-top-action-bar"></div>						
						</div>
						
						<span id="leca-system-loading-notification">Loading...</span>
						
					</div>
				</div>
							
			</header>
			
			<div class="leca-main-sidbar">
			
				<ul id="leca-primary-navigation">
					
					<li class="leca-top-menu-item">
						<a href="#" data-context="report_designer">
							<i class="material-icons">receipt</i>
							<Label>Report</Label>							
						</a>
					</li>
					<li class="leca-top-menu-item">
						<a href="#" data-context="dashboard_designer">
							<i class="material-icons">dashboard</i>
							<Label>Dashboard</Label>							
						</a>
					</li>
					<li class="leca-top-menu-item">
						<a href="#" data-context="sales_hierarchy">
							<i class="material-icons">account_tree</i>
							<Label>Sales Hierarchy</Label>							
						</a>
					</li>
					<li class="leca-top-menu-item">
						<a href="#" data-context="access_control">
							<i class="material-icons">settings_input_component</i>
							<Label>Access Control</Label>							
						</a>
					</li>				
					
					<li class="leca-top-menu-item">
						<a href="#" data-context="logout">
							<i class="material-icons">power_settings_new</i>
							<Label>Logout</Label>							
						</a>
					</li>
					
				</ul>
			
			</div>
			<div class="leca-main-content">
			
				<div id="leca-workspace-context">
				
					<div class="leca-context-workspace" id="leca-workspace-report_designer">
					
						<div id="leca-workspace-report_designer-archive" class="leca-sub-workspace leca-archive-view">
							<div id="leca-report_designer-archive-report-grid">
									
							</div>
						</div>
						<div id="leca-workspace-report_designer-create" class="container-fluid">	
						
							<div class="row">
								<div class="col-md-6">
									<div class="leca-report-config-form form-section">
										<h3>Report</h3>
										<div class="form-row">
											<label>Report Name</label>
											<input type="text" id="leca-report_designer-create-report_name">
										</div>	
										<h3>Configuration</h3>												
										<div class="leca-tab-header" data-tab="report-ds-tab">
											<a href="#leca-report_designer-create-ds-bean-config" class="leca-tab-btn selected" data-dtype="bean">Bean</a>
											<a href="#leca-report_designer-create-ds-format-config" class="leca-tab-btn" data-dtype="format">Format</a>
											<a href="#leca-report_designer-create-ds-users-config" class="leca-tab-btn" data-dtype="users">Users</a>																														
										</div>
										<div class="leca-tab-content-container">
											<div id="leca-report_designer-create-ds-bean-config" style="display: block;">
												<div class="leca-dashboard-chart-ds-config-row">
													<label>Bean</label>
													<select id="leca-report_designer-create-bean-select"></select>
												</div>
												<div class="leca-dashboard-chart-ds-config-row">
													<label>Method</label>
													<select id="leca-report_designer-create-method-select"></select>
												</div>
												<div class="leca-dashboard-chart-ds-config-row">
													<label>Object Key</label>
													<input type="text" id="leca-report_designer-create-ds-object-key" />
												</div>
											</div>													
											<div id="leca-report_designer-create-ds-format-config">
												<div class="leca-dashboard-chart-ds-config-row">
													<label><input type="checkbox" class="leca-report_designer-create-format-check report_designer-create-format-pdf" value="yes" /> PDF</label>
													<label><input type="checkbox" class="leca-report_designer-create-format-check report_designer-create-format-txt" value="yes" /> TXT</label>
													<label><input type="checkbox" class="leca-report_designer-create-format-check report_designer-create-format-xls" value="yes" /> XLS</label>
													<label><input type="checkbox" class="leca-report_designer-create-format-check report_designer-create-format-csv" value="yes" /> CSV</label>															
												</div>
											</div>
											<div id="leca-report_designer-create-ds-users-config">																
												<div class="leca-dashboard-chart-ds-config-row">
													<label><input type="checkbox" class="leca-report_designer-create-target-user-check" value="1" checked /> SA</label>
													<label><input type="checkbox" class="leca-report_designer-create-target-user-check" value="0" checked /> TSI_DETS</label>
													<label><input type="checkbox" class="leca-report_designer-create-target-user-check" value="2" checked /> TSI_PP</label>
													<label><input type="checkbox" class="leca-report_designer-create-target-user-check" value="3" checked /> TSI_PLG</label>															
												</div>																
											</div>																		
										</div>										
									</div>
																			
								</div>
								<div class="col-md-6">
									
								</div>
							</div>
						
						</div>
						<div id="leca-workspace-report_designer-single" class="container-fluid">	
							<div class="row">
								<div class="col-md-5">
									<div class="leca-report-config-form form-section">
										<h3>Report</h3>
										<div class="form-row">
											<label>Report Name</label>
											<input type="text" id="leca-report_designer-single-report_name">
										</div>										
									
										<h3>Configuration</h3>												
										<div class="leca-tab-header" data-tab="report-ds-tab">
											<a href="#leca-report_designer-single-ds-bean-config" class="leca-tab-btn selected" data-dtype="bean">Bean</a>
											<a href="#leca-report_designer-single-ds-format-config" class="leca-tab-btn" data-dtype="format">Format</a>
											<a href="#leca-report_designer-single-ds-users-config" class="leca-tab-btn" data-dtype="users">Users</a>																														
										</div>
										<div class="leca-tab-content-container">
											<div id="leca-report_designer-single-ds-bean-config" style="display: block;">
												<div class="leca-dashboard-chart-ds-config-row">
													<label>Bean</label>
													<select id="leca-report_designer-single-bean-select"></select>
												</div>
												<div class="leca-dashboard-chart-ds-config-row">
													<label>Method</label>
													<select id="leca-report_designer-single-method-select"></select>
												</div>
												<div class="leca-dashboard-chart-ds-config-row">
													<label>Object Key</label>
													<input type="text" id="leca-report_designer-single-ds-object-key" />
												</div>
											</div>													
											<div id="leca-report_designer-single-ds-format-config">
												<div class="leca-dashboard-chart-ds-config-row">
													<label><input type="checkbox" class="leca-report_designer-single-format-check report_designer-single-format-pdf" value="yes" /> PDF</label>
													<label><input type="checkbox" class="leca-report_designer-single-format-check report_designer-single-format-txt" value="yes" /> TXT</label>
													<label><input type="checkbox" class="leca-report_designer-single-format-check report_designer-single-format-xls" value="yes" /> XLS</label>
													<label><input type="checkbox" class="leca-report_designer-single-format-check report_designer-single-format-csv" value="yes" /> CSV</label>															
												</div>
											</div>
											<div id="leca-report_designer-single-ds-users-config">																
												<div class="leca-dashboard-chart-ds-config-row">
													<label><input type="checkbox" class="leca-report_designer-single-target-user-check" value="1" checked /> SA</label>
													<label><input type="checkbox" class="leca-report_designer-single-target-user-check" value="0" checked /> TSI_DETS</label>
													<label><input type="checkbox" class="leca-report_designer-single-target-user-check" value="2" checked /> TSI_PP</label>
													<label><input type="checkbox" class="leca-report_designer-single-target-user-check" value="3" checked /> TSI_PLG</label>															
												</div>																
											</div>																		
										</div>
																													
									</div>																		
								</div>
								<div class="col-md-7" style="padding-left: 0px;">
									<div id="leca-report-type-list">
									
									</div>
								</div>
							</div>
						</div>
						
						<div id="leca-workspace-report_designer-designer">
							<div class="leca-rd-wrapper">			
								<div class="leca-rd-designer-section">
									<div class="leca-rd-container">
										<div class="column">
											<div class="leca-rd-sidebar-panel left">												
																							 
												<div class="leca-rd-properties-section-header">								
													<i class="fa fa-sliders-h"></i>&nbsp;
													<span><span id="leca-rd-widget-properties-section-label">Fields</span> - Properties</span>																		
												</div>														
												<div class="leca-sidebar-tab-content leca-tab-content">
													<div id="leca-rd-sidebar-fields-panel">
														<div id="leca-rd-field-list-empty-msg" style="display: block;">
															<h3>No fields found for this template</h3>
														</div>
														<div id="leca-rd-fields-list-container">
														
														</div>
													</div>							
													<div id="leca-rd-sidebar-properties-panel" style="display: block;">
														
														<div id="leca-rd-properties-empty-msg" style="display: block;">
															<h3>Please select a widget (from designer pad) to edit the properties</h3>
														</div>
														
														<div id="leca-rd-widget-static_text-properties">																							
															<div class="form-row">
																<label>Label</label>
																<input type="text" class="leca-rd-unit-field" data-key="label" data-type="text" />
															</div>
															<div class="form-row">											
																<label>Text Decoration</label>
																<div class="leca-rd-unit-field selector multi four" data-key="text_decoration" data-type="selector">																
																	<label title="None" class="selected" data-option="none"><i class="material-icons">close</i></label>
																	<label title="Bold" data-option="bold"><i class="material-icons">format_bold</i></label>
																	<label title="Italic" data-option="italic"><i class="material-icons">format_italic</i></label>															
																	<label title="Underline" data-option="underline"><i class="material-icons">format_underlined</i></label>																
																</div>											
															</div>										
															<div class="form-row">
																<label>Text Align</label>
																<div class="leca-rd-unit-field selector four" data-key="text_align" data-type="selector">																	
																	<label title="Left" data-option="left"><i class="material-icons">format_align_left</i></label>
																	<label title="Center" data-option="center"><i class="material-icons">format_align_center</i></label>
																	<label title="Right" data-option="right"><i class="material-icons">format_align_right</i></label>
																	<label title="Justify" data-option="justify"><i class="material-icons">format_align_justify</i></label>
																</div>
															</div>
															<div class="form-row">
																<label>Padding</label>
																<label class="leca-rd-check-wrapper"><input type="checkbox" class="leca-rd-unit-field" data-key="use_global_padding" data-type="checkbox" />&nbsp;Use global</label>
																<table class="leca-unit-table-container two">
																	<tr data-support="text,media">													
																		<td>
																			<label>Left</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_left" data-type="number">
																		</td>
																		<td>
																			<label>Right</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_right" data-type="number">
																		</td>
																	</tr>
																	<tr data-support="text,media">
																		<td>
																			<label>Top</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_top" data-type="number">
																		</td>
																		<td>
																			<label>Bottom</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_bottom" data-type="number">
																		</td>													
																	</tr>
																</table>
															</div>
															<div class="form-row" id="rd-static-repeat-field-config">
																<label>Repeat Field</label>																	
																<select class="leca-rd-unit-field" data-key="repeat_field" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<label>Editable</label>																	
																<select class="leca-rd-unit-field" data-key="is_editable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<label>Removable</label>
																<select class="leca-rd-unit-field" data-key="is_removable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<p class="desc">Use this widget to insert static text, this widget doesn't support any dynamic binding, the text has to be given at the design time itself.</p>
															</div>
														</div>
														
														<div id="leca-rd-widget-dynamic_text-properties">
															<div class="form-row">
																<label>Handle</label>
																<input type="text" class="leca-rd-unit-field" data-key="handle" data-type="text" />
															</div>																			
															<div class="form-row">											
																<label>Text Decoration</label>
																<div class="leca-rd-unit-field selector multi four" data-key="text_decoration" data-type="selector">																
																	<label title="None" class="selected" data-option="none"><i class="material-icons">close</i></label>
																	<label title="Bold" data-option="bold"><i class="material-icons">format_bold</i></label>
																	<label title="Italic" data-option="italic"><i class="material-icons">format_italic</i></label>															
																	<label title="Underline" data-option="underline"><i class="material-icons">format_underlined</i></label>																
																</div>											
															</div>										
															<div class="form-row">
																<label>Text Align</label>
																<div class="leca-rd-unit-field selector four" data-key="text_align" data-type="selector">																	
																	<label title="Left" data-option="left"><i class="material-icons">format_align_left</i></label>
																	<label title="Center" data-option="center"><i class="material-icons">format_align_center</i></label>
																	<label title="Right" data-option="right"><i class="material-icons">format_align_right</i></label>
																	<label title="Justify" data-option="justify"><i class="material-icons">format_align_justify</i></label>
																</div>
															</div>	
															<div class="form-row">
																<label>Padding</label>
																<label class="leca-rd-check-wrapper"><input type="checkbox" class="leca-rd-unit-field" data-key="use_global_padding" data-type="checkbox" />&nbsp;Use global</label>
																<table class="leca-unit-table-container two">
																	<tr data-support="text,media">													
																		<td>
																			<label>Left</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_left" data-type="number">
																		</td>
																		<td>
																			<label>Right</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_right" data-type="number">
																		</td>
																	</tr>
																	<tr data-support="text,media">
																		<td>
																			<label>Top</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_top" data-type="number">
																		</td>
																		<td>
																			<label>Bottom</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_bottom" data-type="number">
																		</td>													
																	</tr>
																</table>
															</div>
															<div class="form-row">
																<label>Stretch (When Overflow)</label>
																<select class="leca-rd-unit-field" data-key="stretch_with_overflow" data-type="text">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>																
															</div>
															<div class="form-row">
																<label>Remove Line (When Blank)</label>
																<select class="leca-rd-unit-field" data-key="remove_line_when_blank" data-type="text">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>																
															</div>	
															<div class="form-row" id="rd-dynamic-repeat-field-config">
																<label>Repeat Field</label>																	
																<select class="leca-rd-unit-field" data-key="repeat_field" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>														
															<div class="form-row">
																<label>Editable</label>																	
																<select class="leca-rd-unit-field" data-key="is_editable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<label>Removable</label>
																<select class="leca-rd-unit-field" data-key="is_removable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<p class="desc">Use this widget to insert dynamic text, use the Handle field to configure the Binding Property (eg. property name from the data source object).</p>
															</div>
														</div>
														
														<div id="leca-rd-widget-record_table-properties">
															<div class="form-row">
																<label>Handle</label>
																<input type="text" class="leca-rd-unit-field" data-key="handle" data-type="text" />
															</div>
															<div class="form-row">
																<label>Has Footer ?</label>
																<select class="leca-rd-unit-field" data-key="has_footer" data-type="select" >
																	<option value="no">No</option>
																	<option value="yes">Yes</option>												
																</select>											
															</div>															
															<div class="form-row">
																<label>Editable</label>																	
																<select class="leca-rd-unit-field" data-key="is_editable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<label>Removable</label>
																<select class="leca-rd-unit-field" data-key="is_removable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
														</div>
														
														<div id="leca-rd-widget-record_table_column-properties">
															<div class="form-row">
																<label>Handle</label>
																<input type="text" class="leca-rd-unit-field" data-key="handle" data-type="text" />
															</div>
															<div class="form-row">
																<label>Label</label>
																<input type="text" class="leca-rd-unit-field" data-key="label" data-type="text" />
															</div>
															<div class="form-row">
																<label>Type</label>
																<select class="leca-rd-unit-field" data-key="type" data-type="select" >
																	<option value="column">Column</option>
																	<option value="placeholder">Placeholder</option>
																</select>
															</div>
															<div class="form-row">
																<label>Column Align</label>
																<div class="leca-rd-unit-field selector four" data-key="column_align" data-type="selector">																	
																	<label title="Left" data-option="left"><i class="material-icons">format_align_left</i></label>
																	<label title="Center" data-option="center"><i class="material-icons">format_align_center</i></label>
																	<label title="Right" data-option="right"><i class="material-icons">format_align_right</i></label>
																	<label title="Justify" data-option="justify"><i class="material-icons">format_align_justify</i></label>
																</div>
															</div>	
															<div class="form-row">
																<label>Record Align</label>
																<div class="leca-rd-unit-field selector four" data-key="record_align" data-type="selector">																	
																	<label title="Left" data-option="left"><i class="material-icons">format_align_left</i></label>
																	<label title="Center" data-option="center"><i class="material-icons">format_align_center</i></label>
																	<label title="Right" data-option="right"><i class="material-icons">format_align_right</i></label>
																	<label title="Justify" data-option="justify"><i class="material-icons">format_align_justify</i></label>
																</div>
															</div>	
															<div class="form-row">
																<label>Padding</label>
																<label class="leca-rd-check-wrapper"><input type="checkbox" class="leca-rd-unit-field" data-key="use_global_padding" data-type="checkbox" />&nbsp;Use global</label>
																<table class="leca-unit-table-container two">
																	<tr data-support="text,media">													
																		<td>
																			<label>Left</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_left" data-type="number">
																		</td>
																		<td>
																			<label>Right</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_right" data-type="number">
																		</td>
																	</tr>
																	<tr data-support="text,media">
																		<td>
																			<label>Top</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_top" data-type="number">
																		</td>
																		<td>
																			<label>Bottom</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_bottom" data-type="number">
																		</td>													
																	</tr>
																</table>
															</div>
															<div class="form-row">
																<label>Editable</label>																	
																<select class="leca-rd-unit-field" data-key="is_editable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<label>Removable</label>
																<select class="leca-rd-unit-field" data-key="is_removable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
														</div>
														
														<div id="leca-rd-widget-sub_report-properties">
														
															<div class="form-row">
																<label>Handle</label>
																<input type="text" class="leca-rd-unit-field" data-key="handle" data-type="text" />
															</div>											
															<div class="form-row">
																<label>Sub Report</label>
																<select class="leca-rd-unit-field" data-key="report" data-type="select" id="leca-rd-report-list-select">																	
																</select>
															</div>
																			
															<div class="form-row">
																<label>Editable</label>																	
																<select class="leca-rd-unit-field" data-key="is_editable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<label>Removable</label>
																<select class="leca-rd-unit-field" data-key="is_removable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<p class="desc">Use this widget to insert Sub Report.</p>
															</div>
														
														</div>
														
														<div id="leca-rd-widget-image-properties">
														
														</div>
																										
														<div id="leca-rd-widget-page_number-properties">															
															<div class="form-row">
																<label>Label</label>
																<input type="text" class="leca-rd-unit-field" data-key="label" data-type="text" />
															</div>															
															<div class="form-row" id="rd-page-number-repeat-field-config">
																<label>Repeat Field</label>																	
																<select class="leca-rd-unit-field" data-key="repeat_field" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															
															<div class="form-row">											
																<label>Text Decoration</label>
																<div class="leca-rd-unit-field selector multi four" data-key="text_decoration" data-type="selector">																	
																	<label title="None" class="selected" data-option="none"><i class="material-icons">close</i></label>
																	<label title="Bold" data-option="bold"><i class="material-icons">format_bold</i></label>
																	<label title="Italic" data-option="italic"><i class="material-icons">format_italic</i></label>															
																	<label title="Underline" data-option="underline"><i class="material-icons">format_underlined</i></label>																																																	
																</div>											
															</div>										
															<div class="form-row">
																<label>Text Align</label>
																<div class="leca-rd-unit-field selector four" data-key="text_align" data-type="selector">																	
																	<label title="Left" data-option="left"><i class="material-icons">format_align_left</i></label>
																	<label title="Center" data-option="center"><i class="material-icons">format_align_center</i></label>
																	<label title="Right" data-option="right"><i class="material-icons">format_align_right</i></label>
																	<label title="Justify" data-option="justify"><i class="material-icons">format_align_justify</i></label>																		
																</div>
															</div>
																
															<div class="form-row">
																<label>Padding</label>
																<label class="leca-rd-check-wrapper"><input type="checkbox" class="leca-rd-unit-field" data-key="use_global_padding" data-type="checkbox" />&nbsp;Use global</label>
																<table class="leca-unit-table-container two">
																	<tr data-support="text,media">													
																		<td>
																			<label>Left</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_left" data-type="number">
																		</td>
																		<td>
																			<label>Right</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_right" data-type="number">
																		</td>
																	</tr>
																	<tr data-support="text,media">
																		<td>
																			<label>Top</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_top" data-type="number">
																		</td>
																		<td>
																			<label>Bottom</label>
																			<input type="number" class="leca-rd-unit-field" value="0" data-key="padding_bottom" data-type="number">
																		</td>													
																	</tr>
																</table>
															</div>	
															<div class="form-row">
																<label>Editable</label>																	
																<select class="leca-rd-unit-field" data-key="is_editable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<label>Removable</label>
																<select class="leca-rd-unit-field" data-key="is_removable" data-type="select">
																	<option value="yes">Yes</option>
																	<option value="no">No</option>
																</select>
															</div>
															<div class="form-row">
																<p class="desc">Use this widget to insert Page Number.</p>
															</div>
														</div>
														
														<div id="leca-rd-widget-chart-properties">
														
														</div>
														
														<div id="leca-rd-widget-page_header-properties">
														
															<div class="form-row">
																<p class="desc">Use this widget to add group container on Page Header, which will be repeated on each page.</p>																
															</div>
															<div class="form-row">
																<p class="desc">This widget has no property</p>
															</div>
														
														</div>
														
														<div id="leca-rd-widget-page_footer-properties">
														
															<div class="form-row">
																<p class="desc">Use this widget to add group container on Page Footer, which will be repeated on each page.</p>																
															</div>
															<div class="form-row">
																<p class="desc">This widget has no property</p>
															</div>
														
														</div>
														
														<div id="leca-rd-widget-separator-properties">
														
														</div>											
														
													</div>
												</div>
											</div>
										</div>
										<div class="column">							
											<div class="leca-rd-designer-pad">							
												<div class="leca-rd-designer-pad-header leca-tab-header" data-tab="report-tab">
													<a href="#leca-rd-designer-design-view" title="Design View" class="leca-tab-btn selected" data-target="design"><i class="material-icons">developer_mode</i> Design</a>
													<a href="#leca-rd-designer-preview-view" title="Pre View" class="leca-tab-btn" data-target="preview"><i class="material-icons">monitor</i> Preview</a>
													<a href="#leca-rd-designer-data-view" title="Pre View" class="leca-tab-btn" data-target="data"><i class="material-icons">data_object</i> Data</a>
												</div>
												<div class="leca-rd-designer-pad-content leca-tab-content">															
													<div id="leca-rd-designer-design-view" style="display: block;">
														
														<div class="leca-rd-selector-layer">
															<div id="leca-rd-highlight-bar">
																<div id="leca-rd-highlight-widget-name"></div>
															</div>
															<div id="leca-rd-selector-bar">
																<div id="leca-rd-selector-widget-name"></div>
																<div id="leca-rd-selector-actions">
																	<a id="leca-rd-widget-edit-btn" href="" title="Edit Widget"><i class="material-icons">edit</i></a>																					
																	<a id="leca-rd-widget-clone-btn" href="" title="Clone Widget"><i class="material-icons">content_copy</i></a>
																	<a id="leca-rd-widget-delete-btn" href="" title="Remove Widget"><i class="material-icons">delete</i></a>
																</div>										
															</div>
														</div>
														
														<!-- Main page designer -->
														<div id="leca-rd-designer-design-pad"></div>
														
													</div>
													<div id="leca-rd-designer-preview-view">
														<iframe src="" id="leca-rd-designer-preview-iframe" style="width:100%;height:650px;"></iframe>
													</div>
													<div id="leca-rd-designer-data-view">
														<pre><code id="leca-rd-designer-data-blue-print" class="javascript"></code></pre>
													</div>
												</div>						
											</div>							
										</div>
										<div class="column">
										
											<div class="leca-rd-sidebar-panel right">
												
												<div class="leca-rd-sidebar-tab-header leca-tab-header">
													<div class="leca-rd-sidebar-tab-item">
														<a href="#leca-rd-sidebar-panel-component" title="Component Panel" class="leca-tab-btn selected">
															<i class="material-icons">widgets</i> 
															<span>Widgets</span>
														</a>										
													</div>									
													<div class="leca-rd-sidebar-tab-item">
														<a href="#leca-rd-sidebar-panel-report-setup" title="Data Source Configuration" class="leca-tab-btn">
															<i class="material-icons">settings</i>
															<span>Setup</span>
														</a>
													</div>
												</div>
												
												<div class="leca-sidebar-tab-content leca-tab-content">
													<div id="leca-rd-sidebar-panel-component" style="display: block;">									
														<div id="leca-rd-widget-list">																
															
															<a href="#" data-type="static_text" data-support="text,media" draggable="true"><i class="material-icons">text_format</i><span>Static Text</span></a>
															<a href="#" data-type="dynamic_text" data-support="text,media" draggable="true"><i class="material-icons">code</i><span>Dynamic Text</span></a>
															<a href="#" data-type="record_table" data-support="text,media" draggable="true"><i class="material-icons">table_chart</i><span>Records</span></a>								
															<a href="#" data-type="sub_report" data-support="text,media" draggable="true"><i class="material-icons">integration_instructions</i><span>Sub Report</span></a>
																														
															<a href="#" data-type="page_header" data-support="text,media" draggable="true"><i class="material-icons" style="transform:rotate(180deg);">call_to_action</i><span>Page Header</span></a>
															<a href="#" data-type="page_footer" data-support="text,media" draggable="true"><i class="material-icons">call_to_action</i><span>Page Footer</span></a>
															<a href="#" data-type="separator" data-support="text,media" draggable="true"><i class="material-icons">insert_page_break</i><span>Separator</span></a>
															<a href="#" data-type="page_number" data-support="text,media" draggable="true"><i class="material-icons">qr_code</i><span>Page Number</span></a>
															
															<a href="#" data-type="image" data-support="media" draggable="true"><i class="material-icons">image</i><span>Image</span></a>														
															<a href="#" data-type="chart" data-support="media" draggable="true"><i class="material-icons">insert_chart</i><span>Chart</span></a>
																														
														</div>									
													</div>
													<div id="leca-rd-sidebar-panel-page-layout">
													
														<div class="leca-rd-page-setup-container">
															
														</div>
													
													</div>
													<div id="leca-rd-sidebar-panel-report-setup">								
														<div class="leca-rd-report-setup-container">										
															<div class="form-row">
																<label>Name</label>
																<input type="text" class="leca-rd-report-setup-field" data-key="name" data-type="text">
															</div>
															<div class="form-row">
																<label>Format</label>													
																<select class="leca-rd-report-setup-field" data-key="format" data-type="select">
																	<option value="text">Text</option>
																	<option value="media">Multi Media</option>
																</select>
															</div>										
															<div class="form-row">
																<label>Printer</label>													
																<select class="leca-rd-report-setup-field" data-key="printer_type" data-type="select">
																	<option value="dmp">Dot Matrix</option>
																	<option value="other">Ink or Laser</option>
																</select>
															</div>
															<div class="form-row leca-rd-cpi-form-row">
																<label>Characters Per Inch</label>													
																<select class="leca-rd-report-setup-field" data-key="printer_cpi" data-type="select">
																	<option value="10">10 CPI</option>
																	<option value="12" selected>12 CPI</option>
																	<option value="15">15 CPI</option>
																	<!-- <option value="17">17 CPI</option> -->
																	<!-- <option value="20">20 CPI</option> -->
																</select>
															</div>
															<div class="form-row leca-rd-lpi-form-row">
																<label>Lines Per Inch</label>	
																<select class="leca-rd-report-setup-field" data-key="printer_lpi" data-type="select">
																	<option value="6">6 Lines</option>												
																	<option value="8">8 Lines</option>
																	<option value="7/72">7/72 Lines</option>
																	<option value="n/72">n/72 Lines</option>
																	<option value="n/216">n/216 Lines</option>
																</select>
																<input type="number" data-key="lpi-divisor" placeholder="nth divisor" >
															</div>
															<div class="form-row leca-rd-font-form-row">
																<label>Font Family</label>													
																<select class="leca-rd-report-setup-field" data-key="font" data-type="select">
																
																	<option value="courier">Courier</option>
																	<option value="courier-bold">Courier Bold</option>
																	<option value="courier-italic">Courier Italic</option>
																	<option value="courier-bold-italic">Courier Bold Oblique</option>																	
																	<option value="helvetica">Helvetica</option>
																	<option value="helvetica-bold">Helvetica Bold</option>
																	<option value="helvetica-italic">Helvetica Italic</option>
																	<option value="helvetica-bold-italic">Helvetica Bold Oblique</option>																	
																	<option value="roman">Roman</option>
																	<option value="roman-bold">Roman Bold</option>
																	<option value="roman-italic">Roman Italic</option>
																	<option value="roman-bold-italic">Roman Bold Oblique</option>																	
																	<option value="symbol">Symbol</option>
																	<option value="zapfdingbats">Zapfdingbats</option>																	
																												
																</select>
															</div>	
															<div class="form-row">
																<label>Font Size</label>
																<input type="number" class="leca-rd-report-setup-field" data-key="font_size" data-type="number" value="14">
															</div>								
															<div class="form-row">
																<label>Char Spacing</label>
																<input type="number" class="leca-rd-report-setup-field" data-key="char_space" data-type="number" value="14">
															</div>
															<div class="form-row">
																<label>Paper</label>													
																<select class="leca-rd-report-setup-field" data-key="paper" data-type="select">
																	<option value="A0">A0</option>
																	<option value="A1">A1</option>
																	<option value="A2">A2</option>
																	<option value="A3">A3</option>
																	<option value="A4" selected>A4</option>
																	<option value="A5">A5</option>
																	<option value="A6">A6</option>
																	<option value="A7">A7</option>
																	<option value="A8">A8</option>
																	<option value="A9">A9</option>
																	<option value="A10">A10</option>
																	<option value="Legal">Legal</option>
																	<option value="Letter">Letter</option>
																	<option value="Ledger">Ledger</option>
																</select>
															</div>
															<div class="form-row">
																<label>Orientation</label>													
																<select class="leca-rd-report-setup-field" data-key="orientation" data-type="select">
																	<option value="portrait">Portrait</option>
																	<option value="landscape">Landscape</option>
																</select>
															</div>
															<div class="form-row">
																<label>Page Index</label>													
																<select class="leca-rd-report-setup-field" data-key="index_mode" data-type="select">
																	<option value="continuous">Continuous</option>
																	<option value="collection">Landscape</option>
																</select>
															</div>
															<div class="form-row">
																<label>Page Margin</label>
																<table class="leca-unit-table-container two">
																	<tr data-support="text,media">													
																		<td>
																			<label>Left</label>
																			<input type="number" class="leca-rd-report-setup-field" value="0" data-key="margin_left" data-type="number">
																		</td>
																		<td>
																			<label>Right</label>
																			<input type="number" class="leca-rd-report-setup-field" value="0" data-key="margin_right" data-type="number">
																		</td>
																	</tr>
																	<tr class="leca-rd-margin-top-bottom-form-row" data-support="text,media">
																		<td>
																			<label>Top</label>
																			<input type="number" class="leca-rd-report-setup-field" value="0" data-key="margin_top" data-type="number">
																		</td>
																		<td>
																			<label>Bottom</label>
																			<input type="number" class="leca-rd-report-setup-field" value="0" data-key="margin_bottom" data-type="number">
																		</td>													
																	</tr>
																</table>
															</div>			
															<div class="form-row">
																<label>Global Padding</label>
																<table class="leca-unit-table-container two">
																	<tr data-support="text,media">													
																		<td>
																			<label>Left</label>
																			<input type="number" class="leca-rd-report-setup-field" value="0" data-key="padding_left" data-type="number">
																		</td>
																		<td>
																			<label>Right</label>
																			<input type="number" class="leca-rd-report-setup-field" value="0" data-key="padding_right" data-type="number">
																		</td>
																	</tr>
																	<tr data-support="text,media">
																		<td>
																			<label>Top</label>
																			<input type="number" class="leca-rd-report-setup-field" value="0" data-key="padding_top" data-type="number">
																		</td>
																		<td>
																			<label>Bottom</label>
																			<input type="number" class="leca-rd-report-setup-field" value="0" data-key="padding_bottom" data-type="number">
																		</td>													
																	</tr>
																</table>
															</div>												
															<hr>
															<div class="form-row">
																<label>Title Page</label>												
																<select class="leca-rd-report-setup-field" data-key="title_page" data-type="select">
																	<option value="none">No Title</option>
																	<option value="first">First Page</option>																	
																</select>
															</div>
															<div class="form-row">
																<label>Title</label>
																<input type="text" class="leca-rd-report-setup-field" data-key="title" data-type="text">
															</div>
															<div class="form-row">
																<label>Sub Title</label>
																<input type="text" class="leca-rd-report-setup-field" data-key="subtitle" data-type="text">
															</div>										
														</div>														
													</div>
												</div>								
											</div>							
										</div>
									</div>
								</div>		
							</div>
						</div>
					
					</div>
					
					<div class="leca-context-workspace" id="leca-workspace-dashboard_designer">
					
						<div id="leca-workspace-dashboard_designer-archive">	
							
							<div id="leca-dashboard-tab-header" class="leca-tab-header" data-tab="dashboard-tab">									
								<a href="#leca-dashboard-widget-list" title="" data-target="widgets" class="leca-tab-btn">Widgets</a>										
								<a href="#leca-dashboard-view-list" title="" data-target="dashboard" class="leca-tab-btn">Dashboards</a>																	
							</div>
							<div class="leca-tab-content-container">																		
								<div id="leca-dashboard-widget-list">
								
								</div>
								<div id="leca-dashboard-view-list">
								
								</div>
							</div>
							
						</div>
						
						<div id="leca-workspace-dashboard_designer-single">	
						
						</div>
						
						<div id="leca-workspace-dashboard_designer-create">	
						
						</div>
					
						<div id="leca-workspace-dashboard_designer-designer">	
						
						</div>
					
					</div>
					
					<div class="leca-context-workspace" id="leca-workspace-sales_hierarchy">
					
						<div id="leca-workspace-sales_hierarchy-archive">	
						
						</div>
						
						<div id="leca-workspace-sales_hierarchy-single">	
						
						</div>
						
						<div id="leca-workspace-sales_hierarchy-create">	
						
						</div>
					
					</div>
					
					<div class="leca-context-workspace" id="leca-workspace-access_control">
					
						<div id="leca-workspace-access_control-archive">	
						
						</div>
						
						<div id="leca-workspace-access_control-single">	
						
						</div>
						
						<div id="leca-workspace-access_control-create">	
						
						</div>
					
					</div>
				
				</div>
			
			</div>
		
		</div>	
	
	</body>	
	
</html>