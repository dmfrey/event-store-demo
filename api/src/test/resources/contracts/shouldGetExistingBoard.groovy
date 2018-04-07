

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method GET()

        url '/boards/' + uuid().pattern()

    }

    response {

        status 200

        body( file("json/board.json" ) )

        headers {
            header( '''Content-Type''', '''application/json;charset=UTF-8''' )
        }

    }

}