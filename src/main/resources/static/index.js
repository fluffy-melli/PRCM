Vue.config.devtools = false
Vue.config.productionTip = false
new Vue({
    el: '#app',
    data: {
        status: {
            node: true,
            newnode: false,
            edit: false,
        },
        node: {
            config: null,
            running: null,
            view: 0,
            log: "",
            _debug: {
                label: [],
                memory: []
            },
        },
        formData: {
            file: '',
            node: '',
            workdir: '',
            args: []
        },
        chart: {
            memory: null
        }
    },
    mounted() {
        this.loadnodes()
        setInterval(() => {
            this.loadnodes()
        }, 2000)
    },
    methods: {
        async loadnodes() {
            const response1 = await axios.get('/api/get-config')
            this.node.config = response1.data
            const response2 = await axios.get('/api/get-run-node')
            this.node.running = response2.data
            const response3 = await axios.get('/api/node/get-log/'+this.node.view)
            this.node.log = response3.data
            const response4 = await axios.get('/api/node/usage/'+this.node.view)
            this.node._debug.memory = response4.data["usage-memory"]
            this.node._debug.label = response4.data["label"]
            this.draw_memory_chart()
        },
        async nodestart(id) {
            const response = await axios.get('/api/node/start/'+id)
            if (!response.data) {
                alert("fail")
                return
            }
            this.loadnodes()
        },
        async nodestop(id) {
            const response = await axios.get('/api/node/stop/'+id)
            if (!response.data) {
                alert("fail")
                return
            }
            this.loadnodes()
        },
        view_choice_node(id) {
            this.node.view = id
        },
        nodechanges() {
            this.formData = {
                file: '',
                node: '',
                workdir: '',
                args: []
            }
            if (this.status.edit) {
                this.status.node = false
                this.status.newnode = true
                this.status.edit = false
                return
            }
            this.status.node = !this.status.node
            this.status.newnode = !this.status.newnode
        },
        submitForm() {
            if (this.formData.args.length >= 1) {
                this.formData.args.splice(0, 1)
                this.formData.file = this.formData.args[0]
            }
            axios.post('/api/new-config', this.formData)
                .then(response => {
                    this.status.edit = false
                    this.status.newnode = false
                    this.loadnodes()
                })
                .catch(error => {
                    alert('fail')
                })
            this.formData = {
                file: '',
                node: '',
                workdir: '',
                args: []
            }
        },
        submit_del_config(id) {
            axios.post('/api/del-config', {"id":id})
                .then(response => {
                    this.loadnodes()
                })
                .catch(error => {
                    alert('fail')
                })
        },
        async load_edit_config(id) {
            if (this.status.edit) {
                this.status.edit = false
                this.status.newnode = false
                this.formData = {
                    file: '',
                    node: '',
                    workdir: '',
                    args: []
                }
                return
            }
            const config = (await axios.get('/api/get-config')).data
            if (!(id in config)) {
                alert('fail')
                return
            }
            const args = config[id]?.args ?? []
            if (config[id]?.path ?? false) {
                args.unshift(config[id]?.path)
            }
            this.formData = {
                id: id,
                file: config[id]?.path,
                node: config[id]?.node,
                workdir: config[id]?.workdir,
                args: config[id]?.args ?? []
            }
            this.status.edit = true
            this.status.newnode = true
        },
        async submit_edit_config(id) {
            axios.post('/api/edit-config', this.formData)
                .then(response => {
                    this.formData = {
                        file: '',
                        node: '',
                        workdir: '',
                        args: []
                    }
                    this.status.edit = false
                    this.status.newnode = false
                })
                .catch(error => {
                    alert('fail')
                })
        },
        addArg() {
            this.formData.args.push('')
        },
        removeArg(index) {
            this.formData.args.splice(index, 1)
        },
        draw_memory_chart() {
            if (Object.keys(this.node.config).length == 0) {
                return
            }
            if (this.chart.memory != null) {
                this.chart.memory.data.labels = this.node._debug.label
                this.chart.memory.data.datasets[0].data = this.node._debug.memory
                this.chart.memory.update()
                return
            }
            const ctx = document.getElementById('chart-usage-memory').getContext('2d')
            this.chart.memory = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: this.node._debug.label,
                    datasets: [{
                        label: 'Usage Memory',
                        data: this.node._debug.memory,
                        borderColor: 'rgba(75, 192, 192, 1)',
                        borderWidth: 1,
                        fill: false
                    }]
                },
                options: {
                    scales: {
                        x: {
                            position: 'bottom'
                        },
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    if (value >= 1073741824) { // 1024 * 1024 * 1024
                                        return (value / 1073741824).toFixed(2) + ' GB';
                                    } else if (value >= 1048576) { // 1024 * 1024
                                        return (value / 1048576).toFixed(2) + ' MB';
                                    } else if (value >= 1024) {
                                        return (value / 1024).toFixed(2) + ' KB';
                                    } else {
                                        return value + ' Bytes';
                                    }
                                }
                            }
                        }
                    },
                    animation: {
                        duration: 0
                    },
                    responsive: true,
                    maintainAspectRatio: false
                }
            })
        }
    }
})