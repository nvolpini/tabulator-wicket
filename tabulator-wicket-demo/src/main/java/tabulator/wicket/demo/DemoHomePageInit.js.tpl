const ${tableVarName} = new Tabulator("#${markupId}", {
  layout: "fitColumns",
  selectableRows: true,
  ajaxURL: "/api/demo1",
  columns: [
    {title: "ID", field: "id"},
    {title: "Nome", field: "nome"},
    {title: "Valor", field: "valor"}
  ]
});
