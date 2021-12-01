const app = Vue.createApp({
    data(){
        return{
            accounts:[],
            firstName:"",
            lastName:"",
            selected:"",
            picked:"",
            id:0,
            accountOriginId:[],
            accountOrigin:[],
            selectedOther:"",
            otherAccounts: [],
            amountTransfer:"",
            description:"",
            message:""
    
        }
    },
    created(){
        this.id=this.searchParams()
        setTimeout(()=> this.createdClient(),2000)
    },
    methods:{
        createdClient(){
            axios.get('/api/clients/current')
        .then(res=>{
        this.firstName= res.data.current.firstName
        this.lastName= res.data.current.lastName
        this.accounts= res.data.current.accounts
        this.accountOriginId = this.accounts.filter(account => account.id == this.id)
        this.accountOrigin = this.accounts.filter(account => account.id != this.id).sort((a,b)=> parseInt(a.id - b.id))
        this.selected= this.accountOriginId[0].number
        this.closeSpinner()
        })
        .catch(e=>{
        alert(e)})
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
        searchParams(){
            let query = new URLSearchParams(window.location.search).get("id")
            return query
        },
        createTransfer(){
            let param = new URLSearchParams()
            param.append('amount', this.amountTransfer)
            param.append('description', this.description)
            param.append('originAccount', this.selected)
            param.append('destinationAccount', this.selectedOther)
            axios.post('/api/transactions',param,
            {headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(response =>{
                this.message = response.data
                swal({
                    title: this.message.toUpperCase(),
                    icon: "success",
                    text: "Desea descargar comprobante pdf?",
                    buttons: ["Cancel", "Descargar"]
                  })
                  .then((pdfDownload) => {
                    if (pdfDownload) {
                        this.createPdf()
                      swal("Se ha descargado comprobante con exito", {
                        icon: "success",
                        buttons: false
                      }); 
                    } else {
                        window.location.assign("/web/accounts.html");
                    }
                  });
            })
            .catch(e=> {
                this.message = e.response.data
                swal({
                    title: this.message.toUpperCase(),
                    icon: "warning",
                    buttons: "Cerrar",
                    dangerMode: true
                  });
               })
        },
        createPdf(){
            let param = new URLSearchParams()
            param.append('accountOrigin', this.selected)
            param.append('accountDestination', this.selectedOther)
            param.append('amount', this.amountTransfer)
            param.append('description', this.description)
            axios.post('/api/transactions/pdf',param, {responseType: 'blob'})
            .then(response=>{
                let file = response.headers['content-disposition']
                let fileName = decodeURI(file.substring(20))
                let link = document.createElement('a')
                link.href = URL.createObjectURL(response.data)
                link.download = fileName
                link.click()
                link.remove()
                setTimeout(()=>window.location.assign("/web/accounts.html"),3000)
            })
            .catch(e=>console.log(e.response.data))
        },
        numberFormat(number){
            return new Intl.NumberFormat('en-es', {style: 'currency',currency: 'USD', minimumFractionDigits: 2}).format(number)
        },
        next1(){
            let next = this.$refs.form1
            let next1 = this.$refs.form2
            let next2 = this.$refs.stepCol2
            let progress = this.$refs.progress
            next.style.left = "-450px";
            next1.style.left = "40px";
            next2.style.color = "#fff";
            progress.style.width ="136px";
        },
        next2(){
            let next = this.$refs.form2
            let next1 = this.$refs.form3
            let next2 = this.$refs.stepCol3
            let progress = this.$refs.progress
            next.style.left = "-450px";
            next1.style.left = "40px";
            next2.style.color = "#fff";
            progress.style.width ="207px";
            this.otherAccounts = this.accounts.filter(account => account.number != this.selected).sort((a,b)=> parseInt(a.id - b.id))
            this.selectedOther =  this.otherAccounts[0].number
        },
        next3(){
            let next = this.$refs.form2
            let next1 = this.$refs.form4
            let next2 = this.$refs.stepCol3
            let progress = this.$refs.progress
            next.style.left = "-450px";
            next2.style.color = "#fff";
            next1.style.left = "40px";
            progress.style.width ="207px";
        },
        next4(){
            let next = this.$refs.form3
            let next1 = this.$refs.form5
            let next2 = this.$refs.stepCol4
            let progress = this.$refs.progress
            next.style.left = "-450px";
            next1.style.left = "40px";
            next2.style.color = "#fff";
            progress.style.width ="280px";
        },
        next5(){
            let next = this.$refs.form4
            let next1 = this.$refs.form5
            let next2 = this.$refs.stepCol4
            let progress = this.$refs.progress
            next.style.left = "-450px";
            next1.style.left = "40px";
            next2.style.color = "#fff";
            progress.style.width ="280px";
        },
        next6(){
            let next = this.$refs.form5
            let next1 = this.$refs.form6
            let next2 = this.$refs.stepCol5
            let progress = this.$refs.progress
            next.style.left = "-450px";
            next1.style.left = "40px";
            next2.style.color = "#fff";
            progress.style.width ="360px";
        },
        back1(){
            let back = this.$refs.form1
            let back1 = this.$refs.form2
            let next2 = this.$refs.stepCol2
            let progress = this.$refs.progress
            back.style.left = "40px";
            back1.style.left = "450px";
            next2.style.color = "#333";
            progress.style.width ="65px";
        },
        back2(){
            let back = this.$refs.form2
            let back1 = this.$refs.form3
            let next2 = this.$refs.stepCol3
            let progress = this.$refs.progress
            back.style.left = "40px";
            back1.style.left = "450px";
            next2.style.color = "#333";
            progress.style.width ="136px";
        },
        back3(){
            let back = this.$refs.form2
            let back1 = this.$refs.form4
            let next2 = this.$refs.stepCol3
            let progress = this.$refs.progress
            back.style.left = "40px";
            back1.style.left = "450px";
            next2.style.color = "#333";
            progress.style.width ="136px";
        },
        back4(){
            let back = this.$refs.form3
            let back1 = this.$refs.form5
            let next2 = this.$refs.stepCol4
            let progress = this.$refs.progress
            back.style.left = "40px";
            back1.style.left = "450px";
            next2.style.color = "#333";
            progress.style.width ="207px";
        },
        back5(){
            let back = this.$refs.form4
            let back1 = this.$refs.form5
            let next2 = this.$refs.stepCol4
            let progress = this.$refs.progress
            back.style.left = "40px";
            back1.style.left = "450px";
            next2.style.color = "#333";
            progress.style.width ="207px";
        },
        back6(){
            let back = this.$refs.form5
            let back1 = this.$refs.form6
            let next2 = this.$refs.stepCol5
            let progress = this.$refs.progress
            back.style.left = "40px";
            back1.style.left = "450px";
            next2.style.color = "#333";
            progress.style.width ="280px";
        },
        logOut(){
            axios.post('/api/logout')
            .then(response=> window.location.replace("/web/index.html"))
        }
    },
})
app.mount("#app")