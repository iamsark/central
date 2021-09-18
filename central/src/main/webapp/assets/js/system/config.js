var lecaSystemConfig = {
	report_designer: {
		archive: {
			grids: {
				"report": {
					width: "full",
					height: "full",
					filter: false,
					primary_key: "REPORT_ID",
					source: { method: "GET", action: "list", task: "REPORTS" },
					columns: [
						{ key: "", label: "S.NO", type: "INDEX", width: "5", classes: "", align: "", searchable: false, filterable: false },
						{ key: "REPORT_NAME", label: "Report Name", type: "LINK", width: "70", classes: "", align: "", link: "REPORT_ID", context: "report_designer", searchable: false, filterable: false },
						{ key: "STATUS", label: "Status", type: "TOGGLE", width: "25", align: "right", toggle: { on: { label: "On", value: true }, off: { label: "Off", value: false }, classes: "", tips: "", task: "REPORT_STATUS" }, searchable: false, filterable: false }
					],
					empty_msg: "No Report found.!"
				},
				"report_type": {
					width: "full",
					height: "full",
					filter: false,
					primary_key: "REPORT_TYPE_ID",
					source: { method: "GET", action: "list", task: "REPORT_TYPES" },
					columns: [
						{ key: "", label: "S.NO", type: "INDEX", width: "10", classes: "", align: "", searchable: false, filterable: false },
						{ key: "REPORT_TYPE_NAME", label: "Report Type Name", type: "LINK", width: "45", classes: "", align: "", link: "REPORT_TYPE_ID", context: "report_designer", searchable: false, filterable: false },
						{ key: "", label: "Desinger", type: "BUTTON", width: "20", classes: "ikea-acm-grid-record-btn-action", align: "", link: "REPORT_TYPE_ID", context: "report_designer", btn_value: "Templates" },
						{ key: "STATUS", label: "Status", type: "TOGGLE", width: "25", align: "right", toggle: { on: { label: "On", value: true }, off: { label: "Off", value: false }, classes: "", tips: "", task: "REPORT_STATUS" }, searchable: false, filterable: false }
					],
					empty_msg: "No Report found.!"
				}
			},
			fields: [],
			sub_grids: {},
			actions: [{action: "create", label: "New Report", state: "primary", icon: "note_add"}],
			cbox: [],
			focus: ""
		},
		single: {
			grids: {}, 
			fields: [
				{key: "REPORT_NAME", type: "TEXT", mandatory: true, behavior: "", length: 0}
			],
			sub_grids: {},
			actions: [
				{action: "designer", label: "Designer", state: "primary", icon: "developer_mode"},
				{action: "update", label: "Update Report", state: "primary", icon: "save"},
				{action: "delete", label: "Delete Report", state: "warning", icon: "delete"},
				{action: "cancel", label: "Cancel", state: "secondary", icon: "close"}
			],
			cbox: [],
			focus: ""
		},
		create: {
			grids: {}, 
			sub_grids: {},
			fields: [],
			actions: [
				{action: "save", label: "Save", state: "primary", icon: "save"},
				{action: "cancel", label: "Cancel", state: "secondary", icon: "close"}
			],
			cbox: [],
			focus: ""
		},
		dependencies: []
	},
	dashboard_designer: {
		archive: {
			main_grid: "",
			grids: {
				"widget_list": {
					width: "full",
					height: "full",
					filter: false,
					primary_key: "ID",
					source: { method: "GET", action: "list", task: "WIDGET" },
					columns: [
						{ key: "", label: "S.NO", type: "INDEX", width: "5", classes: "", align: "", searchable: false, filterable: false },
						{ key: "TITLE", label: "Title", type: "LINK", width: "35", classes: "", align: "", link: "ID", context: "dashboard", searchable: false, filterable: false },
						{ key: "HANDLE", label: "Handle", type: "TEXT", width: "35", classes: "", align: "", searchable: false, filterable: false },
						{ key: "STATUS", label: "Status", type: "TOGGLE", width: "25", align: "right", toggle: { on: { label: "On", value: true }, off: { label: "Off", value: false }, classes: "", tips: "", task: "WIDGET_STATUS" }, searchable: false, filterable: false }
					],
					empty_msg: "No Widget found.!"
				},
				"dashboard_list": {
					width: "full",
					height: "full",
					filter: false,
					primary_key: "ID",
					source: { method: "GET", action: "list", task: "DASHBOARD" },
					columns: [
						{ key: "", label: "S.NO", type: "INDEX", width: "5", classes: "", align: "", searchable: false, filterable: false },
						{ key: "TITLE", label: "Title", type: "LINK", width: "35", classes: "", align: "", link: "ID", context: "dashboard", searchable: false, filterable: false },
						{ key: "HANDLE", label: "Handle", type: "TEXT", width: "35", classes: "", align: "", searchable: false, filterable: false },
						{ key: "STATUS", label: "Status", type: "TOGGLE", width: "25", align: "right", toggle: { on: { label: "On", value: true }, off: { label: "Off", value: false }, classes: "", tips: "", task: "WIDGET_STATUS" }, searchable: false, filterable: false }
					],
					empty_msg: "No Widget found.!"
				}
			}, 
			sub_grids: {},
			fields: [],
			actions: [{ action: "new-widget", label: "New Widget", state: "primary", icon: "add" }],
			cbox: [],
			focus: ""
		},
		single: {
			grids: {}, 
			fields: [],
			sub_grids: {},
			actions: [],
			cbox: [],
			focus: ""
		},
		create: {
			grids: {}, 
			sub_grids: {},
			fields: [],
			actions: [],
			cbox: [],
			focus: ""
		},
		dependencies: []
	},
	sales_hierarchy: {
		archive: {
			main_grid: "",
			grids: {}, 
			sub_grids: {},
			fields: [],
			actions: [],
			cbox: [],
			focus: ""
		},
		single: {
			grids: {}, 
			fields: [],
			sub_grids: {},
			actions: [],
			cbox: [],
			focus: ""
		},
		create: {
			grids: {}, 
			sub_grids: {},
			fields: [],
			actions: [],
			cbox: [],
			focus: ""
		},
		dependencies: []
	},
	access_control: {
		archive: {
			main_grid: "",
			grids: {}, 
			sub_grids: {},
			fields: [],
			actions: [],
			cbox: [],
			focus: ""
		},
		single: {
			grids: {}, 
			fields: [],
			sub_grids: {},
			actions: [],
			cbox: [],
			focus: ""
		},
		create: {
			grids: {}, 
			sub_grids: {},
			fields: [],
			actions: [],
			cbox: [],
			focus: ""
		},
		dependencies: []
	}
};