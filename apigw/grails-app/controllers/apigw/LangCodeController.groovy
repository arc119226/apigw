package apigw

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
@Secured(['ROLE_ADMIN','ROLE_DEV','ROLE_USER'])
@Transactional(readOnly = false)
class LangCodeController {

    def index(Integer max) {
        respond LangCode.list(params), model:[langCodeCount: LangCode.count()]
    }

    def show(LangCode langCode) {
        this.flash.message=flash.message
        this.flash.error=flash.error
        respond langCode
    }

    def create() {
        respond new LangCode(params)
    }

    @Transactional
    def save(LangCode langCode) {
        if (langCode == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (langCode.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond langCode.errors, view:'create'
            return
        }

        langCode.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'langCode.label', default: 'LangCode'), langCode.id])
                redirect langCode
            }
            '*' { respond langCode, [status: CREATED] }
        }
    }

    def edit(LangCode langCode) {
        respond langCode
    }

    @Transactional
    def update(LangCode langCode) {
        if (langCode == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (langCode.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond langCode.errors, view:'edit'
            return
        }

        langCode.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'langCode.label', default: 'LangCode'), langCode.id])
                redirect langCode
            }
            '*'{ respond langCode, [status: OK] }
        }
    }

    @Transactional
    def delete(LangCode langCode) {

        if (langCode == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        try{
            langCode.delete flush:true
        }catch (allError){
            flash.error =  "Impossível excluir. Verifique os relacionamentos."
            respond langCode
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'langCode.label', default: 'LangCode'), langCode.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'langCode.label', default: 'LangCode'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}