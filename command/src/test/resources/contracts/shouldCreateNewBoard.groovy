package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {

        method POST()

        url '/boards'

//        headers {
//            header( contentType(), applicationJson() )
//        }

    }

    response {

        String requestPath = request.url.serverValue.toString()
        String hostnamePattern = '((http[s]?|ftp):/)/?([^:/]+)(:[0-9]{1,5})?'
        // above pattern is identical to hostname() with the following change:
        // removed the negated s from third match group; this was preventing hostnames with an s from matching
        // intention was to negate \s to prevent spaces from matching, but I can't get the \ to properly escape

        status 201

        headers {
            header([
                    Location: $(
                            stub('http://localhost' + requestPath + '/' + UUID.randomUUID()),
                            test(regex(hostnamePattern + requestPath + '/' + uuid()))
                    )
            ])
        }

    }

}