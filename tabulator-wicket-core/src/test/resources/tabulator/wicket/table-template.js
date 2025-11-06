const __tableVarName__ = new Tabulator("#__markupId__", {
	layout: "fitColumns",
	selectableRows: true,
	pagination: true,
	paginationSize:30,
	columnDefaults:{
		headerFilter: true,
		headerSort: true
	},
	columns: [
		{title: "ID", field: "id"},
		{title: "Nome", field: "nome"}
	],
	// comentário permitido
});
