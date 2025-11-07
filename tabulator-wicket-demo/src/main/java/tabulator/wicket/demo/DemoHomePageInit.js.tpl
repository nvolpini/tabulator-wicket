const __tableVarName__ = new Tabulator("#__markupId__", {
  layout: "fitColumns",
  selectableRows: true,
  ajaxURL: "__url__",
  pagination: true,
  	paginationSize:10,
	paginationCounter:"rows",
	paginationSizeSelector:[10,20,40,100, true],
  locale: true,
  columns: [
    {title: "ID", field: "id"},
    {title: "Nome", field: "nome"},
    {title: "Valor", field: "valor"},
    {title: "Computed", field: "valor"
    	, formatter: (cell) => {
			return "Awesome!";
		}
	}
  ]
});
