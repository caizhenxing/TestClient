var jobs=[];

Ext.define('MyApp.view.FolderSelectionCombo', {
	extend: 'Ext.form.field.ComboBox',
	alias: 'widget.FolderSelectionCombo',
	id: 'FolderSelectionCombo',
	triggerAction:'all',
	tpl:"<tpl for='.'><div style='height:200px'><div id='tree'></div></div></tpl>",
	selectedClass:'',
	onSelect:Ext.emptyFn,
    fieldLabel: '选择',
    onTriggerClick: function () {
    	Ext.widget("FolderSelectionWindow").show();//this.el, 'tl-bl?');
    }
 });

Ext.define('MyApp.view.CronSettingCombo', {
	extend: 'Ext.form.TriggerField',
	alias: 'widget.CronSettingCombo',
	id: 'CronSettingCombo',
    onTriggerClick: function () {
    	Ext.widget("CronExpressionSettingWindow").show();//this.el, 'tl-bl?');
    }
 });

Ext.define('MyApp.view.ScheduledJobWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.ScheduledJobWindow',
    id: 'ScheduledJobWindow',
    modal:true,
    height: 360,
    width: 770,
    autoScroll: true,
    layout: {
        type: 'fit'
    },
    title: '测试任务定时设置',

    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            items: [
            {
                xtype: 'gridpanel',
                id: 'JobsGrid',
                title: '',
                autoFill : true,
                store: 'ScheduledJob',
                stripeRows : true,
                selModel:Ext.create('Ext.selection.CheckboxModel',{
                	mode: 'MULTI',
                	checkOnly: true,
                	allowDeselect: true,
                	enableKeyNav: true,
                	listeners: {
                		deselect: function(that, record, index, eOpts) {
                			var removedJob=record.raw.jobName;
                			if(removedJob!=undefined){
                				jobs = jobs.join(',').toString().replace(removedJob+',', '').replace(','+removedJob, '').split(',');
                    			Ext.getCmp('pauseBtn').disabled=true;
                    			Ext.getCmp('resumeBtn').disabled=true;
                			}	
                		},
                		select: function(that, record, index, eOpts) {
                			var addedJob=record.raw.jobName;
                			if(addedJob!=undefined){
                				if(jobs.join(',').toString().indexOf(addedJob)==-1){
                    				jobs.push(addedJob);
                    			}
    		                	if(record.raw.status=='active'){
    		                		Ext.getCmp('pauseBtn').disabled=false;
    		                		Ext.getCmp('resumeBtn').disabled=true;
    		                	}
    		                	else if(record.raw.status=='suspend'){
    		                		Ext.getCmp('resumeBtn').disabled=false;
    		                		Ext.getCmp('pauseBtn').disabled=true;
    		                	}
    		                	else{
    		                		Ext.getCmp('pauseBtn').disabled=true;
    		                        Ext.getCmp('resumeBtn').disabled=true;
    		                	}
                			}
		                }
//	                	selectionchange: function(that, selected, eOpts) {	
//	                	}
	                }
                }
                ),
                columns: [
				{
				    xtype: 'gridcolumn',
					flex:14,
				    dataIndex: 'dirPath',
				    text: '被测目录',
				    editor: Ext.widget('FolderSelectionCombo')
				},
				{
				    xtype: 'gridcolumn',
				    flex: 7,
				    dataIndex: 'cronExpression',
				    text: '定时设置',
				    editor: Ext.widget('CronSettingCombo')
				},
                {
				    xtype: 'gridcolumn',
				    dataIndex: 'jobName',
				    text: '创建/修改时间',
				    flex: 7
				},
				{
				    xtype: 'gridcolumn',
				    dataIndex: 'status',
				    text: '状态',
				    flex: 3,
				    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {	
				    	if(value.indexOf('expired')!=-1){
				    		if(!value.indexOf('executed')!=-1){
				    			return '<span style="color: #BDBDBD;font-size:14px">' + "已过期" + '</span>';
				    		}else{
				    			var count = value.replace('executed','').replace('expired','');
				    			return '<span style="color: #BDBDBD;font-size:14px">' + "运行了" + count + "次but过期" + '</span>';
				    		}
				    	}else{
				    		if(value=='active'){
	                            return '<span style="color: blue;font-size:14px">' + "激活" + '</span>';
	                        }else if(value=='suspend'){
	                            return '<span style="color: #FFA500;font-size:14px">' + "暂停" + '</span>';
	                        }
	                        else if(value.indexOf('executed')!=-1){
	                        	var count = value.replace('executed','');
	                            return '<span style="color:green;font-size:14px">' + "已运行" + count + "次" + '</span>';
	                        }
	                        else if(value=='reactive'){
	                            return '<span style="color: blue;font-size:14px">' + "重新激活" + '</span>';
	                        }
				    	}
                    }
				},
                {
                    xtype: 'actioncolumn',
                    flex:1,
                    items: [
                        {
                            handler: function(view, rowIndex, colIndex, item, e, record, row) {
                                Ext.MessageBox.confirm(
                                "confirm",
                                "确认删除？",
                                function(e){
                                    if(e=='yes'){
                                        Ext.getStore('ScheduledJob').removeAt(rowIndex);
                                        Ext.getStore('ScheduledJob').sync();
                                    }
                                }
                                ); 
                            },
                            icon: 'image/delete.png',
                            tooltip: 'delete'
                        }
                    ]
                }                  
                ],
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'top',
                        items: [
                        {
                            xtype: 'button',
                            handler: function(button, event) {
                                var store = Ext.getStore('ScheduledJob');
                                store.insert(0,{});
                                var rowEdit = Ext.getCmp('JobsGrid').getPlugin("JobEditPlugin");
                                rowEdit.startEdit(0,1); 
                            },
                            icon: 'image/add.png',
                            tooltip: '新增定时设置'
                        },
                        {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'button',
                            handler: function(button, event) {
                                Ext.getStore('ScheduledJob').load();
                            },
                            icon: 'image/refresh.png',
                            tooltip: '刷新'
                        },
                        {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'button',
                            id: 'pauseBtn',
                            handler: function(button, event) {
                            	Ext.Ajax.request( {
									url : 'job/pauseScheduledJobs',
									params : {  
										jobs : jobs								  
									},
								    success : function(response, options) {
								    	var obj=JSON.parse(response.responseText).obj;
								    	Ext.Ajax.request( {
											url : 'job/setJobStatusSuspend',
											params : {  
	    										jobs : jobs								  
	    									},
										    success : function(response, options) {	
										    	Ext.getStore('ScheduledJob').reload();
										    },
										    failure: function(response, opts) {
								             	Ext.Msg.alert("更改任务状态失败");
								            }
										});
								    },
								    failure: function(response, opts) {
						             	Ext.Msg.alert("暂停任务出错误");
						            }
								});
                            },
                            icon: 'image/pause.png',
                            tooltip: '暂停'
                        },
                        {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'button',
                            id: 'resumeBtn',
                            handler: function(button, event) {
                            	Ext.Ajax.request( {
									url : 'job/resumeScheduledJobs',
									params : {  
										jobs : jobs								  
									},
								    success : function(response, options) {
								    	var obj=JSON.parse(response.responseText).obj;
								    	Ext.Ajax.request( {
											url : 'job/setJobStatusReactive',
											params : {  
	    										jobs : jobs								  
	    									},
										    success : function(response, options) {	
										    	Ext.getStore('ScheduledJob').reload();
										    },
										    failure: function(response, opts) {
								             	Ext.Msg.alert("更改任务状态失败");
								            }
										});
								    },
								    failure: function(response, opts) {
						             	Ext.Msg.alert("恢复任务出错误");
						            }
								});
                            },
                            icon: 'image/resume.png',
                            tooltip: '恢复'
                        },
                    ]
                }
                ],
                plugins: [
                    Ext.create('Ext.grid.plugin.RowEditing', {
                        pluginId: 'JobEditPlugin',
                        autoCancel:true,
                        listeners: {
                            edit: {
                                fn: me.onJobEdit,
                                scope: me
                            }
                        }
                    })
                ]
            }],
            listeners: {
            	show: {
                    fn: me.onWindowShow,
                    scope: me
                }
            }
        });
        
        me.callParent(arguments);
    },

    onJobEdit: function(editor, context, eOpts) {
        Ext.getStore('ScheduledJob').sync({
            success:function(){
                Ext.getStore('ScheduledJob').load();
            }
        });
    },
    
    onWindowShow: function(window, eOpts) {
		Ext.getStore('ScheduledJob').load();
		Ext.getCmp('pauseBtn').disabled=true;
        Ext.getCmp('resumeBtn').disabled=true;
    }    
    
});