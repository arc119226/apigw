package apigw

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_ADMIN','ROLE_DEV','ROLE_USER'])
@Transactional(readOnly = false)
class GwDeployController {

    def index(Integer max) {
        respond GwDeploy.list(params), model:[gwDeployCount: GwDeploy.count()]
    }

    def show(GwDeploy gwDeploy) {
        this.flash.message=flash.message
        this.flash.error=flash.error
        respond gwDeploy
    }

    def create() {
        respond new GwDeploy(params)
    }

    @Transactional
    def save(GwDeploy gwDeploy) {
        if (gwDeploy == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwDeploy.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwDeploy.errors, view:'create'
            return
        }

        gwDeploy.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gwDeploy.label', default: 'GwDeploy'), gwDeploy.id])
                redirect gwDeploy
            }
            '*' { respond gwDeploy, [status: CREATED] }
        }
    }

    def edit(GwDeploy gwDeploy) {
        respond gwDeploy
    }

    @Transactional
    def update(GwDeploy gwDeploy) {
        if (gwDeploy == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (gwDeploy.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond gwDeploy.errors, view:'edit'
            return
        }

        gwDeploy.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'gwDeploy.label', default: 'GwDeploy'), gwDeploy.id])
                redirect gwDeploy
            }
            '*'{ respond gwDeploy, [status: OK] }
        }
    }

    @Transactional
    def delete(GwDeploy gwDeploy) {

        if (gwDeploy == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        try{
            gwDeploy.delete flush:true
        }catch (allError){
            flash.error =  "Impossível excluir. Verifique os relacionamentos."
            respond gwDeploy
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gwDeploy.label', default: 'GwDeploy'), gwDeploy.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gwDeploy.label', default: 'GwDeploy'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}