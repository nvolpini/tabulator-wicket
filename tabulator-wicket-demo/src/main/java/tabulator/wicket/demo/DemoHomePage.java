package tabulator.wicket.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import tabulator.wicket.TableRowData;
import tabulator.wicket.TabulatorBehavior;
import tabulator.wicket.TabulatorPanel;
import tabulator.wicket.TabulatorTemplateInitializer;
import tabulator.wicket.TabulatorTheme;
import tabulator.wicket.TabulatorUtils;
import tabulator.wicket.behavior.SelectionSubmitBehavior;
import tabulator.wicket.events.DataLoadedEvent;
import tabulator.wicket.events.RowClickEvent;
import tabulator.wicket.events.RowSelectionChangedEvent;

public class DemoHomePage extends WebPage {

	FeedbackPanel feedback;
	FeedbackPanel feedback1;

	public DemoHomePage() {
		
		add(feedback1 = new FeedbackPanel("feedback1"));
		feedback1.setOutputMarkupId(true);

		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);

		Label table = new Label("table");

		add(table);

		final TabulatorBehavior behavior = new TabulatorBehavior(
				new TabulatorTemplateInitializer(DemoHomePage.class, "DemoHomePageInit.js.tpl", Map.of()));
		table.add(behavior);

		//behavior.theme(TabulatorTheme.MIDNIGHT);
		
		behavior.add(new DataLoadedEvent() {
			@Override
			protected void onTableEvent(AjaxRequestTarget target, List<Map<String, Object>> data) {
                info("Dados carregados: "+ data.size());
                target.add(feedback1);


			}
		});
		

		behavior.add(new RowClickEvent() {
		    @Override
		    protected void onEvent(AjaxRequestTarget target, TableRowData rowData) {
		    	//info("Row clicked: " + rowData.getRowId().orElse(null)+" - "+rowData.getRowData().size());
		    	
				//target.add(feedback);
		    	
		    }
		});
		
		add(new AjaxLink<Void>("reload") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				info("Atualizado: "+LocalDateTime.now());
				target.add(feedback);
				behavior.reload(target);
				
			}
		});


		add(new AjaxLink<Void>("showAlert") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				
				behavior.runOnTable(target, "alert('teste!');");
				
				//TabulatorUtils.runOnTable(target, table, (tableVar) -> String.format("%s.alert('teste!')", tableVar)) ;
				
			}
		});


		add(new AjaxLink<Void>("dismissAlert") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				
				behavior.runOnTable(target, "clearAlert();");
				
				//TabulatorUtils.runOnTable(target, table, (tableVar) -> String.format("%s.clearAlert()", tableVar)) ;
				
			}
		});
		
		behavior.add(new RowSelectionChangedEvent() {
		    @Override
		    protected void onSelectionChanged(AjaxRequestTarget target, List<TableRowData> selectedRows) {
		        info("Seleção alterada: " + selectedRows.size() + " linhas");
		        selectedRows.forEach(r->info("Row selected: " + r.getOr("id").orElse(null)));
		        target.add(feedback1);
		    }
		});
		
		SelectionSubmitBehavior selectionBehavior = new SelectionSubmitBehavior() {
		    @Override
		    protected void onSelectionSubmit(AjaxRequestTarget target, List<TableRowData> selectedRows) {
		        info("Selecionados: " + selectedRows.size());
		        selectedRows.forEach(r->info("Row selected: " + r.getInt("id").orElse(null)));
		        target.add(feedback);
		    }
		};
		table.add(selectionBehavior);

		add(new AjaxLink<Void>("getSelection") {

			@Override
			public void onClick(AjaxRequestTarget target) {
			
				selectionBehavior.run(target);
			}
		});

	}
}
