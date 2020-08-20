package apigw

import grails.gorm.services.Service

@Service(GwPrjRef)
interface GwPrjRefService {

    GwPrjRef get(Serializable id)

    List<GwPrjRef> list(Map args)

    Long count()

    void delete(Serializable id)

    GwPrjRef save(GwPrjRef gwPrjRef)

}