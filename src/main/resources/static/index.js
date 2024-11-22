new Vue({
    el: '#app',
    data: {
        status: {
            node: true,
            newnode: false
        },
        node: {
            config: [],
            runing: []
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
    },
    methods: {
        async loadnodes() {
            const response1 = await axios.get('/api/get-config')
            this.node.config = response1.data
            const response2 = await axios.get('/api/get-run-node')
            this.node.runing = response2.data
        },
        submitForm() {
            this.formData.file = this.formData.args[0]
            this.formData.args.splice(0, 1)
            axios.post('/api/new-config', this.formData)
                .then(response => {
                    console.log('서버 응답:', response.data)
                    alert('폼 제출 성공')
                })
                .catch(error => {
                    console.error('에러 발생:', error)
                    alert('폼 제출 실패')
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