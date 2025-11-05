const __tableVarName__ = new Tabulator("#__markupId__", {
  layout: "fitColumns",
  selectableRows: true,
  ajaxURL: "/api/demo1",
  columns: [
    {title: "ID", field: "id"},
    {title: "Nome", field: "nome"},
    {title: "Valor", field: "valor"}
  ]
});
