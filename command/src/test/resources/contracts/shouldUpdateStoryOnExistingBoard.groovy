

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method PUT()

        urlPath( value( consumer( regex( '/boards/' + uuid().pattern() + '/stories/' + uuid().pattern() ) ) ) ) {

            queryParameters {

                parameter 'name' : anyNonEmptyString()

            }

        }

    }

    response {

        status 202

    }

}