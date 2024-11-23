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
        },
        formData: {
            file: '',
            node: '',
            workdir: '',
            args: []
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
                    console.log('서버 응답:', response.data)
                    alert('폼 제출 성공')
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
        }
    }
})