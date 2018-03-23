

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method PUT()

        urlPath( '/boards/' + anyUuid().serverValue + '/stories/' + anyUuid().serverValue ) {

            queryParameters {

                parameter 'name' : value( consumer( matching( anyNonEmptyString() ) ), producer( "Story Number 1" ) )

            }

        }

    }

    response {

        status 202

    }

}