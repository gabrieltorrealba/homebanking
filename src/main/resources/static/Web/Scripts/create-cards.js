const app = Vue.createApp({
    data(){
        return{
            data:[],
            accounts:[],
            firstName:"",
            lastName:"",
            selected:"",
            picked:"",
            message:"",
            selectedAccount:""

        }
    },
    created(){
        setTimeout(()=> this.createdClientCard(),2000)
        
    },
    methods:{
        createdClientCard(){
            axios.get('/api/clients/current')
        .then(res=>{
            this.closeSpinner()
        this.data=res.data.current
        this.accounts= this.data.accounts
        this.firstName= this.data.firstName
        this.lastName= this.data.lastName
        })
        .catch(e=>{
        alert(e)})
        },
        createCard(){
            let param = new URLSearchParams()
            param.append('cardType', this.selected)
            param.append('cardColor', this.picked)
            param.append('accountNumber', this.selectedAccount)
            axios.post('/api/clients/current/cards',param,
            {headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(response =>{
                swal({
                    title: "TARJETA CREADA CON EXITO!",
                    icon: "success",
                    buttons: false
                  });
                  setTimeout(()=>window.location.assign("/web/cards.html"),2000)
            })
            .catch(e=> {
                this.message= e.response.data
                swal({
                    title: this.message.toUpperCase(),
                    icon: "warning",
                    buttons: "Cerrar",
                    dangerMode: true
                  });
               })
        },
        closeSpinner(){
            let spinner = this.$refs.spinner
            let body = this.$refs.body
            spinner.style.display = "none";
            body.style.display = "block";
            
        },
        nextCard(){
            let next = this.$refs.formCard1
            let next1 = this.$refs.formCard2
            let next2 = this.$refs.stepColCard
            let progress = this.$refs.progressCard
            next.style.left = "-450px";
            next1.style.left = "40px";
            next2.style.color= "#fff";
            progress.style.width ="360px";
        },
        backCard(){
            let back = this.$refs.formCard1
            let back1 = this.$refs.formCard2
            let back2 = this.$refs.stepColCard
            let progress = this.$refs.progressCard
            back.style.left = "40px";
            back1.style.left = "450px";
            back2.style.color= "#333";
            progress.style.width ="180px";
        },
        logOut(){
            axios.post('/api/logout')
            .then(response=> window.location.replace("/web/index.html"))
        }
    }
})
app.mount("#app")