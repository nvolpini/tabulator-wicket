const __tableVarName__ = new Tabulator("#__markupId__", {
  layout: "fitColumns",
  selectableRows: true,
  ajaxURL: "__url__",
  columns: [
    {title: "ID", field: "id"},
    {title: "Nome", field: "nome"},
    {title: "Valor", field: "valor"}
  ]
});
