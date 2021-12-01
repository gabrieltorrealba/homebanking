const app = Vue.createApp({
    data(){
        return{
        clientes: [],
        data:"",
        nombre:"",
        apellido:"",
        email:"",
        nombreModificado:"",
        apellidoModificado:"",
        emailModificado:"",
        clientesEliminar:[],
        inputModificar: "",
        search:"",
        payments:[],
        add: 0,
        loanName:"",
        loanMaxAmount:0,
        percentage: 0,
       
        }
    },
    created(){
        this.createdClient()
    },
    methods:{
        createdClient(){
            axios.get("/rest/clients")
        .then(res=>{
        this.clientes=res.data._embedded.clients
        this.data=res.data
        })
        .catch(e=>{
        alert(e)})
        },
        sendUserData(client){
            client.preventDefault()
            axios.post("/rest/clients", {
            firstName: this.nombre,
            lastName: this.apellido,
            email: this.email
            }).then(resp=> {
            this.createdClient()
            this.nombre=""
            this.apellido=""
            this.email=""
            }).catch(e=>{
            alert(e)})
        },
         
        modificarCliente(cliente){
        axios.patch(cliente, {
        firstName : this.nombreModificado,
        lastName: this.apellidoModificado,
        email: this.emailModificado
        }).then(res=> {this.createdClient()})
        this.nombreModificado=""
        this.apellidoModificado=""
        this.emailModificado=""
        },
        agregarInput(e){
                 this.inputModificar=e.target.id
                 this.nombreModificado=e.target.value
                 console.log(e)

                },
        cerrarInput(){
        this.inputModificar=""
        },
        searchClient(e){
            e.preventDefault()
        },
        logOut(){
            axios.post('/api/logout')
            .then(response=> window.location.replace("/web/index.html"))
        },
        paymentsA(e){
            e.preventDefault()
            this.payments.push(this.add)
            this.add = 0
         },
         createdLoan(){
             axios.post('/api/loans/created',{
                name: this.loanName,
                maxAmount: this.loanMaxAmount,
                percentage: this.percentage,
                payments: this.payments
             }, { headers:{
                'Content-Type': 'application/json'
            }})
            .then(response =>{
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
    },
    computed:{
        disableBtn(){
            if (this.loanName.length > 0 && this.loanMaxAmount > 0 && this.percentage > 0 && this.payments.length > 0) {
                return false
            } return true
        },
        disableBtn2(){
            if(this.add > 0){
                return false
            } return true
        }
    }
})
app.mount("#app")