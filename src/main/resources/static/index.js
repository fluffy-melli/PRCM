Vue.config.devtools = false
Vue.config.productionTip = false
new Vue({
    el: '#app',
    data: {
        status: {
            node: true,
            newnode: false
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
    beforeDestroy() {
        window.removeEventListener('resize', this.resizeChart)
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
            this.status.node = !this.status.node
            this.status.newnode = !this.status.newnode
        },
        submitForm() {
            this.formData.file = this.formData.args[0]
            this.formData.args.splice(0, 1)
            axios.post('/api/new-config', this.formData)
                .then(response => {
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
        addArg() {
            this.formData.args.push('')
        },
        removeArg(index) {
            this.formData.args.splice(index, 1)
        },
        draw_memory_chart() {
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