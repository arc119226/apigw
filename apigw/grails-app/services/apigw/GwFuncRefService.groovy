package apigw

import grails.gorm.services.Service

@Service(GwFuncRef)
interface GwFuncRefService {

    GwFuncRef get(Serializable id)

    List<GwFuncRef> list(Map args)

    Long count()

    void delete(Serializable id)

    GwFuncRef save(GwFuncRef gwFuncRef)

}