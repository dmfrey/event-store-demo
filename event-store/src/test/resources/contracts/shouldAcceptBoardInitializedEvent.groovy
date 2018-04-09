

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description 'should accept board initialized event'

    request {

        method POST()

        url '/'

        headers {
            header( '''Content-Type''', applicationJson() )
        }

        body( file("json/board_initialized_event.json" ) )

    }

    response {

        status 202

    }

}