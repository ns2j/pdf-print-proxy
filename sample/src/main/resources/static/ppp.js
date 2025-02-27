export class Ppp {
   static URL = 'http://localhost:6753' 

   static async getError(response) {
     const json = await response.json()
     console.log(json)
     return new Error(`response error.\nurl: ${response.url}\nstatus: ${json.error}(${response.status})\nmessage: ${json.message}`)
   }
    
   static async getPrinterNames() {
     let r
     try{
       r = await fetch(Ppp.URL)
     } catch (e) {
       throw new Error('error occured!\nis pdf-print-proxy running?')
     }
     if (!r.ok)
         throw await Ppp.getError(r)
     return await r.json()
   }

   static async getBase64(response) {
     const blob = await response.blob()
     return new Promise((onSuccess, onError) => {
       try {
         const reader = new FileReader()
         reader.onload = function() {onSuccess(this.result)}
         reader.readAsDataURL(blob)
       } catch (e) {
         onError(e)
       }
     })
   }
   
   static async getBase64Pdf(response) {
     const b64 = await Ppp.getBase64(response)
     return b64.replace(/^.*,/, '')
   }
   
   static async print(printerName, pdf) {
     let r
     try{
       r = await fetch(Ppp.URL, {
         method: 'post',
         headers: {
           'Content-Type': 'application/json'
         },
         body: JSON.stringify({
           printerName: printerName,
           pdf: pdf
           })
         })
     } catch (e) {
       throw new Error('error occured!\nis pdf-print-proxy running?')
     }
     if (!r.ok)
       throw await Ppp.getError(r)
     return r
  }
}

export class PppSelect {
  constructor(selectElem) {
    this.selectElem = selectElem
  }

  addOptionTag(text, value) {
    const o = document.createElement("option")
    o.text = text
    o.value = value
    this.selectElem.appendChild(o)
  }

  addPrinterNames(printerNames) {
    this.addOptionTag("System Default Printer", "")
    printerNames.names.forEach(n => this.addOptionTag(n, n))
  }

  getSelectedPrinterName() {
    return this.selectElem.selectedOptions[0].value
  }
}

