

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method POST()

        urlPath( value( consumer( regex( '/boards/' + uuid().pattern() + '/stories' ) ) ) ) {

            queryParameters {

                parameter 'name' : anyNonEmptyString()

            }

        }

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