Ext.define('MyApp.view.ModifyNodeWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.ModifyNodeWindow',
    height: 100,
    id: 'ModifyNodeWindow',
    width: 300,
    layout: {
        type: 'fit'
    },
    title: '修改',
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	items:[
        		{
        			xtype:'form',
        			id:"ModifyNodeForm",
        			items:[
        				{
        					xtype:'textfield',
        					anchor: '100%',
        					fieldLabel: '目录名',
        					itemId:'ModifiedNodeTextField',
        					name: 'folderName',
        					regex:/^((?![\/:*?"<>|@']).)*$/,
        			    	regexText:"禁止包含字符\/:*?\"<>|@'",
        				},
                        {
                            xtype: 'button',
                            handler: function(button, event) {
                            	var thestore=Ext.getStore('TreeStore');
                            	var form=Ext.getCmp('ModifyNodeForm').getForm();
                            	if(form.isValid()){
                            		var oldpath=Ext.getCmp('Base').theNode.raw.folderName;
                                	var sufix="";
                                	if(Ext.String.endsWith(oldpath,"-leaf")){
                                		sufix="-leaf";
                                	}
                                	if(Ext.String.endsWith(oldpath,"-dir")){
                                		sufix="-dir";
                                	}                            	
                                	var newname = form.findField('folderName').getValue();
                                	var folder=oldpath.substring(0,oldpath.lastIndexOf("/")) +"/"+  newname + sufix;
                                	
                                	thestore.proxy.extraParams={
                                		oldFolderName:oldpath,
                                		folderName:folder
                                	};
                                	
                                	Ext.getCmp('Base').theNode.set("text",newname);
                                	Ext.getCmp('Base').theNode.raw.folderName=folder;
                                	//Ext.getCmp('Base').theNode.expand();
                                	me.close();
                                	
                                	thestore.sync({
                                		success:function(betch,options){
                                			thestore.load();
                                		},
                                		failure:function(betch,options){
                                			Ext.Msg.alert("错误",thestore.getProxy().getReader().rawData.msg);
                                			thestore.load();
                                		}
                                	});
                            	}
                            },
                            text: '修改'
                        }        				
        			]
        		}
        	]
        });
        me.callParent(arguments);
        Ext.ComponentQuery.query('#ModifiedNodeTextField')[0].setValue(Ext.getCmp('Base').theNode.raw.text);
    }
});