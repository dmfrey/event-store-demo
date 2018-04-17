

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method POST()

        urlPath( value( consumer( regex( '/boards/11111111-90ab-cdef-1234-567890abcdef/stories' ) ) ) ) {

            queryParameters {

                parameter 'name' : anyNonEmptyString()

            }

        }

    }

    response {

        status 201

        headers {
            header([
                    Location: "http://localhost/boards/11111111-90ab-cdef-1234-567890abcdef/stories/10240df9-4a1e-4fa4-bbd1-0bb33d764603"
            ])
        }

    }

}