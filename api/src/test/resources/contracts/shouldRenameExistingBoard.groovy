

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method PATCH()

        urlPath( value( consumer( regex( '/boards/' + uuid().pattern() ) ) ) ) {

            queryParameters {

                parameter 'name' : anyNonEmptyString()

            }

        }

    }

    response {

        status 202

    }

}