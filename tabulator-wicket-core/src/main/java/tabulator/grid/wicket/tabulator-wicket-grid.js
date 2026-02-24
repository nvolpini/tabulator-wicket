(function(){

    window.tabulator = window.tabulator || {};
    window.tabulator.wicket = window.tabulator.wicket || {};
    window.tabulator.wicket.grid = window.tabulator.wicket.grid || {};

    const grid = window.tabulator.wicket.grid;
	
	grid.defaults = {
	    layout: "fitColumns"
	};

	grid.registry = {
	    formatters: {},
	    callbacks: {},
	    ajax: {},
	    mutators: {},
	    validators: {}
	};
	
	grid.resolveFunctionValue = function(value){

	    if (typeof value !== "string") {
	        return value;
	    }

	    // 1️⃣ tentar path completo
	    let resolved = grid.resolvePath(value);
	    if (typeof resolved === "function") {
	        return resolved;
	    }

	    // 2️⃣ tentar registry formatters
	    if (grid.registry.formatters[value]) {
	        return grid.registry.formatters[value];
	    }

	    // 3️⃣ callbacks
	    if (grid.registry.callbacks[value]) {
	        return grid.registry.callbacks[value];
	    }

	    // 4️⃣ ajax
	    if (grid.registry.ajax[value]) {
	        return grid.registry.ajax[value];
	    }

	    // 5️⃣ mutators
	    if (grid.registry.mutators[value]) {
	        return grid.registry.mutators[value];
	    }

	    return value; // não resolve → mantém string
	};
	
	grid.merge = function(base, override){

	    const result = Object.assign({}, base);

	    for (const key in override) {

	        const value = override[key];

	        if (value
	            && typeof value === "object"
	            && !Array.isArray(value)
	            && typeof result[key] === "object"
	            && !Array.isArray(result[key])) {

	            result[key] =
	                grid.merge(result[key], value);

	        } else {
	            result[key] = value;
	        }
	    }

	    return result;
	};
	
	grid.resolvePath = function(path){

	    if (typeof path !== "string") return null;

	    const parts = path.split(".");
	    let ctx = window;

	    for (const p of parts) {
	        if (ctx[p] == null) return null;
	        ctx = ctx[p];
	    }

	    return ctx;
	};
	
	grid.resolveFunctions = function(obj){

	    if (!obj || typeof obj !== "object") {
	        return obj;
	    }

	    for (const key in obj) {

	        const value = obj[key];

	        if (typeof value === "string") {

	            obj[key] = grid.resolveFunctionValue(value);

	        } else if (typeof value === "object") {

	            grid.resolveFunctions(value);
	        }
	    }

	    return obj;
	};
	
    grid.create = function(config){

        if (!config || !config.elementId) {
            throw new Error("GridConfig precisa de elementId");
        }

        // 1️⃣ merge defaults
        let finalOptions = grid.merge(
            grid.defaults,
            config.options || {}
        );

        // 2️⃣ columns
        finalOptions.columns = config.columns || [];

        // 3️⃣ ajaxUrl (neutro)
        if (config.ajaxUrl) {
            finalOptions.ajaxURL = config.ajaxUrl;
        }

        // 4️⃣ persistence
        if (config.persistenceId) {
            finalOptions.persistenceID = config.persistenceId;
        }

        // 5️⃣ resolver functions por path
        finalOptions =
            grid.resolveFunctions(finalOptions);

        // 6️⃣ criar Tabulator
        return new Tabulator(
            "#" + config.elementId,
            finalOptions
        );
    };

})();
	