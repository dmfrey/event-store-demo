

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method POST()

        url '/boards'

    }

    response {

        status 201

        headers {
            header([
                    Location: "${fromRequest().path().serverValue}/" + anyUuid().clientValue
            ])
        }

    }

}