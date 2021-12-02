const app = Vue.createApp({
    data(){
        return{
            cardNumber:"",
            cvv:"",
            amount:"",
            description:""
        }
    },
    created(){
    
    },
    methods:{
        numberFormat(number){
            return new Intl.NumberFormat('en-es', {style: 'currency',currency: 'USD', minimumFractionDigits: 2}).format(number)
        },
        createdPayment(){
            let param = new URLSearchParams()
            param.append('account','VIN-31526387',{
                number: this.cardNumber,
                cvv: this.cvv,
                amount: this.amount
            })
            axios.post('http://localhost:8080/api/payments',{
                number: this.cardNumber.toString(),
                cvv: this.cvv,
                amount: this.amount,
                description: this.description,
                accountNumber: "VIN-31526387"
            })
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
            if (this.cardNumber > 0 && this.cvv > 0 && this.amount > 0 && this.description.length > 0) {
                return false
            } return true
        }
    }
})
app.mount("#app")
