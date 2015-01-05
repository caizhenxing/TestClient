var store = Ext.create('Ext.data.TreeStore', {
	storeId:"TreeStore",
    root: {
        text: '根节点',
        id: 'root',
        folderName:'root',
        expanded: true
    },
    proxy: {
        type: 'ajax',
        api: {
            create: 'job/addNode',
            read: 'job/gettree',
            update: 'job/modifyNode',
            destroy: 'job/delNode'                
        },
        reader: {
            type: 'json',
            messageProperty: 'msg'
        }
    }
});

Ext.define('MyApp.view.Base', {
    extend: 'Ext.container.Viewport',
    id: 'Base',
    layout: {
        type: 'border'
    },
    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
			{
				xtype: 'panel',
				bodyStyle:"background-image:url('image/head.png')",
				region: 'north',
				flex:1
			},
			{
				region: 'center',
				flex:20,
				layout:'border',
				items:[
				{
					xtype: 'tabpanel',
				    flex: 17,
				    region: 'west',
				    listeners:{
				    	'tabchange':function( tabPanel, newCard, oldCard, eOpts ){
							Ext.getCmp('MainContainer').removeAll(false);
				    	}
				    },
				    activeTab:0,
				    items:[
					{
						title:"测试配置环境",
						layout: {
					        type: 'fit'
					    },
						items:[
						{
						    xtype: 'treepanel',
						    id:'tree',
				//		    layout: {
				//		        type: 'fit'
				//		    },
				//		    flex: 17,
				//		    region: 'west',
						    title: '文档结构',
						    store: store,
						    rootVisible: true,
						    listeners: {
						        itemmousedown : {
						            fn: me.itemmousedown,
						            scope: me
						        },
						        afterrender : {
						        	fn: me.afterrender,
						            scope: me
						        }
						    },
						    viewConfig: {
						        plugins: {
						            ptype: 'treeviewdragdrop'
						        },
						        copy: true,
						        listeners: {
						        	beforedrop : {
						        		fn: me.beforedrop,
						                scope: me
						        	},
						        	drop : {
						        		fn: me.drop,
						                scope: me
						        	},
						        }
						    },
						    dockedItems:[
						    {
						    	xtype:'toolbar',
						    	dock:'top',
						    	autoScroll: true,
						    	items:[
								{
									xtype:'button',
									text:'插入目录',
									handler:function(){
										me.checkLeaf(me.addFolder);
										Ext.getCmp('MainContainer').removeAll(false);
									}
								},
								{
									xtype:'tbseparator'
								},
								{
									xtype:'button',
									text:'插入测试',
									handler:function(){
										me.checkLeaf(me.addItem);
										Ext.getCmp('MainContainer').removeAll(false);
									}					    			
								},	
								{
									xtype:'tbseparator'
								},
								{
									xtype:'button',
									text:'删除节点',
									handler:function(){
										if(Ext.getCmp('tree').getSelectionModel().getSelection()[0].raw.folderName!='root'){
											Ext.widget('DeletedModeWindow').show();
											Ext.getCmp('MainContainer').removeAll(false);
										}else
											Ext.Msg.alert("警告","不能删除root！");
									}    			
								},
								{
									xtype:'tbseparator'
								},					    		
								{
									xtype:'button',
									text:'修改节点',
									handler:function(){
										me.checkNotRoot(me.modifyNode);
										Ext.getCmp('MainContainer').removeAll(false);
									}					    			
								},
								{
									xtype:'tbseparator'
								},					    		
								{
									xtype:'button',
									tooltip: '同一目录下批量复制测试',
									//icon: 'image/copy.png',
						            text: '批量复制',
									handler:function(){
										me.checkIfLeafNode(me.batchedCopyTest);
										Ext.getCmp('MainContainer').removeAll(false);
									}					    			
								}]
						    },
						    {
						    	xtype:'toolbar',
						    	dock:'bottom',
						    	items:[
						    	{
						    		xtype:'radiogroup',
						    		fieldLabel : "查找",
						    		labelWidth: 30,
						    		itemId:'nodeType',
						    		layout: 'hbox',
						    		items:[
									{
										boxLabel: "测试名", name: 'istest', checked:true
									},  
									{
										boxLabel: "目录名", name: 'istest'
									}]
						    	},
						    	{
						    		xtype:'textfield',
						   	    	id:'NodeSearchedText',
						   	    	layout : 'auto',
						   	    	flex:6,
						   	    	emptyText:'输入节点包含文字按回车查找',
						   	    	listeners: {
						                specialkey: function(field, e){
						                	var searchtext=Ext.getCmp('NodeSearchedText').getValue();
						                	var istest=Ext.ComponentQuery.query('#nodeType')[0].items.items[0].checked;
						                    if (e.getKey() == e.ENTER) {
						                    	Ext.Ajax.request( {
													url : 'job/getNodeIdByText',  
													params : {  
														text : searchtext,
														isTest : istest
													},
												    success : function(response, options) {
												    	var id=Ext.decode(response.responseText);
												    	if(id!=""){
												    		var node=Ext.getStore('TreeStore').getNodeById(id);
												    		Ext.getCmp('tree').expandPath(node.getPath(), 'id','/', function(success, lastNode) {
													    		Ext.getCmp('tree').getSelectionModel().select(node);
													    		Ext.getCmp('tree').fireEvent('itemmousedown',null,node);
													    	});
												    	}else{
												    		var type=istest?"测试":"目录";
												    		Ext.Msg.alert("警告","未找到包含名称'"+searchtext+"'的"+type+"节点！");
												    	}
												    },
												    failure: function(response, opts) {
										             	Ext.Msg.alert("错误","查找节点失败");
										            }
												});
						                    }
						                }
						            }
						    	}]
						    }]
						}]
					},
					{
						title:"测试运行环境",
						//id: 'RunEnvTab',
						layout: {
					        type: 'fit'
					    },
					    items:[
					    {
				            xtype: 'gridpanel',
				            title: '列表',
				            autoFill : true,
				            store: 'RunningSet',
				            stripeRows : true,
				            listeners: {
						        itemmousedown : {
						            fn: me.griditemmousedown,
						            scope: me
						        }
						    },
				            columns: [
							{
							    xtype: 'gridcolumn',
								flex:79,
							    dataIndex: 'name',
							    text: '运行集合',
							},
				            {
							    xtype: 'gridcolumn',
							    dataIndex: 'time',
							    text: '创建/修改时间',
							    flex: 58
							},
							{
							    xtype: 'gridcolumn',
							    dataIndex: 'author',
							    text: '作者',
							    flex: 37,
							},
							{
				                xtype: 'actioncolumn',
				                text: '修改',
				                flex:20,
				                items: [
				                {
				                    handler: function(view, rowIndex, colIndex, item, e, record, row) {
				                    	Ext.getCmp('Base').folderName=record.raw.name+'@'+record.raw.time+'@'+record.raw.author;
				                    	Ext.Ajax.request( {
											url : 'job/getSelectedTestPaths',
											params : {  
												foldername : Ext.getCmp('Base').folderName
											},
										    success : function(response, options) {
										    	var json=Ext.decode(response.responseText);
										    	if(json.success){
										    		Ext.getCmp('Base').SelectedTests=json.obj;
										    		Ext.Ajax.request( {
					        							url : 'job/getSelectedTree',
					        							method:'POST',
					        							params : {  
					        								testset : json.obj
					        							},
					        						    success : function(response, options) {
					        						    	var treestore=Ext.decode(response.responseText);
					        						    	if(treestore!=null){
					        						    		Ext.getStore('SelectedTreeStore').getProxy().data = treestore;
					        						    		Ext.getStore('SelectedTreeStore').load();
					        						    		Ext.widget('RunningSetUpdateWindow').show();
					        						    	}else
					        						    		Ext.Msg.alert("错误","The store of the tree is null!");
					        						    },
					        						    failure: function(response, opts) {
					        						    	Ext.Msg.alert("错误","获取已选择节点树失败");
					        				            }
					        						});
										    	}else
										    		Ext.Msg.alert("错误","未找到文件或读文件异常");
										    },
										    failure: function(response, opts) {
										    	Ext.Msg.alert("错误","获取测试集合失败");
								            }
										});
				                    },
				                    icon: 'image/edit.png',
				                    tooltip: 'edit'
				                }]
				            },
				            {
				                xtype: 'actioncolumn',
				                text: '删除',
				                flex:20,
				                items: [
				                {
				                    handler: function(view, rowIndex, colIndex, item, e, record, row) {
				                    	Ext.MessageBox.confirm(
				                        "confirm",
				                        "确认删除？",
				                        function(e){
				                            if(e=='yes'){
				                                Ext.getStore('RunningSet').removeAt(rowIndex);
				                                Ext.getStore('RunningSet').sync({});
				                            }
				                        }); 
				                    },
				                    icon: 'image/delete.png',
				                    tooltip: 'delete'
				                }]
				            }],
				            dockedItems: [
				            {
				                xtype: 'toolbar',
				                dock: 'top',
				                items: [
				                {
				                    xtype: 'button',
				                    handler: function(button, event) {
				                    	Ext.widget('RunningSetSettingWindow').show();
				                    },
				                    icon: 'image/add.png',
				                    tooltip: '新增运行集合'
				                },
				                {
				                    xtype: 'tbseparator'
				                },
				                {
				                    xtype: 'button',
				                    handler: function(button, event) {
				                        Ext.getStore('RunningSet').load();
						    			Ext.getCmp('MainContainer').removeAll(false);
				                    },
				                    icon: 'image/refresh.png',
				                    tooltip: '刷新'
				                }]
				            }]
				        }],
				        listeners: {
				            activate: {
				                fn: function(window, eOpts) {
				            		Ext.getStore('RunningSet').load();
				                },
				                scope: me
				            }
				        }
					}]
				},
				{
					xtype:'container',
					flex:50,
				    region: 'center',
				    id:'MainContainer',
				    layout: {
						type: 'fit'
					}
				}
				]
			},
			]
        });
        me.callParent(arguments);
    },
    beforedrop : function(node, data, overModel, dropPosition, dropHandlers) {
	    dropHandlers.wait = true;
	    Ext.MessageBox.confirm('Drop', '确定要拷贝？', function(btn){
	        if (btn === 'yes') {
	            dropHandlers.processDrop();
	        } else {
	            dropHandlers.cancelDrop();
	        }
	    });
	},
	drop : function(node, oldParent, newParent, index, eOpts ){
		var oldPath=Ext.getCmp('Base').folderName;
		var newPath=newParent.raw.folderName;
		Ext.Ajax.request( {  
			url : 'job/copyNodeWithoutHistory',  
			params : {  
				srcPath : oldPath,
				targetPath : newPath  
			},  
		    success : function(response, options) {
		    	Ext.getStore('TreeStore').load();
		    },  
		    failure: function(response, opts) {
             	Ext.Msg.alert("错误","请求失败");
            }
		});
	},
    itemmousedown:function(view,record, item, index, e, eOpts ){
 		Ext.getCmp('MainContainer').removeAll(false);
 		Ext.getStore('CheckPointResult').loadData([]);
 		if(Ext.String.endsWith(record.raw.folderName,'-leaf')){
 			Ext.getCmp('Base').folderName=record.raw.folderName;
 			var mainpanel = Ext.widget('MainPanel');
 			Ext.getCmp('MainContainer').add(mainpanel);
 			mainpanel.setTitle(Ext.getCmp('Base').folderName);
 		}else if(Ext.String.endsWith(record.raw.folderName,'-dir')){
 			Ext.getCmp('Base').folderName=record.raw.folderName;
 			var panel = Ext.widget('FolderPanel');
 			Ext.getCmp('MainContainer').add(panel);
 			panel.setTitle(Ext.getCmp('Base').folderName);
 		}
 		else if(record.raw.folderName==='root'){
			var panel = Ext.widget('RootPanel');
			Ext.getCmp('MainContainer').add(panel);
 		}
 		location.href='#node='+record.raw.folderName;
 	},
 	griditemmousedown:function(view, record, item, index, e, eOpts ){
		Ext.getCmp('MainContainer').removeAll(false);
		
 		var runningSetFullName=record.raw.name+'@'+record.raw.time+'@'+record.raw.author;
 		Ext.getCmp('Base').folderName=runningSetFullName;
 		
 		var panel = Ext.widget('RunningSetPanel');
 		Ext.getCmp('MainContainer').add(panel);
 		panel.setTitle(record.raw.name);
 		Ext.getCmp('RunningSetStatisticsPanel').add(Ext.widget('TestCaseResultGrid'));
    	Ext.getCmp('RunningSetStatusDistributionPanel').add(Ext.widget('TestStatusDistributionPieChart'));
    	Ext.getCmp('RunningSetPassedRateTrendPanel').add(Ext.widget('TestPassedRateTrendChart'));
    	
 		Ext.getStore('TestCaseResult').proxy.extraParams.dirPath=runningSetFullName;
        Ext.getStore('TestStatusDistribution').proxy.extraParams.dirPath=runningSetFullName;
        Ext.getStore('TestPassedRate').proxy.extraParams.dirPath=runningSetFullName;
        
        Ext.getStore('TestCaseResult').load();
    	Ext.getStore('TestStatusDistribution').load();
    	Ext.getStore('TestPassedRate').load();
 	},
 	afterrender:function(){
 		var url=location.href;
 		if(url.indexOf('#node=')>0 && !new RegExp('#node=$').test(url)){
 			url=url.substring(url.indexOf('#node=')+6).replace(new RegExp('/','gm'), '>');
 	 		if(url.substring(url.length-1)=='>')
 	 			url=url.substring(0, url.length-1);
 	 		var node=Ext.getStore('TreeStore').getNodeById(url);
 	 		if(node!=undefined)
 	 			Ext.getCmp('tree').expandPath(node.getPath(), 'id','/', function(success, lastNode) {
 	 				Ext.getCmp('tree').getSelectionModel().select(node);
 	 				Ext.getCmp('tree').fireEvent('itemmousedown',null,node);
 	 			});	
 		}
 	},
 	checkIfLeafNode:function(thefunction){
 		var selection = Ext.getCmp('tree').getSelectionModel().getSelection();
		if(selection.length!=0){
			var themodel=selection[0];
			if(!themodel.get("leaf")){
				Ext.MessageBox.alert("提示","请选择测试节点！");
			}else{
				thefunction(themodel);
			}
		}else{
			Ext.MessageBox.alert("提示","请选择测试节点！");
		}
 	},
 	checkLeaf:function(thefunction){
 		var selection = Ext.getCmp('tree').getSelectionModel().getSelection();
		if(selection.length!=0){
			var themodel=selection[0];
			if(themodel.get("leaf")){
				Ext.MessageBox.alert("提示","请选择目录");
			}else{
				thefunction(themodel);
			}
		}else{
			Ext.MessageBox.alert("提示","请选择目录");
		}
 	},
 	checkNotRoot:function(thefunction){
 		var selection = Ext.getCmp('tree').getSelectionModel().getSelection();
		if(selection.length!=0){
			var themodel=selection[0];
			if(themodel.isRoot()){
				Ext.MessageBox.alert("提示","不能操作根节点");
			}else{
				thefunction(themodel);
			}
		}else{
			Ext.MessageBox.alert("提示","请选择目标");
		}
 	}, 
 	batchedCopyTest:function(themodel){
 		Ext.widget('CopyTestAtSameDirWindow').show();
 		Ext.getCmp('BatchCopyTestForm').theLeafNode=themodel; 	
 	},
 	addFolder:function(themodel){
 		var win=Ext.widget("AddFolderWindow");
 		win.show();
 		Ext.getCmp('AddFolderForm').theNode=themodel; 			
 	},
 	addItem:function(themodel){
 		var win=Ext.widget("AddItemWindow");
 		win.show();
 		Ext.getCmp('AddItemForm').theNode=themodel;
 	},
 	delNode:function(themodel){
 		themodel.remove(false);
        var thestore=Ext.getStore('TreeStore');
        var path=themodel.raw.folderName;
        thestore.proxy.extraParams={
        	folderName:path
        };
 		Ext.getStore('TreeStore').sync(
 			{
                success:function(betch,options){
                 },
                failure:function(betch,options){
                   Ext.Msg.alert("错误",thestore.getProxy().getReader().rawData.msg);
                   thestore.load();
                }
             }
 		);
 	},
 	modifyNode:function(themodel){
 		Ext.getCmp('Base').theNode=themodel;
 		Ext.widget("ModifyNodeWindow").show();
 	},
 	saveEvn:function(){
 		Ext.Ajax.request({
         	url:'job/saveEnvironment',
         	params: {
         		folderName: Ext.getCmp('Base').folderName,
         		env:Ext.getCmp('EnvEditForm').getForm().findField('var').value
         		} ,
            success: function(response, opts) {
            	Ext.getCmp('EnvEditWindow').close();
            },
             failure: function(response, opts) {
             	Ext.Msg.alert("错误","请求失败");
            }
         });
 	},
 	showEvn:function(){
 		Ext.Ajax.request({
         	url:'job/showEnvironment',
         	params: { folderName: Ext.getCmp('Base').folderName } ,
            success: function(response, opts) {
            	Ext.getCmp('EnvEditForm').getForm().findField('var').setValue(Ext.decode(response.responseText).obj);
            },
             failure: function(response, opts) {
             	Ext.Msg.alert("错误","请求失败");
             	Ext.getCmp('EnvEditForm').getForm().findField('var').setValue("请求失败");
            }
         });
 	},
	PayAction: function (auth, onum, title, amount,callbackhost,paymentdomain,busttype) { 
		//需要配置出来的东西 
		//callbackhost
		//paymentdomain
			//var callbackhost="m.ctrip.com";
			//var paymentdomain="https://secure.ctrip.com";
            var sback, pback; 
            var host = "http://" + callbackhost; 
            sback = "/webapp/lipin/#booking.success?onum=" + onum; 
            pback = "/webapp/lipin/#order.detail?onum=" + onum; 
            pback = encodeURIComponent(host + pback); 
            sback = encodeURIComponent(host + sback); 
            var tokenJson = { 
                oid: onum, 
                bustype: busttype, 
                pback: pback, 
                sback: sback, 
                auth: auth, 
                title: title, 
                amount: amount 
            }; 
            if (onum != "") { 
                //bustype:业务类型token:身份验证数据sback:支付成功回调页面oid:订单号pback:订单详情回调地址 
                tokenJson = encodeURIComponent(this.Base64Enocde(JSON.stringify(tokenJson))); 
                //tokenJson = tokenJson.toString(); 
                var QueryString = ["bustype="+busttype+"&oid=", onum, "&token=", tokenJson].join(""); 
                window.open( paymentdomain + "/webapp/payment/index.html#index?" + QueryString); 
                //this.jump(); 
            } 
     },
	Base64Enocde:function(input) {  
    	var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";  
        var output = "";  
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;  
        var i = 0;  
        input = this._utf8_encode(input);  
        while (i < input.length) {  
            chr1 = input.charCodeAt(i++);  
            chr2 = input.charCodeAt(i++);  
            chr3 = input.charCodeAt(i++);  
            enc1 = chr1 >> 2;  
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);  
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);  
            enc4 = chr3 & 63;  
            if (isNaN(chr2)) {  
                enc3 = enc4 = 64;  
            } else if (isNaN(chr3)) {  
                enc4 = 64;  
            }  
            output = output +  
            _keyStr.charAt(enc1) + _keyStr.charAt(enc2) +  
            _keyStr.charAt(enc3) + _keyStr.charAt(enc4);  
        }  
        return output;  
    } , 
    _utf8_encode: function (string) {  
        string = string.replace(/\r\n/g,"\n");  
        var utftext = "";  
        for (var n = 0; n < string.length; n++) {  
            var c = string.charCodeAt(n);  
            if (c < 128) {  
                utftext += String.fromCharCode(c);  
            } else if((c > 127) && (c < 2048)) {  
                utftext += String.fromCharCode((c >> 6) | 192);  
                utftext += String.fromCharCode((c & 63) | 128);  
            } else {  
                utftext += String.fromCharCode((c >> 12) | 224);  
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);  
                utftext += String.fromCharCode((c & 63) | 128);  
            }  
        }  
        return utftext;  
    }     
});