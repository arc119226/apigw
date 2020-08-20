package com.arcare.security

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
@Secured(['ROLE_ADMIN'])
@Transactional(readOnly = false)
class RoleController {

    def index(Integer max) {
        respond Role.list(params), model:[roleCount: Role.count()]
    }

    def show(Role role) {
        this.flash.message=flash.message
        this.flash.error=flash.error
        respond role
    }

    def create() {
        respond new Role(params)
    }

    @Transactional
    def save(Role role) {
        if (role == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (role.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond role.errors, view:'create'
            return
        }

        role.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'role.label', default: 'Role'), role.id])
                redirect role
            }
            '*' { respond role, [status: CREATED] }
        }
    }

    def edit(Role role) {
        respond role
    }

    @Transactional
    def update(Role role) {
        if (role == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (role.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond role.errors, view:'edit'
            return
        }

        role.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'role.label', default: 'Role'), role.id])
                redirect role
            }
            '*'{ respond role, [status: OK] }
        }
    }

    @Transactional
    def delete(Role role) {

        if (role == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        try{
            role.delete flush:true
        }catch (allError){
            flash.error =  "Impossível excluir. Verifique os relacionamentos."
            respond role
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'role.label', default: 'Role'), role.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.label', default: 'Role'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}