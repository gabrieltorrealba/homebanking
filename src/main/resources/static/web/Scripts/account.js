const app = Vue.createApp({
    data(){
        return{
        client:[],
        account:[],
        transactions:[],
        numberAccount:"",
        from:"",
        to:"",
        dolarCompra: 0,
        dolarVenta: 0,
        id: 0
        }
    },
    created(){
        this.id = this.searchParams()
        this.createdClientAccount()
        setTimeout(()=> this.createdClient(),2000) 
        this.dolar()
    },
    methods:{
        createdClientAccount(){
            axios.get(`/api/clients/current/accounts/${this.id}`)
        .then(res=>{
        this.account = res.data.account
        this.transactions =this.account.transaction
        this.transactions.sort((a,b)=> parseInt(b.id - a.id))
        this.numberAccount = this.account.number
        console.log(res.data)
        })
        .catch(e=>{
            if (e.response.status == 401) {
                window.location.replace("/web/accounts.html")
            }
        })
        }, 
        createdClient(){
            axios.get("/api/clients/current")
            .then(res=>{
                this.closeSpinner()
                this.client=res.data.current
            })
        },
        searchParams(){
            let query = new URLSearchParams(window.location.search).get("id")
            return query
        },
        closeSpinner(){
            let spinner = this.$refs.spinner
            let body = this.$refs.body
            spinner.style.display = "none";
            body.style.display = "block";
            
        },
        changePageTransfer(id){
            let param= `./transfers.html?id=${id}`
            return param
        },
        formatDate(date){
            return new Date(date).toLocaleDateString('en-gb')
        },
        numberFormat(number){
            return new Intl.NumberFormat('en-es', {style: 'currency',currency: 'USD', minimumFractionDigits: 2}).format(number)
        },
        dolar(){
            axios.get("https://www.dolarsi.com/api/api.php?type=valoresprincipales")
            .then(res=>{
                this.dolarCompra= res.data[0].casa.compra
                this.dolarVenta= res.data[0].casa.venta  
            })
        },
        deleteAccount(id){
            axios.delete(`/api/clients/current/accounts/${id}`)
            .then(response=> {
                this.message = response.data
                swal({
                    title: this.message.toUpperCase(),
                    icon: "success",
                    buttons: false
                  });
                setTimeout(()=>window.location.assign("/web/accounts.html"),2000)
            })
            .catch(e=>{ 
                this.message = e.response.data
                swal({
                    title: this.message.toUpperCase(),
                    icon: "warning",
                    buttons: "Cerrar",
                    dangerMode: true
                  });
            })
        },
        createdPDF(){
            let param = new URLSearchParams()
            param.append('from', this.from)
            param.append('to', this.to)
            param.append('id', this.id)
            axios.post('/api/transactions/created/pdf', param, {responseType: 'blob'})
            .then(response=>{
                let file = response.headers['content-disposition']
                let fileName = decodeURI(file.substring(20))
                let link = document.createElement('a')
                link.href = URL.createObjectURL(response.data)
                link.download = fileName
                link.click()
                link.remove()
                setTimeout(()=>location.reload(),3000)
            })
            .catch(e=>console.log(e.response.data))
        },
        logOut(){
            axios.post('/api/logout')
            .then(response=> window.location.replace("/web/index.html"))
        }
    }
})
app.mount("#app")