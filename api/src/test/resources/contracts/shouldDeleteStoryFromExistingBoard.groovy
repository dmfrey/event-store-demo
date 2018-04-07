

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method DELETE()

        urlPath( value( consumer( regex( '/boards/' + uuid().pattern() + "/stories/" + uuid().pattern() ) ) ) )

    }

    response {

        status 202

    }

}