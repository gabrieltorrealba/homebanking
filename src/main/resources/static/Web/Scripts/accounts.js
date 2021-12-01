const app = Vue.createApp({
    data(){
        return{
            data:"",
            firstName:"",
            lastName:"",
            picked:"",
            accounts:[],
            loans:[]
        }
    },
    created(){
        setTimeout(()=> this.createdClientAccount(),2000) 
    },
    methods:{
         createdClientAccount(){
            axios.get('/api/clients/current')
        .then(res=>{
           
        this.data=res.data.current
        this.firstName= this.data.firstName
        this.lastName= this.data.lastName
        this.accounts= this.data.accounts
        this.accounts.sort((a,b)=> parseInt(a.id - b.id))
        this.loans= this.data.loans
        this.loans.sort((a,b)=> parseInt(a.id - b.id))
        this.closeSpinner()
        })
        .catch(e=>{
        alert(e)})
        }, 
        numberFormat(number){
            return new Intl.NumberFormat('en-es', {style: 'currency',currency: 'USD', minimumFractionDigits: 2}).format(number)
        },
        closeSpinner(){
            let spinner = this.$refs.spinner
            let body = this.$refs.body
            spinner.style.display = "none";
            body.style.display = "block";
            
        },
        changePage(account){
            let param= `./account.html?id=${account}`
            return param
        },
        changePageTransfer(account){
            let param= `./transfers.html?id=${account}`
            return param
        },
        logOut(){
            axios.post('/api/logout')
            .then(response=> window.location.replace("/web/index.html"))
        },
        createAccount(){
            let param = new URLSearchParams()
            param.append('type',this.picked)
            axios.post('/api/clients/current/accounts', param, {headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(response=> {this.message = response.data
                swal({
                    title: this.message.toUpperCase(),
                    icon: "success",
                    buttons: false
                  });
                setTimeout(()=>location.reload(),2000)})
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
                setTimeout(()=>location.reload(),2000)
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
        }
    }
})
app.mount("#app")