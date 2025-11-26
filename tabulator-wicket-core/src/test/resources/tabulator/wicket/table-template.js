function testFunctionPre() {
	
}

var statusContextMenu = [
    {
        label:"<i class='fa fa-check text-success'></i> Selecionar",
        action:function(e, cell){
            const row = cell.getRow();
            const data = row.getData();
            
        }
    },
];

const __tableVarName__ = new Tabulator("#__markupId__", {
	layout: "fitColumns",
	selectableRows: true,
	ajaxURL: "__url__",
	pagination: true,
	paginationSize:30,
	columnDefaults:{
		headerFilter: true,
		headerSort: true
	},
	columns: [
		{title: "ID", field: "id"},
		{title: "Nome", field: "nome"
			,formatter: (cell) => {
			  return cell.getValue() + "!";
			}, 
			contextMenu: statusContextMenu

		},
		{title: "Descr", field: "text"
		, formatter: function(cell, formatterParams, onRender){
			        const value = cell.getValue();
			        const maxLength = 30;

			        if (value && value.length > maxLength) {
			            return value.substring(0, maxLength) + "...";
			        } else {
			            return value;
			        }
			    }
			}
		
	],
	// comentário permitido
});

function testFunctionPos() {
	
}
