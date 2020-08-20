package apigw

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_ADMIN','ROLE_DEV','ROLE_USER'])
@Transactional(readOnly = false)
class GwXmlController {

    def index(Integer max) {
        respond GwXml.list(params), model:[gwXmlCount: GwXml.count()]
    }

    def show(GwXml gwXml) {
        this.flash.message=flash.message
        this.flash.error=flash.error
        respond gwXml,[isSave:false,isUpdate:true]
    }

    def create() {
        respond new GwXml(params),[isSave:true,isUpdate:false]
    }

    @Transactional
    def save(GwXml gwXml) {
        if (gwXml == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwXml.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwXml.errors, view:'create'
            return
        }

        gwXml.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gwXml.label', default: 'GwXml'), gwXml.id])
                redirect gwXml
            }
            '*' { respond gwXml, [status: CREATED] }
        }
    }

    def edit(GwXml gwXml) {
        respond gwXml,[isSave:false,isUpdate:true]
    }

    @Transactional
    def update(GwXml gwXml) {
        if (gwXml == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwXml.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwXml.errors, view:'edit'
            return
        }

        gwXml.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'gwXml.label', default: 'GwXml'), gwXml.id])
                redirect gwXml
            }
            '*'{ respond gwXml, [status: OK] }
        }
    }

    @Transactional
    def delete(GwXml gwXml) {

        if (gwXml == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        try{
            gwXml.delete flush:true
        }catch (allError){
            flash.error =  "Impossível excluir. Verifique os relacionamentos."
            respond gwXml
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gwXml.label', default: 'GwXml'), gwXml.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gwXml.label', default: 'GwXml'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}