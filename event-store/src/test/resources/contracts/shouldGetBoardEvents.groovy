

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method GET()

        urlPath( value( consumer( regex( '/' + uuid().pattern() ) ) ) )

    }

    response {

        status 200

        body( file("json/board_events.json" ) )

        headers {
            header( '''Content-Type''', '''application/json;charset=UTF-8''' )
        }

    }

}