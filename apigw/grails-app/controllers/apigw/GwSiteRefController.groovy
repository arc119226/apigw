package apigw

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_ADMIN','ROLE_DEV','ROLE_USER'])
@Transactional(readOnly = false)
class GwSiteRefController {

    def index(Integer max) {
        respond GwSiteRef.list(params), model:[gwSiteRefCount: GwSiteRef.count()]
    }

    def show(GwSiteRef gwSiteRef) {
        this.flash.message=flash.message
        this.flash.error=flash.error
        respond gwSiteRef
    }

    def create() {
        respond new GwSiteRef(params)
    }

    @Transactional
    def save(GwSiteRef gwSiteRef) {
        if (gwSiteRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwSiteRef.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwSiteRef.errors, view:'create'
            return
        }

        gwSiteRef.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gwSiteRef.label', default: 'GwSiteRef'), gwSiteRef.id])
                redirect gwSiteRef
            }
            '*' { respond gwSiteRef, [status: CREATED] }
        }
    }

    def edit(GwSiteRef gwSiteRef) {
        respond gwSiteRef
    }

    @Transactional
    def update(GwSiteRef gwSiteRef) {
        if (gwSiteRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwSiteRef.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwSiteRef.errors, view:'edit'
            return
        }

        gwSiteRef.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'gwSiteRef.label', default: 'GwSiteRef'), gwSiteRef.id])
                redirect gwSiteRef
            }
            '*'{ respond gwSiteRef, [status: OK] }
        }
    }

    @Transactional
    def delete(GwSiteRef gwSiteRef) {

        if (gwSiteRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        try{
            gwSiteRef.delete flush:true
        }catch (allError){
            flash.error =  "Impossível excluir. Verifique os relacionamentos."
            respond gwSiteRef
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gwSiteRef.label', default: 'GwSiteRef'), gwSiteRef.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gwSiteRef.label', default: 'GwSiteRef'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}