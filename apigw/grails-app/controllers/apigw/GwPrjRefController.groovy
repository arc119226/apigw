package apigw

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_ADMIN','ROLE_DEV','ROLE_USER'])
@Transactional(readOnly = false)
class GwPrjRefController {

    def index(Integer max) {
        respond GwPrjRef.list(params), model:[gwPrjRefCount: GwPrjRef.count()]
    }

    def show(GwPrjRef gwPrjRef) {
        this.flash.message=flash.message
        this.flash.error=flash.error
        respond gwPrjRef
    }

    def create() {
        respond new GwPrjRef(params)
    }

    @Transactional
    def save(GwPrjRef gwPrjRef) {
        if (gwPrjRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwPrjRef.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwPrjRef.errors, view:'create'
            return
        }

        gwPrjRef.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gwPrjRef.label', default: 'GwPrjRef'), gwPrjRef.id])
                redirect gwPrjRef
            }
            '*' { respond gwPrjRef, [status: CREATED] }
        }
    }

    def edit(GwPrjRef gwPrjRef) {
        respond gwPrjRef
    }

    @Transactional
    def update(GwPrjRef gwPrjRef) {
        if (gwPrjRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwPrjRef.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwPrjRef.errors, view:'edit'
            return
        }

        gwPrjRef.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'gwPrjRef.label', default: 'GwPrjRef'), gwPrjRef.id])
                redirect gwPrjRef
            }
            '*'{ respond gwPrjRef, [status: OK] }
        }
    }

    @Transactional
    def delete(GwPrjRef gwPrjRef) {

        if (gwPrjRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        try{
            gwPrjRef.delete flush:true
        }catch (allError){
            flash.error =  "Impossível excluir. Verifique os relacionamentos."
            respond gwPrjRef
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gwPrjRef.label', default: 'GwPrjRef'), gwPrjRef.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gwPrjRef.label', default: 'GwPrjRef'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}