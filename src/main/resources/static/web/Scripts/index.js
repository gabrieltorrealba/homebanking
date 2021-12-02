const app = Vue.createApp({
    data(){
        return{
        email:"",
        password:"",
        registerFirstName:"",
        registerLastName: "",
        registerEmail: "",
        registerPassword: "",
        disable: true,
        message:"",
        picked:""
        }
    },
    created(){
    
    },
    methods:{
        logIn(email,password){
            let param = new URLSearchParams()
            param.append('email', this.lowerCaseMail(email))
            param.append('password', password)
            axios.post('/api/login',param,
            {headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(response =>{

                 this.email.includes("@admin.com")  ? window.location.assign("/manager.html") : window.location.assign("/web/accounts.html")
            })
            .catch(e=> {
                console.log(e.response.data.status)
                if (e.response.data.status == 409) {
                    swal({
                        title: "ERROR",
                        text: "Ya posee sesión iniciada",
                        icon: "warning",
                        buttons: "Cerrar",
                        dangerMode: true
                      });
                }
                if (e.response.data.status == 401) {
                    swal({
                        title: "ERROR",
                        text: "Algún dato es incorrecto",
                        icon: "warning",
                        buttons: "Cerrar",
                        dangerMode: true
                      }); 
                }
                
                })
        },
        capitalizeName(name){
            name = name.toLowerCase()
            return name[0].toUpperCase() + name.slice(1)
        },
        lowerCaseMail(mail){
            return mail.toLowerCase()
        },
        registrationUser(event){
            event.preventDefault()
            let param = new URLSearchParams()
            param.append('firstName',this.capitalizeName(this.registerFirstName))
            param.append('lastName',this.capitalizeName(this.registerLastName))
            param.append('email', this.lowerCaseMail(this.registerEmail))
            param.append('password', this.registerPassword)
            param.append('type', this.picked)
            axios.post('/api/clients', param, 
            {headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(response=>{
                this.message = response.data
                swal({
                    title: this.message.toUpperCase(),
                    text: "Redireccionando...",
                    icon: "success",
                    buttons: false
                  });
                setTimeout(()=>this.logIn(this.lowerCaseMail(this.registerEmail),this.registerPassword),3000)
        }).catch(e=> {
            this.message = e.response.data
            swal({
                title: "ADVERTENCIA",
                text: this.message,
                icon: "warning",
                buttons: "Cerrar",
                dangerMode: true
              });
        })
        },
        registerPrev(){
            let next = this.$refs.form1
            let next1 = this.$refs.form2
            let next2 = this.$refs.btn
            let next3 =  this.$refs.color1
            let next4 =  this.$refs.color2
            next.style.left = "800px";
            next1.style.left= "0px";
            next2.style.left= "125px";
            next3.style.color= "black";
            next4.style.color= "#fff";
        },
        loginPrev(){
            let next = this.$refs.form1
            let next1 = this.$refs.form2
            let next2 = this.$refs.btn
            let next3 =  this.$refs.color1
            let next4 =  this.$refs.color2
            next.style.left = "0px";
            next1.style.left= "-800px";
            next2.style.left= "0px";
            next3.style.color= "#fff";
            next4.style.color= "black";
        }  
    },
    computed:{
        disableBottom(){
            if(this.email.length > 0 && this.password.length > 0){
                return false
            } return true
        },
        disableRegister(){
            if(this.registerEmail.length > 0 && this.registerFirstName.length > 0 && this. registerLastName.length > 0 && this.registerPassword.length > 0 && this.picked.length > 0){
                return false
            } return true
        }
    }
})
app.mount("#app")
