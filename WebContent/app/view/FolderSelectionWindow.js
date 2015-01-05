var treestore = Ext.create('Ext.data.TreeStore', {
	storeId:"FolderTree",
    root: {
        text: '根节点',
        id: 'root',
        path:'root',
        expanded: true
    }, 
    proxy: {
        type: 'ajax',
        api: {
            read: 'job/getFolderTree'                
        },
        reader: {
            type: 'json',
            messageProperty: 'msg'
        }
    }
});

Ext.define('MyApp.view.FolderSelectionWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.FolderSelectionWindow',
    id: 'FolderSelectionWindow',
    height: 500,
    width: 400,
    layout: {
        type: 'fit'
    },
    title: '设置',
    modal:true,
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
	    	items: [
	    	{
	    		xtype: 'treepanel',
	    		//id: 'foldertree',
	    		title: '目录',
	    		store: treestore,
	    		animate: true,
	    		frame: true,
	    		autoScroll: true,
	    	    border: false,
	    	    useArrows: true,
	    	    trackMouseOver: false,
	    	    lines: false,
	    	    rootVisible: false,
	    	    containerScroll: true,
	    	    listeners: {
	                itemmousedown : function (that, record, item, index, e, eOpts) {
	                	Ext.getCmp("FolderSelectionCombo").setValue(record.raw.path);
	                }
	            }
	    	}]
        });
        me.callParent(arguments);
    }
});