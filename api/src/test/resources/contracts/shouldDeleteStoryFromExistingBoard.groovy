

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method DELETE()

        url '/boards/' + anyUuid().serverValue + "/stories/" + anyUuid().serverValue

    }

    response {

        status 202

    }

}