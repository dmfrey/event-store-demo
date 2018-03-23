

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method PATCH()

        urlPath( '/boards/' + anyUuid().serverValue ) {

            queryParameters {

                parameter 'name' : value( consumer( matching( anyNonEmptyString() ) ), producer( "My Board" ) )

            }

        }

    }

    response {

        status 202

    }

}