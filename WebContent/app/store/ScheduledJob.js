Ext.define('MyApp.store.ScheduledJob', {
    extend: 'Ext.data.Store',

    requires: [
        'MyApp.model.ScheduledJob'
    ],

    constructor: function(cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            model: 'MyApp.model.ScheduledJob',
            storeId: 'ScheduledJob',
            pageSize: 9999,
            proxy: {
                type: 'ajax',
                afterRequest: function(request, success) {
                    if(!success){
                    	Ext.Msg.alert('错误','任务操作失败');
                        return;
                    }
                    else{
                    	if(request.action!='read'){
                    		var obj=request.proxy.reader.rawData;
                        	if(obj.success){
                        		switch (request.action)
        	    				{
        		    				case "create":
    		    						Ext.Ajax.request( {
    										url : 'job/addScheduledJobItem',
    										params : {  
    											jobName : obj.obj.jobName,
    											dirPath : obj.obj.dirPath,
    											cronExpression : obj.obj.cronExpression
    										},
    									    success : function(response, options) {
    									    	Ext.getStore("ScheduledJob").reload();
    									    },
    									    failure: function(response, opts) {
    									    	Ext.Msg.alert("错误","更改任务状态失败");
    							             	Ext.getStore("ScheduledJob").reload();
    							            }
    									});
        		    					break;
        		    				case "destroy":
        		    					Ext.Ajax.request( {
        									url : 'job/deleteScheduledJobItem',
        									params : {  
        										jobName : request.jsonData[0].jobName									  
        									},
        								    success : function(response, options) {
        								    	Ext.getStore("ScheduledJob").reload();
        								    },
        								    failure: function(response, opts) {
        								    	Ext.Msg.alert("错误",obj.msg);
        						            }
        								});
        		    					break;
        		    				case "update":
        		    					Ext.Ajax.request( {
        									url : 'job/updateScheduledJobItem',
        									params : {  
        										jobName : obj.obj.jobName,
        										dirPath : obj.obj.dirPath,
        										cronExpression : obj.obj.cronExpression									  
        									},
        									success : function(response, options) {
        								    	Ext.getStore("ScheduledJob").reload();
        								    },
        								    failure: function(response, opts) {
        								    	Ext.Msg.alert("错误","更改任务状态失败");
        						             	Ext.getStore("ScheduledJob").reload();
        						            }
        								});
        		    					break;
        		    				default:
        		    					break;
        	    				}
                        	}else{
                        		Ext.Msg.alert('错误',obj.msg);
                        		Ext.getStore("ScheduledJob").reload();
                        	}
                    	}	
                    }
                },
                api: {
                	create: 'job/addScheduledJob',
                    read: 'job/getScheduledJobItems',
                    update: 'job/updateScheduledJob',
                    destroy: 'job/deleteScheduledJob'
                },
                reader: {
                    type: 'json',
                    messageProperty: 'msg',
                    root: 'rows'
                },
                writer: {
                    type: 'json',
                    allowSingle: false
                }
            },
            sorters: {
                direction: 'DESC',
                property: 'jobName'
            }
        }, cfg)]);
    }
});