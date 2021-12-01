const app = Vue.createApp({
    data(){
        return{
            data:[],
            firstName:"",
            lastName:"",
            message:"",
            loanId:"",
            selectPayments:0,
            amount:0,
            loans:[],
            payments:[],
            pay:[],
            showPayments:0,
            accounts:[],
            maxAmount:0,
            newAmount:0,
            selectAccount:"",
            loanName:"",
            message:""
        }
    },
    created(){
        this.createdClientLoan()
        setTimeout(()=> this.createdLoans(),2000) 
        
    },
    methods:{
        createdClientLoan(){
            axios.get('/api/clients/current')
        .then(res=>{
        this.data=res.data.current
        this.firstName= this.data.firstName
        this.lastName= this.data.lastName
        this.accounts= this.data.accounts
        })
        .catch(e=>{
        alert(e)})
        },

        createdLoans(){
            axios.get('/api/loans')
            .then(response =>{
                this.loans = response.data.loans
                this.closeSpinner()
            })
        },
        applyLoan(){
            axios.post('/api/loans',{
                id: this.loanId,
                amount: this.amount,
                payment: this.selectPayments,
                account: this.selectAccount
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
        closeSpinner(){
            let spinner = this.$refs.spinner
            let body = this.$refs.body
            spinner.style.display = "none";
            body.style.display = "block";
        },
        numberFormat(number){
            return new Intl.NumberFormat('en-es', {style: 'currency',currency: 'USD', minimumFractionDigits: 2}).format(number)
        },
         nextLoan(){
            let next = this.$refs.formLoan1
            let next1 = this.$refs.formLoan2
            let next2 = this.$refs.stepColLoan1
            let progress = this.$refs.progressLoan
            next.style.left = "-450px";
            next1.style.left = "40px";
            next2.style.color= "#fff";
            progress.style.width ="240px";
            let pay = this.loans.filter(pay => pay.id == this.loanId)
            this.pay = pay
            this.payments = pay[0].payments
            this.maxAmount = pay[0].maxAmount
            this.loanName = pay[0].name
        },
        nextLoan1(){
            let next = this.$refs.formLoan2
            let next1 = this.$refs.formLoan3
            let next2 = this.$refs.stepColLoan2
            let progress = this.$refs.progressLoan
            next.style.left = "-450px";
            next1.style.left = "40px";
            next2.style.color= "#fff";
            progress.style.width ="360px";
        },
        backLoan(){
            let back = this.$refs.formLoan1
            let back1 = this.$refs.formLoan2
            let back2 = this.$refs.stepColLoan1
            let progress = this.$refs.progressLoan
            back.style.left = "40px";
            back1.style.left = "450px";
            back2.style.color= "#333";
            progress.style.width ="120px";
        }, 
        backLoan1(){
            let back = this.$refs.formLoan2
            let back1 = this.$refs.formLoan3
            let back2 = this.$refs.stepColLoan2
            let progress = this.$refs.progressLoan
            back.style.left = "40px";
            back1.style.left = "450px";
            back2.style.color= "#333";
            progress.style.width ="240px";
        }, 
        logOut(){
            axios.post('/api/logout')
            .then(response=> window.location.replace("/web/index.html"))
        }
    },
    computed:{
        calculetePay(){
           // let loansFilter = this.loans.filter(loan=>)
             let interest = (this.amount*this.pay[0].percentage)/100
            let suma = this.amount + interest
           return this.showPayments = suma/this.selectPayments
        },
        botton(){
            this.newAmount= this.maxAmount/10
        },
    }
})
app.mount("#app")