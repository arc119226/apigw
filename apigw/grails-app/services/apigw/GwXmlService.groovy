package apigw

import grails.gorm.services.Service

@Service(GwXml)
interface GwXmlService {

    GwXml get(Serializable id)

    List<GwXml> list(Map args)

    Long count()

    void delete(Serializable id)

    GwXml save(GwXml gwXml)

}