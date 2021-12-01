const app = Vue.createApp({
    data(){
        return{
            data:[],
            firstName:"",
            lastName:"",
            cards:[],
            debitCards:[],
            creditCards:[],
            v:0
        }
    },
    created(){
        setTimeout(()=> this.createdClientCard(),2000) 
        
    },
    methods:{
        createdClientCard(){
            axios.get('/api/clients/current')
        .then(res=>{
        this.data=res.data.current
        this.firstName= this.data.firstName
        this.lastName= this.data.lastName
        this.cards= this.data.cards
        this.cards.sort((a,b)=> parseInt(a.id - b.id))
        this.debitCards= this.cards.filter(card=> card.type == "DEBIT")
        this.creditCards= this.cards.filter(card=> card.type == "CREDIT")
        this.closeSpinner() 
        })
        .catch(e=>{
        alert(e)})
        },
        toString(number){
            number.toString().charAt(0)
            number = parseInt(this.v)
            return number
        },
        sliceNumber(number){
            return number.toString().slice(-4)
        },
        toUpperCase(name){
            return name.toUpperCase()
        },
        sliceDate(date){
            return date.slice(5,7)+"/"+date.slice(2,4)
        },
        closeSpinner(){
            
            let spinner = this.$refs.spinner
            let body = this.$refs.body
            spinner.style.display = "none";
            body.style.display = "block";  
        },
        deleteCard(id){
            axios.delete(`/api/clients/current/cards/${id}`)
            .then(response=>{
                this.message = response.data
                swal({
                    title: this.message.toUpperCase(),
                    icon: "success",
                    buttons: false
                  });
                setTimeout(()=>location.reload(),2000)
            } )
        },
        expiredCard(date){
            let today = new Date()
            let expired = new Date(date)
            return today > expired
        },
        logOut(){
            axios.post('/api/logout')
            .then(response=> window.location.replace("/web/index.html"))
        }
    }
})
app.mount("#app")