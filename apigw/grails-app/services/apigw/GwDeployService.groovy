package apigw

import grails.gorm.services.Service

@Service(GwDeploy)
interface GwDeployService {

    GwDeploy get(Serializable id)

    List<GwDeploy> list(Map args)

    Long count()

    void delete(Serializable id)

    GwDeploy save(GwDeploy gwDeploy)

}