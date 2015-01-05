Ext.define('MyApp.model.ScheduledJob', {
    extend: 'Ext.data.Model',
    idProperty:'jobName',
    fields: [
		{
		    name: 'jobName',
		    type: 'string'
		},
        {
            name: 'dirPath',
            type: 'string'
        },
        {
            name: 'cronExpression',
            type: 'string'
        },
        {
            name: 'status',
            type: 'string'
        }
    ],

    validations: [
        {
            type: 'length',
            field: 'dirPath',
            min: 1,
        },
        {
            type: 'length',
            field: 'cronExpression',
            min: 1,
        }
    ]
});