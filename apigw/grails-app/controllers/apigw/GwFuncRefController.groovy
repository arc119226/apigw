package apigw

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_ADMIN','ROLE_DEV','ROLE_USER'])
@Transactional(readOnly = false)
class GwFuncRefController {

    def index(Integer max) {
        respond GwFuncRef.list(params), model:[gwFuncRefCount: GwFuncRef.count()]
    }

    def show(GwFuncRef gwFuncRef) {
        this.flash.message=flash.message
        this.flash.error=flash.error
        respond gwFuncRef
    }

    def create() {
        respond new GwFuncRef(params)
    }

    @Transactional
    def save(GwFuncRef gwFuncRef) {
        if (gwFuncRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwFuncRef.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwFuncRef.errors, view:'create'
            return
        }

        gwFuncRef.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gwFuncRef.label', default: 'GwFuncRef'), gwFuncRef.id])
                redirect gwFuncRef
            }
            '*' { respond gwFuncRef, [status: CREATED] }
        }
    }

    def edit(GwFuncRef gwFuncRef) {
        respond gwFuncRef
    }

    @Transactional
    def update(GwFuncRef gwFuncRef) {
        if (gwFuncRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwFuncRef.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwFuncRef.errors, view:'edit'
            return
        }

        gwFuncRef.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'gwFuncRef.label', default: 'GwFuncRef'), gwFuncRef.id])
                redirect gwFuncRef
            }
            '*'{ respond gwFuncRef, [status: OK] }
        }
    }

    @Transactional
    def delete(GwFuncRef gwFuncRef) {

        if (gwFuncRef == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        try{
            gwFuncRef.delete flush:true
        }catch (allError){
            flash.error =  "Impossível excluir. Verifique os relacionamentos."
            respond gwFuncRef
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gwFuncRef.label', default: 'GwFuncRef'), gwFuncRef.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gwFuncRef.label', default: 'GwFuncRef'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}