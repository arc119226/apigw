package apigw

import grails.gorm.services.Service

@Service(GwSiteRef)
interface GwSiteRefService {

    GwSiteRef get(Serializable id)

    List<GwSiteRef> list(Map args)

    Long count()

    void delete(Serializable id)

    GwSiteRef save(GwSiteRef gwSiteRef)

}